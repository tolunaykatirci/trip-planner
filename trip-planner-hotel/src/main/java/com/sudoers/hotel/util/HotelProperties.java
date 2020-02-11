package com.sudoers.hotel.util;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class HotelProperties {

    private String hotelName;
    private int port;

    private String databasePath;
    private int roomCount;

    private String travelAgencyIP;
    private int travelAgencyPort;
}

