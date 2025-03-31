package com.kreative.bin2png;

import java.awt.Dialog;
import java.awt.Frame;
import java.awt.Window;
import java.awt.image.BufferedImage;

public class BytesToImageRunnable implements Runnable {
	private static enum ParentType { DIALOG, FRAME, WINDOW };
	
	private final ParentType parentType;
	private final Dialog parentDialog;
	private final Frame parentFrame;
	private final Window parentWindow;
	private final String title;
	private final int width;
	private final int height;
	private final byte[] data;
	private final int offset;
	private final int scansize;
	private final PixelPacking packing;
	private final int[] colorTable;
	private BufferedImage image;
	
	public BytesToImageRunnable(
		Dialog parent, String title,
		int width, int height,
		byte[] data, int offset, int scansize,
		PixelPacking packing, int[] colorTable
	) {
		this.parentType = ParentType.DIALOG;
		this.parentDialog = parent;
		this.parentFrame = null;
		this.parentWindow = null;
		this.title = title;
		this.width = width;
		this.height = height;
		this.data = data;
		this.offset = offset;
		this.scansize = scansize;
		this.packing = packing;
		this.colorTable = colorTable;
		this.image = null;
	}
	
	public BytesToImageRunnable(
		Frame parent, String title,
		int width, int height,
		byte[] data, int offset, int scansize,
		PixelPacking packing, int[] colorTable
	) {
		this.parentType = ParentType.FRAME;
		this.parentDialog = null;
		this.parentFrame = parent;
		this.parentWindow = null;
		this.title = title;
		this.width = width;
		this.height = height;
		this.data = data;
		this.offset = offset;
		this.scansize = scansize;
		this.packing = packing;
		this.colorTable = colorTable;
		this.image = null;
	}
	
	public BytesToImageRunnable(
		Window parent, String title,
		int width, int height,
		byte[] data, int offset, int scansize,
		PixelPacking packing, int[] colorTable
	) {
		this.parentType = ParentType.WINDOW;
		this.parentDialog = null;
		this.parentFrame = null;
		this.parentWindow = parent;
		this.title = title;
		this.width = width;
		this.height = height;
		this.data = data;
		this.offset = offset;
		this.scansize = scansize;
		this.packing = packing;
		this.colorTable = colorTable;
		this.image = null;
	}
	
	public void run() {
		BytesToImageDialog dialog;
		switch (parentType) {
			case DIALOG:
				dialog = new BytesToImageDialog(
					parentDialog, title,
					width, height,
					data, offset, scansize,
					packing, colorTable
				);
				break;
			case FRAME:
				dialog = new BytesToImageDialog(
					parentFrame, title,
					width, height,
					data, offset, scansize,
					packing, colorTable
				);
				break;
			case WINDOW:
				dialog = new BytesToImageDialog(
					parentWindow, title,
					width, height,
					data, offset, scansize,
					packing, colorTable
				);
				break;
			default:
				throw new IllegalStateException();
		}
		image = dialog.showDialog();
		dialog.dispose();
	}
	
	public BufferedImage getImage() {
		return image;
	}
}
