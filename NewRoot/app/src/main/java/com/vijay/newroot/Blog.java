package com.vijay.newroot;

/**
 * Created by HP on 1/22/2018.
 */
public class Blog {
    String desc;
    String image;
    String title;
    String username;
    String profileImage;
    private long time;
    int likeCount;
    String location;
    String uid;
    public Blog(){

    }

    public Blog(String title, String desc, String image, String username, String profileImage, long time,int likeCount,String location,String uid) {
        this.title = title;
        this.desc = desc;
        this.image = image;
        this.username = username;
        this.profileImage=profileImage;
        this.time=time;
        this.likeCount=likeCount;
        this.location=location;
        this.uid=uid;

    }
    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return this.desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getImage() {
        return this.image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
