package com.sudoers.airline.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class AppConfig {

    public static AirlineProperties airlineProperties;
    public static String projectPath;

    // get application properties (ip, port, db, ... )

    public static void getApplicationProperties() {

        try {
            // read properties file

            File jarPath=new File(AppConfig.class.getProtectionDomain().getCodeSource().getLocation().getPath());
            projectPath = jarPath.getParent();
            String propertiesPath=projectPath + "/config.properties";
            System.out.println("propertiesPath:"+propertiesPath);

            File file = new File(propertiesPath);
            if(!file.isFile()){
                System.out.println("Unable to find config.properties");
                System.exit(-1);
            }
            InputStream input = new FileInputStream(propertiesPath);

//            InputStream input = AppConfig.class.getClassLoader().getResourceAsStream("config.properties");
//            if (input == null) {
//                System.out.println("Unable to find config.properties");
//                System.exit(-1);
//            }


            Properties properties = new Properties();
            // load properties file from class path, inside static method
            properties.load(input);

            // load application properties
            airlineProperties = new AirlineProperties();
            airlineProperties.setAirlineName(properties.getProperty("airline.name"));
            airlineProperties.setPort(Integer.parseInt(properties.getProperty("airline.port")));
            airlineProperties.setDatabasePath(properties.getProperty("airline.database.path"));
            airlineProperties.setSeatCount(Integer.parseInt(properties.getProperty("airline.database.seat_count")));

            airlineProperties.setTravelAgencyIP(properties.getProperty("travel-agency.ip"));
            airlineProperties.setTravelAgencyPort(Integer.parseInt(properties.getProperty("travel-agency.port")));

        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }
}
