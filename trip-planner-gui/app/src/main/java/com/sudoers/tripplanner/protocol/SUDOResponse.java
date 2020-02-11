package com.sudoers.tripplanner.protocol;

import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SUDOResponse {

    // custom protocol response
    private int status;
    private String message;
    private String version;
    private HashMap<String, String> dataMap;
    private List<String> dataList;

    // constructor
    public SUDOResponse() {
    }

    // create response string from createResponse object
    public static String createResponse(SUDOResponse sudoResponse) {
        StringBuilder sb = new StringBuilder();
        sb.append("SUDO\r\n");
        sb.append("STATUS:").append(sudoResponse.getStatus()).append("\r\n");
        sb.append("MESSAGE:").append(sudoResponse.getMessage()).append("\r\n");
        sb.append("VERSION:VER_1.0\r\n");
        if (sudoResponse.getDataList() != null) {
            for (String data:sudoResponse.getDataList()) {
                sb.append("DATA:").append(data).append("\r\n");
            }
        }
        if(sudoResponse.getDataMap() != null) {
            for (String key:sudoResponse.getDataMap().keySet()) {
                sb.append("DATA>").append(key).append(":").append(sudoResponse.getDataMap().get(key)).append("\r\n");
            }
        }
        return sb.toString();
    }

    // parse request from BufferedReader and create SUDOResponse
    public static SUDOResponse parseResponse(BufferedReader in) {
        SUDOResponse sudoResponse = new SUDOResponse();

        HashMap<String, String> tempDataMap = new HashMap<>();
        List<String> tempDataList = new ArrayList<>();

        try {
            String currentLine;
            while ((currentLine = in.readLine()) != null) {
                if(currentLine.startsWith("SUDO")) {
                    // do nothing
                }
                else if(currentLine.startsWith("STATUS:"))
                    sudoResponse.setStatus(Integer.parseInt(currentLine.substring(7).trim()));
                else if (currentLine.startsWith("MESSAGE:"))
                    sudoResponse.setMessage(currentLine.substring(8));
                else if (currentLine.startsWith("VERSION:"))
                    sudoResponse.setVersion(currentLine.substring(8));
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
            sudoResponse.setDataList(tempDataList);
            sudoResponse.setDataMap(tempDataMap);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return sudoResponse;
    }

    // toString method
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("SUDO\r\n");
        sb.append("STATUS:").append(status).append("\r\n");
        sb.append("MESSAGE:").append(message).append("\r\n");
        sb.append("VERSION:").append(version).append("\r\n");
        if (dataList != null){
            for (String data:dataList) {
                sb.append("DATA:").append(data).append("\r\n");
            }
        }
        if (dataMap != null) {
            for (String key:dataMap.keySet()) {
                sb.append("DATA>").append(key).append(":").append(dataMap.get(key)).append("\r\n");
            }
        }
        return sb.toString();
    }

    // getters and setters

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
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
