package com.stancu;

import com.stancu.v1.PasswordValidator;
import com.stancu.v2_refactored.PasswordValidator2;

public class Main {

    public static void main(String[] args) {

        PasswordValidator passwordValidator = new PasswordValidator();
//        PasswordValidator2 passwordValidator = new PasswordValidator2();
        String password = "bbb";
        System.out.println(passwordValidator.strongPasswordChecker(password));

    }
}
