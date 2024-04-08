package com.example.userservice.Model;

import javax.naming.AuthenticationException;

public class UserDisabledException extends AuthenticationException {
    public UserDisabledException(String msg, Throwable t) {
        super(msg);
    }

    public UserDisabledException(String msg) {
        super(msg);
    }
}
