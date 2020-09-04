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
package ch.cern.sso.tomcat.common.logger;

import java.util.logging.Level;

/**
 *
 * @author lurodrig
 */
public class DebugLevel extends Level {

    public static final Level DEBUG = new DebugLevel("DEBUG", Level.FINE.intValue() + 1);

    public DebugLevel(String name, int value) {
        super(name, value);
    }
}
