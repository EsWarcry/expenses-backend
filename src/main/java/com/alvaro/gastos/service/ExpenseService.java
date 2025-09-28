package com.alvaro.gastos.service;

import com.alvaro.gastos.dto.ApiResponse;
import com.alvaro.gastos.dto.ExpenseDTO;
import java.util.List;

public interface ExpenseService {

    ApiResponse<ExpenseDTO> createExpense(ExpenseDTO expenseDTO);

    ApiResponse<ExpenseDTO> getExpenseById(Long id);

    ApiResponse<List<ExpenseDTO>> getExpensesByUserId(Long userId);

    ApiResponse<List<ExpenseDTO>> getAllExpenses();

    ApiResponse<ExpenseDTO> updateExpense(Long id, ExpenseDTO expenseDTO);

    ApiResponse<Void> deleteExpense(Long id);

    ApiResponse<List<ExpenseDTO>> getExpensesByKeycloakId(String keycloakId);

}
