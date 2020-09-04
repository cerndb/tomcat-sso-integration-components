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
