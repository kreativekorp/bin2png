package com.kreative.bin2png;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;

public class ColorTables {
	public static int[] create(int length, String spec) {
		String s = spec.toLowerCase().replaceAll("[^0-9a-z]", "");
		
		if (s.matches("b(lack)?(to)?w(hite)?")) return createBlackToWhite(length);
		if (s.matches("w(hite)?(to)?b(lack)?")) return createWhiteToBlack(length);
		
		if (s.matches("w(in(dows)?)?")) {
			if (length >= 256) return extendWithFill(length, createWindowsEis(), 0);
			if (length >= 16) return extendWithFill(length, createWindows4(), 0);
			return createBlackToWhite(length);
		}
		
		if (s.matches("w(in(dows)?)?4(bit)?")) return extendWithFill(length, createWindows4(), 0);
		if (s.matches("w(in(dows)?)?8(bit)?")) return extendWithFill(length, createWindowsEis(), 0);
		
		if (s.matches("w(in(dows)?)?eis")) return extendWithFill(length, createWindowsEis(), 0);
		if (s.matches("w(in(dows)?)?(paint|bmp|p)")) return extendWithFill(length, createWindowsPaint(), 0);
		if (s.matches("w(in(dows)?)?(websafe|wsafe|web|ws|w)")) return extendWithFill(length, createWindowsWebSafe(), 0);
		
		if (s.matches("m(ac(os|intosh)?)?")) {
			if (length >= 256) return extendWithFill(length, createMacintosh8(), 0);
			if (length >= 16) return extendWithFill(length, createMacintosh4(), 0);
			return createWhiteToBlack(length);
		}
		
		if (s.matches("m(ac(os|intosh)?)?4(bit)?")) return extendWithFill(length, createMacintosh4(), 0);
		if (s.matches("m(ac(os|intosh)?)?8(bit)?")) return extendWithFill(length, createMacintosh8(), 0);
		
		return null;
	}
	
	public static Map<String,int[]> createPresets(int length) {
		Map<String,int[]> presets = new LinkedHashMap<String,int[]>();
		if (length < 1) {
			presets.put("None", new int[0]);
		}
		if (length == 1) {
			presets.put("Black", new int[]{255 << 24});
			presets.put("White", new int[]{-1});
		}
		if (length > 1) {
			presets.put("Black to White", createBlackToWhite(length));
			presets.put("White to Black", createWhiteToBlack(length));
		}
		if (length == 16) {
			presets.put("Windows", createWindows4());
			presets.put("Mac OS", createMacintosh4());
		}
		if (length == 256) {
			presets.put("Windows (Eis)", createWindowsEis());
			presets.put("Windows (Paint)", createWindowsPaint());
			presets.put("Windows (Web-Safe)", createWindowsWebSafe());
			presets.put("Mac OS", createMacintosh8());
		}
		return presets;
	}
	
	public static int[] createBlackToWhite(int length) {
		int[] clut = new int[length--];
		for (int i = 0; i <= length; i++) {
			int k = 255 * i / length;
			clut[i] = (255 << 24) | (k << 16) | (k << 8) | (k << 0);
		}
		return clut;
	}
	
	public static int[] createWhiteToBlack(int length) {
		int[] clut = new int[length--];
		for (int i = 0; i <= length; i++) {
			int k = 255 * (length - i) / length;
			clut[i] = (255 << 24) | (k << 16) | (k << 8) | (k << 0);
		}
		return clut;
	}
	
	public static int[] extendWithFill(int length, int[] colorTable, int fillColor) {
		int[] newColorTable = new int[length];
		int oldLength = (colorTable != null) ? colorTable.length : 0;
		for (int i = 0; i < length; i++) {
			newColorTable[i] = (i < oldLength) ? colorTable[i] : fillColor;
		}
		return newColorTable;
	}
	
	public static int[] readACT(InputStream in) throws IOException {
		LinkedList<Integer> colorList = new LinkedList<Integer>();
		while (in.available() > 0) {
			int a = 255       << 24;
			int r = in.read() << 16;
			int g = in.read() <<  8;
			int b = in.read() <<  0;
			colorList.add(a|r|g|b);
		}
		int i = 0;
		int n = colorList.size();
		int[] colorTable = new int[n];
		for (int c : colorList) colorTable[i++] = c;
		return colorTable;
	}
	
	public static int[] readBMP(InputStream in) throws IOException {
		DataInputStream dis = new DataInputStream(in);
		int magic = dis.readShort();
		if (magic != 0x424D) throw new IOException("Invalid value in header");
		int fileLength = Integer.reverseBytes(dis.readInt());
		if (fileLength < 54) throw new IOException("Invalid value in header");
		dis.readInt(); // reserved
		int dataOffset = Integer.reverseBytes(dis.readInt());
		if (dataOffset < 54) throw new IOException("Invalid value in header");
		int headerLength = Integer.reverseBytes(dis.readInt());
		if (headerLength < 40) throw new IOException("Invalid value in header");
		int width = Integer.reverseBytes(dis.readInt());
		if (width <= 0) throw new IOException("Invalid value in header");
		int height = Integer.reverseBytes(dis.readInt());
		if (height <= 0) throw new IOException("Invalid value in header");
		int planes = Short.reverseBytes(dis.readShort());
		if (planes < 0 || planes > 1) throw new IOException("Invalid value in header");
		int bpp = Short.reverseBytes(dis.readShort());
		if (bpp < 1 || bpp > 32) throw new IOException("Invalid value in header");
		dis.readInt(); // compression
		int dataLength = Integer.reverseBytes(dis.readInt());
		if (dataLength < 0) throw new IOException("Invalid value in header");
		dis.readInt(); // ppm-x
		dis.readInt(); // ppm-y
		int colorCount = Integer.reverseBytes(dis.readInt());
		if (colorCount < 0) throw new IOException("Invalid value in header");
		if (colorCount == 0 && bpp <= 8) colorCount = (1 << bpp);
		dis.readInt(); // important colors
		dis.skipBytes(headerLength - 40);
		int[] colorTable = new int[colorCount];
		for (int i = 0; i < colorCount; i++) colorTable[i] = Integer.reverseBytes(dis.readInt() | 0xFF);
		return colorTable;
	}
	
	public static void writeACT(OutputStream out, int[] colorTable) throws IOException {
		for (int i = 0; i < colorTable.length; i++) {
			out.write(colorTable[i] >> 16);
			out.write(colorTable[i] >>  8);
			out.write(colorTable[i] >>  0);
		}
	}
	
	public static void writeBMP(OutputStream out, int[] colorTable) throws IOException {
		DataOutputStream dos = new DataOutputStream(out);
		int count = colorTable.length;
		int ctLength = count * 4;
		int rowLength = ((count + 3) / 4) * 4;
		int dataLength = rowLength;
		int dataOffset = 54 + ctLength;
		int fileLength = dataOffset + dataLength;
		dos.writeShort(0x424D);                          // magic
		dos.writeInt(Integer.reverseBytes(fileLength));  // file length
		dos.writeInt(0);                                 // reserved
		dos.writeInt(Integer.reverseBytes(dataOffset));  // data offset
		dos.writeInt(Integer.reverseBytes(40));          // header length
		dos.writeInt(Integer.reverseBytes(count));       // width
		dos.writeInt(Integer.reverseBytes(1));           // height
		dos.writeShort(Short.reverseBytes((short)1));    // planes
		dos.writeShort(Short.reverseBytes((short)8));    // bpp
		dos.writeInt(0);                                 // compression
		dos.writeInt(Integer.reverseBytes(dataLength));  // data length
		dos.writeInt(0);                                 // ppm-x
		dos.writeInt(0);                                 // ppm-y
		dos.writeInt(Integer.reverseBytes(count));       // color count
		dos.writeInt(0);                                 // important colors
		for (int c : colorTable) dos.writeInt(Integer.reverseBytes(c & 0xFFFFFF));
		for (int i = 0; i < count; i++) dos.writeByte(i);
		for (int i = count; i < rowLength; i++) dos.writeByte(0);
		dos.flush();
	}
	
	public static int[] createWindows4() {
		return new int[] {
			0xFF000000, 0xFF800000, 0xFF008000, 0xFF808000, 0xFF000080, 0xFF800080, 0xFF008080, 0xFFC0C0C0,
			0xFF808080, 0xFFFF0000, 0xFF00FF00, 0xFFFFFF00, 0xFF0000FF, 0xFFFF00FF, 0xFF00FFFF, 0xFFFFFFFF
		};
	}
	
	public static int[] createMacintosh4() {
		return new int[] {
			0xFFFFFFFF, 0xFFFCF305, 0xFFFF6503, 0xFFDD0907, 0xFFF30885, 0xFF4700A5, 0xFF0000D4, 0xFF02ABEB,
			0xFF1FB814, 0xFF006512, 0xFF562D05, 0xFF91713A, 0xFFC0C0C0, 0xFF808080, 0xFF404040, 0xFF000000
		};
	}
	
	public static int[] createWindowsEis() {
		return new int[] {
			0xFF000000, 0xFF800000, 0xFF008000, 0xFF808000, 0xFF000080, 0xFF800080, 0xFF008080, 0xFFC0C0C0,
			0xFFC0DCC0, 0xFFA6CAF0, 0xFF2A3FAA, 0xFF2A3FFF, 0xFF2A5F00, 0xFF2A5F55, 0xFF2A5FAA, 0xFF2A5FFF,
			0xFF2A7F00, 0xFF2A7F55, 0xFF2A7FAA, 0xFF2A7FFF, 0xFF2A9F00, 0xFF2A9F55, 0xFF2A9FAA, 0xFF2A9FFF,
			0xFF2ABF00, 0xFF2ABF55, 0xFF2ABFAA, 0xFF2ABFFF, 0xFF2ADF00, 0xFF2ADF55, 0xFF2ADFAA, 0xFF2ADFFF,
			0xFF2AFF00, 0xFF2AFF55, 0xFF2AFFAA, 0xFF2AFFFF, 0xFF550000, 0xFF550055, 0xFF5500AA, 0xFF5500FF,
			0xFF551F00, 0xFF551F55, 0xFF551FAA, 0xFF551FFF, 0xFF553F00, 0xFF553F55, 0xFF553FAA, 0xFF553FFF,
			0xFF555F00, 0xFF555F55, 0xFF555FAA, 0xFF555FFF, 0xFF557F00, 0xFF557F55, 0xFF557FAA, 0xFF557FFF,
			0xFF559F00, 0xFF559F55, 0xFF559FAA, 0xFF559FFF, 0xFF55BF00, 0xFF55BF55, 0xFF55BFAA, 0xFF55BFFF,
			0xFF55DF00, 0xFF55DF55, 0xFF55DFAA, 0xFF55DFFF, 0xFF55FF00, 0xFF55FF55, 0xFF55FFAA, 0xFF55FFFF,
			0xFF7F0000, 0xFF7F0055, 0xFF7F00AA, 0xFF7F00FF, 0xFF7F1F00, 0xFF7F1F55, 0xFF7F1FAA, 0xFF7F1FFF,
			0xFF7F3F00, 0xFF7F3F55, 0xFF7F3FAA, 0xFF7F3FFF, 0xFF7F5F00, 0xFF7F5F55, 0xFF7F5FAA, 0xFF7F5FFF,
			0xFF7F7F00, 0xFF7F7F55, 0xFF7F7FAA, 0xFF7F7FFF, 0xFF7F9F00, 0xFF7F9F55, 0xFF7F9FAA, 0xFF7F9FFF,
			0xFF7FBF00, 0xFF7FBF55, 0xFF7FBFAA, 0xFF7FBFFF, 0xFF7FDF00, 0xFF7FDF55, 0xFF7FDFAA, 0xFF7FDFFF,
			0xFF7FFF00, 0xFF7FFF55, 0xFF7FFFAA, 0xFF7FFFFF, 0xFFAA0000, 0xFFAA0055, 0xFFAA00AA, 0xFFAA00FF,
			0xFFAA1F00, 0xFFAA1F55, 0xFFAA1FAA, 0xFFAA1FFF, 0xFFAA3F00, 0xFFAA3F55, 0xFFAA3FAA, 0xFFAA3FFF,
			0xFFAA5F00, 0xFFAA5F55, 0xFFAA5FAA, 0xFFAA5FFF, 0xFFAA7F00, 0xFFAA7F55, 0xFFAA7FAA, 0xFFAA7FFF,
			0xFFAA9F00, 0xFFAA9F55, 0xFFAA9FAA, 0xFFAA9FFF, 0xFFAABF00, 0xFFAABF55, 0xFFAABFAA, 0xFFAABFFF,
			0xFFAADF00, 0xFFAADF55, 0xFFAADFAA, 0xFFAADFFF, 0xFFAAFF00, 0xFFAAFF55, 0xFFAAFFAA, 0xFFAAFFFF,
			0xFFD40000, 0xFFD40055, 0xFFD400AA, 0xFFD400FF, 0xFFD41F00, 0xFFD41F55, 0xFFD41FAA, 0xFFD41FFF,
			0xFFD43F00, 0xFFD43F55, 0xFFD43FAA, 0xFFD43FFF, 0xFFD45F00, 0xFFD45F55, 0xFFD45FAA, 0xFFD45FFF,
			0xFFD47F00, 0xFFD47F55, 0xFFD47FAA, 0xFFD47FFF, 0xFFD49F00, 0xFFD49F55, 0xFFD49FAA, 0xFFD49FFF,
			0xFFD4BF00, 0xFFD4BF55, 0xFFD4BFAA, 0xFFD4BFFF, 0xFFD4DF00, 0xFFD4DF55, 0xFFD4DFAA, 0xFFD4DFFF,
			0xFFD4FF00, 0xFFD4FF55, 0xFFD4FFAA, 0xFFD4FFFF, 0xFFFF0055, 0xFFFF00AA, 0xFFFF1F00, 0xFFFF1F55,
			0xFFFF1FAA, 0xFFFF1FFF, 0xFFFF3F00, 0xFFFF3F55, 0xFFFF3FAA, 0xFFFF3FFF, 0xFFFF5F00, 0xFFFF5F55,
			0xFFFF5FAA, 0xFFFF5FFF, 0xFFFF7F00, 0xFFFF7F55, 0xFFFF7FAA, 0xFFFF7FFF, 0xFFFF9F00, 0xFFFF9F55,
			0xFFFF9FAA, 0xFFFF9FFF, 0xFFFFBF00, 0xFFFFBF55, 0xFFFFBFAA, 0xFFFFBFFF, 0xFFFFDF00, 0xFFFFDF55,
			0xFFFFDFAA, 0xFFFFDFFF, 0xFFFFFF55, 0xFFFFFFAA, 0xFFCCCCFF, 0xFFFFCCFF, 0xFF33FFFF, 0xFF66FFFF,
			0xFF99FFFF, 0xFFCCFFFF, 0xFF007F00, 0xFF007F55, 0xFF007FAA, 0xFF007FFF, 0xFF009F00, 0xFF009F55,
			0xFF009FAA, 0xFF009FFF, 0xFF00BF00, 0xFF00BF55, 0xFF00BFAA, 0xFF00BFFF, 0xFF00DF00, 0xFF00DF55,
			0xFF00DFAA, 0xFF00DFFF, 0xFF00FF55, 0xFF00FFAA, 0xFF2A0000, 0xFF2A0055, 0xFF2A00AA, 0xFF2A00FF,
			0xFF2A1F00, 0xFF2A1F55, 0xFF2A1FAA, 0xFF2A1FFF, 0xFF2A3F00, 0xFF2A3F55, 0xFFFFFBF0, 0xFFA0A0A4,
			0xFF808080, 0xFFFF0000, 0xFF00FF00, 0xFFFFFF00, 0xFF0000FF, 0xFFFF00FF, 0xFF00FFFF, 0xFFFFFFFF
		};
	}
	
	public static int[] createWindowsPaint() {
		return new int[] {
			0xFF000000, 0xFF800000, 0xFF008000, 0xFF808000, 0xFF000080, 0xFF800080, 0xFF008080, 0xFFC0C0C0,
			0xFFC0DCC0, 0xFFA6CAF0, 0xFF402000, 0xFF602000, 0xFF802000, 0xFFA02000, 0xFFC02000, 0xFFE02000,
			0xFF004000, 0xFF204000, 0xFF404000, 0xFF604000, 0xFF804000, 0xFFA04000, 0xFFC04000, 0xFFE04000,
			0xFF006000, 0xFF206000, 0xFF406000, 0xFF606000, 0xFF806000, 0xFFA06000, 0xFFC06000, 0xFFE06000,
			0xFF008000, 0xFF208000, 0xFF408000, 0xFF608000, 0xFF808000, 0xFFA08000, 0xFFC08000, 0xFFE08000,
			0xFF00A000, 0xFF20A000, 0xFF40A000, 0xFF60A000, 0xFF80A000, 0xFFA0A000, 0xFFC0A000, 0xFFE0A000,
			0xFF00C000, 0xFF20C000, 0xFF40C000, 0xFF60C000, 0xFF80C000, 0xFFA0C000, 0xFFC0C000, 0xFFE0C000,
			0xFF00E000, 0xFF20E000, 0xFF40E000, 0xFF60E000, 0xFF80E000, 0xFFA0E000, 0xFFC0E000, 0xFFE0E000,
			0xFF000040, 0xFF200040, 0xFF400040, 0xFF600040, 0xFF800040, 0xFFA00040, 0xFFC00040, 0xFFE00040,
			0xFF002040, 0xFF202040, 0xFF402040, 0xFF602040, 0xFF802040, 0xFFA02040, 0xFFC02040, 0xFFE02040,
			0xFF004040, 0xFF204040, 0xFF404040, 0xFF604040, 0xFF804040, 0xFFA04040, 0xFFC04040, 0xFFE04040,
			0xFF006040, 0xFF206040, 0xFF406040, 0xFF606040, 0xFF806040, 0xFFA06040, 0xFFC06040, 0xFFE06040,
			0xFF008040, 0xFF208040, 0xFF408040, 0xFF608040, 0xFF808040, 0xFFA08040, 0xFFC08040, 0xFFE08040,
			0xFF00A040, 0xFF20A040, 0xFF40A040, 0xFF60A040, 0xFF80A040, 0xFFA0A040, 0xFFC0A040, 0xFFE0A040,
			0xFF00C040, 0xFF20C040, 0xFF40C040, 0xFF60C040, 0xFF80C040, 0xFFA0C040, 0xFFC0C040, 0xFFE0C040,
			0xFF00E040, 0xFF20E040, 0xFF40E040, 0xFF60E040, 0xFF80E040, 0xFFA0E040, 0xFFC0E040, 0xFFE0E040,
			0xFF000080, 0xFF200080, 0xFF400080, 0xFF600080, 0xFF800080, 0xFFA00080, 0xFFC00080, 0xFFE00080,
			0xFF002080, 0xFF202080, 0xFF402080, 0xFF602080, 0xFF802080, 0xFFA02080, 0xFFC02080, 0xFFE02080,
			0xFF004080, 0xFF204080, 0xFF404080, 0xFF604080, 0xFF804080, 0xFFA04080, 0xFFC04080, 0xFFE04080,
			0xFF006080, 0xFF206080, 0xFF406080, 0xFF606080, 0xFF806080, 0xFFA06080, 0xFFC06080, 0xFFE06080,
			0xFF008080, 0xFF208080, 0xFF408080, 0xFF608080, 0xFF808080, 0xFFA08080, 0xFFC08080, 0xFFE08080,
			0xFF00A080, 0xFF20A080, 0xFF40A080, 0xFF60A080, 0xFF80A080, 0xFFA0A080, 0xFFC0A080, 0xFFE0A080,
			0xFF00C080, 0xFF20C080, 0xFF40C080, 0xFF60C080, 0xFF80C080, 0xFFA0C080, 0xFFC0C080, 0xFFE0C080,
			0xFF00E080, 0xFF20E080, 0xFF40E080, 0xFF60E080, 0xFF80E080, 0xFFA0E080, 0xFFC0E080, 0xFFE0E080,
			0xFF0000C0, 0xFF2000C0, 0xFF4000C0, 0xFF6000C0, 0xFF8000C0, 0xFFA000C0, 0xFFC000C0, 0xFFE000C0,
			0xFF0020C0, 0xFF2020C0, 0xFF4020C0, 0xFF6020C0, 0xFF8020C0, 0xFFA020C0, 0xFFC020C0, 0xFFE020C0,
			0xFF0040C0, 0xFF2040C0, 0xFF4040C0, 0xFF6040C0, 0xFF8040C0, 0xFFA040C0, 0xFFC040C0, 0xFFE040C0,
			0xFF0060C0, 0xFF2060C0, 0xFF4060C0, 0xFF6060C0, 0xFF8060C0, 0xFFA060C0, 0xFFC060C0, 0xFFE060C0,
			0xFF0080C0, 0xFF2080C0, 0xFF4080C0, 0xFF6080C0, 0xFF8080C0, 0xFFA080C0, 0xFFC080C0, 0xFFE080C0,
			0xFF00A0C0, 0xFF20A0C0, 0xFF40A0C0, 0xFF60A0C0, 0xFF80A0C0, 0xFFA0A0C0, 0xFFC0A0C0, 0xFFE0A0C0,
			0xFF00C0C0, 0xFF20C0C0, 0xFF40C0C0, 0xFF60C0C0, 0xFF80C0C0, 0xFFA0C0C0, 0xFFFFFBF0, 0xFFA0A0A4,
			0xFF808080, 0xFFFF0000, 0xFF00FF00, 0xFFFFFF00, 0xFF0000FF, 0xFFFF00FF, 0xFF00FFFF, 0xFFFFFFFF
		};
	}
	
	public static int[] createWindowsWebSafe() {
		return new int[] {
			0xFF000000, 0xFF800000, 0xFF008000, 0xFF808000, 0xFF000080, 0xFF800080, 0xFF008080, 0xFFC0C0C0,
			0xFFC0DCC0, 0xFFA6CAF0, 0xFF330000, 0xFF660000, 0xFF990000, 0xFFCC0000, 0xFF003300, 0xFF333300,
			0xFF663300, 0xFF993300, 0xFFCC3300, 0xFFFF3300, 0xFF006600, 0xFF336600, 0xFF666600, 0xFF996600,
			0xFFCC6600, 0xFFFF6600, 0xFF009900, 0xFF339900, 0xFF669900, 0xFF999900, 0xFFCC9900, 0xFFFF9900,
			0xFF00CC00, 0xFF33CC00, 0xFF66CC00, 0xFF99CC00, 0xFFCCCC00, 0xFFFFCC00, 0xFF33FF00, 0xFF66FF00,
			0xFF99FF00, 0xFFCCFF00, 0xFF000033, 0xFF330033, 0xFF660033, 0xFF990033, 0xFFCC0033, 0xFFFF0033,
			0xFF003333, 0xFF333333, 0xFF663333, 0xFF993333, 0xFFCC3333, 0xFFFF3333, 0xFF006633, 0xFF336633,
			0xFF666633, 0xFF996633, 0xFFCC6633, 0xFFFF6633, 0xFF009933, 0xFF339933, 0xFF669933, 0xFF999933,
			0xFFCC9933, 0xFFFF9933, 0xFF00CC33, 0xFF33CC33, 0xFF66CC33, 0xFF99CC33, 0xFFCCCC33, 0xFFFFCC33,
			0xFF00FF33, 0xFF33FF33, 0xFF66FF33, 0xFF99FF33, 0xFFCCFF33, 0xFFFFFF33, 0xFF000066, 0xFF330066,
			0xFF660066, 0xFF990066, 0xFFCC0066, 0xFFFF0066, 0xFF003366, 0xFF333366, 0xFF663366, 0xFF993366,
			0xFFCC3366, 0xFFFF3366, 0xFF006666, 0xFF336666, 0xFF666666, 0xFF996666, 0xFFCC6666, 0xFFFF6666,
			0xFF009966, 0xFF339966, 0xFF669966, 0xFF999966, 0xFFCC9966, 0xFFFF9966, 0xFF00CC66, 0xFF33CC66,
			0xFF66CC66, 0xFF99CC66, 0xFFCCCC66, 0xFFFFCC66, 0xFF00FF66, 0xFF33FF66, 0xFF66FF66, 0xFF99FF66,
			0xFFCCFF66, 0xFFFFFF66, 0xFF000099, 0xFF330099, 0xFF660099, 0xFF990099, 0xFFCC0099, 0xFFFF0099,
			0xFF003399, 0xFF333399, 0xFF663399, 0xFF993399, 0xFFCC3399, 0xFFFF3399, 0xFF006699, 0xFF336699,
			0xFF666699, 0xFF996699, 0xFFCC6699, 0xFFFF6699, 0xFF009999, 0xFF339999, 0xFF669999, 0xFF999999,
			0xFFCC9999, 0xFFFF9999, 0xFF00CC99, 0xFF33CC99, 0xFF66CC99, 0xFF99CC99, 0xFFCCCC99, 0xFFFFCC99,
			0xFF00FF99, 0xFF33FF99, 0xFF66FF99, 0xFF99FF99, 0xFFCCFF99, 0xFFFFFF99, 0xFF0000CC, 0xFF3300CC,
			0xFF6600CC, 0xFF9900CC, 0xFFCC00CC, 0xFFFF00CC, 0xFF0033CC, 0xFF3333CC, 0xFF6633CC, 0xFF9933CC,
			0xFFCC33CC, 0xFFFF33CC, 0xFF0066CC, 0xFF3366CC, 0xFF6666CC, 0xFF9966CC, 0xFFCC66CC, 0xFFFF66CC,
			0xFF0099CC, 0xFF3399CC, 0xFF6699CC, 0xFF9999CC, 0xFFCC99CC, 0xFFFF99CC, 0xFF00CCCC, 0xFF33CCCC,
			0xFF66CCCC, 0xFF99CCCC, 0xFFCCCCCC, 0xFFFFCCCC, 0xFF00FFCC, 0xFF33FFCC, 0xFF66FFCC, 0xFF99FFCC,
			0xFFCCFFCC, 0xFFFFFFCC, 0xFF3300FF, 0xFF6600FF, 0xFF9900FF, 0xFFCC00FF, 0xFF0033FF, 0xFF3333FF,
			0xFF6633FF, 0xFF9933FF, 0xFFCC33FF, 0xFFFF33FF, 0xFF0066FF, 0xFF3366FF, 0xFF6666FF, 0xFF9966FF,
			0xFFCC66FF, 0xFFFF66FF, 0xFF0099FF, 0xFF3399FF, 0xFF6699FF, 0xFF9999FF, 0xFFCC99FF, 0xFFFF99FF,
			0xFF00CCFF, 0xFF33CCFF, 0xFF66CCFF, 0xFF99CCFF, 0xFFCCCCFF, 0xFFFFCCFF, 0xFF33FFFF, 0xFF66FFFF,
			0xFF99FFFF, 0xFFCCFFFF, 0xFF111111, 0xFF222222, 0xFF444444, 0xFF555555, 0xFF777777, 0xFF888888,
			0xFFAAAAAA, 0xFFBBBBBB, 0xFFDDDDDD, 0xFFEEEEEE, 0xFF804000, 0xFFFF8000, 0xFF80FF00, 0xFF004040,
			0xFF404040, 0xFF808040, 0xFFFF8040, 0xFFFF0080, 0xFF004080, 0xFFFF8080, 0xFF00FF80, 0xFF80FF80,
			0xFFFFFF80, 0xFF8000FF, 0xFF0080FF, 0xFF8080FF, 0xFFFF80FF, 0xFF80FFFF, 0xFFFFFBF0, 0xFFA0A0A4,
			0xFF808080, 0xFFFF0000, 0xFF00FF00, 0xFFFFFF00, 0xFF0000FF, 0xFFFF00FF, 0xFF00FFFF, 0xFFFFFFFF
		};
	}
	
	public static int[] createMacintosh8() {
		return new int[] {
			0xFFFFFFFF, 0xFFFFFFCC, 0xFFFFFF99, 0xFFFFFF66, 0xFFFFFF33, 0xFFFFFF00, 0xFFFFCCFF, 0xFFFFCCCC,
			0xFFFFCC99, 0xFFFFCC66, 0xFFFFCC33, 0xFFFFCC00, 0xFFFF99FF, 0xFFFF99CC, 0xFFFF9999, 0xFFFF9966,
			0xFFFF9933, 0xFFFF9900, 0xFFFF66FF, 0xFFFF66CC, 0xFFFF6699, 0xFFFF6666, 0xFFFF6633, 0xFFFF6600,
			0xFFFF33FF, 0xFFFF33CC, 0xFFFF3399, 0xFFFF3366, 0xFFFF3333, 0xFFFF3300, 0xFFFF00FF, 0xFFFF00CC,
			0xFFFF0099, 0xFFFF0066, 0xFFFF0033, 0xFFFF0000, 0xFFCCFFFF, 0xFFCCFFCC, 0xFFCCFF99, 0xFFCCFF66,
			0xFFCCFF33, 0xFFCCFF00, 0xFFCCCCFF, 0xFFCCCCCC, 0xFFCCCC99, 0xFFCCCC66, 0xFFCCCC33, 0xFFCCCC00,
			0xFFCC99FF, 0xFFCC99CC, 0xFFCC9999, 0xFFCC9966, 0xFFCC9933, 0xFFCC9900, 0xFFCC66FF, 0xFFCC66CC,
			0xFFCC6699, 0xFFCC6666, 0xFFCC6633, 0xFFCC6600, 0xFFCC33FF, 0xFFCC33CC, 0xFFCC3399, 0xFFCC3366,
			0xFFCC3333, 0xFFCC3300, 0xFFCC00FF, 0xFFCC00CC, 0xFFCC0099, 0xFFCC0066, 0xFFCC0033, 0xFFCC0000,
			0xFF99FFFF, 0xFF99FFCC, 0xFF99FF99, 0xFF99FF66, 0xFF99FF33, 0xFF99FF00, 0xFF99CCFF, 0xFF99CCCC,
			0xFF99CC99, 0xFF99CC66, 0xFF99CC33, 0xFF99CC00, 0xFF9999FF, 0xFF9999CC, 0xFF999999, 0xFF999966,
			0xFF999933, 0xFF999900, 0xFF9966FF, 0xFF9966CC, 0xFF996699, 0xFF996666, 0xFF996633, 0xFF996600,
			0xFF9933FF, 0xFF9933CC, 0xFF993399, 0xFF993366, 0xFF993333, 0xFF993300, 0xFF9900FF, 0xFF9900CC,
			0xFF990099, 0xFF990066, 0xFF990033, 0xFF990000, 0xFF66FFFF, 0xFF66FFCC, 0xFF66FF99, 0xFF66FF66,
			0xFF66FF33, 0xFF66FF00, 0xFF66CCFF, 0xFF66CCCC, 0xFF66CC99, 0xFF66CC66, 0xFF66CC33, 0xFF66CC00,
			0xFF6699FF, 0xFF6699CC, 0xFF669999, 0xFF669966, 0xFF669933, 0xFF669900, 0xFF6666FF, 0xFF6666CC,
			0xFF666699, 0xFF666666, 0xFF666633, 0xFF666600, 0xFF6633FF, 0xFF6633CC, 0xFF663399, 0xFF663366,
			0xFF663333, 0xFF663300, 0xFF6600FF, 0xFF6600CC, 0xFF660099, 0xFF660066, 0xFF660033, 0xFF660000,
			0xFF33FFFF, 0xFF33FFCC, 0xFF33FF99, 0xFF33FF66, 0xFF33FF33, 0xFF33FF00, 0xFF33CCFF, 0xFF33CCCC,
			0xFF33CC99, 0xFF33CC66, 0xFF33CC33, 0xFF33CC00, 0xFF3399FF, 0xFF3399CC, 0xFF339999, 0xFF339966,
			0xFF339933, 0xFF339900, 0xFF3366FF, 0xFF3366CC, 0xFF336699, 0xFF336666, 0xFF336633, 0xFF336600,
			0xFF3333FF, 0xFF3333CC, 0xFF333399, 0xFF333366, 0xFF333333, 0xFF333300, 0xFF3300FF, 0xFF3300CC,
			0xFF330099, 0xFF330066, 0xFF330033, 0xFF330000, 0xFF00FFFF, 0xFF00FFCC, 0xFF00FF99, 0xFF00FF66,
			0xFF00FF33, 0xFF00FF00, 0xFF00CCFF, 0xFF00CCCC, 0xFF00CC99, 0xFF00CC66, 0xFF00CC33, 0xFF00CC00,
			0xFF0099FF, 0xFF0099CC, 0xFF009999, 0xFF009966, 0xFF009933, 0xFF009900, 0xFF0066FF, 0xFF0066CC,
			0xFF006699, 0xFF006666, 0xFF006633, 0xFF006600, 0xFF0033FF, 0xFF0033CC, 0xFF003399, 0xFF003366,
			0xFF003333, 0xFF003300, 0xFF0000FF, 0xFF0000CC, 0xFF000099, 0xFF000066, 0xFF000033, 0xFFEE0000,
			0xFFDD0000, 0xFFBB0000, 0xFFAA0000, 0xFF880000, 0xFF770000, 0xFF550000, 0xFF440000, 0xFF220000,
			0xFF110000, 0xFF00EE00, 0xFF00DD00, 0xFF00BB00, 0xFF00AA00, 0xFF008800, 0xFF007700, 0xFF005500,
			0xFF004400, 0xFF002200, 0xFF001100, 0xFF0000EE, 0xFF0000DD, 0xFF0000BB, 0xFF0000AA, 0xFF000088,
			0xFF000077, 0xFF000055, 0xFF000044, 0xFF000022, 0xFF000011, 0xFFEEEEEE, 0xFFDDDDDD, 0xFFBBBBBB,
			0xFFAAAAAA, 0xFF888888, 0xFF777777, 0xFF555555, 0xFF444444, 0xFF222222, 0xFF111111, 0xFF000000
		};
	}
}
