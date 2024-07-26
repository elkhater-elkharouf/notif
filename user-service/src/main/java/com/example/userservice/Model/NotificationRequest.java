package com.example.userservice.Model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class NotificationRequest {
    private String token;
    private String title;
    private String body;

}
