package org.opencrx.application.imap;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.mail.Header;
import javax.mail.MessagingException;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;

public class MimeMessageImpl extends MimeMessage {

    //-----------------------------------------------------------------------    
    public static String getHeadersAsRFC822(
        Part part,
        String[] fields
    ) throws MessagingException {
        if(fields == null) {
            List<String> headerNames = new ArrayList<String>();
            Enumeration<Header> allHeaders = part.getAllHeaders();
            while(allHeaders.hasMoreElements()) {
                headerNames.add(
                    allHeaders.nextElement().getName()
                );
            }
            return MimeMessageImpl.getHeadersAsRFC822(
                part,
                headerNames.toArray(new String[headerNames.size()])
            );
        }
        else {
            StringBuilder header = new StringBuilder();
            for(String field: fields) {
                String[] values = part.getHeader(field);
                if(values != null) {
                    for(String value: values) {
                        header.append(field).append(": ").append(value).append("\r\n");
                    }
                }
            }
            return header.toString(); 
        }
    }
        
    //-----------------------------------------------------------------------
    public MimeMessageImpl(
    ) {
        super((Session)null);
    }
    
    //-----------------------------------------------------------------------
    public MimeMessageImpl(
       InputStream is
    ) throws MessagingException {
        super(null, is);
    }
    
    //-----------------------------------------------------------------------
    public MimeMessageImpl(
        MimeMessage message
    ) throws MessagingException {
        super(message);
    }
    
    //-----------------------------------------------------------------------
    public long getUid(
    ) {
        return this.uid;
    }

    //-----------------------------------------------------------------------
    public void setUid(
        long uid
    ) {
        this.uid = uid;
    }

    //-----------------------------------------------------------------------
    public int getMessageNumber(
    ) {
        return this.messageNumber;
    }

    //-----------------------------------------------------------------------
    public void setMessageNumber(
        int messageNumber
    ) {
        this.messageNumber = messageNumber;
    }

    //-----------------------------------------------------------------------
    // Member
    //-----------------------------------------------------------------------    
    public static final String[] STANDARD_HEADER_FIELDS = new String[]{
        "Return-Path", "From", "To", "Cc", "Bcc", "Subject", "Date", "Message-ID", "MIME-Version", "Content-Type", "In-Reply-To"
    };
    
    protected long uid;
    protected int messageNumber;
    
}
