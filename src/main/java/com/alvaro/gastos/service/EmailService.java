package com.alvaro.gastos.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendTemporaryPassword(String to, String username, String tempPassword){
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Tu cuenta fue creada - app Gastos");
        message.setText("Hola "+username+ ",\n\n" +
                "Tu cuenta fue creada exitosamente. \n" +
                "Tu contraseña temporal es: " + tempPassword+ "\n\n"+
                "Por tu seguridad deberas cambiarla en tu primer inicio de sesión. \n\n" +
                "Saludos, \nEquipo App Gastos.");
        mailSender.send(message);
    }
}
