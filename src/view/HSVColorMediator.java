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

class HSVColorMediator extends Object implements SliderObserver, ObserverIF ,ConstantesCouleurs  {
	
	// sliders pour les couleurs
	
	ColorSlider hueCS;
    ColorSlider saturationCS;
    ColorSlider valueCS;

	// buffer image pour chaque model HSV
	
	BufferedImage hueImage;
    BufferedImage saturationImage;
    BufferedImage valueImage;

    // taille de l'image
    
	int imagesWidth;
	int imagesHeight;
	
	// le resultat des couleurs
	ColorDialogResult result;
	
	// la couleur dans le modele HSV
    double hue;
    double saturation;
    double value;
    
 // couleur RGB
    
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
		
		
		//Prepare le buffer image qui affiche le pixel de chaque couleur
		
		
		
		 hueImage = new BufferedImage(imagesWidth, imagesHeight,
                 BufferedImage.TYPE_INT_ARGB);
         saturationImage = new BufferedImage(imagesWidth, imagesHeight,
                 BufferedImage.TYPE_INT_ARGB);
         valueImage = new BufferedImage(imagesWidth, imagesHeight,
                 BufferedImage.TYPE_INT_ARGB);

		
         // conversion de RGB a HSV
         double[] hsvTable = convertRgbToHsv(red, green, blue);

         // setting the proper HSV model values
         this.hue = hsvTable[HUE];
         this.saturation = hsvTable[SATURATION];
         this.value = hsvTable[VALUE];

         // computing each value so that their proper
         // pixels will be shown
         computeHueImage(this.hue, this.saturation, this.value);
         computeSaturationImage(this.hue, this.saturation, this.value);
         computeValueImage(this.hue, this.saturation, this.value);
	
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

    
    public double getHue() {
            return hue;
    }

   
    public double getSaturation() {
            return saturation;
    }

   
    public double getValue() {
            return value;
    }

   
    public int getHueColor() {
            return (int) ((this.hue / MAX_HUE) * MAX_RGB);
    }

    
    public int getSaturationColor() {
            return (int) (this.saturation * MAX_RGB);
    }

   
    public int getValueColor() {
            return (int) (this.value * MAX_RGB);
    }


	
	
	/*
	 * @see View.SliderObserver#update(double)
	 */
	public void update(ColorSlider cs, int v) {
		
		 boolean updateHue = false;
         boolean updateSaturation = false;
         boolean updateValue = false;

         // change the hue number, advise the others that it changed
         if (cs == hueCS && v != this.getHueColor()) {
                 this.hue = (((double) v / MAX_RGB) * MAX_HUE);
                 updateSaturation = true;
                 updateValue = true;
         }

         // change the saturation number and advise the others that it changed
         if (cs == saturationCS && v != this.getSaturationColor()) {
                 this.saturation = ((double) v / MAX_RGB);
                 updateHue = true;
                 updateValue = true;
         }

         // change the value number and advise the others that it changed
         if (cs == valueCS && v != this.getValueColor()) {
                 this.value = ((double) v / MAX_RGB);
                 updateHue = true;
                 updateSaturation = true;
         }

         // compute the Hue image with the values of Hue, Saturation and
         // Value
         if (updateHue) {
                 computeHueImage(hue, saturation, value);
         }

         // compute the Saturation image with the values of Hue, Saturation
         // and Value
         if (updateSaturation) {
                 computeSaturationImage(hue, saturation, value);
         }

         // compute the Value image with the values of Hue, Saturation and
         // Value
         if (updateValue) {
                 computeValueImage(hue, saturation, value);
         }

         // convert the HSV model to RGB model
         int rgbTable[] = convertHsvToRgb(hue, saturation, value);

         // create a new pixel
         Pixel pixel = new Pixel(rgbTable[RED], rgbTable[GREEN], rgbTable[BLUE],
                         MAX_RGB);
         // set the result with that new pixel
         result.setPixel(pixel);

	}
	
	  
      public void update() {
              // When updated with the new "result" color, if the "currentColor"
              // is aready properly set, there is no need to recompute the images.
              int[] rgbPixelTable = convertHsvToRgb(hue, saturation, value);

              // retrieve the RGB model colors
              red = rgbPixelTable[RED];
              green = rgbPixelTable[GREEN];
              blue = rgbPixelTable[BLUE];

              // convert HSV to RGB
              Pixel currentColor = new Pixel(red, green, blue, MAX_RGB);
              if (currentColor.getARGB() == result.getPixel().getARGB())
                      return;

              // convert RGB to HSV
              red = result.getPixel().getRed();
              green = result.getPixel().getGreen();
              blue = result.getPixel().getBlue();
              double[] hsvColorTable = convertRgbToHsv(red, green, blue);

              // assing the color accordingly
              hue = hsvColorTable[HUE];
              saturation = hsvColorTable[SATURATION];
              value = hsvColorTable[VALUE];

              // set the color slider accordingly
              hueCS.setValue((int) ((((hue / MAX_HUE) * MAX_RGB))));
              saturationCS.setValue((int) ((saturation * MAX_RGB)));
              valueCS.setValue((int) ((value * MAX_RGB)));

              // recompute again since the latest result is not the same as the
              // current color
              computeHueImage(hue, saturation, value);
              computeSaturationImage(hue, saturation, value);
              computeValueImage(hue, saturation, value);

              
      }

     

	
	
	
	
      /**
       * Compute the value of Hue Image
       * 
       * @param hue
       * @param saturation
       * @param value
       */
      public void computeHueImage(double hue, double saturation, double value) {

              // create a default pixel with the red, green and value
              Pixel p = new Pixel(red, green, blue);

              // create an empty table
              int[] rgbColorTable;

              // for each width index, convert the HSV model to RGB with the proper
              // Hue value modified
              for (int i = 0; i < imagesWidth; ++i) {
                      rgbColorTable = convertHsvToRgb(
                                      (((double) i / (double) imagesWidth) * MAX_HUE),
                                      saturation, value);

                      // set the proper red, green, and blue Pixel value
                      p.setRed(rgbColorTable[RED]);
                      p.setGreen(rgbColorTable[GREEN]);
                      p.setBlue(rgbColorTable[BLUE]);

                      // retrieve the attribute of that pixel
                      int rgb = p.getARGB();

                      // for each height index, set the hue image with the proper RGB
                      // schema
                      for (int j = 0; j < imagesHeight; j++) {
                              hueImage.setRGB(i, j, rgb);
                      }

              }

              // if the image as change, repaint that slider
              if (hueCS != null) {
                      hueCS.update(hueImage);
              }

      }

      /**
       * Compute the value of Saturation Image
       * 
       * @param hue
       * @param saturation
       * @param value
       */
      public void computeSaturationImage(double hue, double saturation,
                      double value) {

              // create a default pixel with the red, green and value
              Pixel p = new Pixel(red, green, blue);

              // create an empty table
              int[] rgbColorTable;

              // for each width index, convert the HSV model to RGB with the proper
              // Saturation value modified
              for (int i = 0; i < imagesWidth; i++) {
                      rgbColorTable = convertHsvToRgb(hue,
                                      (((double) i / (double) imagesWidth)), value);

                      // set the proper red, green, and blue Pixel value
                      p.setRed(rgbColorTable[RED]);
                      p.setGreen(rgbColorTable[GREEN]);
                      p.setBlue(rgbColorTable[BLUE]);

                      // retrieve the attribute of that pixel
                      int rgb = p.getARGB();

                      // for each height index, set the saturation image
                      for (int j = 0; j < imagesHeight; ++j) {
                              saturationImage.setRGB(i, j, rgb);
                      }

              }

              // if the image as change, repaint that slider
              if (saturationCS != null) {
                      saturationCS.update(saturationImage);
              }
      }


	

      /**
       * Compute the value of Value Image
       * 
       * @param hue
       * @param saturation
       * @param value
       */
      public void computeValueImage(double hue, double saturation, double value) {

              // create a default pixel with the red, green and value
              Pixel p = new Pixel(red, green, blue);

              // create an empty table
              int[] rgbColorTable;

              // for each width index, convert the HSV model to RGB with the proper
              // Value value modified
              for (int i = 0; i < imagesWidth; i++) {
                      rgbColorTable = convertHsvToRgb(hue, saturation,
                                      (((double) i / (double) imagesWidth)));

                      // set the proper red, green, and blue Pixel value
                      p.setRed(rgbColorTable[RED]);
                      p.setGreen(rgbColorTable[GREEN]);
                      p.setBlue(rgbColorTable[BLUE]);

                      // retrieve the attribute of that pixel
                      int rgb = p.getARGB();

                      // for each height index, set the value image
                      for (int j = 0; j < imagesHeight; j++) {
                              valueImage.setRGB(i, j, rgb);
                      }

              }

              // if the image as change, repaint that slider
              if (valueCS != null) {
                      valueCS.update(valueImage);
              }

      }


	
	
	
	
      /**
       * <b>convertRgbToHsv</b>
       * <p>
       * Effectue le calcul des valeurs RGB en valeur HSV
       * 
       * @param redPixelValue
       * @param greenPixelValue
       * @param bluePixelValue
       * @return un tableau de valeurs HSV dans la place [0,255]
       * 
       * @source Code formé à partir des notes de cours de GTI410
       */
      public static double[] convertRgbToHsv(int redPixelValue,
                      int greenPixelValue, int bluePixelValue) {
              double[] hsvTable = new double[HSV_TABLE_SIZE];

              /*
               * L'Algorithme du code suivant provient des notes de cours de Cardinal,
               * Patrick - GTI410 Applications des techniques numériques en graphisme
               * et imagerie v1.2, Hiver2013
               */

              // normalize between 0 and 1
              double r = (double) redPixelValue / MAX_RGB;
              double g = (double) greenPixelValue / MAX_RGB;
              double b = (double) bluePixelValue / MAX_RGB;

              // found the minimum and the maximum values
              double max = Math.max(r, Math.max(g, b));
              double min = Math.min(r, Math.min(g, b));

              // calculate the Value et Saturation values
              double value = max;
              hsvTable[VALUE] = value;
              double saturation = (value - min) / value;
              hsvTable[SATURATION] = saturation;

              /*
               * if(max == 0) { // r = g = b = 0 // s = 0, v is undefined
               * //tabHSV[VALUE] = 0; hsvTable[SATURATION] = 0; hsvTable[HUE] = 0;
               * return hsvTable; }
               */

              double hue = 0;

              // calculate the Hue value according to the min and max values of RGB
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
              // to be between 0 and 360
              if (hue < 0)
                      hue += 360;

              hsvTable[HUE] = hue;

              return hsvTable;
      }




	
	
	/**
   
     * Effectue le calcul des valeurs HSV en RGB
     
     
     */

	public int[] convertHsvToRgb(double hue, double saturation, double value) {
        int[] rgbPixelTable = new int[RGB_TABLE_SIZE];
        /*
         * L'Algorithme du code suivant provient des notes sur le sites
         * http://en.wikipedia.org/wiki/HSL_and_HSV
         */

        // hue H elements of [0, 360], saturation SHSV elements of [0, 1], and
        // value V elements of [0, 1], we first find chroma:
        double chroma = value * saturation;

        // Then we can find a point (R1, G1, B1) along the bottom three faces of
        // the RGB cube, with the same hue and chroma as our color (using the
        // intermediate value X for the second largest component of this color):
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
        rgbPixelTable[RED] = (int) ((r + matchValue) * 255);
        rgbPixelTable[GREEN] = (int) ((g + matchValue) * 255);
        rgbPixelTable[BLUE] = (int) ((b + matchValue) * 255);

        // return the value in a table format
        return rgbPixelTable;
   }



}

