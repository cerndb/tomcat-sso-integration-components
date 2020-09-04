//        © Copyright 2020 CERN
//        This software is distributed under the terms of the GNU General Public Licence
//        version 3 (GPL Version 3), copied verbatim in the file "LICENSE". In applying
//        this licence, CERN does not waive the privileges and immunities granted to it
//        by virtue of its status as an Intergovernmental Organization or submit itself
//        to any jurisdiction.
package ch.cern.sso.tomcat.common.aisession;

//SQL
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import javax.servlet.http.Cookie;

/**
 * Utilities - collection of miscellaneous static methods used by the servlets.
 */
public class Utils {

    private static final String c_cookieName = "AI_SESSION";

    // ============================================================================
    // The following functions read the data from the resource property file.
    //
    public static String getValue(String p_key) throws MissingResourceException {
        return ResourceBundle.getBundle("aislogin").getString(p_key);
    }

    public static void validateAiSession(Cookie ai_session) throws InstantiationException, AiSessionIsExpiredException, AiSessionIsNotValid {
        Credentials credentials = new Credentials(ai_session.getValue());
        if (credentials.isExpired()) {
            throw new AiSessionIsExpiredException();
        }
        if (!credentials.isValidDecrypt()) {
            throw new AiSessionIsNotValid();
        }
    }

    public static void validateCredentials(Credentials credentials) throws InstantiationException, AiSessionIsExpiredException, AiSessionIsNotValid {
        if (credentials.isExpired()) {
            throw new AiSessionIsExpiredException();
        }
        if (!credentials.isValidDecrypt()) {
            throw new AiSessionIsNotValid();
        }
    }
}
