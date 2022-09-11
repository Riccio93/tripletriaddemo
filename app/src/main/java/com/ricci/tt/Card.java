package com.ricci.tt;

public class Card {
    private int id;
    private String name;
    private int lValue;
    private int tValue;
    private int rValue;
    private int bValue;
    private boolean played = false;

    //Costruttore
    public Card(int id, String name, int lValue, int tValue, int rValue, int bValue){
        this.id = id;
        this.name = name;
        this.lValue = lValue;
        this.tValue = tValue;
        this.rValue = rValue;
        this.bValue = bValue;
    }

    //Costruttore senza parametri, utilizzato solo in getAllCards in DbHandler (I parametri vengono settati in quel metodo)
    public Card(){}

    //Metodi get e set per tutti i parametri della carta
    public int getId(){
        return id;
    }
    public String getName(){return name;}
    public int getlValue(){
        return lValue;
    }
    public int gettValue(){
        return tValue;
    }
    public int getrValue(){
        return rValue;
    }
    public int getbValue(){
        return bValue;
    }
    public void setId(int id){
        this.id = id;
    }
    public void setName(String name) { this.name=name; }
    public void setlValue(int lValue){
        this.lValue = lValue;
    }
    public void settValue(int tValue){
        this.tValue = tValue;
    }
    public void setrValue(int rValue){
        this.rValue = rValue;
    }
    public void setbValue(int bValue){
        this.bValue = bValue;
    }
    public boolean isPlayed(){
        return played;
    }
    public void setPlayed(){played = true;}
}
