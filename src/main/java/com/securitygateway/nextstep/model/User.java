package com.securitygateway.nextstep.model;

import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "users")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_seq")
    @SequenceGenerator(name = "user_seq", sequenceName = "user_sequence", allocationSize = 1)
    private Long id;

    /* ===== Fields from NextStep User ===== */

    @Embedded
    @Valid
    private Username name;     // firstName + lastName

    @Column(unique = true, nullable = false)
    @Email(message = "Enter a valid email")
    @NotBlank(message = "Email can't be blank")
    private String email;

    @NotBlank(message = "Password can't be blank")
    @Column(nullable = false)
    private String password;    // unified password field

    @Enumerated(EnumType.STRING)
    @NotNull(message = "Choose your gender please")
    private Gender gender;

    @Column(unique = true)
    private String phoneNumber;

    @Column(length = 1000)
    private String profilePicture;

    private Boolean isVerified;

    @Enumerated(EnumType.STRING)
    private Role role;       // NextStep Role enum

    /* ===== Fields from StudentRegistrationSystem User ===== */

    // old "username" (login username)
    private String username;    // different meaning than 'name'

    // old "fullName"
    private String fullName;

    // old "phone"
    private String phone;

    /* ===== Custom Utility Methods ===== */

    public String getMergedFullName() {
        if (name != null) {
            return name.getFirstName() + " " + name.getLastName();
        }
        return fullName; // fallback from old system
    }

    /* ===== UserDetails Methods for JWT ===== */

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (role != null) {
            return List.of(new SimpleGrantedAuthority(role.name()));
        }
        return List.of();
    }

    @Override
    public String getUsername() {
        // For Spring Security login, email is username
        return this.email;
    }

    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() { return true; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() { return true; }
}
