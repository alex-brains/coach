package by.alex.coach.models.enums;

import java.time.Duration;

/**
 * Стадии SRS. Каждая стадия хранит интервал до следующего повторения.
 * При правильном ответе → переход на следующую стадию.
 * При неправильном → сброс на NEW (карточка уходит на переизучение).
 *
 * Схема: NEW → 8ч → 1д → 2д → 4д → 8д → 16д → 1м → 2м → ARCHIVED
 */
public enum SrsStage {
    NEW(Duration.ZERO),              // только создана, ещё не изучалась
    HOURS_8(Duration.ofHours(8)),
    DAY_1(Duration.ofDays(1)),
    DAY_2(Duration.ofDays(2)),
    DAY_4(Duration.ofDays(4)),
    DAY_8(Duration.ofDays(8)),
    DAY_16(Duration.ofDays(16)),
    MONTH_1(Duration.ofDays(30)),
    MONTH_2(Duration.ofDays(60)),
    ARCHIVED(Duration.ZERO);        // финальная стадия, не повторяется

    private final Duration interval;

    SrsStage(Duration interval) {
        this.interval = interval;
    }

    public Duration getInterval() {
        return interval;
    }

    /**
     * Следующая стадия при правильном ответе.
     * MONTH_2 → ARCHIVED (конец пути).
     */
    public SrsStage next() {
        SrsStage[] values = SrsStage.values();
        int nextOrdinal = this.ordinal() + 1;
        return nextOrdinal < values.length ? values[nextOrdinal] : ARCHIVED;
    }

    public boolean isArchived() {
        return this == ARCHIVED;
    }

    public boolean isNew() {
        return this == NEW;
    }
}
