package com.example.userservice.Entities;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Map;

@Builder
@Getter
@Setter
public class GenericNotification implements Serializable {


    private static final long serialVersionUID = 7846513936766381673L;

    /**
     * attributes of mail content
     */
    private transient Map<String, Object> attributes;
    /**
     * label
     */
    private String label;

    /**
     * emailFrom
     */
    private String emailFrom;
    /**
     * emailTo
     */
    private String emailTo;


    private byte[] attachmentData;
}
