package ser.bal.pyro;

import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.util.ArrayList;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import purejavacomm.CommPortIdentifier;
import purejavacomm.NoSuchPortException;
import purejavacomm.PortInUseException;
import purejavacomm.SerialPort;
import purejavacomm.UnsupportedCommOperationException;

public class RS232coms {
	
	
	
	String portName;	
	 
	public RS232coms(String portName) {
		 this.portName = portName;
	}

	public static ArrayList<String> scanPorts() {
		 ArrayList<String> ports = new ArrayList<String>();
		 try {
			    
			 	//Returns all available communication that can purejavacomms library detect
	            java.util.Enumeration<CommPortIdentifier> portEnum = CommPortIdentifier.getPortIdentifiers();
	            
	            
	            while (portEnum.hasMoreElements()) {
	            	//We assign each port element to a portIndefier object.
	                CommPortIdentifier portIdentifier = portEnum.nextElement();
	                // Check if the port is a serial port
	                if (portIdentifier.getPortType() == CommPortIdentifier.PORT_SERIAL) {
	                 //   System.out.println("Found Serial Port: " + portIdentifier.getName());
	                    ports.add(portIdentifier.getName());	                    
	                }
	            }
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
		return ports;
	 }
	
	public String quary(String command) {
		 StringBuilder response = new StringBuilder();
		 SerialPort serialPort = null;
	 
		 try {
			 	//Port indentifier selects the port we named
				CommPortIdentifier portIdentifier = CommPortIdentifier.getPortIdentifier(portName);
				//opening the port and setting it to the object port the name "Piro" is for other programs to read if it is in use and 2000 time uot.
				serialPort = (SerialPort) portIdentifier.open("Piro", 2000);
				
				//Setting rs232 com parameters
				serialPort.setSerialPortParams(9600, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
				//serialPort.setFlowControlMode(SerialPort.FLOWCONTROL_XONXOFF_IN | SerialPort.FLOWCONTROL_XONXOFF_OUT);
				serialPort.setFlowControlMode(SerialPort.FLOWCONTROL_NONE);	
				serialPort.enableReceiveTimeout(1000); // Set timeout for read operations
		        serialPort.enableReceiveThreshold(0);
				
				//Setting output and input streams.
				OutputStream outputStream = serialPort.getOutputStream();
				InputStream inputStream = serialPort.getInputStream();
				
				outputStream.write(command.getBytes());
			//	System.out.println("Sent: " + command);
								
				int bytesRead=-1;// = inputStream.read(buffer);
				//break while loop if it is longer than 1 s.
				long timeOut = System.nanoTime();
				
				while ((char)bytesRead !='\n'){
					bytesRead = inputStream.read();
			       // System.out.print(bytesRead);
			        if (bytesRead!=-1) response.append((char)bytesRead);
			        if (System.nanoTime()-timeOut>1000000000L) {
			        	System.out.println("\n"+"breaking"+"\n");
			        	break;			        	
			        	} //breaks loop if it takes to long
			    }
				inputStream.close();
				//outputStream.write("SYST:LOC\n".getBytes());				
				
				
				} catch (NoSuchPortException e) {
		            System.err.println("Port " + portName + " not found.");	
		            Platform.runLater(() -> {
			            Alert alert = new Alert(AlertType.ERROR);
			    		alert.setTitle("Error");
			    		alert.setHeaderText("Port " + portName + " not found.");
			    		alert.showAndWait();
			    		});
		        } catch (PortInUseException e) {
		            System.err.println("Port " + portName + " is in use.");		            
		            Platform.runLater(() -> {
		            Alert alert = new Alert(AlertType.ERROR);
			    		alert.setTitle("Error");
			    		alert.setHeaderText("Port " + portName + " is in use.");
			    		alert.showAndWait();
			    		});
		        } catch (UnsupportedCommOperationException e) {
		            System.err.println("Failed to read from or write to the serial port.");
		            Platform.runLater(() -> {
			            Alert alert = new Alert(AlertType.ERROR);
			    		alert.setTitle("Error");
			    		alert.setHeaderText("Failed to read from or write to the serial port.");
			    		alert.showAndWait();
			    		});
		        } catch (InterruptedIOException e) {  
		        	System.err.println("Time out");	
		        	Platform.runLater(() -> {
			            Alert alert = new Alert(AlertType.ERROR);
			    		alert.setTitle("Error");
			    		alert.setHeaderText("Time out");
			    		alert.showAndWait();
			    		});
		        } catch (IOException e) {
		  	          
		        } finally {
		            if (serialPort != null) {
		            	serialPort.close();		                
		            }
		        }
		return response.toString();
		   	 
     }

	public void write(String command) {
		  
		 SerialPort serialPort = null;
		 try {
			 	//Port indentifier selects the port we named
				CommPortIdentifier portIdentifier = CommPortIdentifier.getPortIdentifier(portName);
				//opening the port and setting it to the object port the name "Piro" is for other programs to read if it is in use and 2000 time uot.
				serialPort = (SerialPort) portIdentifier.open("Piro", 2000);
				
				//Setting rs232 com parameters
				serialPort.setSerialPortParams(9600, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
				serialPort.setFlowControlMode(SerialPort.FLOWCONTROL_XONXOFF_IN | SerialPort.FLOWCONTROL_XONXOFF_OUT);
				
				serialPort.enableReceiveTimeout(1000); // Set timeout for read operations
		        serialPort.enableReceiveThreshold(0);
				
				//Setting uotput and imput streams.
				OutputStream outputStream = serialPort.getOutputStream();
				outputStream.write(command.getBytes());						
				//outputStream.write("SYST:LOC\n".getBytes());				
						
								
				} catch (NoSuchPortException e) {
		            System.err.println("Port " + portName + " not found.");
		        } catch (PortInUseException e) {
		            System.err.println("Port " + portName + " is in use.");
		        } catch (UnsupportedCommOperationException e) {
		            System.err.println("Failed to read from or write to the serial port.");
		        } catch (IOException e) {
		  	          
		        } finally {
		            if (serialPort != null) {
		            	serialPort.close();		                
		            }
		        }
		   	 
     }

	public Double getTemperature() {
		String meas = this.quary("FETch?\n");		
		return Double.parseDouble(meas);
	}
	public Double getCurrent() {
		String meas = this.quary("READ?\n");		
		return Double.parseDouble(meas.replaceAll(",.*$", ""));
	}
		 
}
