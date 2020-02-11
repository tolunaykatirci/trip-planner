package com.sudoers.travelagency.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class AppConfig {

    // application config for initial properties

    public static TravelAgencyProperties travelAgencyProperties;
    public static String projectPath;

    public static void getApplicationProperties() {

        try {

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

            // load properties file from class path, inside static method
            Properties properties = new Properties();
            properties.load(input);

            // update program properties
            travelAgencyProperties = new TravelAgencyProperties();
            travelAgencyProperties.setTravelAgencyName(properties.getProperty("travel-agency.name"));
            travelAgencyProperties.setPort(Integer.parseInt(properties.getProperty("travel-agency.port")));
            travelAgencyProperties.setDatabasePath(properties.getProperty("travel-agency.database.path"));

        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }
}
