package ch.cern.sso.cross.context.test.suite.utils.Cookies;

import java.util.Map;
import java.util.TreeMap;

/**
 * @author jgraniec
 */
public abstract class CookieRetriever { //woof, woof!
    private Map<String, String> cookies;
    abstract Map<String,String> getCookiesInternal() throws CookieParsingException;
    public final Map<String,String> getCookies() throws CookieParsingException{
        this.cookies = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        cookies.putAll(getCookiesInternal());
        return cookies;
    }
}
