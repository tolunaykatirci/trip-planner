package com.sudoers.travelagency.util;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class TravelAgencyProperties {

    private String travelAgencyName;
    private int port;
    private String databasePath;

}
