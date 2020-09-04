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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.TreeMap;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

/**
 *
 * @author lurodrig
 */
public class SsoRemoteHeadersWrapper extends HttpServletRequestWrapper {

    private final ResourceBundle messages = ResourceBundle.getBundle("Messages");
    private final static Logger LOGGER = Logger.getLogger("ch.cern.sso");

    // Bunch of custom headers to be injected in the request
    private Map<String, String> headersToInject;

    public SsoRemoteHeadersWrapper(HttpServletRequest httpServletRequest, Map<String, String> headers) {
        super(httpServletRequest);
        this.headersToInject = headers;
    }

    @Override
    public Enumeration<String> getHeaderNames() {
        TreeMap<String, String> headers = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        headers.putAll(this.headersToInject);
        Set<String> originalHeadersNames = new HashSet<String>(Collections.list(super.getHeaderNames()));
        for (String originalHeaderName : originalHeadersNames) {
            headers.put(originalHeaderName, super.getHeader(originalHeaderName));
        }
        return Collections.enumeration(headers.keySet());
    }

    @Override
    public Enumeration<String> getHeaders(String name) {
        List<String> values = new ArrayList<String>();
        // If the target application is looking for one of our injected headers
        // do not look in the original one. This avoids header spoofing
        if (headersToInject.containsKey(name.toUpperCase())) {
            values.add(headersToInject.get(name.toUpperCase()));
        } else {
            values = Collections.list(super.getHeaders(name));
        }
        return Collections.enumeration(values);
    }

    @Override
    public String getHeader(String name) {
        String headerValue = super.getHeader(name);
        // Our headers always prevail, so no possible spoofing
        if (headersToInject.containsKey(name.toUpperCase())) {
            headerValue = headersToInject.get(name.toUpperCase());
        }
        return headerValue;
    }

}
