package application;

import java.awt.image.BufferedImage;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;

public class ConnectDB {
	
	private static Connection conn;
	private static PreparedStatement ps;
	public static ResultSet rs;
	
	private static PreparedStatement psAdd;
	private static PreparedStatement psChange;
	private static PreparedStatement psDel;
	
	private static PreparedStatement psGet;
	private static ResultSet rsGet;
	private static PreparedStatement psGetId;
	private static ResultSet rsGetId;
	
	private static PreparedStatement psFirst;
	private static PreparedStatement psLast;
	public static  ResultSet rsFirst;
	public static  ResultSet rsLast;
	
	private static PreparedStatement psHistograms;
	public static  ResultSet rsHistograms;
	
	public static int cmpResult;
	
	public ConnectDB() throws SQLException {
		try {
			Class.forName("org.postgresql.Driver");

		      String url = "jdbc:postgresql://localhost:5432/Images_db";
		      String user = "postgres";
		      String passwd = "admin";
		      
		    conn = DriverManager.getConnection(url, user, passwd);        
		         
		    } catch (Exception e) {
		      e.printStackTrace();
		    }
		
		ps = conn.prepareStatement("SELECT COUNT(*) FROM images");
		rs = ps.executeQuery();
		rs.next();
		
	}
	
	public void addImageToDB(byte[] img, String histo,String histoR, String histoG, String histoB, String RED, String GREEN, String BLUE, String NP) {
			  
		  try {  
			   psAdd = conn
			     .prepareStatement("INSERT INTO images(image,histogram,histored,histogreen,histoblue,red,green,blue,pixelsnumber) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)",Statement.RETURN_GENERATED_KEYS);
			   psAdd.setBytes(1, img);
			   psAdd.setString(2, histo);
			   psAdd.setString(3, histoR);
			   psAdd.setString(4, histoG);
			   psAdd.setString(5, histoB);
			   psAdd.setString(6, RED);
			   psAdd.setString(7, GREEN);
			   psAdd.setString(8, BLUE);
			   psAdd.setString(9, NP);
			   psAdd.executeUpdate();
			   psAdd.close();
		  } catch (Exception e) {
		   e.printStackTrace();
		  } finally {
			   try {
				  // conn.close();
			   } catch (Exception e) {
				   e.printStackTrace();
			   }
		  }
	}
	
	public static byte[] getImageFromDB(int id) {
		  byte[] byteImg = null;
	
		  try {
			   psGet = conn
			     .prepareStatement("SELECT image FROM images WHERE id = ?");
			   psGet.setInt(1, id);
			   rsGet = psGet.executeQuery();
			   
			   while (rsGet.next()) {
			    byteImg = rsGet.getBytes(1);
			   }
			 
			   return byteImg;
			   
		  } catch (Exception e) {
			  return null;
		  }
	}
	
	public static int getImageId(byte[] byteImg) {
		int imageId = -1;
		
		try {
			   psGetId = conn
			     .prepareStatement("SELECT id FROM images WHERE image = ?");
			   psGetId.setBytes(1, byteImg);
			   rsGetId = psGetId.executeQuery();
			   
			   while (rsGetId.next()) {
			    imageId = rsGetId.getInt(1);
			   }
			 
		  } catch (Exception e) {
			  e.printStackTrace();
		  }
		
		return imageId;
	}
	
	public static void deleteImageFromDB(int id) {
		  
		  try {  
			   psDel = conn
					     .prepareStatement("DELETE FROM images WHERE id = ?");
			   psDel.setInt(1, id);
			   psDel.executeUpdate();
		  } catch (Exception e) {
			   e.printStackTrace();
		  } finally {
			   try {
				  // conn.close();
			   } catch (Exception e) {
				   e.printStackTrace();
			   }
		  }
	}
	
	public static int getFirstId() {
		int firstId;
		try {
			   psFirst = conn
			     .prepareStatement("SELECT id FROM images");
			   rsFirst = psFirst.executeQuery();
			   rsFirst.next();
			   
			   firstId = rsFirst.getInt(1);
			   
			   return firstId;
			   
		  } catch (Exception e) {
			  e.printStackTrace();
			  return 0;
		  }
	}
	
	public static int getLastId() {
		int lastId = 0;
		try {
			psLast = conn
				     .prepareStatement("SELECT id FROM images");
			rsLast = psLast.executeQuery();
			while(rsLast.next()) {
				rsLast.next();
				lastId = rsLast.getInt(1);
			}
			
			return lastId;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return 0;
		}
		
		
	}

	public static int[] findSimilarImages1(int[] histo) {
		int[] result = new int[100];
		int[] dbHisto;
		int compareResult = 0;
		String[] dbHistograms;
		cmpResult = 0;
		int sum = 0;
		
		try {
			psHistograms = conn.prepareStatement("SELECT * FROM images");
			rsHistograms = psHistograms.executeQuery();
			rsHistograms.next();
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		
		
		for (int i=0; i<histo.length; i++) {
			sum += histo[i];
		}

		try {
			for (int i=0; i<ConnectDB.rs.getInt(1); i++) {
				dbHistograms = new String[ConnectDB.rsHistograms.getString(3).length()];
				dbHisto = new int[ConnectDB.rsHistograms.getString(3).length()];
				
				dbHistograms[i] = ConnectDB.rsHistograms.getString(3);
				String tempStr = "";
				int tempInt = 0;
				int cmpRGB = 0;
				for (int j=0; j<dbHistograms[i].length(); j++) {	
					if (dbHistograms[i].charAt(j) != ',') {
						tempStr += dbHistograms[i].charAt(j);
					} else {
						cmpRGB++;
						tempInt = Integer.parseInt(tempStr);
						dbHisto[cmpRGB] = tempInt;
						tempStr = "";
					}	
				}
				compareResult = ImageProcessing.compareHistograms1(histo, dbHisto);
				//System.out.println(Math.abs(sum-compareResult));
				
				if (Math.abs(sum-compareResult) < 250000) {
					result[cmpResult] = ConnectDB.rsHistograms.getInt(1);
					cmpResult++;
					
				}
				ConnectDB.rsHistograms.next();	
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}

	public static int[] findSimilarImages2(double[][] selectedImageHisto) {
		double[][] dbHisto = new double[256][3];
		String[] dbHistogramsR, dbHistogramsG, dbHistogramsB;
		int[] result = new int[100];
		double compareResult = 0;
		cmpResult = 0;
		
		try {
			psHistograms = conn.prepareStatement("SELECT * FROM images");
			rsHistograms = psHistograms.executeQuery();
			rsHistograms.next();
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
				
		try {
			for (int i=0; i<ConnectDB.rs.getInt(1); i++) {
				dbHistogramsR = new String[ConnectDB.rsHistograms.getString(4).length()];
				dbHistogramsR[i] = ConnectDB.rsHistograms.getString(4);
				
				dbHistogramsG = new String[ConnectDB.rsHistograms.getString(5).length()];
				dbHistogramsG[i] = ConnectDB.rsHistograms.getString(5);
				
				dbHistogramsB = new String[ConnectDB.rsHistograms.getString(6).length()];
				dbHistogramsB[i] = ConnectDB.rsHistograms.getString(6);
				
				String tempStr = "";
				double tempInt = 0;
				int cmpR=0, cmpG=0, cmpB=0;
				
				for (int j=0; j<dbHistogramsR[i].length(); j++) {	
					
					if (dbHistogramsR[i].charAt(j) != ',') {
						tempStr += dbHistogramsR[i].charAt(j);
					} else {
						tempInt = Double.parseDouble(tempStr);
						dbHisto[cmpR][0] = tempInt;
						cmpR++;
						tempStr = "";
					}
				}
				
				tempStr = "";
				tempInt = 0;
				
				for (int j=0; j<dbHistogramsG[i].length(); j++) {		
					if (dbHistogramsG[i].charAt(j) != ',') {
						tempStr += dbHistogramsG[i].charAt(j);
					} else {
						tempInt = Double.parseDouble(tempStr);
						dbHisto[cmpG][1] = tempInt;
						cmpG++;
						tempStr = "";
					}
				}
				
				tempStr = "";
				tempInt = 0;
				
				for (int j=0; j<dbHistogramsB[i].length(); j++) {		
					if (dbHistogramsB[i].charAt(j) != ',') {
						tempStr += dbHistogramsB[i].charAt(j);
					} else {
						tempInt = Double.parseDouble(tempStr);
						dbHisto[cmpB][2] = tempInt;
						cmpB++;
						tempStr = "";
					}
				}
				compareResult = ImageProcessing.compareHistograms2(selectedImageHisto, dbHisto);
				//System.out.println(compareResult);
				
				if (compareResult < 6) {
					result[cmpResult] = ConnectDB.rsHistograms.getInt(1);
					cmpResult++;
				}

				ConnectDB.rsHistograms.next();	
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public static int[] findSimilarImages3(int[][] selectedImageHisto) {
		int nBins = ImageProcessing.nBins;
		int[][] dbHisto = new int[nBins*nBins*nBins][4];
		String[] dbHistogramsR, dbHistogramsG, dbHistogramsB, dbHistogramsNP;
		int[] result = new int[100];
		double compareResult = 0;
		cmpResult = 0;
		
		try {
			psHistograms = conn.prepareStatement("SELECT * FROM images");
			rsHistograms = psHistograms.executeQuery();
			rsHistograms.next();
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
				
		try {
			for (int i=0; i<ConnectDB.rs.getInt(1); i++) {
				dbHistogramsR = new String[ConnectDB.rsHistograms.getString(7).length()];
				dbHistogramsR[i] = ConnectDB.rsHistograms.getString(7);
				
				dbHistogramsG = new String[ConnectDB.rsHistograms.getString(8).length()];
				dbHistogramsG[i] = ConnectDB.rsHistograms.getString(8);
				
				dbHistogramsB = new String[ConnectDB.rsHistograms.getString(9).length()];
				dbHistogramsB[i] = ConnectDB.rsHistograms.getString(9);
				
				dbHistogramsNP = new String[ConnectDB.rsHistograms.getString(10).length()];
				dbHistogramsNP[i] = ConnectDB.rsHistograms.getString(10);
				
				String tempStr = "";
				int tempInt = 0;
				int cmpR=0, cmpG=0, cmpB=0, cmpNP=0;
				
				for (int j=0; j<dbHistogramsR[i].length(); j++) {	
					
					if (dbHistogramsR[i].charAt(j) != ',') {
						tempStr += dbHistogramsR[i].charAt(j);
					} else {
						tempInt = Integer.parseInt(tempStr);
						dbHisto[cmpR][0] = tempInt;
						cmpR++;
						tempStr = "";
					}
				}
				
				tempStr = "";
				tempInt = 0;
				
				for (int j=0; j<dbHistogramsG[i].length(); j++) {		
					if (dbHistogramsG[i].charAt(j) != ',') {
						tempStr += dbHistogramsG[i].charAt(j);
					} else {
						tempInt = Integer.parseInt(tempStr);
						dbHisto[cmpG][1] = tempInt;
						cmpG++;
						tempStr = "";
					}
				}
				
				tempStr = "";
				tempInt = 0;
				
				for (int j=0; j<dbHistogramsB[i].length(); j++) {		
					if (dbHistogramsB[i].charAt(j) != ',') {
						tempStr += dbHistogramsB[i].charAt(j);
					} else {
						tempInt = Integer.parseInt(tempStr);
						dbHisto[cmpB][2] = tempInt;
						cmpB++;
						tempStr = "";
					}
				}
				
				tempStr = "";
				tempInt = 0;
				
				for (int j=0; j<dbHistogramsNP[i].length(); j++) {		
					if (dbHistogramsNP[i].charAt(j) != ',') {
						tempStr += dbHistogramsNP[i].charAt(j);
					} else {
						tempInt = Integer.parseInt(tempStr);
						dbHisto[cmpNP][3] = tempInt;
						cmpNP++;
						tempStr = "";
					}
				}
				
				compareResult = ImageProcessing.compareHistograms3(selectedImageHisto, dbHisto);
				//System.out.println(compareResult);
				
				if (compareResult < 600) {
					result[cmpResult] = ConnectDB.rsHistograms.getInt(1);
					cmpResult++;
				}

				ConnectDB.rsHistograms.next();	
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}

}
