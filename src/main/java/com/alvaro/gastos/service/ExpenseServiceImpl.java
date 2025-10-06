package com.alvaro.gastos.service;

import com.alvaro.gastos.dto.ApiResponse;
import com.alvaro.gastos.dto.ExpenseDTO;
import com.alvaro.gastos.entities.Expense;
import com.alvaro.gastos.entities.ExpenseType;
import com.alvaro.gastos.entities.User;
import com.alvaro.gastos.repository.ExpenseRepository;
import com.alvaro.gastos.repository.ExpenseTypeRepository;
import com.alvaro.gastos.repository.UserRepository;
import com.alvaro.gastos.response.ExpenseResponse;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ExpenseServiceImpl implements ExpenseService{

    private static final Logger logger = LoggerFactory.getLogger(ExpenseServiceImpl.class);
    private final ExpenseRepository expenseRepository;
    private final UserRepository userRepository;
    private final ExpenseTypeRepository expenseTypeRepository;

    public ExpenseServiceImpl(ExpenseRepository expenseRepository, UserRepository userRepository, ExpenseTypeRepository expenseTypeRepository) {
        this.expenseRepository = expenseRepository;
        this.userRepository = userRepository;
        this.expenseTypeRepository = expenseTypeRepository;
    }


    /**
     * Crea un nuevo registro de gasto en la base de datos.
     * Realiza las validaciones necesarias para User y ExpenseType antes de guardar.
     *
     * @param expenseDTO El DTO del gasto con los datos a guardar.
     * @return Un ApiResponse que contiene el ExpenseDTO creado o un mensaje de error.
     */
    @Transactional
    @Override
    public ApiResponse<ExpenseDTO> createExpense(ExpenseDTO expenseDTO) {

        Optional<User> userOptional = userRepository.findById(expenseDTO.getUserId());
        if (userOptional.isEmpty()){
            logger.warn("Intento de crear gasto para User ID no encontrado: {}", expenseDTO.getUserId());
            return new ApiResponse<>("error", "Usuario no encontrado con ID: " + expenseDTO.getUserId(), null);
        }
        User user = userOptional.get();

        Optional<ExpenseType> expenseTypeOptional = expenseTypeRepository.findById(expenseDTO.getExpenseTypeId());
        if (expenseTypeOptional.isEmpty()){
            logger.warn("Intento de crear gasto con ExpenseType ID no encocntrado: {}", expenseDTO.getExpenseTypeId());
            return new ApiResponse<>("error", "Tipo de gasto no encontrado", null);
        }

        ExpenseType expenseType = expenseTypeOptional.get();

        Expense expense = new Expense();

        expense.setUser(user);
        expense.setExpenseType(expenseType);
        expense.setExpenseDate(expenseDTO.getExpenseDate());
        expense.setAmount(expenseDTO.getAmount());
        expense.setMileage(expenseDTO.getMileage());
        expense.setDescription(expenseDTO.getDescription());
        expense.setImageUrl(expenseDTO.getImageUrl());
        //CreatedAt se genera automaticamente por @CreationTimestamp

        try {
            Expense savedExpense = expenseRepository.save(expense);
            logger.info("Gasto creado y guardado en la BD con ID: {}", savedExpense.getId());

            return new ApiResponse<>("Gasto creado exitosamente.", convertToDto(savedExpense));
        } catch (Exception e){
            logger.error("Error al crear el gasto en la BD: {}", e.getMessage(), e);
            return new ApiResponse<>("error", "Error interno al crear el gasto.", null);
        }

    }

    @Transactional(readOnly = true)
    @Override
    public ApiResponse<ExpenseDTO> getExpenseById(Long id) {

        Optional<Expense> expenseOptional = expenseRepository.findById(id);

        if (expenseOptional.isPresent()){
            logger.info("Gasto encontrado con ID: {}", id);
            return new ApiResponse<>("Gasto encontrado.", convertToDto(expenseOptional.get()));
        } else {
            logger.warn("Gasto no encontrado", id);
            return new ApiResponse<>( "error", "Gasto no encontrado", null);
        }
    }

    @Override
    public ApiResponse<List<ExpenseDTO>> getExpensesByUserId(Long userId) {
        return null;
    }

    @Transactional(readOnly = true)
    public ApiResponse<ExpenseResponse> getExpensesByKeycloakId(String keycloakId) {

        Optional<User> userOpt = userRepository.findByKeycloakId(keycloakId);

        if (userOpt.isEmpty()) {
            logger.warn("Usuario no encontrado con keycloakId {}", keycloakId);
            return new ApiResponse<>("error", "Usuario no encontrado", null);
        }

        User user = userOpt.get();

        List<Expense> expenses = expenseRepository.findByUser_Id(user.getId());

        List<ExpenseDTO> expenseDTOS = expenses.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());

        double total = expenseDTOS.stream()
                        .mapToDouble(ExpenseDTO::getAmount)
                                .sum();

        ExpenseResponse responseDTO = new ExpenseResponse(expenseDTOS, total);

        logger.info("Recuperados {} gastos para el usuario ID {}", expenseDTOS.size(), keycloakId);
        return new ApiResponse<>("success","Gastos recuperados exitosamente para el usuario con ID: " + keycloakId, responseDTO);

    }

    @Override
    public ApiResponse<ExpenseResponse> getExpensesByUserAndMonth(String keycloakId, int month) {

        Optional<User> userOptional = userRepository.findByKeycloakId(keycloakId);
        if (userOptional.isEmpty()){
            return new ApiResponse<>("error", "Usuario no encontrado", null);
        }

        User userFind = userOptional.get();

        List<Expense> expenseList = expenseRepository.findByUserAndMonth(keycloakId, month);

        List<ExpenseDTO> expenseDTOS = expenseList.stream()
                                        .map(this::convertToDto)
                                        .collect(Collectors.toList());

        Double total = expenseList.stream()
                .mapToDouble(Expense::getAmount)
                .sum();

        ExpenseResponse expenseResponse = new ExpenseResponse(expenseDTOS, total);
        return new ApiResponse<ExpenseResponse>("success", "Gastos por usuario recuperados exitosamente!!!", expenseResponse);
    }

    @Transactional(readOnly = true)
    @Override
    public ApiResponse<List<ExpenseDTO>> getAllExpenses() {

        List<Expense> expenses = expenseRepository.findAll();
        List<ExpenseDTO> expenseDTOS = expenses.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());

        logger.info("Recuperados {} gastos", expenseDTOS.size());
        return new ApiResponse<>("Gastos recuperados exitosamente.", expenseDTOS);
    }


    @Transactional
    @Override
    public ApiResponse<ExpenseDTO> updateExpense(Long id, ExpenseDTO expenseDTO) {

        Optional<Expense> expenseOptional = expenseRepository.findById(id);

        if (expenseOptional.isEmpty()){
            logger.warn("Gasto no encontrado con el ID {}", id);
            return new ApiResponse<>("Gasto no encontrado", null);
        }

        Expense expense = expenseOptional.get();
        // Verificar y actualizar User si el ID es diferente
        if (!expense.getUser().getId().equals(expenseDTO.getUserId())){
            Optional<User> userOptional = userRepository.findById(expenseDTO.getUserId());
            if (userOptional.isEmpty()){
                logger.warn("Intento de actualizar gasto con nuevo User ID no encontrado:", expenseDTO.getUserId());
                return new ApiResponse<>("error","Usuario no encontrado ", null);
            }
            expense.setUser(userOptional.get());
            logger.warn("Usuario actualizado con el ID {}", expenseDTO.getUserId());
            //return new ApiResponse<>("Usuario no encontrado", null);
        }

        if (!expense.getExpenseType().getId().equals(expenseDTO.getExpenseTypeId())){

            Optional<ExpenseType> expenseTypeOptional = expenseTypeRepository.findById(expenseDTO.getExpenseTypeId());
            if (expenseTypeOptional.isEmpty()){
                logger.warn("Intento de actualizar tipo de gasto con el ID {} no encontrado.", expenseDTO.getExpenseTypeId());
                return new ApiResponse<>("error", "Tipo de gasto no encontrado", null);
            }
            expense.setExpenseType(expenseTypeOptional.get());
        }

        // Actualizar los campos escalares
        expense.setExpenseDate(expenseDTO.getExpenseDate());
        expense.setAmount(expenseDTO.getAmount());
        expense.setMileage(expenseDTO.getMileage());
        expense.setDescription(expenseDTO.getDescription());
        expense.setImageUrl(expenseDTO.getImageUrl());
        // createdAt no se actualiza, ya que es @CreationTimestamp

        try {
            Expense updateExpense = expenseRepository.save(expense);
            logger.info("Gasto con ID {} actualizado exitosamente", updateExpense.getId());
            return new ApiResponse<>("Gasto actualizado exitosamente", convertToDto(updateExpense));
        }catch (Exception e){
            logger.warn("Error al actualizar el gasto con ID {}", id, e.getMessage());
            return new ApiResponse<>("error", "Error interno al actualizar el gasto", null);
        }

    }

    @Transactional
    @Override
    public ApiResponse<Void> deleteExpense(Long id) {

        if (expenseRepository.existsById(id)){
            try {
                expenseRepository.deleteById(id);
                logger.info("Gasto con ID {} eliminado existosamente.", id);
                return new ApiResponse<>("Gasto eliminado exitosamente.", null);
            }catch (Exception e){
                logger.error("Error al eliminar el gasto con ID {}", id, e.getMessage(), e);
                return new ApiResponse<>("error", "Error interno al eliminar el gasto", null);
            }
        } else {
            logger.warn("Gasto no encontrado para eliminar con ID {}", id);
            return new ApiResponse<>("error", "Gasto no encontrado con ID " +id, null);
        }
    }

    private ExpenseDTO convertToDto(Expense expense) {
        ExpenseDTO expenseDTO = new ExpenseDTO();
        expenseDTO.setId(expense.getId());
        expenseDTO.setUserId(expense.getUser().getId()); // Mapea el ID del usuario
        expenseDTO.setExpenseTypeId(expense.getExpenseType().getId()); // Mapea el ID del tipo de gasto
        expenseDTO.setExpenseDate(expense.getExpenseDate());
        expenseDTO.setAmount(expense.getAmount());
        expenseDTO.setMileage(expense.getMileage());
        expenseDTO.setDescription(expense.getDescription());
        expenseDTO.setImageUrl(expense.getImageUrl());
        expenseDTO.setCreatedAt(expense.getCreatedAt());
        expenseDTO.setExpenseTypeName(expense.getExpenseType().getName());
        return expenseDTO;
    }

    public byte[] exportExpensesToExcel(String keycloakId, int month){

        List<Expense> expenses = expenseRepository.findByUserAndMonth(keycloakId, month);

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Gastos");

            // ========= EStilos =========

            Font boldFont = workbook.createFont();
            boldFont.setBold(true);

            CellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setFont(boldFont);

            CellStyle totalStyle = workbook.createCellStyle();
            totalStyle.setFont(boldFont);

            //Estiloo para fechas

            CreationHelper createHelper = workbook.getCreationHelper();
            CellStyle dateStyle = workbook.createCellStyle();
            dateStyle.setDataFormat(createHelper.createDataFormat().getFormat("dd/MM/yyyy"));

            // Encabezado

            Row header = sheet.createRow(0);
            String[] columns = {"Tipo de Gasto", "Fecha", "Monto €", "KM", "Descripción"};
            for (int i=0; i < columns.length; i++){
                Cell cell = header.createCell(i);
                cell.setCellValue(columns[i]);
                cell.setCellStyle(headerStyle);
            }

            //===== Filas

            int rowNum = 1;
            for (Expense exp : expenses){
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(exp.getExpenseType().getName());

                //Damos formato a la fecha
                Cell dateCell = row.createCell(1);
                if (exp.getExpenseDate() != null) {
                    LocalDate date = exp.getExpenseDate();
                    dateCell.setCellValue(Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant()));
                    dateCell.setCellStyle(dateStyle);
                }

                row.createCell(2).setCellValue(exp.getAmount());
                row.createCell(3).setCellValue(exp.getMileage() != null ? exp.getMileage() : 0);
                row.createCell(4).setCellValue(exp.getDescription() != null ? exp.getDescription() : "");
            }

            Double total = expenses.stream().mapToDouble(Expense::getAmount).sum();

            Row rowTotal = sheet.createRow(rowNum + 1);
            rowTotal.createCell(1).setCellValue("Total €:");
            Cell totalCell = rowTotal.createCell(2);
            totalCell.setCellValue(total);
            totalCell.setCellStyle(totalStyle);

            // ==== Ajustar el ancho de las celldas

            for (int i = 0; i< columns.length; i++){
                sheet.autoSizeColumn(i);
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            return out.toByteArray();
        }catch (IOException e){
            throw new RuntimeException("Error generando Excel de gastos");
        }
    }
}
