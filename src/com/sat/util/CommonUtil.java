package com.sat.util;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Collection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author Sathishkumar T
 */
public class CommonUtil implements Serializable {
    
    private final static Logger LOGGER = LogManager.getLogger("CommonUtil");

    /**
     * Check whether the instance is null or not
     *
     * @param o
     * @return true if the object is null
     */
    public static boolean isNull(Object o) {
        return !isNotNull(o);
    }

    /**
     * Check whether the instance is not null
     *
     * @param o
     * @return - true if the object is null else return false;
     */
    public static boolean isNotNull(Object o) {
        return !(o == null || o.toString().trim().isEmpty());
    }

    /**
     * Object is empty or not.
     *
     * @param item Object
     * @return - true if the object is not null and not empty else return false;
     */
    public static boolean isNotEmpty(Object item) {
        return !isEmpty(item);
    }

    /**
     * Object is the NULL or empty .
     *
     * @param item
     * @return true if the if the Object is NULL or empty else return false.
     */
    public static boolean isEmpty(Object item) {
        return item == null || item.toString().trim().isEmpty();
    }

    /**
     * Collection is the NULL or empty .
     *
     * @param coll
     * @return true if the if the Object is NULL or empty else return false.
     */
    public static boolean isEmpty(Collection coll) {
        return coll == null || coll.isEmpty();
    }

    /**
     * Is Collection is empty or not.
     *
     * @param list
     * @return - true if the Collection is null or empty else return false.
     */
    public static boolean isNotEmpty(Collection list) {
        return !(list == null || list.isEmpty());
    }

    /**
     * Get PrintStackTrace as String.
     *
     * @param throwable
     * @return
     */
    public static String getStackTrace(Throwable throwable) {
        Writer writer = null;
        PrintWriter printWriter = null;
        try {
            writer = new StringWriter();
            printWriter = new PrintWriter(writer);
            throwable.printStackTrace(printWriter);
            return writer.toString();
        } finally {
            try {
                if (writer != null) {
                    writer.close();
                }
                if (printWriter != null) {
                    printWriter.close();
                }
            } catch (IOException e) {
                LOGGER.error(e);
            }
        }
    }
    
    public static Long getLong(Object o) {
        if (o == null) {
            return 0L;
        }
        return Long.parseLong(o.toString());
    }
    
    public static Integer getInt(Object o) {
        if (o == null) {
            return 0;
        }
        return Integer.parseInt(o.toString());
    }
    
    public static Double getDouble(Object o) {
        if (o == null) {
            return 0d;
        }
        return Double.parseDouble(o.toString());
    }

    /**
     * Replace the special characters [^a-zA-Z 0-9]
     *
     * @param txt
     * @return - replaced text
     */
    public static String removeSpecialChars(String txt) {
        return txt.replaceAll("[^a-zA-Z 0-9]+", "");
    }
    
}
