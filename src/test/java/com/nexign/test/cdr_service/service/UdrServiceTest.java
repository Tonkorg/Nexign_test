package com.nexign.test.cdr_service.service;

import com.nexign.test.cdr_service.entity.CdrRecord;
import com.nexign.test.cdr_service.entity.Subscriber;
import com.nexign.test.cdr_service.entity.UDR;
import com.nexign.test.cdr_service.repository.CdrRecordRepository;
import com.nexign.test.cdr_service.repository.SubscriberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


/**
 * Тесты для сервиса обработки UDR и отчетов CDR.
 */
class UdrServiceTest {

    @Mock
    private CdrRecordRepository cdrRepository;

    @Mock
    private SubscriberRepository subscriberRepository;

    @InjectMocks
    private UdrService udrService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }


    /**
     * Тестирует получение UDR для абонента за определенный период.
     */
    @Test
    void testGetUdrForSubscriber() {
        LocalDateTime start = LocalDateTime.of(2025, 1, 1, 0, 0);
        LocalDateTime end = LocalDateTime.of(2025, 1, 2, 0, 0);
        CdrRecord outgoing = new CdrRecord(1L, "01", "79991112233", "79992223344", start, start.plusSeconds(3600));
        CdrRecord incoming = new CdrRecord(2L, "02", "79992223344", "79991112233", start, start.plusSeconds(1800));

        when(cdrRepository.findByCallerNumberAndStartTimeBetween("79991112233", start, end))
                .thenReturn(Collections.singletonList(outgoing));
        when(cdrRepository.findByReceiverNumberAndStartTimeBetween("79991112233", start, end))
                .thenReturn(Collections.singletonList(incoming));

        UDR udr = udrService.getUdrForSubscriber("79991112233", start, end);

        assertEquals("79991112233", udr.getMsisdn());
        assertEquals("00:30:00", udr.getIncomingCall().getTotalTime());
        assertEquals("01:00:00", udr.getOutcomingCall().getTotalTime());
    }

    /**
     * Тестирует получение UDR для всех абонентов за месяц.
     */
    @Test
    void testGetAllUdrsForMonth() {
        when(subscriberRepository.findAll()).thenReturn(Collections.singletonList(new Subscriber("79991112233")));
        LocalDateTime start = LocalDateTime.of(2025, 1, 1, 0, 0);
        when(cdrRepository.findByCallerNumberAndStartTimeBetween(anyString(), any(), any()))
                .thenReturn(Collections.emptyList());
        when(cdrRepository.findByReceiverNumberAndStartTimeBetween(anyString(), any(), any()))
                .thenReturn(Collections.emptyList());

        List<UDR> udrs = udrService.getAllUdrsForMonth(1, 2025);

        assertEquals(1, udrs.size());
        assertEquals("79991112233", udrs.get(0).getMsisdn());
    }

    @Test
    void testGenerateCdrReport() {
        LocalDateTime start = LocalDateTime.of(2025, 1, 1, 0, 0);
        CdrRecord record = new CdrRecord(1L, "01", "79991112233", "79992223344", start, start.plusSeconds(3600));
        when(cdrRepository.findByStartTimeBetweenOrderByStartTimeAsc(any(), any()))
                .thenReturn(Collections.singletonList(record));

        String uuid = udrService.generateCdrReport("79991112233", "2025-01-01T00:00:00", "2025-01-02T00:00:00");

        assertNotNull(uuid);
        verify(cdrRepository, times(1)).findByStartTimeBetweenOrderByStartTimeAsc(any(), any());
    }
}