package com.sudoers.travelagency.protocol;

import lombok.*;

import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SUDORequest {

    // custom protocol
    private String host;
    private int port;
    private String version;
    private String function;
    private HashMap<String, String> dataMap;
    private List<String> dataList;

    // this function creates SUDORequest object by parsing socket input
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

    // this function creates output String from SUDORequest object
    // to send with socket
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

}
