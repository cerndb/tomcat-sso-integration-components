//        © Copyright 2020 CERN
//        This software is distributed under the terms of the GNU General Public Licence
//        version 3 (GPL Version 3), copied verbatim in the file "LICENSE". In applying
//        this licence, CERN does not waive the privileges and immunities granted to it
//        by virtue of its status as an Intergovernmental Organization or submit itself
//        to any jurisdiction.
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
