package org.netmelody.met2rss.server;

import java.util.Arrays;
import java.util.List;

import com.sun.syndication.feed.WireFeed;
import com.sun.syndication.feed.rss.Channel;
import com.sun.syndication.feed.synd.SyndContent;
import com.sun.syndication.feed.synd.SyndContentImpl;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndEntryImpl;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.feed.synd.SyndFeedImpl;

public final class ForecastFeedGenerator {

    public WireFeed createFeedFor(Forecast forecast) {
        return createFeedFor(forecast, false);
    }
    
    public WireFeed createSimpleFeedFor(Forecast forecast) {
        return createFeedFor(forecast, true);
    }
    
    private WireFeed createFeedFor(Forecast forecast, boolean simple) {
        SyndFeed feed = new SyndFeedImpl();
        feed.setFeedType("rss_2.0");
        feed.setTitle(forecast.getTitle());
        feed.setLink(forecast.getForecasterUrl());
        feed.setDescription("");

        final SyndEntry entry = new SyndEntryImpl();
        entry.setTitle("Forecast");
        entry.setLink(forecast.getForecastUrl());
        entry.setPublishedDate(forecast.getDate());
        
        final SyndContent description = new SyndContentImpl();
        final String content = createFeedContent(forecast.predictions(), simple);
        description.setValue(content);
        description.setType("text/html");
        entry.setDescription(description);

        feed.setEntries(Arrays.asList(entry));
        
        final Channel result = (Channel)feed.createWireFeed();
        result.setLastBuildDate(forecast.getDate());
        result.setTtl(60);
        return result;
    }

    private String createFeedContent(final List<Prediction> predictions, boolean simple) {
        StringBuilder builder = new StringBuilder();
        
        if (predictions.isEmpty()) {
            return "";
        }
        
        if (simple) {
            for(Prediction prediction : predictions) {
                builder.append(prediction.getWeather());
            }
        }
        else {
            builder.append(predictions.get(0).htmlHeader());
            for(Prediction prediction : predictions) {
                builder.append(prediction.htmlBody());
            }
            builder.append(predictions.get(0).htmlFooter());
        }
        return builder.toString();
    }
}

