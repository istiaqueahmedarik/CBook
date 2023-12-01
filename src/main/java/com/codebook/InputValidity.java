package com.codebook;

import java.util.concurrent.ExecutionException;

public class InputValidity {
    public static void LoginInput(String email, String password) throws ExceptionHandler {
        if (email.length() == 0) {
            throw new ExceptionHandler("Email cannot be empty");
        }
        if (password.length() == 0) {
            throw new ExceptionHandler("Password cannot be empty");
        }
    }

    public static void signUpValidator(String name, String email, String password, String repeatPass, String username)
            throws ExceptionHandler, InterruptedException, ExecutionException {
        if (name.length() == 0) {
            throw new ExceptionHandler("Name cannot be empty");
        }
        if (email.length() == 0) {
            throw new ExceptionHandler("Email cannot be empty");
        }
        if (password.length() == 0) {
            throw new ExceptionHandler("Password cannot be empty");
        }
        if (repeatPass.length() == 0) {
            throw new ExceptionHandler("Repeat Password cannot be empty");
        }
        if (username.length() == 0) {
            throw new ExceptionHandler("Username cannot be empty");
        }
        if (password.length() < 8) {
            throw new ExceptionHandler("Password must be atleast 8 characters long");
        }
        if (Methods.isUserNameExists(username) == true) {
            throw new ExceptionHandler("Username already exists");
        }
        if (!password.equals(repeatPass)) {
            throw new ExceptionHandler("Passwords do not match");
        }
    }

    public static void intermediateSignUpValidator(Integer status_code) throws ExceptionHandler {
        if (status_code == 401) {
            throw new ExceptionHandler("User already exists");
        }
        if (status_code == 404) {
            throw new ExceptionHandler("Email already exists");
        }
        if (status_code == 201) {
            return;
        }
    }

    public static void codeInput(String title, String code, String language) throws ExceptionHandler {
        if (title.length() == 0) {
            throw new ExceptionHandler("Title cannot be empty");
        }
        if (code.length() == 0) {
            throw new ExceptionHandler("Code cannot be empty");
        }
        if (language==null || language.length() == 0) {
            throw new ExceptionHandler("Language cannot be empty");
        }
    }
}
