package com.nexign.test.cdr_service.service;

import com.nexign.test.cdr_service.entity.CdrRecord;
import com.nexign.test.cdr_service.entity.UDR;
import com.nexign.test.cdr_service.repository.CdrRecordRepository;
import com.nexign.test.cdr_service.repository.SubscriberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileWriter;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;


/**
 * Сервис для генерации отчетов об использовании данных (UDR) и отчетов CDR.
 */
@Service
@RequiredArgsConstructor
public class UdrService {


    private final CdrRecordRepository cdrRepository;
    private final SubscriberRepository subscriberRepository;
    private static final DateTimeFormatter ISO_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;


    /**
     * Получает UDR для конкретного абонента за определенный период времени.
     *
     * @param msisdn Номер телефона абонента
     * @param start  Начало периода
     * @param end    Конец периода
     * @return Объект UDR с длительностями звонков
     */
    public UDR getUdrForSubscriber(String msisdn, LocalDateTime start, LocalDateTime end) {
        List<CdrRecord> outgoing = cdrRepository.findByCallerNumberAndStartTimeBetween(msisdn, start, end);
        List<CdrRecord> incoming = cdrRepository.findByReceiverNumberAndStartTimeBetween(msisdn, start, end);

        long outgoingSeconds = outgoing.stream()
                .mapToLong(r -> Duration.between(r.getStartTime(), r.getEndTime()).getSeconds())
                .sum();
        long incomingSeconds = incoming.stream()
                .mapToLong(r -> Duration.between(r.getStartTime(), r.getEndTime()).getSeconds())
                .sum();

        return new UDR(msisdn,
                new UDR.CallDuration(formatDuration(incomingSeconds)),
                new UDR.CallDuration(formatDuration(outgoingSeconds)));
    }

    /**
     * Получает UDR для всех абонентов за конкретный месяц.
     *
     * @param month Номер месяца (1-12)
     * @param year  Год
     * @return Список UDR
     */
    public List<UDR> getAllUdrsForMonth(int month, int year) {
        LocalDateTime start = LocalDateTime.of(year, month, 1, 0, 0);
        LocalDateTime end = start.plusMonths(1);
        return subscriberRepository.findAll().stream()
                .map(s -> getUdrForSubscriber(s.getMsisdn(), start, end))
                .collect(Collectors.toList());
    }

    /**
     * Генерирует файл отчета CDR для абонента за указанный период времени.
     *
     * @param msisdn    Номер телефона абонента
     * @param startDate Начальная дата в формате ISO 8601
     * @param endDate   Конечная дата в формате ISO 8601
     * @return UUID сгенерированного отчета
     */
    public String generateCdrReport(String msisdn, String startDate, String endDate) {
        LocalDateTime start = LocalDateTime.parse(startDate, ISO_FORMATTER);
        LocalDateTime end = LocalDateTime.parse(endDate, ISO_FORMATTER);
        List<CdrRecord> records = cdrRepository.findByStartTimeBetweenOrderByStartTimeAsc(start, end)
                .stream()
                .filter(r -> r.getCallerNumber().equals(msisdn) || r.getReceiverNumber().equals(msisdn))
                .collect(Collectors.toList());

        String uuid = UUID.randomUUID().toString();
        String filename = "reports/" + msisdn + "_" + uuid + ".csv";
        File reportDir = new File("reports");
        if (!reportDir.exists()) reportDir.mkdir();

        try (FileWriter writer = new FileWriter(filename)) {
            for (CdrRecord r : records) {
                writer.write(String.format("%s,%s,%s,%s,%s%n",
                        r.getCallType(),
                        r.getCallerNumber(),
                        r.getReceiverNumber(),
                        r.getStartTime().format(ISO_FORMATTER),
                        r.getEndTime().format(ISO_FORMATTER)));
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate CDR report", e);
        }
        return uuid;
    }

    /**
     * Форматирует длительность в секундах в формат HH:mm:ss.
     *
     * @param seconds Длительность в секундах
     * @return Отформатированная строка длительности
     */
    private String formatDuration(long seconds) {
        long hours = seconds / 3600;
        long minutes = (seconds % 3600) / 60;
        long secs = seconds % 60;
        return String.format("%02d:%02d:%02d", hours, minutes, secs);
    }
}