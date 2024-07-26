package com.example.userservice.Services.User;

import com.example.userservice.Entities.Privilege;
import com.example.userservice.Entities.User;
import org.springframework.data.domain.Page;

import javax.ws.rs.core.Response;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;

public interface IUserService {
    public User getCurrentLoggedInUser() ;
    public User addUser(User u) throws SQLException;
    public User addUserWithRole(User user, int roleId);
    public Page<User> getAllUserspaginated(int page, int size);
        public User getUser(String email);
     //   public String updateUser(int userId, Role role, List<Privilege> privileges, Image image);
        public User updateUser(User u);
        public void deleteUser(int id);
        public User getUserById(int id);
        public List<User> getAllUsers();
        public boolean desActiverCompteUser(int idUser);
        public User affecterUserRole(int idUser , int idRole);
        public User laodUserByUserName(String email);
    public Set<User> getAlUsers() ;

        public String getCurrentUsername()  ;
        User addUserWithRoleAndAffectPrivileges(User user);

public List<String> getAllDepartmentUser();
        Set<String> findAllByEnabled();
        Response requestPasswordReset(String email) throws Exception;
        Response resetPassword(String token, String NewPassword , String ConfirmPassword);

        Set<Privilege> getListPrivilegesUser(int idUser);
    public List<User> getUsersByStatus(boolean enabled);

    User annulerPrivilegeUser(int idUser , int idPrivilege);
    public boolean changePassword(int userId, String oldPassword, String newPassword, String retypeNewPassword);
        User addPrivilegeToUser(int idUser , int idPrivilegr);
        public Set<User> getUsersByRoleName(String roleName);
    public List<User> getUsersByDepartment(String department);
}
