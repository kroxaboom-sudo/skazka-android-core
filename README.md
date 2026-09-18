# Skazka Android Core

**RU:** Базовые Android-компоненты, которые нужны нескольким приложениям Skazka.

**EN:** Shared Android building blocks used by more than one Skazka app.

## Что здесь будет / What belongs here

- network и fallback-клиент
- background/job primitives
- telemetry/diagnostics contracts
- localization RU/EN
- notification primitives
- device helpers, включая общий Screen Awake Core
- общие Android contracts

## Граница / Boundary

Хранилище, синхронизация и backup живут в skazka-data; медиадвижки — в skazka-media. Production-серверная логика сюда не переносится.

## Статус / Status

Миграция началась. Репозиторий уже выделен из общей архитектуры Skazka, но рабочий код переносится небольшими проверяемыми шагами. Пока API не помечен как stable, совместимость между версиями не гарантируется.

Migration has started. The repository is separated at the architecture level, while working code is being moved in small, verifiable steps. Until an API is marked stable, compatibility between versions is not guaranteed.

## Принципы / Principles

- RU — основной язык, EN — обязательный второй.
- Публичный код не содержит ключей, production-конфигурации, приватных маршрутов или закрытых endpoints.
- Общая логика не должна знать о конкретном приложении больше, чем требуется её публичному API.
- Исправление в общем модуле должно быть пригодно для всех клиентов Skazka, которые его подключают.

---

Skazka is built as a set of small reusable components instead of one growing monolith.
