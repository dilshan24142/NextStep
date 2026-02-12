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

    /* ===== NextStep User Fields ===== */
    @Embedded
    @Valid
    private Username name; // firstName + lastName

    @Column(unique = true, nullable = false)
    @Email(message = "Enter a valid email")
    @NotBlank(message = "Email can't be blank")
    private String email;

    @Column(nullable = false)
    @NotBlank(message = "Password can't be blank")
    private String password;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "Choose your gender please")
    private Gender gender;

    @Column(unique = true)
    private String phoneNumber;

    @Column(length = 1000)
    private String profilePicture;

    private Boolean isVerified = false;

    @Enumerated(EnumType.STRING)
    @NotNull
    private Role role;

    /* ===== Legacy / Backward Compatibility Fields ===== */
    @Column(name = "login_username")
    private String loginUsername; // old login username

    @Column(name = "legacy_full_name")
    private String legacyFullName; // old full name

    @Column(name = "legacy_phone")
    private String legacyPhone; // old phone number

    /* ===== Utility Methods ===== */

    /**
     * Returns merged full name: first tries new Username object, then legacyFullName, then email
     */
    public String getMergedFullName() {
        if (name != null && name.getFirstName() != null && name.getLastName() != null) {
            return name.getFirstName() + " " + name.getLastName();
        }
        if (legacyFullName != null && !legacyFullName.trim().isEmpty()) {
            return legacyFullName;
        }
        return email;
    }

    /**
     * Returns full name from Username object, falls back to legacyFullName
     */
    public String getFullName() {
        if (name != null) {
            return name.getFirstName() + " " + name.getLastName();
        }
        return legacyFullName;
    }

    /* =======================
       Spring Security Methods
       ======================= */

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (role != null) {
            return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
        }
        return List.of();
    }

    @Override
    public String getUsername() {
        return this.email; // login uses email
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return Boolean.TRUE.equals(this.isVerified);
    }

    /* ===== Helper Methods for Legacy Migration ===== */

    public void populateNameFromLegacyFullName() {
        if (this.legacyFullName != null && !this.legacyFullName.trim().isEmpty()) {
            String[] nameParts = this.legacyFullName.split("\\s+", 2);
            if (this.name == null) {
                this.name = new Username();
            }
            this.name.setFirstName(nameParts[0]);
            this.name.setLastName(nameParts.length > 1 ? nameParts[1] : "");
        }
    }

    public void setName(String firstName, String lastName) {
        if (this.name == null) {
            this.name = new Username();
        }
        this.name.setFirstName(firstName);
        this.name.setLastName(lastName);
    }
}
