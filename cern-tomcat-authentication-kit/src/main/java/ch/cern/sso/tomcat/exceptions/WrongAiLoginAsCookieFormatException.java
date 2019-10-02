/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.cern.sso.tomcat.exceptions;

/**
 *
 * @author lurodrig
 */
public class WrongAiLoginAsCookieFormatException extends Exception {

    public WrongAiLoginAsCookieFormatException(String message) {
        super(message);
    }
}
