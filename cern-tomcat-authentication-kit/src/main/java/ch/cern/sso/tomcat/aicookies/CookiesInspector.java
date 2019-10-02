package ch.cern.sso.tomcat.aicookies;

import ch.cern.sso.tomcat.common.utils.ArrayManipulator;
import ch.cern.sso.tomcat.common.cookies.CookieNameComparator;
import ch.cern.sso.tomcat.common.exceptions.HeaderInjectionException;
import ch.cern.sso.tomcat.common.utils.MessagesKeys;
import ch.cern.sso.tomcat.common.utils.PrincipalWrapper;
import ch.cern.sso.tomcat.common.utils.SessionUtils;
import java.rmi.RemoteException;
import java.util.Arrays;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;

/**
 *
 * @author lurodrig
 */
public class CookiesInspector {

    private final ResourceBundle messages = ResourceBundle.getBundle("Messages");
    private final static Logger LOGGER = Logger.getLogger("ch.cern.sso");

    private final SessionUtils sessionUtils;

    public CookiesInspector(SessionUtils sessionUtils) {
        this.sessionUtils = sessionUtils;
    }

    public void preventSpoofing(Cookie[] requestCookies, String[] aiCookieNames, PrincipalWrapper principalWrapper) throws HeaderInjectionException, ServletException, InstantiationException, RemoteException {
        // If the request has no requestCookies be lazy
        if (requestCookies != null) {
            String[] requestCookieNames = getCookieNames(requestCookies);
            // If no aicookies have been configured be lazy
            if (aiCookieNames != null) {
                String[] intersect = ArrayManipulator.intersection(requestCookieNames, aiCookieNames);
                if (intersect != null && intersect.length > 0) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("The username " + principalWrapper.getName() + " is injecting the cookies:\n");
                    for (String cookieName : intersect) {
                        Arrays.sort(requestCookies, new CookieNameComparator());
                        Cookie tmp = new Cookie(cookieName, null);
                        int i = Arrays.binarySearch(requestCookies, tmp, new CookieNameComparator());
                        sb.append(cookieName + " with value " + requestCookies[i].getValue() + "\n");
                    }
                    throw new HeaderInjectionException(sb.toString());
                }
            } else {
                LOGGER.log(Level.WARNING, messages.getString(MessagesKeys.NO_AI_COOKIES_CONFIGURED));
            }
        } else {
            // If user clean the cookies this can happen
            LOGGER.log(Level.WARNING, messages.getString(MessagesKeys.NO_COOKIES_IN_HTTP_REQUEST));
        }
    }

    private String[] getCookieNames(Cookie[] cookies) {
        String[] result = new String[cookies.length];
        for (int i = 0; i < cookies.length; i++) {
            result[i] = cookies[i].getName();
        }
        return result;
    }
}
