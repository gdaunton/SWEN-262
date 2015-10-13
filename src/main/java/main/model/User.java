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
}