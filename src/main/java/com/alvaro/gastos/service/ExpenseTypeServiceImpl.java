package com.alvaro.gastos.service;

import com.alvaro.gastos.dto.ApiResponse;
import com.alvaro.gastos.entities.ExpenseType;
import com.alvaro.gastos.repository.ExpenseTypeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ExpenseTypeServiceImpl implements ExpenseTypeService{

    private static final Logger logger = LoggerFactory.getLogger(ExpenseTypeServiceImpl.class);

    private final ExpenseTypeRepository expenseTypeRepository;

    public ExpenseTypeServiceImpl(ExpenseTypeRepository expenseTypeRepository) {
        this.expenseTypeRepository = expenseTypeRepository;
    }


    @Transactional
    @Override
    public ApiResponse<ExpenseType> createExpenseType(ExpenseType expenseType) {

        if (expenseTypeRepository.findByName(expenseType.getName()).isPresent()){
            logger.warn("Intento de crear tipo de gasto duplicado: {}", expenseType.getName());
            return new ApiResponse<>("error", "El tipo de gasto con el nombre '"+expenseType.getName()+"'ya existe.", null);
        }
        try {
            ExpenseType savedExpenseType = expenseTypeRepository.save(expenseType);
            logger.info("Tipo de gasto creado y guardado en la BD: {}", savedExpenseType.getName());
            return new ApiResponse<>("Tipo de gasto creado exitosamente.", savedExpenseType);
        } catch (Exception e) {
            logger.error("Error al crear el tipo de gasto: {}", e.getMessage(), e);
            return new ApiResponse<>("error", "Error interno al crear el tipo de gasto.", null);
        }
    }

    @Transactional(readOnly = true)
    @Override
    public ApiResponse<ExpenseType> getExpenseTypeById(Long id) {

        Optional<ExpenseType> expenseTypeOptional = expenseTypeRepository.findById(id);

        if (expenseTypeOptional.isPresent()){
            return new ApiResponse<>("tipo de gasto encontrado", expenseTypeOptional.get());
        } else {
            return new ApiResponse<>("error", "Tipo de gasto no encontrado", null);
        }
    }

    @Transactional(readOnly = true)
    @Override
    public ApiResponse<ExpenseType> getExpenseTypeByName(String name) {

        Optional<ExpenseType> expenseTypeOptional = expenseTypeRepository.findByName(name);

        if (expenseTypeOptional.isPresent()){
            logger.info("El tipo de gasto encontrado con nombre: {}", name);
            return new ApiResponse<>("Tipo de gasto encontrado.", expenseTypeOptional.get());
        } else {
            logger.warn("Tipo de gasto no encontrado con nombre: {}", name);
            return new ApiResponse<>("error", "Tipo de gasto no encontrado con nombre: "+name, null);
        }
    }

    @Override
    public ApiResponse<List<ExpenseType>> getAllExpenseTypes() {

        List<ExpenseType> expenseTypeList = expenseTypeRepository.findAll();

        if (expenseTypeList.isEmpty()){
            logger.warn("Lista de gastos no encontrado");
        }

        logger.info("Recuperados {} tipos de gasto.", expenseTypeList.size());
        return new ApiResponse<>("Tipos de gasto recuperados exitosamente.", expenseTypeList);
    }

    @Transactional
    @Override
    public ApiResponse<ExpenseType> updateExpenseType(ExpenseType expenseType, Long id) {

        Optional<ExpenseType> expenseTypeOptional = expenseTypeRepository.findById(id);

        if (expenseTypeOptional.isPresent()){
            ExpenseType updateExpenseType = expenseTypeOptional.get();

            Optional<ExpenseType> typeWithSameName = expenseTypeRepository.findByName(expenseType.getName());
            if (typeWithSameName.isPresent() && !typeWithSameName.get().getId().equals(id)){
                logger.warn("Intento de actualizar tipo de gasto a nombre duplicado: {}", expenseType.getName());
                return new ApiResponse<>("error", "Ya existe otro tipo de gasto con el nombre '" + expenseType.getName() + "'.", null);
            }
            updateExpenseType.setName(expenseType.getName());

            try{
                ExpenseType savedExpenseType = expenseTypeRepository.save(updateExpenseType);
                logger.info("Tipo de gasto con ID {} actualizado a {}", id, savedExpenseType.getName());
                return new ApiResponse<>("Tipo de gasto actualizado exitosamente.", savedExpenseType);
            } catch (Exception e) {
                logger.error("Error al actualizar el tipo de gasto con ID {}: {}", id, e.getMessage(), e);
                return new ApiResponse<>("error", "Error interno al actualizar el tipo de gasto.", null);
            }

        } else {
            logger.warn("Tipo de gasto no encontrado para actualizar con ID: {}", id);
            return new ApiResponse<>("error", "Tipo de gasto no encontrado con ID: " + id, null);
        }
    }

    @Transactional
    @Override
    public ApiResponse<Void> deleteExpenseType(Long id) {
        if (expenseTypeRepository.existsById(id)) {
            try {
                expenseTypeRepository.deleteById(id);
                logger.info("Tipo de gasto con ID {} eliminado exitosamente.", id);
                return new ApiResponse<>("Tipo de gasto eliminado exitosamente.", null);
            } catch (Exception e) {
                logger.error("Error al eliminar el tipo de gasto con ID {}: {}", id, e.getMessage(), e);
                return new ApiResponse<>("error", "Error interno al eliminar el tipo de gasto.", null);
            }
        } else {
            logger.warn("Tipo de gasto no encontrado para eliminar con ID: {}", id);
            return new ApiResponse<>("error", "Tipo de gasto no encontrado con ID: " + id, null);
        }
    }

    @Override
    public ApiResponse<List<ExpenseType>> searchExpensesType(String name) {

        List<ExpenseType> expenseTypeList = expenseTypeRepository.findByNameContainingIgnoreCase(name);
        if (expenseTypeList.isEmpty()){
            return new ApiResponse<>("info", "No se encontraron tipos de gastos", null);
        }

        return new ApiResponse<>("success", "Busqueda exitosa", expenseTypeList);
    }
}
