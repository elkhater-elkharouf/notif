package com.example.userservice.Controller;

import com.example.userservice.Model.NotificationRequest;
import com.example.userservice.Services.User.FirebaseNotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    @Autowired
    private FirebaseNotificationService firebaseNotificationService;

    @PostMapping("/send")
    public void sendNotification(@RequestBody NotificationRequest notificationRequest) {
        firebaseNotificationService.sendNotification(
                notificationRequest.getToken(),
                notificationRequest.getTitle(),
                notificationRequest.getBody()
        );
    }

}
