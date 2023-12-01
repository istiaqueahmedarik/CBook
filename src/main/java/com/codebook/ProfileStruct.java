package com.codebook;

import java.util.concurrent.ExecutionException;

import org.checkerframework.checker.units.qual.t;

import com.mashape.unirest.http.exceptions.UnirestException;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProfileStruct extends USERS {
    public String displayName;
    public String username;
    public String userImage;
    public String id_token;
    public boolean isAdmin;

    @Override
    void DeletePost(String postID) throws UnirestException, ExceptionHandler {
        if (isAdmin || MainUser.username.equals(username)) {
            Methods.deletePost(postID);
        } else {
            throw new ExceptionHandler("You are not an admin");
        }

    }

    @Override
    BasicResponse DeleteResource(String resourceID) throws ExceptionHandler, InterruptedException, ExecutionException {
        if (isAdmin || MainUser.username.equals(username)) {
            System.out.println("deleting");
            return Methods.DeleteResource(resourceID);
        } else {
            throw new ExceptionHandler("You are not an admin");
        }

    }

    @Override
    void CanAddAdmin(String username) throws ExceptionHandler, InterruptedException, ExecutionException {
        if (isAdmin || MainUser.username.equals(username)) {
            Methods.addAdmin(username);
        } else {
            throw new ExceptionHandler("You are not an admin");
        }

    }

    @Override
    boolean isAdmin() {
        return isAdmin;
    }
}
