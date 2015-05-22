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

class HSVColorMediator extends Object implements SliderObserver, ObserverIF {
	ColorSlider hueCS;
	ColorSlider saturationCS;
	ColorSlider valueCS;
	double hue;
	double saturation;
	double value;
	BufferedImage hueImage;
	BufferedImage saturationImage;
	BufferedImage valueImage;
	int imagesWidth;
	int imagesHeight;
	ColorDialogResult result;
	int red;
	int green;
	int blue;

	HSVColorMediator(ColorDialogResult result, int imagesWidth, int imagesHeight) {
		this.imagesWidth = imagesWidth;
		this.imagesHeight = imagesHeight;

		this.red = result.getPixel().getRed();
		this.green = result.getPixel().getGreen();
		this.blue = result.getPixel().getBlue();

		

		this.result = result;
		result.addObserver(this);

		hueImage = new BufferedImage(imagesWidth, imagesHeight,
				BufferedImage.TYPE_INT_ARGB);
		saturationImage = new BufferedImage(imagesWidth, imagesHeight,
				BufferedImage.TYPE_INT_ARGB);
		valueImage = new BufferedImage(imagesWidth, imagesHeight,
				BufferedImage.TYPE_INT_ARGB);
		
		double[] hsvColors = convertRGBtoHSV(red, green, blue);
		
		this.hue = hsvColors[0];
		this.saturation = hsvColors[1];
		this.value = hsvColors[2];
		
		computeHueImage(this.hue, this.saturation, this.value);
		computeSaturationImage(this.hue, this.saturation, this.value);
		computeValueImage(this.hue, this.saturation, this.value);

	}

	/*
	 * @see View.SliderObserver#update(double)
	 */
	public void update(ColorSlider cs, int v) {
		boolean updateHue = false;
		boolean updateSaturation = false;
		boolean updateValue = false;

		if (cs == hueCS && v != this.getHue()) {
			this.hue = (((double) v / 255) * 360);
			updateSaturation = true;
			updateValue = true;
		}
		if (cs == saturationCS && v != this.getSaturation()) {
			this.saturation = ((double) v / 255);
			updateHue = true;
			updateValue = true;
		}
		if (cs == valueCS && v != this.getValue()) {
			this.value = ((double) v / 255);
			updateHue = true;
			updateSaturation = true;
		}
		if (updateHue) {
			computeHueImage(hue, saturation, value);
		}
		if (updateSaturation) {
			computeSaturationImage(hue, saturation, value);
		}
		if (updateValue) {
			computeValueImage(hue, saturation, value);
		}

		int rgbTable[] = convertHSVtoRGB(hue, saturation, value);

		Pixel pixel = new Pixel(rgbTable[0], rgbTable[1], rgbTable[2], 255);

		result.setPixel(pixel);

	}

	public void computeHueImage(double hue, double saturation, double value) {
		Pixel p = new Pixel(red, green, blue);
		int[] rgbColors;
		for (int i = 0; i < imagesWidth; ++i) {
			rgbColors = convertHSVtoRGB(
					(((double) i / (double) imagesWidth) * 360), saturation,
					value);
			p.setRed(rgbColors[0]);
			p.setGreen(rgbColors[1]);
			p.setBlue(rgbColors[2]);
			int rgb = p.getARGB();
			for (int j = 0; j < imagesHeight; j++) {
				hueImage.setRGB(i, j, rgb);
			}
		}
		if (hueCS != null) {
			hueCS.update(hueImage);
		}
	}

	public void computeSaturationImage(double hue, double saturation,
			double value) {
		Pixel p = new Pixel(red, green, blue);
		int[] rgbColors;
		for (int i = 0; i < imagesWidth; i++) {
			rgbColors = convertHSVtoRGB(hue,
					(((double) i / (double) imagesWidth)), value);
			p.setRed(rgbColors[0]);
			p.setGreen(rgbColors[1]);
			p.setBlue(rgbColors[2]);
			int rgb = p.getARGB();
			for (int j = 0; j < imagesHeight; ++j) {
				saturationImage.setRGB(i, j, rgb);
			}
		}
		if (saturationCS != null) {
			saturationCS.update(saturationImage);
		}
	}

	public void computeValueImage(double hue, double saturation, double value) {
		Pixel p = new Pixel(red, green, blue);
		int[] rgbColors;
		for (int i = 0; i < imagesWidth; i++) {
			rgbColors = convertHSVtoRGB(hue, saturation,
					(((double) i / (double) imagesWidth)));
			p.setRed(rgbColors[0]);
			p.setGreen(rgbColors[1]);
			p.setBlue(rgbColors[2]);
			int rgb = p.getARGB();
			for (int j = 0; j < imagesHeight; j++) {
				valueImage.setRGB(i, j, rgb);
			}
		}
		if (valueCS != null) {
			valueCS.update(valueImage);
		}
	}

	public BufferedImage getHueImage() {
		return hueImage;
	}

	public BufferedImage getSaturationImage() {
		return saturationImage;
	}

	public BufferedImage getValueImage() {
		return valueImage;
	}

	public void setHueCS(ColorSlider slider) {
		hueCS = slider;
		slider.addObserver(this);
	}

	public void setSaturationCS(ColorSlider slider) {
		saturationCS = slider;
		slider.addObserver(this);
	}

	public void setValueCS(ColorSlider slider) {
		valueCS = slider;
		slider.addObserver(this);
	}

	/**
	 * @return
	 */
	public int getHue() {
		return (int) ((this.hue / 360) * 255);
	}
	
	/**
	 * @return
	 */
	public int getSaturation() {
		return (int) (this.saturation * 255);
	}
	
	/**
	 * @return
	 */
	public int getValue() {
		return (int) (this.value * 255);
	}

	public static double[] convertRGBtoHSV(int red, int green, int blue) {
		double[] hsvColors = new double[3];

		double r = (double) red / 255;
		double g = (double) green / 255;
		double b = (double) blue / 255;

		double max = Math.max(r, Math.max(g, b));
		double min = Math.min(r, Math.min(g, b));

		double value = max;
		hsvColors[2] = value;
		double saturation = (value - min) / value;
		hsvColors[1] = saturation;

		double hue = 0;

		if (r == max && g == min)
			hue = 5 + (r - b) / (r - g);
		else if (r == max && b == min)
			hue = 1 - (r - g) / (r - b);
		else if (g == max && b == min)
			hue = 1 + (g - r) / (g - b);
		else if (g == max && r == min)
			hue = 3 - (g - b) / (g - r);
		else if (b == max && r == min)
			hue = 3 + (b - g) / (b - r);
		else if (b == max && g == min)
			hue = 5 - (b - r) / (b - g);
		hue = hue * 60;

		if (hue < 0)
			hue += 360;

		hsvColors[0] = hue;

		return hsvColors;
	}

	public int[] convertHSVtoRGB(double hue, double saturation, double value) {
		int[] rgbColors = new int[3];
		double chroma = value * saturation;
		double hPrime = hue / 60;
		double x = chroma * (1 - Math.abs(hPrime % 2 - 1));
		double r = 0, g = 0, b = 0;

		if (0 <= hPrime && hPrime <= 1) {
			r = chroma;
			g = x;
			b = 0;

		} else if (1 <= hPrime && hPrime <= 2) {
			r = x;
			g = chroma;
			b = 0;
		} else if (1 <= hPrime && hPrime <= 2) {
			r = x;
			g = chroma;
			b = 0;
		} else if (2 <= hPrime && hPrime <= 3) {
			r = 0;
			g = chroma;
			b = x;
		} else if (3 <= hPrime && hPrime <= 4) {
			r = 0;
			g = x;
			b = chroma;
		} else if (4 <= hPrime && hPrime <= 5) {
			r = x;
			g = 0;
			b = chroma;
		} else if (5 <= hPrime && hPrime <= 6) {
			r = chroma;
			g = 0;
			b = x;
		}

		double matchValue = value - chroma;
		rgbColors[0] = (int) ((r + matchValue) * 255);
		rgbColors[1] = (int) ((g + matchValue) * 255);
		rgbColors[2] = (int) ((b + matchValue) * 255);

		return rgbColors;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see model.ObserverIF#update()
	 */
	public void update() {

		int[] rgbColors = convertHSVtoRGB(hue, saturation, value);

		red = rgbColors[0];
		green = rgbColors[1];
		blue = rgbColors[2];

		Pixel currentColor = new Pixel(red, green, blue, 255);
		if (currentColor.getARGB() == result.getPixel().getARGB())
			return;

		red = result.getPixel().getRed();
		green = result.getPixel().getGreen();
		blue = result.getPixel().getBlue();
		
		double[] hsvColors = convertRGBtoHSV(red, green, blue);

		hue = hsvColors[0];
		saturation = hsvColors[1];
		value = hsvColors[2];

		hueCS.setValue((int) ((((hue / 360) * 255))));
		saturationCS.setValue((int) ((saturation * 255)));
		valueCS.setValue((int) ((value * 255)));

		computeHueImage(hue, saturation, value);
		computeSaturationImage(hue, saturation, value);
		computeValueImage(hue, saturation, value);

		// Efficiency issue: When the color is adjusted on a tab in the
		// user interface, the sliders color of the other tabs are recomputed,
		// even though they are invisible. For an increased efficiency, the
		// other tabs (mediators) should be notified when there is a tab
		// change in the user interface. This solution was not implemented
		// here since it would increase the complexity of the code, making it
		// harder to understand.
	}

}
