package com.nexign.test.cdr_service.repository;

import com.nexign.test.cdr_service.entity.CdrRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Интерфейс репозитория для управления сущностями CdrRecord.
 */

public interface CdrRecordRepository extends JpaRepository<CdrRecord, Long> {
    List<CdrRecord> findByCallerNumberAndStartTimeBetween(String msisdn, LocalDateTime start, LocalDateTime end);

    List<CdrRecord> findByReceiverNumberAndStartTimeBetween(String msisdn, LocalDateTime start, LocalDateTime end);

    List<CdrRecord> findByStartTimeBetweenOrderByStartTimeAsc(LocalDateTime start, LocalDateTime end);
}