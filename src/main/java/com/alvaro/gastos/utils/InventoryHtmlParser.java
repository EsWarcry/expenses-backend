package com.alvaro.gastos.utils;

import com.alvaro.gastos.dto.InventoryItemDTO;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class InventoryHtmlParser {

    public InventoryItemDTO parserHtml(String html, String ci, String serialNumber, String userName){

        InventoryItemDTO dto = new InventoryItemDTO();

        if (ci == null || ci.isEmpty()){
            ci = extractCi(html);
        }
        if (serialNumber == null || serialNumber.isEmpty()){
            serialNumber = extractSerial(html);
        }
        dto.setUserName(userName);
        dto.setQueryDate(LocalDate.now());
        dto.setCi(ci);
        dto.setType(extractType(html));
        dto.setBrand(extractBrand(html));
        dto.setModel(extractModel(html));
        dto.setSerialNumber(serialNumber);

        return dto;
    }


    private String extractType(String html){
        Pattern pattern = Pattern.compile("TIPO.*?:\\s*</FONT><B><FONT[^>]*>([^<]+)</FONT>", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(html);

        if (matcher.find()){
            String raw = matcher.group(1).trim().toLowerCase();

            if (raw.contains("pc")) return "PC";
            if (raw.contains("monitor")) return "MONITOR";
            if (raw.contains("impresora")) return "IMPRESORA";
            if (raw.contains("portatil")) return "PORTATIL";
            return "OTRO";
        }

        return "DESCONOCIDO";
    }

    private String extractBrand(String html){
        Pattern pattern = Pattern.compile("MODELO\\s*:\\s*</FONT><B><FONT[^>]*>([^<]+)</FONT>", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(html);
        if (matcher.find()){
            String raw = matcher.group(1).trim();
            String[] parts = raw.split("-", 2);
            if (parts.length > 1){
                String brandModel = parts[1].trim();
                String[] tokens = brandModel.split(" ", 2);
                return tokens[0];
            }
        }
        return "DESCONOCIDO";
    }

    private String extractModel(String html){
        Pattern pattern = Pattern.compile("MODELO\\s*:\\s*</FONT><B><FONT[^>]*>([^<]+)</FONT>", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(html);
        if (matcher.find()) {
            String raw = matcher.group(1).trim(); // Ej: "15818841 - LENOVO THINKCENTRE M900"
            String[] parts = raw.split("-", 2);
            if (parts.length > 1) {
                String brandModel = parts[1].trim();
                String[] tokens = brandModel.split(" ", 2);
                if (tokens.length > 1) {
                    return tokens[1]; // THINKCENTRE M900
                }
            }
        }
        return "DESCONOCIDO";
    }

    private String extractSerial(String html) {
        Pattern pattern = Pattern.compile("N[º°]\\s*SERIE\\s*:\\s*</FONT><B><FONT[^>]*>([^<]+)</FONT>", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(html);
        if (matcher.find()) {
            return matcher.group(1).trim();
        }
        return "DESCONOCIDO";
    }

    private String extractCi(String html) {
        // Busca "ELEMENTO DE HW :" y captura el número que viene después
        Pattern pattern = Pattern.compile(
                "ELEMENTO\\s*DE\\s*HW\\s*:\\s*</FONT>.*?<FONT[^>]*>(?:&nbsp;|\\s)*([0-9A-Z]+)",
                Pattern.CASE_INSENSITIVE | Pattern.DOTALL
        );
        Matcher matcher = pattern.matcher(html);
        if (matcher.find()) {
            return matcher.group(1).trim();
        }
        return "DESCONOCIDO";
    }

}
