package dthaibinhf.project.chemistbe.config;


import dthaibinhf.project.chemistbe.model.Account;
import dthaibinhf.project.chemistbe.repository.AccountRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.userdetails.User;

import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final AccountRepository repository;

    public CustomUserDetailsService(AccountRepository repository) {
        this.repository = repository;
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Account account = repository.findActiveByEmail(username).orElseThrow(
                () -> new UsernameNotFoundException(username)
        );
        GrantedAuthority authority = new SimpleGrantedAuthority(account.getRole().getName());
            return new  User(account.getEmail(), account.getPassword(), Collections.singleton(authority));
    }
}
