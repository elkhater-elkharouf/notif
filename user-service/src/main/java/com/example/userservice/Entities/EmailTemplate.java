package com.example.userservice.Entities;


import lombok.Getter;
import lombok.Setter;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;


/**
 * Email notification template
 */
@Entity
@Table(name = "EMAIL_TEMPLATE")
@Getter
@Setter
public class EmailTemplate {


    /**
     * id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", updatable = false, nullable = false)
    private Long id;

    /**
     * label
     */
    @Column(name = "LABEL", updatable = false, nullable = false)
    private String label;

    /**
     * objectFR
     */
    @Column(name = "OBJECT", updatable = false, nullable = false)
    private String object;


    /**
     * contentHtmlFr
     */
    @Lob
    @Column(name = "CONTENT_HTML", updatable = false, nullable = false, columnDefinition = "LONGTEXT")
    private String contentHtml;


    /**
     * contentTextFr
     */
    @Column(name = "CONTENT_TEXT", updatable = false)
    private String contentText;



}
