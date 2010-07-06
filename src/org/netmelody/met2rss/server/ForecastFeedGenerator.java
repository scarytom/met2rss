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
        SyndFeed feed = new SyndFeedImpl();
        feed.setFeedType("rss_2.0");
        feed.setTitle(forecast.getTitle());
        feed.setLink(forecast.getForecasterUrl());
        feed.setDescription("");

        final List<Prediction> predictions = forecast.predictions();
        StringBuilder builder = new StringBuilder();
        if (!predictions.isEmpty()) {
            builder.append(predictions.get(0).htmlHeader());
            for(Prediction prediction : forecast.predictions()) {
                builder.append(prediction.htmlBody());
            }
            builder.append(predictions.get(0).htmlFooter());
        }
        
        final SyndEntry entry = new SyndEntryImpl();
        entry.setTitle("Forecast");
        entry.setLink(forecast.getForecastUrl());
        entry.setPublishedDate(forecast.getDate());
        
        final SyndContent description = new SyndContentImpl();
        description.setValue(builder.toString());
        description.setType("text/html");
        entry.setDescription(description);

        feed.setEntries(Arrays.asList(entry));
        
        final Channel result = (Channel)feed.createWireFeed();
        result.setLastBuildDate(forecast.getDate());
        result.setTtl(60);
        return result;
    }
}

