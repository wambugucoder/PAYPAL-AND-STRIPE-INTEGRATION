package com.gofundme.server.security

import com.gofundme.server.model.UserModel
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails


class GoFundMeUserDetails(private val user:UserModel):UserDetails {
    override fun getAuthorities(): MutableCollection<out GrantedAuthority> {
       return mutableListOf(SimpleGrantedAuthority(user.roles.toString()))
    }

    override fun getPassword(): String {
       return user.password
    }

    override fun getUsername(): String {
       return user.email
    }

    override fun isAccountNonExpired(): Boolean {
       return user.isAccountNonExpired
    }

    override fun isAccountNonLocked(): Boolean {
       return user.isAccountNotLocked
    }

    override fun isCredentialsNonExpired(): Boolean {
       return user.isAccountNonExpired
    }

    override fun isEnabled(): Boolean {
        return user.isEnabled
    }
}