package com.sat.domain;

import java.util.Date;

/**
 *
 * @author Sathishkumar T
 */
public interface EmailTransaction extends Domain {

    public String getType();

    public void setType(String type);

    public String getStatus();

    public void setStatus(String status);

    public int getNoOfTries();

    public void setNoOfTries(int noOfTries);

    public String getFromName();

    public void setFromName(String fromName);

    public String getFromAddress();

    public void setFromAddress(String fromAddress);

    public String getToAddress();

    public void setToAddress(String toAddress);

    public String getCcAddress();

    public void setCcAddress(String ccAddress);

    public String getBccAddress();

    public void setBccAddress(String bccAddress);

    public String getSubject();

    public void setSubject(String subject);

    public String getHtmlMessage();

    public void setHtmlMessage(String htmlMessage);

    public Date getCreatedDatetime();

    public void setCreatedDatetime(Date createdDatetime);

    public Date getModifiedDatetime();

    public void setModifiedDatetime(Date modifiedDatetime);

    public String getModule();

    public void setModule(String module);

    public String getSection();

    public void setSection(String section);

    public String getFileNumber();

    public void setFileNumber(String fileNumber);

    public String getAttachments();

    public void setAttachments(String attachments);

    public String getException();

    public void setException(String exception);
}
