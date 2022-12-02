package com.diy.software.controllers;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.concurrent.ThreadLocalRandom;

import com.diy.software.util.Tuple;
import com.diy.hardware.BarcodedProduct;
import com.diy.hardware.PLUCodedProduct;
import com.diy.hardware.PriceLookUpCode;
import com.diy.hardware.external.ProductDatabases;
import com.diy.software.listeners.ItemsControlListener;
import com.jimmyselectronics.AbstractDevice;
import com.jimmyselectronics.AbstractDeviceListener;
import com.jimmyselectronics.Item;
import com.jimmyselectronics.necchi.Barcode;
import com.jimmyselectronics.necchi.BarcodeScanner;
import com.jimmyselectronics.necchi.BarcodeScannerListener;
import com.jimmyselectronics.virgilio.ElectronicScale;
import com.jimmyselectronics.virgilio.ElectronicScaleListener;

public class ItemsControl implements ActionListener, BarcodeScannerListener, ElectronicScaleListener {
	private StationControl sc;
	private ArrayList<ItemsControlListener> listeners;
	public ArrayList<Tuple<BarcodedProduct, Integer>> tempList = new ArrayList<>();
	private ArrayList<Tuple<String, Double>> checkoutList = new ArrayList<>();
	private double checkoutListTotal = 0.0;

	private boolean scanSuccess = true, weighSuccess = true, inCatalog = false;

	public String userMessage = "";
	private long baggingAreaTimerStart;
	private long baggingAreaTimerEnd;
	private final static double PROBABILITY_OF_BAGGING_WRONG_ITEM = 0.20;
	private final static ThreadLocalRandom random = ThreadLocalRandom.current();
	private Item wrongBaggedItem = new Item(235) {
	};

	private boolean removedWrongBaggedItem;
	private double scaleExpectedWeight;
	private double scaleReceivedWeight;

	public ItemsControl(StationControl sc) {
		this.sc = sc;
		sc.station.handheldScanner.register(this);
		sc.station.mainScanner.register(this);
		sc.station.baggingArea.register(this);
		this.listeners = new ArrayList<>();
	}

	public Item getWrongBaggedItem() {
		return wrongBaggedItem;
	}

	public void addListener(ItemsControlListener l) {
		listeners.add(l);
	}

	public void removeListener(ItemsControlListener l) {
		listeners.remove(l);
	}

	public void addItemToCheckoutList(Tuple<String, Double> item) {
		checkoutList.add(item);
		refreshGui();
	}

	public void addScannedItemToCheckoutList(Barcode barcode) {
		BarcodedProduct barcodedProduct = ProductDatabases.BARCODED_PRODUCT_DATABASE.get(barcode);
		double price;
		if (barcodedProduct != null) {
			price = (double) barcodedProduct.getPrice();
			this.addItemToCheckoutList(new Tuple<String, Double>(barcodedProduct.getDescription(), price));
			this.updateCheckoutTotal(price);
		} else {
			System.err.println("Scanned item is not in product database!");
		}
	}

	public void updateCheckoutTotal(double amount) {
		if (checkoutListTotal + amount >= 0)
			checkoutListTotal += amount;
		refreshGui();
	}

	public double getCheckoutTotal() {
		return checkoutListTotal;
	}

	public ArrayList<Tuple<String, Double>> getCheckoutList() {
		return checkoutList;
	}

	private void refreshGui() {
		for (ItemsControlListener l : listeners) {
			l.itemsHaveBeenUpdated(this);
			l.productSubtotalUpdated(this);
		}
	}

	/**
	 * Picks up next item and if shopping cart is empty after, notifies
	 * noMoreItemsAvail
	 * 
	 * If the shoppingCart is empty at the start, ignores selecting next item and
	 * instead notified noMoreItemsAvail
	 */
	public void pickupNextItem() {
		try {
			sc.customer.selectNextItem();
			for (ItemsControlListener l : listeners)
				l.itemWasSelected(this);
			if (sc.customer.shoppingCart.size() == 0) {
				for (ItemsControlListener l : listeners)
					l.noMoreItemsAvailableInCart(this);
			}
		} catch (NoSuchElementException e) {
			// next item does not exist
			for (ItemsControlListener l : listeners)
				l.noMoreItemsAvailableInCart(this);
		}
	}

	public void putUnscannedItemBack() {
		try {
			sc.customer.deselectCurrentItem();
			for (ItemsControlListener l : listeners) {
				l.itemsAreAvailableInCart(this);
				l.awaitingItemToBeSelected(this);
			}
		} catch (Exception e) {
			// exception should never occur since this code path is only ever called when
			// currentItem is not null
		}
	}

	// TODO: scanItem now differtiates between using handheldScanner and mainScanner
	// ALSO: note that a new weight area called scanningArea exists now to grab
	// weight of items during general scanning phase
	public void scanCurrentItem(boolean useHandheld) {
		baggingAreaTimerStart = System.currentTimeMillis();
		scanSuccess = false;
		sc.customer.scanItem(useHandheld);
		if (!scanSuccess) {
			// if scanSuccess is still false after listeners have been called, we can show
			// an alert showing a failed scan if time permits.
		}
	}

	public void placeItemOnScale() {
		scaleExpectedWeight = sc.weightOfItemScanned;
		weighSuccess = false;
		baggingAreaTimerEnd = System.currentTimeMillis();
		// placing an item could potentially fail so allow for retries
		// simulating a 40% chance of putting wrong item on the scale
		if (random.nextDouble(0.0, 1.0) > PROBABILITY_OF_BAGGING_WRONG_ITEM) {
			weighSuccess = true;
			sc.customer.placeItemInBaggingArea();

		} else {
			// simulation weight discrepancy
			scaleReceivedWeight = wrongBaggedItem.getWeight();
			removedWrongBaggedItem = false;
			sc.customer.placeItemInBaggingArea();
			sc.station.baggingArea.add(wrongBaggedItem);

		}

		if (baggingAreaTimerEnd - baggingAreaTimerStart > 10000) {
			userMessage = "Please place item on scale!";
			// not blocking station for now
			// sc.blockStation();
		}

		if (!weighSuccess) {
			// if weighSuccess is still false after listeners have been called, we can show
			// and alert showing a failed weigh-in if time permits.
		}
	}

	/**
	 * removes the last wrongly added item from the scale
	 */
	public void removeLastBaggedItem() {
		sc.unblockStation();
		removedWrongBaggedItem = true;
		sc.station.baggingArea.remove(wrongBaggedItem);
		for (ItemsControlListener l : listeners)
			l.awaitingItemToBeSelected(this);
	}

	/**
	 * After the attendant approved no bag request, customer leave the item in cart
	 */
	public void placeBulkyItemInCart() {
		try {
			// Customer leaves the current item in the cart.
			sc.customer.leaveBulkyItemInCart();
			for (ItemsControlListener l : listeners)
				l.awaitingItemToBeSelected(this);
		} catch (Exception e) {
			// do nothing if failure
		}
	}

	private Barcode searchBarcodedProductDatabase(String strProductName) {
		Barcode result = null;
		for (Entry<Barcode, BarcodedProduct> entry : ProductDatabases.BARCODED_PRODUCT_DATABASE.entrySet()) {
			if (entry.getValue().getDescription().compareTo(strProductName) == 0) {
				result = entry.getKey();
				break;
			}
		}
		return result;
	}

	private PriceLookUpCode searchPLUCodedProductDatabase(String strProductName) {
		PriceLookUpCode result = null;
		for (Entry<PriceLookUpCode, PLUCodedProduct> entry : ProductDatabases.PLU_PRODUCT_DATABASE.entrySet()) {
			if (entry.getValue().getDescription().compareTo(strProductName) == 0) {
				result = entry.getKey();
			}
		}
		return result;
	}

	// Add Item by Browsing UC
//		1. Customer I/O: Displays the visual catalogue, allowing the customer to browse through it.
//		2. Customer I/O: The customer selects the product of interest.
//		3. Customer I/O: Signals to the customer to place the item in the Bagging Area.
//		4. Customer I/O: Signals to the System that an item is to be added, indicating the information about 
//		the item.
//		5. System: Blocks the self-checkout system from further customer interaction.
//		6. Bagging Area: Signals to the System that the weight has changed.
//		7. System: Unblocks the self-checkout system.

	// Approach: Catalog will be buttons will product description on it, clicking a
	// button will return string description which will then be used to search the
	// product database. Then the
//		appropriate action will performed if it is produce vs barcode product. The the dollar value 
//		and weight will be added
	private void addItemByBrowsing(String strProductName) {
		Barcode barcodeIdentifier = null;
		PriceLookUpCode PLUCodeIdentifier = null;

		boolean isItemSelected = false;

		barcodeIdentifier = searchBarcodedProductDatabase(strProductName);
		if (barcodeIdentifier != null) {
			addScannedItemToCheckoutList(barcodeIdentifier);
			isItemSelected = true;

		}

		PLUCodeIdentifier = searchPLUCodedProductDatabase(strProductName);
		if (PLUCodeIdentifier != null) {
			// FIXME: Call add item by PLU code method
			isItemSelected = true;
		}
		if (isItemSelected) {
			sc.goBackOnUI();

			inCatalog = false;
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String c = e.getActionCommand();

		System.out.println(c);

		if (inCatalog) {
			addItemByBrowsing(c);
		} else {
			try {
				switch (c) {
				case "pick up":
					System.out.println("Customer picks up next item");
					pickupNextItem();
					break;
				case "main scan":
					System.out.println("Customer uses main scanner to scan next item");
					scanCurrentItem(false);
					break;
				case "handheld scan":
					System.out.println("Customer uses handheld scanner to scan next item");
					scanCurrentItem(true);
					break;
				case "put back":
					System.out.println("Customer put back current item");
					putUnscannedItemBack();
					break;
				case "bag":
					System.out.println("Customer put item in bagging area");
					placeItemOnScale();
					break;
				case "pay":
					System.out.println("Starting payment workflow");
					sc.startPaymentWorkflow();
					break;
				case "member":
					sc.startMembershipWorkflow();
					break;
				case "catalog":
					inCatalog = true;
					sc.startCatalogWorkflow();
					break;
				default:
					break;
				}
			} catch (Exception ex) {

			}
		}
	}

	@Override
	public void enabled(AbstractDevice<? extends AbstractDeviceListener> device) {
	}

	@Override
	public void disabled(AbstractDevice<? extends AbstractDeviceListener> device) {
	}

	@Override
	public void turnedOn(AbstractDevice<? extends AbstractDeviceListener> device) {
	}

	@Override
	public void turnedOff(AbstractDevice<? extends AbstractDeviceListener> device) {
	}

	@Override
	public void barcodeScanned(BarcodeScanner barcodeScanner, Barcode barcode) {
		if (!sc.isMembershipInput()) {
			scanSuccess = true;
			for (ItemsControlListener l : listeners)
				l.awaitingItemToBePlacedInBaggingArea(this);
		}
	}

	/**
	 * sets user message to announce weight on the indicated scale has changed
	 * 
	 * @param scale         The scale where the event occurred.
	 * @param weightInGrams The new weight.
	 */
	@Override
	public void weightChanged(ElectronicScale scale, double weightInGrams) {
		weighSuccess = true;
		for (ItemsControlListener l : listeners)
			l.awaitingItemToBeSelected(this);
		if (sc.expectedWeightMatchesActualWeight(weightInGrams)) {
			sc.unblockStation();
			userMessage = "Weight of scale has changed to: " + weightInGrams;
		} else {
			// System.out.println("Expected: " + sc.getExpectedWeight() + "Added: " +
			// weightInGrams);
			String weightDescrepancyMessage = "Expected item weight of: " + scaleExpectedWeight + ", "
					+ "Weight bagged: " + scaleReceivedWeight
					+ ". Weight Descrepancy detected please bag the right item";
			String weightDescrepancyMessageAttendant = "Expected item weight of: " + scaleExpectedWeight + ", "
					+ "Weight bagged: " + scaleReceivedWeight + ". Customer bagged the wrong item";
			for (ItemsControlListener l : listeners)
				l.awaitingItemToBeRemoved(this, weightDescrepancyMessage);

			sc.getAttendantControl().updateWeightDescrepancyMessage(weightDescrepancyMessageAttendant);
			;

		}
		if (removedWrongBaggedItem) {
			sc.unblockStation();
			for (ItemsControlListener l : listeners)
				l.awaitingItemToBeSelected(this);
		}
	}

	@Override
	public void overload(ElectronicScale scale) {
		userMessage = "Weight on scale has been overloaded, weight limit is: "
				+ sc.station.baggingArea.getWeightLimit();
	}

	@Override
	public void outOfOverload(ElectronicScale scale) {
		userMessage = "Excessive weight removed, continue scanning";
		sc.unblockStation();
	}
}
