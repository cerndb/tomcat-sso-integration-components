//        © Copyright 2020 CERN
//        This software is distributed under the terms of the GNU General Public Licence
//        version 3 (GPL Version 3), copied verbatim in the file "LICENSE". In applying
//        this licence, CERN does not waive the privileges and immunities granted to it
//        by virtue of its status as an Intergovernmental Organization or submit itself
//        to any jurisdiction.
package ch.cern.sso.cross.context.test.suite.utils.Cookies;

import org.openqa.selenium.Cookie;

import java.util.Random;

/**
 * @author jgraniec
 */
public class CookieFactory {
    private static final long SEED = 3081830083147678839L;
    private static final Random r = new Random(SEED);
    private static final int COOKIE_LENGTH_MIN = 3;
    private static final int COOKIE_LENGTH_MAX = 10;

    public static Cookie createCookie(){
        String name = getRandomString(COOKIE_LENGTH_MIN,COOKIE_LENGTH_MAX);
        return createCookie(name);
    }
    public static Cookie createCookie(String name){
        String value = getRandomString(COOKIE_LENGTH_MIN,COOKIE_LENGTH_MAX);
        return createCookie(name, value);
    }
    public static Cookie createCookie(String name, String value){
       return new Cookie(name, value);
    }

    private static String getRandomString(int lengthMin, int lengthMax){
        int length =r.nextInt(lengthMax-lengthMin)+lengthMin;
        StringBuilder sb = new StringBuilder(length);
        while(length-->0){
            sb.append(((char) (r.nextInt('z' - 'a') + 'a')));
        }
        return sb.toString();

    }
}
