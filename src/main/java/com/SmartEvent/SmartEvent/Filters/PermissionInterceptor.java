package com.SmartEvent.SmartEvent.Filters;

import com.SmartEvent.SmartEvent.Anotation.RequirePermission;
import com.SmartEvent.SmartEvent.Enums.Permission;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Arrays;
import java.util.Collection;

@Component
public class PermissionInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            RequirePermission requirePermission = handlerMethod.getMethodAnnotation(RequirePermission.class);

            if (requirePermission != null) {
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

                if (authentication == null || !authentication.isAuthenticated()) {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    return false;
                }

                Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
                Permission[] requiredPermissions = requirePermission.value();

                // Check if user has any of the required permissions
                boolean hasPermission = Arrays.stream(requiredPermissions)
                        .anyMatch(permission -> authorities.stream()
                                .anyMatch(auth -> auth.getAuthority().equals(permission.name())));

                if (!hasPermission) {
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    return false;
                }
            }
        }

        return true;
    }
}