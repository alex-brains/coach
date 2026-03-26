-- ШАГ 1: Меняем тип колонки stage с int на varchar
-- Запускай ПЕРЕД деплоем нового кода

-- 1. Добавляем новую колонку
ALTER TABLE srs_reviews ADD COLUMN stage_new varchar(20);

-- 2. Конвертируем числа в названия стадий
UPDATE srs_reviews SET stage_new = CASE
    WHEN stage = 0 THEN 'NEW'
    WHEN stage = 1 THEN 'HOURS_8'
    WHEN stage = 2 THEN 'DAY_1'
    WHEN stage = 3 THEN 'DAY_2'
    WHEN stage = 4 THEN 'DAY_4'
    WHEN stage = 5 THEN 'DAY_8'
    WHEN stage = 6 THEN 'DAY_16'
    WHEN stage = 7 THEN 'MONTH_1'
    WHEN stage = 8 THEN 'MONTH_2'
    ELSE 'NEW'
END;

ALTER TABLE srs_reviews ALTER COLUMN stage_new SET NOT NULL;
ALTER TABLE srs_reviews ALTER COLUMN stage_new SET DEFAULT 'NEW';
ALTER TABLE srs_reviews DROP COLUMN stage;
ALTER TABLE srs_reviews RENAME COLUMN stage_new TO stage;

-- Индекс для быстрой выборки due карточек
CREATE INDEX IF NOT EXISTS idx_srs_reviews_due
    ON srs_reviews (next_review_at)
    WHERE archived = false;

-- ШАГ 2: Создаём SRS-записи для всех существующих вопросов и слов
-- которые были добавлены до того как мы включили auto-create

INSERT INTO srs_reviews (item_type, item_id, stage, next_review_at, correct_in_row, archived)
SELECT 'QUESTION', id, 'NEW', now(), 0, false
FROM questions
WHERE id NOT IN (
    SELECT item_id FROM srs_reviews WHERE item_type = 'QUESTION'
);

INSERT INTO srs_reviews (item_type, item_id, stage, next_review_at, correct_in_row, archived)
SELECT 'LANGUAGE_ITEM', id, 'NEW', now(), 0, false
FROM languages_items
WHERE id NOT IN (
    SELECT item_id FROM srs_reviews WHERE item_type = 'LANGUAGE_ITEM'
);

-- Проверка — должно показать количество карточек в очереди
SELECT COUNT(*) as due_count
FROM srs_reviews
WHERE archived = false AND next_review_at <= now();
