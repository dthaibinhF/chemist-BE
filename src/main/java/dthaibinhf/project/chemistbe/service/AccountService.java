package dthaibinhf.project.chemistbe.service;

import dthaibinhf.project.chemistbe.mapper.AccountMapper;
import dthaibinhf.project.chemistbe.repository.AccountRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;
}
