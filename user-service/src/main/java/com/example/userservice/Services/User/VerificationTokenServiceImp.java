package com.example.userservice.Services.User;

import com.example.userservice.Entities.User;
import com.example.userservice.Entities.VerificationToken;
import com.example.userservice.Repository.VerificationTokenRepository;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Optional;

@Service
@Transactional
public class VerificationTokenServiceImp implements VerificationTokenService{

    private VerificationTokenRepository verificationTokenRepository;

    public VerificationTokenServiceImp(VerificationTokenRepository verificationTokenRepository) {
        this.verificationTokenRepository = verificationTokenRepository;
    }

    @Override
    public VerificationToken findByToken(String token) {
        return verificationTokenRepository.findByToken(token);
    }

    @Override
    public VerificationToken findByUser(User user) {
        return verificationTokenRepository.findByUser(user);
    }

    @Override
    public void affectUserToken(User user, String token) {
        VerificationToken verificationToken = new VerificationToken(token,user);
        //Sexpire dans 24H
        verificationToken.setExpiryDate(calculateExpiryDate(24*60));
        verificationTokenRepository.save(verificationToken);
    }

    //Calcul expiry Date
    private Timestamp calculateExpiryDate(int expiryTimeInMinute){
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MINUTE,expiryTimeInMinute);
        return new Timestamp(cal.getTime().getTime());
    }


}
