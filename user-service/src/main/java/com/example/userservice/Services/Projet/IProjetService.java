package com.example.userservice.Services.Projet;

import com.example.userservice.Entities.Mail;
import com.example.userservice.Entities.Projet;
import com.example.userservice.Entities.User;

import java.util.List;
import java.util.Set;

public interface IProjetService {
    public Projet addProjet(Projet Projet);
    public Projet updateProjet(Projet Projet);
    public void deleteProjet(int id);
    public Projet getProjetById(int id);
    public List<Projet> getAllProjets();
    public Projet addMailAndAsseignToProject(Mail mail , int idP);
    public Projet addProjetWithUsers(Projet projet, Set<Integer> userIds) ;
    public Projet addProjetWithMailAndUsers(Projet projet,List<Integer> userIds);
    public Set<User> getUsersByProjetId(int projetId);
}
