package comporttest;

import java.util.Arrays;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;

public final class DataListener implements SerialPortDataListener {

	SerialReadByEvents mainApp;
	SerialPort comPort;
	
	public DataListener(SerialReadByEvents serial ) {
		this.mainApp = serial;
		this.comPort = serial.getComPort();
	}
	
	@Override
	public int getListeningEvents() { return SerialPort.LISTENING_EVENT_DATA_AVAILABLE; }
	
	
	double indoorTemperatureIndegC = 0;
	double outdoorTemperatureIndegC = 0;
	int indoorHumidity = 0;
	int outdoorHumidity = 0;
	double windspeed = 0;
	int solar = 0;
	
	
	@Override
	public void serialEvent(SerialPortEvent event) {
		try {
			if (event.getEventType() != SerialPort.LISTENING_EVENT_DATA_AVAILABLE)
				return;
			byte[] newData = new byte[comPort.bytesAvailable()];
			int numRead = comPort.readBytes(newData, newData.length);
			//System.out.print("Read " + numRead + " bytes: ");
			//System.out.println(bytesToHex(newData));
			
			int prevIndex = 0;
			for(int i = 0; i<numRead; i++) {
				if(newData[i] == 0x06) {
					prevIndex = i;
					//System.out.println("Received Ack");
				} else if(Arrays.equals(Arrays.copyOfRange(newData,i,i+3), "LOO".getBytes())) {
				//} else if((i < numRead-3) && newData[i] == "L".getBytes()[0] && newData[i+1] == "O".getBytes()[0] && newData[i+2] == "O".getBytes()[0]) {
					//System.out.println("We found a LOOP packet Ack");
					//System.out.println(bytesToHex(Arrays.copyOfRange(newData, i, i+99)));
					
					int temperature =  ((newData[i+10] & 0xff) << 8) | (newData[i+9] & 0xff);//bytes 9 and 10
					indoorTemperatureIndegC = ((temperature/10.0)-32)*5/9;
					temperature = ((newData[i+13] & 0xff) << 8) | (newData[i+12] & 0xff);
					outdoorTemperatureIndegC = ((temperature/10.0)-32)*5/9;
					
					indoorHumidity =  (newData[i+11] & 0xff);
					outdoorHumidity =  (newData[i+33] & 0xff);
					
					windspeed = (newData[i+14] & 0xff)*16.09344/36 ;
					
					solar = ((newData[i+45] & 0xff) << 8) | (newData[i+44] & 0xff);
					
	
					System.out.println("indoor temperature is: " + indoorTemperatureIndegC);
					System.out.println("outdoor temperature is: " + outdoorTemperatureIndegC);
					System.out.println("indoor humidity is: " + indoorHumidity);
					System.out.println("outdoor humidity is: " + outdoorHumidity);
					System.out.println("Windspeed: " + windspeed);
					System.out.println("solar radiation: " + solar);
					
					mainApp.changed();
					
					
				} else if(newData[i] == "\n".getBytes()[0]) {
					byte [] packet = Arrays.copyOfRange(newData, prevIndex, i);
					prevIndex = i;
					//System.out.println(bytesToHex(packet));
				}
			}
		} catch (Exception err) {
			err.printStackTrace();
		}
	}
	
	public double getIndoorTemperatureIndegC() {
		return indoorTemperatureIndegC;
	}

	public void setIndoorTemperatureIndegC(double indoorTemperatureIndegC) {
		this.indoorTemperatureIndegC = indoorTemperatureIndegC;
	}

	public double getOutdoorTemperatureIndegC() {
		return outdoorTemperatureIndegC;
	}

	public void setOutdoorTemperatureIndegC(double outdoorTemperatureIndegC) {
		this.outdoorTemperatureIndegC = outdoorTemperatureIndegC;
	}

	public int getIndoorHumidity() {
		return indoorHumidity;
	}

	public void setIndoorHumidity(int indoorHumidity) {
		this.indoorHumidity = indoorHumidity;
	}

	public int getOutdoorHumidity() {
		return outdoorHumidity;
	}

	public void setOutdoorHumidity(int outdoorHumidity) {
		this.outdoorHumidity = outdoorHumidity;
	}

	public double getWindspeed() {
		return windspeed;
	}

	public void setWindspeed(double windspeed) {
		this.windspeed = windspeed;
	}


	public int getSolar() {
		return solar;
	}

	public void setSolar(int solar) {
		this.solar = solar;
	}


	private final static char[] hexArray = "0123456789ABCDEF".toCharArray();
	public static String bytesToHex(byte[] bytes) {
	    char[] hexChars = new char[bytes.length * 2];
	    for ( int j = 0; j < bytes.length; j++ ) {
	        int v = bytes[j] & 0xFF;
	        hexChars[j * 2] = hexArray[v >>> 4];
	        hexChars[j * 2 + 1] = hexArray[v & 0x0F];
	    }
	    return new String(hexChars);
	}
	
}
