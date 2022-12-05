package com.diy.software.fakedata;



import java.util.Calendar;
import java.util.HashMap;

import javax.swing.text.PlainDocument;

import com.diy.hardware.BarcodedProduct;
import com.diy.hardware.PLUCodedItem;
import com.diy.hardware.PLUCodedProduct;
import com.diy.hardware.PriceLookUpCode;
import com.diy.hardware.external.CardIssuer;
import com.diy.hardware.external.ProductDatabases;
import com.jimmyselectronics.Item;
import com.diy.software.controllers.AttendantControl;
import com.jimmyselectronics.necchi.Barcode;
import com.jimmyselectronics.necchi.BarcodedItem;
import com.jimmyselectronics.necchi.Numeral;
import com.jimmyselectronics.opeechee.Card;
import com.jimmyselectronics.svenden.ReusableBag;
import com.unitedbankingservices.coin.CoinStorageUnit;

public class FakeDataInitializer {
	
	private Barcode barcode1, barcode2, barcode3, barcode4;
	private BarcodedItem item1, item2, item3, item4;
	private BarcodedProduct bp1, bp2, bp3, bp4;
	
	private PriceLookUpCode code1, code2, code3, code4;
	private PLUCodedProduct pp1, pp2, pp3, pp4, reusableBagProduct;
	private PLUCodedItem pitem1, pitem2, pitem3, pitem4, reusableBagItem;
	private static PriceLookUpCode reusableBagCode = null;
	
	private Card card1, card2, card3, card4, card5;
	private CardIssuer fakebank;
	private CoinStorageUnit fakeCoinStorageUnit;
	private final Double AMOUNT_AVAILABLE = 1000.0;
	private final int COIN_STORAGE_CAPACITY = 50;
	Calendar expire_date = Calendar.getInstance();
	
	public void addProductAndBarcodeData () {
		barcode1 = new Barcode(new Numeral[] { Numeral.one, Numeral.two, Numeral.three, Numeral.four }); 
		item1 = new BarcodedItem(barcode1, 450); 
		barcode2 = new Barcode(new Numeral[] { Numeral.zero, Numeral.four, Numeral.two, Numeral.zero }); 
		item2 = new BarcodedItem(barcode2, 420); 
		barcode3 = new Barcode(new Numeral[] { Numeral.four, Numeral.three, Numeral.two, Numeral.one }); 
		item3 = new BarcodedItem(barcode3, 350); 
		barcode4 = new Barcode(new Numeral[] { Numeral.one, Numeral.two, Numeral.one, Numeral.two }); 
		item4 = new BarcodedItem(barcode4, 550); 	
		
		bp1 = new BarcodedProduct(barcode1, "Can of Beans", 2, 450);
		ProductDatabases.BARCODED_PRODUCT_DATABASE.put(barcode1, bp1);
		bp2 = new BarcodedProduct(barcode2, "Bag of Doritos", 5, 420);
		ProductDatabases.BARCODED_PRODUCT_DATABASE.put(barcode2, bp2);
		bp3 = new BarcodedProduct(barcode3, "Rib Eye Steak", 17, 350);
		ProductDatabases.BARCODED_PRODUCT_DATABASE.put(barcode3, bp3);
		bp4 = new BarcodedProduct(barcode4, "Cauliflower", 6, 550);
		ProductDatabases.BARCODED_PRODUCT_DATABASE.put(barcode4, bp4);

	}
	
	public void addPLUCodedProduct() {
		code1 = new PriceLookUpCode("1234");
		pitem1 = new PLUCodedItem(code1, 200);
		pp1 = new PLUCodedProduct(code1, "Green Apples", 8);
		
		code2 = new PriceLookUpCode("9876");
		pitem2 = new PLUCodedItem(code2, 300);
		pp2 = new PLUCodedProduct(code2, "Broccoli", 5);

		code3 = new PriceLookUpCode("11111");
		pitem3 = new PLUCodedItem(code3, 300);
		pp3 = new PLUCodedProduct(code3, "Tomatoes", 4);

		code4 = new PriceLookUpCode("23456");
		pitem4 = new PLUCodedItem(code4, 200);
		pp4 = new PLUCodedProduct(code4, "Oranges", 7);

		reusableBagCode = new PriceLookUpCode("0000");
		reusableBagItem = new PLUCodedItem(reusableBagCode, (new ReusableBag()).getWeight());  // );
		reusableBagProduct = new PLUCodedProduct(reusableBagCode, "Reusable bag", 2);

		ProductDatabases.PLU_PRODUCT_DATABASE.put(code1, pp1);
		ProductDatabases.PLU_PRODUCT_DATABASE.put(code2, pp2);
		ProductDatabases.PLU_PRODUCT_DATABASE.put(code3, pp3);
		ProductDatabases.PLU_PRODUCT_DATABASE.put(code4, pp4);
		ProductDatabases.PLU_PRODUCT_DATABASE.put(reusableBagCode, reusableBagProduct);
		
		ProductDatabases.INVENTORY.put(pp1, 10);
		ProductDatabases.INVENTORY.put(pp2, 10);
		ProductDatabases.INVENTORY.put(pp3, 10);
		ProductDatabases.INVENTORY.put(pp4, 10);
		ProductDatabases.INVENTORY.put(reusableBagProduct, 10);
		

	}

	public static PriceLookUpCode getReusableBagCode() {
		return reusableBagCode;
	}
	
	/**
	 * add fake membership data
	 * @return void
	 */
	public void addFakeMembers() {
		MembershipDatabase.membershipMap.put(1234, "Itadori");
		MembershipDatabase.membershipMap.put(1235, "Tanjiro");
		MembershipDatabase.membershipMap.put(1236, "Nezuko");
		MembershipDatabase.membershipMap.put(1237, "Zenitsu");
	}
	
	public void addFakeAttendantLogin() {
		AttendantControl.logins.add("password");
		AttendantControl.logins.add("wordpass");
		AttendantControl.logins.add("Password1");
		AttendantControl.logins.add("12345");
	}
	
	public void addCardData() {
		
		fakebank = new CardIssuer("RBC", 14);
		expire_date.add(Calendar.YEAR, 5);
		card1 = new Card("AMEX", "0000000000001234", "Stephen Strange", "000", "1234", false, true);
		card2 = new Card("VISA", "0000000000004321", "Tony Stark", "111", "0987", true, true);
		card3 = new Card("MAST", "0000000000009999", "Natasha Romanoff", "222", "1111", true, false);
		card4 = new Card("MEMBERSHIP", "1234", "Itadori", "000", "0000", false, true);
		
		fakebank.addCardData("0000000000001234", "Stephen Strange", expire_date, "000", AMOUNT_AVAILABLE);
		fakebank.addCardData("0000000000004321", "Tony Stark", expire_date, "111", AMOUNT_AVAILABLE);
		fakebank.addCardData("0000000000009999", "Natasha Romanoff", expire_date, "222", AMOUNT_AVAILABLE);
		fakebank.addCardData("1234", "Itadori", expire_date, "000", AMOUNT_AVAILABLE);
		
		card5 = new Card(GiftcardDatabase.CompanyGiftCard, "00001234", "Jimmy James", null, null, false, false);
		GiftcardDatabase.giftcardMap.put("00001234", 50.00);
	}
	
	public void setCoinStorageCapacity() {
		fakeCoinStorageUnit = new CoinStorageUnit(COIN_STORAGE_CAPACITY);
	}
	
	public Barcode[] getBarcodes() {
		return new Barcode[] {barcode1, barcode2, barcode3, barcode4};
	}

	public Item[] getItems() {
		return new Item[] {item1, item2, pitem3, pitem4};		//smaller list for testing
//		return new Item[] {item1, item2, item3, item4, pitem1, pitem2, pitem3, pitem4};
	}
	
	public PriceLookUpCode[] getPLUCode() {
		return new PriceLookUpCode[] {code1, code2, code3, code4, reusableBagCode};
	}

	public PLUCodedItem[] getPLUItem() {
		return new PLUCodedItem[] {pitem1, pitem2, pitem3, pitem4};
	}
	
	public double getReusableBagPrice() {
		return ProductDatabases.PLU_PRODUCT_DATABASE.get(reusableBagCode).getPrice();
	}
	
	public Card[] getCards() {
		return new Card[] {card1, card2, card3, card4, card5};
	}
	
	public CardIssuer getCardIssuer() {
		return fakebank;
	}

	public HashMap<Integer, String> getMembershipMap() {
		return MembershipDatabase.membershipMap;
	}
	
	public CoinStorageUnit getFakeUnit() {
		return fakeCoinStorageUnit;
	}
}
