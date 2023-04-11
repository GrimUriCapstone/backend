package grimuri.backend.global.util;

import javax.servlet.http.HttpServletRequest;

public class RequestUtil {

    public static Boolean isAuthorizationToken(HttpServletRequest request) {
        boolean exists = request.getHeader("Authorization") != null;

        return exists;
    }

    public static String getAuthorizationToken(String header) {
        if (header == null || !header.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Invalid authorization header");
        }

        String[] parts = header.split(" ");
        if (parts.length != 2) {
            throw new IllegalArgumentException("Invalid authorization header");
        }

        return parts[1];
    }

    public static String getAuthorizationToken(HttpServletRequest request) throws IllegalArgumentException {
        return getAuthorizationToken(request.getHeader("Authorization"));
    }

}
