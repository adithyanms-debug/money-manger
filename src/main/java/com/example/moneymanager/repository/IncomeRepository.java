package com.example.moneymanager.repository;

import com.example.moneymanager.entity.ExpenseEntity;
import com.example.moneymanager.entity.IncomeEntity;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface IncomeRepository extends JpaRepository<IncomeEntity, Long> {
    List<IncomeEntity> findByProfileIdOrderedByDateDesc(Long profileId);

    List<ExpenseEntity> findTop5ByProfileIdOrderByDateDesc(Long profileId);

    @Query("SELECT SUM(e.amount) FROM IncomeEntity e WHERE i.profile.id = :profileId")
    BigDecimal findTotalExpenseByProfile(@Param("profileId") Long profileId);

    //@Query("SELECT * FROM  tbl_incomes where profile_id = ?1 and date between ?2 and ?3 and name like %?4% ")
    List<IncomeEntity> findByProfileIdAndDateBetweenAndNameContainingIgnoreCase(
            Long profileId,
            LocalDate startDate,
            LocalDate rndDate,
            String keyWord,
            Sort sort
    );

    List<IncomeEntity> findByProfileIdAndDateBetween(Long profileId, LocalDate startDate, Long endDate);
}
