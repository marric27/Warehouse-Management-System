package com.relatech.warehouse_management_system.GRN.repository;

import com.relatech.warehouse_management_system.GRN.entity.GRN;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface GrnRepository extends JpaRepository<GRN, Long> {

    @Query("SELECT g FROM GRN g WHERE LOWER(g.supplier) LIKE LOWER(CONCAT('%', :term, '%'))")
    List<GRN> searchByTerm(@Param("term") String term);


    /**
     * (Optional) paginated search example.
     */
    @Query("SELECT g FROM GRN g WHERE LOWER(g.supplier) LIKE LOWER(CONCAT('%', :term, '%'))")
    Page<GRN> searchByTerm(@Param("term") String term, Pageable pageable);

    @Query("SELECT g FROM GRN g LEFT JOIN FETCH g.items WHERE g.id = :id")
    Optional<GRN> findByIdWithItems(@Param("id") Long id);
}

