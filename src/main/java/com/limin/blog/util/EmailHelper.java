package com.limin.blog.util;

import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Service;

@Service
public class EmailHelper {
    private JavaMailSenderImpl mailSender;

    public boolean sendEmail(){
        return true;
    }
}
