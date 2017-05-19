package comporttest;

import java.io.IOException;
import java.util.ArrayList;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.CoapServer;
import org.eclipse.californium.core.coap.CoAP.Type;
import org.eclipse.californium.core.server.resources.CoapExchange;
import org.eclipse.californium.core.server.resources.Resource;

import com.fazecast.jSerialComm.SerialPort;

/**
 * 
 */
public class SerialReadByEvents  {
	SerialPort comPort;
	DavisWeatherStation station;
	
	
  public static void main(String[] argv) {
	  SerialReadByEvents serial;
		
	  //station = new DavisWeatherStation();
	  serial = new SerialReadByEvents();
	  
	  serial.comPort = SerialPort.getCommPorts()[0];
	  serial.comPort.setBaudRate(19200);
	  if(!serial.comPort.openPort()) {
		  System.out.println("Could not open port");
		  System.exit(1);
	  } else {
		  System.out.println("Serial Port is open");
	  }
	  DataListener listener = new DataListener(serial);
	  serial.comPort.addDataListener(listener);

	  serial.station = new DavisWeatherStation(listener);
	  //station.setDataProvider(listener);
	  serial.station.start();
	  
	  try {
		  serial.comPort.writeBytes("\n".getBytes(), 1);
          Thread.sleep(2000);
          
		  serial.comPort.writeBytes("TEST\n".getBytes(), 5);
          Thread.sleep(2000);

          while(true) {
			  serial.comPort.writeBytes("LPS 1 1\n".getBytes(), 8);
			  Thread.sleep(1000);
          }
          
	  } catch (Exception e) { e.printStackTrace(); }
	  serial.comPort.removeDataListener();
	  serial.comPort.closePort();
	  serial.station.stop();
  }
  
  public void changed() {
		station.changed();
	}
  
	public SerialPort getComPort() {
		return comPort;
	}
	
	/* Constructor */
	public SerialReadByEvents() {
	    
	}
}


/**
 * 
 *
 * 
 */
class DavisWeatherStation extends CoapServer{
	
	ArrayList<CoapResource> resourceCollection = new ArrayList<CoapResource>();
	public DavisWeatherStation(DataListener provider) {
		CoapResource baseResource = new CoapResource("weatherstation");

		resourceCollection.add(new IndoorTemperatureResource(provider));
		resourceCollection.add(new OutdoorTemperatureResource(provider));
		resourceCollection.add(new IndoorHumidityResource(provider));
		resourceCollection.add(new OutdoorHumidityResource(provider));
		resourceCollection.add(new WindSpeedResource(provider));
		resourceCollection.add(new SolarRadiationResource(provider));
		
		for(CoapResource childResource : resourceCollection) {
			baseResource.add( childResource);
		}
		
		resourceCollection.add(baseResource);
		add(baseResource);
	}
	
	public void changed() {
		
		for(CoapResource childResource : resourceCollection) {
			childResource.changed();
		}
	}

}

class IndoorTemperatureResource extends CoapResource {
	DataListener provider;
	public IndoorTemperatureResource(DataListener provider) {
		super("indoortemperature");
		this.provider = provider;
		
		//set content type
		//change json payload to up-to-date SenML (no 'e' and upper level is an array and URN basename).
		
//		setObservable(true);
//		getAttributes().setObservable();
//		setObserveType(Type.NON);
	}
	@Override
	public void handleGET(CoapExchange exchange) {
		if(provider!=null) {
			exchange.respond("[{\"bn\":\"urn:dev:mac:tbd\"},{\"n\":indoortemperature,\"v\":" + provider.getIndoorTemperatureIndegC() + ",\"u\":\"degC\"}]");
		} else {
			exchange.respond("nak - provider not available");
		}
	}
}




class OutdoorTemperatureResource extends CoapResource {
	DataListener provider;

	public OutdoorTemperatureResource(DataListener provider) {
		super("outdoortemperature");
		this.provider = provider;
//		setObservable(true);
//		getAttributes().setObservable();
//		setObserveType(Type.NON);
	}

	@Override
	public void handleGET(CoapExchange exchange) {
		if(provider!=null) {
			exchange.respond("[{\"bn\":\"urn:dev:mac:tbd\"},{\"n\":outdoortemperature,\"v\":" + provider.getOutdoorTemperatureIndegC() + ",\"u\":\"degC\"}]");
		} else {
			exchange.respond("nak - provider not available");
		}
	}

}

class IndoorHumidityResource extends CoapResource {
	DataListener provider;

	public IndoorHumidityResource(DataListener provider) {
		super("indoorhumidity");
		this.provider = provider;
//		setObservable(true);
//		getAttributes().setObservable();
//		setObserveType(Type.NON);
	}

	@Override
	public void handleGET(CoapExchange exchange) {
		if(provider!=null) {
			exchange.respond("[{\"bn\":\"urn:dev:mac:tbd\"},{\"n\":indoorhumidity,\"v\":" + provider.getIndoorHumidity() + ",\"u\":\"%RH\"}]");
		} else {
			exchange.respond("nak - provider not available");
		}
	}

}

class OutdoorHumidityResource extends CoapResource {
	DataListener provider;

	public OutdoorHumidityResource(DataListener provider) {
		super("outdoorhumidity");
		this.provider = provider;
//		setObservable(true);
//		getAttributes().setObservable();
//		setObserveType(Type.NON);

	}

	@Override
	public void handleGET(CoapExchange exchange) {
		if(provider!=null) {
			exchange.respond("[{\"bn\":\"urn:dev:mac:tbd\"},{\"n\":outdoorhumidity,\"v\":" + provider.getOutdoorHumidity() + ",\"u\":\"%RH\"}]");
		} else {
			exchange.respond("nak - provider not available");
		}
	}

}
class WindSpeedResource extends CoapResource {
	DataListener provider;

	public WindSpeedResource(DataListener provider) {
		super("windspeed");
		this.provider = provider;
		setObservable(true);
		getAttributes().setObservable();
		setObserveType(Type.NON);
	}

	@Override
	public void handleGET(CoapExchange exchange) {
		if(provider!=null) {
			exchange.respond("[{\"bn\":\"urn:dev:mac:tbd\"},{\"n\":windspeed,\"v\":" + provider.getWindspeed() + ",\"u\":\"m/s\"}]");
		} else {
			exchange.respond("nak - provider not available");
		}
	}

}

class SolarRadiationResource extends CoapResource {
	DataListener provider;

	public SolarRadiationResource(DataListener provider) {
		super("solar");
		this.provider = provider;
		setObservable(true);
		getAttributes().setObservable();
		setObserveType(Type.NON);
	}

	@Override
	public void handleGET(CoapExchange exchange) {
		if(provider!=null) {
			exchange.respond("[{\"bn\":\"urn:dev:mac:tbd\"},{\"n\":\"solar\",\"v\":" + provider.getSolar() + ",\"u\":\"w/m2\"}]");
		} else {
			exchange.respond("nak - provider not available");
		}
	}

}
