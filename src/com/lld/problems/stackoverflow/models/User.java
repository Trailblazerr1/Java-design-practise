package com.lld.problems.stackoverflow.models;

public class User {
    String userId;
    String name;
    int reputation;

    public User(String userId, String name, int reputation) {
        this.userId = userId;
        this.name = name;
        this.reputation = reputation;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getReputation() {
        return reputation;
    }

    public void setReputation(int reputation) {
        this.reputation = reputation;
    }
}
