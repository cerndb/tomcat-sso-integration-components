package ch.cern.sso.tomcat.common.cookies;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import ch.cern.sso.tomcat.common.utils.Constants;
import ch.cern.sso.tomcat.common.utils.PrincipalWrapper;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.Cookie;

/**
 *
 * @author lurodrig
 */
public class AisCookieFactory {

    private final static Logger LOGGER = Logger.getLogger(AisCookieFactory.class.getName());

    public Cookie[] getCookies(String[] aiCookiesArray, PrincipalWrapper principalWrapper, boolean isCookiesUpperCase, boolean isLoginAsEnabled, Cookie loginAsCookie) {
        // If loginAs is enable and its cookie is present we have to add 3 "extra cookies"
        List<String> aiCookieNames = new ArrayList<String>(Arrays.asList(aiCookiesArray));
        int aisCookiesLength = aiCookieNames.size();
        if (isLoginAsEnabled && loginAsCookie != null) {
            aisCookiesLength = aisCookiesLength + 3;
            // Extend the aicookies array with the extra "original" ones
            aiCookieNames.add(Constants.ORIGINAL_AI_USERNAME_COOKIE);
            aiCookieNames.add(Constants.ORIGINAL_AI_USER_COOKIE);
            aiCookieNames.add(Constants.ORIGINAL_AI_HRID_COOKIE);
            aiCookieNames.add(Constants.ORIGINAL_AI_LANG_COOKIE);
        }

        List<Cookie> aicookies = new ArrayList<Cookie>(aisCookiesLength);
        for (String aiCookieName : aiCookieNames) {
            aicookies.add(this.getCookie(aiCookieName, principalWrapper, isCookiesUpperCase, isLoginAsEnabled, loginAsCookie));
        }
        return aicookies.toArray(new Cookie[aisCookiesLength]);
    }

    public Cookie getCookie(String cookieName, PrincipalWrapper principalWrapper, boolean isCookiesUpperCase, boolean isLoginAsEnabled, Cookie loginAsCookie) {
        Cookie cookie = new Cookie(cookieName, null);
        String value = null;
        switch (cookieName) {
            case Constants.AI_USERNAME: {
                value = this.buildLoginName(principalWrapper, isLoginAsEnabled, loginAsCookie);
                break;
            }
            case Constants.ORIGINAL_AI_USERNAME_COOKIE: {
                value = principalWrapper.getName();
                break;
            }
            case Constants.AI_USER: {
                value = this.buildLoginName(principalWrapper, isLoginAsEnabled, loginAsCookie);
                break;
            }
            case Constants.ORIGINAL_AI_USER_COOKIE: {
                value = principalWrapper.getName();
                break;
            }
            case Constants.AI_LANG: {
                value = String.valueOf(this.buildAiLang(principalWrapper, isLoginAsEnabled, loginAsCookie));
                break;
            }
            case Constants.ORIGINAL_AI_LANG_COOKIE: {
                value = String.valueOf(principalWrapper.getPreferredLanguage().charAt(0));
                break;
            }
            case Constants.AI_HRID: {
                value = this.buildHrId(principalWrapper, isLoginAsEnabled, loginAsCookie);
                break;
            }
            case Constants.ORIGINAL_AI_HRID_COOKIE: {
                value = principalWrapper.getHrId();
                break;
            }
            case Constants.AI_IDENTITY_CLASS: {
                value = principalWrapper.getIdentityClass();
                break;
            }
        }
        if (value != null && isCookiesUpperCase) {
            value = value.toUpperCase();
        }
        LOGGER.log(Level.FINE, "{0} : {1}", new Object[]{cookieName, value});
        cookie.setValue(value);
        return cookie;
    }

    public String buildLoginName(PrincipalWrapper principalWrapper, boolean isLoginAsEnabled, Cookie loginAsCookie) {
        String value = null;
        if (isLoginAsEnabled && loginAsCookie != null) {
            // AI_LOGIN_AS=userName(Required):hrId(optional):aiLang(optional)
            value = this.getValueFromLoginAsCookie(Constants.AI_USERNAME, loginAsCookie);
        } else {
            value = principalWrapper.getName();
        }
        return value;
    }

    public char buildAiLang(PrincipalWrapper principalWrapper, boolean isLoginAsEnabled, Cookie loginAsCookie) {
        char value = 0;
        if (isLoginAsEnabled && loginAsCookie != null) {
            // AI_LOGIN_AS=userName(Required):hrId(optional):aiLang(optional)
            value = this.getValueFromLoginAsCookie(Constants.AI_LANG, loginAsCookie).charAt(0);
        } else {
            if (principalWrapper.getPreferredLanguage() != null && !principalWrapper.getPreferredLanguage().isEmpty()) {
                value = principalWrapper.getPreferredLanguage().charAt(0);
            }
        }
        return value;
    }

    public String buildHrId(PrincipalWrapper principalWrapper, boolean isLoginAsEnabled, Cookie loginAsCookie) {
        String value = null;
        if (isLoginAsEnabled && loginAsCookie != null) {
            // AI_LOGIN_AS=userName(Required):hrId(optional):aiLang(optional)
            value = this.getValueFromLoginAsCookie(Constants.AI_HRID, loginAsCookie);
        } else {
            value = principalWrapper.getHrId();
        }
        return value;
    }

    public String getValueFromLoginAsCookie(String name, Cookie loginAsCookie) {
        String[] values = loginAsCookie.getValue().split(":");
        String patternName = loginAsCookie.getComment();
        String value = null;
        switch (name) {
            case Constants.AI_USERNAME: {
                value = values[0];
                break;
            }
            case Constants.AI_HRID: {
                value = "0";
                if (patternName.equals(AiLoginAsCookiePattern.USERNAME_HRID.toString())
                        || patternName.equals(AiLoginAsCookiePattern.USERNAME_HRID_LANG.toString())) {
                    value = values[1];
                }
                break;
            }
            case Constants.AI_LANG: {
                value = "e";
                if (patternName.equals(AiLoginAsCookiePattern.USERNAME_LANG.toString())) {
                    value = values[1];
                }
                if (patternName.equals(AiLoginAsCookiePattern.USERNAME_HRID_LANG.toString())) {
                    value = values[2];
                }
                break;
            }
        }
        return value;
    }
}
