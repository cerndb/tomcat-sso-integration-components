/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.cern.sso.tomcat.common.cookies;

import java.util.Comparator;
import javax.servlet.http.Cookie;

/**
 *
 * @author lurodrig
 */
public class CookieNameComparator implements Comparator<Cookie> {

    @Override
    public int compare(Cookie o1, Cookie o2) {
        return o1.getName().compareTo(o2.getName());
    }

}
