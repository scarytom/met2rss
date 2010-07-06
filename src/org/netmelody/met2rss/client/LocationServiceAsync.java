package org.netmelody.met2rss.client;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface LocationServiceAsync {

    void getRegions(AsyncCallback<List<String>> callback);

    void getLocationsFor(String region, AsyncCallback<List<String>> callback);

    void getEndpointFor(String location, AsyncCallback<String> callback);

}
