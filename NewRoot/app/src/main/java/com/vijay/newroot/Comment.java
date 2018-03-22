package com.vijay.newroot;

/**
 * Created by HP on 1/21/2018.
 */
public class Comment {
    public String uid;
    public String author;
    public String text;
    public String image;
    public Comment() {
        // Default constructor required for calls to DataSnapshot.getValue(Comment.class)
    }

    public Comment(String uid, String author, String text,String image) {
        this.uid = uid;
        this.author = author;
        this.text = text;
        this.image=image;
    }
    
}
