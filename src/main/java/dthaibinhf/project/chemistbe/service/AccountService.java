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

import java.util.List;
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
        Role role = roleRepository.findById(accountDTO.getRoleId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Role not found: " + accountDTO.getRoleId()));
        accountMapper.partialUpdate(accountDTO, account);
        account.setRole(role);
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
}
