/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.cern.sso.tomcat.loginas;

/**
 *
 * @author lurodrig
 */
public enum AiLoginAsCookiePattern {
    USERNAME("^[a-zA-Z][a-zA-Z0-9]{2,7}"),
    USERNAME_HRID("^[a-zA-Z][a-zA-Z0-9]{2,7}:[0-9]+"),
    USERNAME_LANG("^[a-zA-Z][a-zA-Z0-9]{2,7}:[e|E|f|F]+"),
    USERNAME_HRID_LANG("^[a-zA-Z][a-zA-Z0-9]{2,7}:[0-9]+:[e|E|f|F]+");

    private final String pattern;

    private AiLoginAsCookiePattern(String pattern) {
        this.pattern = pattern;
    }

    public String getPattern() {
        return this.pattern;
    }
}
