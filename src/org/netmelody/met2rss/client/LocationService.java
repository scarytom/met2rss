package org.netmelody.met2rss.client;

import java.util.List;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("locations")
public interface LocationService extends RemoteService {

    List<String> getRegions();
    
    List<String> getLocationsFor(String region) throws IllegalArgumentException;
    
    String getEndpointFor(String location) throws IllegalArgumentException;
}
