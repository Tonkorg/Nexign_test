package com.nexign.test.cdr_service.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Представляет собой объект записи данных вызова (CDR)
 */
@Entity
@Table(name = "cdr_records")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CdrRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String callType;
    private String callerNumber;
    private String receiverNumber;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
}