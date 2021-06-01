package com.drawsforall.user.management.business;

import com.drawsforall.user.management.business.exception.UserNotFoundException;
import com.drawsforall.user.management.persistence.AdminUserRepository;
import com.drawsforall.user.management.persistence.UserRepository;
import com.drawsforall.user.management.persistence.entity.AdminUser;
import com.drawsforall.user.management.persistence.entity.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final AdminUserRepository userRepository;

    @Autowired
    public UserDetailsServiceImpl(AdminUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        AdminUser user = userRepository.findByUsernameIgnoreCase(
                username).orElseThrow(() -> new UserNotFoundException()
        );



        Set<GrantedAuthority> grantedAuthorities = getAuthorities(user);
        return new User(user.getUsername(), user.getPassword(), grantedAuthorities);
    }

    private Set<GrantedAuthority> getAuthorities(AdminUser user) {
        Set<Role> roleByUserId = user.getRoles();
        return roleByUserId.stream()
                .map(role -> new SimpleGrantedAuthority(role.getName().toString().toUpperCase()))
                .collect(Collectors.toSet());
    }
}
