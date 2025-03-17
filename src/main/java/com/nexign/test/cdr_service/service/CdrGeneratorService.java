package com.nexign.test.cdr_service.service;

import com.nexign.test.cdr_service.entity.CdrRecord;
import com.nexign.test.cdr_service.entity.Subscriber;
import com.nexign.test.cdr_service.repository.CdrRecordRepository;
import com.nexign.test.cdr_service.repository.SubscriberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

/**
 * Сервис для генерации записей данных о звонках (CDR).
 */
@Service
@RequiredArgsConstructor
public class CdrGeneratorService {


    private final CdrRecordRepository cdrRepository;
    private final SubscriberRepository subscriberRepository;
    private final Random random = new Random();


    /**
     * Генерирует CDR записи за весь год (2025).
     */
    public void generateCdrRecordsForYear() {
        LocalDateTime start = LocalDateTime.of(2025, 1, 1, 0, 0);
        LocalDateTime end = start.plusYears(1);
        List<Subscriber> subscribers = subscriberRepository.findAll();
        int maxRecords = 1000;
        int count = 0;

        if (subscribers.isEmpty()) {
            throw new IllegalStateException("Список абонентов пуст. Генерация невозможна.");
        }

        while (start.isBefore(end) && count < maxRecords) {
            CdrRecord record = generateRandomRecord(start, subscribers);
            cdrRepository.save(record);
            start = start.plusMinutes(random.nextInt(60));
            count++;
        }
    }

    /**
     * Генерирует одну случайную CDR запись.
     *
     * @param start       Время начала звонка
     * @param subscribers Список доступных абонентов
     * @return Сгенерированная запись CdrRecord
     */
    private CdrRecord generateRandomRecord(LocalDateTime start, List<Subscriber> subscribers) {
        String caller = subscribers.get(random.nextInt(subscribers.size())).getMsisdn();
        String receiver = subscribers.get(random.nextInt(subscribers.size())).getMsisdn();
        int durationSeconds = random.nextInt(3600);
        return new CdrRecord(null, random.nextBoolean() ? "01" : "02", caller, receiver,
                start, start.plusSeconds(durationSeconds));
    }
}