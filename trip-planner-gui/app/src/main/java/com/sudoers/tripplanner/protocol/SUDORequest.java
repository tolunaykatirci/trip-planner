package com.sudoers.tripplanner.protocol;

import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SUDORequest {

    // custom protocol
    private String host;
    private int port;
    private String version;
    private String function;
    private HashMap<String, String> dataMap;
    private List<String> dataList;

    // constructor
    public SUDORequest() {
    }

    // parse request from BufferedReader and create SUDORequest
    public static SUDORequest parseRequest(BufferedReader in) {
        SUDORequest sudoRequest = new SUDORequest();

        HashMap<String, String> tempDataMap = new HashMap<>();
        List<String> tempDataList = new ArrayList<>();

        try {
            String currentLine;
            while (in.ready()) {
                currentLine = in.readLine();

                if(currentLine.startsWith("HOST:"))
                    sudoRequest.setHost(currentLine.substring(5));
                else if (currentLine.startsWith("PORT:"))
                    sudoRequest.setPort(Integer.parseInt(currentLine.substring(5)));
                else if (currentLine.startsWith("VERSION:"))
                    sudoRequest.setVersion(currentLine.substring(8));
                else if (currentLine.startsWith("FUNCTION:"))
                    sudoRequest.setFunction(currentLine.substring(9));
                else if (currentLine.startsWith("DATA:")) {
                    String dataValue = currentLine.substring(5);
                    tempDataList.add(dataValue);
                }
                else if (currentLine.startsWith("DATA>")){
                    String dataKeyValue = currentLine.substring(5);
                    String[] splitted = dataKeyValue.split(":");
                    String dataKey = splitted[0];
                    String dataValue = splitted[1];
                    tempDataMap.put(dataKey, dataValue);
                }
                else {
                    System.out.println("Couldn't parse parameter: " + currentLine);
                }
            }
            sudoRequest.setDataList(tempDataList);
            sudoRequest.setDataMap(tempDataMap);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return sudoRequest;
    }

    // create request string from SUDORequest object
    public static String createRequest(SUDORequest sudoRequest) {
        StringBuilder sb = new StringBuilder();
        sb.append("SUDO\r\n");
        sb.append("HOST:").append(sudoRequest.getHost()).append("\r\n");
        sb.append("PORT:").append(sudoRequest.getPort()).append("\r\n");
        sb.append("VERSION:").append(sudoRequest.getVersion()).append("\r\n");
        sb.append("FUNCTION:").append(sudoRequest.getFunction()).append("\r\n");
        if (sudoRequest.getDataList() != null){
            for (String value:sudoRequest.getDataList()) {
                sb.append("DATA:").append(value).append("\r\n");
            }
        }
        if (sudoRequest.getDataMap() != null){
            for (String key:sudoRequest.getDataMap().keySet()) {
                sb.append("DATA>").append(key).append(":").append(sudoRequest.getDataMap().get(key)).append("\r\n");
            }
        }
        sb.append("\r\n");

        return sb.toString();
    }

    // toString method
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("SUDO\n");
        sb.append("HOST:").append(host).append("\n");
        sb.append("PORT:").append(port).append("\n");
        sb.append("VERSION:").append(version).append("\n");
        sb.append("FUNCTION:").append(function).append("\n");
        if (dataList != null){
            for (String value:dataList) {
                sb.append("DATA:").append(value).append("\n");
            }
        }
        if (dataMap != null){
            for (String key:dataMap.keySet()) {
                sb.append("DATA>").append(key).append(":").append(dataMap.get(key)).append("\n");
            }
        }

        return sb.toString();
    }

    // getters and setters

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getFunction() {
        return function;
    }

    public void setFunction(String function) {
        this.function = function;
    }

    public HashMap<String, String> getDataMap() {
        return dataMap;
    }

    public void setDataMap(HashMap<String, String> dataMap) {
        this.dataMap = dataMap;
    }

    public List<String> getDataList() {
        return dataList;
    }

    public void setDataList(List<String> dataList) {
        this.dataList = dataList;
    }
}
