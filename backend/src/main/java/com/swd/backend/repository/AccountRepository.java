package com.swd.backend.repository;

import com.swd.backend.entity.AccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface AccountRepository extends JpaRepository<AccountEntity, String> {
    public AccountEntity findUserEntityByEmail(String email);

    public AccountEntity findUserEntityByUserId(String userId);

    public AccountEntity findUserEntityByPhone(String phone);

    public List<AccountEntity> findAccountEntitiesByRoleIdIn(Collection<Integer> roles);
}
