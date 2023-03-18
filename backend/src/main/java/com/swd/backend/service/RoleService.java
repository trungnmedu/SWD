package com.swd.backend.service;

import com.swd.backend.entity.RoleEntity;
import com.swd.backend.repository.RoleRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

@Service
@AllArgsConstructor
public class RoleService {
    private RoleRepository roleRepository;

    public RoleEntity getRoleById(int id) {
        return roleRepository.findRoleEntityById(id);
    }

    public RoleEntity getRoleByRoleName(String roleName) {
        return roleRepository.findRoleEntityByRoleName(roleName);
    }

    public List<RoleEntity> getRoleIdsFromListRoleName(Collection<String> roleNames) {
        return roleRepository.findRoleEntitiesByRoleNameIn(roleNames);
    }
}
