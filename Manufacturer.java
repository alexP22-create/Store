package com.company;

import java.io.Serializable;

public class Manufacturer implements Serializable {
    private static final long serialVersionUID = 5992828693837741477L;
    private String name;
    private int countProducts;
    public Manufacturer(String name){
        this.name = name;
        this.countProducts = 1;
    }
    public String getName() {
        return name;
    }
    public int getCount() {
        return countProducts;
    }
    public void addToProduct(){
        this.countProducts ++;
    }
}
