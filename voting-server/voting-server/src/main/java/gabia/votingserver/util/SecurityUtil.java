package gabia.votingserver.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtil {

    public static String getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        validate(authentication);
        return authentication.getName();
    }

    public static String getCurrentUserRole() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        validate(authentication);
        for (GrantedAuthority authority : authentication.getAuthorities()) {
            return authority.getAuthority().substring(5);
        }
        return "INVALID_USER";
    }

    private static void validate(Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            throw new RuntimeException("인증 정보가 없습니다.");
        }
    }
}
