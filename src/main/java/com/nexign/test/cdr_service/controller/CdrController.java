package com.nexign.test.cdr_service.controller;

import com.nexign.test.cdr_service.entity.UDR;
import com.nexign.test.cdr_service.service.CdrGeneratorService;
import com.nexign.test.cdr_service.service.UdrService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;

/**
 * REST-контроллер для управления операциями с CDR и UDR.
 */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Validated
public class CdrController {
    private final UdrService udrService;
    private final CdrGeneratorService generatorService;

    /**
     * Запускает генерацию CDR записей за год.
     *
     * @return Ответ с подтверждением завершения генерации
     */
    @Operation(summary = "Генерация CDR записей за год")
    @ApiResponse(responseCode = "200", description = "Генерация CDR завершена")
    @PostMapping("/generate")
    public ResponseEntity<String> generateCdr() {
        try {
            generatorService.generateCdrRecordsForYear();
            return ResponseEntity.ok("Генерация CDR завершена");
        } catch (IllegalStateException e) {
            return ResponseEntity.status(500).body("Ошибка генерации: " + e.getMessage());
        }
    }

    /**
     * Получает UDR для конкретного абонента за указанный период или за весь доступный период.
     *
     * @param msisdn Номер телефона абонента (MSISDN)
     * @param month  Номер месяца (1-12), необязательный параметр
     * @param year   Год, необязательный параметр
     * @return Объект UDR с данными о длительности звонков
     */
    @Operation(summary = "Получение UDR для конкретного абонента")
    @ApiResponse(responseCode = "200", description = "UDR успешно получен")
    @GetMapping("/udr/{msisdn}")
    public UDR getUdrForSubscriber(
            @PathVariable String msisdn,
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer year) {
        if (month != null && year != null) {
            LocalDateTime start = LocalDateTime.of(year, month, 1, 0, 0);
            LocalDateTime end = start.plusMonths(1);
            return udrService.getUdrForSubscriber(msisdn, start, end);
        }
        return udrService.getUdrForSubscriber(msisdn, LocalDateTime.of(2025, 1, 1, 0, 0),
                LocalDateTime.of(2026, 1, 1, 0, 0));
    }

    /**
     * Получает UDR для всех абонентов за указанный месяц.
     *
     * @param month Номер месяца (1-12)
     * @param year  Год
     * @return Список объектов UDR
     */
    @Operation(summary = "Получение UDR для всех абонентов за месяц")
    @ApiResponse(responseCode = "200", description = "Список UDR успешно получен")
    @GetMapping("/udr/all")
    public List<UDR> getAllUdrsForMonth(
            @RequestParam Integer month,
            @RequestParam Integer year) {
        return udrService.getAllUdrsForMonth(month, year);
    }

    /**
     * Инициирует генерацию отчета CDR для абонента за указанный период.
     *
     * @param msisdn    Номер телефона абонента (MSISDN)
     * @param startDate Начальная дата периода в формате ISO 8601
     * @param endDate   Конечная дата периода в формате ISO 8601
     * @return Ответ с UUID сгенерированного отчета
     */
    @Operation(summary = "Генерация отчета CDR для абонента")
    @ApiResponse(responseCode = "200", description = "Генерация отчета начата")
    @ApiResponse(responseCode = "400", description = "Некорректные входные данные")
    @PostMapping("/cdr/report")
    public ResponseEntity<String> generateCdrReport(
            @Parameter(description = "MSISDN абонента")
            @RequestParam @NotBlank(message = "MSISDN не может быть пустым") String msisdn,
            @Parameter(description = "Начальная дата ")
            @RequestParam @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}", message = "Некорректный формат даты") String startDate,
            @Parameter(description = "Конечная дата ")
            @RequestParam @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}", message = "Некорректный формат даты") String endDate) {
        try {
            String uuid = udrService.generateCdrReport(msisdn, startDate, endDate);
            return ResponseEntity.ok("Генерация отчета начата. UUID: " + uuid);
        } catch (DateTimeParseException e) {
            return ResponseEntity.badRequest().body("Некорректный формат даты");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Ошибка генерации отчета: " + e.getMessage());
        }
    }
}