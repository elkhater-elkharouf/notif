package com.example.userservice.Services.User;

import com.example.userservice.Entities.Image;
import com.example.userservice.Entities.Privilege;
import com.example.userservice.Entities.Role;
import com.example.userservice.Entities.User;
import org.springframework.web.multipart.MultipartFile;

import javax.ws.rs.core.Response;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;

public interface IUserService {
    public User getCurrentLoggedInUser() ;
    public User addUser(User u) throws SQLException;
        public User getUser(String email);
     //   public String updateUser(int userId, Role role, List<Privilege> privileges, Image image);
        public User updateUser(User u);
        public void deleteUser(int id);
        public User getUserById(int id);
        public List<User> getAllUsers();
        public boolean desActiverCompteUser(int idUser);
        public User affecterUserRole(int idUser , int idRole);
        public User laodUserByUserName(String email);

        public String getCurrentUsername()  ;
        User addUserWithRoleAndAffectPrivileges(User user);

public List<String> getAllDepartmentUser();
        Set<String> findAllByEnabled();
        Response requestPasswordReset(String email) throws Exception;
        Response resetPassword(String token, String NewPassword , String ConfirmPassword);

        Set<Privilege> getListPrivilegesUser(int idUser);


        User annulerPrivilegeUser(int idUser , int idPrivilege);

        User addPrivilegeToUser(int idUser , int idPrivilegr);
}
