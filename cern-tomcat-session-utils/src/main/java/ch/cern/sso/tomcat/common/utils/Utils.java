//        © Copyright 2020 CERN
//        This software is distributed under the terms of the GNU General Public Licence
//        version 3 (GPL Version 3), copied verbatim in the file "LICENSE". In applying
//        this licence, CERN does not waive the privileges and immunities granted to it
//        by virtue of its status as an Intergovernmental Organization or submit itself
//        to any jurisdiction.
package ch.cern.sso.tomcat.common.utils;

import org.keycloak.common.util.MultivaluedHashMap;

import java.util.List;
import java.util.Map;

/**
 * @author jgraniec
 */
public class Utils {
    public static MultivaluedHashMap<String, String> listMapToMultivalued(Map<String, List<String>> map){
        MultivaluedHashMap<String, String> result = new MultivaluedHashMap();
        for (final Map.Entry<String, List<String>> entry : map.entrySet()) {
            result.put(entry.getKey(), entry.getValue());
        }
        return result;

    }
}
