package SpringBoot.Policy_Module_Pro_Max.models;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.*;

@SuppressWarnings("serial")
public class UserInfo extends org.springframework.security.core.userdetails.User {

    private final User user;
    private final Long id;

    private Collection<GrantedAuthority> grantedAuthorities = new HashSet<>();

    public UserInfo(User user, Boolean enabled, Boolean accountNonExpired, Boolean credentialsNonExpired, Boolean accountNonLocked) {

        super(user.getUsername(),
                user.getPassword(),
                enabled,
                accountNonExpired,
                credentialsNonExpired,
                accountNonLocked,
                getGrantedAuthorities(user.getRoles()));
        this.user = user;
        this.id = user.getId();

        for(Role role : user.getRoles()) {
            grantedAuthorities.add(new SimpleGrantedAuthority(role.getName()));
        }
    }

    public static List<GrantedAuthority> getGrantedAuthorities(Set<Role> roles) {
        List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
        for(Role role : roles) {
            authorities.add(new SimpleGrantedAuthority(role.getName()));
        }
        return authorities;
    }

    public User getUser() {
        return user;
    }

    public Long getId() {
        return id;
    }

    public Collection<GrantedAuthority> getGrantedAuthorities() {
        return grantedAuthorities;
    }

    public void setGrantedAuthority(Collection<GrantedAuthority> grantedAuthority) {
        this.grantedAuthorities = grantedAuthority;
    }

}
