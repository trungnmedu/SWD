package com.swd.backend.service;

import com.swd.backend.entity.AccountEntity;
import com.swd.backend.entity.AccountOtpEntity;
import com.swd.backend.repository.AccountOtpRepository;
import com.swd.backend.utils.DateHelper;
import lombok.AllArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.Random;

@Service
@AllArgsConstructor
public class OtpStateService {
    private AccountOtpRepository accountOtpRepository;
    private EmailService emailService;
    private AccountService accountService;

    private String generateOtp() {
        return new DecimalFormat("000000").format(new Random().nextInt(999999));
    }

    public AccountOtpEntity generateOtp(String userId) {
        String otp = generateOtp();
        Timestamp createAt = DateHelper.getTimestampAtZone(DateHelper.VIETNAM_ZONE);
        Timestamp expireAt = DateHelper.plusMinutes(createAt, 20);
        AccountOtpEntity accountOtpEntity = accountOtpRepository.findOtpStateByUserId(userId);
        if (accountOtpEntity != null) {
            accountOtpEntity.setOtpCode(otp);
            accountOtpEntity.setCreateAt(createAt);
            accountOtpEntity.setExpireAt(expireAt);
        } else {
            accountOtpEntity = AccountOtpEntity.builder().userId(userId).createAt(createAt).expireAt(expireAt).otpCode(otp).build();
        }
        accountOtpRepository.save(accountOtpEntity);
        return accountOtpEntity;
    }

    public AccountOtpEntity findOtpStateByUserId(String userId) {
        return accountOtpRepository.findOtpStateByUserId(userId);
    }

    public void sendEmailOtpRestPassword(String userId, String email) throws DataAccessException {
        AccountOtpEntity accountOtp = accountOtpRepository.findOtpStateByUserId(userId);
        if (accountOtp == null) {
            accountOtp = AccountOtpEntity.builder().userId(userId).build();
        }
        String otp = generateOtp();
        Timestamp createAt = DateHelper.getTimestampAtZone(DateHelper.VIETNAM_ZONE);
        Timestamp expireAt = DateHelper.plusMinutes(createAt, 20);

        accountOtp.setOtpCode(otp);
        accountOtp.setCreateAt(createAt);
        accountOtp.setExpireAt(expireAt);
        accountOtpRepository.save(accountOtp);
        AccountEntity account = accountService.findAccountByUsername(userId);
        sendEmailOtpResetPassword(account.getFullName(), account.getEmail(), otp);
    }

    private void sendEmailOtpResetPassword(String userName, String email, String otpCode) {
        String htmlBody = "<div style=\"font-family: Helvetica,Arial,sans-serif;min-width:1000px;overflow:auto;line-height:2\">" +
                " <div style=\"margin:50px auto;width:70%;padding:20px 0\">" +
                " <div style=\"border-bottom:1px solid #eee\">" +
                "<h1 style=\"font-size:1.4em;color: #00466a;text-decoration:none;font-weight:600\">Playground Basketball</h1>" +
                "    </div>" +
                "    <p style=\"font-size:1.1em\">Hi, " + userName + ".</p>" +
                "    <p>Thank you for choosing Playground Basketball. Use the following OTP to complete your Reset Password procedures. OTP is valid for 20 minutes</p>" +
                "    <h2 style=\"background: #00466a;margin: 0 auto;width: max-content;padding: 0 10px;color: #fff;border-radius: 4px;\">" + otpCode + "</h2>" +
                "    <p style=\"font-size:0.9em;\">Regards,<br />Playground Basketball</p>" +
                "  </div>" +
                "</div>";
        String emailSubject = "RESET PASSWORD PLAYGROUND BASKETBALL CODE";
        emailService.sendHtmlTemplateMessage(email, emailSubject, htmlBody);
    }

    public boolean verifyOtp(String userId, String otp) {
        AccountOtpEntity accountOtp = accountOtpRepository.findOtpStateByUserId(userId);
        if (accountOtp == null) {
            return false;
        }
        if (accountOtp.getOtpCode().matches(otp)) {
            accountOtpRepository.delete(accountOtp);
            return true;
        }
        return false;
    }

    public void updateState(AccountOtpEntity accountOtp) {
        accountOtpRepository.save(accountOtp);
    }
}
