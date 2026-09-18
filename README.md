# Skazka Android Core

> RU — основной язык · EN — required second language

## RU

Общие Android-компоненты Skazka: базовые контракты, фоновые задачи, device-интеграции, диагностика и другие небольшие переиспользуемые части.

**Текущий статус:** репозиторий создан как целевая граница модуля. Рабочий код переносится из существующих проектов поэтапно, с тестами и без копирования project-specific зависимостей.

**Граница модуля:** network/storage/sync/background/telemetry/localization/notifications/device — только те части, которые действительно остаются общими.

Перед первым стабильным релизом здесь появятся собственные versioning, тесты, changelog и лицензия. До выбора лицензии публикация кода не означает автоматическое разрешение на его повторное использование.

## EN

Shared Android components for Skazka: base contracts, background work, device integrations, diagnostics, and other small reusable building blocks.

**Current status:** this repository is the target module boundary. Working code is being extracted from existing projects incrementally, with tests and without copying project-specific dependencies.

**Module boundary:** network/storage/sync/background/telemetry/localization/notifications/device — только те части, которые действительно остаются общими.

Before the first stable release, this repository will get its own versioning, tests, changelog, and license. Until a license is selected, publishing the source does not automatically grant reuse rights.

## Development rules / Правила разработки

See [DEVELOPMENT_RULES.md](DEVELOPMENT_RULES.md).
