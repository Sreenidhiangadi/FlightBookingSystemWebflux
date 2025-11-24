package com.flightapp;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class FlightBookingWebfluxApplicationTests {

    @Test
    void contextLoads() {
    }

    @Test
    void mainMethodRuns() {
        FlightBookingWebfluxApplication.main(
                new String[]{"--spring.main.web-application-type=none"}
        );
    }
}