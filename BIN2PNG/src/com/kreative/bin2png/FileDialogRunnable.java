package com.kreative.bin2png;

import java.awt.Dialog;
import java.awt.FileDialog;
import java.awt.Frame;

public class FileDialogRunnable implements Runnable {
	private static enum ParentType { DIALOG, FRAME };
	
	private final ParentType parentType;
	private final Dialog parentDialog;
	private final Frame parentFrame;
	private final String title;
	private final int mode;
	private String directory;
	private String file;
	
	public FileDialogRunnable(Dialog parent, String title, int mode) {
		this.parentType = ParentType.DIALOG;
		this.parentDialog = parent;
		this.parentFrame = null;
		this.title = title;
		this.mode = mode;
		this.directory = null;
		this.file = null;
	}
	
	public FileDialogRunnable(Frame parent, String title, int mode) {
		this.parentType = ParentType.FRAME;
		this.parentDialog = null;
		this.parentFrame = parent;
		this.title = title;
		this.mode = mode;
		this.directory = null;
		this.file = null;
	}
	
	public void run() {
		FileDialog dialog;
		switch (parentType) {
			case DIALOG: dialog = new FileDialog(parentDialog, title, mode); break;
			case FRAME: dialog = new FileDialog(parentFrame, title, mode); break;
			default: throw new IllegalStateException();
		}
		dialog.setVisible(true);
		directory = dialog.getDirectory();
		file = dialog.getFile();
		dialog.dispose();
	}
	
	public String getDirectory() {
		return directory;
	}
	
	public String getFile() {
		return file;
	}
}
