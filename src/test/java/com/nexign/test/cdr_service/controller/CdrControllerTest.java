package com.nexign.test.cdr_service.controller;

import com.nexign.test.cdr_service.entity.UDR;
import com.nexign.test.cdr_service.service.CdrGeneratorService;
import com.nexign.test.cdr_service.service.UdrService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.time.format.DateTimeParseException;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


/**
 * Тесты для REST-контроллера управления CDR и UDR.
 */
class CdrControllerTest {

    @Mock
    private UdrService udrService;

    @Mock
    private CdrGeneratorService generatorService;

    @InjectMocks
    private CdrController cdrController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    /**
     * Тестирует генерацию CDR записей через эндпоинт.
     */
    @Test
    void testGenerateCdr() {
        ResponseEntity<String> response = cdrController.generateCdr();

        verify(generatorService, times(1)).generateCdrRecordsForYear();
        assertEquals("Генерация CDR завершена", response.getBody());
    }

    /**
     * Тестирует получение UDR для абонента с указанием месяца и года.
     */
    @Test
    void testGetUdrForSubscriberWithMonth() {
        UDR udr = new UDR("79991112233", new UDR.CallDuration("00:00:00"), new UDR.CallDuration("00:00:00"));
        when(udrService.getUdrForSubscriber(anyString(), any(), any())).thenReturn(udr);

        UDR result = cdrController.getUdrForSubscriber("79991112233", 1, 2025);

        assertEquals("79991112233", result.getMsisdn());
    }

    /**
     * Тестирует получение UDR для абонента за весь период без указания месяца и года.
     */
    @Test
    void testGetUdrForSubscriberFullPeriod() {
        UDR udr = new UDR("79991112233", new UDR.CallDuration("00:00:00"), new UDR.CallDuration("00:00:00"));
        when(udrService.getUdrForSubscriber(anyString(), any(), any())).thenReturn(udr);

        UDR result = cdrController.getUdrForSubscriber("79991112233", null, null);

        assertEquals("79991112233", result.getMsisdn());
    }
    /**
     * Тестирует получение UDR для всех абонентов за месяц.
     */
    @Test
    void testGetAllUdrsForMonth() {
        UDR udr = new UDR("79991112233", new UDR.CallDuration("00:00:00"), new UDR.CallDuration("00:00:00"));
        when(udrService.getAllUdrsForMonth(1, 2025)).thenReturn(Collections.singletonList(udr));

        var result = cdrController.getAllUdrsForMonth(1, 2025);

        assertEquals(1, result.size());
        assertEquals("79991112233", result.get(0).getMsisdn());
    }

    @Test
    void testGenerateCdrReport() {
        when(udrService.generateCdrReport(anyString(), anyString(), anyString())).thenReturn("test-uuid");

        ResponseEntity<String> response = cdrController.generateCdrReport("79991112233",
                "2025-01-01T00:00:00", "2025-01-02T00:00:00");

        assertEquals("Генерация отчета начата. UUID: test-uuid", response.getBody());
    }

    /**
     * Тестирует генерацию отчета CDR с некорректной датой.
     */
    /**
     * Тестирует генерацию отчета CDR с некорректной датой.
     */
    @Test
    void testGenerateCdrReportWithInvalidDate() {
        when(udrService.generateCdrReport(anyString(), eq("invalid-date"), anyString()))
                .thenThrow(new DateTimeParseException("Некорректный формат даты", "invalid-date", 0));

        ResponseEntity<String> response = cdrController.generateCdrReport("79991112233",
                "invalid-date", "2025-01-02T00:00:00");

        assertEquals(400, response.getStatusCodeValue());
        assertEquals("Некорректный формат даты", response.getBody());
    }
}