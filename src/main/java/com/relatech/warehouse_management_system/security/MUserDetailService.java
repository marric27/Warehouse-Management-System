//package com.relatech.warehouse_management_system.security;
//
//import com.relatech.warehouse_management_system.security.model.Role;
//import com.relatech.warehouse_management_system.security.model.UserEntity;
//import com.relatech.warehouse_management_system.security.repo.RoleRepository;
//import com.relatech.warehouse_management_system.security.repo.UserRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.security.core.userdetails.User;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.core.userdetails.UsernameNotFoundException;
//
//import java.util.Collection;
//import java.util.List;
//import java.util.stream.Collectors;
//
//public class MUserDetailService implements UserDetailsService {
//
////    @Autowired
////    private UserRepository userRepository;
////
////    @Autowired
////    private RoleRepository roleRepository;
//
//    @Override
//    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//        UserEntity user = userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("Username not found"));
//        return new User(user.getUsername(), user.getPassword(), mapRolesToAuthorities(user.getRoles()));
//    }
//
//    private Collection<GrantedAuthority> mapRolesToAuthorities(List<Role> roles) {
//        return roles.stream().map(role -> new SimpleGrantedAuthority(role.getName())).collect(Collectors.toList());
//    }
//
//}
