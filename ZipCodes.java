import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.List;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class ZipCodes{
    public static Map<String, List<DistanceTo>> zipcodes = new HashMap<String, List<DistanceTo>>();
    public static List<VehicleRequest> requests = new ArrayList<VehicleRequest>();
    public static Map<String, List<EmergencyVehicle>> vehicles = new HashMap<String, List<EmergencyVehicle>>();
    
    public static void readDistancedata() throws IOException{
    	String splitBy = ",";
        BufferedReader br = new BufferedReader(new FileReader("emergency.csv"));    
        String line = br.readLine();

        while ((line = br.readLine()) != null) {
        	String[] b = line.split(splitBy);            
            if (b.length > 0) {
                if(zipcodes.containsKey(b[0]))
                {
                	zipcodes.get(b[0]).add(new DistanceTo((b[1]), Integer.parseInt(b[2])));
                } else {
                	List<DistanceTo> DistanceToobj = new ArrayList<>();
                	DistanceToobj.add(new DistanceTo(b[1], Integer.parseInt(b[2])));
                	zipcodes.put(b[0],DistanceToobj);
                }            
            }
        }
        for (String key:zipcodes.keySet())
        {
        	Collections.sort(zipcodes.get(key));
        }
        }
    
    public static void readRequestdata() throws IOException{    	
    	String splitBy = ",";
        BufferedReader br = new BufferedReader(new FileReader("request.csv"));    
        String line = br.readLine();
        while ((line = br.readLine()) != null) {
        	String[] b = line.split(splitBy);            
            if (b.length > 0) {
                requests.add(new VehicleRequest(Integer.parseInt(b[0]), Integer.parseInt(b[1]), b[2]));            
            }
        }  
        } 
    
    public static void readEmergencyVehicleData() throws IOException{
    	String splitBy = ",";
        BufferedReader br = new BufferedReader(new FileReader("vehicles.csv"));    
        String line = br.readLine(); //heading        
        while ((line = br.readLine()) != null) {
        	String[] b = line.split(splitBy);            
            if (b.length > 0) {
                if(vehicles.containsKey(b[2])){
                	vehicles.get(b[2]).add(new EmergencyVehicle(Integer.parseInt(b[0]), Integer.parseInt(b[1])));
                } else {
                	List<EmergencyVehicle> vehicleobj = new ArrayList<>();
                	vehicleobj.add(new EmergencyVehicle(Integer.parseInt(b[0]), Integer.parseInt(b[1])));
                	vehicles.put(b[2],vehicleobj);
                }
            
            }
        }
        }
    
    public static void main(String[] args) throws IOException {
        readDistancedata();
        readEmergencyVehicleData();
        readRequestdata();
        
        for(VehicleRequest request:requests){
        	
        	List<DistanceTo> nearestzipcodes = zipcodes.get(request.zipcode);
        	if(nearestzipcodes == null || nearestzipcodes.isEmpty()){
        		continue;
        	}
    		for(DistanceTo nearestzipcode: nearestzipcodes){
    			List<EmergencyVehicle> EmergencyVehicle = vehicles.get(nearestzipcode.zipcode);
                if(EmergencyVehicle == null){
                	continue;
                }
    			for(EmergencyVehicle vehicle:EmergencyVehicle){
        		if(vehicle.isavailable())
        		{
        			 vehicle.dispatch();
        			request.AssignVehicle(vehicle.id, nearestzipcode.distance);
        			break;
        		}
        	}
        	if(request.vehicleid != -1){
        		break;
        	}
    		}     		
        }
        
    for(VehicleRequest request:requests){
    	if(request.vehicleid == -1){
    		System.out.println("Sorry no vehicle could be dispatched to zipcode: " +request.zipcode+ " of vehicle type: " +String.valueOf(request.type)+" for request id: " +String.valueOf(request.id));
    	} else{
    		System.out.println("vehicle was dispatched to zipcode: " +request.zipcode+ " of vehicle id: " +String.valueOf(request.vehicleid) +" for request id: " +String.valueOf(request.id));

    	}
    }
    }
} 

 class DistanceTo implements Comparable<DistanceTo>{	  
	  String zipcode;
	  int distance;
	  
	  DistanceTo(String zipcode, int distance) {
		  this.zipcode = zipcode;
		  this.distance = distance;
	  }
	
	@Override
	public int compareTo(DistanceTo o) {		
		return distance-o.distance;
	}
}
 
 class VehicleRequest {	  
	  int id;
	  int type;
	  String zipcode;
	  int vehicleid = -1;
	  int distance;
	  
	  VehicleRequest(int id, int type, String zipcode) {
		  this.id = id;		  
		  this.type = type;		
		  this.zipcode = zipcode;
	  }		  	  
 
	  void AssignVehicle(int id, int distance){
		this.vehicleid = id;
		this.distance = distance;
	}
 }  
	 
class EmergencyVehicle{
	int id;
	int type;
	boolean isavailable;
	
	EmergencyVehicle(int id, int type){
	this.id = id;
	this.type = type;
	this.isavailable = true;
	}
	
	boolean isavailable(){
		return isavailable;		
	}
	
	public void dispatch(){
		this.isavailable=false;		
	}
}

