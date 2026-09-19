# Skazka Android Core

> RU — основной язык · EN — required second language

## RU

Общие Android-компоненты Skazka, которые действительно переиспользуются между приложениями.

**Статус:** `0.1.0-preview`.

- `screen-awake-core` — чистая state machine режима: permissions/ready/infinite/timer/charging/recovery.
- `screen-awake-android` — безопасное сохранение и восстановление системного `SCREEN_OFF_TIMEOUT`; UI, Widget, Tile, уведомления и метрики остаются у конкретного приложения.
- `telemetry-core` — transport-neutral очередь: coalesce только low-priority usage, сохранение error/source events, защита событий, добавленных во время flush, и bounded queue.
- В общий core не складываются произвольные app-specific helpers: новый модуль появляется только когда у него есть независимая ответственность и повторное использование.

Проверено на HOSTKEY: Android Core self-test — PASS; `screen-awake-core:build` — PASS; `telemetry-core:build` — PASS; `screen-awake-android:assembleDebug` — PASS; `screen-awake-android:lintDebug` — PASS.

## EN

Shared Android components that are genuinely reusable across Skazka applications.

**Status:** `0.1.0-preview`.

- `screen-awake-core` — pure mode state machine for permissions/ready/infinite/timer/charging/recovery.
- `screen-awake-android` — safe preservation and restoration of Android `SCREEN_OFF_TIMEOUT`; UI, widgets, tiles, notifications, and metrics stay in the concrete application.
- `telemetry-core` — transport-neutral queue: coalesces only low-priority usage, preserves error/source events and events added during flush, and keeps the queue bounded.
- Arbitrary app-specific helpers do not belong here; a module is added only when it has an independent responsibility and real reuse value.

Verified on HOSTKEY: Android Core self-test — PASS; `screen-awake-core:build` — PASS; `telemetry-core:build` — PASS; `screen-awake-android:assembleDebug` — PASS; `screen-awake-android:lintDebug` — PASS.

## Design canon / Канон дизайна

Визуальный источник истины вынесен отдельно в `kroxaboom-sudo/skazka-design-system` и версионируется как `SKAZKA-DESIGN-1.0`. Android Core может предоставлять технические реализации компонентов, но не переопределяет цвета, иконки, типографику, геометрию или responsive-правила канона.

The visual source of truth lives in `kroxaboom-sudo/skazka-design-system` and is versioned as `SKAZKA-DESIGN-1.0`. Android Core may provide technical component implementations, but it does not redefine canonical colors, icons, typography, geometry or responsive behavior.

## Coordinates / Координаты

- `com.kroxaboom.skazka:screen-awake-core:0.1.0-preview`
- `com.kroxaboom.skazka:screen-awake-android:0.1.0-preview`
- `com.kroxaboom.skazka:telemetry-core:0.1.0-preview`

See [DEVELOPMENT_RULES.md](DEVELOPMENT_RULES.md).

> A license will be selected before the first stable public release. Until then, publication of the source does not grant reuse rights.
