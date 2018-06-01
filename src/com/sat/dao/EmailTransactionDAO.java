/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sat.dao;

import com.sat.util.HibernateUtil;
import com.sat.domain.EmailTransaction;
import com.sat.domain.impl.EmailTransactionImpl;
import java.util.Calendar;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author Sathishkumar T
 */
public class EmailTransactionDAO extends HibernateUtil {

    private final static Logger LOGGER = LogManager.getLogger("EmailTransactionDAO");

    public static enum Type {
        PRINT, FAX, EMAIL;
    }

    public static enum Status {
        PENDING, COMPLETED, ERROR, BLOCKED;
    }

    public List<EmailTransaction> getPendingTransactions() {
        try {
            Criteria criteria = getSession().createCriteria(EmailTransactionImpl.class);
            criteria.add(Restrictions.eq("type", Type.EMAIL.name()));
            criteria.add(Restrictions.or(Restrictions.eq("status", Status.PENDING.name()),
                    Restrictions.eq("status", Status.ERROR.name())));
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DATE, -1);
            criteria.add(Restrictions.gt("modifiedDatetime", calendar.getTime()));
            criteria.add(Restrictions.lt("noOfTries", 25));
            return criteria.list();
        } catch (HibernateException e) {
            LOGGER.error(e);
        }
        return null;
    }

}
