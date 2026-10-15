# Управление / Control

Категория `category_control`: RU «Управление», EN «Control».

Источник состава и порядка: `catroid/src/main/java/org/catrobat/catroid/ui/fragment/CategoryBricksFactory.kt:1559`, метод `setupControlCategoryList` (сгруппированный список, ветка grouping enabled).

Правила документа:

- Порядок бриков строго по фабрике; подгруппы — `SubCategoryHeaderBrick` из того же метода.
- Условные брики помечены (фон/global-сцена/NFC/Phiro) — позиция сохранена по фабрике.
- Кастомный хвост из конца палитры не трогаем и сюда не включаем (только состав `setup*` выше).
- Названия RU/EN — по первому `@string` главного layout (`getViewResource`) из `catroid/src/main/res/values/strings.xml` и `values-ru/strings.xml`.
- Параметры — `addAllowedBrickField(BrickField.*)` + спиннер/сообщение/спрайт по факту кода брика.
- Экшен — фактический класс из `content/actions` через `ActionFactory.create*` (резолв по `ActionFactory.java`); у ScriptBrick экшена нет.
- Описание — 1–2 строки строго по `BrickInfo.java` + факт кода; домыслов нет.

Всего бриков в этом файле: 63 (заголовки подгрупп не считаются).

## Ожидание и задержки / Waiting & Delays (`subcategory_control_waiting`)

### 1. `WaitBrick`

| Поле | Значение |
|---|---|
| Название RU | Ждать |
| Название EN | Wait |
| Ключ строки / layout | `brick_wait` / `brick_wait` |
| Параметры | Formula: TIME_TO_WAIT_IN_SECONDS |
| Дефолт в палитре | BrickValues.WAIT |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/WaitBrick.java:33` |
| Класс экшена | WaitAction (catroid/src/main/java/org/catrobat/catroid/content/actions/WaitAction.java:33) — через createWaitAction |
| Описание | Приостанавливает выполнение на определенное время. ВАЖНО: игнорируется в асинхронном циклеКод: создаёт `WaitAction` в `addActionToSequence`. |

### 2. `WaitFramesBrick`

| Поле | Значение |
|---|---|
| Название RU | Ждать |
| Название EN | Wait |
| Ключ строки / layout | `brick_wait_frames` / `brick_wait_frames` |
| Параметры | Formula: NUMBER |
| Дефолт в палитре | 1 |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/WaitFramesBrick.java:30` |
| Класс экшена | WaitFramesAction (catroid/src/main/java/org/catrobat/catroid/content/actions/WaitFramesAction.kt:29) — через createWaitFramesAction |
| Описание | Нет в `BrickInfo.java`. Факт кода: создаёт `WaitFramesAction`. |

### 3. `WaitUntilBrick`

| Поле | Значение |
|---|---|
| Название RU | Ждать, пока |
| Название EN | Wait until |
| Ключ строки / layout | `brick_wait_until` / `brick_wait_until` |
| Параметры | Formula: IF_CONDITION |
| Дефолт в палитре | условие 1 < 2 |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/WaitUntilBrick.java:30` |
| Класс экшена | WaitUntilAction (catroid/src/main/java/org/catrobat/catroid/content/actions/WaitUntilAction.java:33) — через createWaitUntilAction |
| Описание | Приостанавливает выполнение, пока условие не станет истиннымКод: создаёт `WaitUntilAction` в `addActionToSequence`. |

### 4. `WaitWhileBrick`

| Поле | Значение |
|---|---|
| Название RU | Ждать, пока |
| Название EN | Wait until |
| Ключ строки / layout | `brick_wait_until` / `brick_wait_while` |
| Параметры | Formula: IF_CONDITION |
| Дефолт в палитре | условие 1 < 2 |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/WaitWhileBrick.java:30` |
| Класс экшена | WaitWhileAction (catroid/src/main/java/org/catrobat/catroid/content/actions/WaitWhileAction.java:33) — через createWaitWhileAction |
| Описание | Приостанавливает выполнение, пока условие истинно (продолжает, когда становится ложным)Код: создаёт `WaitWhileAction` в `addActionToSequence`. |

### 5. `WaitTillIdleBrick`

| Поле | Значение |
|---|---|
| Название RU | Ждать остановки всех других скриптов |
| Название EN | Wait until all other scripts have stopped |
| Ключ строки / layout | `brick_wait_till_idle` / `brick_wait_till_idle` |
| Параметры | — (без формульных полей) |
| Дефолт в палитре | — |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/WaitTillIdleBrick.java:29` |
| Класс экшена | WaitTillIdleAction (catroid/src/main/java/org/catrobat/catroid/content/actions/WaitTillIdleAction.java:33) — через createWaitTillIdleAction |
| Описание | Ждет, пока во всей сцене остановятся все скриптыКод: создаёт `WaitTillIdleAction` в `addActionToSequence`. |

### 6. `NoteBrick`

| Поле | Значение |
|---|---|
| Название RU | — |
| Название EN | — |
| Ключ строки / layout | `brick_note` / `brick_note` |
| Параметры | Formula: NOTE |
| Дефолт в палитре | brick_note_default_value |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/NoteBrick.java:32` |
| Класс экшена | — (ScriptBrick: addActionToSequence пустой, экшена нет) |
| Описание | Ничего не делает :/ используйте его для своих заметок.Код: событийный ScriptBrick, `addActionToSequence` пустой. |

## Циклы / Loops (`subcategory_control_loops`)

### 7. `ForeverBrick`

| Поле | Значение |
|---|---|
| Название RU | Вечно повторять |
| Название EN | Forever |
| Ключ строки / layout | `brick_forever` / `brick_forever` |
| Параметры | — (без формульных полей) |
| Дефолт в палитре | — |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/ForeverBrick.java:37` |
| Класс экшена | RepeatAction (catroid/src/main/java/org/catrobat/catroid/content/actions/RepeatAction.kt:30) — через createScriptSequenceAction, createForeverAction |
| Описание | Вечно повторяет блоки внутри, пока его что-нибудь не остановит.Код: создаёт `RepeatAction` в `addActionToSequence`. |

### 8. `RepeatBrick`

| Поле | Значение |
|---|---|
| Название RU | Повторять |
| Название EN | Repeat |
| Ключ строки / layout | `brick_repeat` / `brick_repeat` |
| Параметры | Formula: TIMES_TO_REPEAT |
| Дефолт в палитре | BrickValues.REPEAT |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/RepeatBrick.java:47` |
| Класс экшена | RepeatAction (catroid/src/main/java/org/catrobat/catroid/content/actions/RepeatAction.kt:30) — через createScriptSequenceAction, createRepeatAction |
| Описание | Повторяет блоки несколько разКод: создаёт `RepeatAction` в `addActionToSequence`. |

### 9. `RepeatUntilBrick`

| Поле | Значение |
|---|---|
| Название RU | Повторять, пока |
| Название EN | Repeat until |
| Ключ строки / layout | `brick_repeat_until` / `brick_repeat_until` |
| Параметры | Formula: REPEAT_UNTIL_CONDITION |
| Дефолт в палитре | условие 1 < 2 |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/RepeatUntilBrick.java:38` |
| Класс экшена | RepeatUntilAction (catroid/src/main/java/org/catrobat/catroid/content/actions/RepeatUntilAction.kt:30) — через createScriptSequenceAction, createRepeatUntilAction |
| Описание | Повторяет блоки, пока условие ложноКод: создаёт `RepeatUntilAction` в `addActionToSequence`. |

### 10. `RepeatWhileBrick`

| Поле | Значение |
|---|---|
| Название RU | Повторять, пока |
| Название EN | Repeat until |
| Ключ строки / layout | `brick_repeat_until` / `brick_repeat_while` |
| Параметры | Formula: REPEAT_UNTIL_CONDITION |
| Дефолт в палитре | условие 1 < 2 |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/RepeatWhileBrick.java:38` |
| Класс экшена | RepeatWhileAction (catroid/src/main/java/org/catrobat/catroid/content/actions/RepeatWhileAction.kt:30) — через createScriptSequenceAction, createRepeatWhileAction |
| Описание | Повторяет блоки, пока условие истинно (останавливается, когда становится ложным)Код: создаёт `RepeatWhileAction` в `addActionToSequence`. |

### 11. `AsyncRepeatBrick`

| Поле | Значение |
|---|---|
| Название RU | Асинхронно повторить |
| Название EN | Async repeat |
| Ключ строки / layout | `brick_async_repeat` / `brick_async_repeat` |
| Параметры | Formula: TIMES_TO_REPEAT |
| Дефолт в палитре | Formula(3) |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/AsyncRepeatBrick.java:13` |
| Класс экшена | AsyncRepeatAction (catroid/src/main/java/org/catrobat/catroid/content/actions/AsyncRepeatAction.kt:30) — через createScriptSequenceAction, createAsyncRepeatAction |
| Описание | Повторяет блоки без задержек. ВАЖНО: плохо работает. игнорирует блоки \Ждать\Код: создаёт `AsyncRepeatAction` в `addActionToSequence`. |

### 12. `IntervalRepeatBrick`

| Поле | Значение |
|---|---|
| Название RU | Повторять с интервалом |
| Название EN | Repeat with interval |
| Ключ строки / layout | `brick_interval_repeat` / `brick_interval_repeat` |
| Параметры | Formula: TIMES_TO_REPEAT, INTERVAL |
| Дефолт в палитре | Formula(3), Formula(1) |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/IntervalRepeatBrick.java:12` |
| Класс экшена | IntervalRepeatAction (catroid/src/main/java/org/catrobat/catroid/content/actions/IntervalRepeatAction.kt:8) — через createScriptSequenceAction, createIntervalRepeatAction |
| Описание | Повторяет блоки с определенным интерваломКод: создаёт `IntervalRepeatAction` в `addActionToSequence`. |

### 13. `ForVariableFromToBrick`

| Поле | Значение |
|---|---|
| Название RU | Для значений от |
| Название EN | For values from |
| Ключ строки / layout | `brick_loop_for_values_from` / `brick_for_variable_from_to` |
| Параметры | Formula: FOR_LOOP_FROM, FOR_LOOP_TO; спиннер (выбор из списка в UI) |
| Дефолт в палитре | FOR_LOOP_FROM..TO |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/ForVariableFromToBrick.java:40` |
| Класс экшена | ForVariableFromToAction (catroid/src/main/java/org/catrobat/catroid/content/actions/ForVariableFromToAction.java:33) — через createScriptSequenceAction, createForVariableFromToAction |
| Описание | Повторяет блоки для каждого значения в диапозоне. в переменную записывается текущее значение.Код: создаёт `ForVariableFromToAction` в `addActionToSequence`. |

### 14. `ForItemInUserListBrick`

| Поле | Значение |
|---|---|
| Название RU | Для каждого значения от |
| Название EN | For each value from |
| Ключ строки / layout | `brick_loop_for_each_value_from` / `brick_for_item_in_userlist` |
| Параметры | — (без формульных полей) |
| Дефолт в палитре | — |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/ForItemInUserListBrick.java:40` |
| Класс экшена | ForItemInUserListAction (catroid/src/main/java/org/catrobat/catroid/content/actions/ForItemInUserListAction.java:31) — через createScriptSequenceAction, createForItemInUserListAction |
| Описание | Повторяет блоки для каждого элемента в списке. в переменную записывается значение текущего элементаКод: создаёт `ForItemInUserListAction` в `addActionToSequence`. |

## Условия / Conditions (`subcategory_control_conditions`)

### 15. `IfLogicBeginBrick`

| Поле | Значение |
|---|---|
| Название RU | Если |
| Название EN | If |
| Ключ строки / layout | `brick_if_begin` / `brick_if_begin_if` |
| Параметры | Formula: IF_CONDITION |
| Дефолт в палитре | условие 1 < 2 |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/IfLogicBeginBrick.java:21` |
| Класс экшена | IfLogicAction (catroid/src/main/java/org/catrobat/catroid/content/actions/IfLogicAction.kt:32) — через createScriptSequenceAction, createIfLogicAction |
| Описание | Выполняет блоки внутри, если условие истинно, иначе выполняет блоки в ветке \иначе\.Код: создаёт `IfLogicAction` в `addActionToSequence`. |

### 16. `IfThenLogicBeginBrick`

| Поле | Значение |
|---|---|
| Название RU | Если |
| Название EN | If |
| Ключ строки / layout | `brick_if_begin` / `brick_if_then_begin_if` |
| Параметры | Formula: IF_CONDITION |
| Дефолт в палитре | условие 1 < 2 |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/IfThenLogicBeginBrick.java:40` |
| Класс экшена | IfLogicAction (catroid/src/main/java/org/catrobat/catroid/content/actions/IfLogicAction.kt:32) — через createScriptSequenceAction, createIfLogicAction |
| Описание | Выполняет блоки внутри, если условие истинно.Код: создаёт `IfLogicAction` в `addActionToSequence`. |

### 17. `CooldownBrick`

| Поле | Значение |
|---|---|
| Название RU | Кулдаун |
| Название EN | Cooldown |
| Ключ строки / layout | `brick_cooldown` / `brick_cooldown` |
| Параметры | Formula: TIME |
| Дефолт в палитре | 1.0 |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/CooldownBrick.java:37` |
| Класс экшена | CooldownAction (catroid/src/main/java/org/catrobat/catroid/content/actions/CooldownAction.java:32) — через createScriptSequenceAction, createCooldownAction |
| Описание | Нет в `BrickInfo.java`. Факт кода: создаёт `CooldownAction`. |

### 18. `TryCatchFinallyBrick`

| Поле | Значение |
|---|---|
| Название RU | Попробовать |
| Название EN | Try |
| Ключ строки / layout | `brick_try` / `brick_try` |
| Параметры | — (без формульных полей); спиннер (выбор из списка в UI) |
| Дефолт в палитре | — |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/TryCatchFinallyBrick.java:12` |
| Класс экшена | TryCatchFinallyAction (catroid/src/main/java/org/catrobat/catroid/content/actions/TryCatchFinallyAction.kt:7); ScriptSequenceAction (catroid/src/main/java/org/catrobat/catroid/content/actions/ScriptSequenceAction.java:34) — через createTryCatchFinallyAction |
| Описание | Пробует выполнить действие, при фатальной ошибке - перехватывает ее. В конце вызывает блокиКод: создаёт `TryCatchFinallyAction` в `addActionToSequence` (+ ещё 1). |

### 19. `PhiroIfLogicBeginBrick` — только если Phiro включён в настройках

| Поле | Значение |
|---|---|
| Название RU | Если Phiro |
| Название EN | If Phiro |
| Ключ строки / layout | `brick_phiro_sensor_begin` / `brick_phiro_if_sensor` |
| Параметры | — (без формульных полей); спиннер (выбор из списка в UI) |
| Дефолт в палитре | — |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/PhiroIfLogicBeginBrick.java:45` |
| Класс экшена | PhiroSensorAction (catroid/src/main/java/org/catrobat/catroid/content/actions/PhiroSensorAction.java:36) — через createScriptSequenceAction, createPhiroSendSelectedSensorAction |
| Описание | Проверяет выбранный сенсор робота Phiro: если он сработал - выполняет блоки внутри, иначе выполняет блоки ветки \иначе\. ВАЖНО: требует подключения по Bluetooth.Код: создаёт `PhiroSensorAction` в `addActionToSequence`. |

## Сцены и проекты / Scenes & Projects (`subcategory_control_scenes`)

### 20. `SceneStartBrick`

| Поле | Значение |
|---|---|
| Название RU | Запустить сцену |
| Название EN | Start scene |
| Ключ строки / layout | `brick_scene_start` / `brick_scene_start` |
| Параметры | конструктор: String sceneToStart; спиннер (выбор из списка в UI) |
| Дефолт в палитре | (null) |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/SceneStartBrick.java:49` |
| Класс экшена | SceneStartAction (catroid/src/main/java/org/catrobat/catroid/content/actions/SceneStartAction.kt:30) — через createSceneStartAction |
| Описание | Запускает сценуКод: создаёт `SceneStartAction` в `addActionToSequence`. |

### 21. `SceneIdBrick`

| Поле | Значение |
|---|---|
| Название RU | Запустить сцену под номером |
| Название EN | Start scene with id |
| Ключ строки / layout | `brick_scene_id` / `brick_scene_id` |
| Параметры | Formula: SCENE_ID |
| Дефолт в палитре | "1" |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/SceneIdBrick.java:36` |
| Класс экшена | SceneIdAction (catroid/src/main/java/org/catrobat/catroid/content/actions/SceneIdAction.kt:29) — через createSceneIdAction |
| Описание | Запускает сцену под ее номеромКод: создаёт `SceneIdAction` в `addActionToSequence`. |

### 22. `SceneTransitionBrick`

| Поле | Значение |
|---|---|
| Название RU | Продолжить сцену |
| Название EN | Continue scene |
| Ключ строки / layout | `brick_scene_transition` / `brick_scene_transition` |
| Параметры | конструктор: String scene; спиннер (выбор из списка в UI) |
| Дефолт в палитре | (null) |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/SceneTransitionBrick.java:49` |
| Класс экшена | SceneTransitionAction (catroid/src/main/java/org/catrobat/catroid/content/actions/SceneTransitionAction.java:34) — через createSceneTransitionAction |
| Описание | Продолжает сцену с того момента, на котором она закончилась. ВАЖНО: не работает, если сохранение сцен выключеноКод: создаёт `SceneTransitionAction` в `addActionToSequence`. |

### 23. `SceneBackBrick`

| Поле | Значение |
|---|---|
| Название RU | Вернуться к предыдущей сцене |
| Название EN | Go to previous scene |
| Ключ строки / layout | `brick_scene_back` / `brick_scene_back` |
| Параметры | — (без формульных полей) |
| Дефолт в палитре | — |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/SceneBackBrick.java:7` |
| Класс экшена | SceneBackAction (catroid/src/main/java/org/catrobat/catroid/content/actions/SceneBackAction.kt:8) — через createSceneBackAction |
| Описание | Возвращает на предыдущую сцену.Код: создаёт `SceneBackAction` в `addActionToSequence`. |

### 24. `ClearSceneBrick`

| Поле | Значение |
|---|---|
| Название RU | Очистить кэш сцены |
| Название EN | Clear scene cache |
| Ключ строки / layout | `brick_clear_scene` / `brick_clear_scene` |
| Параметры | конструктор: String sceneToStart; спиннер (выбор из списка в UI) |
| Дефолт в палитре | (null) |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/ClearSceneBrick.java:50` |
| Класс экшена | ClearSceneAction (catroid/src/main/java/org/catrobat/catroid/content/actions/ClearSceneAction.kt:30) — через createClearSceneAction |
| Описание | Очищает кэш сцены ВАЖНО: после этого ее нельзя будет продолжить.Код: создаёт `ClearSceneAction` в `addActionToSequence`. |

### 25. `SetSaveScenesBrick`

| Поле | Значение |
|---|---|
| Название RU | Задать сохранение сцен |
| Название EN | Set "Save scenes" to |
| Ключ строки / layout | `brick_set_save_scenes` / `brick_set_save_scenes` |
| Параметры | Formula: VALUE |
| Дефолт в палитре | 1 |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/SetSaveScenesBrick.java:32` |
| Класс экшена | SetSaveScenesAction (catroid/src/main/java/org/catrobat/catroid/content/actions/SetSaveScenesAction.kt:41) — через createSetSaveScenesAction |
| Описание | Определяет сохранять ли сцены.Код: создаёт `SetSaveScenesAction` в `addActionToSequence`. |

### 26. `SetStopSoundsBrick`

| Поле | Значение |
|---|---|
| Название RU | Задать остановку звуков |
| Название EN | Set "Stop Sounds" to |
| Ключ строки / layout | `brick_set_stop_sounds` / `brick_set_stop_sounds` |
| Параметры | Formula: VALUE |
| Дефолт в палитре | 1 |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/SetStopSoundsBrick.java:32` |
| Класс экшена | SetStopSoundsAction (catroid/src/main/java/org/catrobat/catroid/content/actions/SetStopSoundsAction.kt:41) — через createSetStopSoundsAction |
| Описание | Определяет остановку звуков, при переходе на другую сценуКод: создаёт `SetStopSoundsAction` в `addActionToSequence`. |

### 27. `SetPreloadingBrick`

| Поле | Значение |
|---|---|
| Название RU | Включить предзагрузку проекта |
| Название EN | Enable project preloading |
| Ключ строки / layout | `brick_set_preloading` / `brick_set_preloading` |
| Параметры | конструктор: int enabled |
| Дефолт в палитре | 1 |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/SetPreloadingBrick.java:7` |
| Класс экшена | — (createSetPreloadingAction (в ActionFactory класс *Action не найден)) |
| Описание | Включает или выключает предзагрузку проекта. |

### 28. `LaunchProjectBrick`

| Поле | Значение |
|---|---|
| Название RU | Запустить проект из файла |
| Название EN | Launch project from file |
| Ключ строки / layout | `brick_launch_project` / `brick_launch_project` |
| Параметры | Formula: PROJECT_NAME |
| Дефолт в палитре | "project.neotrobat" |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/LaunchProjectBrick.java:9` |
| Класс экшена | LaunchProjectAction (catroid/src/main/java/org/catrobat/catroid/content/actions/LaunchProjectAction.kt:15) — через createLaunchProjectAction |
| Описание | Запускает проект из файлов проекта, останавливает текущийКод: создаёт `LaunchProjectAction` в `addActionToSequence`. |

### 29. `ReturnToPreviousProjectBrick`

| Поле | Значение |
|---|---|
| Название RU | Вернуться к пред. проекту |
| Название EN | Return to previous project |
| Ключ строки / layout | `brick_return_to_previous_project` / `brick_return_to_previous_project` |
| Параметры | — (без формульных полей) |
| Дефолт в палитре | — |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/ReturnToPreviousProjectBrick.java:7` |
| Класс экшена | ReturnToPreviousProjectAction (catroid/src/main/java/org/catrobat/catroid/content/actions/ReturnToPreviousProjectAction.kt:10) — через createReturnToPreviousProjectAction |
| Описание | Возвращается к предыдущему проекту, откуда был запущен этот ВАЖНО: плохо работаетКод: создаёт `ReturnToPreviousProjectAction` в `addActionToSequence`. |

## Остановка и ошибки / Stopping & Safety (`subcategory_control_stopping`)

### 30. `StopScriptBrick`

| Поле | Значение |
|---|---|
| Название RU | Остановить |
| Название EN | Stop |
| Ключ строки / layout | `brick_stop_script` / `brick_stop_script` |
| Параметры | конструктор: int spinnerSelection; спиннер (выбор из списка в UI) |
| Дефолт в палитре | STOP_THIS_SCRIPT |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/StopScriptBrick.java:39` |
| Класс экшена | StopThisScriptAction (catroid/src/main/java/org/catrobat/catroid/content/actions/StopThisScriptAction.java:31); StopOtherScriptsAction (catroid/src/main/java/org/catrobat/catroid/content/actions/StopOtherScriptsAction.java:35); StopAllScriptsAction (catroid/src/main/java/org/catrobat/catroid/content/actions/StopAllScriptsAction.java:33) — через createStopScriptAction |
| Описание | Останавливает выбранный скрипт (-ы)Код: создаёт `StopThisScriptAction` в `addActionToSequence` (+ ещё 2). |

### 31. `ExitStageBrick`

| Поле | Значение |
|---|---|
| Название RU | Выйти из проекта |
| Название EN | Finish stage |
| Ключ строки / layout | `brick_finish_stage` / `brick_exit_stage` |
| Параметры | — (без формульных полей) |
| Дефолт в палитре | — |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/ExitStageBrick.java:29` |
| Класс экшена | FinishStageAction (catroid/src/main/java/org/catrobat/catroid/content/actions/FinishStageAction.kt:29) — через createFinishStageAction |
| Описание | Завершает выполнение сцены и выходит из Stage.Код: создаёт `FinishStageAction` в `addActionToSequence`. |

## Клонирование / Cloning (`subcategory_control_cloning`)

### 32. `WhenClonedBrick`

| Поле | Значение |
|---|---|
| Название RU | Когда начинает как клон |
| Название EN | When you start as a clone |
| Ключ строки / layout | `brick_when_cloned` / `brick_when_cloned` |
| Параметры | конструктор: WhenClonedScript script |
| Дефолт в палитре | — |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/WhenClonedBrick.java:31` |
| Класс экшена | — (ScriptBrick: addActionToSequence пустой, экшена нет) |
| Описание | Вызывается ТОЛЬКО для клона, сразу после его клорования.Код: событийный ScriptBrick, `addActionToSequence` пустой. |

### 33. `CloneBrick`

| Поле | Значение |
|---|---|
| Название RU | Клонировать |
| Название EN | Create clone of |
| Ключ строки / layout | `brick_clone` / `brick_clone` |
| Параметры | — (без формульных полей); спиннер (выбор из списка в UI) |
| Дефолт в палитре | — |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/CloneBrick.java:42` |
| Класс экшена | CloneAction (catroid/src/main/java/org/catrobat/catroid/content/actions/CloneAction.kt:29) — через createCloneAction |
| Описание | Клонирует текущий спрайт. ВАЖНО: включая все его переменные, трансформации и образы.Код: создаёт `CloneAction` в `addActionToSequence`. |

### 34. `CloneAndNameBrick`

| Поле | Значение |
|---|---|
| Название RU | Клонировать и назвать |
| Название EN | Clone and name |
| Ключ строки / layout | `brick_clone_and_name` / `brick_clone_and_name` |
| Параметры | Formula: CLONE_NAME; спиннер (выбор из списка в UI) |
| Дефолт в палитре | "clone" |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/CloneAndNameBrick.java:22` |
| Класс экшена | CloneAndNameAction (catroid/src/main/java/org/catrobat/catroid/content/actions/CloneAndNameAction.kt:9) — через createCloneAndNameAction |
| Описание | Клонирует текущий спрайт и задает ему имя. ВАЖНО: копирует все его переменные, трансформации и образы.Код: создаёт `CloneAndNameAction` в `addActionToSequence`. |

### 35. `DeleteThisCloneBrick`

| Поле | Значение |
|---|---|
| Название RU | Удалить этот клон |
| Название EN | Delete this clone |
| Ключ строки / layout | `brick_delete_this_clone` / `brick_delete_this_clone` |
| Параметры | — (без формульных полей) |
| Дефолт в палитре | — |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/DeleteThisCloneBrick.java:29` |
| Класс экшена | DeleteThisCloneAction (catroid/src/main/java/org/catrobat/catroid/content/actions/DeleteThisCloneAction.java:31) — через createDeleteThisCloneAction |
| Описание | Удаляет текущий клон. ВАЖНО: только для клона, не для оригинального спрайта.Код: создаёт `DeleteThisCloneAction` в `addActionToSequence`. |

### 36. `DeleteCloneByNumberBrick`

| Поле | Значение |
|---|---|
| Название RU | Удалить клон |
| Название EN | Delete clone |
| Ключ строки / layout | `brick_delete_clone_by_number` / `brick_delete_clone_by_number` |
| Параметры | Formula: NUMBER |
| Дефолт в палитре | 1 |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/DeleteCloneByNumberBrick.java:8` |
| Класс экшена | DeleteCloneByNumberAction (catroid/src/main/java/org/catrobat/catroid/content/actions/DeleteCloneByNumberAction.kt:8) — через createDeleteCloneByNumberAction |
| Описание | Удаляет клон текущего спрайта по его номеру (cloneIndex). 0 — оригинал, 1+ — клоны.Код: создаёт `DeleteCloneByNumberAction` в `addActionToSequence`. |

### 37. `ExecuteForCloneNumberBrick`

| Поле | Значение |
|---|---|
| Название RU | Выполнить для номера клона |
| Название EN | Execute for clone number |
| Ключ строки / layout | `brick_execute_for_clone_number` / `brick_execute_for_clone_number` |
| Параметры | Formula: NUMBER |
| Дефолт в палитре | 1 |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/ExecuteForCloneNumberBrick.java:15` |
| Класс экшена | ExecuteForCloneNumberAction (catroid/src/main/java/org/catrobat/catroid/content/actions/ExecuteForCloneNumberAction.java:12) — через createScriptSequenceAction, createExecuteForCloneNumberAction |
| Описание | Выполняет вложенные блоки только для клона с заданным номером (cloneIndex). ВАЖНО: номер сравнивается с полем cloneIndex клона; для оригинала оно равно 0.Код: создаёт `ExecuteForCloneNumberAction` в `addActionToSequence`. |

### 38. `RunAsSpriteBrick`

| Поле | Значение |
|---|---|
| Название RU | Выполнить от имени |
| Название EN | Run on behalf |
| Ключ строки / layout | `brick_run_as_sprite_title` / `brick_run_as_sprite` |
| Параметры | Formula: NAME |
| Дефолт в палитре | Formula("Sprite") |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/RunAsSpriteBrick.java:19` |
| Класс экшена | — (createRunAsSpriteAction (в ActionFactory класс *Action не найден)) |
| Описание | Запускает блоки внутри от имени определенного спрайта |

## Машина состояний / State Machine (`subcategory_control_state_machine`)

### 39. `SetStateBrick`

| Поле | Значение |
|---|---|
| Название RU | Задать состояние |
| Название EN | Set state of |
| Ключ строки / layout | `brick_set_state` / `brick_set_state` |
| Параметры | Formula: NAME, STRING |
| Дефолт в палитре | "ai", "idle" |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/SetStateBrick.java:8` |
| Класс экшена | SetStateAction (catroid/src/main/java/org/catrobat/catroid/content/actions/SetStateAction.kt:8) — через createSetStateAction |
| Описание | Переводит машину состояний спрайта в указанное состояние.Код: создаёт `SetStateAction` в `addActionToSequence`. |

### 40. `IfInStateBrick`

| Поле | Значение |
|---|---|
| Название RU | Если состояние |
| Название EN | If state of |
| Ключ строки / layout | `brick_if_in_state` / `brick_if_in_state` |
| Параметры | Formula: NAME, STRING |
| Дефолт в палитре | "ai", "idle" |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/IfInStateBrick.java:15` |
| Класс экшена | IfInStateAction (catroid/src/main/java/org/catrobat/catroid/content/actions/IfInStateAction.java:13) — через createScriptSequenceAction, createIfInStateAction |
| Описание | Если машина состояний спрайта находится в указанном состоянии, выполняет блоки внутри.Код: создаёт `IfInStateAction` в `addActionToSequence`. |

## События и сообщения / Events & Messages (`subcategory_control_messages`)

### 41. `BroadcastReceiverBrick`

| Поле | Значение |
|---|---|
| Название RU | Когда будет получено |
| Название EN | When you receive |
| Ключ строки / layout | `brick_broadcast_receive` / `brick_broadcast_receive` |
| Параметры | сообщение broadcastMessage (String, выбор из BroadcastMessageContainer); сообщение (BroadcastScript) |
| Дефолт в палитре | первое сообщение проекта |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/BroadcastReceiverBrick.java:33` |
| Класс экшена | — (ScriptBrick: addActionToSequence пустой, экшена нет) |
| Описание | Вызывается, когда любой спрайт в этой сцене отправит выбранное сообщение.Код: событийный ScriptBrick, `addActionToSequence` пустой. |

### 42. `BroadcastWithParamsReceiverBrick`

| Поле | Значение |
|---|---|
| Название RU | получить |
| Название EN | receive |
| Ключ строки / layout | `brick_broadcast_with_params_receive` / `brick_broadcast_with_params_receive` |
| Параметры | Formula: VALUE_1; спиннер (выбор из списка в UI) |
| Дефолт в палитре | — |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/BroadcastWithParamsReceiverBrick.java:16` |
| Класс экшена | — (ScriptBrick: addActionToSequence пустой, экшена нет) |
| Описание | Вызывается при получении сообщения с данными и сохраняет их в выбранную переменную.Код: событийный ScriptBrick, `addActionToSequence` пустой. |

### 43. `BroadcastBrick`

| Поле | Значение |
|---|---|
| Название RU | Вещать всем |
| Название EN | Broadcast |
| Ключ строки / layout | `brick_broadcast` / `brick_broadcast` |
| Параметры | сообщение broadcastMessage (String, выбор из BroadcastMessageContainer) |
| Дефолт в палитре | то же сообщение |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/BroadcastBrick.java:29` |
| Класс экшена | BroadcastAction (catroid/src/main/java/org/catrobat/catroid/content/actions/BroadcastAction.kt:5) — через createBroadcastAction |
| Описание | Отправляет выбранное сообщениеКод: создаёт `BroadcastAction` в `addActionToSequence`. |

### 44. `BroadcastWaitBrick`

| Поле | Значение |
|---|---|
| Название RU | Вещать всем и ждать |
| Название EN | Broadcast and wait |
| Ключ строки / layout | `brick_broadcast_wait` / `brick_broadcast_wait` |
| Параметры | сообщение broadcastMessage (String, выбор из BroadcastMessageContainer) |
| Дефолт в палитре | то же сообщение |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/BroadcastWaitBrick.java:29` |
| Класс экшена | BroadcastAction (catroid/src/main/java/org/catrobat/catroid/content/actions/BroadcastAction.kt:5) — через createBroadcastAction |
| Описание | Отправляет выбранное сообщение и ждет, пока все получатели завершат действияКод: создаёт `BroadcastAction` в `addActionToSequence`. |

### 45. `BroadcastWithParamsBrick`

| Поле | Значение |
|---|---|
| Название RU | Отправить с параметрами |
| Название EN | Broadcast with params |
| Ключ строки / layout | `brick_broadcast_with_params` / `brick_broadcast_with_params` |
| Параметры | Formula: VALUE_1, VALUE_2 |
| Дефолт в палитре | "signal", "data" |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/BroadcastWithParamsBrick.java:8` |
| Класс экшена | BroadcastWithParamsAction (catroid/src/main/java/org/catrobat/catroid/content/actions/BroadcastWithParamsAction.kt:10) — через createBroadcastWithParamsAction |
| Описание | Отправляет сообщение с данными. ВАЖНО: принимает его блок «Принять сообщение» с параметрами.Код: создаёт `BroadcastWithParamsAction` в `addActionToSequence`. |

### 46. `SetNfcTagBrick` — только если NFC включён

| Поле | Значение |
|---|---|
| Название RU | Записать на NFC метку |
| Название EN | Wait for next NFC tag to write |
| Ключ строки / layout | `brick_set_nfc_tag` / `brick_set_nfc_tag` |
| Параметры | Formula: NFC_NDEF_MESSAGE; спиннер (выбор из списка в UI) |
| Дефолт в палитре | brick_set_nfc_tag_default_value |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/SetNfcTagBrick.java:41` |
| Класс экшена | SetNfcTagAction (catroid/src/main/java/org/catrobat/catroid/content/actions/SetNfcTagAction.java:35) — через createSetNfcTagAction |
| Описание | Ожидает следующий NFC-тег и записывает в него сообщение (NDEF) выбранного типа. ВАЖНО: требуется NFC-адаптер устройства.Код: создаёт `SetNfcTagAction` в `addActionToSequence`. |

## Эмуляция касаний / Touch Emulation (`subcategory_control_touches`)

### 47. `TapAtBrick`

| Поле | Значение |
|---|---|
| Название RU | "Нажать один раз в" |
| Название EN | "Single tap at" |
| Ключ строки / layout | `brick_tap_at` / `brick_tap_at` |
| Параметры | Formula: X_POSITION, Y_POSITION |
| Дефолт в палитре | TOUCH_X_START/Y_START |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/TapAtBrick.java:30` |
| Класс экшена | TapAtAction (catroid/src/main/java/org/catrobat/catroid/content/actions/TapAtAction.kt:34) — через createTapAtAction |
| Описание | Имитирует одиночное нажатие (тап) в указанных координатах экрана. Можно использовать для автокликов.Код: создаёт `TapAtAction` в `addActionToSequence`. |

### 48. `TapForBrick`

| Поле | Значение |
|---|---|
| Название RU | "Нажать в" |
| Название EN | "Touch at" |
| Ключ строки / layout | `brick_touch_at` / `brick_tap_for` |
| Параметры | Formula: X_POSITION, Y_POSITION, DURATION_IN_SECONDS |
| Дефолт в палитре | + TOUCH_DURATION |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/TapForBrick.kt:33` |
| Класс экшена | TapAtAction (catroid/src/main/java/org/catrobat/catroid/content/actions/TapAtAction.kt:34) — через createTapForAction |
| Описание | Нет в `BrickInfo.java`. Факт кода: создаёт `TapAtAction`. |

### 49. `TouchAndSlideBrick`

| Поле | Значение |
|---|---|
| Название RU | "Коснуться и перелистывать" |
| Название EN | "Touch and slide" |
| Ключ строки / layout | `brick_touch_slide` / `brick_touch_slide` |
| Параметры | Formula: X_POSITION, Y_POSITION, X_POSITION_CHANGE, Y_POSITION_CHANGE, DURATION_IN_SECONDS |
| Дефолт в палитре | X_START/Y_START/X_GOAL/Y_GOAL/DURATION |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/TouchAndSlideBrick.kt:33` |
| Класс экшена | TapAtAction (catroid/src/main/java/org/catrobat/catroid/content/actions/TapAtAction.kt:34) — через createTouchAndSlideAction |
| Описание | Нет в `BrickInfo.java`. Факт кода: создаёт `TapAtAction`. |

### 50. `OpenUrlBrick`

| Поле | Значение |
|---|---|
| Название RU | Открыть |
| Название EN | Open |
| Ключ строки / layout | `brick_open` / `brick_open_url` |
| Параметры | Formula: OPEN_URL |
| Дефолт в палитре | OPEN_IN_BROWSER |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/OpenUrlBrick.kt:33` |
| Класс экшена | OpenUrlAction (catroid/src/main/java/org/catrobat/catroid/content/actions/OpenUrlAction.kt:33) — через createOpenUrlAction |
| Описание | Нет в `BrickInfo.java`. Факт кода: создаёт `OpenUrlAction`. |

## Циклы / Loops (`subcategory_control_loops`)

### 51. `CountLoopBrick`

| Поле | Значение |
|---|---|
| Название RU | повторить %s раз сохранить |
| Название EN | repeat %s times store |
| Ключ строки / layout | `brick_count_loop` / `brick_count_loop` |
| Параметры | Formula: TIMES_TO_REPEAT |
| Дефолт в палитре | Formula(0) |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/CountLoopBrick.java:38` |
| Класс экшена | CountLoopAction (catroid/src/main/java/org/catrobat/catroid/content/actions/CountLoopAction.kt:32) — через createScriptSequenceAction, createCountLoopAction |
| Описание | Повторяет блоки внутри определенное количество раз и сохраняет номер итерации в переменнуюКод: создаёт `CountLoopAction` в `addActionToSequence`. |

## Ожидание и задержки / Waiting & Delays (`subcategory_control_waiting`)

### 52. `DelayMicrosecondsBrick`

| Поле | Значение |
|---|---|
| Название RU | задержка |
| Название EN | delay |
| Ключ строки / layout | `brick_delay_microseconds` / `brick_delay_microseconds` |
| Параметры | Formula: TIME_TO_WAIT_IN_SECONDS |
| Дефолт в палитре | Formula(1.0) |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/DelayMicrosecondsBrick.java:33` |
| Класс экшена | DelayMicrosecondsAction (catroid/src/main/java/org/catrobat/catroid/content/actions/DelayMicrosecondsAction.kt:31) — через createDelayMicrosecondsAction |
| Описание | Приостанавливает выполнение на определенное количество микросекундКод: создаёт `DelayMicrosecondsAction` в `addActionToSequence`. |

### 53. `UpdateScreenBrick`

| Поле | Значение |
|---|---|
| Название RU | — |
| Название EN | Update screen |
| Ключ строки / layout | `brick_update_screen` / `brick_update_screen` |
| Параметры | — (без формульных полей) |
| Дефолт в палитре | — |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/UpdateScreenBrick.java:7` |
| Класс экшена | UpdateScreenAction (catroid/src/main/java/org/catrobat/catroid/content/actions/UpdateScreenAction.kt:6) — через createUpdateScreenAction |
| Описание | Принудительно обновляет экран (yield кадра).Код: создаёт `UpdateScreenAction` в `addActionToSequence`. |

## Условия / Conditions (`subcategory_control_conditions`)

### 54. `SwitchBeginBrick`

| Поле | Значение |
|---|---|
| Название RU | переключатель |
| Название EN | switch |
| Ключ строки / layout | `brick_switch_begin` / `brick_switch_begin` |
| Параметры | Formula: TEXT |
| Дефолт в палитре | Formula(0) |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/SwitchBeginBrick.java:36` |
| Класс экшена | SwitchCaseAction (catroid/src/main/java/org/catrobat/catroid/content/actions/SwitchCaseAction.kt:32); CaseAction (файл не найден — указан по факту кода) — через createScriptSequenceAction, createSwitchCaseAction |
| Описание | Проверяет выражение на совпадение с каждым значением Case и выполняет соответствующие блокиКод: создаёт `SwitchCaseAction` в `addActionToSequence` (+ ещё 1). |

### 55. `SwitchCaseBrick`

| Поле | Значение |
|---|---|
| Название RU | случай |
| Название EN | case |
| Ключ строки / layout | `brick_switch_case` / `brick_switch_case` |
| Параметры | Formula: TEXT |
| Дефолт в палитре | Formula(0) |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/SwitchCaseBrick.java:30` |
| Класс экшена | — (ScriptBrick: addActionToSequence пустой, экшена нет) |
| Описание | Задает значение для проверки внутри блока SwitchКод: событийный ScriptBrick, `addActionToSequence` пустой. |

## Циклы / Loops (`subcategory_control_loops`)

### 56. `SpawnThreadBrick`

| Поле | Значение |
|---|---|
| Название RU | Запустить поток |
| Название EN | Spawn thread |
| Ключ строки / layout | `brick_spawn_thread_title` / `brick_spawn_thread` |
| Параметры | Formula: IF_CONDITION |
| Дефолт в палитре | Formula("thread1") |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/SpawnThreadBrick.java:11` |
| Класс экшена | SpawnThreadAction (catroid/src/main/java/org/catrobat/catroid/content/actions/SpawnThreadAction.kt:10) — через createSpawnThreadAction |
| Описание | Запускает блоки внутри в отдельном потоке с указанным ID. ВАЖНО: выполняется параллельно с основным скриптом.Код: создаёт `SpawnThreadAction` в `addActionToSequence`. |

### 57. `StopThreadBrick`

| Поле | Значение |
|---|---|
| Название RU | Остановить поток |
| Название EN | Stop thread |
| Ключ строки / layout | `brick_stop_thread_title` / `brick_stop_thread` |
| Параметры | Formula: IF_CONDITION |
| Дефолт в палитре | Formula("thread1") |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/StopThreadBrick.java:8` |
| Класс экшена | StopThreadAction (catroid/src/main/java/org/catrobat/catroid/content/actions/StopThreadAction.kt:8) — через createStopThreadAction |
| Описание | Останавливает поток с указанным ID.Код: создаёт `StopThreadAction` в `addActionToSequence`. |

### 58. `WaitThreadBrick`

| Поле | Значение |
|---|---|
| Название RU | Ждать поток |
| Название EN | Wait for thread |
| Ключ строки / layout | `brick_wait_thread_title` / `brick_wait_thread` |
| Параметры | Formula: IF_CONDITION |
| Дефолт в палитре | Formula("thread1") |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/WaitThreadBrick.java:8` |
| Класс экшена | WaitThreadAction (catroid/src/main/java/org/catrobat/catroid/content/actions/WaitThreadAction.kt:8) — через createWaitThreadAction |
| Описание | Ждет, пока поток с указанным ID завершится.Код: создаёт `WaitThreadAction` в `addActionToSequence`. |

### 59. `InstantBrick`

| Поле | Значение |
|---|---|
| Название RU | Мгновенно |
| Название EN | Instant |
| Ключ строки / layout | `brick_instant` / `brick_instant` |
| Параметры | — (без формульных полей) |
| Дефолт в палитре | — |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/InstantBrick.java:11` |
| Класс экшена | InstantAction (catroid/src/main/java/org/catrobat/catroid/content/actions/InstantAction.kt:6) — через createScriptSequenceAction, createInstantAction |
| Описание | Служебный контейнерный блок: мгновенно выполняет вложенные блоки, не дожидаясь их завершения.Код: создаёт `InstantAction` в `addActionToSequence`. |

### 60. `WhenClonedWithNameBrick`

| Поле | Значение |
|---|---|
| Название RU | При клонировании по имени |
| Название EN | When cloned with name |
| Ключ строки / layout | `brick_when_cloned_with_name_title` / `brick_when_cloned_with_name` |
| Параметры | Formula: VALUE_1 |
| Дефолт в палитре | Formula("clone") |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/WhenClonedWithNameBrick.java:12` |
| Класс экшена | — (ScriptBrick: addActionToSequence пустой, экшена нет) |
| Описание | Вызывается для клона, имя которого совпадает с указанным.Код: событийный ScriptBrick, `addActionToSequence` пустой. |

### 61. `SetGameTimeScaleBrick`

| Поле | Значение |
|---|---|
| Название RU | Скорость игры %s |
| Название EN | Set game speed %s |
| Ключ строки / layout | `brick_set_game_time_scale` / `brick_set_game_time_scale` |
| Параметры | Formula: VALUE |
| Дефолт в палитре | 100f |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/SetGameTimeScaleBrick.java:8` |
| Класс экшена | SetGameTimeScaleAction (catroid/src/main/java/org/catrobat/catroid/content/actions/SetGameTimeScaleAction.kt:8) — через createSetGameTimeScaleAction |
| Описание | Нет в `BrickInfo.java`. Факт кода: создаёт `SetGameTimeScaleAction`. |

### 62. `SaveGameBrick`

| Поле | Значение |
|---|---|
| Название RU | Сохранить игру, слот |
| Название EN | Save game, slot |
| Ключ строки / layout | `brick_save_game` / `brick_save_game` |
| Параметры | Formula: VALUE |
| Дефолт в палитре | 1.0 |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/SaveGameBrick.java:8` |
| Класс экшена | SaveGameAction (catroid/src/main/java/org/catrobat/catroid/content/actions/SaveGameAction.kt:15) — через createSaveGameAction |
| Описание | Нет в `BrickInfo.java`. Факт кода: создаёт `SaveGameAction`. |

### 63. `LoadGameBrick`

| Поле | Значение |
|---|---|
| Название RU | Загрузить игру, слот |
| Название EN | Load game, slot |
| Ключ строки / layout | `brick_load_game` / `brick_load_game` |
| Параметры | Formula: VALUE |
| Дефолт в палитре | 1.0 |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/LoadGameBrick.java:8` |
| Класс экшена | LoadGameAction (catroid/src/main/java/org/catrobat/catroid/content/actions/LoadGameAction.kt:12) — через createLoadGameAction |
| Описание | Нет в `BrickInfo.java`. Факт кода: создаёт `LoadGameAction`. |

---
Проверено по: `CategoryBricksFactory.kt`, `content/BrickInfo.java`, классам `content/bricks/*`, `content/actions/*` (через `content/ActionFactory.java`), `res/values/strings.xml`, `res/values-ru/strings.xml`.
