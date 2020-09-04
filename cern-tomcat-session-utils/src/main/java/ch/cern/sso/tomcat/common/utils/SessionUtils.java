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
package ch.cern.sso.tomcat.common.utils;

import java.util.UUID;
import java.util.logging.Logger;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author lurodrig
 */
public class SessionUtils {

    private final Logger LOGGER = Logger.getLogger("ch.cern.sso");

    public Cookie searchCookie(HttpServletRequest request, String name) {
        Cookie cookie = null;
        if (request.getCookies() != null) {
            // Take the first ocurrence and exit. Not millions of cookies so no need for better algorithm than linear search
            cookie = searchCookie(request.getCookies(), name);
        }
        return cookie;
    }

    public Cookie searchCookie(Cookie[] cookies, String name) {
        if (cookies == null) {
            return null;
        }
        Cookie cookie = null;
        for (Cookie c : cookies) {
            if (name.equals(c.getName())) {
                cookie = c;
            }
        }
        return cookie;
    }

    public boolean isValid(Cookie cookie) {
        // Check the cookie max age
        return cookie.getMaxAge() != 0;
    }

    public Cookie createCookie(String name, String path) {
        Cookie cookie = new Cookie(name, UUID.randomUUID().toString());
        cookie.setPath(path);
        return cookie;
    }

    public boolean isLogoutRequest(HttpServletRequest request) {
        return isKeycloakLogoutRequest(request) || isCustomLogoutRequest(request);
    }

    public boolean isKeycloakLogoutRequest(HttpServletRequest request) {
        return (request.getQueryString() != null && request.getQueryString().contains("GLO"));
    }

    public boolean isCustomLogoutRequest(HttpServletRequest request) {
        return (request.getRequestURI() != null && request.getRequestURI().contains("saml2slo"));
    }
}
