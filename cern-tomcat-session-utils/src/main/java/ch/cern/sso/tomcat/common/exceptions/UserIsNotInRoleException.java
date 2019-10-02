/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.cern.sso.tomcat.common.exceptions;

/**
 *
 * @author lurodrig
 */
public class UserIsNotInRoleException extends Exception {

    public UserIsNotInRoleException (String message) {
        super(message);
    }

    public UserIsNotInRoleException(String message, Throwable cause) {
        super(message, cause);
    }
}
