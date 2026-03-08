package com.project.dugoga.global.security.annotation;

import org.springframework.security.access.prepost.PreAuthorize;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

public interface Auth {
    /*
        마스터 전용
     */
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @PreAuthorize("hasRole('MASTER')")
    @interface IsMaster {
    }

    /*
        매니저 전용
     */
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @PreAuthorize("hasRole('MANAGER')")
    @interface IsManager {
    }

    /*
        마스터, 매니저 전용 (관리자)
     */
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @PreAuthorize("hasAnyRole('MASTER','MANAGER')")
    @interface IsAdmin {
    }

    /*
        마스터, 매니저, 오너 전용
     */
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @PreAuthorize("hasAnyRole('MASTER','MANAGER','OWNER')")
    @interface IsAdminOrOwner {
    }

    /*
        오너 전용
     */
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @PreAuthorize("hasRole('OWNER')")
    @interface IsOwner {
    }

    /*
        일반 사용자 전용
     */
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @PreAuthorize("hasRole('CUSTOMER')")
    @interface IsCustomer {
    }
}
