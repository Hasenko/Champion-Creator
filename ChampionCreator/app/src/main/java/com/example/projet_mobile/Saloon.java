package com.example.projet_mobile;

import com.google.firebase.database.Exclude;

import java.util.ArrayList;
import java.util.List;

public class Saloon {
    private User creator;
    private String name;
    private int maxSize;
    private boolean isPrivate;
    private String password;
    private List<User> userInSaloon = new ArrayList<>();

    public Saloon(){}
    public Saloon(User creator, String name, int maxSize, boolean isPrivate)
    {
        this.creator = creator;
        this.name = name;

        if(isPrivate)
            this.password = genPassword();
        this.isPrivate = isPrivate;

        setMaxSize(maxSize);
        addUser(creator);
    }

    // setter
    public void setName(String name) { this.name = name; }
    public void setMaxSize(int maxSize)
    {
        if (maxSize < 1)
            this.maxSize = 2;
        else if (maxSize > 5)
            this.maxSize = 5;
        else
            this.maxSize = maxSize;
    }

    // getter
    public User getCreator() { return this.creator; }
    public String getName() { return this.name; }
    public int getMaxSize() { return this.maxSize; }
    public int getCountUserInSaloon() { return this.userInSaloon.size(); }
    public boolean getIsPrivate() { return this.isPrivate; }
    public String getPassword() { return this.password; }
    @Exclude

    public String getStringPrivacy()
    {
        if (isPrivate)
            return "private";
        return "public";
    }

    @Exclude
    public String getStringUserInSaloonSize()
    {
        return getCountUserInSaloon() + "/" + this.maxSize;
    }

    @Exclude
    public String getUid()
    {
        return this.creator.getName() + "_ROOM";
    }

    public List<User> getUserInSaloon() { return this.userInSaloon; }
    public void removeUserFromSaloon(int index) { this.userInSaloon.remove(index); }

    // special
    public void addUser(User user)
    {
        userInSaloon.add(user);
    }
    private String genPassword()
    {
        final String DIGIT = "0123456789";
        String password = "";

        for (int i = 0; i < 8; i++)
            password += DIGIT.charAt((int) (Math.random() * 10));

        return password;
    }

}