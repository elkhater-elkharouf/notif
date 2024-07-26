package com.example.userservice.Entities;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.sql.Blob;
import java.util.Set;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class User implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idUser ;
    private String Fname ;
    private String Lname ;
private String numTel ;
    private String email ;
    private String Password ;
    private String department ;
    private String fcmToken = "epC_KhE6nqOku-t7xsdKkw:APA91bFk84BwjOGu-Tow6ajHLoRhf62hYve-zoO9Sg5eLMPOGOowdbT0RWpaGX0pZ0MR5Z61LfbPz_D8Rt0VuMTzHCcWiBQO_yGlx6x3-iFbO2itdTU8m_S2LUoLN6BP1u2KYDltJ0Pp";
    private boolean enabled;
    @ManyToOne
    private Role role;

    @OneToOne(mappedBy = "user")
    @JsonIgnoreProperties("user")
    Image image;

    @ManyToMany(mappedBy = "users" ,cascade = CascadeType.ALL , fetch = FetchType.EAGER)
    @JsonIgnore
    private Set<Projet> projets;
}
