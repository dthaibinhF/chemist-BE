package dthaibinhf.project.chemistbe.controller;


import dthaibinhf.project.chemistbe.dto.AccountDTO;
import dthaibinhf.project.chemistbe.service.AccountService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/account")
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AccountController {
    AccountService accountService;

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")  // Only ADMIN/MANAGER can view user accounts
    public ResponseEntity<AccountDTO> getAccount(@PathVariable Integer id) {
        return ResponseEntity.ok(accountService.getAccountById(id));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")  // Only ADMIN/MANAGER can list all accounts
    public ResponseEntity<List<AccountDTO>> getAllAccounts() {
        return ResponseEntity.ok(accountService.getAllAccounts());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")  // Only ADMIN/MANAGER can update accounts
    public ResponseEntity<AccountDTO> updateAccount(@PathVariable Integer id, @Valid @RequestBody AccountDTO accountDTO) {
        return ResponseEntity.ok(accountService.updateAccount(id, accountDTO));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")  // Only ADMIN can delete accounts
    public ResponseEntity<Void> deleteAccount(@PathVariable Integer id) {
        accountService.deleteAccount(id);
        return ResponseEntity.noContent().build();
    }
}
