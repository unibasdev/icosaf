package it.unibas.arduino.client.model;

import com.google.gson.annotations.SerializedName;
import java.util.Date;
import java.util.Map;
import lombok.Data;

@Data
public class Response {

    private Map<String, String> data;
    private String token;
    private Date timestamp;
    @SerializedName("RESULT")
    private String result;
    @SerializedName("ERROR")
    private String error;
    
}
