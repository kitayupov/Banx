package com.kitayupov.bank;

public class Bid {

    private String description;
    private String name;
    private int imgRes;
    private Constants.Status statusA;
    private Constants.Status statusB;
    private long date;

    public Bid(String description, String name, int imgRes) {
        this.description = description;
        this.name = name;
        this.imgRes = imgRes;
        this.statusA = Constants.Status.WAIT;
        this.statusB = Constants.Status.WAIT;
        this.date = System.currentTimeMillis();
    }

    public Bid(String description, String name, int imgRes, Constants.Status statusA, Constants.Status statusB, long date) {
        this.description = description;
        this.name = name;
        this.imgRes = imgRes;
        this.statusA = statusA;
        this.statusB = statusB;
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getImgRes() {
        return imgRes;
    }

    public void setImgRes(int imgRes) {
        this.imgRes = imgRes;
    }

    public Constants.Status getStatusA() {
        return statusA;
    }

    public Constants.Status getStatusB() {
        return statusB;
    }

    public void setStatus(int userId, Constants.Status status) {
        if (userId == Constants.ADMINISTRATOR1_ID) {
            statusA = status;
        }
        if (userId == Constants.ADMINISTRATOR2_ID) {
            statusB = status;
        }
    }

    public long getDate() {
        return date;
    }

    @Override
    public String toString() {
        return description + " " + name + " " + statusA.name() + " " + statusB.name() + " " + date;
    }
}
