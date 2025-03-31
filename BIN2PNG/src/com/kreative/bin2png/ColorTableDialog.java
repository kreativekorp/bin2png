package com.kreative.bin2png;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import javax.swing.*;

public class ColorTableDialog extends JDialog {
	private static final long serialVersionUID = 1L;
	
	private ColorTablePanel panel;
	private JButton okButton;
	private JButton cancelButton;
	private JButton loadButton;
	private JButton saveButton;
	private Map<String,int[]> presets;
	private JComboBox presetMenu;
	private boolean confirmed = false;
	private boolean eventLock = false;
	
	public ColorTableDialog(Dialog parent, int rows, int columns, int[] colorTable) {
		super(parent, "Color Table");
		setModal(true);
		make(rows, columns, colorTable);
	}
	
	public ColorTableDialog(Frame parent, int rows, int columns, int[] colorTable) {
		super(parent, "Color Table");
		setModal(true);
		make(rows, columns, colorTable);
	}
	
	public ColorTableDialog(Window parent, int rows, int columns, int[] colorTable) {
		super(parent, "Color Table");
		setModal(true);
		make(rows, columns, colorTable);
	}
	
	private void make(int rows, int columns, int[] colorTable) {
		colorTable = ColorTables.extendWithFill(colorTable.length, colorTable, 0);
		panel = new ColorTablePanel(rows, columns, colorTable);
		okButton = new JButton("OK");
		cancelButton = new JButton("Cancel");
		loadButton = new JButton("Load...");
		saveButton = new JButton("Save...");
		
		presets = ColorTables.createPresets(colorTable.length);
		ArrayList<String> presetNames = new ArrayList<String>();
		presetNames.add("Custom");
		presetNames.addAll(presets.keySet());
		String[] presetNameArray = new String[presetNames.size()];
		presetNameArray = presetNames.toArray(presetNameArray);
		presetMenu = new JComboBox(presetNameArray);
		presetMenu.setEditable(false);
		presetMenu.setMaximumRowCount(presetMenu.getItemCount());
		presetMenu.setSelectedIndex(0);
		for (Map.Entry<String,int[]> e : presets.entrySet()) {
			if (Arrays.equals(e.getValue(), colorTable)) {
				presetMenu.setSelectedItem(e.getKey());
				break;
			}
		}
		
		JPanel presetPanel = new JPanel(new BorderLayout(4, 4));
		presetPanel.add(new JLabel("Table:"), BorderLayout.LINE_START);
		presetPanel.add(presetMenu, BorderLayout.CENTER);
		JPanel leftPanel = new JPanel(new BorderLayout(8, 8));
		leftPanel.add(presetPanel, BorderLayout.PAGE_START);
		leftPanel.add(panel, BorderLayout.CENTER);
		
		JPanel buttonPanel = new JPanel(new GridLayout(0, 1, 8, 8));
		buttonPanel.add(okButton);
		buttonPanel.add(cancelButton);
		buttonPanel.add(loadButton);
		buttonPanel.add(saveButton);
		JPanel rightPanel = new JPanel(new BorderLayout());
		rightPanel.add(buttonPanel, BorderLayout.PAGE_START);
		rightPanel.add(Box.createHorizontalStrut(80), BorderLayout.CENTER);
		
		JPanel mainPanel = new JPanel(new BorderLayout(16, 16));
		mainPanel.add(leftPanel, BorderLayout.CENTER);
		mainPanel.add(rightPanel, BorderLayout.LINE_END);
		mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
		
		setContentPane(mainPanel);
		SwingUtils.setDefaultButton(getRootPane(), okButton);
		SwingUtils.setCancelButton(getRootPane(), cancelButton);
		setResizable(false);
		pack();
		setLocationRelativeTo(null);
		okButton.requestFocusInWindow();
		
		panel.addColorTableListener(new ColorTableListener() {
			@Override
			public void dimensionsChanged(ColorTablePanel src) {
				// Nothing.
			}
			@Override
			public void colorTableChanged(ColorTablePanel src) {
				if (eventLock) return;
				eventLock = true;
				presetMenu.setSelectedIndex(0);
				eventLock = false;
			}
			@Override
			public void selectionChanged(ColorTablePanel src) {
				// Nothing.
			}
		});
		
		presetMenu.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (eventLock) return;
				eventLock = true;
				Object presetObject = presetMenu.getSelectedItem();
				if (presetObject != null) {
					String presetString = presetObject.toString();
					if (presetString != null) {
						int[] colorTable = presets.get(presetString);
						if (colorTable != null) {
							colorTable = ColorTables.extendWithFill(colorTable.length, colorTable, 0);
							panel.setColorTable(colorTable);
						}
					}
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
		
		loadButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				load();
			}
		});
		
		saveButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				save();
			}
		});
	}
	
	private static String lastLoadDirectory = null;
	private void load() {
		FileDialog fd = new FileDialog(this, "Load", FileDialog.LOAD);
		if (lastLoadDirectory != null) fd.setDirectory(lastLoadDirectory);
		fd.setVisible(true);
		String parent = fd.getDirectory();
		String name = fd.getFile();
		fd.dispose();
		if (parent == null || name == null) return;
		File file = new File((lastLoadDirectory = parent), name);
		try {
			if (name.toLowerCase().endsWith(".act")) {
				FileInputStream in = new FileInputStream(file);
				int[] colorTable = ColorTables.readACT(in);
				in.close();
				int colorTableLength = panel.getColorTable().length;
				colorTable = ColorTables.extendWithFill(colorTableLength, colorTable, 0);
				panel.setColorTable(colorTable);
				return;
			}
			if (name.toLowerCase().endsWith(".bmp")) {
				FileInputStream in = new FileInputStream(file);
				int[] colorTable = ColorTables.readBMP(in);
				in.close();
				int colorTableLength = panel.getColorTable().length;
				colorTable = ColorTables.extendWithFill(colorTableLength, colorTable, 0);
				panel.setColorTable(colorTable);
				return;
			}
			JOptionPane.showMessageDialog(
				this, "The format of the selected file was not recognized.",
				"Load", JOptionPane.ERROR_MESSAGE
			);
		} catch (IOException ioe) {
			JOptionPane.showMessageDialog(
				this, "An error occurred while loading the selected file.",
				"Load", JOptionPane.ERROR_MESSAGE
			);
		}
	}
	
	private static String lastSaveDirectory = null;
	private void save() {
		FileDialog fd = new FileDialog(this, "Save", FileDialog.SAVE);
		if (lastSaveDirectory != null) fd.setDirectory(lastSaveDirectory);
		fd.setVisible(true);
		String parent = fd.getDirectory();
		String name = fd.getFile();
		fd.dispose();
		if (parent == null || name == null) return;
		File file = new File((lastSaveDirectory = parent), name);
		try {
			if (name.toLowerCase().endsWith(".act")) {
				FileOutputStream out = new FileOutputStream(file);
				ColorTables.writeACT(out, panel.getColorTable());
				out.close();
				return;
			}
			if (name.toLowerCase().endsWith(".bmp")) {
				FileOutputStream out = new FileOutputStream(file);
				ColorTables.writeBMP(out, panel.getColorTable());
				out.close();
				return;
			}
			JOptionPane.showMessageDialog(
				this, "The format of the selected file was not recognized.",
				"Save", JOptionPane.ERROR_MESSAGE
			);
		} catch (IOException ioe) {
			JOptionPane.showMessageDialog(
				this, "An error occurred while saving the selected file.",
				"Save", JOptionPane.ERROR_MESSAGE
			);
		}
	}
	
	public int[] showDialog() {
		confirmed = false;
		setVisible(true);
		return confirmed ? panel.getColorTable() : null;
	}
}
