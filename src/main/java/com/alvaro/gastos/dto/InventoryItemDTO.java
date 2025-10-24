package com.alvaro.gastos.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InventoryItemDTO {

    private String type;
    private String brand;
    private String model;
    private String serialNumber;
    private String ci;
    private LocalDate queryDate;
    private String userName;
    private boolean found;

}
