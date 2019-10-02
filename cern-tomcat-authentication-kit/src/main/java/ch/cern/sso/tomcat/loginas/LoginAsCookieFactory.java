/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.cern.sso.tomcat.loginas;

import ch.cern.sso.tomcat.loginas.AiLoginAsCookiePattern;
import ch.cern.sso.tomcat.exceptions.WrongAiLoginAsCookieFormatException;
import ch.cern.sso.tomcat.common.utils.MessagesKeys;
import ch.cern.sso.tomcat.common.utils.SessionUtils;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.Cookie;

/**
 *
 * @author lurodrig
 */
public class LoginAsCookieFactory {

    private final ResourceBundle messages = ResourceBundle.getBundle("Messages");
    private final static Logger LOGGER = Logger.getLogger(LoginAsCookieFactory.class.getName());
    private final SessionUtils sessionUtils;

    public LoginAsCookieFactory(SessionUtils sessionUtils) {
        this.sessionUtils = sessionUtils;
    }

    public Cookie getLoginAsCookie(Cookie[] cookies, String name, boolean loginAsEnabled) throws WrongAiLoginAsCookieFormatException {
        Cookie cookie = null;
        if (loginAsEnabled) {
            // Look for the loginas cookie
            Cookie loginAsCookie = sessionUtils.searchCookie(cookies, name);
            if (loginAsCookie != null) {
                String loginAsValue = loginAsCookie.getValue();
                if (loginAsValue != null) {
                    // The pattern of this cookie is 
                    // AI_LOGIN_AS=userName(Required):hrId(optional):aiLang(optional)
                    AiLoginAsCookiePattern aiLoginAsCookiePattern = getLoginAsCookiePattern(loginAsValue);
                    if (aiLoginAsCookiePattern != null) {
                        cookie = new Cookie(name, loginAsValue);
                        cookie.setComment(aiLoginAsCookiePattern.toString());
                    }
                }
            }
        } else {
            LOGGER.log(Level.FINE, messages.getString(MessagesKeys.NO_AI_LOGIN_AS_CONFIGURED));
        }
        return cookie;
    }

    private AiLoginAsCookiePattern getLoginAsCookiePattern(String value) throws WrongAiLoginAsCookieFormatException {
        // AI_LOGIN_AS=userName(Required):hrId(optional):aiLang(optional)
        for (AiLoginAsCookiePattern pattern : AiLoginAsCookiePattern.values()) {
            if (value.matches(pattern.getPattern())) {
                return pattern;
            }
        }
        throw new WrongAiLoginAsCookieFormatException("Wrong format of the AI_LOGIN_AS cookie: " + value);
    }

}
