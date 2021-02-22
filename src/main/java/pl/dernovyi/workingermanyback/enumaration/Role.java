package pl.dernovyi.workingermanyback.enumaration;

import static pl.dernovyi.workingermanyback.constant.Authority.*;

public enum Role {
    ROLE_USER(USER_AUTHORITIES),
    ROLE_MANAGER(MANAGER_AUTHORITIES),
    ROLE_ADMIN(ADMIN_AUTHORITIES),
    ROLE_SUPER_ADMIN(SUPER_ADMIN_AUTHORITIES);
    private String[] authorities;

    Role(String... authorities){
        this.authorities = authorities;
    }
    public String[] getAuthorities(){
        return authorities;
    }
}
