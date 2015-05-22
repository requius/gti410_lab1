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
	float cyan;
	float magenta;
	float yellow;
	float black;
	BufferedImage cyanImage;
	BufferedImage magentaImage;
	BufferedImage yellowImage;
	BufferedImage blackImage;
	int imagesWidth;
	int imagesHeight;
	ColorDialogResult result;
	
	CMYKColorMediator(ColorDialogResult result, int imagesWidth, int imagesHeight) {
		this.imagesWidth = imagesWidth;
		this.imagesHeight = imagesHeight;
		
		int red = result.getPixel().getRed();
		int green = result.getPixel().getGreen();
		int blue = result.getPixel().getBlue();
		
		float[] cmykColors = convertRGVtoCMYK(red, green, blue);
		
		this.cyan = cmykColors[0];
		this.magenta = cmykColors[1];
		this.yellow = cmykColors[2];
		this.black = cmykColors[3];
		
		this.result = result;
		result.addObserver(this);
		
		cyanImage = new BufferedImage(imagesWidth, imagesHeight, BufferedImage.TYPE_INT_ARGB);
		magentaImage = new BufferedImage(imagesWidth, imagesHeight, BufferedImage.TYPE_INT_ARGB);
		yellowImage = new BufferedImage(imagesWidth, imagesHeight, BufferedImage.TYPE_INT_ARGB);
		blackImage = new BufferedImage(imagesWidth, imagesHeight, BufferedImage.TYPE_INT_ARGB);
		
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
	
	public void computeCyanImage(float cyan, float magenta, float yellow, float black) { 
		int[] rgbColors = convertCMYKtoRGB(cyan, magenta, yellow, black);
		Pixel p = new Pixel(rgbColors[0], rgbColors[1], rgbColors[2], 255); 
		for (int i = 0; i<imagesWidth; ++i) {	
			p.setRed(255 - (int)(black * 255) - (int)(((double)i / (double)imagesWidth) * (255.0 - (black * 255))));
			int rgb = p.getARGB();
			for (int j = 0; j<imagesHeight; ++j) {
				cyanImage.setRGB(i, j, rgb);
			}
		}
		if (cyanCS != null) {
			cyanCS.update(cyanImage);
		}
	}
	
	public void computeMagentaImage(float cyan, float magenta, float yellow, float black) {
		int[] rgbColors = convertCMYKtoRGB(cyan, magenta, yellow, black);
		Pixel p = new Pixel(rgbColors[0], rgbColors[1], rgbColors[2], 255);
		for (int i = 0; i<imagesWidth; ++i) {
			p.setGreen(255 - (int)(black * 255) - (int)(((double)i / (double)imagesWidth) * (255.0 - (black * 255)))); 
			int rgb = p.getARGB();
			for (int j = 0; j<imagesHeight; ++j) {
				magentaImage.setRGB(i, j, rgb);
			}
		}
		if (magentaCS != null) {
			magentaCS.update(magentaImage);
		}
	}
	
	public void computeYellowImage(float cyan, float magenta, float yellow, float black) { 
		int[] rgbColors = convertCMYKtoRGB(cyan, magenta, yellow, black);
		Pixel p = new Pixel(rgbColors[0], rgbColors[1], rgbColors[2], 255);
		for (int i = 0; i<imagesWidth; ++i) {
			p.setBlue(255 - (int)(black * 255) - (int)(((double)i / (double)imagesWidth) * (255.0 - (black * 255)))); 
			int rgb = p.getARGB();
			for (int j = 0; j<imagesHeight; ++j) {
				yellowImage.setRGB(i, j, rgb);
			}
		}
		if (yellowCS != null) {
			yellowCS.update(yellowImage);
		}
	}
	
	public void computeBlackImage(float cyan, float magenta, float yellow, float black) {
		int blackTemp;
		int[] rgbColors = convertCMYKtoRGB(cyan, magenta, yellow, black);
		Pixel p = new Pixel(rgbColors[0], rgbColors[1], rgbColors[2], 255);
		for (int i = 0; i<imagesWidth; ++i) {
			blackTemp = (int)Math.round((((double)i / (double)imagesWidth) * (255.0)));
			
			rgbColors = convertCMYKtoRGB(cyan, magenta, yellow, blackTemp);
			
			p.setRed(rgbColors[0]); 
			p.setGreen(rgbColors[1]); 
			p.setBlue(rgbColors[2]);
			
			int rgb = p.getARGB();
			for (int j = 0; j<imagesHeight; ++j) {
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
		return yellowImage;
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
		return Math.round(cyan * 255);
	}
	
	/**
	 * @return
	 */
	public int getMagenta() {
		return Math.round(magenta * 255);
	}
	
	/**
	 * @return
	 */
	public int getYellow() {
		return Math.round(yellow * 255);
	}
	
	/**
	 * @return
	 */
	public int getBlack() {
		return Math.round(black * 255);
	}

	public float[] convertRGVtoCMYK(int red, int green, int blue){
		float[] cmykColors = new float[4];
		
		float redTemp = red / 255;
		float greenTemp = green / 255;
		float blueTemp = blue / 255;
		
		float black = 1 - Math.max(redTemp, Math.max(greenTemp, blueTemp));
		float cyan = (1 - redTemp - black) / (1 - black);
		float magenta = (1 - greenTemp - black) / (1 - black);
		float yellow = (1 - blueTemp - black) / (1 - black);
		
		cmykColors[0] = cyan;
		cmykColors[1] = magenta;
		cmykColors[2] = yellow;
		cmykColors[3] = black;
		
		return cmykColors;
	}

	
	public int[] convertCMYKtoRGB(float cyan, float magenta, float yellow, float black){
		int[] rgbColors = new int[3];
		
		int red = Math.round(255 * (1 - cyan) * (1 - black)); 
		int green = Math.round(255 * (1 - magenta) * (1 - black)); 
		int blue = Math.round(255 * (1 - yellow) * (1 - black)); 
		
		rgbColors[0] = red;
		rgbColors[1] = green;
		rgbColors[2] = blue;
		
		return rgbColors;
	}
	
	
	/* (non-Javadoc)
	 * @see model.ObserverIF#update()
	 */
	public void update() {
		// When updated with the new "result" color, if the "currentColor"
		// is aready properly set, there is no need to recompute the images.
		int[] rgbColors = convertCMYKtoRGB(cyan, magenta, yellow, black);
		Pixel currentColor = new Pixel(rgbColors[0], rgbColors[1], rgbColors[2], 255);
		if(currentColor.getARGB() == result.getPixel().getARGB()) return;
		
		cyan = result.getPixel().getRed();
		magenta = result.getPixel().getGreen();
		yellow = result.getPixel().getBlue();
		
		cyanCS.setValue(Math.round(cyan * 100));
		magentaCS.setValue(Math.round(magenta * 100 ));
		yellowCS.setValue(Math.round(yellow * 100 ));
		blackCS.setValue(Math.round(black * 100 ));
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
