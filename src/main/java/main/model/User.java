package main.model;

import java.io.Serializable;

public class User implements Serializable {
    public String username;
    public String passwordHash;
    private int id;

    public User(String username, String hash, int id){
        this.username = username;
        this.passwordHash = hash;
    }

    /**
     * Get this users ID
     * @return
     */
    public int getId(){
        return this.id;
    }

    @Override
    public String toString() {
        return username;
    }

    @Override
    public boolean equals(Object user) {
        return ((User)user).username.equals(this.username) && ((User)user).passwordHash.equals(this.passwordHash);
    }
}