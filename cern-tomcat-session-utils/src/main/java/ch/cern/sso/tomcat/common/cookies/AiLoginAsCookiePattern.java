/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
//        © Copyright 2020 CERN
//        This software is distributed under the terms of the GNU General Public Licence
//        version 3 (GPL Version 3), copied verbatim in the file "LICENSE". In applying
//        this licence, CERN does not waive the privileges and immunities granted to it
//        by virtue of its status as an Intergovernmental Organization or submit itself
//        to any jurisdiction.
package ch.cern.sso.tomcat.common.cookies;

/**
 *
 * @author lurodrig
 */
public enum AiLoginAsCookiePattern {
    USERNAME("[a-zA-Z]{3,8}"),
    USERNAME_HRID("[a-zA-Z]{3,8}:[0-9]+"),
    USERNAME_LANG("[a-zA-Z]{3,8}:[e|E|f|F]+"),
    USERNAME_HRID_LANG("[a-zA-Z]{3,8}:[0-9]+:[e|E|f|F]+");

    private final String pattern;

    private AiLoginAsCookiePattern(String pattern) {
        this.pattern = pattern;
    }

    public String getPattern() {
        return this.pattern;
    }
}
