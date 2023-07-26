package com.pjt.petmily.domain.user.service;

import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class EmailServiceImpl implements EmailService {
    @Autowired
    JavaMailSender emailSender; // Bean 등록해둔 MailConfig를 emailsender라는 이름으로 autowired

    public static final String ePw = createKey();


    private MimeMessage createMessage(String to)throws Exception{
        System.out.println("보내는 대상 : "+ to);
        System.out.println("인증 번호 : "+ePw);
        MimeMessage  message = emailSender.createMimeMessage();

        String fromEmail = "ryejinee@gmail.com";
        String fromName = "PETMILY";

        InternetAddress fromAddress = new InternetAddress(fromEmail, fromName);
        InternetAddress toAddress = new InternetAddress(to);

        message.addRecipients(MimeMessage.RecipientType.TO, String.valueOf(toAddress));//보내는 대상
        message.setSubject("PETMILY 인증번호");//제목

        String msgg="";
        msgg+= "<div style='margin:20px;'>";
        msgg+= "<h1> 안녕하세요 PETMILY입니다. </h1>";
        msgg+= "<br>";
        msgg+= "<p>아래 코드를 복사해 입력해주세요<p>";
        msgg+= "<br>";
        msgg+= "<p>감사합니다.<p>";
        msgg+= "<br>";
        msgg+= "<div align='center' style='border:1px solid black; font-family:verdana';>";
        msgg+= "<h3 style='color:blue;'>인증 코드입니다.</h3>";
        msgg+= "<div style='font-size:130%'>";
        msgg+= "CODE : <strong>";
        msgg+= ePw+"</strong><div><br/> ";
        msgg+= "</div>";
        message.setText(msgg, "utf-8", "html");//내용
        message.setFrom(fromAddress);//보내는 사람

        return message;
    }

    public static String createKey() {
        StringBuffer key = new StringBuffer();
        Random rnd = new Random();

        for (int i = 0; i < 8; i++) { // 인증코드 8자리
            int index = rnd.nextInt(3); // 0~2 까지 랜덤

            switch (index) {
                case 0:
                    key.append((char) ((int) (rnd.nextInt(26)) + 97));
                    //  a~z  (ex. 1+97=98 => (char)98 = 'b')
                    break;
                case 1:
                    key.append((char) ((int) (rnd.nextInt(26)) + 65));
                    //  A~Z
                    break;
                case 2:
                    key.append((rnd.nextInt(10)));
                    // 0~9
                    break;
            }
        }
        return key.toString();
    }




    private Map<String, String> codes = new HashMap<>();
    @Override
    public String sendSimpleMessage(String to)throws Exception {
        // TODO Auto-generated method stub
        MimeMessage message = createMessage(to);
        try{//예외처리
            emailSender.send(message);
            codes.put(to, ePw);
        }catch(MailException es){
            es.printStackTrace();
            throw new IllegalArgumentException();
        }
        return ePw;
    }

    public String getVerificationCode(String userEmail){
        return codes.get(userEmail);
    }



    public static final String nPw = createNewPassword();

    private MimeMessage createPasswordMessage(String to)throws Exception{
        System.out.println("보내는 대상 : "+ to);
        System.out.println("인증 번호 : "+nPw);
        MimeMessage  message = emailSender.createMimeMessage();

        String fromEmail = "smk921@naver.com";
        String fromName = "PETMILY";

        InternetAddress fromAddress = new InternetAddress(fromEmail, fromName);
        InternetAddress toAddress = new InternetAddress(to);

        message.addRecipients(MimeMessage.RecipientType.TO, String.valueOf(toAddress));//보내는 대상
        message.setSubject("PETMILY 인증번호");//제목

        String msgg="";
        msgg+= "<div style='margin:20px;'>";
        msgg+= "<h1> 안녕하세요 PETMILY입니다. </h1>";
        msgg+= "<br>";
        msgg+= "<p>비밀번호가 변경되었습니다<p>";
        msgg+= "<br>";
//        msgg+= "<p>감사합니다.<p>";
//        msgg+= "<br>";
        msgg+= "<div align='center' style='border:1px solid black; font-family:verdana';>";
        msgg+= "<h3 style='color:blue;'>변경된 비밀번호입니다.</h3>";
        msgg+= "<div style='font-size:130%'>";
        msgg+= "PASSWORD : <strong>";
        msgg+= nPw+"</strong><div><br/> ";
        msgg+= "</div>";
        message.setText(msgg, "utf-8", "html");//내용
        message.setFrom(fromAddress);//보내는 사람

        return message;
    }

    public static String createNewPassword() {
        StringBuffer key = new StringBuffer();
        Random rnd = new Random();

        for (int i = 0; i < 8; i++) { // 인증코드 8자리
            int index = rnd.nextInt(2); // 0~1 까지 랜덤

            switch (index) {
                case 0:
                    key.append((char) ((int) (rnd.nextInt(26)) + 97));
                    //  a~z  (ex. 1+97=98 => (char)98 = 'b')
                case 1:
                    key.append((rnd.nextInt(10)));
                    // 0~9
                    break;
            }
        }
        return key.toString();
    }

    @Override
    public String sendNewPasswordMessage(String newPw)throws Exception {
        MimeMessage message = createPasswordMessage(newPw);
        try{
            emailSender.send(message);
        }catch(MailException es) {
            es.printStackTrace();
            throw new IllegalArgumentException();
        }
        return nPw;
    }


}
