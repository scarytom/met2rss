package org.netmelody.met2rss.server;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public final class Prediction {

    private static final String[] SUFFIXES = {"st", "nd", "rd", "th", "th", "th", "th", "th", "th", "th",
                                              "th", "th", "th", "th", "th", "th", "th", "th", "th", "th",
                                              "st", "nd", "rd", "th", "th", "th", "th", "th", "th", "th",
                                              "st"};
    
    private Date date;
    private String weather;
    private BigDecimal temperature;
    private String windDirection;
    private BigDecimal windSpeed;
    private BigDecimal windGustSpeed;
    private String visibility;

    public Prediction(Date date, String weather, BigDecimal temperature,
                      String windDirection, BigDecimal windSpeed,
                      BigDecimal windGustSpeed, String visibility) {
        super();
        this.date = date;
        this.weather = weather;
        this.temperature = temperature;
        this.windDirection = windDirection;
        this.windSpeed = windSpeed;
        this.windGustSpeed = windGustSpeed;
        this.visibility = visibility;
    }

    public Date getDate() {
        return this.date;
    };
    
    public String getWeather() {
        return this.weather;
    }
    
    public BigDecimal getTemperature() {
        return this.temperature;
    }
    
    public String getWindDirection() {
        return this.windDirection;
    }
    
    public BigDecimal getWindSpeed() {
        return this.windSpeed;
    }
    
    public BigDecimal getWindGustSpeed() {
        return this.windGustSpeed;
    }
    
    public String getVisibility() {
        return this.visibility;
    }
    
    public String htmlHeader() {
        StringBuilder result = new StringBuilder();
        result.append("<table>");
        result.append("<tr><th>Date</th><th>Weather</th><th>Temperature (°C)</th><th>Wind Direction</th><th>Wind Speed (mph)</th><th>Wind Gust Speed (mph)</th><th>Visibility</th></tr>");
        return result.toString();
    }
    
    public String htmlFooter() {
        return "</table>";
    }
    
    public String htmlBody() {
        StringBuilder result = new StringBuilder();
        result.append("<tr>");
        result.append("<td>");
        result.append(displayableDate(this.date));
        result.append("</td>");
        result.append("<td>");
        result.append(this.weather);
        result.append("</td>");
        result.append("<td>");
        result.append(this.temperature);
        result.append("</td>");
        result.append("<td>");
        result.append(this.windDirection);
        result.append("</td>");
        result.append("<td>");
        result.append(this.windSpeed);
        result.append("</td>");
        result.append("<td>");
        result.append(this.windGustSpeed);
        result.append("</td>");
        result.append("<td>");
        result.append(this.visibility);
        result.append("</td>");
        result.append("</tr>");
        return result.toString();
    }
    
    private String displayableDate(Date date) {
        final SimpleDateFormat dayFormat = new SimpleDateFormat("d");
        dayFormat.setTimeZone(TimeZone.getTimeZone("Europe/London"));
        String suffix = SUFFIXES[Integer.parseInt(dayFormat.format(date))];

        final SimpleDateFormat dateFormat = new SimpleDateFormat("EEE d'" + suffix + "' HHmm");
        dateFormat.setTimeZone(TimeZone.getTimeZone("Europe/London"));
        return dateFormat.format(date);
    }
}
