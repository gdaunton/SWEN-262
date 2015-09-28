package model;

import model.util.HashSlingingSlasher;

public class User{
    private String username;
    private String password_hash;

    /**
     * Creates a new User object
     * @param username The user's username
     * @param password_hash The user's password hash
     */
    public User(String username, String password_hash){
        this.username = username;
        this.password_hash = password_hash;
    }

    /**
     * Gets the user's username
     * @return The user's username
     */
    public String getUsername(){
        return username;
    }

    /**
     * Compaires the given password with this user's hash
     * @param password The password to check
     * @return Whether the password matches this user's hash
     * @throws Exception
     */
    public boolean compareHash(String password) throws Exception{
        return HashSlingingSlasher.check(password, this.password_hash);
    }
}