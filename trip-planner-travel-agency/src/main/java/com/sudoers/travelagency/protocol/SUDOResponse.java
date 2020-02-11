package com.sudoers.travelagency.protocol;

import lombok.*;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SUDOResponse {

    // custom protocol response
    private int status;
    private String message;
    private String version;
    private HashMap<String, String> dataMap;
    private List<String> dataList;

    // this function creates output String from createResponse object
    // to send with socket
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

    // toString method
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("SUDO\r\n");
        sb.append("STATUS:").append(status).append("\r\n");
        sb.append("MESSAGE:").append(message).append("\r\n");
        sb.append("VERSION:VER_1.0\r\n");
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
}
