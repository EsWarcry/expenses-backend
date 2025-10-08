package com.alvaro.gastos.repository;

import com.alvaro.gastos.entities.Expense;
import com.alvaro.gastos.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    List<Expense> findByUser_Id(Long userId);

    @Query("SELECT e FROM Expense e " +  //Si no se dejamos el espacio entre e y las " final, genera error. Ojo.
            "WHERE e.user.keycloakId = :keycloakId " +
            "AND MONTH(e.expenseDate) = :month " +
            "AND YEAR(e.expenseDate) = YEAR(CURRENT_DATE)"
    )
    List<Expense> findByUserAndMonth(@Param("keycloakId") String keycloakId, @Param("month") int month);

    @Query("SELECT e FROM Expense e " +
            "WHERE e.user.keycloakId = :keycloakId " +
            "AND e.expenseDate BETWEEN :startDate AND :endDate"
    )
    List<Expense> findByUserAndDateRange(@Param("keycloakId") String keycloakId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}
