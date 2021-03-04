package com.company;

import com.opencsv.exceptions.CsvValidationException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {
    //this function finds a currency by name in the currency list and returns it
    public static Currency findCurrency(ArrayList<Currency> list, String name){
        for (Currency currency : list) {
            if (currency.getName().equals(name)) {
                return currency;
            }
        }
        return null;
    }
    //next function fund a currency by symbol in the currency list and returns it
    public static Currency findCurrencySymbol(ArrayList<Currency> list, String symbol){
        for (Currency currency : list) {
            if (currency.getSymbol().equals(symbol)) {
                return currency;
            }
        }
        return null;
    }
    public static void listCurrencies(ArrayList<Currency> currencies){
        for (Currency currency : currencies) {
            System.out.println(currency.getName() + " " + currency.getParityToEur());
        }
    }
    public static void loadCommand(Store store, ArrayList<Currency> currencies,
                                   String[] command) throws IOException, CsvValidationException {
        //we use builder so we can acces here the string value found in the readCSV
        // function and save in the builder
        StringBuilder currencySymbol = new StringBuilder();
        Product[] products = store.readCSV(command[1], currencySymbol);
        Currency usedCurrency = findCurrencySymbol(currencies, currencySymbol.toString());
        //building the store the way it was in the input.csv file
        store.updateStore(products, usedCurrency, "Kaufland");
        Currency euroCurrency = findCurrency(currencies, "EUR");
        //change the currency to EUR
        store.applyChangeCurrency(euroCurrency, currencies);
    }
    //calculate the total price of the given products id
    public static void calculateTotal(Store store, String[] command){
        //first we build the array
        Product[] productsSold = new Product[1000];
        for(int i = 1; i < command.length; i++){
            Product product = store.findProduct(command[i]);
            productsSold[i - 1] = product;
        }
        System.err.println(store.getCurrency().getSymbol() +
                store.calculateTotal(productsSold));
    }
    public static void addDiscountCommand(Store store, String[] command){
        //since the nameDiscount contains spaces we will concatenate all the
        //strings splitted by space
        StringBuilder nameDiscount = new StringBuilder();
        for(int i = 3; i < command.length; i++){
            nameDiscount.append(command[i]).append(" ");
        }
        nameDiscount.setLength(nameDiscount.length() - 1);////////???????
        Discount newdiscount = store.createDiscount(command[1], nameDiscount.toString(), command[2]);
        store.addDiscount(newdiscount);
    }
    public static void applyDiscountCommand(Store store, String[] command){
        //we will find the discount knowing it's value and type
        Discount discount = store.findDiscount(command[1], command[2]);
        try{
            store.applyDiscount(discount);
        }
        catch (DiscountNotFoundException | NegativePriceException ex){
            System.out.println(ex.getMessage());
        }
    }
    public static void takeCommands(Store store, ArrayList<Currency> currencies) throws
            IOException, CsvValidationException, ClassNotFoundException {
        Scanner sc = new Scanner(System.in);
        while(sc.hasNextLine()){
            String line = sc.nextLine();
            String[] command = line.split(" ");
            switch(command[0]) {
                case "listcurrencies":
                    listCurrencies(currencies);
                    break;
                case "getstorecurrency":
                    System.out.println(store.getCurrency().getName());
                    // code block
                    break;
                case "addcurrency":
                    Currency newCurrency = store.createCurrency(command[2], command[1],
                            Double.parseDouble(command[3]));
                    currencies.add(newCurrency);
                    break;
                case "loadcsv":
                    loadCommand(store, currencies, command);
                    break;
                case "savecsv":
                    store.saveCSV(command[1]);
                    break;
                case "setstorecurrency":
                    Currency currency = findCurrency(currencies, command[1]);
                    store.applyChangeCurrency(currency, currencies);
                    break;
                case "updateparity":
                    Currency currency1 = findCurrency(currencies, command[1]);
                    if(currency1 != null)
                        currency1.updateParity(Double.parseDouble(command[2]));
                    break;
                case "listproducts":
                    store.listProducts();
                    break;
                case "showproduct":
                    store.showProduct(command[1]);
                    break;
                case "listmanufacturers":
                    store.listManufacturers();
                    break;
                case "listproductsbymanufacturarer":
                    store.listProductsByManufacturer(command[1]);
                    break;
                case "listdiscounts":
                    store.listDiscounts();
                    break;
                case "addiscount":
                    addDiscountCommand(store, command);
                    break;
                case "applydiscount":
                    applyDiscountCommand(store, command);
                    break;
                case "calculatetotal":
                    calculateTotal(store, command);
                    break;
                case "exit":
                    System.exit(0);
                    break;
                case "quit":
                    System.exit(1);
                    break;
                case "savestore":
                    Store.saveStore("BinarySavedFile.bin");
                    break;
                case "loadstore":
                    Store.loadStore("BinarySavedFile.bin");
                     break;
                default:
                    // code block
            }

        }
        sc.close();
    }
    public static void main(String[] args) throws IOException,
            CsvValidationException, ClassNotFoundException {
        //we create the unique store instance using singleton design pattern
        Store store = Store.Instance();
        //the currency list has to have the Euro element
        Currency currency = store.createCurrency("€", "EUR", 1.0);
        ArrayList<Currency> currencyList = new ArrayList<>();
        currencyList.add(currency);
        //store has Eur set as currency by default
        store.setCurrency(currencyList.get(0));
        store.initializeManufacturersDiscounts();
        //the program will take commands from Stdin
        takeCommands(store, currencyList);
    }
}
