package com.codebook;

import java.util.concurrent.ExecutionException;

import com.mashape.unirest.http.exceptions.UnirestException;

public abstract class USERS {
    abstract void DeletePost(String postID) throws UnirestException, ExceptionHandler;

    abstract BasicResponse DeleteResource(String resourceID)
            throws ExceptionHandler, InterruptedException, ExecutionException;

    abstract void CanAddAdmin(String username) throws ExceptionHandler, InterruptedException, ExecutionException;

    abstract boolean isAdmin();
}
