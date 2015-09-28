package model;

import model.util.HashSlingingSlasher;

public class User{
    private String username;
    private String password_hash;

    public User(String username, String password_hash){
        this.username = username;
        this.password_hash = password_hash;
    }

    public String getUsername(){
        return username;
    }

    public boolean compareHash(String password) throws Exception{
        return HashSlingingSlasher.check(password, this.password_hash);
    }
}