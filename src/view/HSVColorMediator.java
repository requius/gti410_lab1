/******************************************************
Cours : GTI410
Session : E2015
Groupe : 01
Projet : Laboratoire #1
�tudiant(e)(s) : Andrew Leong
Domgaing Moyo David Francis
Code(s) perm. :  LEOS26099000
DJOD14058001
Charg� de cours : Francis Cardinal
Charg�s de labo : Francis Cardinal
Nom du fichier : HSVColorMediator.java
Date cr�� : 2015/05/11
Date dern. modif. 2015/05/24
 *******************************************************/

package view;

import java.awt.image.BufferedImage;

import model.ObserverIF;
import model.Pixel;

class HSVColorMediator extends Object implements SliderObserver, ObserverIF {
	ColorSlider hueCS;
	ColorSlider saturationCS;
	ColorSlider valueCS;
	float hue;
	float saturation;
	float value;
	BufferedImage hueImage;
	BufferedImage saturationImage;
	BufferedImage valueImage;
	int imagesWidth;
	int imagesHeight;
	ColorDialogResult result;
	int red;
	int green;
	int blue;

	/**
	 * Constructeur
	 * @param result
	 * @param imagesWidth
	 * @param imagesHeight
	 */
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

		float[] hsvColors = convertRGBtoHSV(red, green, blue);

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
			this.hue = (((float) v / 255) * 360);
			updateSaturation = true;
			updateValue = true;
		}
		if (cs == saturationCS && v != this.getSaturation()) {
			this.saturation = ((float) v / 255);
			updateHue = true;
			updateValue = true;
		}
		if (cs == valueCS && v != this.getValue()) {
			this.value = ((float) v / 255);
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
	/**
	 * Calcule la valeur de Hue 
	 * @param hue
	 * @param saturation
	 * @param value
	 */
	public void computeHueImage(float hue, float saturation, float value) {
		Pixel p = new Pixel(red, green, blue);
		int[] rgbColors;
		for (int i = 0; i < imagesWidth; ++i) {
			rgbColors = convertHSVtoRGB(
					(((float) i / (float) imagesWidth) * 360), saturation,
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

	/**
	 * Caclule la valeur de Saturation
	 * @param hue
	 * @param saturation
	 * @param value
	 */
	public void computeSaturationImage(float hue, float saturation, float value) {
		Pixel p = new Pixel(red, green, blue);
		int[] rgbColors;
		for (int i = 0; i < imagesWidth; i++) {
			rgbColors = convertHSVtoRGB(hue,
					(((float) i / (float) imagesWidth)), value);
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
	/**
	 * Calcule la valeur de Value
	 * @param hue
	 * @param saturation
	 * @param value
	 */
	public void computeValueImage(float hue, float saturation, float value) {
		Pixel p = new Pixel(red, green, blue);
		int[] rgbColors;
		for (int i = 0; i < imagesWidth; i++) {
			rgbColors = convertHSVtoRGB(hue, saturation,
					(((float) i / (float) imagesWidth)));
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

	/**
	 * @return
	 */
	public BufferedImage getHueImage() {
		return hueImage;
	}

	/**
	 * @return
	 */
	public BufferedImage getSaturationImage() {
		return saturationImage;
	}

	/**
	 * @return
	 */
	public BufferedImage getValueImage() {
		return valueImage;
	}

	/**
	 * @param slider
	 */
	public void setHueCS(ColorSlider slider) {
		hueCS = slider;
		slider.addObserver(this);
	}
	
	/**
	 * @param slider
	 */
	public void setSaturationCS(ColorSlider slider) {
		saturationCS = slider;
		slider.addObserver(this);
	}

	/**
	 * @param slider
	 */
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
	
//Cette fonction est inspiré à partir des notes de cours de GTI410,
//la partie HSV to RGB conversion formula
//http://www.rapidtables.com/convert/color/hsv-to-rgb.htm	
	/**
	 * Converti les valeurs RGB en valeur HSV
	 * @param red
	 * @param green
	 * @param blue
	 * @return 
	 */
	public float[] convertRGBtoHSV(int red, int green, int blue) {
		float[] hsvColors = new float[3];

		float r = (float) red / 255;
		float g = (float) green / 255;
		float b = (float) blue / 255;

		float max = Math.max(r, Math.max(g, b));
		float min = Math.min(r, Math.min(g, b));

		float hue = 0;
		float value = max;
		float saturation = (value - min) / value;
		
		if (r == max && g == min) {
			hue = 5 + (r - b) / (r - g);
		} else if (r == max && b == min) {
			hue = 1 - (r - g) / (r - b);
		} else if (g == max && b == min) {
			hue = 1 + (g - r) / (g - b);
		} else if (g == max && r == min) {
			hue = 3 - (g - b) / (g - r);
		} else if (b == max && r == min) {
			hue = 3 + (b - g) / (b - r);
		} else if (b == max && g == min) {
			hue = 5 - (b - r) / (b - g);
		}
		hue = hue * 60;

		if (hue < 0)
			hue += 360;

		hsvColors[0] = hue;
		hsvColors[1] = saturation;
		hsvColors[2] = value;
		
		return hsvColors;
	}

//Cette fonction est inspiré à partir du site web ci-dessous,
//la partie HSV to RGB conversion formula
//http://www.rapidtables.com/convert/color/hsv-to-rgb.htm	
	/**
	 * Converti les valeurs HSV en valeurs RGB
	 * @param hue
	 * @param saturation
	 * @param value
	 * @return 
	 */
	public int[] convertHSVtoRGB(float hue, float saturation, float value) {
		int[] rgbColors = new int[3];
		
		float c = value * saturation;
		float h = hue / 60;
		float x = c * (1 - Math.abs(h % 2 - 1));
		float r = 0, g = 0, b = 0;

		if (0 <= h && h <= 1) {
			r = c;
			g = x;
			b = 0;
		} else if (1 <= h && h <= 2) {
			r = x;
			g = c;
			b = 0;
		} else if (1 <= h && h <= 2) {
			r = x;
			g = c;
			b = 0;
		} else if (2 <= h && h <= 3) {
			r = 0;
			g = c;
			b = x;
		} else if (3 <= h && h <= 4) {
			r = 0;
			g = x;
			b = c;
		} else if (4 <= h && h <= 5) {
			r = x;
			g = 0;
			b = c;
		} else if (5 <= h && h <= 6) {
			r = c;
			g = 0;
			b = x;
		}

		float m = (float) (value - c);
		
		rgbColors[0] = (int) ((r + m) * 255);
		rgbColors[1] = (int) ((g + m) * 255);
		rgbColors[2] = (int) ((b + m) * 255);

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

		float[] hsvColors = convertRGBtoHSV(red, green, blue);

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