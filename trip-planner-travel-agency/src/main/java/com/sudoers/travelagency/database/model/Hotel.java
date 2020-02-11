package com.sudoers.travelagency.database.model;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Hotel {
    private int hotelId;
    private String name;
    private String ip;
    private int port;
    private int capacity;
}
