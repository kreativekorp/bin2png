package com.kreative.bin2png;

import java.awt.FileDialog;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import javax.imageio.ImageIO;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class Main {
	private static enum Source { STANDARD_IO, FILE_DIALOG, PATH };
	private static enum ArgumentType { NONE, WIDTH, HEIGHT, INPUT, OFFSET, SCANSIZE, PACKING, CTSPEC, FORMAT, OUTPUT };
	
	public static void main(String[] args) {
		// Set default options.
		int width = 32;
		int height = 32;
		Source inputSource = Source.STANDARD_IO;
		File inputPath = null;
		int offset = 0;
		int scansize = 32;
		PixelPacking packing = PixelPacking.PACK_1BPP_MSB_TO_LSB;
		String colorTableSpec = "Black to White";
		boolean useDialog = false;
		String outputFormat = "png";
		Source outputSource = Source.STANDARD_IO;
		File outputPath = null;
		
		// Parse arguments.
		ArgumentType argType = ArgumentType.NONE;
		for (String arg : args) {
			switch (argType) {
				case WIDTH:
					try { width = parseInt(arg); }
					catch (NumberFormatException nfe) {}
					break;
				case HEIGHT:
					try { height = parseInt(arg); }
					catch (NumberFormatException nfe) {}
					break;
				case INPUT:
					inputSource = Source.PATH;
					inputPath = new File(arg);
					break;
				case OFFSET:
					try { offset = parseInt(arg); }
					catch (NumberFormatException nfe) {}
					break;
				case SCANSIZE:
					try { scansize = parseInt(arg); }
					catch (NumberFormatException nfe) {}
					break;
				case PACKING:
					try { packing = PixelPacking.parse(arg); }
					catch (IllegalArgumentException nfe) {}
					break;
				case CTSPEC:
					colorTableSpec = arg;
					break;
				case FORMAT:
					outputFormat = arg;
					break;
				case OUTPUT:
					outputSource = Source.PATH;
					outputPath = new File(arg);
					break;
				default:
					if (arg.startsWith("-w=")) {
						try { width = parseInt(arg.substring(3)); }
						catch (NumberFormatException nfe) {}
						break;
					}
					if (arg.equals("-w")) {
						argType = ArgumentType.WIDTH;
						continue;
					}
					if (arg.startsWith("-h=")) {
						try { height = parseInt(arg.substring(3)); }
						catch (NumberFormatException nfe) {}
						break;
					}
					if (arg.equals("-h")) {
						argType = ArgumentType.HEIGHT;
						continue;
					}
					if (arg.startsWith("-i=")) {
						inputSource = Source.PATH;
						inputPath = new File(arg.substring(3));
						break;
					}
					if (arg.equals("-L")) {
						inputSource = Source.FILE_DIALOG;
						inputPath = null;
						break;
					}
					if (arg.equals("-I")) {
						inputSource = Source.STANDARD_IO;
						inputPath = null;
						break;
					}
					if (arg.equals("-i")) {
						argType = ArgumentType.INPUT;
						continue;
					}
					if (arg.startsWith("-s=")) {
						try { offset = parseInt(arg.substring(3)); }
						catch (NumberFormatException nfe) {}
						break;
					}
					if (arg.equals("-s")) {
						argType = ArgumentType.OFFSET;
						continue;
					}
					if (arg.startsWith("-l=")) {
						try { scansize = parseInt(arg.substring(3)); }
						catch (NumberFormatException nfe) {}
						break;
					}
					if (arg.equals("-l")) {
						argType = ArgumentType.SCANSIZE;
						continue;
					}
					if (arg.startsWith("-b=")) {
						try { packing = PixelPacking.parse(arg.substring(3)); }
						catch (IllegalArgumentException nfe) {}
						break;
					}
					if (arg.equals("-b")) {
						argType = ArgumentType.PACKING;
						continue;
					}
					if (arg.startsWith("-c=")) {
						colorTableSpec = arg.substring(3);
						break;
					}
					if (arg.equals("-c")) {
						argType = ArgumentType.CTSPEC;
						continue;
					}
					if (arg.equals("-D")) {
						useDialog = true;
						break;
					}
					if (arg.equals("-d")) {
						useDialog = false;
						break;
					}
					if (arg.startsWith("-f=")) {
						outputFormat = arg.substring(3);
						break;
					}
					if (arg.equals("-f")) {
						argType = ArgumentType.FORMAT;
						continue;
					}
					if (arg.startsWith("-o=")) {
						outputSource = Source.PATH;
						outputPath = new File(arg.substring(3));
						break;
					}
					if (arg.equals("-S")) {
						outputSource = Source.FILE_DIALOG;
						outputPath = null;
						break;
					}
					if (arg.equals("-O")) {
						outputSource = Source.STANDARD_IO;
						outputPath = null;
						break;
					}
					if (arg.equals("-o")) {
						argType = ArgumentType.OUTPUT;
						continue;
					}
					printHelp();
					return;
			}
			argType = ArgumentType.NONE;
		}
		
		// Fix invalid arguments.
		if (width < 1) width = 32;
		if (height < 1) height = 32;
		if (offset < 0) offset = 0;
		
		// Create color table.
		int[] colorTable;
		String colorTableSpecLC = colorTableSpec.toLowerCase();
		if (colorTableSpecLC.endsWith(".act")) {
			try {
				FileInputStream in = new FileInputStream(colorTableSpec);
				colorTable = ColorTables.extendWithFill(packing.lookupTableSize, ColorTables.readACT(in), 0);
				in.close();
			} catch (IOException e) {
				System.err.println(e);
				return;
			}
		} else if (colorTableSpecLC.endsWith(".bmp")) {
			try {
				FileInputStream in = new FileInputStream(colorTableSpec);
				colorTable = ColorTables.extendWithFill(packing.lookupTableSize, ColorTables.readBMP(in), 0);
				in.close();
			} catch (IOException e) {
				System.err.println(e);
				return;
			}
		} else {
			colorTable = ColorTables.create(packing.lookupTableSize, colorTableSpec);
			if (colorTable == null) colorTable = ColorTables.createBlackToWhite(packing.lookupTableSize);
		}
		
		// Initialize GUI.
		if (inputSource == Source.FILE_DIALOG || useDialog || outputSource == Source.FILE_DIALOG) {
			try { System.setProperty("apple.awt.UIElement", "true"); } catch (Exception e) {}
			try { System.setProperty("com.apple.mrj.application.apple.menu.about.name", "BIN2PNG"); } catch (Exception e) {}
			try { System.setProperty("apple.laf.useScreenMenuBar", "true"); } catch (Exception e) {}
			try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception e) {}
			
			try {
				Method getModule = Class.class.getMethod("getModule");
				Object javaDesktop = getModule.invoke(Toolkit.getDefaultToolkit().getClass());
				Object allUnnamed = getModule.invoke(Main.class);
				Class<?> module = Class.forName("java.lang.Module");
				Method addOpens = module.getMethod("addOpens", String.class, module);
				addOpens.invoke(javaDesktop, "sun.awt.X11", allUnnamed);
			} catch (Exception e) {}
			
			try {
				Toolkit tk = Toolkit.getDefaultToolkit();
				Field aacn = tk.getClass().getDeclaredField("awtAppClassName");
				aacn.setAccessible(true);
				aacn.set(tk, "BIN2PNG");
			} catch (Exception e) {}
		}
		
		// Read binary data from input.
		ByteArrayOutputStream data = new ByteArrayOutputStream();
		byte[] buf = new byte[65536]; int read;
		try {
			switch (inputSource) {
				case STANDARD_IO:
					while ((read = System.in.read(buf)) > 0) data.write(buf, 0, read);
					break;
				case FILE_DIALOG:
					FileDialogRunnable r = new FileDialogRunnable((Frame)null, "Open Binary File", FileDialog.LOAD);
					try { SwingUtilities.invokeAndWait(r); }
					catch (InvocationTargetException e) { e.printStackTrace(); }
					catch (InterruptedException e) { e.printStackTrace(); }
					String parent = r.getDirectory(), name = r.getFile();
					if (parent == null || name == null) return;
					inputPath = new File(parent, name);
					// fallthrough;
				case PATH:
					FileInputStream in = new FileInputStream(inputPath);
					while ((read = in.read(buf)) > 0) data.write(buf, 0, read);
					in.close();
					break;
			}
		} catch (IOException e) {
			System.err.println(e);
			return;
		}
		
		// Show options dialog.
		// Convert binary data to image.
		BufferedImage image;
		if (useDialog) {
			BytesToImageRunnable r = new BytesToImageRunnable(
				(Frame)null, "Options",
				width, height, data.toByteArray(),
				offset, scansize, packing, colorTable
			);
			try { SwingUtilities.invokeAndWait(r); }
			catch (InvocationTargetException e) { e.printStackTrace(); }
			catch (InterruptedException e) { e.printStackTrace(); }
			image = r.getImage();
			if (image == null) return;
		} else {
			image = BytesToImage.bytesToImage(
				width, height, data.toByteArray(),
				offset, scansize, packing, colorTable
			);
		}
		
		// Write image to output.
		try {
			switch (outputSource) {
				case STANDARD_IO:
					ImageIO.write(image, outputFormat, System.out);
					break;
				case FILE_DIALOG:
					FileDialogRunnable r = new FileDialogRunnable((Frame)null, "Save Image File", FileDialog.SAVE);
					try { SwingUtilities.invokeAndWait(r); }
					catch (InvocationTargetException e) { e.printStackTrace(); }
					catch (InterruptedException e) { e.printStackTrace(); }
					String parent = r.getDirectory(), name = r.getFile();
					if (parent == null || name == null) return;
					outputPath = new File(parent, name);
					// fallthrough;
				case PATH:
					ImageIO.write(image, outputFormat, outputPath);
					break;
			}
		} catch (IOException e) {
			System.err.println(e);
			return;
		}
	}
	
	public static int parseInt(String s) {
		if (s.startsWith("0X") || s.startsWith("0x")) return Integer.parseInt(s.substring(2), 16);
		if (s.startsWith("0H") || s.startsWith("0h")) return Integer.parseInt(s.substring(2), 16);
		if (s.startsWith("0O") || s.startsWith("0o")) return Integer.parseInt(s.substring(2), 8);
		if (s.startsWith("0B") || s.startsWith("0b")) return Integer.parseInt(s.substring(2), 2);
		if (s.startsWith("#") || s.startsWith("$")) return Integer.parseInt(s.substring(1), 16);
		return Integer.parseInt(s);
	}
	
	public static void printHelp() {
		System.out.println("bin2png - convert binary data to a bitmap image");
		System.out.println();
		System.out.println("  -i <path>   specify input file");
		System.out.println("  -I          specify standard input");
		System.out.println("  -L          ask for input file");
		System.out.println("  -w <int>    image width (pixels)");
		System.out.println("  -h <int>    image height (pixels)");
		System.out.println("  -s <int>    data offset (bytes)");
		System.out.println("  -l <int>    scanline/row length (bytes)");
		System.out.println("  -b <str>    pixel format/bits per pixel");
		System.out.println("  -c <str>    color table");
		System.out.println("  -d          no options/preview dialog");
		System.out.println("  -D          show options/preview dialog");
		System.out.println("  -f <str>    output format (default png)");
		System.out.println("  -o <path>   specify output file");
		System.out.println("  -O          specify standard output");
		System.out.println("  -S          ask for output file");
	}
}
