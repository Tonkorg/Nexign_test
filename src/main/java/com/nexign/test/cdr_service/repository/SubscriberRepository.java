package com.nexign.test.cdr_service.repository;

import com.nexign.test.cdr_service.entity.Subscriber;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Интерфейс репозитория для управления сущностями Subscriber.
 */
public interface SubscriberRepository extends JpaRepository<Subscriber, String> {
}
