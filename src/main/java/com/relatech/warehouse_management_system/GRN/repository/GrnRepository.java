package com.relatech.warehouse_management_system.GRN.repository;

import com.relatech.warehouse_management_system.GRN.entity.GRN;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface GrnRepository extends JpaRepository<GRN, String> {

    @Query("SELECT g FROM GRN g WHERE LOWER(g.supplier) LIKE LOWER(CONCAT('%', :term, '%')) " +
            "OR LOWER(g.id) LIKE LOWER(CONCAT('%', :term, '%'))")
    List<GRN> searchByTerm(@Param("term") String term);

    /**
     * (Optional) paginated search example.
     */
    @Query("SELECT g FROM GRN g WHERE LOWER(g.supplier) LIKE LOWER(CONCAT('%', :term, '%')) " +
            "OR LOWER(g.id) LIKE LOWER(CONCAT('%', :term, '%'))")
    Page<GRN> searchByTerm(@Param("term") String term, Pageable pageable);
}

