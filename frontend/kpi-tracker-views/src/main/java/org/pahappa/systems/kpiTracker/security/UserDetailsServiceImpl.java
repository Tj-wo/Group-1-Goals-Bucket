package org.pahappa.systems.kpiTracker.security;

import java.util.ArrayList;
import java.util.List;

import org.pahappa.systems.kpiTracker.core.services.StaffService;
import org.pahappa.systems.kpiTracker.models.staff.Staff;
import org.sers.webutils.model.security.User;
import org.sers.webutils.server.core.utils.ApplicationContextProvider;
import org.sers.webutils.server.core.service.UserService;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.GrantedAuthorityImpl;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 * Custom UserDetailsService implementation for Spring Security.
 * This class loads user details from the database for authentication.
 */
public class UserDetailsServiceImpl implements UserDetailsService {

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        try {
            // Get services from Spring context
            StaffService staffService = ApplicationContextProvider.getBean(StaffService.class);
            UserService userService = ApplicationContextProvider.getBean(UserService.class);

            // First try to find user directly by username
            User user = userService.getUserByUsername(username.trim());

            // If not found, try to find by email
            if (user == null) {
                user = userService.findUserByUsername(username.trim());
            }

            if (user == null) {
                // Fallback: Check for default admin user if no users in database
                if ("administrator".equals(username.trim())) {
                    return createUserDetails("admin@kpitracker.com", "password", "ROLE_ADMIN");
                }
                throw new UsernameNotFoundException("User not found: " + username);
            }

            // Check if user account is active
            if (user.getRecordStatus() != org.sers.webutils.model.RecordStatus.ACTIVE) {
                throw new UsernameNotFoundException("User account is inactive: " + username);
            }

            // Convert database user to Spring Security UserDetails
            return createUserDetailsFromDatabaseUser(user);

        } catch (Exception e) {
            // Fallback: Check for default admin user if database lookup fails
            if ("administrator".equals(username.trim())) {
                return createUserDetails("admin@kpitracker.com", "password", "ROLE_ADMIN");
            }
            throw new UsernameNotFoundException("Error loading user: " + username, e);
        }
    }

    /**
     * Creates UserDetails from a database User entity
     */
    private UserDetails createUserDetailsFromDatabaseUser(User user) {
        List<GrantedAuthority> authorities = new ArrayList<>();

        // Add user roles from database
        if (user.getRoles() != null && !user.getRoles().isEmpty()) {
            for (org.sers.webutils.model.security.Role role : user.getRoles()) {
                authorities.add(new GrantedAuthorityImpl("ROLE_" + role.getName().toUpperCase()));
            }
        } else {
            // Default role if no roles assigned
            authorities.add(new GrantedAuthorityImpl("ROLE_USER"));
        }

        // {noop} prefix indicates plain text password (for development only)
        String encodedPassword = "{noop}" + user.getPassword();

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                encodedPassword,
                true, // enabled
                true, // accountNonExpired
                true, // credentialsNonExpired
                true, // accountNonLocked
                authorities);
    }

    /**
     * Creates UserDetails for fallback admin user
     */
    private UserDetails createUserDetails(String username, String password, String role) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new GrantedAuthorityImpl(role));

        // {noop} prefix indicates plain text password (for development only)
        String encodedPassword = "{noop}" + password;

        return new org.springframework.security.core.userdetails.User(username, encodedPassword, true, true, true, true,
                authorities);
    }
}
