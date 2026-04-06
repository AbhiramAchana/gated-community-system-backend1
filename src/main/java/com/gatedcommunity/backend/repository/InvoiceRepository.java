package com.gatedcommunity.backend.repository;

import com.gatedcommunity.backend.entity.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    List<Invoice> findByPropertyId(Long propertyId);

    List<Invoice> findByPropertyOwnerIdOrPropertyTenantId(Long ownerId, Long tenantId);

    List<Invoice> findByStatus(String status);
}
