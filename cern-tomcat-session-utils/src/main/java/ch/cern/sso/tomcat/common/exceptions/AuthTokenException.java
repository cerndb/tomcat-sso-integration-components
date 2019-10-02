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
public class AuthTokenException extends Exception {

    public AuthTokenException(String message) {
        super(message);
    }
   
}
