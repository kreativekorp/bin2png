package com.kreative.bin2png;

import java.util.HashSet;

public enum PixelPacking {
	PACK_1BPP_MSB_TO_LSB (1 << 1, "1BPP_MSB_TO_LSB", "PACK_1BPP", "1BPP", "1") {
		public void unpack(byte[] data, int dataOffset, int dataLength, int[] rgb, int rgbOffset, int rgbLength, int[] ct) {
			while (dataLength > 0 && rgbLength > 0) {
				int chunk = data[dataOffset] & 0xFF; dataOffset++; dataLength--;
				if (rgbLength > 0) { rgb[rgbOffset] = ct[(chunk >> 7) & 1]; rgbOffset++; rgbLength--; }
				if (rgbLength > 0) { rgb[rgbOffset] = ct[(chunk >> 6) & 1]; rgbOffset++; rgbLength--; }
				if (rgbLength > 0) { rgb[rgbOffset] = ct[(chunk >> 5) & 1]; rgbOffset++; rgbLength--; }
				if (rgbLength > 0) { rgb[rgbOffset] = ct[(chunk >> 4) & 1]; rgbOffset++; rgbLength--; }
				if (rgbLength > 0) { rgb[rgbOffset] = ct[(chunk >> 3) & 1]; rgbOffset++; rgbLength--; }
				if (rgbLength > 0) { rgb[rgbOffset] = ct[(chunk >> 2) & 1]; rgbOffset++; rgbLength--; }
				if (rgbLength > 0) { rgb[rgbOffset] = ct[(chunk >> 1) & 1]; rgbOffset++; rgbLength--; }
				if (rgbLength > 0) { rgb[rgbOffset] = ct[(chunk >> 0) & 1]; rgbOffset++; rgbLength--; }
			}
		}
	},
	PACK_1BPP_LSB_TO_MSB (1 << 1, "1BPP_LSB_TO_MSB") {
		public void unpack(byte[] data, int dataOffset, int dataLength, int[] rgb, int rgbOffset, int rgbLength, int[] ct) {
			while (dataLength > 0 && rgbLength > 0) {
				int chunk = data[dataOffset] & 0xFF; dataOffset++; dataLength--;
				if (rgbLength > 0) { rgb[rgbOffset] = ct[(chunk >> 0) & 1]; rgbOffset++; rgbLength--; }
				if (rgbLength > 0) { rgb[rgbOffset] = ct[(chunk >> 1) & 1]; rgbOffset++; rgbLength--; }
				if (rgbLength > 0) { rgb[rgbOffset] = ct[(chunk >> 2) & 1]; rgbOffset++; rgbLength--; }
				if (rgbLength > 0) { rgb[rgbOffset] = ct[(chunk >> 3) & 1]; rgbOffset++; rgbLength--; }
				if (rgbLength > 0) { rgb[rgbOffset] = ct[(chunk >> 4) & 1]; rgbOffset++; rgbLength--; }
				if (rgbLength > 0) { rgb[rgbOffset] = ct[(chunk >> 5) & 1]; rgbOffset++; rgbLength--; }
				if (rgbLength > 0) { rgb[rgbOffset] = ct[(chunk >> 6) & 1]; rgbOffset++; rgbLength--; }
				if (rgbLength > 0) { rgb[rgbOffset] = ct[(chunk >> 7) & 1]; rgbOffset++; rgbLength--; }
			}
		}
	},
	PACK_2BPP_MSB_TO_LSB (1 << 2, "2BPP_MSB_TO_LSB", "PACK_2BPP", "2BPP", "2") {
		public void unpack(byte[] data, int dataOffset, int dataLength, int[] rgb, int rgbOffset, int rgbLength, int[] ct) {
			while (dataLength > 0 && rgbLength > 0) {
				int chunk = data[dataOffset] & 0xFF; dataOffset++; dataLength--;
				if (rgbLength > 0) { rgb[rgbOffset] = ct[(chunk >> 6) & 3]; rgbOffset++; rgbLength--; }
				if (rgbLength > 0) { rgb[rgbOffset] = ct[(chunk >> 4) & 3]; rgbOffset++; rgbLength--; }
				if (rgbLength > 0) { rgb[rgbOffset] = ct[(chunk >> 2) & 3]; rgbOffset++; rgbLength--; }
				if (rgbLength > 0) { rgb[rgbOffset] = ct[(chunk >> 0) & 3]; rgbOffset++; rgbLength--; }
			}
		}
	},
	PACK_2BPP_LSB_TO_MSB (1 << 2, "2BPP_LSB_TO_MSB") {
		public void unpack(byte[] data, int dataOffset, int dataLength, int[] rgb, int rgbOffset, int rgbLength, int[] ct) {
			while (dataLength > 0 && rgbLength > 0) {
				int chunk = data[dataOffset] & 0xFF; dataOffset++; dataLength--;
				if (rgbLength > 0) { rgb[rgbOffset] = ct[(chunk >> 0) & 3]; rgbOffset++; rgbLength--; }
				if (rgbLength > 0) { rgb[rgbOffset] = ct[(chunk >> 2) & 3]; rgbOffset++; rgbLength--; }
				if (rgbLength > 0) { rgb[rgbOffset] = ct[(chunk >> 4) & 3]; rgbOffset++; rgbLength--; }
				if (rgbLength > 0) { rgb[rgbOffset] = ct[(chunk >> 6) & 3]; rgbOffset++; rgbLength--; }
			}
		}
	},
	PACK_4BPP_MSB_TO_LSB (1 << 4, "4BPP_MSB_TO_LSB", "PACK_4BPP", "4BPP", "4") {
		public void unpack(byte[] data, int dataOffset, int dataLength, int[] rgb, int rgbOffset, int rgbLength, int[] ct) {
			while (dataLength > 0 && rgbLength > 0) {
				int chunk = data[dataOffset] & 0xFF; dataOffset++; dataLength--;
				if (rgbLength > 0) { rgb[rgbOffset] = ct[(chunk >> 4) & 15]; rgbOffset++; rgbLength--; }
				if (rgbLength > 0) { rgb[rgbOffset] = ct[(chunk >> 0) & 15]; rgbOffset++; rgbLength--; }
			}
		}
	},
	PACK_4BPP_LSB_TO_MSB (1 << 4, "4BPP_LSB_TO_MSB") {
		public void unpack(byte[] data, int dataOffset, int dataLength, int[] rgb, int rgbOffset, int rgbLength, int[] ct) {
			while (dataLength > 0 && rgbLength > 0) {
				int chunk = data[dataOffset] & 0xFF; dataOffset++; dataLength--;
				if (rgbLength > 0) { rgb[rgbOffset] = ct[(chunk >> 0) & 15]; rgbOffset++; rgbLength--; }
				if (rgbLength > 0) { rgb[rgbOffset] = ct[(chunk >> 4) & 15]; rgbOffset++; rgbLength--; }
			}
		}
	},
	PACK_8BPP (1 << 8, "8BPP", "8") {
		public void unpack(byte[] data, int dataOffset, int dataLength, int[] rgb, int rgbOffset, int rgbLength, int[] ct) {
			while (dataLength > 0 && rgbLength > 0) {
				int chunk = data[dataOffset] & 0xFF; dataOffset++; dataLength--;
				rgb[rgbOffset] = ct[chunk]; rgbOffset++; rgbLength--;
			}
		}
	},
	PACK_15BPP_RGB555_BE (0, "15BPP_RGB555_BE", "PACK_RGB555_BE", "RGB555_BE") {
		public void unpack(byte[] data, int dataOffset, int dataLength, int[] rgb, int rgbOffset, int rgbLength, int[] ct) {
			while (dataLength > 0 && rgbLength > 0) {
				int chunk = ((data[dataOffset] & 0xFF) << 8); dataOffset++; dataLength--;
				if (dataLength > 0) { chunk |= (data[dataOffset] & 0xFF); dataOffset++; dataLength--; }
				int r0 = ((chunk >> 10) & 31), g0 = ((chunk >> 5) & 31), b0 = ((chunk >> 0) & 31);
				int r1 = ((r0 * 255) / 31), g1 = ((g0 * 255) / 31), b1 = ((b0 * 255) / 31);
				rgb[rgbOffset] = (255 << 24) | (r1 << 16) | (g1 << 8) | (b1 << 0); rgbOffset++; rgbLength--;
			}
		}
	},
	PACK_15BPP_RGB555_LE (0, "15BPP_RGB555_LE", "PACK_RGB555_LE", "RGB555_LE") {
		public void unpack(byte[] data, int dataOffset, int dataLength, int[] rgb, int rgbOffset, int rgbLength, int[] ct) {
			while (dataLength > 0 && rgbLength > 0) {
				int chunk = (data[dataOffset] & 0xFF); dataOffset++; dataLength--;
				if (dataLength > 0) { chunk |= ((data[dataOffset] & 0xFF) << 8); dataOffset++; dataLength--; }
				int r0 = ((chunk >> 10) & 31), g0 = ((chunk >> 5) & 31), b0 = ((chunk >> 0) & 31);
				int r1 = ((r0 * 255) / 31), g1 = ((g0 * 255) / 31), b1 = ((b0 * 255) / 31);
				rgb[rgbOffset] = (255 << 24) | (r1 << 16) | (g1 << 8) | (b1 << 0); rgbOffset++; rgbLength--;
			}
		}
	},
	PACK_15BPP_BGR555_BE (0, "15BPP_BGR555_BE", "PACK_BGR555_BE", "BGR555_BE") {
		public void unpack(byte[] data, int dataOffset, int dataLength, int[] rgb, int rgbOffset, int rgbLength, int[] ct) {
			while (dataLength > 0 && rgbLength > 0) {
				int chunk = ((data[dataOffset] & 0xFF) << 8); dataOffset++; dataLength--;
				if (dataLength > 0) { chunk |= (data[dataOffset] & 0xFF); dataOffset++; dataLength--; }
				int b0 = ((chunk >> 10) & 31), g0 = ((chunk >> 5) & 31), r0 = ((chunk >> 0) & 31);
				int b1 = ((b0 * 255) / 31), g1 = ((g0 * 255) / 31), r1 = ((r0 * 255) / 31);
				rgb[rgbOffset] = (255 << 24) | (r1 << 16) | (g1 << 8) | (b1 << 0); rgbOffset++; rgbLength--;
			}
		}
	},
	PACK_15BPP_BGR555_LE (0, "15BPP_BGR555_LE", "PACK_BGR555_LE", "BGR555_LE") {
		public void unpack(byte[] data, int dataOffset, int dataLength, int[] rgb, int rgbOffset, int rgbLength, int[] ct) {
			while (dataLength > 0 && rgbLength > 0) {
				int chunk = (data[dataOffset] & 0xFF); dataOffset++; dataLength--;
				if (dataLength > 0) { chunk |= ((data[dataOffset] & 0xFF) << 8); dataOffset++; dataLength--; }
				int b0 = ((chunk >> 10) & 31), g0 = ((chunk >> 5) & 31), r0 = ((chunk >> 0) & 31);
				int b1 = ((b0 * 255) / 31), g1 = ((g0 * 255) / 31), r1 = ((r0 * 255) / 31);
				rgb[rgbOffset] = (255 << 24) | (r1 << 16) | (g1 << 8) | (b1 << 0); rgbOffset++; rgbLength--;
			}
		}
	},
	PACK_16BPP_RGB565_BE (0, "16BPP_RGB565_BE", "PACK_RGB565_BE", "RGB565_BE") {
		public void unpack(byte[] data, int dataOffset, int dataLength, int[] rgb, int rgbOffset, int rgbLength, int[] ct) {
			while (dataLength > 0 && rgbLength > 0) {
				int chunk = ((data[dataOffset] & 0xFF) << 8); dataOffset++; dataLength--;
				if (dataLength > 0) { chunk |= (data[dataOffset] & 0xFF); dataOffset++; dataLength--; }
				int r0 = ((chunk >> 11) & 31), g0 = ((chunk >> 5) & 63), b0 = ((chunk >> 0) & 31);
				int r1 = ((r0 * 255) / 31), g1 = ((g0 * 255) / 63), b1 = ((b0 * 255) / 31);
				rgb[rgbOffset] = (255 << 24) | (r1 << 16) | (g1 << 8) | (b1 << 0); rgbOffset++; rgbLength--;
			}
		}
	},
	PACK_16BPP_RGB565_LE (0, "16BPP_RGB565_LE", "PACK_RGB565_LE", "RGB565_LE") {
		public void unpack(byte[] data, int dataOffset, int dataLength, int[] rgb, int rgbOffset, int rgbLength, int[] ct) {
			while (dataLength > 0 && rgbLength > 0) {
				int chunk = (data[dataOffset] & 0xFF); dataOffset++; dataLength--;
				if (dataLength > 0) { chunk |= ((data[dataOffset] & 0xFF) << 8); dataOffset++; dataLength--; }
				int r0 = ((chunk >> 11) & 31), g0 = ((chunk >> 5) & 63), b0 = ((chunk >> 0) & 31);
				int r1 = ((r0 * 255) / 31), g1 = ((g0 * 255) / 63), b1 = ((b0 * 255) / 31);
				rgb[rgbOffset] = (255 << 24) | (r1 << 16) | (g1 << 8) | (b1 << 0); rgbOffset++; rgbLength--;
			}
		}
	},
	PACK_16BPP_BGR565_BE (0, "16BPP_BGR565_BE", "PACK_BGR565_BE", "BGR565_BE") {
		public void unpack(byte[] data, int dataOffset, int dataLength, int[] rgb, int rgbOffset, int rgbLength, int[] ct) {
			while (dataLength > 0 && rgbLength > 0) {
				int chunk = ((data[dataOffset] & 0xFF) << 8); dataOffset++; dataLength--;
				if (dataLength > 0) { chunk |= (data[dataOffset] & 0xFF); dataOffset++; dataLength--; }
				int b0 = ((chunk >> 11) & 31), g0 = ((chunk >> 5) & 63), r0 = ((chunk >> 0) & 31);
				int b1 = ((b0 * 255) / 31), g1 = ((g0 * 255) / 63), r1 = ((r0 * 255) / 31);
				rgb[rgbOffset] = (255 << 24) | (r1 << 16) | (g1 << 8) | (b1 << 0); rgbOffset++; rgbLength--;
			}
		}
	},
	PACK_16BPP_BGR565_LE (0, "16BPP_BGR565_LE", "PACK_BGR565_LE", "BGR565_LE") {
		public void unpack(byte[] data, int dataOffset, int dataLength, int[] rgb, int rgbOffset, int rgbLength, int[] ct) {
			while (dataLength > 0 && rgbLength > 0) {
				int chunk = (data[dataOffset] & 0xFF); dataOffset++; dataLength--;
				if (dataLength > 0) { chunk |= ((data[dataOffset] & 0xFF) << 8); dataOffset++; dataLength--; }
				int b0 = ((chunk >> 11) & 31), g0 = ((chunk >> 5) & 63), r0 = ((chunk >> 0) & 31);
				int b1 = ((b0 * 255) / 31), g1 = ((g0 * 255) / 63), r1 = ((r0 * 255) / 31);
				rgb[rgbOffset] = (255 << 24) | (r1 << 16) | (g1 << 8) | (b1 << 0); rgbOffset++; rgbLength--;
			}
		}
	},
	PACK_16BPP_GRAY_BE (0, "PACK_16BPP_GREY_BE", "16BPP_GRAY_BE", "16BPP_GREY_BE", "GRAY16_BE", "GREY16_BE") {
		public void unpack(byte[] data, int dataOffset, int dataLength, int[] rgb, int rgbOffset, int rgbLength, int[] ct) {
			while (dataLength > 0 && rgbLength > 0) {
				int chunk = ((data[dataOffset] & 0xFF) << 8); dataOffset++; dataLength--;
				if (dataLength > 0) { chunk |= (data[dataOffset] & 0xFF); dataOffset++; dataLength--; }
				int k = chunk / 257;
				rgb[rgbOffset] = (255 << 24) | (k << 16) | (k << 8) | (k << 0); rgbOffset++; rgbLength--;
			}
		}
	},
	PACK_16BPP_GRAY_LE (0, "PACK_16BPP_GREY_LE", "16BPP_GRAY_LE", "16BPP_GREY_LE", "GRAY16_LE", "GREY16_LE") {
		public void unpack(byte[] data, int dataOffset, int dataLength, int[] rgb, int rgbOffset, int rgbLength, int[] ct) {
			while (dataLength > 0 && rgbLength > 0) {
				int chunk = (data[dataOffset] & 0xFF); dataOffset++; dataLength--;
				if (dataLength > 0) { chunk |= ((data[dataOffset] & 0xFF) << 8); dataOffset++; dataLength--; }
				int k = chunk / 257;
				rgb[rgbOffset] = (255 << 24) | (k << 16) | (k << 8) | (k << 0); rgbOffset++; rgbLength--;
			}
		}
	},
	PACK_16BPP_CUSTOM_BE (1 << 16, "16BPP_CUSTOM_BE", "PACK_16BPP_BE", "16BPP_BE", "16BE") {
		public void unpack(byte[] data, int dataOffset, int dataLength, int[] rgb, int rgbOffset, int rgbLength, int[] ct) {
			while (dataLength > 0 && rgbLength > 0) {
				int chunk = ((data[dataOffset] & 0xFF) << 8); dataOffset++; dataLength--;
				if (dataLength > 0) { chunk |= (data[dataOffset] & 0xFF); dataOffset++; dataLength--; }
				rgb[rgbOffset] = ct[chunk]; rgbOffset++; rgbLength--;
			}
		}
	},
	PACK_16BPP_CUSTOM_LE (1 << 16, "16BPP_CUSTOM_LE", "PACK_16BPP_LE", "16BPP_LE", "16LE") {
		public void unpack(byte[] data, int dataOffset, int dataLength, int[] rgb, int rgbOffset, int rgbLength, int[] ct) {
			while (dataLength > 0 && rgbLength > 0) {
				int chunk = (data[dataOffset] & 0xFF); dataOffset++; dataLength--;
				if (dataLength > 0) { chunk |= ((data[dataOffset] & 0xFF) << 8); dataOffset++; dataLength--; }
				rgb[rgbOffset] = ct[chunk]; rgbOffset++; rgbLength--;
			}
		}
	},
	PACK_24BPP_RGB (0, "24BPP_RGB", "RGB") {
		public void unpack(byte[] data, int dataOffset, int dataLength, int[] rgb, int rgbOffset, int rgbLength, int[] ct) {
			int r, g, b;
			while (dataLength > 0 && rgbLength > 0) {
				if (dataLength > 0) { r = data[dataOffset] & 0xFF; dataOffset++; dataLength--; } else r = 0;
				if (dataLength > 0) { g = data[dataOffset] & 0xFF; dataOffset++; dataLength--; } else g = 0;
				if (dataLength > 0) { b = data[dataOffset] & 0xFF; dataOffset++; dataLength--; } else b = 0;
				rgb[rgbOffset] = (255 << 24) | (r << 16) | (g << 8) | (b << 0); rgbOffset++; rgbLength--;
			}
		}
	},
	PACK_24BPP_BGR (0, "24BPP_BGR", "BGR") {
		public void unpack(byte[] data, int dataOffset, int dataLength, int[] rgb, int rgbOffset, int rgbLength, int[] ct) {
			int b, g, r;
			while (dataLength > 0 && rgbLength > 0) {
				if (dataLength > 0) { b = data[dataOffset] & 0xFF; dataOffset++; dataLength--; } else b = 0;
				if (dataLength > 0) { g = data[dataOffset] & 0xFF; dataOffset++; dataLength--; } else g = 0;
				if (dataLength > 0) { r = data[dataOffset] & 0xFF; dataOffset++; dataLength--; } else r = 0;
				rgb[rgbOffset] = (255 << 24) | (r << 16) | (g << 8) | (b << 0); rgbOffset++; rgbLength--;
			}
		}
	},
	PACK_32BPP_XRGB (0, "32BPP_XRGB", "XRGB") {
		public void unpack(byte[] data, int dataOffset, int dataLength, int[] rgb, int rgbOffset, int rgbLength, int[] ct) {
			int r, g, b;
			while (dataLength > 0 && rgbLength > 0) {
				if (dataLength > 0) { dataOffset++; dataLength--; }
				if (dataLength > 0) { r = data[dataOffset] & 0xFF; dataOffset++; dataLength--; } else r = 0;
				if (dataLength > 0) { g = data[dataOffset] & 0xFF; dataOffset++; dataLength--; } else g = 0;
				if (dataLength > 0) { b = data[dataOffset] & 0xFF; dataOffset++; dataLength--; } else b = 0;
				rgb[rgbOffset] = (255 << 24) | (r << 16) | (g << 8) | (b << 0); rgbOffset++; rgbLength--;
			}
		}
	},
	PACK_32BPP_XBGR (0, "32BPP_XBGR", "XBGR") {
		public void unpack(byte[] data, int dataOffset, int dataLength, int[] rgb, int rgbOffset, int rgbLength, int[] ct) {
			int b, g, r;
			while (dataLength > 0 && rgbLength > 0) {
				if (dataLength > 0) { dataOffset++; dataLength--; }
				if (dataLength > 0) { b = data[dataOffset] & 0xFF; dataOffset++; dataLength--; } else b = 0;
				if (dataLength > 0) { g = data[dataOffset] & 0xFF; dataOffset++; dataLength--; } else g = 0;
				if (dataLength > 0) { r = data[dataOffset] & 0xFF; dataOffset++; dataLength--; } else r = 0;
				rgb[rgbOffset] = (255 << 24) | (r << 16) | (g << 8) | (b << 0); rgbOffset++; rgbLength--;
			}
		}
	},
	PACK_32BPP_RGBX (0, "32BPP_RGBX", "RGBX") {
		public void unpack(byte[] data, int dataOffset, int dataLength, int[] rgb, int rgbOffset, int rgbLength, int[] ct) {
			int r, g, b;
			while (dataLength > 0 && rgbLength > 0) {
				if (dataLength > 0) { r = data[dataOffset] & 0xFF; dataOffset++; dataLength--; } else r = 0;
				if (dataLength > 0) { g = data[dataOffset] & 0xFF; dataOffset++; dataLength--; } else g = 0;
				if (dataLength > 0) { b = data[dataOffset] & 0xFF; dataOffset++; dataLength--; } else b = 0;
				if (dataLength > 0) { dataOffset++; dataLength--; }
				rgb[rgbOffset] = (255 << 24) | (r << 16) | (g << 8) | (b << 0); rgbOffset++; rgbLength--;
			}
		}
	},
	PACK_32BPP_BGRX (0, "32BPP_BGRX", "BGRX") {
		public void unpack(byte[] data, int dataOffset, int dataLength, int[] rgb, int rgbOffset, int rgbLength, int[] ct) {
			int b, g, r;
			while (dataLength > 0 && rgbLength > 0) {
				if (dataLength > 0) { b = data[dataOffset] & 0xFF; dataOffset++; dataLength--; } else b = 0;
				if (dataLength > 0) { g = data[dataOffset] & 0xFF; dataOffset++; dataLength--; } else g = 0;
				if (dataLength > 0) { r = data[dataOffset] & 0xFF; dataOffset++; dataLength--; } else r = 0;
				if (dataLength > 0) { dataOffset++; dataLength--; }
				rgb[rgbOffset] = (255 << 24) | (r << 16) | (g << 8) | (b << 0); rgbOffset++; rgbLength--;
			}
		}
	},
	PACK_32BPP_ARGB (0, "32BPP_ARGB", "ARGB") {
		public void unpack(byte[] data, int dataOffset, int dataLength, int[] rgb, int rgbOffset, int rgbLength, int[] ct) {
			int a, r, g, b;
			while (dataLength > 0 && rgbLength > 0) {
				if (dataLength > 0) { a = data[dataOffset] & 0xFF; dataOffset++; dataLength--; } else a = 0;
				if (dataLength > 0) { r = data[dataOffset] & 0xFF; dataOffset++; dataLength--; } else r = 0;
				if (dataLength > 0) { g = data[dataOffset] & 0xFF; dataOffset++; dataLength--; } else g = 0;
				if (dataLength > 0) { b = data[dataOffset] & 0xFF; dataOffset++; dataLength--; } else b = 0;
				rgb[rgbOffset] = (a << 24) | (r << 16) | (g << 8) | (b << 0); rgbOffset++; rgbLength--;
			}
		}
	},
	PACK_32BPP_ABGR (0, "32BPP_ABGR", "ABGR") {
		public void unpack(byte[] data, int dataOffset, int dataLength, int[] rgb, int rgbOffset, int rgbLength, int[] ct) {
			int a, b, g, r;
			while (dataLength > 0 && rgbLength > 0) {
				if (dataLength > 0) { a = data[dataOffset] & 0xFF; dataOffset++; dataLength--; } else a = 0;
				if (dataLength > 0) { b = data[dataOffset] & 0xFF; dataOffset++; dataLength--; } else b = 0;
				if (dataLength > 0) { g = data[dataOffset] & 0xFF; dataOffset++; dataLength--; } else g = 0;
				if (dataLength > 0) { r = data[dataOffset] & 0xFF; dataOffset++; dataLength--; } else r = 0;
				rgb[rgbOffset] = (a << 24) | (r << 16) | (g << 8) | (b << 0); rgbOffset++; rgbLength--;
			}
		}
	},
	PACK_32BPP_RGBA (0, "32BPP_RGBA", "RGBA") {
		public void unpack(byte[] data, int dataOffset, int dataLength, int[] rgb, int rgbOffset, int rgbLength, int[] ct) {
			int r, g, b, a;
			while (dataLength > 0 && rgbLength > 0) {
				if (dataLength > 0) { r = data[dataOffset] & 0xFF; dataOffset++; dataLength--; } else r = 0;
				if (dataLength > 0) { g = data[dataOffset] & 0xFF; dataOffset++; dataLength--; } else g = 0;
				if (dataLength > 0) { b = data[dataOffset] & 0xFF; dataOffset++; dataLength--; } else b = 0;
				if (dataLength > 0) { a = data[dataOffset] & 0xFF; dataOffset++; dataLength--; } else a = 0;
				rgb[rgbOffset] = (a << 24) | (r << 16) | (g << 8) | (b << 0); rgbOffset++; rgbLength--;
			}
		}
	},
	PACK_32BPP_BGRA (0, "32BPP_BGRA", "BGRA") {
		public void unpack(byte[] data, int dataOffset, int dataLength, int[] rgb, int rgbOffset, int rgbLength, int[] ct) {
			int b, g, r, a;
			while (dataLength > 0 && rgbLength > 0) {
				if (dataLength > 0) { b = data[dataOffset] & 0xFF; dataOffset++; dataLength--; } else b = 0;
				if (dataLength > 0) { g = data[dataOffset] & 0xFF; dataOffset++; dataLength--; } else g = 0;
				if (dataLength > 0) { r = data[dataOffset] & 0xFF; dataOffset++; dataLength--; } else r = 0;
				if (dataLength > 0) { a = data[dataOffset] & 0xFF; dataOffset++; dataLength--; } else a = 0;
				rgb[rgbOffset] = (a << 24) | (r << 16) | (g << 8) | (b << 0); rgbOffset++; rgbLength--;
			}
		}
	};
	
	public final int lookupTableSize;
	private final HashSet<String> names;
	
	private PixelPacking(int lookupTableSize, String... names) {
		this.lookupTableSize = lookupTableSize;
		this.names = new HashSet<String>();
		this.names.add(name().toUpperCase().replaceAll("[^0-9A-Z]", ""));
		for (String name : names) this.names.add(name.toUpperCase().replaceAll("[^0-9A-Z]", ""));
	}
	
	public abstract void unpack(
		byte[] data, int dataOffset, int dataLength,
		int[] rgb, int rgbOffset, int rgbLength,
		int[] colorTable
	);
	
	public static PixelPacking parse(String s) {
		String name = s.toUpperCase().replaceAll("[^0-9A-Z]", "");
		for (PixelPacking packing : values()) {
			if (packing.names.contains(name)) {
				return packing;
			}
		}
		return valueOf(s);
	}
}
