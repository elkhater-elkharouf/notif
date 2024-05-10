package com.example.userservice.Entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Mail implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String host;
    private int port;
    private String username;
    private String password;



    @OneToOne(mappedBy = "mail", cascade = CascadeType.ALL)
    @JsonIgnore
    private Projet projet;

}
