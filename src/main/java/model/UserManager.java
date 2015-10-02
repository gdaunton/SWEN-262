package model;

import model.util.HashSlingingSlasher;

import java.io.*;

public class UserManager{
    private File userFile;
    private int currentId;

    /**
     * Start up the User Manager
     */
    public UserManager(){
        userFile = new File("users.dat");
        try {
            ObjectInputStream s = new ObjectInputStream(new FileInputStream(userFile));
            currentId = ((User)s.readObject()).getId();
            s.close();
        }catch(Exception e) {
            currentId = 0;
        }
    }

    /**
     * Creates a new user and adds it to file
     * @param username The user's username
     * @param password The user's password
     * @return The created user
     */
    public User createUser(String username, String password){
        try {
            String hash = HashSlingingSlasher.getSaltedHash(password);
            ObjectOutputStream stream = new ObjectOutputStream(new FileOutputStream(userFile));
            User newUser = new User(username, hash, currentId++);
            stream.writeObject(newUser);
            stream.close();
            return newUser;
        }catch(Exception e){
            System.out.println("There was an error creating a new user");
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Checks a given user to see if it is on file
     * @param username The user's username
     * @param password The user's password
     * @return The user if it exsits, if not null
     * @throws InvalidPasswordException
     * @throws Exception
     */
    public User checkUser(String username, String password) throws InvalidPasswordException, Exception{
        User current;
        ObjectInputStream s = new ObjectInputStream(new FileInputStream(userFile));
        while ((current = (User)s.readObject()) != null) {
            if (current.username.equals(username))
                if(compareHash(password, current.passwordHash))
                    return current;
                else
                    throw new InvalidPasswordException("Invalid Password");
        }
        s.close();
        return null;
    }

    /**
     * Compaires the given password with this user's hash
     * @param password The password to check
     * @return Whether the password matches this user's hash
     * @throws Exception
     */
    public boolean compareHash(String password, String hash) throws Exception{
        return HashSlingingSlasher.check(password, hash);
    }

    public class InvalidPasswordException extends Exception{
        public InvalidPasswordException(String message){
            super(message);
        }
    }
}