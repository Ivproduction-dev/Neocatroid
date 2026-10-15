# Вышивка / Embroidery

Категория `category_embroidery`: RU «Вышивка», EN «Embroidery».

Источник состава и порядка: `catroid/src/main/java/org/catrobat/catroid/ui/fragment/CategoryBricksFactory.kt:3203`, метод `setupEmbroideryCategoryList`.

Правила документа:

- Порядок бриков строго по фабрике; подгруппы — `SubCategoryHeaderBrick` из того же метода.
- Названия RU/EN — по первому `@string` главного layout (`getViewResource`) из `catroid/src/main/res/values/strings.xml` и `values-ru/strings.xml`; у бриков без текстовой подписи — значения `R.array.*` из `arrays.xml`.
- Параметры — `addAllowedBrickField(BrickField.*)` + спиннер по факту кода брика.
- Экшен — фактический класс из `content/actions` через `ActionFactory.create*` (резолв по `ActionFactory.java`); у ScriptBrick экшена нет.
- Описание — 1–2 строки строго по `BrickInfo.java` + факт кода; домыслов нет.

Всего бриков в этом файле: 8 (заголовки подгрупп не считаются).

### 1. `StitchBrick`

| Поле | Значение |
|---|---|
| Название RU | Прикрепить |
| Название EN | Stitch |
| Ключ строки / layout | `brick_stitch` / `brick_stitch` |
| Параметры | — (без формульных полей) |
| Дефолт в палитре | () |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/StitchBrick.java:30` |
| Класс экшена | StitchAction (catroid/src/main/java/org/catrobat/catroid/content/actions/StitchAction.java:31) — через createStitchAction |
| Описание | Выполняет один стежок вышивки пером в текущей позиции спрайта. Используется вместе с другими блоками вышивки. EN: Performs a single embroidery stitch with the pen at the sprite's current position. Used together with other embroidery blocks. Код: создаёт `StitchAction` в `addActionToSequence` через `createStitchAction`. |

### 2. `SetThreadColorBrick`

| Поле | Значение |
|---|---|
| Название RU | Задать цвет нити |
| Название EN | Set thread color to |
| Ключ строки / layout | `brick_thread_color` / `brick_set_thread_color` |
| Параметры | Formula: THREAD_COLOR (`brick_set_thread_color_action_edit_text`) |
| Дефолт в палитре | (Formula(BrickValues.THREAD_COLOR)) |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/SetThreadColorBrick.java:39` |
| Класс экшена | SetThreadColorAction (catroid/src/main/java/org/catrobat/catroid/content/actions/SetThreadColorAction.java:35) — через createSetThreadColorAction |
| Описание | Задает цвет нити для вышивания (embroidery) спрайта. EN: Sets the embroidery thread color of the sprite. Код: создаёт `SetThreadColorAction` в `addActionToSequence` через `createSetThreadColorAction`. |

### 3. `RunningStitchBrick`

| Поле | Значение |
|---|---|
| Название RU | Запустить вышивку с длиной |
| Название EN | Start running stitch with length |
| Ключ строки / layout | `brick_running_stitch` / `brick_running_stitch_with_length` |
| Параметры | Formula: EMBROIDERY_LENGTH (`brick_running_stitch_edit_text_length`) |
| Дефолт в палитре | (Formula(BrickValues.STITCH_LENGTH)) |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/RunningStitchBrick.java:30` |
| Класс экшена | RunningStitchAction (catroid/src/main/java/org/catrobat/catroid/content/actions/RunningStitchAction.java:34) — через createRunningStitchAction |
| Описание | Начинает вышивание бегущим стежком указанной длины (функция вышивания). EN: Starts an embroidery running stitch of the given length (embroidery feature). Код: создаёт `RunningStitchAction` в `addActionToSequence` через `createRunningStitchAction`. |

### 4. `ZigZagStitchBrick`

| Поле | Значение |
|---|---|
| Название RU | Запустить вышивку зигзагом с длиной |
| Название EN | Start zigzag stitch with length |
| Ключ строки / layout | `brick_zigzag_stitch` / `brick_zigzag_stitch` |
| Параметры | Formula: ZIGZAG_EMBROIDERY_LENGTH (`brick_zigzag_stitch_edit_text_length`); Formula: ZIGZAG_EMBROIDERY_WIDTH (`brick_zigzag_stitch_edit_text_width`) |
| Дефолт в палитре | (Formula(BrickValues.ZIGZAG_STITCH_LENGTH), Formula(BrickValues.ZIGZAG_STITCH_WIDTH)) |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/ZigZagStitchBrick.java:30` |
| Класс экшена | ZigZagStitchAction (catroid/src/main/java/org/catrobat/catroid/content/actions/ZigZagStitchAction.java:35) — через createZigZagStitchAction |
| Описание | Выполняет зигзагообразный стежок вышивки с заданной длиной и шириной. EN: Performs a zigzag embroidery stitch with the given length and width. Код: создаёт `ZigZagStitchAction` в `addActionToSequence` через `createZigZagStitchAction`. |

### 5. `TripleStitchBrick`

| Поле | Значение |
|---|---|
| Название RU | Запустить тройную вышивку с длиной |
| Название EN | Start triple stitch with length |
| Ключ строки / layout | `brick_triple_stitch` / `brick_triple_stitch` |
| Параметры | Formula: EMBROIDERY_LENGTH (`brick_triple_stitch_edit_text_steps`) |
| Дефолт в палитре | (Formula(BrickValues.STITCH_LENGTH)) |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/TripleStitchBrick.java:30` |
| Класс экшена | TripleStitchAction (catroid/src/main/java/org/catrobat/catroid/content/actions/TripleStitchAction.java:34) — через createTripleStitchAction |
| Описание | Начинает тройную (усиленную) строчку для вышивки с заданной длиной стежка. Каждый стежок прошивается трижды для более плотного шва. EN: Starts a triple (reinforced) embroidery stitch with the given stitch length. Each stitch is sewn three times for a denser seam. Код: создаёт `TripleStitchAction` в `addActionToSequence` через `createTripleStitchAction`. |

### 6. `SewUpBrick`

| Поле | Значение |
|---|---|
| Название RU | Шить |
| Название EN | Sew up |
| Ключ строки / layout | `brick_sew_up` / `brick_sew_up` |
| Параметры | — (без формульных полей) |
| Дефолт в палитре | () |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/SewUpBrick.java:29` |
| Класс экшена | SewUpAction (catroid/src/main/java/org/catrobat/catroid/content/actions/SewUpAction.java:31) — через createSewUpAction |
| Описание | Завершает вышивание: делает стежок-замок (\"зашивает\") в текущей позиции спрайта. EN: Finishes embroidery: makes a lock stitch ('sews up') at the sprite's current position. Код: создаёт `SewUpAction` в `addActionToSequence` через `createSewUpAction`. |

### 7. `StopRunningStitchBrick`

| Поле | Значение |
|---|---|
| Название RU | Остановить текущую вышивку |
| Название EN | Stop current stitch |
| Ключ строки / layout | `brick_stop_stitch` / `brick_stop_running_stitch` |
| Параметры | — (без формульных полей) |
| Дефолт в палитре | () |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/StopRunningStitchBrick.java:30` |
| Класс экшена | StopRunningStitchAction (catroid/src/main/java/org/catrobat/catroid/content/actions/StopRunningStitchAction.java:29) — через createStopRunningStitchAction |
| Описание | Останавливает выполняющийся в данный момент блок вышивки (например, Running Stitch). EN: Stops the currently running embroidery stitch block (e.g. Running Stitch). Код: создаёт `StopRunningStitchAction` в `addActionToSequence` через `createStopRunningStitchAction`. |

### 8. `WriteEmbroideryToFileBrick`

| Поле | Значение |
|---|---|
| Название RU | Записать данные вышивки в файл |
| Название EN | Write embroidery data to file |
| Ключ строки / layout | `brick_write_embroidery` / `brick_write_embroidery_to_file` |
| Параметры | Formula: WRITE_FILENAME (`brick_write_embroidery_to_file_edit_text`) |
| Дефолт в палитре | (context.getString(R.string.brick_default_embroidery_file)) |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/WriteEmbroideryToFileBrick.kt:32` |
| Класс экшена | WriteEmbroideryToFileAction (catroid/src/main/java/org/catrobat/catroid/content/actions/WriteEmbroideryToFileAction.kt:50) — через createWriteEmbroideryToFileAction |
| Описание | Нет в `BrickInfo.java`. Код: создаёт `WriteEmbroideryToFileAction` в `addActionToSequence` через `createWriteEmbroideryToFileAction`. |
