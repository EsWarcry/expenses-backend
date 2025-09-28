package com.alvaro.gastos.repository;

import com.alvaro.gastos.entities.Expense;
import com.alvaro.gastos.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    List<Expense> findByUser_Id(Long userId);
}
