package ch.cern.sso.tomcat.common.utils;

import javax.servlet.http.Cookie;
import java.util.Comparator;

/**
 * Created by Jakub Granieczny on 2020-03-13.
 */
public class CookieNameComparator implements Comparator<Cookie> {


    @Override
    public int compare(Cookie o1, Cookie o2) {
        return o1.getName().compareTo(o2.getName());
    }
}
