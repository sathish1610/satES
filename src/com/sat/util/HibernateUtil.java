/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sat.util;

import java.io.IOException;
import java.util.List;
import java.util.Properties;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.cfg.Configuration;

/**
 *
 * @author Sathishkumar T
 */
public class HibernateUtil {

    private final static Logger LOGGER = LogManager.getLogger("HibernateUtil");
    private static Configuration configuration = new AnnotationConfiguration();
    private static org.hibernate.SessionFactory sessionFactory;
    private static Session session = null;

    public Session getSession() {
        if (sessionFactory == null) {
            try {
                Properties properties = new Properties();
                properties.load(HibernateUtil.class.getResourceAsStream("/com/sat/properties/hibernate.properties"));
                sessionFactory = configuration.configure("/com/sat/hibernate/hibernate.cfg.xml").addProperties(properties).buildSessionFactory();
            } catch (IOException e) {
                LOGGER.error(e);
            }
        }
        if (session == null || !session.isOpen()) {
            session = sessionFactory.openSession();
        }
        return session;
    }

    public List executeQuery(String queryString) {
        Query query = getSession().createQuery(queryString);
        return query.list();
    }
}
