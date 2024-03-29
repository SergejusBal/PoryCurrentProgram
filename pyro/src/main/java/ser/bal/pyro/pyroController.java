package ser.bal.pyro;

import java.io.File;
import java.io.FileWriter;
import java.time.LocalDateTime;
import java.util.ArrayList;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

public class pyroController {

	@FXML
	private Button startButton;
	@FXML
	private Button stopButton; 
	@FXML
	private Button pauseButton; 
	@FXML
	private Button iniKeithley2700Button; 
	@FXML
	private Button reKeithley2700Button; 
	@FXML
	private Button iniKeithley6514Button; 
	@FXML
	private Button reKeithley6514Button; 
	@FXML
	private Button saveFile;
	@FXML
	private Label statusLabel;
	@FXML
	private Label labelSpeed;	
	int count=0;
	Double counttemp=300.0;
	Long countstarttime=1000000000L;
	Double speedV =0.0;
	@FXML
	private Label labelK;
	@FXML
	private Label labelA;
	@FXML
	ChoiceBox <String> portChoiceBar1;
	String port2700V;
	@FXML
	ChoiceBox <String> portChoiceBar2;
	String port6514V;
	@FXML
	private TextField saveFileTexT;		
	
	@FXML
	private TextField sampleArea;
	@FXML
	private TextField sampleThickness;
	@FXML
	private Rectangle keythley2700rec;
	@FXML
	private Rectangle keythley6514rec;
	@FXML
	NumberAxis xAxis = new NumberAxis();
	@FXML
	NumberAxis yAxis = new NumberAxis();
	@FXML
	private  LineChart <Number, Number> lineChart = new LineChart<Number,Number>(xAxis,yAxis);;
	XYChart.Series <Number, Number> temCurrent = new XYChart.Series <Number, Number>();;
	
	
	
	RS232coms keithley2700;
	RS232coms keithley6514;
	Long startTime;
	File file;	
	
	boolean running = false;
	boolean stopped = true;
	boolean paused = false;
	boolean saved = false;
	boolean inK2700 = false;
	boolean inK6514 = false;
	boolean info = true;
	
	Double sampleAreaV;
	Double sampleThicknessV;	
	
	@FXML
	private void initialize() {
		 reKeithley2700Button.setDisable(true); 
		 reKeithley6514Button.setDisable(true); 
		 startButton.setDisable(true);
		 stopButton.setDisable(true);
		 pauseButton.setDisable(true);
		 saveFileTexT.setText("No file Selected");
		 saveFileTexT.setDisable(true);
		 
		 		
		 lineChart.getData().add(temCurrent);
		 lineChart.setStyle("-fx-font-size: 14px; -fx-font-family: 'Times New Roman';");
		 lineChart.setTitle("Pyrocurrent Measurement");
		 lineChart.setCreateSymbols(false);
		 lineChart.setAnimated(false);
		 lineChart.setLegendVisible(false);
		 
		 xAxis.setStyle("-fx-font-size: 16px; -fx-font-family: 'Times New Roman';  -fx-font-style: italic;");
		 xAxis.setLabel("Temperature, K");
		 yAxis.setStyle("-fx-font-size: 16px; -fx-font-family: 'Times New Roman';  -fx-font-style: italic;");
		 yAxis.setLabel("Current, nA");
		 ArrayList <String> allPorts = RS232coms.scanPorts();
		 
		 portChoiceBar1.getItems().addAll(allPorts);
		 portChoiceBar2.getItems().addAll(allPorts);
	}
	
	
	//__________________________________________________________
	// Start method is the main method that saves and reads data
	public void start() {
	
		//restarts timer so it ticks from 1 this is needed as we can pause writing, but timer will tick
		if(stopped) { 
			startTime = System.nanoTime();
			stopped = false;
			}
		
		// set all TextFields and buttons to disabled also set program to running for a while loop ahead. 
		statusLabel.setText("Status: measuring");
		running = true;
			
		sampleArea.setDisable(true);
		sampleThickness.setDisable(true);		
		stopButton.setDisable(false);
		pauseButton.setDisable(false);
		startButton.setDisable(true);
		
		
		// try and catch block that checks parameters 
		try {
			sampleAreaV = Double.parseDouble(sampleArea.getText());
			sampleThicknessV =Double.parseDouble(sampleThickness.getText());
			
			}
			catch(NumberFormatException ex) {
				Alert alert = new Alert(AlertType.ERROR);
				alert.setTitle("Error");
				alert.setHeaderText("There is an error with program Operator, please change Operator!?");
				alert.showAndWait();
				stop();
			}
			catch(Exception ex) {
				Alert alert = new Alert(AlertType.ERROR);
				alert.setTitle("Error");
				alert.setHeaderText("Unknown Error: "+ex.toString());
				alert.showAndWait();
				stop();
			}
		
		
		
		// Separate tread for writing and calculating so UI would for normally.
		Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                
            	while (running) {
            		Thread.sleep(100);
                    Double temp = keithley2700.getTemperature();	 //get values from Keithley2700 using RS232
                    Thread.sleep(100);
                   // System.out.print(temp);
                    Double curr = keithley6514.getCurrent();
                   // System.out.print("     "+curr);// get values from Keithley2700 using RS232
            		Long time  = (System.nanoTime() - startTime)/1000000; // set time values to milliseconds
            		//System.out.println("     "+time);
            		// FileWhriter writes data
            		try {
            			FileWriter writer = new FileWriter(file,true);
            			if(info) writer.append("\n"+LocalDateTime.now()+" \n"+
            								   sampleAreaV+" mm^2 "+sampleThicknessV+" mm\n\n");
            			
            			
            			writer.append((double)time/1000 +" "+temp+" "+curr+"\n");
            			writer.close();
            			info = false;
            		} 
            		catch(Exception ex) {
            			Alert alert = new Alert(AlertType.ERROR);
        				alert.setTitle("Error");
        				alert.setHeaderText("Unknown Error: "+ex.toString());
        				alert.showAndWait();
        				stop();
            		}
            	  //Calculates measurement speed
            	   count++;
             	   Long counttime = System.nanoTime();
             	                	   
             	   if(count==10) { 
             	   speedV = (Double) (temp-counttemp)/(counttime-countstarttime)*1000000000*60;                		  
           		   counttemp=temp;
           		   countstarttime=counttime;           		              		   
           		   count=0;
             	   }             	   
                    
            		// Updates parameters to UI
                   Platform.runLater(() -> {
                	   //Adds points to chart
                	   temCurrent.getData().add(new XYChart.Data<Number, Number>(temp, curr*1000000000));
                	   
                	   // Shows current measure values
                	   labelK.setText(temp+" K");
                	   labelA.setText(curr+" A");
                	   labelSpeed.setText(String.format("%.2f", speedV)+" K/min");    //          	               	                   	   
                	  
                    });
                }
                return null;
            }
        };

        // Start the task on a new thread that is described above.
        new Thread(task).start();
	}
		
	//___________________________________________________
	// Stop method restarts the timer
	public void stop() {
		
		statusLabel.setText("Status: measurment is stopped");
		if(inK6514 && inK2700) startButton.setDisable(false); // resets start button. 
		
		//resets all parameters to default
		running = false;
		stopped = true;
		
		sampleThickness.setDisable(false);
		sampleArea.setDisable(false);
		stopButton.setDisable(true);
		pauseButton.setDisable(true);
		startButton.setText("Start");
		
	}
	
	//___________________________________________________
	// Pause method that pauses the timer
	public void pause() {
		
		running = false;
		statusLabel.setText("Status: measurment is paused");
		pauseButton.setDisable(true);
		startButton.setDisable(false);
		startButton.setText("Continue");
		
	}
	
	//____________________________________________________
	//Save File
	public void selectFile() {
		try {
			 // FileChooser chooses that path and name of our file;
			 FileChooser fileChooser = new FileChooser();
			 fileChooser.setTitle("Save File");
			 fileChooser.getExtensionFilters().addAll(
			 new ExtensionFilter("Text Files", "*.txt"));
			 
			 Stage stage = (Stage) saveFile.getScene().getWindow(); // navigates to primary stage
			 file = fileChooser.showSaveDialog(stage); //Disables stage during file selection
			 
			 //If file is selected properly others parameters are set
			 if (file != null) {
				statusLabel.setText("Status: file selected");
				saved=true;
				saveFileTexT.setText(file.getAbsolutePath());
				if(inK6514 && inK2700) startButton.setDisable(false);
				}
		}
		catch(Exception ex) {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Error");
			alert.setHeaderText("Unknown Error: "+ex.toString());
			alert.showAndWait();
		}
	}
	
	//____________________________________________________
	//Save initialise Keithley2700
	public void initializeKeithley2700() {
		
		// Set parameters if communication was successful;
		portChoiceBar1.setDisable(true);       
		statusLabel.setText("Status: Keithley2700 is initialized");
		keythley2700rec.setFill(Color.GREEN);
		iniKeithley2700Button.setDisable(true);
		reKeithley2700Button.setDisable(false);
		inK2700 = true;
		if(inK6514 && saved) startButton.setDisable(false);
		
		// try and catch block that checks parameters 
			try {			
				port2700V = portChoiceBar1.getValue().toString();			
				}
				catch(Exception ex) {
					Alert alert = new Alert(AlertType.ERROR);
					alert.setTitle("Error");
					alert.setHeaderText("Unknown Error: "+ex.toString());
					alert.showAndWait();
					releaseKeithley2700();
				}		

		keithley2700 = new RS232coms(port2700V);
		//Separate tread for RS232 communication.
	
		
		Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
            	String k2700 = keithley2700.quary("*IDN?\n");
            	System.out.println(k2700); 
            	                
             	keithley2700.write("SENS:FUNC\s'TEMP'\n"+             	
             					   ";UNIT:TEMP\sK\n"+             	
             					   ";SENS:TEMP:TRANsducer\sFRTD\n"+             
             					   ";SENS:TEMP:FRTD:TYPE\sPT100\n"+             	
             					   ";SYST:RWLock\n");
             	
                Platform.runLater(() -> {
                	   if(!k2700.contains("MODEL 2700")) {
                		   releaseKeithley2700();
                		   keithley2700.write("*RST\n"+";SYST:lOC\n");                		   
                		   Alert alert = new Alert(AlertType.ERROR);
                		   alert.setTitle("Error");
                		   alert.setHeaderText(port2700V + " is connected too: ");
                		   alert.setContentText(k2700);
                		   if(k2700.isBlank()) {alert.setContentText("NO DEVICE FOUND.");} 
                		   alert.showAndWait();                		   
                	   }
                	   
                    });

                return null;
            }
        };
      
        new Thread(task).start();
		}
	
	//____________________________________________________
	//Releases Keithley2700 to reset parameters
	public void releaseKeithley2700() {
		portChoiceBar1.setDisable(false);	
		statusLabel.setText("Status: Keithley2700 is released");
		keythley2700rec.setFill(Color.RED);
		iniKeithley2700Button.setDisable(false);
		reKeithley2700Button.setDisable(true);
		startButton.setDisable(true);
		stopButton.setDisable(true);
		pauseButton.setDisable(true);
		inK2700 = false;
		if (running) stop();
		keithley2700.write("SYST:lOC\n");
    	 
	}
	
	//____________________________________________________
	//Initialise Keithley6514
	public void initializeKeithley6514() {
		// Set parameters if communication was successful;        
        portChoiceBar2.setDisable(true);
		statusLabel.setText("Status: Keithley6514 is initialized");
		keythley6514rec.setFill(Color.GREEN);
		iniKeithley6514Button.setDisable(true);
		reKeithley6514Button.setDisable(false);
		inK6514 = true;
		if(inK2700 && saved) startButton.setDisable(false);	
						
		// try and catch block that checks parameters 
			try {
				
				port6514V = portChoiceBar2.getValue().toString();		
				}
			catch(Exception ex) {
					Alert alert = new Alert(AlertType.ERROR);
					alert.setTitle("Error");
					alert.setHeaderText("Unknown Error: "+ex.toString());
					alert.showAndWait();
					releaseKeithley6514();
				}
		keithley6514 = new RS232coms(port6514V);
		//Separate tread for RS232 communication.
		Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
            	String k6514 = keithley6514.quary("*IDN?\n");
            	System.out.println(k6514);            	
            	
            	keithley6514.write("CONF:CURR\n"+             	
  					   ";SENS:CURR:RANG\s1e-11\n"+             	
  					   ";SYST:ZCH\sON\n"+             
  					   ";SYST:ZCOR\sON\n"+             	
  					   ";SYST:ZCH\sOFF\n"+
  					   ";SENS:CURR:RANG\s1e-9\n"+
  					   ";SENS:CURR:RANG:AUTO\sON\n"+
  					   ";SYST:RWLock\n"+
  					   ";INIT\n");
            	
	        	Platform.runLater(() -> {
	          	   if(!k6514.contains("MODEL 6514")) {
	          		   releaseKeithley6514();
	          		   keithley6514.write("*RST\n"+";SYST:lOC\n");                		   
	          		   Alert alert = new Alert(AlertType.ERROR);
	          		   alert.setTitle("Error");
	          		   alert.setHeaderText(port6514V + " is connected too: ");
	          		   alert.setContentText(k6514);
	          		   if(k6514.isBlank()) {alert.setContentText("NO DEVICE FOUND.");} 
	          		   alert.showAndWait();                		   
	          	   }
	          	   
	              });
            	 return null;
            }
        };
       
        new Thread(task).start();
             		
	}
	
	//____________________________________________________
	//Releases Keithley2700 to reset parameters
	public void releaseKeithley6514() {		
		portChoiceBar2.setDisable(false);
		statusLabel.setText("Status: Keithley6514 is released");
		keythley6514rec.setFill(Color.RED);
		iniKeithley6514Button.setDisable(false);
		reKeithley6514Button.setDisable(true);
		startButton.setDisable(true);
		stopButton.setDisable(true);
		pauseButton.setDisable(true);
		inK6514 = false;
		if (running) stop();
		keithley6514.write("SYST:lOC\n");
	}
}
