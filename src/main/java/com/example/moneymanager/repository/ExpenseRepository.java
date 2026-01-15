package com.example.moneymanager.repository;

import com.example.moneymanager.entity.ExpenseEntity;
import org.springframework.boot.data.autoconfigure.web.DataWebProperties;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface ExpenseRepository extends JpaRepository<ExpenseEntity, Long> {

    List<ExpenseEntity> findByProfileIdOrderedByDateDesc(Long profileId);

    List<ExpenseEntity> findTop5ByProfileIdOrderByDateDesc(Long profileId);

    @Query("SELECT SUM(e.amount) FROM ExpenseEntity e WHERE i.profile.id = :profileId")
    BigDecimal findTotalExpenseByProfile(@Param("profileId") Long profileId);

    //@Query("SELECT * FROM  tbl_expenses where profile_id = ?1 and date between ?2 and ?3 and name like %?4% ")
    List<ExpenseEntity> findByProfileIdAndDateBetweenAndNameContainingIgnoreCase(
            Long profileId,
            LocalDate startDate,
            LocalDate rndDate,
            String keyWord,
            Sort sort
    );

    List<ExpenseEntity> findByProfileIdAndDateBetween(Long profileId, LocalDate startDate, Long endDate);
}
