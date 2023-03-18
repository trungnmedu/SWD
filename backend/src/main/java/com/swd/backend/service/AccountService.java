package com.swd.backend.service;

import com.swd.backend.api.v1.admin.accounts.GetAllAccountResponse;
import com.swd.backend.constance.FrontEndDeployedLink;
import com.swd.backend.constance.RoleProperties;
import com.swd.backend.entity.AccountEntity;
import com.swd.backend.entity.AccountOtpEntity;
import com.swd.backend.entity.RoleEntity;
import com.swd.backend.model.AccountModel;
import com.swd.backend.model.FilterModel;
import com.swd.backend.model.SearchModel;
import com.swd.backend.repository.AccountRepository;
import com.swd.backend.utils.DateHelper;
import com.swd.backend.utils.RegexHelper;
import lombok.AllArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class AccountService {
    public static final String DISABLED_USER_REASON = "The owner is disabled by admin";
    private final AccountRepository accountRepository;
    private final RoleService roleService;
    private final BCryptPasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final AccountLoginService accountLoginService;
    private final YardService yardService;
    private final FirebaseStoreService firebaseStoreService;
    private final InactivationService inactivationService;
    private final ReactivationService reactivationService;

    public AccountEntity updatePassword(String username, String password) throws DataAccessException {
        AccountEntity account = findAccountByUsername(username);
        if (account == null) {
            return null;
        }
        account.setPassword(passwordEncoder.encode(password));
        accountRepository.save(account);
        accountLoginService.deleteAllLogin(username);
        return account;
    }

    public AccountEntity findAccountByUsername(String username) {
        //Case username is null
        if (username == null) {
            return null;
        }

        //Find user by username, phone, or password
        if (username.matches(RegexHelper.EMAIL_REGEX)) {
            return accountRepository.findUserEntityByEmail(username.toLowerCase());
        } else if (username.matches("\\d+")) {
            return accountRepository.findUserEntityByPhone(username);
        } else {
            return accountRepository.findUserEntityByUserId(username);
        }
    }

    public AccountEntity createAccount(String email, String fullName, String password, String phone, String roleName) throws DataAccessException {
        String uuid = UUID.randomUUID().toString();
        RoleEntity roleEntity = roleService.getRoleByRoleName(roleName);
        Timestamp now = DateHelper.getTimestampAtZone(DateHelper.VIETNAM_ZONE);
        AccountEntity accountEntity = AccountEntity.builder()
                .userId(uuid)
                .email(email.toLowerCase())
                .fullName(fullName)
                .phone(phone)
                .password(passwordEncoder.encode(password))
                .roleId(roleEntity.getId())
                .createAt(now)
                .build();
        try {
            accountRepository.save(accountEntity);
            return accountEntity;
        } catch (DataIntegrityViolationException dataIntegrityViolationException) {
            throw new DataIntegrityViolationException("Email or phone already use by another account.");
        }
    }

    public void sendOtpVerifyAccount(AccountEntity accountEntity, AccountOtpEntity accountOtpEntity) {
        String emailSubject = "VERIFY PLAYGROUND BASKETBALL CODE";
        String htmlBody =
                "<img style=\"display: block; width: 60px; padding: 2px; height: 60px; margin: auto;\" src=\"https://firebasestorage.googleapis.com/v0/b/fu-swp391.appspot.com/o/mail-icon.png?alt=media\">" +
                        "<h1 style=\"font-family:open Sans Helvetica, Arial, sans-serif; margin: 0; font-size:20px; padding: 2px; text-align: center;\">Playground Basketball</h1>" +
                        "<p style=\"font-family:open Sans Helvetica, Arial, sans-serif;font-size:18px; margin: 0; padding: 2px; text-align: center;\">Please use the verification code below on the Playground Basketball website:</p>" +
                        "<p style=\"font-family:open Sans Helvetica, Arial, sans-serif;font-size:20px; margin: 0; font-weight:bold;line-height:1;text-align:center;\">" +
                        "<span style=\"color:#222222; background-color:#aad8ff;\">" + accountOtpEntity.getOtpCode() + "</span></p>" +
                        "<p style=\"font-family:open Sans Helvetica, Arial, sans-serif;font-size:18px; padding: 2px; margin: 0; text-align: center;\">It expires in 5 minutes.</p>" +
                        "<p style=\"font-family:open Sans Helvetica, Arial, sans-serif;font-size:18px; padding: 2px; margin: 0; text-align: center;\">If you didn't request this, you can ignore this email or let us know.</p>" +
                        "<p style=\"font-family:open Sans Helvetica, Arial, sans-serif;font-size:18px; padding: 2px; margin: 0; text-align: center;\">Thank!</p>" +
                        "<p style=\"font-family:open Sans Helvetica, Arial, sans-serif;font-size:18px; padding: 2px; margin: 0; text-align: center;\">Playground Basketball</p>";
        emailService.sendHtmlTemplateMessage(accountEntity.getEmail(), emailSubject, htmlBody);
    }

    public AccountEntity createOwnerAccount(String email, String fullName, String password, String phone) throws DataAccessException {
        try {
            String uuid = UUID.randomUUID().toString();
            RoleEntity roleEntity = roleService.getRoleByRoleName(RoleProperties.ROLE_OWNER);
            AccountEntity accountEntity = AccountEntity.builder()
                    .userId(uuid)
                    .email(email.toLowerCase())
                    .fullName(fullName)
                    .phone(phone)
                    .password(passwordEncoder.encode(password))
                    .roleId(roleEntity.getId())
                    .createAt(DateHelper.getTimestampAtZone(DateHelper.VIETNAM_ZONE))
                    .isConfirmed(true)
                    .isActive(true)
                    .build();
            accountRepository.save(accountEntity);
            return accountEntity;
        } catch (DataAccessException dataAccessException) {
            if (dataAccessException instanceof DataIntegrityViolationException) {
                throw new DataIntegrityViolationException("Email or phone already use by another account.");
            } else {
                throw dataAccessException;
            }
        }
    }

    public void sendOwnerAccountViaEmail(String email, String password) {
        String emailSubject = "INVITE TO USE YARD BOOKING ADMIN ACCOUNT";
        String htmlBody =
                "<img style=\"display: block; width: 60px; padding: 2px; height: 60px; margin: auto;\" src=\"https://firebasestorage.googleapis.com/v0/b/fu-swp391.appspot.com/o/mail-icon.png?alt=media\">" +
                        "<h1 style=\"font-family:open Sans Helvetica, Arial, sans-serif; margin: 0; font-size:20px; padding: 2px; text-align: center;\">Playground Basketball</h1>" +
                        "<p style=\"font-family:open Sans Helvetica, Arial, sans-serif;font-size:18px; margin: 0; padding: 2px; text-align: center;\">Please use the password below on the Playground Basketball website:</p>" +
                        "<p style=\"font-family:open Sans Helvetica, Arial, sans-serif;font-size:20px; margin: 0; font-weight:bold;line-height:1;text-align:center;\">" +
                        "<span style=\"color:#222222; background-color:#aad8ff;\">" +password + "</span></p>" +
                        "<p style=\"font-family:open Sans Helvetica, Arial, sans-serif;font-size:18px; padding: 2px; margin: 0; text-align: center;\">If you didn't request this, you can ignore this email or let us know.</p>" +
                        "<p style=\"font-family:open Sans Helvetica, Arial, sans-serif;font-size:18px; padding: 2px; margin: 0; text-align: center;\">Thank!</p>" +
                        "<p style=\"font-family:open Sans Helvetica, Arial, sans-serif;font-size:18px; padding: 2px; margin: 0; text-align: center;\">Playground Basketball</p>";
        emailService.sendHtmlTemplateMessage(email, emailSubject, htmlBody);
    }

    public void modifyUserInformation(String userId, String fullName, String phone, Boolean isActive) {
        AccountEntity account = accountRepository.findUserEntityByUserId(userId);
        if (account == null) {
            throw new RuntimeException("Can not find account.");
        }
        if (fullName != null) account.setFullName(fullName);
        if (phone != null) {
            if (phone.equals("")) phone = null;
            account.setPhone(phone);
        }
        if (isActive == null) {
            accountRepository.save(account);
            return;
        }
        if (isActive && !account.isActive()) {
            account.setActive(true);
            new Thread(() -> {
                try {
                    yardService.reactiveAllYardsOfOwner(userId);
                    yardService.getAllYardEntityOfOwner(userId).forEach(yard -> {
                        if (!yard.isActive())
                            reactivationService.processReactiveYard(yard.getId());
                    });
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }).start();
        } else if (!isActive && account.isActive()) {
            account.setActive(false);
            new Thread(() -> {
                try {
                    accountLoginService.deleteAllLogin(userId);
                    yardService.getAllYardEntityOfOwner(userId).forEach(yard -> {
                        if (yard.isActive())
                            inactivationService.processInactivateYard(userId, yard.getId(), DISABLED_USER_REASON);
                    });
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }).start();
        }
        accountRepository.save(account);
    }

    public void updateUser(AccountEntity accountEntity) throws DataAccessException {
        accountRepository.save(accountEntity);
    }

    public String getRoleFromUserId(String userId) {
        AccountEntity accountEntity = accountRepository.findUserEntityByUserId(userId);
        return roleService.getRoleById(accountEntity.getRoleId()).getRoleName();
    }

    public AccountModel updateAccount(MultipartFile avatar, String userId, String fullName, String phone) throws IOException, DataAccessException {
        AccountEntity account = findAccountByUsername(userId);
        if (account == null) {
            return null;
        }

        if (avatar != null) {
            String url = firebaseStoreService.uploadFile(avatar);
            account.setAvatar(url);
        }

        if (phone != null && phone.matches(RegexHelper.PHONE_REGEX_LOCAL)) {
            account.setPhone(phone);
        }

        if (fullName != null && fullName.trim().length() > 0) {
            account.setFullName(fullName.trim());
        }

        accountRepository.save(account);
        RoleEntity role = roleService.getRoleById(account.getRoleId());
        return AccountModel.builder()
                .role(role.getRoleName())
                .userId(account.getUserId())
                .fullName(account.getFullName())
                .avatar(account.getAvatar())
                .createAt(account.getCreateAt().toString())
                .phone(account.getPhone())
                .isConfirmed(account.isConfirmed())
                .email(account.getEmail())
                .build();
    }

    public AccountModel verifyCurrentPassword(String userId, String password) throws DataAccessException {
        if (userId == null || password == null) {
            return null;
        }
        AccountEntity account = accountRepository.findUserEntityByUserId(userId);
        if (account == null) {
            return null;
        }
        if (passwordEncoder.matches(password, account.getPassword())) {
            RoleEntity role = roleService.getRoleById(account.getRoleId());
            return AccountModel.builder()
                    .role(role.getRoleName())
                    .userId(account.getUserId())
                    .fullName(account.getFullName())
                    .avatar(account.getAvatar())
                    .createAt(account.getCreateAt().toString())
                    .phone(account.getPhone())
                    .isConfirmed(account.isConfirmed())
                    .email(account.getEmail())
                    .build();
        }
        return null;
    }

    public GetAllAccountResponse searchAndFilterAccount(SearchModel searchModel) {
        int pageValue = 1;
        int offSetValue = 10;
        List<String> roleNames = new ArrayList<>();
        roleNames.add(RoleProperties.ROLE_OWNER);
        roleNames.add(RoleProperties.ROLE_USER);
        List<RoleEntity> roles = roleService.getRoleIdsFromListRoleName(roleNames);
        List<Integer> roleIds = roles.stream().map(RoleEntity::getId).collect(Collectors.toList());
        List<AccountModel> accounts = transformAccountEntityToAccountModel(accountRepository.findAccountEntitiesByRoleIdIn(roleIds), roles);

        if (searchModel != null) {
            accounts = searchAccounts(searchModel.getKeyword(), accounts);
            accounts = filterAccounts(searchModel.getFilter(), accounts);
            accounts = sortAccounts(searchModel.getSort(), accounts);
            pageValue = searchModel.getPage() != null ? searchModel.getPage() : 1;
            offSetValue = searchModel.getItemsPerPage() != null ? searchModel.getItemsPerPage() : 10;
        }

        if (accounts == null || accounts.size() == 0) {
            return GetAllAccountResponse.builder().accounts(Collections.emptyList()).maxResult(0).page(0).build();
        }
        int maxResult = accounts.size();

        if ((pageValue - 1) * offSetValue >= maxResult) {
            pageValue = 1;
        }
        int startIndex = Math.max((pageValue - 1) * offSetValue, 0);
        int endIndex = Math.min((pageValue * offSetValue), maxResult);
        return GetAllAccountResponse.builder()
                .accounts(accounts.subList(startIndex, endIndex))
                .maxResult(maxResult)
                .page(pageValue)
                .build();
    }

    private List<AccountModel> sortAccounts(String columnSort, List<AccountModel> accounts) {
        if (columnSort == null || columnSort.trim().length() == 0) {
            return accounts;
        }
        String columnName = columnSort.trim().toLowerCase();
        char sort = columnSort.charAt(0);
        if (sort == '+' || sort == '-') {
            columnName = columnName.substring(1);
        } else {
            sort = '+';
        }

        if (columnName.equals("email")) {
            if (sort == '+') {
                accounts.sort(Comparator.comparing(AccountModel::getEmail));
            } else {
                accounts.sort((firstAccount, secondAccount) -> secondAccount.getEmail().compareTo(firstAccount.getEmail()));
            }
        }

        if (columnName.equals("fullName")) {
            if (sort == '+') {
                accounts.sort(Comparator.comparing(AccountModel::getFullName));
            } else {
                accounts.sort((firstAccount, secondAccount) -> secondAccount.getFullName().compareTo(firstAccount.getFullName()));
            }
        }


        if (columnName.equals("phone")) {
            accounts = accounts.stream().filter(account -> account.getPhone() != null || account.getPhone().length() > 0).collect(Collectors.toList());
            if (sort == '+') {
                accounts.sort(Comparator.comparing(AccountModel::getPhone));
            } else {
                accounts.sort((firstAccount, secondAccount) -> secondAccount.getPhone().compareTo(firstAccount.getPhone()));
            }
        }

        if (columnName.equals("createdAt")) {
            if (sort == '+') {
                accounts.sort(Comparator.comparing(AccountModel::getCreateAt));
            } else {
                accounts.sort((firstAccount, secondAccount) -> secondAccount.getCreateAt().compareTo(firstAccount.getCreateAt()));
            }
        }

        return accounts;
    }

    private List<AccountModel> filterAccounts(FilterModel filter, List<AccountModel> accounts) {
        if (filter == null) {
            return accounts;
        }

        String columnFilter = filter.getField();
        if (columnFilter.equals("role")) {
            return accounts.stream().filter(account -> account.getRole().equals(filter.getValue())).collect(Collectors.toList());
        }

        if (columnFilter.equals("isActive")) {
            if (Boolean.parseBoolean(filter.getValue())) {
                return accounts.stream().filter(AccountModel::isActive).collect(Collectors.toList());
            } else {
                return accounts.stream().filter(account -> !account.isActive()).collect(Collectors.toList());
            }
        }
        return accounts;
    }

    private List<AccountModel> searchAccounts(String keyword, List<AccountModel> accounts) {
        String keywordVale = keyword != null && keyword.trim().length() > 0 ? keyword.trim().toLowerCase() : null;
        if (keywordVale == null) {
            return accounts;
        }
        return accounts.stream().filter(account -> account.getFullName().toLowerCase().contains(keyword)
                || account.getEmail().toLowerCase().contains(keyword)
                || (account.getPhone() != null && account.getPhone().contains(keyword))
        ).collect(Collectors.toList());
    }

    private List<AccountModel> transformAccountEntityToAccountModel(List<AccountEntity> accounts, List<RoleEntity> roles) {
        HashMap<Integer, String> roleNameMapping = new HashMap<>();
        roles.forEach(role -> roleNameMapping.put(role.getId(), role.getRoleName()));
        return accounts.stream().map(account -> AccountModel.builder()
                .userId(account.getUserId())
                .role(roleNameMapping.get(account.getRoleId()))
                .email(account.getEmail())
                .phone(account.getPhone())
                .isActive(account.isActive())
                .avatar(account.getAvatar())
                .isConfirmed(account.isConfirmed())
                .createAt(account.getCreateAt().toString())
                .fullName(account.getFullName())
                .build()).collect(Collectors.toList());
    }
}