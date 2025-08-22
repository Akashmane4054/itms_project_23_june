//package com.itms.product.service;
//
//import java.util.Arrays;
//import java.util.Collection;
//import java.util.Optional;
//import java.util.stream.Collectors;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.security.core.userdetails.User;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.core.userdetails.UsernameNotFoundException;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.stereotype.Service;
//
//import com.itms.product.domain.UserInfo;
//import com.itms.product.repository.UserInfoRepository;
//
//@Service
//public class UserInfoService implements UserDetailsService {
//
//    private final UserInfoRepository repository;
//    private final PasswordEncoder encoder;
//
//    public UserInfoService(UserInfoRepository repository, PasswordEncoder encoder) {
//        this.repository = repository;
//        this.encoder = encoder;
//    }
//
//    @Override
//    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//        Optional<UserInfo> userInfo = repository.findByEmail(username);
//        if (userInfo.isEmpty()) {
//            throw new UsernameNotFoundException("User not found with email: " + username);
//        }
//
//        UserInfo user = userInfo.get();
//        Collection<SimpleGrantedAuthority> authorities = Arrays.stream(user.getRoles().split(","))
//                .map(SimpleGrantedAuthority::new)
//                .collect(Collectors.toList());
//
//        return new User(user.getEmail(), user.getPassword(), authorities);
//    }
//
//    public String addUser(UserInfo userInfo) {
//        userInfo.setPassword(encoder.encode(userInfo.getPassword()));
//        repository.save(userInfo);
//        return "User added successfully!";
//    }
//}