package com.nexign.test.cdr_service.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *  Абонент мобильной связи.
 */
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Subscriber {
    @Id
    private String msisdn;

}