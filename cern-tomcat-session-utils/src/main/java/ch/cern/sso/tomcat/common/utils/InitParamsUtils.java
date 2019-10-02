/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.cern.sso.tomcat.common.utils;

import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;

/**
 *
 * @author lurodrig
 */
public class InitParamsUtils {

    private final ResourceBundle messages = ResourceBundle.getBundle("Messages");
    private final static Logger LOGGER = Logger.getLogger("ch.cern.sso");

    public String getInitParameter(String value, String name, boolean throwException, Level level, String messageKey) throws ServletException {
        String message = Constants.MESSAGE_MISSED_IN_BUNDLE + ": " + messageKey;
        // TODO: investigate why properties bundle is not updated in a shared.loader
        if (messages.containsKey(messageKey)) {
            message = messages.getString(messageKey);
        }
        if (value == null || value.isEmpty()) {
            // Two options here, there is an error on the configuration or is not a required parameter --> just inform
            if (throwException) {
                throw new ServletException(message);
            } else {
                LOGGER.log(level, message);
            }
        }
        return value;
    }

    public String[] getInitParameter(String value, String name, String regex, boolean throwException, Level level, String messageKey) throws ServletException {
        if (value == null || value.isEmpty()) {
            // Two options here, there is an error on the configuration or is not a required parameter --> just inform
            if (throwException) {
                LOGGER.log(level, messages.getString(messageKey));
                throw new ServletException(messages.getString(messageKey));
            } else {
                LOGGER.log(level, messages.getString(messageKey));

            }
        } else {
            // It must contain an array of "regex" separated values
            return value.split(regex);
        }
        return null;
    }

}
