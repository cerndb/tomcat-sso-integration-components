package ch.cern.sso.cross.context.test.suite.utils.Cookies;

/**
 * @author jgraniec
 */
public class CookieParsingException extends Exception {
    public CookieParsingException(String errorMessage) {
        super(errorMessage);
    }
    public CookieParsingException() {
        super("Error parsing the cookie!");
    }
}
