# CDR Service
Микросервис для генерации и обработки CDR записей в телекоммуникационной системе.

## Описание
Приложение моделирует процесс генерации CDR записей, их хранения в H2 базе данных и предоставления отчетов через REST API:
- Генерация CDR за год.
- Получение UDR (Usage Data Report) в формате JSON.
- Генерация CDR отчетов в CSV.

## Требования
- Java 17
- Maven
- Spring Boot 3.x
- H2 Database

## Установка и запуск
1. Склонируйте репозиторий: 
2. Перейдите в папку: `cd cdr-service`
3. Соберите проект: `mvn clean install`
4. Запустите: `java -jar target/cdr-service-0.0.1-SNAPSHOT.jar`

## Эндпоинты
- `POST /api/generate` — Генерация CDR записей.
- `GET /api/udr/{msisdn}` — Получение UDR для абонента.
- `GET /api/udr/all` — UDR для всех абонентов.
- `POST /api/cdr/report` — Генерация CDR отчета.

## Тестирование
- `mvn test` — Запуск юнит-тестов.