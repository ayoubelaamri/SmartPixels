package application;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import javax.imageio.ImageIO;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXRadioButton;

import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;

public class Controller {
	
	@FXML private ImageView btnClose;
	@FXML private Button btnGetStarted;
	
	@FXML private ImageView selector;
	
	@FXML private Button btnSelectImage;
	@FXML private ImageView showSelectedImage;
	@FXML private Pane ShowLargePane2;
	@FXML private Button btnFind;
	@FXML private JFXRadioButton method1;
	@FXML private JFXRadioButton method2;
	@FXML private JFXRadioButton method3;
	
	@FXML private Pane SearchEnginePane;
	@FXML private Pane SimilarImagesPane;
	@FXML private HBox similarImages;
	@FXML private ImageView showOne2;
	@FXML private JFXButton btnMaximizeOne2;
	@FXML private JFXButton btnSaveOne2;
	@FXML private JFXButton btnDeleteOne2;
	@FXML private ImageView largeView2;

	@FXML private Pane ShowAllPane;
	@FXML private Pane ShowLargePane;
	@FXML private JFXButton btnShowAll;
	@FXML private HBox allImages;
	@FXML private JFXButton btnDeleteAll;
	@FXML private ImageView showOne;
	@FXML private JFXButton btnMaximizeOne;
	@FXML private JFXButton btnSaveOne;
	@FXML private JFXButton btnDeleteOne;
	@FXML private ImageView largeView;
	
	@FXML private Button btnSelectImage2;
	@FXML private ImageView showSelectedImage2;
	@FXML private Button btnInsert;
	
	private static File selectedImage;
	private static BufferedImage selectedbufImg = null;
	int selectedMethod = 0;
	
	//==| Scenes Changing |===============================================================
	
	public void Exit() {
		System.out.println("Closing the application ..");
		Platform.exit();
	}
	
	public void GetStarted() {
		App.root.setCenter(App.search);
		App.window.setScene(App.AppScene);
		
	} 
	
	public void getSearchEngine() {
		selector.setY(0);
		App.root.setCenter(App.search);
	}
	
	public void backToSearchEngine() {
		SimilarImagesPane.setVisible(false);
		ShowLargePane2.setVisible(false);
		SearchEnginePane.setVisible(true);
	}
	
	public void backToSimilarImages() {
		ShowLargePane2.setVisible(false);
		SearchEnginePane.setVisible(false);
		SimilarImagesPane.setVisible(true);
	}
	
	public void getManageDB() { 
		App.root.setCenter(App.manage);
		selector.setY(43);
	}
	
	public void backToManageDB() {
		App.root.setCenter(App.manage);
	}
	
	public void backToManageDB2() {
		ShowLargePane.setVisible(false);
		ShowAllPane.setVisible(true);
	}
	
	public void getInsert() {
		App.root.setCenter(App.insert);
	}

	public void getAbout() {
		selector.setY(86);
		App.root.setCenter(App.about);
	}
	
	public void getTeam() {
		selector.setY(129);
		App.root.setCenter(App.team);
	}
	
	public void getLanding() {
		App.window.setScene(App.LandingScene);
	}
	
	//==| Search Engine |===============================================================
	
	public void SelectImage(ActionEvent e ) {
		System.out.print("Selecting image : ");
		FileChooser fc = new FileChooser();
		fc.setInitialDirectory(new File("C:\\Users\\Senor\\Desktop"));
		selectedImage = fc.showOpenDialog(null);
		
		if (selectedImage != null) {
			try {
				selectedbufImg = ImageIO.read(selectedImage);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			if (e.getSource() == btnSelectImage) {
				Image image = new Image(selectedImage.toURI().toString());
				showSelectedImage.setImage(image);
				btnFind.setDisable(false);

			}
			if (e.getSource() == btnSelectImage2) {
				Image image2 = new Image(selectedImage.toURI().toString());
				showSelectedImage2.setImage(image2);
				btnInsert.setDisable(false);
			}
			System.out.println("Image selected !");
		} else {
			System.out.println("No Image selected !");
		}
	}
	
	public void SetMethod(ActionEvent e) {
		if (e.getSource() == method1) {
			selectedMethod = 1;
		}
		if (e.getSource() == method2) {
			selectedMethod = 2;
		}
		if (e.getSource() == method3) {
			selectedMethod = 3;
		}
	}
	
	public void ShowSimilar() {
		
		Image image = null;
		BufferedImage bufImg = null;
		byte[] byteImage;
		int[] result = new int[100];
		
		similarImages.getChildren().clear();
		showOne2.setImage(new Image("file:///../images/blackImage2.png"));
		
		
		if (selectedMethod == 1) {
			System.out.println("Use Method 1 .. ");
			result = ConnectDB.findSimilarImages1(ImageProcessing.computeHistogram1(selectedbufImg));
		} else if (selectedMethod == 2) {
			System.out.println("Use Method 2 .. ");
			result = ConnectDB.findSimilarImages2(ImageProcessing.computeHistogram2(selectedbufImg));
		} else {
			System.out.println("Use Method 3 .. ");
			result = ConnectDB.findSimilarImages3(ImageProcessing.computeHistogram3(selectedbufImg));
		}
		
		System.out.print("Looking for similar images : ");
		try {
			if (ConnectDB.rs.getInt(1) > 0) {
				int i = 0;
				while (i < ConnectDB.cmpResult) {
					byteImage = ConnectDB.getImageFromDB(result[i]);
					try {
						bufImg = ImageProcessing.ByteToImage(byteImage);
						image = SwingFXUtils.toFXImage(bufImg, null);
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					ImageView imgView = new ImageView();
					imgView.setImage(image);
					imgView.setPreserveRatio(true);
					imgView.setFitWidth(100);
					
					final JFXButton imgBtn = new JFXButton();
					imgBtn.setGraphic(imgView);
					imgBtn.addEventHandler(ActionEvent.ACTION, new EventHandler<ActionEvent>() {
			            @Override
			            public void handle(ActionEvent event) {
			            	showOne2.setImage(imgView.getImage());
			            	btnMaximizeOne2.setDisable(false);
			            	btnSaveOne2.setDisable(false);
			            	btnDeleteOne2.setDisable(false);
			            }
			        });
					
					similarImages.getChildren().addAll(imgBtn);
					
					i++;
				}
				
				System.out.println(ConnectDB.cmpResult + " images found.");
				
				SearchEnginePane.setVisible(false);
				SimilarImagesPane.setVisible(true);
				
			} else {
				Alert alert = new Alert(AlertType.ERROR);
		        alert.setTitle("Empty Database !");
		        alert.setHeaderText(null);
		        alert.setContentText("There is no images in the Database !");
		        alert.showAndWait();
			}
		} catch (SQLException exc) {
			exc.printStackTrace();
		}
	}
	
	//==| Manage DB |===================================================================
	
	public void ShowAll() {
		Image image = null;
		BufferedImage bufImage = null;
		byte[] byteImage = new byte[1024];
		int i = 0;
		
		try {
			App.connection = new ConnectDB();
			System.out.print("Gathering images from the DataBase .. ");
			if (ConnectDB.rs.getInt(1) > 0) {
				
				allImages.getChildren().clear();
				showOne.setImage(new Image("file:///../images/blackImage2.png"));
				btnMaximizeOne.setDisable(true);
            	btnSaveOne.setDisable(true);
            	btnDeleteOne.setDisable(true);
            	
				int firstId = ConnectDB.getFirstId();
				
				while (i < ConnectDB.rs.getInt(1)) {
					byteImage = ConnectDB.getImageFromDB(firstId);
					try {
						bufImage = ImageProcessing.ByteToImage(byteImage);
						image = SwingFXUtils.toFXImage(bufImage, null);
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					
					ImageView imgView = new ImageView();
					imgView.setImage(image);
					imgView.setPreserveRatio(true);
					imgView.setFitWidth(100);
					
					final JFXButton imgBtn = new JFXButton();
					imgBtn.setGraphic(imgView);
					imgBtn.addEventHandler(ActionEvent.ACTION, new EventHandler<ActionEvent>() {
			            @Override
			            public void handle(ActionEvent event) {
			            	showOne.setImage(imgView.getImage());
			            	btnMaximizeOne.setDisable(false);
			            	btnSaveOne.setDisable(false);
			            	btnDeleteOne.setDisable(false);
			            	
			            }
			        });
					
					allImages.getChildren().addAll(imgBtn);
					
					firstId++;
					i++;
				}
				System.out.println(i + " images found.");
			} else {
				System.out.println("ERROR => Empty Database !!!");
				Alert alert = new Alert(AlertType.ERROR);
		        alert.setTitle("Empty Database !");
		        alert.setHeaderText(null);
		        alert.setContentText("There is no images in the Database !");
		        alert.showAndWait();
			}
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
	}
	
	public void InsertToDB() {
		byte[] byteImg = new byte[1024];
		BufferedImage bufImg = null;
		
		try {
			byteImg = ImageProcessing.ImageToByte(selectedImage);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		try {
			bufImg = ImageIO.read(selectedImage);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	  //-----------------------------------------------------------------------
		int[] histo1 = new int[bufImg.getWidth()*bufImg.getHeight()];
		histo1 = ImageProcessing.computeHistogram1(bufImg); 
		
		StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i<histo1.length; i++) {
            stringBuilder.append(histo1[i]+",");
        }
        String SHisto = stringBuilder.toString();
      //-----------------------------------------------------------------------
        double[][] histo2 = new double[256][3];
		histo2 = ImageProcessing.computeHistogram2(bufImg); 
		
		StringBuilder SBHistoRed = new StringBuilder();
		StringBuilder SBHistoGreen = new StringBuilder();
		StringBuilder SBHistoBlue = new StringBuilder();
		
        for (int i = 0; i<256; i++) {
            SBHistoRed.append(histo2[i][0]+",");
            SBHistoGreen.append(histo2[i][1]+",");
            SBHistoBlue.append(histo2[i][2]+",");
        }
        String SHistoR = SBHistoRed.toString();
        String SHistoG = SBHistoGreen.toString();
        String SHistoB = SBHistoBlue.toString();
      //-----------------------------------------------------------------------
		int nBins = ImageProcessing.nBins;
		int[][] histo3 = new int[nBins*nBins*nBins][4];
		histo3 = ImageProcessing.computeHistogram3(bufImg); 
		
		StringBuilder SBRed = new StringBuilder();
		StringBuilder SBGreen = new StringBuilder();
		StringBuilder SBBlue = new StringBuilder();
		StringBuilder SBNP = new StringBuilder();
		
        for (int i = 0; i<nBins*nBins*nBins; i++) {
            SBRed.append(histo3[i][0]+",");
            SBGreen.append(histo3[i][1]+",");
            SBBlue.append(histo3[i][2]+",");
            SBNP.append(histo3[i][3]+",");
        }
        String SRed = SBRed.toString();
        String SGreen = SBGreen.toString();
        String SBlue = SBBlue.toString();
        String SNP = SBNP.toString();
      //-----------------------------------------------------------------------
        
        App.connection.addImageToDB(byteImg, SHisto, SHistoR, SHistoG, SHistoB, SRed, SGreen, SBlue, SNP);
        System.out.println("Image added successfully.");
        
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Successful");
        alert.setHeaderText(null);
        alert.setContentText("Image successfully added to Database !");
        alert.showAndWait(); 
	}
	
	public void DeleteAll() {
		int i = 0;
		
		try {
			if (ConnectDB.rs.getInt(1) > 0) {
				
				App.connection = new ConnectDB();
				int firstId = ConnectDB.getFirstId();
				
				while (i < ConnectDB.rs.getInt(1)) {
					App.connection.deleteImageFromDB(firstId);
					firstId++;
					i++;
				}
				System.out.println(i + " images deleted.");
			} else {
				Alert alert = new Alert(AlertType.ERROR);
		        alert.setTitle("Empty Database !");
		        alert.setHeaderText(null);
		        alert.setContentText("There is no images in the Database !");
		        alert.showAndWait();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		allImages.getChildren().clear();
		showOne.setImage(new Image("file:///../images/blackImage.png"));
	}

	public void MaximizeView(ActionEvent e) {
		if (e.getSource() == btnMaximizeOne) {
			largeView.setImage(showOne.getImage());
			ShowAllPane.setVisible(false);
			ShowLargePane.setVisible(true);
		}
		if (e.getSource() == btnMaximizeOne2) {
			largeView2.setImage(showOne2.getImage());
			SimilarImagesPane.setVisible(false);
			SearchEnginePane.setVisible(false);
			ShowLargePane2.setVisible(true);
		}
		
	}
	
	public void Download() {
		//File outputFile = new File(System.getProperty("user.home") + "/a DownloadedImage.jpg");
		File outputFile = new File("D:\\SavedImage\\");
	    BufferedImage bImage = SwingFXUtils.fromFXImage(showOne.getImage(), null);
	    try {
	      ImageIO.write(bImage, "jpg", outputFile);
	    } catch (IOException e) {
	      throw new RuntimeException(e);
	    }
	    System.out.println("Image was saved in your Desktop. ");
	}
	
	public void Delete() {
		
	}
}
