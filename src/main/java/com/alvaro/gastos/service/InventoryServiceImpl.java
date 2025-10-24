package com.alvaro.gastos.service;

import com.alvaro.gastos.dto.ApiResponse;
import com.alvaro.gastos.dto.InventoryItemDTO;
import com.alvaro.gastos.dto.InventoryQureyDTO;
import com.alvaro.gastos.utils.InventoryHtmlParser;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService{

    private final RestTemplate restTemplate;
    private final InventoryHtmlParser htmlParser;

    @Value("${inventory.api.url}")
    private String inventoryApiUrl;
    @Override
    public ApiResponse<List<InventoryItemDTO>> queryInventory(List<InventoryQureyDTO> queries) {

        //Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        //String userName = authentication.getName();
        String userName = "Alvaro Escobar";

        List<InventoryItemDTO> items = new ArrayList<>();

        for (InventoryQureyDTO query : queries){
            try {
                String url;
                Date fecha = new Date();
                if (query.getCi() != null && !query.getCi().isBlank()){
                    url = inventoryApiUrl+"HW="+query.getCi()+"&ssl_redireccionado=true"+"&idsesion="+fecha.getTime();
                } else if (query.getSerialNumber() != null && !query.getSerialNumber().isBlank()){
                    url = inventoryApiUrl+"SERIE="+query.getSerialNumber()+"&ssl_redireccionado=true"+"&idsesion="+fecha.getTime();
                } else {
                    continue;
                }

                String html = restTemplate.getForObject(url, String.class);
                InventoryItemDTO item = htmlParser.parserHtml(html, query.getCi(), query.getSerialNumber(), userName);
                items.add(item);
                //String type = doc.select("112310")
            } catch (Exception e){
                InventoryItemDTO errorItem = new InventoryItemDTO();
                errorItem.setCi(query.getCi());
                errorItem.setSerialNumber(query.getSerialNumber());
                errorItem.setType("ERROR");
                errorItem.setBrand("NO DISPONIBLE");
                errorItem.setModel("NO DISPONIBLE");
                errorItem.setUserName(userName);
                errorItem.setQueryDate(LocalDate.now());
                items.add(errorItem);
            }
        }


        return new ApiResponse<>("success","Lista de consulta exitosa", items);
    }

    /*public byte[] queryInventoryPrueba(List<InventoryQureyDTO> queries) {

        //Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        //String userName = authentication.getName();
        String userName = "Alvaro Escobar";
        System.out.println("Consulta realizada por el usuario: "+userName);

        List<InventoryItemDTO> items = new ArrayList<>();

        for (InventoryQureyDTO query : queries){
            try {
                String url;
                Date fecha = new Date();
                if (query.getCi() != null && !query.getCi().isBlank()){
                    url = inventoryApiUrl+"HW="+query.getCi()+"&ssl_redireccionado=true"+"&idsesion="+fecha.getTime();
                } else if (query.getSerialNumber() != null && !query.getSerialNumber().isBlank()){
                    url = inventoryApiUrl+"SERIE="+query.getSerialNumber()+"&ssl_redireccionado=true"+"&idsesion="+fecha.getTime();
                } else {
                    continue;
                }

                String html = restTemplate.getForObject(url, String.class);
                InventoryItemDTO item = htmlParser.parserHtml(html, query.getCi(), query.getSerialNumber(), userName);
                items.add(item);

                //String type = doc.select("112310")
            } catch (Exception e){
                InventoryItemDTO errorItem = new InventoryItemDTO();
                errorItem.setCi(query.getCi());
                errorItem.setSerialNumber(query.getSerialNumber());
                errorItem.setType("ERROR");
                errorItem.setBrand("NO DISPONIBLE");
                errorItem.setModel("NO DISPONIBLE");
                errorItem.setUserName(userName);
                errorItem.setQueryDate(LocalDate.now());
                items.add(errorItem);
            }
        }


        return generateExcel(items);
    }*/

    @Override
    public byte[] exporterInventoryToExcel(List<InventoryItemDTO> items) {

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Inventario");

            String[] headers = {"TIPO", "MARCA", "MODELO", "Nº SERIE", "CI", "FECHA", "TI"};

            // Encabezado
            Row headerRow = sheet.createRow(0);
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            CellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setFont(headerFont);

            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            int rowNum = 1;
            for (InventoryItemDTO item : items) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(item.getType());
                row.createCell(1).setCellValue(item.getBrand());
                row.createCell(2).setCellValue(item.getModel());
                row.createCell(3).setCellValue(item.getSerialNumber());
                row.createCell(4).setCellValue(item.getCi());
                row.createCell(5).setCellValue(item.getQueryDate().toString());
                row.createCell(6).setCellValue(item.getUserName());
            }

            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            return out.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Error generando Excel de inventario");
        }
    }

}
