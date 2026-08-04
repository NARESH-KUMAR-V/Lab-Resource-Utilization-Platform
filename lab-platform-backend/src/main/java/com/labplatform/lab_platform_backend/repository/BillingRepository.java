package com.labplatform.lab_platform_backend.repository;

import com.labplatform.lab_platform_backend.entity.Billing;
import com.labplatform.lab_platform_backend.entity.BillingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BillingRepository extends JpaRepository<Billing, Long> {

    List<Billing> findByUserEmail(String email);

    List<Billing> findByInstitutionId(Long institutionId);

    @Query("SELECT b FROM Billing b WHERE b.institution.id = :institutionId OR b.owningInstitution.id = :institutionId")
    List<Billing> findByInstitutionOrOwningInstitutionId(@Param("institutionId") Long institutionId);

    @Query("SELECT b FROM Billing b WHERE b.equipment.laboratory.id = :laboratoryId")
    List<Billing> findByEquipmentLaboratoryId(@Param("laboratoryId") Long laboratoryId);

    @Query("SELECT b FROM Billing b WHERE b.department = :department OR b.equipment.laboratory.department = :department")
    List<Billing> findByDepartmentName(@Param("department") String department);

    List<Billing> findByDepartment(String department);

    Optional<Billing> findByBookingId(Long bookingId);

    Optional<Billing> findBySharingRequestId(Long sharingRequestId);

    List<Billing> findByBillingStatus(BillingStatus status);

    @Query("SELECT COALESCE(SUM(b.totalAmount), 0) FROM Billing b")
    Double getTotalPlatformBilledAmount();

    @Query("SELECT COALESCE(SUM(b.totalAmount), 0) FROM Billing b WHERE b.institution.id = :institutionId")
    Double getTotalBilledAmountByInstitution(@Param("institutionId") Long institutionId);

    @Query("SELECT COALESCE(SUM(b.totalAmount), 0) FROM Billing b WHERE b.institution.id = :institutionId AND b.sharingRequest IS NOT NULL")
    Double getOutgoingSharingCostByInstitution(@Param("institutionId") Long institutionId);

    @Query("SELECT COALESCE(SUM(b.totalAmount), 0) FROM Billing b WHERE b.owningInstitution.id = :institutionId AND b.sharingRequest IS NOT NULL")
    Double getResourceSharingValueByInstitution(@Param("institutionId") Long institutionId);

    @Query("SELECT COALESCE(SUM(b.totalAmount), 0) FROM Billing b WHERE b.equipment.laboratory.id = :laboratoryId AND b.sharingRequest IS NOT NULL")
    Double getOutgoingSharingCostByLaboratory(@Param("laboratoryId") Long laboratoryId);

    @Query("SELECT COALESCE(SUM(b.totalAmount), 0) FROM Billing b WHERE b.equipment.laboratory.id = :laboratoryId AND b.sharingRequest IS NOT NULL")
    Double getResourceSharingValueByLaboratory(@Param("laboratoryId") Long laboratoryId);

    @Query("SELECT COALESCE(SUM(b.totalAmount), 0) FROM Billing b WHERE b.user.email = :email")
    Double getTotalBilledAmountByUser(@Param("email") String email);
}
