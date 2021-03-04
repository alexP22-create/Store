package com.company;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvValidationException;

import java.io.*;
import java.util.ArrayList;

// singleton
public class Store implements Serializable{
    private static final long serialVersionUID = -2685363136258450158L;
    private static Store instance;
    private String name;
     Currency currency;
     Product[] products;
     Manufacturer[] manufacturers;
     Discount[] discounts;
     public static final int MaxLengthArray = 100000;
    //next function creates the unique object if it's not already created else
    //returns the unic object
    public static Store Instance(){
        if (instance == null)
            instance = new Store();
        return instance;
    }
    public void initializeManufacturersDiscounts(){
        this.manufacturers = new Manufacturer[MaxLengthArray];
        this.discounts = new Discount[MaxLengthArray];
    }
    public void updateStore(Product[] products, Currency currency, String name) {
        this.currency = currency;
        this.products = products;
        this.name = name;
    }
    public void setCurrency(Currency currency){
        this.currency = currency;
    }
    public void setProducts(Product[] products){
        this.products = products;
    }
    public Currency getCurrency(){
        return this.currency;
    }
    public void addManufacturer(Manufacturer m) throws DuplicateManufacturerException{
        int i = 0;
        while(this.manufacturers[i] != null){
            //if there's another Manufacturer already added throw exception
            if(this.manufacturers[i].getName().equals(m.getName()))
                throw new DuplicateManufacturerException("DuplicateManufacturerException!");
            i++;
        }
        this.manufacturers[i] = m;
    }
    public Manufacturer setManufacturer(String name){
        int i = 0;
        //if the Manufacturer was already created we will use the already created instance
        while(this.manufacturers[i] != null){
            if(this.manufacturers[i].getName().equals(name)){
                this.manufacturers[i].addToProduct();
                return this.manufacturers[i];
            }
            i++;
        }
        Manufacturer m = new Manufacturer(name);
        try {
            addManufacturer(m);
        }
        catch(DuplicateManufacturerException ex) {
            System.out.println(ex.getMessage());
        }
        return m;
    }
    public void listManufacturers(){
        int i = 0;
        while(manufacturers[i] != null){
            System.err.println(manufacturers[i].getName());
            i++;
        }
    }
    public Currency createCurrency(String symbol, String name, double parityToEur){
        return new Currency(name, symbol, parityToEur);
    }
    public void changeCurrency(Currency currency, ArrayList<Currency> list) throws CurrencyNotFoundException{
        boolean foundCurrency = false;
        for (Currency value : list) {
            if (value.getName().equals(currency.getName())) {
                foundCurrency = true;
                break;
            }
        }
        //if that currency doesn't exist, an exception will be thrown
        if (!foundCurrency)
            throw new CurrencyNotFoundException("Currency necunoscut!!!");
        int i = 0;
        if(products != null)
            while(products[i] != null){
                double newPrice = products[i].getPrice();
                //first we have to convert to Euro
                newPrice = newPrice * this.currency.getParityToEur();
                //then from Euro to the new Currency
                newPrice = newPrice / currency.getParityToEur();
                products[i].setPrice(newPrice);
                i++;
            }
        this.currency = currency;
    }
    public void applyChangeCurrency(Currency currency, ArrayList<Currency> list){
        try {
            this.changeCurrency(currency, list);
        }
        catch (CurrencyNotFoundException ex){
            System.err.println(ex.getMessage());
        }
    }
    public Product findProduct(String id){
        int i = 0;
        while(products[i] != null){
            if(products[i].getUniqueId().equals(id))
                return products[i];
            i++;
        }
        return null;
    }
    public Product createProduct(String[] record, StringBuilder symbol){
        //the products can share the same manufacturer
        Manufacturer m = setManufacturer(record[2]);
        //we will separte quanity from new with nbps
        String[] val = record[4].split("\u00a0", 2);
        record[4] = val[0];
        if(symbol.toString().equals("")) {
            symbol.append(record[3].charAt(0));
        }
        record[3] = record[3].substring(1);
        // for bigger numbers for ex 2,432.45 wi will have to replace ","
        record[3] = record[3].replace(",", "");
        Product.ProductBuilder pb = new Product.ProductBuilder();
        pb.setUniqueId(record[0])
           .setName(record[1])
           .setManufacturer(m)
           .setPrice(Double.parseDouble(record[3]))
           .setQuantity(Integer.parseInt(record[4]));
        return pb.buildProduct();
    }
    public void addProduct(Product p, int i, Product[] products) throws DuplicateProductException{
        //if there's another product with the same id throw exception
        for(int j = 0; j < i; j++){
            if(products[j].sameProduct(p))
                throw new DuplicateProductException("DuplicateProductException!");
        }
        products[i] = p;
    }
    //next method uses opencsv
    public Product[] readCSV(String filename,  StringBuilder symbol)
            throws IOException, CsvValidationException {
        Product[] products = new Product[MaxLengthArray];
        FileReader fileReader = new FileReader(filename);
        CSVReader csvReader = new CSVReader(fileReader);
        String[] nextRecord;
        int i = 0;
        while((nextRecord = csvReader.readNext()) != null) {
            if(i != 0) {
                Product p = createProduct(nextRecord, symbol);
                try {
                    addProduct(p, i - 1, products);
                }
                catch(DuplicateProductException ex){
                    System.err.println(ex.getMessage());
                }
            }
            i++;
        }
        return products;
    }
    public void listProducts(){
        int i = 0;
        while(this.products[i] != null){
            System.out.println(this.products[i].toString(this.currency.getSymbol()));
            i++;
        }
    }
    public void listProductsByManufacturer(String name){
        int i = 0;
        while(this.products[i] != null){
            if(this.products[i].getManufacturer().getName().equals(name))
                System.out.println(this.products[i].toString(this.currency.getSymbol()));
            i++;
        }
    }
    //next method writes all the products information in a csv file
    public void saveCSV(String filename) throws IOException {
        FileWriter fw = new FileWriter(new File(filename));
        CSVWriter writer = new CSVWriter(fw);
        String[] header ={"uniq id,product_name","manufacturer",
                "price","number_available_in_stock"};
        writer.writeNext(header);
        int i = 0;
        while(products[i] != null){
            double roundOffPrice = Math.round(this.products[i].getPrice() * 100.0) / 100.0;
            String priceCSV = this.currency.getSymbol() + roundOffPrice;
            String quantityCSV = products[i].getQuantity() + "\u00a0" + "NEW";
            String[] data = {products[i].getUniqueId(), products[i].getName(),
                products[i].getManufacturer().getName(), priceCSV, quantityCSV};
            writer.writeNext(data);
            i++;
        }
        writer.close();
        fw.close();
    }
    public Discount createDiscount(String discountType, String name, String value){
        DiscountType type = null;
        if(discountType.equals("FIXED"))
            type = DiscountType.FIXED_DISCOUNT;
        if(discountType.equals("PERCENTAGE"))
            type = DiscountType.PERCENTAGE_DISCOUNT;
        return new Discount(type, name, Double.parseDouble(value));
    }
    public void addDiscount(Discount discount){
        int i = 0;
        while(this.discounts[i] != null){
            i++;
        }
        this.discounts[i] = discount;
    }
    //checks if a discount was already made
    boolean validDiscount(Discount d){
        int i = 0;
        while(this.discounts[i] != null){
            if(this.discounts[i] == d)
                return true;
            i++;
        }
        return false;
    }
    //applies a discount to all the products
    public void applyDiscount(Discount discount) throws DiscountNotFoundException, NegativePriceException{
        //first we will verify if the Discount exists in the Discount list
        if(!validDiscount(discount))
            throw new DiscountNotFoundException("DiscountNotFound");
        int i = 0;
        while(this.products[i] != null){
            double newPrice = products[i].getPrice();
            if(discount.getDiscountType() == DiscountType.PERCENTAGE_DISCOUNT){
                newPrice = newPrice - discount.getValue() * newPrice / 100;
            }
            else{
                if(newPrice - discount.getValue() < 0)
                    throw new NegativePriceException("NegativePriceException");
                newPrice = newPrice - discount.getValue();
            }
            products[i].setPrice(newPrice);
            i++;
        }
        discount.setAsAppliedNow();
    }
    //is looking for a Discount and if it exists returns it else return null
    public Discount findDiscount(String discountType, String value){
        DiscountType type = null;
        if(discountType.equals("FIXED"))
            type = DiscountType.FIXED_DISCOUNT;
        if(discountType.equals("PERCENTAGE"))
            type = DiscountType.PERCENTAGE_DISCOUNT;
        int i = 0;
        while(discounts[i] != null){
            if(discounts[i].getDiscountType().equals(type)
            && discounts[i].getValue() == Double.parseDouble(value))
                return discounts[i];
            i++;
        }
        return null;
    }
    public void listDiscounts(){
        int i = 0;
        while(discounts[i] != null){
            System.out.println(discounts[i].toString());
            i++;
        }
    }
    //returns a Product array which contains the products with the same manufacturer
    public Product[] getProductsByManufacturer(Manufacturer m){
        Product[] productsManufacturer = new Product[m.getCount()];
        int index = 0, i = 0;
        while(this.products[i] != null){
            if(this.products[i].getManufacturer().getName().equals(m.getName())){
                productsManufacturer[index] = products[i];
                index++;
            }
            i++;
        }
        return productsManufacturer;
    }
    //calculates the total price of the given products
    public double calculateTotal(Product[] products){
        double total = 0;
        int i = 0;
        while (products[i] != null){
            total = total + products[i].getPriceAllProducts();
            i++;
        }
        total = Math.round(total * 1000.0) / 1000.0;
        return total;
    }
    //prints information about a certain product with the given id
    public void showProduct(String uniqueId){
        int i = 0;
        while(this.products[i] != null){
            if(this.products[i].getUniqueId().equals(uniqueId)){
                System.out.println(this.products[i].toString(this.currency.getSymbol()));
                break;
            }
            i++;
        }
    }
    //save the entire store object in a binary file
    public static void saveStore(String fileName) throws IOException {
        FileOutputStream f = new FileOutputStream(fileName);
        ObjectOutputStream o = new ObjectOutputStream(f);
        o.writeObject(instance);
        o.flush();
        o.close();
        f.close();
    }
    //loads the entire store object from a binary file
    public static void loadStore(String fileName) throws IOException, ClassNotFoundException {
        FileInputStream f = new FileInputStream(fileName);
        ObjectInputStream o = new ObjectInputStream(f);
        Store s = (Store) o.readObject();
        instance.products = s.products;
        instance.currency = s.currency;
        instance.discounts = s.discounts;
        instance.manufacturers = s.manufacturers;
        instance.name = s.name;
        o.close();
        f.close();
    }
}
