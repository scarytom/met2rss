package org.netmelody.met2rss.server;

public interface Forecaster {

   Forecast forecastFor(String location);

}