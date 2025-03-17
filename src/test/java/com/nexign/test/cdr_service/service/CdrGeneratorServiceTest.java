package com.nexign.test.cdr_service.service;

import com.nexign.test.cdr_service.entity.CdrRecord;
import com.nexign.test.cdr_service.entity.Subscriber;
import com.nexign.test.cdr_service.repository.CdrRecordRepository;
import com.nexign.test.cdr_service.repository.SubscriberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

/**
 * Тесты для сервиса генерации CDR записей.
 */
class CdrGeneratorServiceTest {

    @Mock
    private CdrRecordRepository cdrRepository;

    @Mock
    private SubscriberRepository subscriberRepository;

    @InjectMocks
    private CdrGeneratorService cdrGeneratorService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    /**
     * Тестирует генерацию CDR записей за год.
     */
    @Test
    void testGenerateCdrRecordsForYear() {
        when(subscriberRepository.findAll()).thenReturn(Collections.singletonList(new Subscriber("79991112233")));
        when(cdrRepository.save(any(CdrRecord.class))).thenReturn(new CdrRecord());

        cdrGeneratorService.generateCdrRecordsForYear();

        verify(subscriberRepository, times(1)).findAll();
        verify(cdrRepository, atLeastOnce()).save(any(CdrRecord.class));
    }

    /**
     * Проверка на пустой список
     */
    @Test
    void testGenerateCdrRecordsForYearWithEmptySubscribers() {
        when(subscriberRepository.findAll()).thenReturn(Collections.emptyList());

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            cdrGeneratorService.generateCdrRecordsForYear();
        });

        assertEquals("Список абонентов пуст. Генерация невозможна.", exception.getMessage());
    }
}