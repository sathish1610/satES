/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sat.email;

import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;
import com.sat.dao.EmailTransactionDAO;
import com.sat.dao.EmailTransactionDAO.Status;
import com.sat.domain.EmailTransaction;
import static com.sat.util.CommonUtil.isEmpty;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.security.Security;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author Sathishkumar T
 */
public class EmailJobScheduler {

    private final static Logger LOGGER = LogManager.getLogger("EmailJobScheduler");
    private EmailTransactionDAO transactionDAO;
    private String fromName;
    Properties properties = new Properties();

    private Session getSession() throws IOException {
        Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());
        Properties props = new Properties();
        props.put("mail.smtp.host", properties.getProperty("mail.smtp.host"));
        props.put("mail.smtp.auth", true);
        props.put("mail.debug", false);
        props.put("mail.smtp.port", properties.getProperty("mail.smtp.port"));
        if (false) {
            props.put("mail.smtp.starttls.enable", true);
        } else {
            props.put("mail.smtp.socketFactory.port", properties.getProperty("mail.smtp.socketFactory.port"));
            props.put("mail.smtp.socketFactory.class", properties.getProperty("mail.smtp.socketFactory.class"));
            props.put("mail.smtp.socketFactory.fallback", false);
        }
        Session session = Session.getInstance(props,
                new javax.mail.Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(properties.getProperty("user.name"), properties.getProperty("user.password"));
            }
        });
        session.setDebug(false);
        return session;
    }

    public void process() throws IOException {
        transactionDAO = new EmailTransactionDAO();
        properties.load(EmailJobScheduler.class.getResourceAsStream("/com/sat/properties/email.properties"));
        List<EmailTransaction> transactions = transactionDAO.getPendingTransactions();
        if (transactions != null && !transactions.isEmpty()) {
            for (EmailTransaction transaction : transactions) {
                if (!isEmpty(properties.getProperty("user.name")) && !isEmpty(properties.getProperty("user.password"))) {
                    try {
                        Message msg = new MimeMessage(getSession());
                        //set subject
                        msg.setSubject(transaction.getSubject());
                        //set content
                        Multipart multipart = new MimeMultipart();
                        if (null != transaction.getHtmlMessage()) {
                            multipart.addBodyPart(getContent(transaction.getHtmlMessage()));
                        }
                        //set attachments
                        if (transaction.getAttachments() != null) {
                            for (String filePath : toStringArray(transaction.getAttachments())) {
                                if (!isEmpty(filePath)) {
                                    File file = new File(filePath);
                                    if (file.getName().contains(".pdf")) {
                                        PdfReader pdfReader = new PdfReader(file.getAbsolutePath());
                                        PdfTextExtractor.getTextFromPage(pdfReader, pdfReader.getNumberOfPages());
                                    }
                                    multipart.addBodyPart(getAttachement(file));
                                }
                            }
                        }
                        //set multipart (Content and Attachements)
                        msg.setContent(multipart);
                        //set date
                        msg.setSentDate(new Date());
                        //set priority
                        msg.setHeader("X-Priority", "1");
                        //set alias
                        fromName = !isEmpty(transaction.getFromName()) ? transaction.getFromName() : properties.getProperty("user.alise.name");
                        InternetAddress from = new InternetAddress(properties.getProperty("user.name"), fromName);
                        msg.setFrom(from);
                        //set recipients
                        Set<InternetAddress> recipients = new HashSet<>();
                        if (!isEmpty(transaction.getToAddress())) {
                            for (String recipient : toStringArray(transaction.getToAddress())) {
                                if (!isEmpty(recipient)) {
                                    recipients.add(new InternetAddress(recipient));
                                }
                            }
                            if (!recipients.isEmpty()) {
                                msg.setRecipients(Message.RecipientType.TO, recipients.toArray(new InternetAddress[]{}));
                            }
                        }
                        recipients.clear();
                        if (!isEmpty(transaction.getCcAddress())) {
                            for (String recipient : toStringArray(transaction.getCcAddress())) {
                                if (!isEmpty(recipient)) {
                                    recipients.add(new InternetAddress(recipient));
                                }
                            }
                            if (!recipients.isEmpty()) {
                                msg.setRecipients(Message.RecipientType.CC, recipients.toArray(new InternetAddress[]{}));
                            }
                        }
                        recipients.clear();
                        if (!isEmpty(transaction.getBccAddress())) {
                            for (String recipient : toStringArray(transaction.getBccAddress())) {
                                if (!isEmpty(recipient)) {
                                    recipients.add(new InternetAddress(recipient));
                                }
                            }
                            if (!recipients.isEmpty()) {
                                msg.setRecipients(Message.RecipientType.BCC, recipients.toArray(new InternetAddress[]{}));
                            }
                        }
                        recipients.clear();
                        Transport.send(msg);
                        transaction.setStatus(Status.COMPLETED.name());
                    } catch (MessagingException | IOException e) {
                        StringWriter errors = new StringWriter();
                        e.printStackTrace(new PrintWriter(errors));
                        transaction.setException(errors.toString());
                        transaction.setStatus(Status.ERROR.name());
                        LOGGER.error(e);
                    } finally {
                        transaction.setFromName(fromName);
                        transaction.setFromAddress(properties.getProperty("user.password"));
                        transaction.setModifiedDatetime(new Date());
                        transaction.setNoOfTries(transaction.getNoOfTries() + 1);
                        transactionDAO.getSession().getTransaction().begin();
                        transactionDAO.getSession().update(transaction);
                        transactionDAO.getSession().getTransaction().commit();
                    }
                }
            }
        }
    }

    private MimeBodyPart getContent(String html) {
        MimeBodyPart mbp = new MimeBodyPart();
        try {
            ByteArrayDataSource bds = new ByteArrayDataSource(html.getBytes(),
                    "text/html");
            mbp.setDataHandler(new DataHandler(bds));
        } catch (MessagingException e) {
            LOGGER.error(e);
        }
        return mbp;
    }

    private MimeBodyPart getAttachement(File file) {
        MimeBodyPart mbp = new MimeBodyPart();
        try {
            FileDataSource fds = new FileDataSource(file);
            mbp.setDataHandler(new DataHandler(fds));
            mbp.setFileName(fds.getName());
        } catch (MessagingException e) {
            LOGGER.error(e);
        }
        return mbp;
    }

    private String[] toStringArray(String recipient) {
        return recipient.split("(,)|(;)");
    }

}
