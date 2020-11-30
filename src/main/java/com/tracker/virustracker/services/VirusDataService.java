package com.tracker.virustracker.services;

import com.tracker.virustracker.models.Location;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

@Service
public class VirusDataService {
    private static String VIRUS_DATASOURCE_URL = "https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_time_series/time_series_covid19_recovered_global.csv";

    private List<Location> allStats = new ArrayList<>();

    public List<Location> getAllStats() {
        return allStats;
    }

    //Fetch data
    //Run on a schedule
    @PostConstruct
    @Scheduled(cron = "* * 1 * * * ") //reload data every first hour of every day
    public void fetchVirusData() throws IOException, InterruptedException {
        List<Location> newStats = new ArrayList<>();
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(VIRUS_DATASOURCE_URL))
                .build();
        HttpResponse <String> httpResponse = client.send(request, HttpResponse.BodyHandlers.ofString());

        StringReader csvReader = new StringReader(httpResponse.body());
        Iterable <CSVRecord> records = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(csvReader);
        for (CSVRecord record : records) {
            Location location = new Location();
            int totalNewCases = Integer.parseInt(record.get(record.size() - 1));
            int previousDayCases = Integer.parseInt(record.get(record.size() - 2));
            location.setState(record.get("Province/State"));
            location.setCountry(record.get("Country/Region"));
            location.setLatestTotalCases(totalNewCases);
            location.setDelta(totalNewCases - previousDayCases);
//            System.out.println(location);
            newStats.add(location);
        }
        this.allStats = newStats;
    }
}

