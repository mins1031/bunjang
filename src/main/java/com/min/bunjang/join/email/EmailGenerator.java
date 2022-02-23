package com.min.bunjang.join.email;

import org.springframework.mail.SimpleMailMessage;

public class EmailGenerator {
    private static final String CONFIRM_EMAIL_SUBJECT = "회원가입 이메일 인증";
    private static final String CONFIRM_EMAIL_CONTENT = "회원가입 인증 이메일 입니다. 아래 링크를 클릭해주세요";
    private static final String CONFIRM_EMAIL_LINK = "http://localhost:8080/confirm-email?token=";
    private static final String SENDER_EMAIL_ADDRESS = "jminyeong96@gmail.com";

    public static SimpleMailMessage generateSimpleMailMessage(String receiverEmail, String tokenValue) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(receiverEmail);
        mailMessage.setFrom(SENDER_EMAIL_ADDRESS);
        mailMessage.setSubject(CONFIRM_EMAIL_SUBJECT);
        mailMessage.setText(CONFIRM_EMAIL_CONTENT + "\n" + CONFIRM_EMAIL_LINK + tokenValue);
        return mailMessage;
    }
}
