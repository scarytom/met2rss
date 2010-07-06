package org.netmelody.met2rss.server;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.WireFeedOutput;

public final class ForecastFeedServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    
    private ForecastFeedGenerator forecastFeedGenerator = new ForecastFeedGenerator();

    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/rss+xml");
        
        final Forecaster forecaster = new MetOfficeForecaster();
        
        String location = req.getRequestURI();
        location = location.replaceAll("\\.rss", "");
        final Forecast forecast = forecaster.forecastFor(location);

        WireFeedOutput feedOutputter = new WireFeedOutput();
        try {
            feedOutputter.output(forecastFeedGenerator.createFeedFor(forecast), resp.getWriter());
        }
        catch (FeedException e) {
            throw new IOException("Failed to create feed", e);
        }
    }
}
