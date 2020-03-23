/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.cern.sso.tomcat.common.utils;

import ch.cern.sso.tomcat.common.cookies.CookieNameComparator;

import javax.servlet.http.Cookie;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Utility class for array manipulations
 *
 * @author lurodrig
 */
public class ArrayManipulator {

    /**
     * From
     * https://stackoverflow.com/questions/80476/how-can-i-concatenate-two-arrays-in-java
     *
     * @param <T>
     * @param a
     * @param b
     * @return
     */
    public static <T> T[] concatenate(T[] a, T[] b) {
        int aLen = a == null ? 0 : a.length;
        int bLen = b == null ? 0 : b.length;

        T[] c = (T[]) Array.newInstance(a.getClass().getComponentType(), aLen + bLen);
        if (a != null) {
            System.arraycopy(a, 0, c, 0, aLen);
        }
        if (b != null) {
            System.arraycopy(b, 0, c, aLen, bLen);
        }

        return c;
    }

    /**
     * From
     * https://stackoverflow.com/questions/35395317/transform-all-elements-of-a-list-of-strings-to-upper-case
     *
     * @param a
     * @param b
     * @param groupsEqualsStrict
     * @return
     */
    public static String[] intersection(String[] a, String[] b, boolean groupsEqualsStrict) {
        List<String> l1 = Arrays.asList(a);
        List<String> l2 = Arrays.asList(b);
        if (!groupsEqualsStrict) {
            toUpperCase(l1);
            toUpperCase(l2);
        }
        Set<String> s1 = new HashSet<>(l1);
        Set<String> s2 = new HashSet<>(l2);
        s1.retainAll(s2);
        return s1.toArray(new String[0]);
    }

    /**
     * From
     * https://stackoverflow.com/questions/17863319/java-find-intersection-of-two-arrays/37788939
     *
     * @param a
     * @param b
     * @return
     */
    public static String[] intersection(String[] a, String[] b) {
        Set<String> s1 = new HashSet<>(Arrays.asList(a));
        Set<String> s2 = new HashSet<>(Arrays.asList(b));
        s1.retainAll(s2);
        return s1.toArray(new String[0]);
    }

    /**
     * From
     * https://stackoverflow.com/questions/5374311/convert-arrayliststring-to-string-array
     *
     * @param l
     * @return
     */
    public static String[] toArray(List<String> l) {
        String[] result = new String[l.size()];
        result = l.toArray(result);
        return result;
    }

    public static void toUpperCase(List<String> list) {
        for (int i = 0; i < list.size(); i++) {
            list.set(i, list.get(i).toUpperCase());
        }
    }

    /**
     * Nice trick for Java 7:
     * https://www.techiedelight.com/join-multiple-strings-java-using-delimiter/
     *
     * @param array
     * @param delimiter
     * @return
     */
    public static String join(String delimiter, String[] array) {
        StringBuilder result = new StringBuilder();
        String prefix = "";
        for (String s : array) {
            result.append(prefix).append(s);
            prefix = delimiter;
        }
        return result.toString();
    }
    
     // From https://www.geeksforgeeks.org/remove-an-element-at-specific-index-from-an-array-in-java/ 
    public static Object[] removeTheElement(Object[] arr,
            int index) {

        // If the array is empty 
        // or the index is not in array range 
        // return the original array 
        if (arr == null
                || index < 0
                || index >= arr.length) {
            return arr;
        }

        // Create another array of size one less 
        Object[] anotherArray = new Object[arr.length - 1];

        // Copy the elements from starting till index 
        // from original array to the other array 
        System.arraycopy(arr, 0, anotherArray, 0, index);

        // Copy the elements from index + 1 till end 
        // from original array to the other array 
        System.arraycopy(arr, index + 1,
                anotherArray, index,
                arr.length - index - 1);

        // return the resultant array 
        return anotherArray;
    }
    
    public static int searchIndex(Cookie[] cookiesFromWrappedRequest, Cookie ai_session) {
        // Remove the AI_SESSION cookie from both arrays, they will never have the same value
        Arrays.sort(cookiesFromWrappedRequest, new CookieNameComparator());
        int index = Arrays.binarySearch(cookiesFromWrappedRequest, ai_session, new CookieNameComparator());
        return index;
    }
}
