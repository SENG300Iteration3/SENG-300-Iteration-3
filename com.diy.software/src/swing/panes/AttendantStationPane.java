package swing.panes;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JScrollPane;

import com.diy.software.controllers.AttendantControl;
import com.diy.software.controllers.BagsControl;
import com.diy.software.controllers.StationControl;
import com.diy.software.listeners.AttendantControlListener;
import com.diy.software.listeners.BagsControlListener;

import swing.styling.GUI_Color_Palette;
import swing.styling.GUI_Fonts;
import swing.styling.GUI_JButton;
import swing.styling.GUI_JLabel;
import swing.styling.GUI_JPanel;
import swing.styling.Screen;

public class AttendantStationPane extends Screen implements AttendantControlListener, BagsControlListener {

	private StationControl sc;
	private BagsControl bc;
	private AttendantControl ac;
	private boolean cusAddedBags = false;
	GUI_JButton approveAddedBagsButton;
	GUI_JButton addInkToPrinterButton;
	GUI_JButton addPaperToPrinterButton;
	GUI_JButton approveNoBagging;
	GUI_JButton startUpButton;
	GUI_JLabel weightDisplayLabel, weightDescrepancyMssg, lowInkLabel, lowPaperLabel, adjustCoinLabel, adjustBanknoteLabel;

	private static String HeaderText = "Attendant Screen";

	public AttendantStationPane(StationControl sc) {
		super(sc, HeaderText);
		this.sc = sc;
		bc = sc.getBagsControl();
		bc.addListener(this);

		ac = sc.getAttendantControl();
		ac.addListener(this);

		int width = 400;
		int height = 50;

		approveAddedBagsButton = makeButton("approvedAddedBags()");
		approveAddedBagsButton.setActionCommand("approve added bags");
		approveAddedBagsButton.addActionListener(ac);
		approveAddedBagsButton.setPreferredSize(new Dimension(width, height));

		addInkToPrinterButton = makeButton("Add ink");
		addInkToPrinterButton.setActionCommand("addInk");
		addInkToPrinterButton.addActionListener(ac);
		addInkToPrinterButton.setPreferredSize(new Dimension(width, height));
		
		addPaperToPrinterButton = makeButton("Add paper");
		addPaperToPrinterButton.setActionCommand("addPaper");
		addPaperToPrinterButton.addActionListener(ac);
		addPaperToPrinterButton.setPreferredSize(new Dimension(width, height));
		
		approveNoBagging = makeButton("Approve no bagging");
		approveNoBagging.setActionCommand("approve no bag");
		approveNoBagging.addActionListener(ac);
		approveNoBagging.setPreferredSize(new Dimension(width, height));
		
		startUpButton = makeButton("Start up station");
		startUpButton.setActionCommand("startUp");
		startUpButton.addActionListener(ac);
		startUpButton.setPreferredSize(new Dimension(width, height));
		
		weightDescrepancyMssg = initalizeLabel("weightDiscrepancyMsg");
		weightDisplayLabel = initalizeLabel("weightDisplayLabel");
		lowInkLabel = initalizeLabel("Low ink");
		lowPaperLabel = initalizeLabel("Low paper");
		adjustCoinLabel = initalizeLabel("Adjust coin");
		adjustBanknoteLabel = initalizeLabel("Adjust Banknote");
		
		
		GUI_JPanel notificationPanel = new GUI_JPanel();
		notificationPanel.setLayout(new GridLayout(2, 3));
		
		notificationPanel.add(weightDescrepancyMssg);
		notificationPanel.add(weightDisplayLabel);
		notificationPanel.add(lowInkLabel);
		notificationPanel.add(lowPaperLabel);
		notificationPanel.add(adjustCoinLabel);
		notificationPanel.add(adjustBanknoteLabel);
		
		this.addLayer(notificationPanel, 0);
		
		
		
		JScrollPane buttonScrollPane = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		buttonScrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(24, 0));
		buttonScrollPane.setBackground(GUI_Color_Palette.DARK_BROWN);
		buttonScrollPane.setPreferredSize(new Dimension(450, 200));
		buttonScrollPane.setBorder(BorderFactory.createMatteBorder(20, 20, 20, 20, GUI_Color_Palette.DARK_BLUE));
		
		
		GUI_JPanel buttonsPanel	= new GUI_JPanel();
		buttonsPanel.setLayout(new GridLayout(5, 1));
		buttonScrollPane.getViewport().add(buttonsPanel);
		
		buttonsPanel.add(approveAddedBagsButton);
		buttonsPanel.add(addInkToPrinterButton);
		buttonsPanel.add(addPaperToPrinterButton);
		buttonsPanel.add(approveNoBagging);
		buttonsPanel.add(startUpButton);
		
		
		this.addLayer(buttonScrollPane, 50);

		
		addInkToPrinterButton.setEnabled(false);
		addPaperToPrinterButton.setEnabled(false);
		approveNoBagging.setEnabled(false);
		approveAddedBagsButton.setEnabled(cusAddedBags);
		startUpButton.setEnabled(true);
	}
	
	
	private GUI_JLabel initalizeLabel(String labelText) {

		GUI_JLabel label = new GUI_JLabel();
		label.setText(labelText);
		label.setForeground(GUI_Color_Palette.WHITE);
		label.setBackground(GUI_Color_Palette.LIGHT_BROWN);
		label.setPreferredSize(new Dimension(300, 100));
		label.setFont(GUI_Fonts.FRANKLIN_BOLD);
		label.setHorizontalAlignment(JLabel.CENTER);
		label.setBorder(BorderFactory.createMatteBorder(20, 20, 20, 20, GUI_Color_Palette.DARK_BLUE));

		return label;
	}

	@Override
	public void awaitingAttendantToVerifyBagsPlacedInBaggingArea(BagsControl bc) {
		approveAddedBagsButton.setEnabled(true);
		weightDisplayLabel.setText("Weight on station scale: " + sc.getWeightOfLastItemAddedToBaggingArea() + "g");
	}

	@Override
	public void attendantApprovedBags(AttendantControl ac) {
		approveAddedBagsButton.setEnabled(false);
		weightDisplayLabel.setText("");
	}

	@Override
	public void attendantPreventUse(AttendantControl ac) {
		// TODO: implement method
	}

	@Override
	public void readyToAcceptNewBagsInBaggingArea(BagsControl bc) {
		approveAddedBagsButton.setEnabled(false);
		weightDisplayLabel.setText("");
	}

	@Override
	public void awaitingCustomerToFinishPlacingBagsInBaggingArea(BagsControl bc) {
		// TODO Auto-generated method stub

	}

	private GUI_JButton makeButton(String text) {
		int overallMargin = 10;

		/* Setup of the title's panel */
		GUI_JButton button = new GUI_JButton();
		button.setText(text);
		button.setBackground(GUI_Color_Palette.DARK_BROWN);
		button.setForeground(GUI_Color_Palette.WHITE);

		button.setFont(new Font("Franklin Gothic", Font.BOLD, 22));

		button.setBorder(BorderFactory.createLineBorder(GUI_Color_Palette.DARK_BLUE, overallMargin));
		button.setLayout(new BorderLayout());

		/* Adding the panel to the window */
		return button;
	}

	@Override
	public void addPaperState() {
		approveAddedBagsButton.setEnabled(false);
		addInkToPrinterButton.setEnabled(false);
		addPaperToPrinterButton.setEnabled(true);
	}

	@Override
	public void addInkState() {
		approveAddedBagsButton.setEnabled(false);
		addInkToPrinterButton.setEnabled(true);
		addPaperToPrinterButton.setEnabled(false);	
	}

	@Override
	public void printerNotLowState() {
		approveAddedBagsButton.setEnabled(false);
		addInkToPrinterButton.setEnabled(false);
		addPaperToPrinterButton.setEnabled(false);	
	}

	@Override
	public void signalWeightDescrepancy(String updateMessage) {
		weightDescrepancyMssg.setText(updateMessage);
		
	}

	@Override
	public void noBagRequest() {
		approveAddedBagsButton.setEnabled(false);
		addInkToPrinterButton.setEnabled(false);
		addPaperToPrinterButton.setEnabled(false);	
		approveNoBagging.setEnabled(true);
	}

	@Override
	public void initialState() {
		approveAddedBagsButton.setEnabled(false);
		addInkToPrinterButton.setEnabled(false);
		addPaperToPrinterButton.setEnabled(false);	
		approveNoBagging.setEnabled(false);
		weightDescrepancyMssg.setText("");
	}
	
	public static void main(String args[]) {
		StationControl sc = new StationControl();
		AttendantStationPane ap = new AttendantStationPane(sc);
		ap.openInNewJFrame();
	}
}
