package com.example.userservice.Services.User;
import org.slf4j.Logger;
import com.example.userservice.Entities.*;
import com.example.userservice.Repository.*;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import com.example.userservice.Services.Role.RoleServiceImp;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.transaction.Transactional;
import javax.ws.rs.core.Response;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;
import org.slf4j.LoggerFactory;
@Service
@AllArgsConstructor
public class UserServiceImp  implements IUserService {
    private static final Logger logger = LoggerFactory.getLogger(UserServiceImp.class);
    UserRepository userRepository;
    RoleRepository roleRepository;
    PrivilegeRepository privilegeRepository;
    VerificationTokenRepository verificationTokenRepository;
    private PasswordEncoder passwordEncoder;
    private VerificationTokenService verificationTokenService;
    private EmailUserService emailUserService;
    RoleServiceImp roleService ;
    ImageServiceImpl imageService ;
    MailRepository mailRepository ;

    @Override
    public User getCurrentLoggedInUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof User) {
            return (User) authentication.getPrincipal();

        }
        return null; // Aucun utilisateur connecté ou utilisateur non trouvé dans le contexte de sécurité
    }
    @Override
    public User addUser(User u) throws SQLException {
        String pwd = u.getPassword();
        u.setPassword(passwordEncoder.encode(pwd));
        u.setEnabled(false);

        Role r = u.getRole();
        Set<Privilege> affectedPrivileges = new HashSet<>();

        for (Privilege p : r.getPrivileges()) {
            Privilege privilege = privilegeRepository.findById(p.getIdPrivilege()).orElse(null);
            if (privilege != null) {
                if (privilege.getRoles() == null) {
                    privilege.setRoles(new HashSet<>());
                }
                privilege.getRoles().add(r);
                affectedPrivileges.add(privilege);
            }
        }

        r.setPrivileges(affectedPrivileges);
        roleRepository.save(r);
        try {
            String token = UUID.randomUUID().toString();
            verificationTokenService.affectUserToken(u , token);
            //send Email
            emailUserService.sendHtmlMail(u);

        }catch (Exception e){

        }


        return userRepository.save(u);
    }

    @Override
    public User getUser(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public User updateUser(User user) {
        User existingUser = userRepository.findById(user.getIdUser()).orElse(null);
        if (existingUser == null) {
            // Gérer le cas où l'utilisateur n'existe pas
            return null;
        }

        // Mettre à jour les champs de l'utilisateur
        existingUser.setEmail(user.getEmail());
        existingUser.setDepartment(user.getDepartment());
        existingUser.setEnabled(user.isEnabled());
        existingUser.setFname(user.getFname());
        existingUser.setLname(user.getLname());
        String pwd = existingUser.getPassword();
        existingUser.setPassword(passwordEncoder.encode(pwd));
        existingUser.setPassword(user.getPassword());

        // Mettre à jour le rôle de l'utilisateur
        Role role = roleService.updateRole(user.getRole());
        if(role!=null){
            role.setPrivileges(role.getPrivileges());
        }
        existingUser.setRole(role);


        // Enregistrer les modifications dans la base de données
        return userRepository.save(existingUser);
    }



    @Override
    public void deleteUser(int id) {
        User user = userRepository.findById(id).orElse(null);
        System.out.println("ahouaaaaaaaaaaaaaaaaaaa"+user.getEmail());

        userRepository.deleteById(id);
    }

    @Override
    public User getUserById(int id) {
        return userRepository.findById(id).orElse(null);
    }

    @Override
    public List<User> getAllUsers() {

        //  User user = (User) authentication.getPrincipal();
        //  System.out.println("$$$$$user connected $$$$$$$ "+user.getEmail()+" id "+user.getIdUser());

        return userRepository.findAll();
    }

    @Override
    public boolean desActiverCompteUser(int idUser) {
        User user = userRepository.findById(idUser).orElse(null);
        if(user.isEnabled()){
            user.setEnabled(false);
            userRepository.save(user);
            return false;
        }else {
            user.setEnabled(true);
            userRepository.save(user);
            return true;
        }

    }

    @Override
    public User affecterUserRole(int idUser, int idRole) {
        User user = userRepository.findById(idUser).orElse(null);
        Role role = roleRepository.findById(idRole).orElse(null);
        user.setRole(role);
        userRepository.save(user);
        return user;
    }

    @Override
    public User laodUserByUserName(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public String getCurrentUsername() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    @Override
    @Transactional
    public User addUserWithRoleAndAffectPrivileges(User u) {
        String pwd = u.getPassword();
        u.setPassword(passwordEncoder.encode(pwd));
        //u.setEnabled(false);

        Role r = u.getRole();
        Set<Privilege> affectedPrivileges = new HashSet<>();

        for (Privilege p : r.getPrivileges()) {
            Privilege privilege = privilegeRepository.findById(p.getIdPrivilege()).orElse(null);
            if (privilege != null) {
                if (privilege.getRoles() == null) {
                    privilege.setRoles(new HashSet<>());
                }
                privilege.getRoles().add(r);
                affectedPrivileges.add(privilege);
            }
        }

        r.setPrivileges(affectedPrivileges);
        roleRepository.save(r);
        try {
            String token = UUID.randomUUID().toString();
            verificationTokenService.affectUserToken(u , token);
            //send Email
            emailUserService.sendHtmlMail(u);

        }catch (Exception e){
            throw new RuntimeException("email invalid");
        }
        return userRepository.save(u);
    }

    @Override
    public List<String> getAllDepartmentUser() {
        List<User> users = userRepository.findAllByDepartmentIsNotNull();
        return users.stream()
                .map(User::getDepartment)
                .distinct()
                .collect(Collectors.toList());

    }

    @Override
    public Set<String> findAllByEnabled() {
        List<User> users = userRepository.findAll();
        return users.stream()
                .map(user -> user.isEnabled() ? "Active" : "Inactive")
                .collect(Collectors.toCollection(HashSet::new));
    }
    @Override
    public Response requestPasswordReset(String email) throws Exception {
        User user = userRepository.findByEmail(email);

        if (user == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("Email not found!").build();
        }

        try {
            String token = UUID.randomUUID().toString();
            verificationTokenService.affectUserToken(user, token);
            emailUserService.resetPasswordMail(user);
        } catch (MessagingException e) {
            // Catching specific MessagingException and logging the error
            // Log the exception here for better debugging
            e.printStackTrace();  // Consider using a logger in real applications
            throw new RuntimeException("Failed to send email", e);
        } catch (Exception e) {
            // Catch any other exceptions and log them
            e.printStackTrace();  // Consider using a logger in real applications
            throw new RuntimeException("An unexpected error occurred", e);
        }

        return Response.status(Response.Status.OK).entity("Password reset email has been sent").build();
    }




    @Override
    public Response resetPassword(String token, String NewPassword , String ConfirmPassword) {
        VerificationToken verificationToken = verificationTokenService.findByToken(token);
        if (!(NewPassword.equals(ConfirmPassword))){
            return  Response.status(Response.Status.BAD_REQUEST).entity("Les deux mots de passe ne correspondent pas. Veuillez réessayer. ").build();
        }
        Timestamp cuurentTimesTamp = new Timestamp(System.currentTimeMillis());
        // check if the token is expired
        if(verificationToken.getExpiryDate().before(cuurentTimesTamp)) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Lien expiré . Veuillez renvoyer votre demande  ").build();

        }
        if (verificationToken==null){
            return  Response.status(Response.Status.BAD_REQUEST).entity("Lien erroné . Veuillez verifier le lien de modification   ").build();
        }
        User user = userRepository.findById(verificationToken.getUser().getIdUser()).orElse(null);
        //prepare new password
        String encodedPassword = passwordEncoder.encode(NewPassword);
        user.setPassword(encodedPassword);
        userRepository.save(user);
        return  Response.status(Response.Status.OK).entity("Mot de passe a été modifier  ").build();
    }


    @Override
    public Set<Privilege> getListPrivilegesUser(int idUser) {
        User user = userRepository.findById(idUser).orElse(null);
        //  List<Privilege> ls = (List<Privilege>) user.getRole().getPrivileges();
        return user.getRole().getPrivileges();
    }

    @Override
    @Transactional
    public User annulerPrivilegeUser(int idUser, int idPrivilege) {
        User user = userRepository.findById(idUser).orElse(null);
        Privilege privilege = privilegeRepository.findById(idPrivilege).orElse(null);
        Role role = user.getRole();
        role.getPrivileges().remove(privilege);
        privilege.getRoles().remove(role);
        privilegeRepository.save(privilege);
        roleRepository.save(role);
        return userRepository.save(user);
    }

    @Override
    public User addPrivilegeToUser(int idUser, int idPrivilegr) {

        User user = userRepository.findById(idUser).orElse(null);

        Privilege privilege = privilegeRepository.findById(idPrivilegr).orElse(null);
        user.getRole().getPrivileges().add(privilege);
        privilege.getRoles().add(user.getRole());
        privilegeRepository.save(privilege);
        return userRepository.save(user);
    }



    public int getEmailConfigurationIdForLoggedInUser(String email) {
        User user = userRepository.findByEmail(email);
        if (user != null) {
            List<Mail> mails = mailRepository.findByUsername(user.getEmail());
            if (!mails.isEmpty()) {
                int mailConfigId = mails.get(0).getId();
                logger.debug("Mail configuration ID found: {}", mailConfigId);
                return mailConfigId;
            } else {
                logger.debug("No mail configurations found for user: {}", email);
            }
        } else {
            logger.debug("No user found with email: {}", email);
        }
        return -1;  // Return -1 if no mail configuration found
    }

    // Method to get the JavaMailSender for the current user
    public JavaMailSender getJavaMailSenderForCurrentUser() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        String userEmail = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        logger.debug("Current user email: {}", userEmail);

        int mailConfigId = getEmailConfigurationIdForLoggedInUser(userEmail);
        logger.debug("Retrieved mail configuration ID: {}", mailConfigId);

        if (mailConfigId != -1) {
            Mail mailConfigEntity = mailRepository.findById(mailConfigId).orElse(null);

            if (mailConfigEntity != null) {
                mailSender.setHost(mailConfigEntity.getHost());
                mailSender.setPort(mailConfigEntity.getPort());
                mailSender.setUsername(mailConfigEntity.getUsername());
                mailSender.setPassword(mailConfigEntity.getPassword());

                Properties props = mailSender.getJavaMailProperties();
                props.put("mail.transport.protocol", "smtp");
                props.put("mail.smtp.auth", "true");
                props.put("mail.smtp.starttls.enable", "true");
                logger.debug("Mail sender configured with host: {}, port: {}", mailConfigEntity.getHost(), mailConfigEntity.getPort());
            } else {
                logger.error("No mail configuration entity found with ID: {}", mailConfigId);
                configureDefaultMailSender(mailSender);
            }
        } else {
            logger.debug("No mail configuration ID found for the user: {}", userEmail);
            configureDefaultMailSender(mailSender);
        }

        return mailSender;
    }

    // Method to configure default mail sender
    private void configureDefaultMailSender(JavaMailSenderImpl mailSender) {
        // You can set default mail sender properties here
        mailSender.setHost("defaultHost");
        mailSender.setPort(25);
        mailSender.setUsername("defaultUsername");
        mailSender.setPassword("defaultPassword");

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");

        logger.debug("Default mail sender configured");
    }
}
