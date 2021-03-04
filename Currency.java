package com.company;

import java.io.Serializable;

public class Currency implements Serializable {
    private static final long serialVersionUID = -1858375601289210717L;
    private String name;
    private String symbol;
    private double parityToEur;

    public Currency(String name, String symbol, double parityToEur){
        this.name = name;
        this.symbol = symbol;
        this.parityToEur = parityToEur;
    }
    public void updateParity(double parityToEur){
        this.parityToEur = parityToEur;
    }
    public double getParityToEur(){
        return parityToEur;
    }
    public String getName(){
        return name;
    }
    public String getSymbol(){
        return symbol;
    }
}
