package com.zz.flight.task;

import com.zz.flight.service.FlightService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class emailTask {
    @Autowired
    private FlightService flightService;

    @Scheduled(cron = "0 0 */24 * * ?")
    public void emailTask(){
        int hour = 24;
        flightService.emailTask(hour);
    }

}
