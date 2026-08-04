package com.labplatform.lab_platform_backend.util;

import com.labplatform.lab_platform_backend.entity.*;
import com.labplatform.lab_platform_backend.repository.UserRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class SecurityUtil {

    private final UserRepository userRepository;

    public SecurityUtil(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            throw new AccessDeniedException("User is not authenticated");
        }
        String email = auth.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new AccessDeniedException("Authenticated user record not found"));
    }

    public boolean isSystemAdmin(User user) {
        return user.getRole() == Role.SYSTEM_ADMIN;
    }

    public boolean isInstitutionAdmin(User user) {
        return user.getRole() == Role.INSTITUTION_ADMIN;
    }

    public Long getUserInstitutionId(User user) {
        if (user.getInstitution() != null) {
            return user.getInstitution().getId();
        }
        if (user.getLaboratory() != null && user.getLaboratory().getInstitution() != null) {
            return user.getLaboratory().getInstitution().getId();
        }
        return null;
    }

    public boolean isSameInstitution(User user, Equipment equipment) {
        if (equipment == null) return false;
        Long userInstId = getUserInstitutionId(user);
        if (userInstId == null) return false;

        Long eqInstId = null;
        if (equipment.getLaboratory() != null && equipment.getLaboratory().getInstitution() != null) {
            eqInstId = equipment.getLaboratory().getInstitution().getId();
        }
        return userInstId.equals(eqInstId);
    }

    public boolean canManageEquipment(User user, Equipment equipment) {
        if (isSystemAdmin(user)) return true;
        if (user.getRole() == Role.RESEARCHER) return false;
        if (user.getRole() == Role.LAB_MANAGER && user.getLaboratory() != null && equipment != null && equipment.getLaboratory() != null) {
            return user.getLaboratory().getId().equals(equipment.getLaboratory().getId());
        }
        return isSameInstitution(user, equipment);
    }

    public boolean canViewEquipment(User user, Equipment equipment) {
        if (isSystemAdmin(user)) return true;
        if (Boolean.TRUE.equals(equipment.getShared())) return true;
        if (user.getRole() == Role.LAB_MANAGER && user.getLaboratory() != null && equipment != null && equipment.getLaboratory() != null) {
            return user.getLaboratory().getId().equals(equipment.getLaboratory().getId());
        }
        return isSameInstitution(user, equipment);
    }

    public boolean canManageCertificate(User user, EquipmentCertificate certificate) {
        if (certificate == null || certificate.getEquipment() == null) return false;
        return canManageEquipment(user, certificate.getEquipment());
    }

    public boolean canViewCertificate(User user, EquipmentCertificate certificate) {
        if (certificate == null || certificate.getEquipment() == null) return false;
        return canViewEquipment(user, certificate.getEquipment());
    }

    public boolean canManageBooking(User user, Booking booking) {
        if (booking == null || booking.getEquipment() == null) return false;
        return canManageEquipment(user, booking.getEquipment());
    }

    public boolean canViewBooking(User user, Booking booking) {
        if (booking == null) return false;
        if (isSystemAdmin(user)) return true;
        if (booking.getUser() != null && booking.getUser().getId().equals(user.getId())) return true;
        if (user.getRole() == Role.LAB_MANAGER && user.getLaboratory() != null && booking.getEquipment() != null && booking.getEquipment().getLaboratory() != null) {
            return user.getLaboratory().getId().equals(booking.getEquipment().getLaboratory().getId());
        }
        return isSameInstitution(user, booking.getEquipment());
    }

    public boolean canManageMaintenance(User user, Maintenance maintenance) {
        if (maintenance == null || maintenance.getEquipment() == null) return false;
        if (isSystemAdmin(user)) return true;
        if (maintenance.getTechnician() != null && maintenance.getTechnician().getId().equals(user.getId())) return true;
        if (user.getRole() == Role.LAB_MANAGER && user.getLaboratory() != null && maintenance.getEquipment().getLaboratory() != null) {
            return user.getLaboratory().getId().equals(maintenance.getEquipment().getLaboratory().getId());
        }
        if (user.getRole() == Role.LAB_TECHNICIAN) return isSameInstitution(user, maintenance.getEquipment());
        return canManageEquipment(user, maintenance.getEquipment());
    }

    public boolean canViewMaintenance(User user, Maintenance maintenance) {
        if (maintenance == null) return false;
        if (isSystemAdmin(user)) return true;
        if (maintenance.getTechnician() != null && maintenance.getTechnician().getId().equals(user.getId())) return true;
        if (user.getRole() == Role.LAB_MANAGER && user.getLaboratory() != null && maintenance.getEquipment() != null && maintenance.getEquipment().getLaboratory() != null) {
            return user.getLaboratory().getId().equals(maintenance.getEquipment().getLaboratory().getId());
        }
        return isSameInstitution(user, maintenance.getEquipment());
    }
}
