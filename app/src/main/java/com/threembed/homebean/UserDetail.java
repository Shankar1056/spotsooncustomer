package com.threembed.homebean;

import java.io.Serializable;

/**
 * Created by embed on 7/7/15.
 */
public class UserDetail implements Serializable
{
    String FirstName;

    public String getLastName() {
        return LastName;
    }



    public String getFirstName() {
        return FirstName;
    }



    public String getProfilePic() {
        return ProfilePic;
    }



    public String getEmail() {
        return Email;
    }



    String LastName;
    String ProfilePic;
    String Email;

}
