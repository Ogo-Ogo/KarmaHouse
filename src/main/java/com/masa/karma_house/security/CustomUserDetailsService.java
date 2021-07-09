package com.masa.karma_house.security;
import com.masa.karma_house.repositories.AuthenticationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    private AuthenticationRepository dao;

    @Autowired
    public void setInjection(AuthenticationRepository authenticationRepository) {
        this.dao = authenticationRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
        com.masa.karma_house.entities.User userFounded = dao.findByName(userName);
        System.out.println("userFounded" + userFounded);
        if (userFounded == null) {
            System.out.println("I'm before UsernameNotFoundException");
            throw new UsernameNotFoundException("Unknown user: " + userName);
        }
      /*  List<SimpleGrantedAuthority> authorities = new ArrayList<SimpleGrantedAuthority>();
        Set<Role> authStrings = userFounded.getRoles();
        for (Role authString : authStrings) {
            authorities.add(new SimpleGrantedAuthority(authString.toString()));
        }*/
        System.out.println("I'm after if UsernameNotFoundException");
        UserDetails user = org.springframework.security.core.userdetails.User.builder()
                .username(userFounded.getName())
                .authorities("user")
                .password(userFounded.getPassword())
                .build();
        System.out.println("returned user" + user);
        return user;
    }
}

