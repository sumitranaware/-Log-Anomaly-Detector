package com.example.Log_Anomoly_Detectiong_Spring.exception;

public class CustomException extends RuntimeException{
    public CustomException(String message){
        super(message);
    }
    public CustomException(String message,Throwable cause){
        super(message,cause);
    }

}
