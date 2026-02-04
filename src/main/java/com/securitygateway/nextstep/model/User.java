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

    @Column(nullable = false)
    @NotBlank(message = "Password can't be blank")
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

    // old "username" (login username) - renamed to avoid confusion
    @Column(name = "login_username")
    private String loginUsername;    // different meaning than 'name'

    // old "fullName" - renamed to avoid confusion with name field
    @Column(name = "legacy_full_name")
    private String legacyFullName;

    // old "phone" - merged with phoneNumber
    // phoneNumber field above already covers this

    /* ===== Custom Utility Methods ===== */

    public String getMergedFullName() {
        if (name != null && name.getFirstName() != null && name.getLastName() != null) {
            return name.getFirstName() + " " + name.getLastName();
        }
        // Fallback to legacy full name if name object is not properly populated
        if (legacyFullName != null && !legacyFullName.trim().isEmpty()) {
            return legacyFullName;
        }
        // Final fallback - return email if no name is available
        return email;
    }

    // Convenience method to get full name from name object
    public String getFullName() {
        if (name != null) {
            return name.getFirstName() + " " + name.getLastName();
        }
        return null;
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
        return true; 
    }

    /* ===== Helper methods for migration ===== */
    
    // Method to populate name from legacy full name
    public void populateNameFromLegacyFullName() {
        if (this.legacyFullName != null && !this.legacyFullName.trim().isEmpty()) {
            String[] nameParts = this.legacyFullName.split("\\s+", 2);
            if (this.name == null) {
                this.name = new Username();
            }
            if (nameParts.length >= 1) {
                this.name.setFirstName(nameParts[0]);
            }
            if (nameParts.length >= 2) {
                this.name.setLastName(nameParts[1]);
            } else {
                this.name.setLastName("");
            }
        }
    }

    // Method to set name directly
    public void setName(String firstName, String lastName) {
        if (this.name == null) {
            this.name = new Username();
        }
        this.name.setFirstName(firstName);
        this.name.setLastName(lastName);
    }
}