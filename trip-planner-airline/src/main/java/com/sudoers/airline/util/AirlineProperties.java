package com.sudoers.airline.util;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class AirlineProperties {

    private String airlineName;
    private int port;

    private String databasePath;
    private int seatCount;

    private String travelAgencyIP;
    private int travelAgencyPort;
}
