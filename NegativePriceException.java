package com.company;

public class NegativePriceException extends Exception{
    public NegativePriceException(String message){
        super(message);
    }
}
