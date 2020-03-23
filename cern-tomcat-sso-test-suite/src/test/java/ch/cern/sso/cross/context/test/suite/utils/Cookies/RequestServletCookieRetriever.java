package ch.cern.sso.cross.context.test.suite.utils.Cookies;

import java.util.Map;
import java.util.TreeMap;

/**
 * @author jgraniec
 */
public class RequestServletCookieRetriever extends CookieRetriever{
    private String pageSource;
    private final String COOKIE_SEPARATING_OPERATOR = ";";
    private final String HEADER_SEPARATOR = "\n";
    private final String HEADER_ASSIGNMENT_OPERATOR = ":";
    private final String COOKIE_ASSIGNMENT_OPERATOR = "=";
    private final String COOKIE_HEADER_NAME = "cookie";
    public RequestServletCookieRetriever(String pageSource){
        this.pageSource = pageSource;
    }
    @Override
    Map<String, String> getCookiesInternal() throws CookieParsingException {
        String[] headers = pageSource.split(HEADER_SEPARATOR);
        Map<String, String> displayedCookies = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

        for(String header : headers){ //TODO: refactor
           int headerNameValueIndex = header.indexOf(HEADER_ASSIGNMENT_OPERATOR);
            if(headerNameValueIndex == -1)
                throw new CookieParsingException();

            String headerName = header.substring(0,headerNameValueIndex)
                                        .trim();
            if(!headerName.equalsIgnoreCase(COOKIE_HEADER_NAME))
                continue;

            String[] cookies = header.substring(headerNameValueIndex+1)
                                        .split(COOKIE_SEPARATING_OPERATOR);

            for(String cookie:cookies){
                if(cookie.isEmpty())
                    continue;
                String[] cookieNameValue = cookie.split(COOKIE_ASSIGNMENT_OPERATOR);
                String cookieName = cookieNameValue[0].trim();

                if(cookieNameValue.length != 2)
                    throw new CookieParsingException();
                String cookieValue = cookieNameValue[1].trim();
                displayedCookies.put(cookieName,cookieValue);
            }
        }
        return displayedCookies;

    }

}
