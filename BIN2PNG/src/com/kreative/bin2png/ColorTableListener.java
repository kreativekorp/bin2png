package com.kreative.bin2png;

public interface ColorTableListener {
	public void dimensionsChanged(ColorTablePanel src);
	public void colorTableChanged(ColorTablePanel src);
	public void selectionChanged(ColorTablePanel src);
}
