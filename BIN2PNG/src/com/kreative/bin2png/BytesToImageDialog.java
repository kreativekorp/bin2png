package com.kreative.bin2png;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;

public class BytesToImageDialog extends JDialog {
	private static final long serialVersionUID = 1L;
	
	public BytesToImageDialog(
		Dialog parent, String title,
		int width, int height,
		byte[] data, int offset, int scansize,
		PixelPacking packing, int[] colorTable
	) {
		super(parent, title); setModal(true); this.data = data;
		make(width, height, offset, scansize, packing, colorTable);
	}
	
	public BytesToImageDialog(
		Frame parent, String title,
		int width, int height,
		byte[] data, int offset, int scansize,
		PixelPacking packing, int[] colorTable
	) {
		super(parent, title); setModal(true); this.data = data;
		make(width, height, offset, scansize, packing, colorTable);
	}
	
	public BytesToImageDialog(
		Window parent, String title,
		int width, int height,
		byte[] data, int offset, int scansize,
		PixelPacking packing, int[] colorTable
	) {
		super(parent, title); setModal(true); this.data = data;
		make(width, height, offset, scansize, packing, colorTable);
	}
	
	private final byte[] data;
	private SpinnerNumberModel widthModel;
	private SpinnerNumberModel heightModel;
	private SpinnerNumberModel offsetModel;
	private SpinnerNumberModel scansizeModel;
	private JComboBox packingMenu;
	private JComboBox ctPresetMenu;
	private JButton ctEditButton;
	private int[] colorTable;
	private Map<String,int[]> ctPresets;
	private BufferedImage image;
	private ImageIcon imageIcon;
	private JLabel imageLabel;
	private JScrollPane imagePane;
	private JButton okButton;
	private JButton cancelButton;
	private boolean confirmed = false;
	private boolean eventLock = false;
	private long lastTimestamp = 0;
	
	private void make(int width, int height, int offset, int scansize, PixelPacking packing, int[] colorTable) {
		if (width < 1) width = 32;
		if (height < 1) height = 32;
		if (offset < 0) offset = 0;
		if (packing == null) packing = PixelPacking.PACK_1BPP_MSB_TO_LSB;
		
		widthModel = new SpinnerNumberModel(width, 1, (data.length * 8), 1);
		heightModel = new SpinnerNumberModel(height, 1, (data.length * 8), 1);
		offsetModel = new SpinnerNumberModel(offset, 0, data.length, 1);
		scansizeModel = new SpinnerNumberModel(scansize, -data.length, +data.length, 1);
		packingMenu = new JComboBox(PixelPacking.values());
		packingMenu.setEditable(false);
		packingMenu.setMaximumRowCount(packingMenu.getItemCount());
		packingMenu.setSelectedItem(packing);
		ctPresetMenu = new JComboBox(new String[]{"Custom"});
		ctPresetMenu.setEditable(false);
		ctPresetMenu.setEnabled(packing.lookupTableSize > 0);
		ctPresetMenu.setMaximumRowCount(1);
		ctPresetMenu.setSelectedIndex(0);
		ctEditButton = new JButton("Edit");
		ctEditButton.setEnabled(packing.lookupTableSize > 0);
		this.colorTable = colorTable;
		updatePresets();
		
		image = new BufferedImage(8, 8, BufferedImage.TYPE_INT_ARGB);
		imageIcon = new ImageIcon(image);
		imageLabel = new JLabel(imageIcon);
		imagePane = new JScrollPane(imageLabel, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		new UpdateImageThread().start();
		
		JPanel buttonPanel = new JPanel(new GridLayout(1, 0, 8, 8));
		buttonPanel.add((okButton = new JButton("OK")));
		buttonPanel.add((cancelButton = new JButton("Cancel")));
		
		JPanel widthPanel = wrapBorder(new JSpinner(widthModel), BorderLayout.LINE_START);
		JPanel heightPanel = wrapBorder(new JSpinner(heightModel), BorderLayout.LINE_START);
		JPanel offsetPanel = wrapBorder(new JSpinner(offsetModel), BorderLayout.LINE_START);
		JPanel scansizePanel = wrapBorder(new JSpinner(scansizeModel), BorderLayout.LINE_START);
		JPanel ctPresetPanel = wrapBorder(ctEditButton, BorderLayout.LINE_END, ctPresetMenu);
		
		JPanel labelPanel = wrapGridVertical("Image Width", "Image Height", "Data Offset", "Row Length", "Bits Per Pixel", "Color Table");
		JPanel inputPanel = wrapGridVertical(widthPanel, heightPanel, offsetPanel, scansizePanel, packingMenu, ctPresetPanel);
		JPanel formPanel1 = wrapBorder(labelPanel, BorderLayout.LINE_START, inputPanel);
		JPanel formPanel2 = wrapBorder(buttonPanel, BorderLayout.PAGE_END, formPanel1);
		JPanel formPanel3 = wrapBorder(formPanel2, BorderLayout.PAGE_START);
		formPanel3.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 12));
		JPanel mainPanel = wrapBorder(formPanel3, BorderLayout.LINE_START, imagePane);
		
		setContentPane(mainPanel);
		SwingUtils.setDefaultButton(getRootPane(), okButton);
		SwingUtils.setCancelButton(getRootPane(), cancelButton);
		setSize(600, 400);
		setLocationRelativeTo(null);
		okButton.requestFocusInWindow();
		
		ChangeListener change = new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				if (eventLock) return;
				eventLock = true;
				new UpdateImageThread().start();
				eventLock = false;
			}
		};
		
		widthModel.addChangeListener(change);
		heightModel.addChangeListener(change);
		offsetModel.addChangeListener(change);
		scansizeModel.addChangeListener(change);
		
		packingMenu.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (eventLock) return;
				eventLock = true;
				updatePresets();
				new UpdateImageThread().start();
				eventLock = false;
			}
		});
		
		ctPresetMenu.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (eventLock) return;
				eventLock = true;
				Object ctPresetObject = ctPresetMenu.getSelectedItem();
				if (ctPresetObject != null) {
					String ctPresetString = ctPresetObject.toString();
					if (ctPresetString != null) {
						int[] colorTable = ctPresets.get(ctPresetString);
						if (colorTable != null) {
							BytesToImageDialog.this.colorTable = colorTable;
							new UpdateImageThread().start();
						}
					}
				}
				eventLock = false;
			}
		});
		
		ctEditButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (eventLock) return;
				eventLock = true;
				ColorTableDialog dialog = new ColorTableDialog(
					BytesToImageDialog.this, 16, 16,
					BytesToImageDialog.this.colorTable
				);
				int[] colorTable = dialog.showDialog();
				dialog.dispose();
				if (colorTable != null) {
					ctPresetMenu.setSelectedIndex(0);
					BytesToImageDialog.this.colorTable = colorTable;
					new UpdateImageThread().start();
				}
				eventLock = false;
			}
		});
		
		okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				confirmed = true;
				dispose();
			}
		});
		
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				confirmed = false;
				dispose();
			}
		});
	}
	
	private void updatePresets() {
		PixelPacking packing = (PixelPacking)(packingMenu.getSelectedItem());
		if (colorTable == null || colorTable.length != packing.lookupTableSize) {
			colorTable = ColorTables.createBlackToWhite(packing.lookupTableSize);
		}
		ctPresets = ColorTables.createPresets(packing.lookupTableSize);
		ArrayList<String> ctPresetNames = new ArrayList<String>();
		ctPresetNames.add("Custom");
		ctPresetNames.addAll(ctPresets.keySet());
		String[] ctPresetNameArray = new String[ctPresetNames.size()];
		ctPresetNameArray = ctPresetNames.toArray(ctPresetNameArray);
		ctEditButton.setEnabled(packing.lookupTableSize > 0);
		ctPresetMenu.setEnabled(packing.lookupTableSize > 0);
		ctPresetMenu.setModel(new DefaultComboBoxModel(ctPresetNameArray));
		ctPresetMenu.setMaximumRowCount(ctPresetMenu.getItemCount());
		ctPresetMenu.setSelectedIndex(0);
		for (Map.Entry<String,int[]> e : ctPresets.entrySet()) {
			if (Arrays.equals(e.getValue(), colorTable)) {
				ctPresetMenu.setSelectedItem(e.getKey());
				break;
			}
		}
	}
	
	private class UpdateImageThread extends Thread {
		private final long timestamp = lastTimestamp = System.currentTimeMillis();
		private final int width = widthModel.getNumber().intValue();
		private final int height = heightModel.getNumber().intValue();
		private final int offset = offsetModel.getNumber().intValue();
		private final int scansize = scansizeModel.getNumber().intValue();
		private final PixelPacking packing = (PixelPacking)(packingMenu.getSelectedItem());
		private final int[] colorTable = BytesToImageDialog.this.colorTable;
		public void run() {
			BufferedImage newImage = BytesToImage.bytesToImage(width, height, data, offset, scansize, packing, colorTable);
			if (lastTimestamp == timestamp) {
				imageIcon.setImage((image = newImage));
				imageLabel.repaint();
				imageLabel.revalidate();
			}
		}
	}
	
	public BufferedImage showDialog() {
		confirmed = false;
		setVisible(true);
		return confirmed ? image : null;
	}
	
	private static final JPanel wrapBorder(JComponent component, String constraints) {
		JPanel panel = new JPanel(new BorderLayout());
		panel.add(component, constraints);
		return panel;
	}
	
	private static final JPanel wrapBorder(JComponent component, String constraints, JComponent center) {
		JPanel panel = new JPanel(new BorderLayout(8, 8));
		panel.add(component, constraints);
		panel.add(center, BorderLayout.CENTER);
		return panel;
	}
	
	private static final JPanel wrapGridVertical(String... strings) {
		JPanel panel = new JPanel(new GridLayout(0, 1, 8, 8));
		for (String string : strings) panel.add(new JLabel(string));
		return panel;
	}
	
	private static final JPanel wrapGridVertical(JComponent... components) {
		JPanel panel = new JPanel(new GridLayout(0, 1, 8, 8));
		for (JComponent component : components) panel.add(component);
		return panel;
	}
}
