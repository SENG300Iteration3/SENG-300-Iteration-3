package com.diy.software.app;

import java.util.ArrayList;

import com.diy.hardware.BarcodedProduct;
import com.diy.hardware.DoItYourselfStation;
import com.diy.hardware.PLUCodedProduct;
import com.diy.hardware.PriceLookUpCode;
import com.diy.hardware.external.ProductDatabases;
import com.diy.software.controllers.PaneControl;
import com.diy.software.controllers.StationControl;
import com.diy.software.fakedata.FakeDataInitializer;
import com.jimmyselectronics.necchi.Barcode;
import com.jimmyselectronics.necchi.Numeral;

import swing.frames.AttendantStationGUI;
import swing.frames.CustomerActionsGUI;
import swing.frames.CustomerStationGUI;

public class SENG300DoItYourselfStation {
	public static void main(String[] args) {
		int totalNumberOfStations;
		try {
			totalNumberOfStations = Integer.parseInt(args[0]);
		} catch (Exception e) {
			totalNumberOfStations = 3;
		}

		configureDoItYourselfStation();
		initializeInventory();
		ArrayList<StationControl> stationControls = new ArrayList<>();
		for (int i = 0; i < totalNumberOfStations; i++) {
			stationControls.add(new StationControl(new FakeDataInitializer()));
		}
		PaneControl pc = new PaneControl(stationControls);
		new AttendantStationGUI(pc);
		new CustomerStationGUI(pc);
		new CustomerActionsGUI(pc);
	}

	public static void configureDoItYourselfStation() {
		DoItYourselfStation.configureBanknoteDenominations(new int[] { 100, 50, 20, 10, 5, 1 });
		DoItYourselfStation.configureCoinDenominations(new long[] { 200, 100, 25, 10, 5, 1 });
	}

	public static void initializeInventory() {
		Barcode barcode1 = new Barcode(new Numeral[] { Numeral.one, Numeral.two, Numeral.three, Numeral.four });
		Barcode barcode2 = new Barcode(new Numeral[] { Numeral.zero, Numeral.four, Numeral.two, Numeral.zero });
		PriceLookUpCode plu1 = new PriceLookUpCode("2718");
		PriceLookUpCode plu2 = new PriceLookUpCode("31415");

		BarcodedProduct bp1 = new BarcodedProduct(barcode1, "Can of Beans", 2, 450);
		ProductDatabases.INVENTORY.put(bp1, 10);
		BarcodedProduct bp2 = new BarcodedProduct(barcode2, "Bag of Doritos", 5, 420);
		ProductDatabases.INVENTORY.put(bp2, 10);
		PLUCodedProduct pcp1 = new PLUCodedProduct(plu1, "Rib Eye Steak", 350);
		ProductDatabases.INVENTORY.put(pcp1, 10);
		PLUCodedProduct pcp2 = new PLUCodedProduct(plu2, "Cauliflower", 550);
		ProductDatabases.INVENTORY.put(pcp2, 10);
	}
}
