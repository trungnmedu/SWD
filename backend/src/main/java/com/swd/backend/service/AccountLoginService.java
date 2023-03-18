package com.swd.backend.service;

import com.swd.backend.entity.AccountLoginEntity;
import com.swd.backend.repository.AccountLoginRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class AccountLoginService {
    AccountLoginRepository accountLoginRepository;

    //Find state-login of user on app's login-context database.
    public List<AccountLoginEntity> findLogin(String userId) {
        return accountLoginRepository.findLoginStateByUserId(userId);
    }

    //Save state-login of user on app's login-context database.
    public void saveLogin(String userId, String token) {
        AccountLoginEntity accountLogin = AccountLoginEntity.builder().userId(userId).accessToken(token).build();
        try {
            accountLoginRepository.save(accountLogin);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public void deleteAllLogin(String userId) {
        List<AccountLoginEntity> logins = accountLoginRepository.findLoginStateByUserId(userId);
        if (logins != null && logins.size() > 0) {
            logins.forEach((login) -> {
                accountLoginRepository.deleteById(login.getId());
            });
        }
    }

    //Destroy state-login of user on app's login-context database.
    public void logoutByToken(String token) {
        new Thread(() -> {
            try {
                AccountLoginEntity login = accountLoginRepository.findTopByAccessToken(token);
                if (login != null) {
                    login.setLogout(true);
                    accountLoginRepository.deleteById(login.getId());
                }
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }).start();
    }

    public AccountLoginEntity findLoginByToken(String token) {
        return accountLoginRepository.findTopByAccessToken(token);
    }
}
