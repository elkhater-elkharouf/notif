package com.example.userservice.Services.User;

import com.example.userservice.Entities.User;
import com.example.userservice.Entities.VerificationToken;


public interface VerificationTokenService {
    public VerificationToken findByToken(String token);
    public VerificationToken findByUser(User user);

    public void affectUserToken(User user , String token);
}
