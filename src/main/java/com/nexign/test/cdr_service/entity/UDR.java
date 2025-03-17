package com.nexign.test.cdr_service.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * Формирует отчет об использовании данных
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UDR {
    private String msisdn;
    private CallDuration incomingCall;
    private CallDuration outcomingCall;


    /**
     * Данные о продолжительности вызова.
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CallDuration {
        private String totalTime;
    }
}