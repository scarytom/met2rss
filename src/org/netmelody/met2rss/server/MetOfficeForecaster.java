package org.netmelody.met2rss.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

public final class MetOfficeForecaster implements Forecaster {

    @Override
    public Forecast forecastFor(String location) {
        final StringBuilder report = new StringBuilder();
        final String sourceLocation = "http://www.metoffice.gov.uk/weather/uk" + location + "_forecast_weather.html";
        
        try {
            final URL url = new URL(sourceLocation);
            BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
            String line;

            boolean inTable = false;
            while ((line = reader.readLine()) != null) {
                if (!inTable) {
                    inTable = -1 != line.indexOf("<div class=\"tableWrapper\">");
                    continue;
                }
                if (-1 != line.indexOf("/div")) {
                    break;
                }
                report.append(line);
            }
            reader.close();

        } catch (MalformedURLException e) {
            // ...
        } catch (IOException e) {
            // ...
        }
        
        return new MetOfficeForecast(report.toString(), sourceLocation);
    }
    
}
