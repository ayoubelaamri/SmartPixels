package application;


import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
 
public class ImageProcessing {
    
	static int nBins = 8;
	
	public static int[] computeHistogram1(BufferedImage img) {
        int width = img.getWidth();
        int height = img.getHeight();
        int[] histo = new int[width*height];
        
        int i=0;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                // histo[luminance(img.getRGB(x, y))]++;
            	histo[i] = luminance(img.getRGB(x, y));
            	i++;
            }
        }
        return histo;
    }
	
	public static int compareHistograms1(int[] a, int[] b) {
    	int result = 0;
    	for(int i=0; i<a.length && i<b.length; i++) {
    		//result += (a[i] - b[i]) * (a[i] - b[i]);
    		result += Math.sqrt(a[i]*b[i]);
    	}
    	return result;
    }
	
	public static int luminance(int rgb) {
        int r = (rgb >> 16) & 0xFF;
        int g = (rgb >> 8) & 0xFF;
        int b = rgb & 0xFF;
        return (r + b + g) / 3;
    }

//===================================================================================
	
	public static double[][] computeHistogram2(BufferedImage img) {
        int width = img.getWidth();
        int height = img.getHeight();

        double[][] histo = new double[256][3];
        
        for (int i=0; i<256; i++) {
        	for (int j=0; j<3; j++) {
        		histo[i][j] = 0;
        	}
        }
        
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
            	int r = (img.getRGB(x, y) >> 16) & 0xFF;
                int g = (img.getRGB(x, y) >> 8) & 0xFF;
                int b = img.getRGB(x, y) & 0xFF;
            	
            	histo[r][0] += 1;
            	histo[g][1] += 1;
            	histo[b][2] += 1;
            }
        }
        
        //=============================================
    	//Normalize histograms ..
	    	double maxColTotal = 0;
			double colTotal;
			for (int w = 0; w < 256; w++) {
				colTotal = 0;
				for (int c = 0; c < 3; c++) {
					colTotal += histo[w][c];
				}
				if (colTotal > maxColTotal) {
					maxColTotal = colTotal;
				}
			}
		
		// normalize histogram based on the maximum column total
			for (int w = 0; w < 256; w++) {
				for (int c = 0; c < 3; c++) {
					histo[w][c] /= maxColTotal;
					BigDecimal bd = new BigDecimal(histo[w][c]).setScale(2, RoundingMode.UP);
					histo[w][c] = bd.doubleValue();
				}
			}
        //=============================================
        
        return histo;
    }
	
	public static double compareHistograms2(double[][] a, double[][] b) {
    	double resultR = 0;
    	double resultG = 0;
    	double resultB = 0;
    	
    	for(int i=0; i<256; i++) {
    		resultR += Math.sqrt((a[i][0] - b[i][0]) * (a[i][0] - b[i][0]));
    		resultG += Math.sqrt((a[i][1] - b[i][1]) * (a[i][1] - b[i][1]));
    		resultB += Math.sqrt((a[i][2] - b[i][2]) * (a[i][2] - b[i][2]));
    	}
    	return Math.sqrt(resultR+resultG+resultB);
    }
	
//===================================================================================

	
    public static int[][] computeHistogram3(BufferedImage img) {
        int width = img.getWidth();
        int height = img.getHeight();

        int[][] histo = new int[nBins*nBins*nBins][4];
        
        int a = 0;
        int b = 0;
    	while (b<nBins) {
    		int c = 0;
    		while (c<nBins) {
    			int d = 0;
    			while (d<nBins && a<nBins*nBins*nBins) {
    				histo[a][0] = b;
    				histo[a][1] = c;
    				histo[a][2] = d;
    				histo[a][3] = 0;
    				d++;
    				a++;
    			}
    			c++;
    		}
    		b++;
    	}
        
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
            	int red = (img.getRGB(x, y) >> 16) & 0xFF;
                int green = (img.getRGB(x, y) >> 8) & 0xFF;
                int blue = img.getRGB(x, y) & 0xFF;
            	
                int nr=0, ng=0, nb=0;
                
                for (int i=0; i<nBins; i++) {
                	if (red>(i+1)*(256/nBins)) 
                    	nr += 1;
                	if (green>(i+1)*(256/nBins)) 
                    	ng += 1;
                	if (blue>(i+1)*(256/nBins)) 
                    	nb += 1; 
                }
                
                for (int i=0; i<nBins*nBins*nBins; i++) {
                	if (histo[i][0] == nr) {
                		if (histo[i][1] == ng) {
                			if (histo[i][2] == nb) {
                        		histo[i][3] += 1;
                        	}
                    	}
                	}
                }
            }
        }
        
        return histo;
    }

    public static double compareHistograms3(int[][] a, int[][] b) {
    	double result = 0;
    	
    	for(int i=0; i<nBins*nBins*nBins; i++) {
    		result += Math.sqrt((a[i][3] - b[i][3]) * (a[i][3] - b[i][3]));
    	}
    	return Math.sqrt(result);
    }
 
//===================================================================================

    
	public static byte [] ImageToByte(File file) throws FileNotFoundException{
        FileInputStream fis = new FileInputStream(file);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] buf = new byte[1024];
        System.out.println("Converting image to Bytes .. ");
        try {
            for (int readNum; (readNum = fis.read(buf)) != -1;) {
                bos.write(buf, 0, readNum);     
            }
        } catch (IOException ex) {
        }
        byte[] bytes = bos.toByteArray();
        
        return bytes;
    }
	
	public static BufferedImage ByteToImage(byte[] img) throws IOException {

			  ByteArrayInputStream bis = new ByteArrayInputStream(img);
		      BufferedImage image = ImageIO.read(bis);
		      //ImageIO.write(image, "jpg", new File("output.jpg") );
		      return image;
    }
	
	public static ImageIcon ResizeImage(BufferedImage bufImage) {
		Image newImg = bufImage.getScaledInstance(300, 300, Image.SCALE_SMOOTH);
        ImageIcon image = new ImageIcon(newImg);
        return image;
	}
 
}