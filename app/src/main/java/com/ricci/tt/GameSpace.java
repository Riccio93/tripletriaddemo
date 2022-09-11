package com.ricci.tt;

import android.graphics.Bitmap;
import android.graphics.Rect;

public class GameSpace {
    private Rect rect;
    private Card card;
    private int owner;
    private Bitmap image;
    private String imageName;

    public GameSpace(int i){
        owner=i;
    }

    public void setRect(Rect rect){
        this.rect = rect;
    }

    public Rect getRect(){
        return rect;
    }

    public void setOwner(int owner){
        this.owner=owner;
    }

    public int getOwner(){
        return owner;
    }

    public void setCard(Card card){
        this.card=card;
    }

    public Card getCard(){
        return card;
    }

    public void setImage(Bitmap image){this.image = image;}

    public Bitmap getImage(){return image;}

    public void setImageName(String imageName){
        this.imageName=imageName;
    }

    public String getImageName(){
        return imageName;
    }
}
