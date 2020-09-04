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
package ch.cern.sso.tomcat.common.aisession;

import ch.cern.sso.tomcat.common.cookies.AisCookieFactory;
import ch.cern.sso.tomcat.common.utils.PrincipalWrapper;
import ch.cern.sso.tomcat.common.utils.Constants;
import javax.servlet.http.Cookie;

/**
 *
 * @author lurodrig
 */
public class AiSessionFactory {

    private AisCookieFactory aisCookieFactory;

    public AiSessionFactory(AisCookieFactory aisCookieFactory) {
        this.aisCookieFactory = aisCookieFactory;
    }

    public String buildAiSession(byte[] clientIP, boolean isCookiesUpperCase, PrincipalWrapper principalWrapper, boolean isLoginAsEnabled, Cookie loginAsCookie) throws NumberFormatException, InstantiationException {
        String value;
        // ip: in the wls implementation we feed this parameter with https://tomcat.apache.org/tomcat-9.0-doc/servletapi/javax/servlet/ServletRequest.html#getRemoteAddr--
        byte[] p_ip = clientIP;
        // PersonID
        Integer p_personId = Integer.valueOf(aisCookieFactory.buildHrId(principalWrapper, isLoginAsEnabled, loginAsCookie));
        int p_cernId = Constants.CERN_ID;
        String p_username = aisCookieFactory.buildLoginName(principalWrapper, isLoginAsEnabled, loginAsCookie);
        if (isCookiesUpperCase) {
            p_username = p_username.toUpperCase();
        }
        String p_language = String.valueOf(aisCookieFactory.buildAiLang(principalWrapper, isLoginAsEnabled, loginAsCookie));
        short p_xres = Constants.DEFAULT_X_RES;
        short p_yres = Constants.DEFAULT_Y_RES;
        boolean p_internal = false;
        int p_originalPersonId = Integer.valueOf(principalWrapper.getHrId());
        long validity = Constants.AI_SESSION_VALIDITY;
        Credentials credentials = new Credentials(p_ip, p_personId, p_cernId, p_username, p_language, p_xres, p_yres, p_internal, p_originalPersonId, validity);
        value = credentials.getSessionString();
        return value;
    }
}
