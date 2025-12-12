package com.relatech.warehouse_management_system.goodsIn.entity.repository;

import com.relatech.warehouse_management_system.goodsIn.entity.Grn;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface GrnRepository extends JpaRepository<Grn, Long> {

    @Query("SELECT g FROM Grn g WHERE LOWER(g.supplier) LIKE LOWER(CONCAT('%', :term, '%'))")
    List<Grn> searchByTerm(@Param("term") String term);


    /**
     * (Optional) paginated search example.
     */
    @Query("SELECT g FROM Grn g WHERE LOWER(g.supplier) LIKE LOWER(CONCAT('%', :term, '%'))")
    Page<Grn> searchByTerm(@Param("term") String term, Pageable pageable);

    @Query("SELECT g FROM Grn g LEFT JOIN FETCH g.items WHERE g.id = :id")
    Optional<Grn> findByIdWithItems(@Param("id") Long id);


    Optional<Grn> findByCode(@NotBlank(message = "The code cannot be empty or null.") String code);
}

