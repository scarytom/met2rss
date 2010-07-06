package org.netmelody.met2rss.server;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public final class MetOfficeForecast implements Forecast {

    private static final String UPDATE_PLACEHOLDER = "Last updated: ";
    private String report;
    private String sourceUrl;

    public MetOfficeForecast(String report, String sourceUrl) {
        this.report = report;
        this.sourceUrl = sourceUrl;
    }

    @Override
    public String getTitle() {
        final int captionStart = this.report.indexOf("<caption>") + 9;
        final int captionEnd = this.report.indexOf("</caption>");
        final String caption = this.report.substring(captionStart, captionEnd);
        return "Met Office " + caption;
    }

    @Override
    public String getForecasterUrl() {
        return "http://www.metoffice.gov.uk";
    }

    @Override
    public String getForecastUrl() {
        return sourceUrl;
    }

    @Override
    public Date getDate() {
        int updateIndex = this.report.indexOf(UPDATE_PLACEHOLDER) + UPDATE_PLACEHOLDER.length();
        String updateDate = this.report.substring(updateIndex, this.report.indexOf("</td>", updateIndex));
        updateDate = updateDate.replace(" on ", " ");
        updateDate = updateDate.replace("  ", " ");
        
        SimpleDateFormat dateFormat = new SimpleDateFormat("HHmm EEEE d MMM yyyy");
        dateFormat.setTimeZone(TimeZone.getTimeZone("Europe/London"));
        try {
            return dateFormat.parse(updateDate);
        }
        catch (ParseException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public List<Prediction> predictions() {
        final List<Prediction> result = new ArrayList<Prediction>();
        
        String text = this.report.replaceAll("<[/]?br[/]?>", " ").replaceAll("<[/]?strong>", "");
        String[] rows = text.split("<[/]?tr.*?>");
        
        String day = "";
        for (String row : rows) {
            String[] data = row.split("<[/]?td.*?>");
            
            if (data.length <= 2) {
                continue;
            }
            
            // remove empty entries
            int i = 0;
            for (int j = 0; j < data.length; j++) {
              if (!"".equals(data[j].trim())) {
                  data[i++] = data[j];
              }
            }
            data = Arrays.copyOf(data, i);
            
            if (8 == data.length) {
                day = data[0];
            }
            else {
                String[] newData = new String[data.length+1];
                System.arraycopy(data, 0, newData, 1, data.length);
                newData[0] = day;
                data = newData;
            }
            
            result.add(createPrediction(data));
            
        }
        return result;
    }

    private Prediction createPrediction(String[] data) {
        SimpleDateFormat yearDateFormat = new SimpleDateFormat("yyyy");
        
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE d MMM HHmm yyyy");
        dateFormat.setTimeZone(TimeZone.getTimeZone("Europe/London"));
        Date date;
        try {
            String dateString = data[0] + " " + data[1] + " " + yearDateFormat.format(getDate());
            dateString = dateString.replace("Day", "1200");
            dateString = dateString.replace("Night", "0000");
            date = dateFormat.parse(dateString);
        }
        catch (ParseException e) {
            throw new IllegalStateException(e);
        }
        
        return new Prediction(date, createWeather(data[2]), createBigDecimal(data[3]), data[4], createBigDecimal(data[5]), createBigDecimal(data[6]), data[7]);
    }

    private String createWeather(String data) {
        int imgIdx = data.indexOf("/lib/images/symbols/");
        return data.substring(0, imgIdx) + getForecasterUrl() + data.substring(imgIdx);
    }
    
    private BigDecimal createBigDecimal(String data) {
        return new BigDecimal("0" + data.trim().split("[^\\d]", 2)[0]);
    }

}
