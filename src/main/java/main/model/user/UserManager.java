package main.model.user;

import main.model.util.HashSlingingSlasher;

import java.io.*;
import java.util.ArrayList;

public class UserManager {
    private static File userFile;
    private int currentId;

    /**
     * Create a new usermanager object
     *
     * @param dataRoot the root of the data folder
     * @throws IOException
     */
    public UserManager(String dataRoot) throws IOException {
        userFile = new File(dataRoot + "users.dat");

        if (!userFile.exists())
            userFile.createNewFile();
        try {
            ObjectInputStream s = new ObjectInputStream(new FileInputStream(userFile));
            currentId = ((User) s.readObject()).getId();
            s.close();
        } catch (Exception e) {
            currentId = 0;
        }
    }

    /**
     * Creates a new user and adds it to file
     *
     * @param username The user's username
     * @param password The user's password
     * @return The created user
     */
    public User createUser(String username, String password) throws UsernameOccupiedException, Exception {
        if (usernameTaken(username))
            throw new UsernameOccupiedException("That username is already in use");
        String hash = HashSlingingSlasher.getSaltedHash(password);
        OutputStream buffer = new BufferedOutputStream(new FileOutputStream(userFile));
        ObjectOutputStream stream = new ObjectOutputStream(buffer);
        User newUser = new User(username, hash, currentId++);
        stream.writeObject(newUser);
        buffer.close();
        stream.close();
        return newUser;
    }

    /**
     * Checks a given username to see if it is on file
     *
     * @param username The user's username
     * @return If the username is taken
     * @throws Exception
     */
    private boolean usernameTaken(String username) throws Exception {
        User current;
        try {
            InputStream buffer = new BufferedInputStream(new FileInputStream(userFile));
            ObjectInputStream s = new ObjectInputStream(buffer);
            while (buffer.available() > 0 && (current = (User) s.readObject()) != null) {
                if (current.username.equals(username))
                    return true;
            }
            buffer.close();
            s.close();
        } catch (EOFException e) {
            return false;
        }
        return false;
    }

    /**
     * Checks a given user to see if it is on file
     *
     * @param username The user's username
     * @param password The user's password
     * @return The user if it exsits, if not null
     * @throws InvalidPasswordException
     * @throws Exception
     */
    public User checkUser(String username, String password) throws InvalidPasswordException {
        User current;
        try {
            InputStream buffer = new BufferedInputStream(new FileInputStream(userFile));
            ObjectInputStream s = new ObjectInputStream(buffer);
            while (buffer.available() > 0 && (current = (User) s.readObject()) != null) {
                if (current.username.equals(username))
                    if (compareHash(password, current.passwordHash))
                        return current;
                    else
                        throw new InvalidPasswordException("Invalid Password");
            }
            buffer.close();
            s.close();
        } catch (Exception e) {
            return null;
        }
        return null;
    }

    /**
     * Returns a list of all of the registered users.
     *
     * @return A list of all of the registered users.
     * @throws Exception
     */
    public static ArrayList<User> getAllUsers() {
        ArrayList<User> temp = new ArrayList<User>();
        User current;
        try {
            InputStream buffer = new BufferedInputStream(new FileInputStream(userFile));
            ObjectInputStream s = new ObjectInputStream(buffer);
            while (buffer.available() > 0 && (current = (User) s.readObject()) != null) {
                temp.add(current);
            }
            buffer.close();
            s.close();
        } catch (Exception e) {
            return temp;
        }
        return temp;
    }

    /**
     * Compaires the given password with this user's hash
     *
     * @param password The password to check
     * @return Whether the password matches this user's hash
     * @throws Exception
     */
    public boolean compareHash(String password, String hash) throws Exception {
        return HashSlingingSlasher.check(password, hash);
    }

    public class InvalidPasswordException extends Exception {
        public InvalidPasswordException(String message) {
            super(message);
        }
    }

    public class UsernameOccupiedException extends Exception {
        public UsernameOccupiedException(String message) {
            super(message);
        }
    }
}