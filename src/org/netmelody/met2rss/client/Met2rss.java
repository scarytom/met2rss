package org.netmelody.met2rss.client;

import java.util.List;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RootPanel;

public class Met2rss implements EntryPoint {

    /**
     * The message displayed to the user when the server cannot be reached or
     * returns an error.
     */
    private static final String SERVER_ERROR = "An error occurred while "
            + "attempting to contact the server. Please check your network "
            + "connection and try again.";
    
    private final LocationServiceAsync locationService = GWT.create(LocationService.class);
    
    private ListBox regionField;
    private ListBox locationField;
    private Label errorLabel;
    private Anchor feedLink;

    public void onModuleLoad() {
        regionField = new ListBox();
        locationField = new ListBox();
        errorLabel = new Label();
        feedLink = new Anchor("", "/");
        
        regionField.addItem("Loading locations...");
        locationField.addItem("Select a region first.");

        RootPanel.get("regionFieldContainer").add(regionField);
        RootPanel.get("locationFieldContainer").add(locationField);
        RootPanel.get("linkContainer").add(feedLink);
        RootPanel.get("errorLabelContainer").add(errorLabel);

        regionField.setFocus(true);
        fetchRegions();
        
        regionField.addChangeHandler(new ChangeHandler(){
            @Override
            public void onChange(ChangeEvent event) {
                fetchLocations();
            }
        });
        locationField.addChangeHandler(new ChangeHandler(){
            @Override
            public void onChange(ChangeEvent event) {
                fetchFeedLocation();
            }
        });
    }

    private void fetchRegions() {
        this.locationService.getRegions(
                new AsyncCallback<List<String>>() {
                    public void onFailure(Throwable caught) {
                        publishError();
                    }

                    public void onSuccess(List<String> regions) {
                        clearError();
                        regionField.clear();
                        for (String region : regions) {
                            regionField.addItem(region);
                        }
                        fetchLocations();
                    }
                });
    }

    private void fetchLocations() {
        final String region = regionField.getValue(regionField.getSelectedIndex());
        
        this.locationService.getLocationsFor(region, 
                new AsyncCallback<List<String>>() {
                    public void onFailure(Throwable caught) {
                        publishError();
                    }

                    public void onSuccess(List<String> locations) {
                        clearError();
                        locationField.clear();
                        for (String location : locations) {
                            locationField.addItem(location);
                        }
                        fetchFeedLocation();
                    }
                });
    }
    
    private void fetchFeedLocation() {
        final String location = locationField.getValue(locationField.getSelectedIndex());
        
        this.locationService.getEndpointFor(location,
                new AsyncCallback<String>() {
                    public void onFailure(Throwable caught) {
                        publishError();
                    }

                    public void onSuccess(String endpoint) {
                        clearError();
                        feedLink.setText("weather feed for " + location);
                        feedLink.setHref("/" + endpoint);
                    }
                });
    }
    
    private void publishError() {
        errorLabel.setText(SERVER_ERROR);
    }
    
    private void clearError() {
        errorLabel.setText("");
    }
}
