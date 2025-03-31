package com.kreative.bin2png;

import java.awt.image.BufferedImage;

public class BytesToImage {
	public static BufferedImage bytesToImage(
		int width, int height,
		byte[] data, int offset, int scansize,
		PixelPacking packing, int[] colorTable
	) {
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		colorTable = ColorTables.extendWithFill(packing.lookupTableSize, colorTable, 0);
		int[] scanline = new int[width];
		if (scansize < 0) {
			int y = height;
			while (y > 0) {
				packing.unpack(data, offset, data.length - offset, scanline, 0, width, colorTable);
				image.setRGB(0, --y, width, 1, scanline, 0, width);
				offset -= scansize;
			}
		} else {
			int y = 0;
			while (y < height) {
				packing.unpack(data, offset, data.length - offset, scanline, 0, width, colorTable);
				image.setRGB(0, y++, width, 1, scanline, 0, width);
				offset += scansize;
			}
		}
		return image;
	}
}
