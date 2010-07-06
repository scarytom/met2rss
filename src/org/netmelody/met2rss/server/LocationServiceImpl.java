package org.netmelody.met2rss.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.netmelody.met2rss.client.LocationService;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class LocationServiceImpl extends RemoteServiceServlet implements LocationService {

    private static final long serialVersionUID = 1L;
    
    private final Map<String, String> regionCodeMap = new HashMap<String, String>();
    private final Map<String, List<String>> locationMap = new HashMap<String, List<String>>();
    private final Map<String, String> locationCodeMap = new HashMap<String, String>();
    
    public LocationServiceImpl() {
        final List<String> locations = new ArrayList<String>();
        final List<String> regions = new ArrayList<String>();
        
        try {
            URL url = new URL("http://www.metoffice.gov.uk/weather/uk/uk_forecast_weather.html");
            BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
            String line;

            while ((line = reader.readLine()) != null) {
                if (line.indexOf("locations[0]=[\"Select region first| \"]") >= 0) {
                    break;
                }
            }
            
            while ((line = reader.readLine()) != null) {
                if (line.indexOf("locations") < 0) {
                    break;
                }
                locations.add(line);
            }
            
            while ((line = reader.readLine()) != null) {
                if (line.indexOf("<option>Select a region</option>") >= 0) {
                    break;
                }
            }
            
            while ((line = reader.readLine()) != null) {
                if (line.indexOf("option") < 0) {
                    break;
                }
                regions.add(line);
            }
            
            reader.close();

        } catch (MalformedURLException e) {
            // ...
        } catch (IOException e) {
            // ...
        }
        
        extractRegions(regions);
        extractLocations(locations);
    }
    
    private void extractRegions(List<String> regionStrings) {
        this.regionCodeMap.clear();
        for (String regionString : regionStrings) {
            final int codeStart = regionString.indexOf('"') + 1;
            final String regionCode = regionString.substring(codeStart, codeStart+2);
            
            final int nameStart = regionString.indexOf('>') + 1;
            final int nameEnd = regionString.indexOf('<', codeStart);
            final String regionName = regionString.substring(nameStart, nameEnd).replaceAll("&amp;", "&");
            
            this.regionCodeMap.put(regionName, regionCode);
        }
    }
    
    private void extractLocations(List<String> locationStrings) {
        this.locationMap.clear();
        for (String locationString : locationStrings) {
            for (String location : locationString.split("\"")) {
                String[] locationBits = location.split("\\|");
                if (locationBits.length <= 1) {
                    continue;
                }
                String locationName = locationBits[0];
                String regionCode = locationBits[1].substring(0, 2);
                String locationCode = locationBits[1];
                
                if (this.locationMap.containsKey(regionCode)) {
                    this.locationMap.get(regionCode).add(locationName);
                }
                else {
                    this.locationMap.put(regionCode, new ArrayList<String>(Arrays.asList(locationName)));
                }
                this.locationCodeMap.put(locationName, locationCode);
            }
        }
    }

    @Override
    public List<String> getRegions() {
        final List<String> result = new ArrayList<String>(this.regionCodeMap.keySet());
        Collections.sort(result);
        return result;
    }

    @Override
    public List<String> getLocationsFor(String region) throws IllegalArgumentException {
        if (!this.regionCodeMap.containsKey(region)) {
            throw new IllegalArgumentException("Invalid region");
        }
        return this.locationMap.get(this.regionCodeMap.get(region));
    }

    @Override
    public String getEndpointFor(String location) throws IllegalArgumentException {
        if (!this.locationCodeMap.containsKey(location)) {
            throw new IllegalArgumentException("Invalid location");
        }
        return this.locationCodeMap.get(location) + ".rss";
    }

}
