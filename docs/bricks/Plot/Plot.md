# Чертежи / Plot

Категория `category_plot`: RU «Чертежи», EN «Plot».

Источник состава и порядка: `catroid/src/main/java/org/catrobat/catroid/ui/fragment/CategoryBricksFactory.kt:3216`, метод `setupPlotCategoryList`.

Правила документа:

- Порядок бриков строго по фабрике; подгруппы — `SubCategoryHeaderBrick` из того же метода.
- Названия RU/EN — по первому `@string` главного layout (`getViewResource`) из `catroid/src/main/res/values/strings.xml` и `values-ru/strings.xml`; у бриков без текстовой подписи — значения `R.array.*` из `arrays.xml`.
- Параметры — `addAllowedBrickField(BrickField.*)` + спиннер по факту кода брика.
- Экшен — фактический класс из `content/actions` через `ActionFactory.create*` (резолв по `ActionFactory.java`); у ScriptBrick экшена нет.
- Описание — 1–2 строки строго по `BrickInfo.java` + факт кода; домыслов нет.

Всего бриков в этом файле: 3 (заголовки подгрупп не считаются).

### 1. `StartPlotBrick`

| Поле | Значение |
|---|---|
| Название RU | Начать чертить |
| Название EN | Start to plot |
| Ключ строки / layout | `brick_start_plot` / `brick_start_plot` |
| Параметры | — (без формульных полей) |
| Дефолт в палитре | () |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/StartPlotBrick.kt:30` |
| Класс экшена | StartPlotAction (catroid/src/main/java/org/catrobat/catroid/content/actions/StartPlotAction.kt:29) — через createStartPlotAction |
| Описание | Нет в `BrickInfo.java`. Код: создаёт `StartPlotAction` в `addActionToSequence` через `createStartPlotAction`. |

### 2. `StopPlotBrick`

| Поле | Значение |
|---|---|
| Название RU | Остановить черчение |
| Название EN | Stop to plot |
| Ключ строки / layout | `brick_stop_plot` / `brick_stop_plot` |
| Параметры | — (без формульных полей) |
| Дефолт в палитре | () |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/StopPlotBrick.kt:30` |
| Класс экшена | StopPlotAction (catroid/src/main/java/org/catrobat/catroid/content/actions/StopPlotAction.kt:28) — через createStopPlotAction |
| Описание | Нет в `BrickInfo.java`. Код: создаёт `StopPlotAction` в `addActionToSequence` через `createStopPlotAction`. |

### 3. `SavePlotBrick`

| Поле | Значение |
|---|---|
| Название RU | Сохранить чертеж как SVG |
| Название EN | Save plot as SVG |
| Ключ строки / layout | `brick_save_plot` / `brick_save_plot` |
| Параметры | Formula: WRITE_FILENAME (`brick_save_plot_edit_text`) |
| Дефолт в палитре | (context.getString(R.string.brick_default_plot_file)) |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/SavePlotBrick.kt:33` |
| Класс экшена | SavePlotAction (catroid/src/main/java/org/catrobat/catroid/content/actions/SavePlotAction.kt:49) — через createSavePlotAction |
| Описание | Нет в `BrickInfo.java`. Код: создаёт `SavePlotAction` в `addActionToSequence` через `createSavePlotAction`. |
