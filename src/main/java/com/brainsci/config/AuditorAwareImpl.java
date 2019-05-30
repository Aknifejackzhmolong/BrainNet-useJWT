package com.brainsci.config;

import com.brainsci.springsecurity.JwtUserDetails;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Spring Data 提供的审计功能，此处记录用户名为createdBy
 * 其审计的对象为实现AbstracEntity的entity
 * 在使用 repository 保存对象时， createdBy CreatedDate lastModifiedBy lastModifiedDate 自动插入
 */
@Component("auditorAware")
public class AuditorAwareImpl implements AuditorAware<String> {

    @Override
    public Optional<String> getCurrentAuditor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object object = authentication.getPrincipal();
        if(object instanceof String)
            return Optional.of((String)object);
        JwtUserDetails currentUser = (JwtUserDetails) object;
        return Optional.of(currentUser.getUsername());
    }
}

