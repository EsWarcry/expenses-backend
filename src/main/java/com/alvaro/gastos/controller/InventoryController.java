package com.alvaro.gastos.controller;

import com.alvaro.gastos.dto.ApiResponse;
import com.alvaro.gastos.dto.InventoryItemDTO;
import com.alvaro.gastos.dto.InventoryQureyDTO;
import com.alvaro.gastos.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/inventory")
@CrossOrigin(origins = {"http://localhost:4200"})
public class InventoryController {

    private final InventoryService inventoryService;

    @PostMapping("/query")
    public ResponseEntity<ApiResponse<List<InventoryItemDTO>>> queryInventory(@RequestBody List<InventoryQureyDTO> queries){

        ApiResponse<List<InventoryItemDTO>> response= inventoryService.queryInventory(queries);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/export")
    public ResponseEntity<byte[]> exportInventoryToExcel(@RequestBody List<InventoryItemDTO> item){

        byte[] excelFile = inventoryService.exporterInventoryToExcel(item);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename= RETIRADA_HW.xlsx")
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.seheet"))
                .body(excelFile);

    }

}
