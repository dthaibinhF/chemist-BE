package dthaibinhf.project.chemistbe.service;

import dthaibinhf.project.chemistbe.dto.AccountDTO;
import dthaibinhf.project.chemistbe.mapper.AccountMapper;
import dthaibinhf.project.chemistbe.model.Account;
import dthaibinhf.project.chemistbe.model.Role;
import dthaibinhf.project.chemistbe.repository.AccountRepository;
import dthaibinhf.project.chemistbe.repository.RoleRepository;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AccountService {

    AccountRepository accountRepository;
    AccountMapper accountMapper;
    RoleRepository roleRepository;
    PasswordEncoder passwordEncoder;

    public List<AccountDTO> getAllAccounts() {
        return accountRepository.findAllActiveAccounts().stream()
                .map(accountMapper::toDto)
                .collect(Collectors.toList());
    }

    public AccountDTO getAccountById(Integer id) {
        Account account = accountRepository.findActiveById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found: " + id));
        return accountMapper.toDto(account);
    }

    @Transactional
    public AccountDTO updateAccount(Integer id, @Valid AccountDTO accountDTO) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found: " + id));
        
        // Handle multiple roles
        if (accountDTO.getRoleIds() != null && !accountDTO.getRoleIds().isEmpty()) {
            Set<Role> roles = new HashSet<>();
            for (Integer roleId : accountDTO.getRoleIds()) {
                Role role = roleRepository.findById(roleId)
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Role not found: " + roleId));
                roles.add(role);
            }
            account.setRoles(roles);
        }
        
        accountMapper.partialUpdate(accountDTO, account);
        if (accountDTO.getPassword() != null && !accountDTO.getPassword().isEmpty()) {
            account.setPassword(passwordEncoder.encode(accountDTO.getPassword()));
        }
        Account updatedAccount = accountRepository.save(account);
        return accountMapper.toDto(updatedAccount);
    }

    @Transactional
    public void deleteAccount(Integer id) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found: " + id));
        account.softDelete();
        accountRepository.save(account);
    }

    // Role management methods
    @Transactional
    public AccountDTO addRoleToAccount(Integer accountId, Integer roleId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found: " + accountId));
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Role not found: " + roleId));
        
        account.addRole(role);
        Account updatedAccount = accountRepository.save(account);
        return accountMapper.toDto(updatedAccount);
    }

    @Transactional
    public AccountDTO removeRoleFromAccount(Integer accountId, Integer roleId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found: " + accountId));
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Role not found: " + roleId));
        
        account.removeRole(role);
        Account updatedAccount = accountRepository.save(account);
        return accountMapper.toDto(updatedAccount);
    }

    @Transactional
    public AccountDTO setAccountRoles(Integer accountId, List<Integer> roleIds) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found: " + accountId));
        
        Set<Role> roles = new HashSet<>();
        for (Integer roleId : roleIds) {
            Role role = roleRepository.findById(roleId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Role not found: " + roleId));
            roles.add(role);
        }
        
        account.setRoles(roles);
        Account updatedAccount = accountRepository.save(account);
        return accountMapper.toDto(updatedAccount);
    }
}
