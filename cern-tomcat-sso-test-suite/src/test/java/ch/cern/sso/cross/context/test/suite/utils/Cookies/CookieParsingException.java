//        © Copyright 2020 CERN
//        This software is distributed under the terms of the GNU General Public Licence
//        version 3 (GPL Version 3), copied verbatim in the file "LICENSE". In applying
//        this licence, CERN does not waive the privileges and immunities granted to it
//        by virtue of its status as an Intergovernmental Organization or submit itself
//        to any jurisdiction.
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
