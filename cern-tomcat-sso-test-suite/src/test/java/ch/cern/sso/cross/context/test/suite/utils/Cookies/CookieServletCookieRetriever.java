package ch.cern.sso.cross.context.test.suite.utils.Cookies;

import java.util.Map;
import java.util.TreeMap;

/**
 * @author jgraniec
 */
public class CookieServletCookieRetriever extends CookieRetriever{
    private String pageSource;
    private final String COOKIE_NAME_VALUE_SEPARATOR = "-->";
    private final String COOKIE_SEPARATOR = "\n";
    public CookieServletCookieRetriever(String pageSource){
        this.pageSource = pageSource;
    }
    @Override
    Map<String, String> getCookiesInternal() throws CookieParsingException {
        String[] cookieValuePairs = pageSource.split(COOKIE_SEPARATOR);

        Map<String, String> displayedCookies = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        for(String cookieValuePair : cookieValuePairs){
            String[] cookie = cookieValuePair.split(COOKIE_NAME_VALUE_SEPARATOR);
            if(cookie.length != 2)
                throw new CookieParsingException();
            displayedCookies.put(cookie[0],cookie[1]);
        }
        return displayedCookies;
    }
}
