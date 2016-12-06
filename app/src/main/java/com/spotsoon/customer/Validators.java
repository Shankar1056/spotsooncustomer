package com.spotsoon.customer;
import com.spotsoon.customer.R;

/**
 * Created by embed on 11/5/15.
 */
public class Validators {
    //name validation
    public boolean lastName_status(String lastName) {
        Boolean flag = true;
        for (int i = 0; i < lastName.length(); i++) {
            if (!((lastName.charAt(i) >= 'A' && lastName.charAt(i) <= 'Z') || (lastName.charAt(i) >= 'a' && lastName.charAt(i) <= 'z') || lastName.charAt(i)==' ')) {
                flag = false;
                break;
            }

        }

        return flag;
    }

    // validating emailid
    public boolean emailId_status(String email) {
        int length = email.length();
        boolean flag1 = false;
        boolean flag2 = false;
        boolean flag3 = false;
        boolean flag4 = false;
        boolean flag5 = false;
        boolean flag6 = false;
        boolean flag7 = false;
        int count = 0;

        //Condition 1 lenght shoul be >3 and <20
        if ((length > 3 && length < 35) == true)
            flag1 = true;
        else
            flag1 = false;

        //The emailId must include "@" followed by a minimum of 1 and maximum of 2 "." characters.
        int temp = email.length();
        if (email.contains("@")) {
            flag2 = true;
            int a = email.indexOf("@");
            for (int i = a; i < temp; i++) {
                if (email.charAt(i) == '.') {
                    flag3 = true;
                    count = count + 1;
                }
            }
            if (count < 1 || count > 2) {
                flag4 = false;
            } else {
                flag4 = true;
            }
        } else {
            flag2 = false;

        }


        if (email.matches("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,4}$")) //Unable to get the right RegEx here!
            flag5 = true;
        else
            flag5 = false;

        //The first letter of the email Id must be in Upper Case.
        if (!((email.charAt(0) >= 'A' && email.charAt(0) <= 'Z') || (email.charAt(0) >= 'a' && email.charAt(0) <= 'z'))) {
            flag6 = false;

        } else {
            flag6 = true;
        }
        if (flag1 == true && flag2 == true && flag3 == true && flag4 == true && flag5 == true)
            return true;
        else {

            return false;
        }
    }

    // validating password with retype password
    public boolean isValidPassword(String pass) {
        if (pass != null && pass.length() >= 6) {
            return true;
        }
        return false;
    }
//contact validation
    public boolean contac_Status(String no) {
        if(no.matches("[0-9]{10}")){
            return true;
        }
        else {

            return false;
        }
    }




}

