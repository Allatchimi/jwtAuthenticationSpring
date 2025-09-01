package com.kidami.security.responses;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse <T>{
    private String status;
    private String message;
    private T data;
    private Object metaData;

    public  ApiResponse(String status,String message,T data,Object metaData){
        this.status = status;
        this.message = message;
        this.data = data;
        this.metaData = metaData;
    }
}
