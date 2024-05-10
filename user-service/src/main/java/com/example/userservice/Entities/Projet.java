package com.example.userservice.Entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.Set;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Projet implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idProjet ;
    private String nameProjet ;
    @Temporal(TemporalType.DATE)
    private Date dateDeb ;
    private int status ;
    @Temporal(TemporalType.DATE)
    private Date dateFin ;
    @ManyToMany(cascade = CascadeType.ALL,fetch = FetchType.EAGER)
    private Set<User> users;

    @OneToOne
    private Mail mail ;

}
