package com.example.security.config;

import com.example.security.model.Authority;
import com.example.security.model.Customer;
import com.example.security.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Component
public class ProjectAuthenticationProvider implements AuthenticationProvider {
    //AuthenticationManager từ username và pwd ng dùng nhập vào, tạo 1 obj Authentication
    //rồi chuyển cho AuthenticationProvider
    //Hàm authenticate của AuthenticationProvider chịu trách nghiệm xử lý logic authenticate
    //với sự giúp đỡ của PasswordEncoder
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        String pwd = authentication.getCredentials().toString();
        List<Customer> customer = customerRepository.findByEmail(username);
        if (customer.size() > 0) {
            if (passwordEncoder.matches(pwd, customer.get(0).getPwd())) {
                return
                    new UsernamePasswordAuthenticationToken(username, pwd, getGrantedAuthority(customer.get(0).getAuthorities()));
            } else {
                throw new BadCredentialsException("Invalid password");
            }
        } else {
            throw new BadCredentialsException("No user founded");
        }
    }

    private List<GrantedAuthority> getGrantedAuthority(Set<Authority> authorities) {
        //chuyển 1 Set<Authority> của 1 Customer -> 1 List<SimpleGrantedAuthority>
        //để có thể pass làm tham số của UsernamePasswordAuthenticationToken(username, pwd, authorities);
        List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
        for(Authority authority : authorities) {
            grantedAuthorities.add(new SimpleGrantedAuthority(authority.getName()));
        }
        return grantedAuthorities;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        //trả về loại authetication mà AuthenticationProvider này sử dụng
        return (UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication));
    }
}
