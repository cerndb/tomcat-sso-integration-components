/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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
