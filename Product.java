package com.company;

import java.io.Serializable;

// builder
public class Product implements Serializable {
    private static final long serialVersionUID = 492148699978435582L;
    private final String uniqueId;
    private String name;
    private final Manufacturer manufacturer;
    private double price;
    private final int quantity;
    //it will calculate the price of all the products of the same kind
    public double getPriceAllProducts(){
        return price * quantity;
    }
    public double getPrice(){
        return price;
    }
    public Product(ProductBuilder productBuilder){
        this.uniqueId = productBuilder.uniqueId;
        this.name = productBuilder.name;
        this.manufacturer = productBuilder.manufacturer;
        this.price = productBuilder.price;
        this.quantity = productBuilder.quantity;
    }

    public String getUniqueId(){
        return uniqueId;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getName(){
        return name;
    }


    public Manufacturer getManufacturer() {
        return manufacturer;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getQuantity(){
        return quantity;
    }


    public boolean sameProduct(Product p){
        return uniqueId.equals(p.uniqueId);
    }
    public String toString(String symbolCurrency){
        double roundOffPrice = Math.round(this.price * 100.0) / 100.0;
        return uniqueId + "," + name + "," + this.getManufacturer().getName() + ","
                + symbolCurrency+roundOffPrice + "," + this.quantity;
    }
    public static class ProductBuilder {
        private  String uniqueId;
        private  String name;
        private  Manufacturer manufacturer;
        private  double price;
        private  int quantity;

        public ProductBuilder setUniqueId(String uniqueId) {
            this.uniqueId = uniqueId;
            return this;
        }

        public ProductBuilder setName(String name) {
            this.name = name;
            return this;
        }

        public ProductBuilder setManufacturer(Manufacturer manufacturer) {
            this.manufacturer = manufacturer;
            return this;
        }

        public ProductBuilder setPrice(double price) {
            this.price = price;
            return this;
        }

        public ProductBuilder setQuantity(int quantity) {
            this.quantity = quantity;
            return this;
        }

        //we will create a productBuilder with all the necessary attributes
        public ProductBuilder(){
        }
        public Product buildProduct(){
            return new Product(this);
        }
    }
}
