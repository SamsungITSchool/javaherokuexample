package com.samsung.srr.itscool.model;

import org.apache.commons.validator.routines.EmailValidator;
import org.mindrot.jbcrypt.BCrypt;

/**
 * Created by raiym on 1/13/16.
 */
public class User {
    public Long id;
    public String email;
    public String password;
    public String token;


    public boolean isPasswordValid(String candidate) {
        this.password = this.password.replace("$2y$10$", "$2a$10$");
        return BCrypt.checkpw(candidate, this.password);
    }

    public boolean isDataValid() {
        return !(this.email == null || this.password == null) && EmailValidator.getInstance().isValid(this.email);

    }
}
