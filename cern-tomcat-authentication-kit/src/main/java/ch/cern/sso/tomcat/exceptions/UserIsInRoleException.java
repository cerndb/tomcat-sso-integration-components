/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.cern.sso.tomcat.exceptions;

import ch.cern.sso.tomcat.common.utils.ArrayManipulator;

/**
 *
 * @author lurodrig
 */
public class UserIsInRoleException extends Exception {

    private String message;
    
    public UserIsInRoleException (String message) {
        super(message);
    }

    public UserIsInRoleException(String username, String[] intersection) {
        message = "User " + username + " is member of these FORBIDDEN groups: " + ArrayManipulator.join(",", intersection);
    }

    @Override
    public String toString() {
        return message;
    }
}
