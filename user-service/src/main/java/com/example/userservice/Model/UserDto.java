package com.example.userservice.Model;

import com.example.userservice.Entities.Image;
import com.example.userservice.Entities.Privilege;
import com.example.userservice.Entities.Role;
import lombok.*;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserDto {
    private int idUser ;
    private String Fname ;
    private String Lname ;
    private String email ;
    private String Password ;
    private String department ;
    private boolean enabled;
    private Role role;
    private List<Privilege> privileges;
    private Image image;
}
