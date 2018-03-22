package com.vijay.newroot;

/**
 * Created by HP on 1/11/2018.
 */
public class Users {
    public String image;
    public String name;
    public int credit;
    public Users(){

    }
    public Users(String image, String name,int credit) {
        this.name=name;
        this.image = image;
        this.credit=credit;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getCredit() {
        return credit;
    }

    public void setCredit(int credit) {
        this.credit = credit;
    }
}
