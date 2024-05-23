package com.example.userservice.Controller;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.userservice.Entities.*;
import com.example.userservice.Exception.Mensaje;
import com.example.userservice.Model.EmailRequest;
import com.example.userservice.Model.PasswordResetModel;
import com.example.userservice.Security.JWTUtil;
import com.example.userservice.Services.Mail.IEmailService;
import com.example.userservice.Services.Privilege.IPrivilegeService;
import com.example.userservice.Services.Projet.IProjetService;
import com.example.userservice.Services.Role.IRoleService;
import com.example.userservice.Services.User.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.security.Principal;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
@RestController
@AllArgsConstructor
@CrossOrigin(origins = "*")
public class UserController {
    IEmailService iEmailService ;
    IProjetService iProjetService ;
    IUserService iUserService;
    IPrivilegeService iPrivilegeService;
    IRoleService iRoleService;
    VerificationTokenService verificationTokenService;
    EmailUserService emailUserService ;
    CloudinaryService cloudinaryService;
    IImageService imagenService;
    UserServiceImp userService ;
    @GetMapping("/helloUser")
    public String Hello(){

        return "bills hello from microservices user !";

    }

    @GetMapping("/email-configuration")
    public int getEmailConfigurationId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = (String) authentication.getPrincipal();
        return userService.getEmailConfigurationIdForLoggedInUser(email);
    }
    @GetMapping("/CurrentLoggedUser")
    public User getCurrentLoggedInUser(){
        return iUserService.getCurrentLoggedInUser();
    }
/*
    @PostMapping("/send")
    public ResponseEntity<String> sendEmail(@RequestPart EmailRequest emailRequest ) {
        try {
            emailUserService.sendEmail(emailRequest.getRecipient(), emailRequest.getSubject(), emailRequest.getSenderName(), emailRequest.getMailContent() , emailRequest.getAttachment());
            return ResponseEntity.ok("Email sent successfully!");
        } catch (MessagingException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to send email: " + e.getMessage());
        }
    }
*/
@PostMapping("/send-email-with-attachment")
public String sendEmailWithAttachment(@RequestBody EmailRequest emailRequest) throws MessagingException {
    emailUserService.sendMailWithAttchment(emailRequest.getToEmail(), emailRequest.getBody(), emailRequest.getSubject(), emailRequest.getAttachment());
    return "Email with attachment sent successfully!";
}

    @GetMapping("/requestPasswordReset/{email}")
    public Response requestPasswordReset(@PathVariable("email") String email) throws Exception {
        return iUserService.requestPasswordReset(email);
    }
    @PostMapping("/password-reset")
    public Response resetPassword(@RequestBody PasswordResetModel passwordResetModel, @QueryParam("token") String token) {
        System.out.println(passwordResetModel);
        return iUserService.resetPassword(token , passwordResetModel.getNewPassword() , passwordResetModel.getConfirmPassword());
    }

    @GetMapping("/AllUsers")
    @ResponseBody

    public List<User> getAllUsers(){
        return iUserService.getAllUsers();
    }
    @GetMapping("/AllDepartment")
    public List<String> getAllDepartmentUser(){
    return iUserService.getAllDepartmentUser();
    }

    @GetMapping("/enabled")
    public Set<String> getAllUsersByEnabled() {
        return iUserService.findAllByEnabled(); // or false for inactive users
    }

    @PostMapping("/addUser")
    @ResponseBody
    public Response addUser (@RequestBody User user ) throws SQLException {

        if (user.getEmail().equals("")){
            return Response.status(Response.Status.BAD_REQUEST).entity("Email Obligatoire").build();
        }
        if (user.getPassword().equals("")){
            return Response.status(Response.Status.BAD_REQUEST).entity("Password Obligatoire").build();
        }
        if (user.getLname().equals("") || user.getFname().equals("")){
            return Response.status(Response.Status.BAD_REQUEST).entity("Nom et prenom Obligatoires").build();
        }
        if (iUserService.laodUserByUserName(user.getEmail()) != null){
            return Response.status(Response.Status.CONFLICT).entity("email déja exist  ").build();
        }else {
            iUserService.addUser(user);
            return Response.status(Response.Status.CREATED).entity(user).build();
        }

    }
    @GetMapping("/getUserById/{id}")
    @ResponseBody
    public  Response  getUserById(@PathVariable("id") int id){

        User user = iUserService.getUserById(id);
        if (user == null){
            return Response.status(Response.Status.NOT_FOUND).entity("aucun user existe avec l'id : "+id).build();
        }else {
            return Response.status(Response.Status.OK).entity(user).build();

        }

    }



    @DeleteMapping("/deleteUser/{id}")
    private void deleteUser(@PathVariable("id") int id)
    {
        iUserService.deleteUser(id);




    }

    @PutMapping(value="/updateUser/{userId}",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<User> updateUser( @PathVariable("userId") int userId,
                                            @RequestPart("user") User user,
                                            @RequestPart(value = "image", required = false) MultipartFile imageFile) throws IOException {
        // Récupérer l'utilisateur existant en utilisant l'ID fourni

        User existingUser = iUserService.getUserById(userId);
        if (existingUser == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        // Mettre à jour les champs de l'utilisateur avec ceux du nouvel utilisateur envoyé dans le corps de la requête
        existingUser.setEmail(user.getEmail());
        existingUser.setDepartment(user.getDepartment());
        existingUser.setEnabled(user.isEnabled());
        existingUser.setRole(user.getRole());
        existingUser.setPassword(user.getPassword());
        existingUser.setFname(user.getFname());
        existingUser.setLname(user.getLname());

        // Vérifier si une nouvelle image a été fournie et la mettre à jour si nécessaire
        if (imageFile != null && !imageFile.isEmpty()) {

            imagenService.save(imageFile,userId);
        }

        // Appeler la méthode de service pour mettre à jour l'utilisateur
        User updatedUser = iUserService.updateUser(existingUser);
        return new ResponseEntity<>(updatedUser, HttpStatus.OK);
    }

    @PutMapping("/desActiverUser/{id}")
    private Response desActiverUser(@PathVariable("id") int id)
    {
        User user = iUserService.getUserById(id);
        if (user == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("user introuvable").build();
        }else if (iUserService.desActiverCompteUser(id) == true){
            return Response.status(Response.Status.OK).entity("user activer").build();
        }else {
            return Response.status(Response.Status.OK).entity("user desactiver").build();
        }

    }



    @PostMapping("/affecterUserRole/{idUser}/{idRole}")
    public User affecterUserRole(@PathVariable("idUser") int idUser,@PathVariable("idRole") int idRole) {
        return iUserService.affecterUserRole(idUser,idRole);
    }

    @PostMapping("/addUserWithRoleAndAffectPrivileges")
    public Response addUserWithRoleAndAffectPrivileges(@RequestBody User user) {
        if (iUserService.laodUserByUserName(user.getEmail()) != null){
            return Response.status(Response.Status.CONFLICT).entity("email déja exist  ").build();
        }else{
            iUserService.addUserWithRoleAndAffectPrivileges(user);
            return Response.status(Response.Status.OK).entity(user).build();

        }

    }



    @GetMapping("/refreshToken")
    public void refreshToken(HttpServletRequest request , HttpServletResponse response) throws Exception{
        String authToken = request.getHeader(JWTUtil.AUTH_HEADER);
        if(authToken != null && authToken.startsWith(JWTUtil.PREFIX_HEADER)){
            try{
                //ignorer Bearer
                String jwt  = authToken.substring(JWTUtil.PREFIX_HEADER.length());
                //meme secret pour generate signature ici pour verifier token
                Algorithm algorithm = Algorithm.HMAC256(JWTUtil.SECRET);
                JWTVerifier jwtVerifier = JWT.require(algorithm).build();
                DecodedJWT decodedJWT = jwtVerifier.verify(jwt);
                String email = decodedJWT.getSubject();
                User user = iUserService.laodUserByUserName(email);
                //génerer nouveau access token
                String jwtAccessToken = JWT.create()
                        .withSubject(user.getEmail())
                        //sexpire dans 5min en millissecondes
                        .withExpiresAt(new Date(System.currentTimeMillis()+JWTUtil.EXP_ACCESS_TOKEN))
                        //nom de l'app qui a gérer le token (on va mettre url de la requete)
                        .withIssuer(request.getRequestURL().toString())
                        //convertit la liste des authoritys de lobjet user spring en liste de priviléges
                        .withClaim("privileges",user.getRole().getPrivileges().stream().map(Privilege::getPrivilegeName).collect(Collectors.toList()))
                        .withClaim("role",user.getRole().getRoleName())
                        .sign(algorithm);

                Map<String , String> idToken = new HashMap<>();
                idToken.put("accessToken",jwtAccessToken);
                idToken.put("refreshToken",jwt);
                response.setHeader(JWTUtil.AUTH_HEADER,jwtAccessToken);

                response.setContentType("application/json");
                //pour serialiser un objet en json avec entrer et sortie
                new ObjectMapper().writeValue(response.getOutputStream(),idToken);

            }catch (Exception e){
                throw e;
            }
        }else {
            throw  new RuntimeException("Refresh Token Required");
        }


    }

    @GetMapping("/currentUser")
    public User profile(Principal principal){
        //Principal utilsateur username
        return iUserService.laodUserByUserName(principal.getName());

    }
    @GetMapping("/username")
    public ResponseEntity<String> getCurrentUsername() {
        String username = iUserService.getCurrentUsername();
        return ResponseEntity.ok(username);
    }


    @GetMapping("/activation")
    public  String activation(@RequestParam("token") String token ){

        VerificationToken verificationToken = verificationTokenService.findByToken(token);
        if (verificationToken == null){
            return "Token cerification invalid";
        }else {
            User user = verificationToken.getUser();
            // if user in not activated
            if(!user.isEnabled()){
                //current Timestamp
                Timestamp cuurentTimesTamp = new Timestamp(System.currentTimeMillis());
                // check if the token is expired
                if(verificationToken.getExpiryDate().before(cuurentTimesTamp)){
                    return "Your verification token has expired !";
                }else {
                    user.setEnabled(true);

                    iUserService.updateUser(user);
                    return "Your account is successfuly activated";
                }
            }else {
                return "Your account is already activated";
            }
        }
    }




    @GetMapping("/getListPrivilegesUser/{idUser}")
    public Set<Privilege> getListPrivilegesUser(@PathVariable("idUser")int idUser) {
        return  iUserService.getListPrivilegesUser(idUser);
    }
    @PostMapping("/annulerPrivilegeUser/{idUser}/{idPrivilege}")
    public User annulerPrivilegeUser(@PathVariable("idUser")int idUser,@PathVariable("idPrivilege") int idPrivilege) {
        return iUserService.annulerPrivilegeUser(idUser,idPrivilege);

    }

    @PostMapping("/addPrivilegeToUser/{idUser}/{idPrivilegr}")
    public User addPrivilegeToUser(@PathVariable("idUser")int idUser,@PathVariable("idPrivilegr")int idPrivilegr) {
        return iUserService.addPrivilegeToUser( idUser,idPrivilegr);
    }




    /**************************************************************************************************/
    @PostMapping("/AffectRoleToPrivilege/{idRole}/{idPrivilege}")
    public Boolean AffectRoleToPrivilege(@PathVariable("idRole") int idRole ,@PathVariable("idPrivilege") int idPrivilege){
        return iRoleService.AffectRoleToPrivilege(idRole,idPrivilege);
    }
    @PostMapping("/addRoleAddRoleWithPrivilege")
    public Role AddRoleWithPrivilege(@RequestBody Role role) {
        return iRoleService.AddRoleWithPrivilege(role);
    }
    @GetMapping("/AllRoles")
    @ResponseBody
    public List<Role> getAllRoles(){
        return iRoleService.getAllRoles();
    }

    @PostMapping("/addRole")
    @ResponseBody
    public Role addRole (@RequestBody Role role){
        return iRoleService.addRole(role);
    }

    @GetMapping("/getRoleById/{id}")
    @ResponseBody
    public Role  getRoleById(@PathVariable("id") int id){
        return   iRoleService.getRoleById(id);
    }


    @DeleteMapping("/deleteRole/{id}")
    private void deleteRole(@PathVariable("id") int id)
    {
        iRoleService.deleteRole(id);
    }

    @PutMapping("/updateRole")
    private Role updateRole(@RequestBody Role role)
    {
        iRoleService.updateRole(role);
        return role;
    }





    /**************************************************************************************************/
    @GetMapping("/AllPrivileges")
    @ResponseBody
    public List<Privilege> getAllAllPrivileges(){
        return iPrivilegeService.getAllPrivileges();
    }

    @PostMapping("/addPrivilege")
    @ResponseBody
    public Privilege addPrivilege (@RequestBody Privilege privilege){
        return iPrivilegeService.addPrivilege(privilege);
    }

    @GetMapping("/getPrivilegeById/{id}")
    @ResponseBody
    public Privilege getaddPrivilegeById(@PathVariable("id") int id){
        return   iPrivilegeService.getPrivilegeById(id);
    }


    @DeleteMapping("/deletePrivilege/{id}")
    private void deletegetaddPrivilege(@PathVariable("id") int id)
    {
        iPrivilegeService.deletePrivilege(id);
    }

    @PutMapping("/updatePrivilege")
    private Privilege updatePrivilege(@RequestBody Privilege privilege)
    {
        iPrivilegeService.updatePrivilege(privilege);
        return privilege;
    }

    @PostMapping("/addAndAssignPrivilegeToRole/{idRole}")
    public Privilege addAndAssignPrivilegeToRole(@RequestBody Privilege privilege, @PathVariable("idRole") int idRole) {
        return iPrivilegeService.addAndAssignPrivilegeToRole(privilege,idRole);
    }




    @GetMapping("/laodUserByUserName/{email}")
    public User laodUserByUserName(@PathVariable("email")String email){
        return iUserService.laodUserByUserName(email);
    }



    /**************************************************************************************************/
    @GetMapping("/AllProjet")
    @ResponseBody
    public List<Projet> getAllAllProjet(){
        return iProjetService.getAllProjets();
    }
    @GetMapping("/getUsersByProjetId/{projetId}")
    public Set<User> getUsersByProjetId(@PathVariable int projetId) {
        return iProjetService.getUsersByProjetId(projetId);
    }
    @PostMapping("/addProjet")
    @ResponseBody
    public Projet addProjet (@RequestBody Projet projet){
        return iProjetService.addProjet(projet);
    }

    @GetMapping("/getProjetById/{id}")
    @ResponseBody
    public Projet getProjetById(@PathVariable("id") int id){
        return   iProjetService.getProjetById(id);
    }


    @DeleteMapping("/deleteProjet/{id}")
    private void deleteProjet(@PathVariable("id") int id)
    {
        iProjetService.deleteProjet(id);
    }

    @PutMapping("/updateProjet")
    private Projet updateProjet(@RequestBody Projet projet)
    {
        iProjetService.updateProjet(projet);
        return projet;
    }
@PostMapping("/addMailAndAsseignToProject/{idP}")
    public Projet addMailAndAsseignToProject(@RequestBody Mail mail,@PathVariable("idP") int idP){
        return iProjetService.addMailAndAsseignToProject(mail,idP);

    }

    @PostMapping("/addProjetWithUsers")
    public ResponseEntity<Projet> addProjetWithUsers(@RequestBody Projet projet, @RequestParam Set<Integer> userIds) {
        Projet addedProjet = iProjetService.addProjetWithUsers(projet, userIds);
        return ResponseEntity.ok().body(addedProjet);
    }


   /***************************************************************/

   @PostMapping("/addEmail")
   public ResponseEntity<Mail> createEmail(@RequestBody Mail mail) {
       Mail createdEmail = iEmailService.addEmail(mail);
       return ResponseEntity.status(HttpStatus.CREATED).body(createdEmail);
   }
    @GetMapping("/AllMail")
    public List<Mail> getAllMail(){
        return iEmailService.AllMail();
    }

/**********************************************************************/

@GetMapping("/listImage")
public ResponseEntity<List<Image>> list(){
    List<Image> list = imagenService.list();
    return new ResponseEntity(list, HttpStatus.OK);
}
    @GetMapping("/imageByUser")
    public Image getByUser(@RequestParam int idUser){
        return imagenService.getByUserId(idUser);
    }

    @PostMapping("/uploadImage")
    public void upload(@RequestPart("file") MultipartFile image,@RequestParam int idUser)throws IOException {
          imagenService.save(image,idUser);
    }

    @DeleteMapping("/deleteImage/{id}")
    public ResponseEntity<?> delete(@PathVariable("id") int id)throws IOException {
        Optional<Image> optionalImage = imagenService.getOne(id);
        if (!optionalImage.isPresent()) {
            return new ResponseEntity<>(new Mensaje("No existe"), HttpStatus.NOT_FOUND);
        }
        Image imagen = optionalImage.get();
        Map result = cloudinaryService.delete(imagen.getImagenId());
        imagenService.delete(id);
        return new ResponseEntity(new Mensaje("imagen eliminada"), HttpStatus.OK);
    }

    @PostMapping("/addProjetByStep")
    public ResponseEntity<Projet> addProjetWithMailAndUsers(@RequestBody Projet projet, @RequestParam List<Integer> userIds) {
        Projet savedProjet = iProjetService.addProjetWithMailAndUsers(projet, userIds);
        return ResponseEntity.ok(savedProjet);
    }
}
