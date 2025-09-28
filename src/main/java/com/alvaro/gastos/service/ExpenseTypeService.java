package com.alvaro.gastos.service;

import com.alvaro.gastos.dto.ApiResponse;
import com.alvaro.gastos.entities.ExpenseType;

import java.util.List;

public interface ExpenseTypeService {

    ApiResponse<ExpenseType> createExpenseType(ExpenseType expenseType);

    ApiResponse<ExpenseType> getExpenseTypeById(Long id);

    ApiResponse<ExpenseType> getExpenseTypeByName(String name);

    ApiResponse<List<ExpenseType>> getAllExpenseTypes();

    ApiResponse<ExpenseType> updateExpenseType(ExpenseType expenseType, Long id);

    ApiResponse<Void> deleteExpenseType(Long id);

    ApiResponse<List<ExpenseType>> searchExpensesType(String name);

}
