package com.soundcloud.exceptions;

public class BadSQLRequest extends RuntimeException{
    public BadSQLRequest(String msg){
        super(msg);
    }
}