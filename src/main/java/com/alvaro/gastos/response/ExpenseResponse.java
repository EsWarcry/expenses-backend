package com.alvaro.gastos.response;

import com.alvaro.gastos.dto.ExpenseDTO;
import lombok.Data;

import java.util.List;

@Data
public class ExpenseResponse {

    private List<ExpenseDTO> expenses;
    private double totalAmount;

    public ExpenseResponse(List<ExpenseDTO> expenses, double totalAmount) {
        this.expenses = expenses;
        this.totalAmount = totalAmount;
    }
}
