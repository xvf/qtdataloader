package com.qt.reader;

import com.qt.domain.ClinicItem;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

/**
 * Created by asrivastava on 5/8/17.
 */
@Component
public class ClinicStreamReader implements ItemReader<ClinicItem> {
    @Value("${api.url}")
    private String apiUrl;
    @Autowired
    public RestTemplate restTemplate;

    private int nextClinicIndex;
    private List<ClinicItem> clinicItemInfoList;

    public ClinicStreamReader() {
        nextClinicIndex = 0;
    }

    public ClinicItem read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
        if (clinicDataNotInitialized()) {
            clinicItemInfoList = fetchClinicData();
        }
        ClinicItem nextClinicItem = null;
        if (nextClinicIndex < clinicItemInfoList.size()) {
            nextClinicItem = clinicItemInfoList.get(nextClinicIndex);
            nextClinicIndex++;
        }

        return nextClinicItem;
    }

    private boolean clinicDataNotInitialized() {
        return this.clinicItemInfoList == null;
    }

    private List<ClinicItem> fetchClinicData() {
        ResponseEntity<ClinicItem[]> response = this.restTemplate.getForEntity(apiUrl, ClinicItem[].class);
        return Arrays.asList((ClinicItem[])response.getBody());

    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
