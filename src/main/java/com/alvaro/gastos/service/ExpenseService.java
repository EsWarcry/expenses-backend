package com.alvaro.gastos.service;

import com.alvaro.gastos.dto.ApiResponse;
import com.alvaro.gastos.dto.ExpenseDTO;
import com.alvaro.gastos.response.ExpenseResponse;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

public interface ExpenseService {

    ApiResponse<ExpenseDTO> createExpense(ExpenseDTO expenseDTO);

    ApiResponse<ExpenseDTO> getExpenseById(Long id);

    ApiResponse<List<ExpenseDTO>> getExpensesByUserId(Long userId);

    ApiResponse<List<ExpenseDTO>> getAllExpenses();

    ApiResponse<ExpenseDTO> updateExpense(Long id, ExpenseDTO expenseDTO);

    ApiResponse<Void> deleteExpense(Long id);

    ApiResponse<ExpenseResponse> getExpensesByKeycloakId(String keycloakId);
    ApiResponse<ExpenseResponse> getExpensesByUserAndMonth(String keycloakId, int month);
    ApiResponse<ExpenseResponse> getExpensesByUserAndDateRange(String keycloakId, LocalDate startDate, LocalDate endDate);

    byte[] exportExpensesToExcel(List<ExpenseDTO> expenses);

}
