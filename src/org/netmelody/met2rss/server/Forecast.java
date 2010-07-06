package org.netmelody.met2rss.server;

import java.util.Date;
import java.util.List;


public interface Forecast {

    /**
     * The date and time when this forecast was made.
     */
    Date getDate();
    
    String getTitle();
    
    String getForecasterUrl();
    
    String getForecastUrl();

    List<Prediction> predictions();

}
