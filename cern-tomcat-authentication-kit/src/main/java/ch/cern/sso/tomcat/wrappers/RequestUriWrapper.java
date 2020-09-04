/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
//        © Copyright 2020 CERN
//        This software is distributed under the terms of the GNU General Public Licence
//        version 3 (GPL Version 3), copied verbatim in the file "LICENSE". In applying
//        this licence, CERN does not waive the privileges and immunities granted to it
//        by virtue of its status as an Intergovernmental Organization or submit itself
//        to any jurisdiction.
package ch.cern.sso.tomcat.wrappers;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

/**
 *
 * @author lurodrig
 */
public class RequestUriWrapper extends HttpServletRequestWrapper {
    
    /**
     * Avoid that the use of blanks and other special characters breaks applications like wos.cern.ch (Qualiac)
     */
    private final boolean isUriEncoded;

    public RequestUriWrapper(HttpServletRequest httpServletRequest, boolean isUriEncoded) {
        super(httpServletRequest);
        this.isUriEncoded = isUriEncoded;
    }

    @Override
    public String getRequestURI() {
        String result = super.getRequestURI();
        if (isUriEncoded) {
            try {
                URI uri = new URI(null, null, result, null, null);
                result = uri.toString();
            } catch (URISyntaxException ex) {
                Logger.getLogger(RequestUriWrapper.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return result;
    }
}
