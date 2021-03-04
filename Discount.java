package com.company;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Discount implements Serializable {
    private static final long serialVersionUID = -7955237198368838340L;
    private String name;
    private DiscountType discountType;
    private double value;
    LocalDateTime lastDateApplied = null;

    public Discount(DiscountType discountType, String name, double value){
        this.discountType = discountType;
        this.name = name;
        this.value = value;
    }
    public void setAsAppliedNow(){
        this.lastDateApplied = LocalDateTime.now();
    }
    public DiscountType getDiscountType() {
        return discountType;
    }
    public double getValue() {
        return value;
    }
    public String toString(){
        return discountType + " " + value + " "  +name + " " + lastDateApplied;
    }
}
