/*
   This file is part of j2dcg.
   j2dcg is free software; you can redistribute it and/or modify
   it under the terms of the GNU General Public License as published by
   the Free Software Foundation; either version 2 of the License, or
   (at your option) any later version.
   j2dcg is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
   GNU General Public License for more details.
   You should have received a copy of the GNU General Public License
   along with j2dcg; if not, write to the Free Software
   Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package view;

import java.awt.image.BufferedImage;

import model.ObserverIF;
import model.Pixel;

class CMYKColorMediator extends Object implements SliderObserver, ObserverIF {
	ColorSlider cyanCS;
	ColorSlider magentaCS;
	ColorSlider yellowCS;
	ColorSlider blackCS;
	int cyan;
	int magenta;
	int yellow;
	int black;
	BufferedImage cyanImage;
	BufferedImage magentaImage;
	BufferedImage yellowImage;
	BufferedImage blackImage;
	int imagesWidth;
	int imagesHeight;
	ColorDialogResult result;

	CMYKColorMediator(ColorDialogResult result, int imagesWidth,
			int imagesHeight) {
		this.imagesWidth = imagesWidth;
		this.imagesHeight = imagesHeight;

		int red = result.getPixel().getRed();
		int green = result.getPixel().getGreen();
		int blue = result.getPixel().getBlue();

		int[] cmykColors = convertRGBtoCMYK(red, green, blue);

		this.cyan = cmykColors[0];
		this.magenta = cmykColors[1];
		this.yellow = cmykColors[2];
		this.black = cmykColors[3];

		this.result = result;
		result.addObserver(this);

		cyanImage = new BufferedImage(imagesWidth, imagesHeight,
				BufferedImage.TYPE_INT_ARGB);
		magentaImage = new BufferedImage(imagesWidth, imagesHeight,
				BufferedImage.TYPE_INT_ARGB);
		yellowImage = new BufferedImage(imagesWidth, imagesHeight,
				BufferedImage.TYPE_INT_ARGB);
		blackImage = new BufferedImage(imagesWidth, imagesHeight,
				BufferedImage.TYPE_INT_ARGB);

		computeCyanImage(cyan, magenta, yellow, black);
		computeMagentaImage(cyan, magenta, yellow, black);
		computeYellowImage(cyan, magenta, yellow, black);
		computeBlackImage(cyan, magenta, yellow, black);
	}

	/*
	 * @see View.SliderObserver#update(double)
	 */
	public void update(ColorSlider s, int v) {
		boolean updateCyan = false;
		boolean updateMagenta = false;
		boolean updateYellow = false;
		boolean updateBlack = false;
		if (s == cyanCS && v != cyan) {
			cyan = v;
			updateMagenta = true;
			updateYellow = true;
			updateBlack = true;
		}
		if (s == magentaCS && v != magenta) {
			magenta = v;
			updateCyan = true;
			updateYellow = true;
			updateBlack = true;
		}
		if (s == yellowCS && v != yellow) {
			yellow = v;
			updateCyan = true;
			updateMagenta = true;
			updateBlack = true;
		}
		if (s == blackCS && v != black) {
			black = v;
			updateCyan = true;
			updateMagenta = true;
			updateYellow = true;
		}
		if (updateCyan) {
			computeCyanImage(cyan, magenta, yellow, black);
		}
		if (updateMagenta) {
			computeMagentaImage(cyan, magenta, yellow, black);
		}
		if (updateYellow) {
			computeYellowImage(cyan, magenta, yellow, black);
		}
		if (updateBlack) {
			computeBlackImage(cyan, magenta, yellow, black);
		}

		int[] rgbColors = convertCMYKtoRGB(cyan, magenta, yellow, black);
		Pixel pixel = new Pixel(rgbColors[0], rgbColors[1], rgbColors[2], 255);

		result.setPixel(pixel);
	}

	public void computeCyanImage(int cyan, int magenta, int yellow, int black) {
		int[] rgbColors = convertCMYKtoRGB(cyan, magenta, yellow, black);
		Pixel p = new Pixel(rgbColors[0], rgbColors[1], rgbColors[2], 255);
		for (int i = 0; i < imagesWidth; ++i) {
			p.setRed(255
					- black
					- (int) (((double) i / (double) imagesWidth) * (255.0 - (double) black)));
			int rgb = p.getARGB();
			for (int j = 0; j < imagesHeight; ++j) {
				cyanImage.setRGB(i, j, rgb);
			}
		}
		if (cyanCS != null) {
			cyanCS.update(cyanImage);
		}
	}

	public void computeMagentaImage(int cyan, int magenta, int yellow, int black) {
		int[] rgbColors = convertCMYKtoRGB(cyan, magenta, yellow, black);
		Pixel p = new Pixel(rgbColors[0], rgbColors[1], rgbColors[2], 255);
		for (int i = 0; i < imagesWidth; ++i) {
			p.setGreen(255
					- black
					- (int) (((double) i / (double) imagesWidth) * (255.0 - (double) black)));
			int rgb = p.getARGB();
			for (int j = 0; j < imagesHeight; ++j) {
				magentaImage.setRGB(i, j, rgb);
			}
		}
		if (magentaCS != null) {
			magentaCS.update(magentaImage);
		}
	}

	public void computeYellowImage(int cyan, int magenta, int yellow, int black) {
		int[] rgbColors = convertCMYKtoRGB(cyan, magenta, yellow, black);
		Pixel p = new Pixel(rgbColors[0], rgbColors[1], rgbColors[2], 255);
		for (int i = 0; i < imagesWidth; ++i) {
			p.setBlue(255
					- black
					- (int) (((double) i / (double) imagesWidth) * (255.0 - (double) black)));
			int rgb = p.getARGB();
			for (int j = 0; j < imagesHeight; ++j) {
				yellowImage.setRGB(i, j, rgb);
			}
		}
		if (yellowCS != null) {
			yellowCS.update(yellowImage);
		}
	}

	public void computeBlackImage(int cyan, int magenta, int yellow, int black) {
		int blackTemp;

		int[] rgbColors = convertCMYKtoRGB(cyan, magenta, yellow, black);
		Pixel p = new Pixel(rgbColors[0], rgbColors[1], rgbColors[2], 255);
		for (int i = 0; i < imagesWidth; ++i) {
			blackTemp = (int) Math
					.round((((double) i / (double) imagesWidth) * (255.0)));

			rgbColors = convertCMYKtoRGB(cyan, magenta, yellow, blackTemp);

			p.setRed(rgbColors[0]);
			p.setGreen(rgbColors[1]);
			p.setBlue(rgbColors[2]);

			int rgb = p.getARGB();
			for (int j = 0; j < imagesHeight; ++j) {
				blackImage.setRGB(i, j, rgb);
			}
		}
		if (blackCS != null) {
			blackCS.update(blackImage);
		}
	}

	/**
	 * @return
	 */
	public BufferedImage getCyanImage() {
		return cyanImage;
	}

	/**
	 * @return
	 */
	public BufferedImage getMagentaImage() {
		return magentaImage;
	}

	/**
	 * @return
	 */
	public BufferedImage getYellowImage() {
		return yellowImage;
	}

	/**
	 * @return
	 */
	public BufferedImage getBlackImage() {
		return blackImage;
	}

	/**
	 * @param slider
	 */
	public void setCyanCS(ColorSlider slider) {
		cyanCS = slider;
		slider.addObserver(this);
	}

	/**
	 * @param slider
	 */
	public void setMagentaCS(ColorSlider slider) {
		magentaCS = slider;
		slider.addObserver(this);
	}

	/**
	 * @param slider
	 */
	public void setYellowCS(ColorSlider slider) {
		yellowCS = slider;
		slider.addObserver(this);
	}

	/**
	 * @param slider
	 */
	public void setBlackCS(ColorSlider slider) {
		blackCS = slider;
		slider.addObserver(this);
	}

	/**
	 * @return
	 */
	public int getCyan() {
		return (int) Math.round(this.cyan);
	}

	/**
	 * @return
	 */
	public int getMagenta() {
		return (int) Math.round(this.magenta);
	}

	/**
	 * @return
	 */
	public int getYellow() {
		return (int) Math.round(this.yellow);
	}

	/**
	 * @return
	 */
	public int getBlack() {
		return (int) Math.round(this.black);
	}

	public int[] convertRGBtoCMYK(int red, int green, int blue) {
		int[] cmykColors = new int[4];

		float redTemp = red / 255;
		float greenTemp = green / 255;
		float blueTemp = blue / 255;

		int black = Math.round((1 - Math.max(redTemp,
				Math.max(greenTemp, blueTemp))) * 255);
		int cyan = Math.round(((1 - redTemp - black) / (1 - black)) * 255);
		int magenta = Math.round(((1 - greenTemp - black) / (1 - black)) * 255);
		int yellow = Math.round(((1 - blueTemp - black) / (1 - black)) * 255);

		cmykColors[0] = cyan;
		cmykColors[1] = magenta;
		cmykColors[2] = yellow;
		cmykColors[3] = black;

		return cmykColors;
	}

	public int[] convertCMYKtoRGB(int cyan, int magenta, int yellow, int black) {
		int[] rgbColors = new int[3];

		int red = Math.round((255 - cyan) * (255 - black) / 255);
		int green = Math.round((255 - magenta) * (255 - black) / 255);
		int blue = Math.round((255 - yellow) * (255 - black) / 255);

		rgbColors[0] = red;
		rgbColors[1] = green;
		rgbColors[2] = blue;

		return rgbColors;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see model.ObserverIF#update()
	 */
	public void update() {
		// When updated with the new "result" color, if the "currentColor"
		// is aready properly set, there is no need to recompute the images.
		int[] rgbColors = convertCMYKtoRGB(cyan, magenta, yellow, black);

		int red = rgbColors[0];
		int green = rgbColors[1];
		int blue = rgbColors[2];

		Pixel currentColor = new Pixel(red, green, blue, 255);

		if (currentColor.getARGB() == result.getPixel().getARGB())
			return;

		red = result.getPixel().getRed();
		green = result.getPixel().getGreen();
		blue = result.getPixel().getBlue();

		int[] cmykColors = convertRGBtoCMYK(red, green, blue);

		cyan = cmykColors[0];
		magenta = cmykColors[1];
		yellow = cmykColors[2];
		black = cmykColors[3];

		cyanCS.setValue(cyan);
		magentaCS.setValue(magenta);
		yellowCS.setValue(yellow);
		blackCS.setValue(black);

		computeCyanImage(cyan, magenta, yellow, black);
		computeMagentaImage(cyan, magenta, yellow, black);
		computeYellowImage(cyan, magenta, yellow, black);
		computeBlackImage(cyan, magenta, yellow, black);

		// Efficiency issue: When the color is adjusted on a tab in the
		// user interface, the sliders color of the other tabs are recomputed,
		// even though they are invisible. For an increased efficiency, the
		// other tabs (mediators) should be notified when there is a tab
		// change in the user interface. This solution was not implemented
		// here since it would increase the complexity of the code, making it
		// harder to understand.
	}

}
