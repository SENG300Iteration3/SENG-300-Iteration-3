package swing.screens;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;

import com.diy.software.util.Tuple;

import swing.styling.GUI_Color_Palette;
import swing.styling.GUI_Fonts;
import swing.styling.GUI_JButton;
import swing.styling.GUI_JLabel;
import swing.styling.GUI_JPanel;
import swing.styling.Screen;

import com.diy.software.controllers.ItemsControl;
import com.diy.software.controllers.StationControl;
import com.diy.software.listeners.ItemsControlListener;

public class AddItemsScreen extends Screen implements ItemsControlListener {
	private ItemsControl itemsControl;

	protected GUI_JLabel subtotalLabel;
	protected GUI_JPanel scannedPanel;
	protected GUI_JButton payBtn;
	protected GUI_JButton memberBtn;

	public AddItemsScreen(StationControl systemControl) {
		super(systemControl, "Self Checkout");
		this.itemsControl = systemControl.getItemsControl();
		this.itemsControl.addListener(this);

		GUI_JLabel itemCheckoutHeader = new GUI_JLabel("ITEM CHECKOUT");
		itemCheckoutHeader.setOpaque(true);
		itemCheckoutHeader.setBackground(GUI_Color_Palette.DARK_BROWN);
		itemCheckoutHeader.setFont(GUI_Fonts.TITLE);
		itemCheckoutHeader.setHorizontalAlignment(JLabel.CENTER);
		itemCheckoutHeader.setPreferredSize(new Dimension(this.width - 200, 100));
		itemCheckoutHeader.setBorder(BorderFactory.createMatteBorder(20, 20, 20, 20, GUI_Color_Palette.DARK_BLUE));
		this.addLayer(itemCheckoutHeader, 0);

		JScrollPane itemScrollPane = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		itemScrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(24, 0));
		itemScrollPane.setBackground(GUI_Color_Palette.DARK_BROWN);
		itemScrollPane.setPreferredSize(new Dimension(this.width - 200, 240));
		itemScrollPane.setBorder(BorderFactory.createMatteBorder(0, 20, 20, 20, GUI_Color_Palette.DARK_BLUE));
		this.addLayer(itemScrollPane, 0);

		this.scannedPanel = new GUI_JPanel();
		scannedPanel.setLayout(new GridLayout(20, 1));
		itemScrollPane.getViewport().add(scannedPanel);

		GUI_JPanel totalPanelBg = makeItemLabel("subtotal", 0);
		((GUI_JLabel) totalPanelBg.getComponent(0)).setFont(GUI_Fonts.TITLE);
		this.subtotalLabel = (GUI_JLabel) totalPanelBg.getComponent(1);
		subtotalLabel.setBorder(new EmptyBorder(0, 0, 0, 34)); // adjust position of text
		subtotalLabel.setFont(GUI_Fonts.TITLE);
		totalPanelBg.setPreferredSize(new Dimension(this.width - 200, 80));
		totalPanelBg.setBorder(BorderFactory.createMatteBorder(0, 20, 20, 20, GUI_Color_Palette.DARK_BLUE));
		this.addLayer(totalPanelBg, 0);

		JPanel buttonPanel = new JPanel(new GridLayout(1, 3));
		buttonPanel.setPreferredSize(new Dimension(this.width - 200, 80));
		this.addLayer(buttonPanel, 30);

		this.payBtn = makeButton("pay", buttonPanel);
		this.payBtn.setActionCommand("pay");
		this.payBtn.addActionListener(itemsControl);

		this.memberBtn = makeButton("enter member id", buttonPanel);
		this.memberBtn.setActionCommand("member");
		this.memberBtn.addActionListener(itemsControl);
	}

	public void invalidateAllScannedItems() {
		this.scannedPanel.removeAll();
	}

	public void addScannedItem(String itemName, double cost) {
		this.scannedPanel.add(makeItemLabel(itemName, cost));
		this.scannedPanel.repaint();
		this.scannedPanel.revalidate();
	}

	public void updateSubtotal(double subtotal) {
		this.subtotalLabel.setText(formatDollars(subtotal));
	}

	private GUI_JPanel makeItemLabel(String itemName, double cost) {
		GUI_JPanel itemPanel = new GUI_JPanel();
		itemPanel.setPreferredSize(new Dimension(this.width - 200, 50));
		itemPanel.setLayout(new BorderLayout());

		GUI_JLabel totalLabel = new GUI_JLabel(itemName.toUpperCase());
		totalLabel.setFont(GUI_Fonts.SUB_HEADER);
		totalLabel.setBorder(new EmptyBorder(0, 30, 0, 0));
		itemPanel.add(totalLabel, BorderLayout.WEST);

		GUI_JLabel costLabel = new GUI_JLabel(formatDollars(cost));
		costLabel.setFont(GUI_Fonts.SUB_HEADER);
		costLabel.setBorder(new EmptyBorder(0, 0, 0, 100));
		itemPanel.add(costLabel, BorderLayout.EAST);

		return itemPanel;
	}

	private String formatDollars(double dollarAmount) {
		return "$" + String.format("%.2f", dollarAmount);
	}

	private GUI_JButton makeButton(String text, JPanel parent) {
		final int left_padding = parent.getComponentCount() == 0 ? 20 : 0;

		GUI_JButton btn = new GUI_JButton(text.toUpperCase());
		btn.setFont(GUI_Fonts.TITLE);
		btn.setBorder(BorderFactory.createMatteBorder(10, left_padding, 10, 20, GUI_Color_Palette.DARK_BLUE));
		btn.setBackground(GUI_Color_Palette.DARK_BROWN);
		btn.setOpaque(true);
		parent.add(btn);

		return btn;
	}

	@Override
	public void awaitingItemToBeSelected(ItemsControl ic) {
		// TODO Auto-generated method stub

	}

	@Override
	public void itemWasSelected(ItemsControl ic) {
		// TODO Auto-generated method stub

	}

	@Override
	public void awaitingItemToBePlacedInBaggingArea(ItemsControl ic) {
		// TODO Auto-generated method stub

	}

	@Override
	public void noMoreItemsAvailableInCart(ItemsControl ic) {
		// TODO Auto-generated method stub

	}

	@Override
	public void itemsAreAvailableInCart(ItemsControl ic) {
		// TODO Auto-generated method stub

	}

	@Override
	public void itemsHaveBeenUpdated(ItemsControl itemsControl) {
		ArrayList<Tuple<String, Double>> checkoutList = itemsControl.getCheckoutList();

		String[] itemDescriptions = new String[checkoutList.size()];
		double[] itemPrices = new double[checkoutList.size()];

		for (int i = 0; i < checkoutList.size(); i++) {
			itemDescriptions[i] = checkoutList.get(i).x;
			itemPrices[i] = checkoutList.get(i).y;
		}
		this.invalidateAllScannedItems();
		for (int i = 0; i < itemDescriptions.length; i++) {
			this.addScannedItem(itemDescriptions[i], itemPrices[i]);
		}
	}

	@Override
	public void productSubtotalUpdated(ItemsControl itemsControl) {
		subtotalLabel.setText("Subtotal: $" + itemsControl.getCheckoutTotal());
	}

	@Override
	public void awaitingItemToBeRemoved(ItemsControl itemsControl, String updateMessage) {
		// TODO Auto-generated method stub
		
	}

}
