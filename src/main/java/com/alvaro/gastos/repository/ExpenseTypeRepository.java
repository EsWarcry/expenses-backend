package com.alvaro.gastos.repository;

import com.alvaro.gastos.entities.ExpenseType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ExpenseTypeRepository extends JpaRepository<ExpenseType, Long> {

    Optional<ExpenseType> findByName(String name);

    List<ExpenseType> findByNameContainingIgnoreCase(String name);
}
