# bin2png

What started as a mission to retrieve some artwork from an AppleWorks 6 `.cwk` file turned into a standalone application to decode bitmaps in binary files.

![](wiki/screenshot.png)

## Options

	$ java -jar bin2png.jar --help
	bin2png - convert binary data to a bitmap image
	
	  -i <path>   specify input file
	  -I          specify standard input
	  -L          ask for input file
	  -w <int>    image width (pixels)
	  -h <int>    image height (pixels)
	  -s <int>    data offset (bytes)
	  -l <int>    scanline/row length (bytes)
	  -b <str>    pixel format/bits per pixel
	  -c <str>    color table
	  -d          no options/preview dialog
	  -D          show options/preview dialog
	  -f <str>    output format (default png)
	  -o <path>   specify output file
	  -O          specify standard output
	  -S          ask for output file
