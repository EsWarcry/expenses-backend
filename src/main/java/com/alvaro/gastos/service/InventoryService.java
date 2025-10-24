package com.alvaro.gastos.service;

import com.alvaro.gastos.dto.ApiResponse;
import com.alvaro.gastos.dto.InventoryItemDTO;
import com.alvaro.gastos.dto.InventoryQureyDTO;

import java.util.List;

public interface InventoryService {

    ApiResponse<List<InventoryItemDTO>> queryInventory(List<InventoryQureyDTO> queries);

    byte[] exporterInventoryToExcel(List<InventoryItemDTO> inventoryItemDTOS);
}
