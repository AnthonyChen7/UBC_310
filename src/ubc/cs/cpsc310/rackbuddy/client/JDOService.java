package ubc.cs.cpsc310.rackbuddy.client;

import java.util.List;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("jdoService")
public interface JDOService extends RemoteService {

	public void addBikeRackData(BikeRackData data);
	
	public void removeBikeRackData(BikeRackData data);
	
	public List<BikeRackData> getAllData();
	
	public void updateBikeRackData(BikeRackData oldData, BikeRackData updatedData);
	
	public  BikeRackData getBikeRackObject(BikeRackData data);
	
	public void removeAll();
	
}