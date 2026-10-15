# События / Event

Категория `category_event`: RU «События», EN «Event».

Источник состава и порядка: `catroid/src/main/java/org/catrobat/catroid/ui/fragment/CategoryBricksFactory.kt:1273`, метод `setupEventCategoryList` (сгруппированный список, ветка grouping enabled).

Правила документа:

- Порядок бриков строго по фабрике; подгруппы — `SubCategoryHeaderBrick` из того же метода.
- Условные брики помечены (фон/global-сцена/NFC/Phiro) — позиция сохранена по фабрике.
- Кастомный хвост из конца палитры не трогаем и сюда не включаем (только состав `setup*` выше).
- Названия RU/EN — по первому `@string` главного layout (`getViewResource`) из `catroid/src/main/res/values/strings.xml` и `values-ru/strings.xml`.
- Параметры — `addAllowedBrickField(BrickField.*)` + спиннер/сообщение/спрайт по факту кода брика.
- Экшен — фактический класс из `content/actions` через `ActionFactory.create*` (резолв по `ActionFactory.java`); у ScriptBrick экшена нет.
- Описание — 1–2 строки строго по `BrickInfo.java` + факт кода; домыслов нет.

Всего бриков в этом файле: 76 (заголовки подгрупп не считаются).

## Запуск и нажатия / Start & Touch (`subcategory_event_touch`)

### 1. `WhenStartedBrick`

| Поле | Значение |
|---|---|
| Название RU | При запуске сцены |
| Название EN | When scene starts |
| Ключ строки / layout | `brick_when_started` / `brick_when_started` |
| Параметры | конструктор: StartScript script |
| Дефолт в палитре | — |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/WhenStartedBrick.java:31` |
| Класс экшена | — (ScriptBrick: addActionToSequence пустой, экшена нет) |
| Описание | Вызывается при запуске сцены 1 раз.Код: событийный ScriptBrick, `addActionToSequence` пустой. |

### 2. `WhenSceneLaunchedBrick` — только если редактируемая сцена — global-сцена

| Поле | Значение |
|---|---|
| Название RU | При запуске сцены |
| Название EN | When scene starts |
| Ключ строки / layout | `brick_when_scene_launched` / `brick_when_scene_launched` |
| Параметры | конструктор: @NonNull WhenSceneLaunchedScript script; спиннер (выбор из списка в UI) |
| Дефолт в палитре | — |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/WhenSceneLaunchedBrick.java:22` |
| Класс экшена | — (ScriptBrick: addActionToSequence пустой, экшена нет) |
| Описание | Запускает скрипт, когда выбранная сцена запускается.Код: событийный ScriptBrick, `addActionToSequence` пустой. |

### 3. `WhenSceneExitedBrick` — только если редактируемая сцена — global-сцена

| Поле | Значение |
|---|---|
| Название RU | При выходе из сцены |
| Название EN | When leaving scene |
| Ключ строки / layout | `brick_when_scene_exited` / `brick_when_scene_exited` |
| Параметры | конструктор: @NonNull WhenSceneExitedScript script; спиннер (выбор из списка в UI) |
| Дефолт в палитре | — |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/WhenSceneExitedBrick.java:22` |
| Класс экшена | — (ScriptBrick: addActionToSequence пустой, экшена нет) |
| Описание | Запускает скрипт, когда выбранная сцена покидается (был выход из неё).Код: событийный ScriptBrick, `addActionToSequence` пустой. |

### 4. `WhenBrick`

| Поле | Значение |
|---|---|
| Название RU | При нажатии |
| Название EN | When tapped |
| Ключ строки / layout | `brick_when` / `brick_when` |
| Параметры | конструктор: @NonNull WhenScript script |
| Дефолт в палитре | — |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/WhenBrick.java:33` |
| Класс экшена | — (ScriptBrick: addActionToSequence пустой, экшена нет) |
| Описание | Вызывается при нажатии пальца на спрайт.Код: событийный ScriptBrick, `addActionToSequence` пустой. |

### 5. `WhenTouchDownBrick`

| Поле | Значение |
|---|---|
| Название RU | При нажатии на экран |
| Название EN | When stage is tapped |
| Ключ строки / layout | `brick_when_touched` / `brick_screen_touched` |
| Параметры | конструктор: WhenTouchDownScript script |
| Дефолт в палитре | — |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/WhenTouchDownBrick.java:31` |
| Класс экшена | — (ScriptBrick: addActionToSequence пустой, экшена нет) |
| Описание | Вызывается, когда палец касается экрана.Код: событийный ScriptBrick, `addActionToSequence` пустой. |

### 6. `WhenSwipedBrick`

| Поле | Значение |
|---|---|
| Название RU | Когда свайп |
| Название EN | When swiped |
| Ключ строки / layout | `brick_when_swiped` / `brick_when_swiped` |
| Параметры | направление свайпа (WhenSwipedScript); спиннер (выбор из списка в UI) |
| Дефолт в палитре | WhenSwipedScript(0) |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/WhenSwipedBrick.java:41` |
| Класс экшена | — (ScriptBrick: addActionToSequence пустой, экшена нет) |
| Описание | Вызывается, когда пользователь делает свайп в выбранном направлении.Код: событийный ScriptBrick, `addActionToSequence` пустой. |

### 7. `WhenVariableChangedBrick`

| Поле | Значение |
|---|---|
| Название RU | Когда переменная изменится |
| Название EN | When variable changes |
| Ключ строки / layout | `brick_when_variable_changed` / `brick_when_variable_changed` |
| Параметры | конструктор: WhenVariableChangedScript script; спиннер (выбор из списка в UI) |
| Дефолт в палитре | — |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/WhenVariableChangedBrick.java:28` |
| Класс экшена | WhenVariableChangedAction (catroid/src/main/java/org/catrobat/catroid/content/actions/WhenVariableChangedAction.kt:6) — через createScriptSequenceAction, createWhenVariableChangedAction |
| Описание | Срабатывает при изменении значения выбранной переменной.Код: создаёт `WhenVariableChangedAction` в `addActionToSequence`. |

### 8. `WhenBackPressedBrick`

| Поле | Значение |
|---|---|
| Название RU | При нажатии на "назад" |
| Название EN | When back pressed |
| Ключ строки / layout | `brick_when_back_pressed` / `brick_when_back_pressed` |
| Параметры | конструктор: BackPressedScript script |
| Дефолт в палитре | — |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/WhenBackPressedBrick.java:9` |
| Класс экшена | — (ScriptBrick: addActionToSequence пустой, экшена нет) |
| Описание | Вызывается, когда пользователь нажимает на кнопку \назад\ на устройстве.Код: событийный ScriptBrick, `addActionToSequence` пустой. |

### 9. `WhenButtonPressedBrick`

| Поле | Значение |
|---|---|
| Название RU | Когда кнопка громкости |
| Название EN | When volume button |
| Ключ строки / layout | `brick_when_button_pressed` / `brick_when_button_pressed` |
| Параметры | кнопка (WhenButtonPressedScript / VolumeButtonHoldScript); спиннер (выбор из списка в UI) |
| Дефолт в палитре | BUTTON_VOLUME_UP |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/WhenButtonPressedBrick.java:44` |
| Класс экшена | — (ScriptBrick: addActionToSequence пустой, экшена нет) |
| Описание | Нет в `BrickInfo.java`. Факт кода: — (ScriptBrick: addActionToSequence пустой, экшена нет) |

## Сообщения / Messages (`subcategory_event_messages`)

### 10. `BroadcastReceiverBrick`

| Поле | Значение |
|---|---|
| Название RU | Когда будет получено |
| Название EN | When you receive |
| Ключ строки / layout | `brick_broadcast_receive` / `brick_broadcast_receive` |
| Параметры | сообщение broadcastMessage (String, выбор из BroadcastMessageContainer); сообщение (BroadcastScript) |
| Дефолт в палитре | первое сообщение проекта или brick_broadcast_default_value |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/BroadcastReceiverBrick.java:33` |
| Класс экшена | — (ScriptBrick: addActionToSequence пустой, экшена нет) |
| Описание | Вызывается, когда любой спрайт в этой сцене отправит выбранное сообщение.Код: событийный ScriptBrick, `addActionToSequence` пустой. |

### 11. `BroadcastWithParamsReceiverBrick`

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

### 12. `BroadcastBrick`

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

### 13. `BroadcastWaitBrick`

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

### 14. `BroadcastWithParamsBrick`

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

## Условия и изменения / Conditions & Changes (`subcategory_event_conditions`)

### 15. `WhenConditionBrick`

| Поле | Значение |
|---|---|
| Название RU | Как только |
| Название EN | When |
| Ключ строки / layout | `brick_when_condition_when` / `brick_when_condition_true` |
| Параметры | Formula: IF_CONDITION |
| Дефолт в палитре | условие 1 < 2 |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/WhenConditionBrick.java:34` |
| Класс экшена | — (ScriptBrick: addActionToSequence пустой, экшена нет) |
| Описание | Вызывается, как только условие в станет истиной.Код: событийный ScriptBrick, `addActionToSequence` пустой. |

### 16. `WhenTimeReachedBrick`

| Поле | Значение |
|---|---|
| Название RU | Когда время достигнет |
| Название EN | When time reaches |
| Ключ строки / layout | `brick_when_time_reached` / `brick_when_time_reached` |
| Параметры | Formula: TIME |
| Дефолт в палитре | Formula(1430) |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/WhenTimeReachedBrick.java:34` |
| Класс экшена | — (ScriptBrick: addActionToSequence пустой, экшена нет) |
| Описание | Нет в `BrickInfo.java`. Факт кода: — (ScriptBrick: addActionToSequence пустой, экшена нет) |

### 17. `WhenShakeBrick`

| Поле | Значение |
|---|---|
| Название RU | При встряхивании телефона |
| Название EN | When phone shaken |
| Ключ строки / layout | `brick_when_shake` / `brick_when_shake` |
| Параметры | конструктор: WhenShakeScript script |
| Дефолт в палитре | — |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/WhenShakeBrick.java:9` |
| Класс экшена | — (ScriptBrick: addActionToSequence пустой, экшена нет) |
| Описание | Вызывается, когда устройство встряхивают.Код: событийный ScriptBrick, `addActionToSequence` пустой. |

### 18. `WhenBounceOffBrick` — только если спрайт не фоновый

| Поле | Значение |
|---|---|
| Название RU | При столкновении с |
| Название EN | When you bounce off |
| Ключ строки / layout | `brick_collision_receive` / `brick_when_bounce_off` |
| Параметры | конструктор: WhenBounceOffScript script; спиннер (выбор из списка в UI) |
| Дефолт в палитре | WhenBounceOffScript(null) |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/WhenBounceOffBrick.java:43` |
| Класс экшена | — (ScriptBrick: addActionToSequence пустой, экшена нет) |
| Описание | Вызывается, когда столкновение закончено. ВАЖНО: плохо работаетКод: событийный ScriptBrick, `addActionToSequence` пустой. |

### 19. `WhenTouchingSpriteBrick` — только если спрайт не фоновый

| Поле | Значение |
|---|---|
| Название RU | Когда касается другого актёра |
| Название EN | When you touch another actor |
| Ключ строки / layout | `brick_when_touching_sprite` / `brick_when_touching_sprite` |
| Параметры | конструктор: WhenTouchingSpriteScript script |
| Дефолт в палитре | — |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/WhenTouchingSpriteBrick.java:35` |
| Класс экшена | — (ScriptBrick: addActionToSequence пустой, экшена нет) |
| Описание | Вызывается, когда этот спрайт касается любого другого спрайта. Не требует физики.Код: событийный ScriptBrick, `addActionToSequence` пустой. |

### 20. `WhenTouchingSpriteByNameBrick` — только если спрайт не фоновый

| Поле | Значение |
|---|---|
| Название RU | Когда касается |
| Название EN | When you touch |
| Ключ строки / layout | `brick_when_touching_sprite_by_name` / `brick_when_touching_sprite_by_name` |
| Параметры | конструктор: WhenTouchingSpriteByNameScript script; спиннер (выбор из списка в UI) |
| Дефолт в палитре | — |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/WhenTouchingSpriteByNameBrick.java:44` |
| Класс экшена | — (ScriptBrick: addActionToSequence пустой, экшена нет) |
| Описание | Вызывается, когда этот спрайт касается выбранного спрайта. Не требует физики.Код: событийный ScriptBrick, `addActionToSequence` пустой. |

### 21. `WhenNeo3DCollidesBrick`

| Поле | Значение |
|---|---|
| Название RU | Когда 3D-объект касается |
| Название EN | When 3D object touches |
| Ключ строки / layout | `brick_when_neo3d_collides` / `brick_when_neo3d_collides` |
| Параметры | Formula: NAME, TARGET |
| Дефолт в палитре | — |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/WhenNeo3DCollidesBrick.java:12` |
| Класс экшена | — (ScriptBrick: addActionToSequence пустой, экшена нет) |
| Описание | Вызывается, когда физический 3D-объект касается другого (пустое второе имя = любой).Код: событийный ScriptBrick, `addActionToSequence` пустой. |

### 22. `WhenNeo3DTouchBrick`

| Поле | Значение |
|---|---|
| Название RU | Когда 3D-объект коснулся |
| Название EN | When 3D object touches |
| Ключ строки / layout | `brick_when_neo3d_touch` / `brick_when_neo3d_touch` |
| Параметры | Formula: NAME, DISTANCE; спиннер (выбор из списка в UI) |
| Дефолт в палитре | — |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/WhenNeo3DTouchBrick.java:29` |
| Класс экшена | — (ScriptBrick: addActionToSequence пустой, экшена нет) |
| Описание | Вызывается, когда Neo3D-объект оказывается ближе дистанции (без физики). Имя другого пишется в переменную.Код: событийный ScriptBrick, `addActionToSequence` пустой. |

### 23. `WhenNeo3DRayHitBrick`

| Поле | Значение |
|---|---|
| Название RU | Когда 3D-луч попал |
| Название EN | When 3D ray hits |
| Ключ строки / layout | `brick_when_neo3d_ray_hit` / `brick_when_neo3d_ray_hit` |
| Параметры | Formula: NAME; спиннер (выбор из списка в UI) |
| Дефолт в палитре | — |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/WhenNeo3DRayHitBrick.java:31` |
| Класс экшена | — (ScriptBrick: addActionToSequence пустой, экшена нет) |
| Описание | Вызывается, когда 3D-луч впервые задевает объекты; имена по порядку в переменных.Код: событийный ScriptBrick, `addActionToSequence` пустой. |

### 24. `WhenIntervalBrick`

| Поле | Значение |
|---|---|
| Название RU | Каждые |
| Название EN | Every |
| Ключ строки / layout | `brick_when_interval_every` / `brick_when_interval` |
| Параметры | Formula: TIME_TO_WAIT_IN_SECONDS |
| Дефолт в палитре | WhenIntervalScript(Formula(1)) |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/WhenIntervalBrick.java:12` |
| Класс экшена | — (ScriptBrick: addActionToSequence пустой, экшена нет) |
| Описание | Нет в `BrickInfo.java`. Факт кода: — (ScriptBrick: addActionToSequence пустой, экшена нет) |

### 25. `WhenTcpMessageBrick`

| Поле | Значение |
|---|---|
| Название RU | Когда получено сообщение TCP |
| Название EN | When TCP message received |
| Ключ строки / layout | `brick_when_tcp_message` / `brick_when_tcp_message` |
| Параметры | Formula: TEXT; спиннер (выбор из списка в UI) |
| Дефолт в палитре | — |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/WhenTcpMessageBrick.java:32` |
| Класс экшена | — (ScriptBrick: addActionToSequence пустой, экшена нет) |
| Описание | Вызывается, когда получено сообщение по TCP серверу, и сохраняет его в переменную.Код: событийный ScriptBrick, `addActionToSequence` пустой. |

### 26. `WhenTcpDisconnectedBrick`

| Поле | Значение |
|---|---|
| Название RU | Когда отключен по таймауту TCP сервера |
| Название EN | When disconnected by TCP server timeout |
| Ключ строки / layout | `brick_when_tcp_disconnected` / `brick_when_tcp_disconnected` |
| Параметры | Formula: TEXT |
| Дефолт в палитре | — |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/WhenTcpDisconnectedBrick.java:14` |
| Класс экшена | — (ScriptBrick: addActionToSequence пустой, экшена нет) |
| Описание | Вызывается, когда соединение было разорвано по таймауту TCP сервера.Код: событийный ScriptBrick, `addActionToSequence` пустой. |

### 27. `WhenBackgroundChangesBrick`

| Поле | Значение |
|---|---|
| Название RU | Когда фон изменится на |
| Название EN | When background changes to |
| Ключ строки / layout | `brick_when_background` / `brick_when_background_changes_to` |
| Параметры | конструктор: @NonNull WhenBackgroundChangesScript script; спиннер (выбор из списка в UI) |
| Дефолт в палитре | — |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/WhenBackgroundChangesBrick.java:49` |
| Класс экшена | SetLookAction (catroid/src/main/java/org/catrobat/catroid/content/actions/SetLookAction.kt:30) — через createSetLookAction |
| Описание | Вызывается, когда фон изменит образ на выбранный.Код: создаёт `SetLookAction` в `addActionToSequence`. |

### 28. `WhenHttpResponseReceivedBrick`

| Поле | Значение |
|---|---|
| Название RU | Когда получен HTTP-ответ |
| Название EN | When HTTP response received |
| Ключ строки / layout | `brick_when_http_response_received` / `brick_when_http_response_received` |
| Параметры | Formula: VALUE_1 |
| Дефолт в палитре | — |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/WhenHttpResponseReceivedBrick.java:11` |
| Класс экшена | — (ScriptBrick: addActionToSequence пустой, экшена нет) |
| Описание | Событие: запускает вложенные блоки после получения HTTP-ответа.Код: событийный ScriptBrick, `addActionToSequence` пустой. |

### 29. `WhenHttpRequestFailedBrick`

| Поле | Значение |
|---|---|
| Название RU | Когда HTTP-запрос завершился ошибкой |
| Название EN | When HTTP request failed |
| Ключ строки / layout | `brick_when_http_request_failed` / `brick_when_http_request_failed` |
| Параметры | Formula: VALUE_1 |
| Дефолт в палитре | — |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/WhenHttpRequestFailedBrick.java:10` |
| Класс экшена | — (ScriptBrick: addActionToSequence пустой, экшена нет) |
| Описание | Событие: запускает вложенные блоки при сетевой ошибке HTTP-запроса.Код: событийный ScriptBrick, `addActionToSequence` пустой. |

### 30. `WhenFirestoreChangedBrick`

| Поле | Значение |
|---|---|
| Название RU | Когда Firestore изменился |
| Название EN | When Firestore changed |
| Ключ строки / layout | `brick_when_firestore_changed` / `brick_when_firestore_changed` |
| Параметры | Formula: FIRESTORE_BASE, FIRESTORE_TRIGGER_PATH |
| Дефолт в палитре | — |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/WhenFirestoreChangedBrick.java:35` |
| Класс экшена | — (ScriptBrick: addActionToSequence пустой, экшена нет) |
| Описание | Вызывается, когда документ или коллекция Firestore по указанному пути изменяется (первое получение не срабатывает).Код: событийный ScriptBrick, `addActionToSequence` пустой. |

### 31. `WhenFirebaseChangedBrick`

| Поле | Значение |
|---|---|
| Название RU | Когда значение Firebase изменится по адресу |
| Название EN | When Firebase value changes at |
| Ключ строки / layout | `brick_when_firebase_changed` / `brick_when_firebase_changed` |
| Параметры | Formula: FIREBASE_TRIGGER_BUCKET, FIREBASE_TRIGGER_PATH |
| Дефолт в палитре | — |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/WhenFirebaseChangedBrick.java:35` |
| Класс экшена | — (ScriptBrick: addActionToSequence пустой, экшена нет) |
| Описание | Вызывается, когда значение в Firebase по указанному бакету и пути изменяется.Код: событийный ScriptBrick, `addActionToSequence` пустой. |

### 32. `WhenFirebaseChildChangedBrick`

| Поле | Значение |
|---|---|
| Название RU | Когда дочерний узел FireBase изменился |
| Название EN | When Firebase child changed |
| Ключ строки / layout | `brick_when_firebase_child_changed` / `brick_when_firebase_child_changed` |
| Параметры | Formula: FIREBASE_TRIGGER_BUCKET, FIREBASE_TRIGGER_PATH; спиннер (выбор из списка в UI) |
| Дефолт в палитре | — |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/WhenFirebaseChildChangedBrick.java:41` |
| Класс экшена | — (ScriptBrick: addActionToSequence пустой, экшена нет) |
| Описание | Вызывается при добавлении, изменении или удалении дочернего узла Firebase (первое получение не срабатывает).Код: событийный ScriptBrick, `addActionToSequence` пустой. |

### 33. `WhenMouseButtonClickedBrick`

| Поле | Значение |
|---|---|
| Название RU | Когда кнопка мыши нажата |
| Название EN | When mouse clicked |
| Ключ строки / layout | `brick_when_mouse_button_clicked` / `brick_when_mouse_button_clicked` |
| Параметры | конструктор: WhenMouseButtonClickedScript script; спиннер (выбор из списка в UI) |
| Дефолт в палитре | — |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/WhenMouseButtonClickedBrick.java:15` |
| Класс экшена | — (ScriptBrick: addActionToSequence пустой, экшена нет) |
| Описание | Вызывается, когда нажата кнопка мыши (левая, правая или средняя).Код: событийный ScriptBrick, `addActionToSequence` пустой. |

### 34. `WhenMouseWheelScrolledBrick`

| Поле | Значение |
|---|---|
| Название RU | Когда колесико мыши прокручено |
| Название EN | When mouse wheel scrolled |
| Ключ строки / layout | `brick_when_mouse_wheel_scrolled` / `brick_when_mouse_wheel_scrolled` |
| Параметры | конструктор: WhenMouseWheelScrolledScript script |
| Дефолт в палитре | — |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/WhenMouseWheelScrolledBrick.java:9` |
| Класс экшена | — (ScriptBrick: addActionToSequence пустой, экшена нет) |
| Описание | Вызывается, когда колесо мыши прокручено.Код: событийный ScriptBrick, `addActionToSequence` пустой. |

### 35. `WhenAIResponseBrick`

| Поле | Значение |
|---|---|
| Название RU | Когда ИИ ответил |
| Название EN | When AI responds |
| Ключ строки / layout | `brick_when_ai_response` / `brick_when_ai_response` |
| Параметры | конструктор: WhenAIResponseScript script; спиннер (выбор из списка в UI) |
| Дефолт в палитре | — |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/WhenAIResponseBrick.java:41` |
| Класс экшена | — (ScriptBrick: addActionToSequence пустой, экшена нет) |
| Описание | Вызывается, когда ИИ ответил на запрос блока «Спросить ИИ».Код: событийный ScriptBrick, `addActionToSequence` пустой. |

## Клонирование / Cloning (`subcategory_event_cloning`)

### 36. `WhenClonedBrick`

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

### 37. `CloneBrick`

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

### 38. `CloneAndNameBrick`

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

### 39. `DeleteThisCloneBrick`

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

## Системные / System (`subcategory_event_system`)

### 40. `WhenProjectExitsBrick`

| Поле | Значение |
|---|---|
| Название RU | При выходе из проекта |
| Название EN | When project exits |
| Ключ строки / layout | `brick_when_project_exits` / `brick_when_project_exits` |
| Параметры | конструктор: ExitProjectScript script |
| Дефолт в палитре | — |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/WhenProjectExitsBrick.java:9` |
| Класс экшена | — (ScriptBrick: addActionToSequence пустой, экшена нет) |
| Описание | Вызывается, когда пользователь выходит из проекта. Можно использовать для выгрузки данных или сохранения.Код: событийный ScriptBrick, `addActionToSequence` пустой. |

### 41. `WhenAppMinimizedBrick`

| Поле | Значение |
|---|---|
| Название RU | Когда приложение свернули |
| Название EN | When app minimized |
| Ключ строки / layout | `brick_when_app_minimized` / `brick_when_app_minimized` |
| Параметры | конструктор: WhenAppMinimizedScript script |
| Дефолт в палитре | — |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/WhenAppMinimizedBrick.java:9` |
| Класс экшена | — (ScriptBrick: addActionToSequence пустой, экшена нет) |
| Описание | Вызывается, когда приложение сворачивается (уходит в фон).Код: событийный ScriptBrick, `addActionToSequence` пустой. |

### 42. `WhenAppRestoredBrick`

| Поле | Значение |
|---|---|
| Название RU | Когда приложение развернули |
| Название EN | When app restored |
| Ключ строки / layout | `brick_when_app_restored` / `brick_when_app_restored` |
| Параметры | конструктор: WhenAppRestoredScript script |
| Дефолт в палитре | — |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/WhenAppRestoredBrick.java:9` |
| Класс экшена | — (ScriptBrick: addActionToSequence пустой, экшена нет) |
| Описание | Вызывается, когда приложение возвращается из фона на передний план.Код: событийный ScriptBrick, `addActionToSequence` пустой. |

### 43. `WhenNfcBrick` — только если NFC включён в настройках

| Поле | Значение |
|---|---|
| Название RU | Когда NFC метка обнаружена |
| Название EN | When NFC |
| Ключ строки / layout | `brick_when_nfc` / `brick_when_nfc` |
| Параметры | конструктор: @NonNull WhenNfcScript script; спиннер (выбор из списка в UI) |
| Дефолт в палитре | — |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/WhenNfcBrick.java:48` |
| Класс экшена | — (ScriptBrick: addActionToSequence пустой, экшена нет) |
| Описание | Вызывается, когда обнаружен NFC-тег (можно выбрать конкретный тег или все теги).Код: событийный ScriptBrick, `addActionToSequence` пустой. |

## Уведомления / Notifications (`subcategory_event_notifications`)

### 44. `WhenNotificationActionClickedBrick`

| Поле | Значение |
|---|---|
| Название RU | Когда нажата кнопка уведомления |
| Название EN | When notification action clicked |
| Ключ строки / layout | `brick_when_notification_action_clicked` / `brick_when_notification_action_clicked` |
| Параметры | конструктор: WhenNotificationActionClickedScript script |
| Дефолт в палитре | — |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/WhenNotificationActionClickedBrick.java:9` |
| Класс экшена | — (ScriptBrick: addActionToSequence пустой, экшена нет) |
| Описание | Вызывается, когда пользователь нажал на действие (кнопку) уведомления.Код: событийный ScriptBrick, `addActionToSequence` пустой. |

### 45. `WhenNotificationReplyBrick`

| Поле | Значение |
|---|---|
| Название RU | Когда ответ на уведомление |
| Название EN | When notification reply |
| Ключ строки / layout | `brick_when_notification_reply` / `brick_when_notification_reply` |
| Параметры | конструктор: WhenNotificationReplyScript script |
| Дефолт в палитре | — |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/WhenNotificationReplyBrick.java:9` |
| Класс экшена | — (ScriptBrick: addActionToSequence пустой, экшена нет) |
| Описание | Вызывается, когда пользователь отвечает на уведомление (вводит текст ответа).Код: событийный ScriptBrick, `addActionToSequence` пустой. |

### 46. `WhenNotificationShownBrick`

| Поле | Значение |
|---|---|
| Название RU | Когда уведомление показано |
| Название EN | When notification shown |
| Ключ строки / layout | `brick_when_notification_shown` / `brick_when_notification_shown` |
| Параметры | конструктор: WhenNotificationShownScript script |
| Дефолт в палитре | — |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/WhenNotificationShownBrick.java:9` |
| Класс экшена | — (ScriptBrick: addActionToSequence пустой, экшена нет) |
| Описание | Вызывается, когда уведомление показано пользователю.Код: событийный ScriptBrick, `addActionToSequence` пустой. |

### 47. `WhenNotificationDismissedBrick`

| Поле | Значение |
|---|---|
| Название RU | Когда уведомление закрыто |
| Название EN | When notification dismissed |
| Ключ строки / layout | `brick_when_notification_dismissed` / `brick_when_notification_dismissed` |
| Параметры | конструктор: WhenNotificationDismissedScript script |
| Дефолт в палитре | — |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/WhenNotificationDismissedBrick.java:9` |
| Класс экшена | — (ScriptBrick: addActionToSequence пустой, экшена нет) |
| Описание | Вызывается, когда пользователь закрыл (отклонил) уведомление.Код: событийный ScriptBrick, `addActionToSequence` пустой. |

## Реклама / AdMob (`subcategory_event_admob`)

### 48. `AdmobInitializedEventBrick`

| Поле | Значение |
|---|---|
| Название RU | Когда AdMob инициализирован |
| Название EN | When AdMob initialized |
| Ключ строки / layout | `brick_event_admob_initialized` / `brick_event_admob_initialized` |
| Параметры | конструктор: @NonNull AdmobInitializedScript script |
| Дефолт в палитре | — |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/AdmobInitializedEventBrick.java:11` |
| Класс экшена | — (ScriptBrick: addActionToSequence пустой, экшена нет) |
| Описание | Событие: выполняется, когда AdMob успешно инициализирован.Код: событийный ScriptBrick, `addActionToSequence` пустой. |

### 49. `AdmobInitFailedEventBrick`

| Поле | Значение |
|---|---|
| Название RU | Когда AdMob не инициализ. |
| Название EN | When AdMob init failed |
| Ключ строки / layout | `brick_event_admob_init_failed` / `brick_event_admob_init_failed` |
| Параметры | конструктор: @NonNull AdmobInitFailedScript script |
| Дефолт в палитре | — |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/AdmobInitFailedEventBrick.java:11` |
| Класс экшена | — (ScriptBrick: addActionToSequence пустой, экшена нет) |
| Описание | Событие: выполняется, когда инициализация AdMob завершилась ошибкой.Код: событийный ScriptBrick, `addActionToSequence` пустой. |

### 50. `AdmobBannerLoadedEventBrick`

| Поле | Значение |
|---|---|
| Название RU | Когда баннер загружен |
| Название EN | When banner loaded |
| Ключ строки / layout | `brick_event_admob_banner_loaded` / `brick_event_admob_banner_loaded` |
| Параметры | конструктор: @NonNull AdmobBannerLoadedScript script |
| Дефолт в палитре | — |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/AdmobBannerLoadedEventBrick.java:11` |
| Класс экшена | — (ScriptBrick: addActionToSequence пустой, экшена нет) |
| Описание | Событие: выполняется, когда баннер успешно загружен.Код: событийный ScriptBrick, `addActionToSequence` пустой. |

### 51. `AdmobBannerFailedEventBrick`

| Поле | Значение |
|---|---|
| Название RU | Когда баннер не загружен |
| Название EN | When banner failed |
| Ключ строки / layout | `brick_event_admob_banner_failed` / `brick_event_admob_banner_failed` |
| Параметры | конструктор: @NonNull AdmobBannerFailedScript script |
| Дефолт в палитре | — |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/AdmobBannerFailedEventBrick.java:11` |
| Класс экшена | — (ScriptBrick: addActionToSequence пустой, экшена нет) |
| Описание | Событие: выполняется, когда баннер не удалось загрузить.Код: событийный ScriptBrick, `addActionToSequence` пустой. |

### 52. `AdmobBannerShownEventBrick`

| Поле | Значение |
|---|---|
| Название RU | Когда баннер показан |
| Название EN | When banner shown |
| Ключ строки / layout | `brick_event_admob_banner_shown` / `brick_event_admob_banner_shown` |
| Параметры | конструктор: @NonNull AdmobBannerShownScript script |
| Дефолт в палитре | — |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/AdmobBannerShownEventBrick.java:11` |
| Класс экшена | — (ScriptBrick: addActionToSequence пустой, экшена нет) |
| Описание | Событие: выполняется, когда баннер показан.Код: событийный ScriptBrick, `addActionToSequence` пустой. |

### 53. `AdmobBannerHiddenEventBrick`

| Поле | Значение |
|---|---|
| Название RU | Когда баннер скрыт |
| Название EN | When banner hidden |
| Ключ строки / layout | `brick_event_admob_banner_hidden` / `brick_event_admob_banner_hidden` |
| Параметры | конструктор: @NonNull AdmobBannerHiddenScript script |
| Дефолт в палитре | — |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/AdmobBannerHiddenEventBrick.java:11` |
| Класс экшена | — (ScriptBrick: addActionToSequence пустой, экшена нет) |
| Описание | Событие: выполняется, когда баннер скрывается.Код: событийный ScriptBrick, `addActionToSequence` пустой. |

### 54. `AdmobInterstitialLoadedEventBrick`

| Поле | Значение |
|---|---|
| Название RU | Когда межстр. загружена |
| Название EN | When interstitial loaded |
| Ключ строки / layout | `brick_event_admob_interstitial_loaded` / `brick_event_admob_interstitial_loaded` |
| Параметры | конструктор: @NonNull AdmobInterstitialLoadedScript script |
| Дефолт в палитре | — |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/AdmobInterstitialLoadedEventBrick.java:11` |
| Класс экшена | — (ScriptBrick: addActionToSequence пустой, экшена нет) |
| Описание | Вызывается, когда полноэкранная (interstitial) реклама AdMob успешно загрузилась.Код: событийный ScriptBrick, `addActionToSequence` пустой. |

### 55. `AdmobInterstitialFailedEventBrick`

| Поле | Значение |
|---|---|
| Название RU | Когда межстр. не загружена |
| Название EN | When interstitial failed |
| Ключ строки / layout | `brick_event_admob_interstitial_failed` / `brick_event_admob_interstitial_failed` |
| Параметры | конструктор: @NonNull AdmobInterstitialFailedScript script |
| Дефолт в палитре | — |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/AdmobInterstitialFailedEventBrick.java:11` |
| Класс экшена | — (ScriptBrick: addActionToSequence пустой, экшена нет) |
| Описание | Вызывается, когда загрузка полноэкранной (interstitial) рекламы AdMob завершилась ошибкой.Код: событийный ScriptBrick, `addActionToSequence` пустой. |

### 56. `AdmobInterstitialShownEventBrick`

| Поле | Значение |
|---|---|
| Название RU | Когда межстр. показана |
| Название EN | When interstitial shown |
| Ключ строки / layout | `brick_event_admob_interstitial_shown` / `brick_event_admob_interstitial_shown` |
| Параметры | конструктор: @NonNull AdmobInterstitialShownScript script |
| Дефолт в палитре | — |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/AdmobInterstitialShownEventBrick.java:11` |
| Класс экшена | — (ScriptBrick: addActionToSequence пустой, экшена нет) |
| Описание | Вызывается, когда полноэкранная (interstitial) реклама AdMob была показана пользователю.Код: событийный ScriptBrick, `addActionToSequence` пустой. |

### 57. `AdmobInterstitialClosedEventBrick`

| Поле | Значение |
|---|---|
| Название RU | Когда межстр. закрыта |
| Название EN | When interstitial closed |
| Ключ строки / layout | `brick_event_admob_interstitial_closed` / `brick_event_admob_interstitial_closed` |
| Параметры | конструктор: @NonNull AdmobInterstitialClosedScript script |
| Дефолт в палитре | — |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/AdmobInterstitialClosedEventBrick.java:11` |
| Класс экшена | — (ScriptBrick: addActionToSequence пустой, экшена нет) |
| Описание | Событие: выполняется, когда межстраничная (interstitial) реклама закрывается.Код: событийный ScriptBrick, `addActionToSequence` пустой. |

### 58. `AdmobRewardedLoadedEventBrick`

| Поле | Значение |
|---|---|
| Название RU | Когда награда загружена |
| Название EN | When rewarded loaded |
| Ключ строки / layout | `brick_event_admob_rewarded_loaded` / `brick_event_admob_rewarded_loaded` |
| Параметры | конструктор: @NonNull AdmobRewardedLoadedScript script |
| Дефолт в палитре | — |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/AdmobRewardedLoadedEventBrick.java:11` |
| Класс экшена | — (ScriptBrick: addActionToSequence пустой, экшена нет) |
| Описание | Вызывается, когда вознаграждаемая (rewarded) реклама AdMob успешно загрузилась.Код: событийный ScriptBrick, `addActionToSequence` пустой. |

### 59. `AdmobRewardedFailedEventBrick`

| Поле | Значение |
|---|---|
| Название RU | Когда награда не загруж. |
| Название EN | When rewarded failed |
| Ключ строки / layout | `brick_event_admob_rewarded_failed` / `brick_event_admob_rewarded_failed` |
| Параметры | конструктор: @NonNull AdmobRewardedFailedScript script |
| Дефолт в палитре | — |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/AdmobRewardedFailedEventBrick.java:11` |
| Класс экшена | — (ScriptBrick: addActionToSequence пустой, экшена нет) |
| Описание | Вызывается, когда загрузка вознаграждаемой (rewarded) рекламы AdMob завершилась ошибкой.Код: событийный ScriptBrick, `addActionToSequence` пустой. |

### 60. `AdmobRewardedShownEventBrick`

| Поле | Значение |
|---|---|
| Название RU | Когда награда показана |
| Название EN | When rewarded shown |
| Ключ строки / layout | `brick_event_admob_rewarded_shown` / `brick_event_admob_rewarded_shown` |
| Параметры | конструктор: @NonNull AdmobRewardedShownScript script |
| Дефолт в палитре | — |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/AdmobRewardedShownEventBrick.java:11` |
| Класс экшена | — (ScriptBrick: addActionToSequence пустой, экшена нет) |
| Описание | Вызывается, когда вознаграждаемая (rewarded) реклама AdMob была показана пользователю.Код: событийный ScriptBrick, `addActionToSequence` пустой. |

### 61. `AdmobRewardedRewardEventBrick`

| Поле | Значение |
|---|---|
| Название RU | Когда награда получена |
| Название EN | When reward earned |
| Ключ строки / layout | `brick_event_admob_rewarded_reward` / `brick_event_admob_rewarded_reward` |
| Параметры | конструктор: @NonNull AdmobRewardedRewardScript script |
| Дефолт в палитре | — |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/AdmobRewardedRewardEventBrick.java:11` |
| Класс экшена | — (ScriptBrick: addActionToSequence пустой, экшена нет) |
| Описание | Вызывается, когда пользователь получил вознаграждение за просмотр rewarded-рекламы AdMob.Код: событийный ScriptBrick, `addActionToSequence` пустой. |

### 62. `AdmobRewardedClosedEventBrick`

| Поле | Значение |
|---|---|
| Название RU | Когда награда закрыта |
| Название EN | When rewarded closed |
| Ключ строки / layout | `brick_event_admob_rewarded_closed` / `brick_event_admob_rewarded_closed` |
| Параметры | конструктор: @NonNull AdmobRewardedClosedScript script |
| Дефолт в палитре | — |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/AdmobRewardedClosedEventBrick.java:11` |
| Класс экшена | — (ScriptBrick: addActionToSequence пустой, экшена нет) |
| Описание | Вызывается, когда показ вознаграждаемой (rewarded) рекламы AdMob завершился и она была закрыта.Код: событийный ScriptBrick, `addActionToSequence` пустой. |

### 63. `AdmobAppOpenLoadedEventBrick`

| Поле | Значение |
|---|---|
| Название RU | Когда App Open загружен |
| Название EN | When App Open loaded |
| Ключ строки / layout | `brick_event_admob_app_open_loaded` / `brick_event_admob_app_open_loaded` |
| Параметры | конструктор: @NonNull AdmobAppOpenLoadedScript script |
| Дефолт в палитре | — |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/AdmobAppOpenLoadedEventBrick.java:11` |
| Класс экшена | — (ScriptBrick: addActionToSequence пустой, экшена нет) |
| Описание | Событие: выполняется, когда реклама App Open загружена.Код: событийный ScriptBrick, `addActionToSequence` пустой. |

### 64. `AdmobAppOpenFailedEventBrick`

| Поле | Значение |
|---|---|
| Название RU | Когда App Open не загружен |
| Название EN | When App Open failed |
| Ключ строки / layout | `brick_event_admob_app_open_failed` / `brick_event_admob_app_open_failed` |
| Параметры | конструктор: @NonNull AdmobAppOpenFailedScript script |
| Дефолт в палитре | — |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/AdmobAppOpenFailedEventBrick.java:11` |
| Класс экшена | — (ScriptBrick: addActionToSequence пустой, экшена нет) |
| Описание | Событие: выполняется, когда рекламу App Open не удалось загрузить.Код: событийный ScriptBrick, `addActionToSequence` пустой. |

### 65. `AdmobAppOpenShownEventBrick`

| Поле | Значение |
|---|---|
| Название RU | Когда App Open показан |
| Название EN | When App Open shown |
| Ключ строки / layout | `brick_event_admob_app_open_shown` / `brick_event_admob_app_open_shown` |
| Параметры | конструктор: @NonNull AdmobAppOpenShownScript script |
| Дефолт в палитре | — |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/AdmobAppOpenShownEventBrick.java:11` |
| Класс экшена | — (ScriptBrick: addActionToSequence пустой, экшена нет) |
| Описание | Событие: выполняется, когда реклама App Open показана.Код: событийный ScriptBrick, `addActionToSequence` пустой. |

### 66. `AdmobAppOpenClosedEventBrick`

| Поле | Значение |
|---|---|
| Название RU | Когда App Open закрыт |
| Название EN | When App Open closed |
| Ключ строки / layout | `brick_event_admob_app_open_closed` / `brick_event_admob_app_open_closed` |
| Параметры | конструктор: @NonNull AdmobAppOpenClosedScript script |
| Дефолт в палитре | — |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/AdmobAppOpenClosedEventBrick.java:11` |
| Класс экшена | — (ScriptBrick: addActionToSequence пустой, экшена нет) |
| Описание | Событие: выполняется, когда реклама App Open закрывается.Код: событийный ScriptBrick, `addActionToSequence` пустой. |

## Запуск и нажатия / Start & Touch (`subcategory_event_touch`)

### 67. `WhenBeforeUpdateBrick`

| Поле | Значение |
|---|---|
| Название RU | Перед обновлением |
| Название EN | Before update |
| Ключ строки / layout | `brick_when_before_update` / `brick_when_before_update` |
| Параметры | конструктор: BeforeUpdateScript script |
| Дефолт в палитре | — |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/WhenBeforeUpdateBrick.java:9` |
| Класс экшена | — (ScriptBrick: addActionToSequence пустой, экшена нет) |
| Описание | Вызывается перед каждым обновлением сцены (кадра).Код: событийный ScriptBrick, `addActionToSequence` пустой. |

### 68. `WhenAfterUpdateBrick`

| Поле | Значение |
|---|---|
| Название RU | После обновления |
| Название EN | After update |
| Ключ строки / layout | `brick_when_after_update` / `brick_when_after_update` |
| Параметры | конструктор: AfterUpdateScript script |
| Дефолт в палитре | — |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/WhenAfterUpdateBrick.java:9` |
| Класс экшена | — (ScriptBrick: addActionToSequence пустой, экшена нет) |
| Описание | Вызывается после каждого обновления сцены (кадра).Код: событийный ScriptBrick, `addActionToSequence` пустой. |

### 69. `WhenFingerMovedOnScreenBrick`

| Поле | Значение |
|---|---|
| Название RU | При движении пальца по экрану |
| Название EN | When finger moved on screen |
| Ключ строки / layout | `brick_when_finger_moved_on_screen` / `brick_when_finger_moved_on_screen` |
| Параметры | конструктор: WhenFingerMovedOnScreenScript script |
| Дефолт в палитре | — |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/WhenFingerMovedOnScreenBrick.java:9` |
| Класс экшена | — (ScriptBrick: addActionToSequence пустой, экшена нет) |
| Описание | Вызывается, когда палец движется по экрану.Код: событийный ScriptBrick, `addActionToSequence` пустой. |

### 70. `WhenFingerMovedOverSpriteBrick`

| Поле | Значение |
|---|---|
| Название RU | При движении пальца над спрайтом |
| Название EN | When finger moved over sprite |
| Ключ строки / layout | `brick_when_finger_moved_over_sprite` / `brick_when_finger_moved_over_sprite` |
| Параметры | конструктор: WhenFingerMovedOverSpriteScript script |
| Дефолт в палитре | — |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/WhenFingerMovedOverSpriteBrick.java:9` |
| Класс экшена | — (ScriptBrick: addActionToSequence пустой, экшена нет) |
| Описание | Вызывается, когда палец движется над этим спрайтом.Код: событийный ScriptBrick, `addActionToSequence` пустой. |

### 71. `WhenSpriteReleasedBrick`

| Поле | Значение |
|---|---|
| Название RU | При отпускании спрайта |
| Название EN | When sprite released |
| Ключ строки / layout | `brick_when_sprite_released` / `brick_when_sprite_released` |
| Параметры | конструктор: WhenSpriteReleasedScript script |
| Дефолт в палитре | — |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/WhenSpriteReleasedBrick.java:9` |
| Класс экшена | — (ScriptBrick: addActionToSequence пустой, экшена нет) |
| Описание | Вызывается, когда палец отпускается от этого спрайта.Код: событийный ScriptBrick, `addActionToSequence` пустой. |

### 72. `WhenWindowResizedBrick`

| Поле | Значение |
|---|---|
| Название RU | При изменении размера окна |
| Название EN | When window resized |
| Ключ строки / layout | `brick_when_window_resized` / `brick_when_window_resized` |
| Параметры | конструктор: WhenWindowResizedScript script |
| Дефолт в палитре | — |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/WhenWindowResizedBrick.java:9` |
| Класс экшена | — (ScriptBrick: addActionToSequence пустой, экшена нет) |
| Описание | Вызывается при изменении размера окна.Код: событийный ScriptBrick, `addActionToSequence` пустой. |

### 73. `WhenNotificationClickedBrick`

| Поле | Значение |
|---|---|
| Название RU | При нажатии на уведомление |
| Название EN | When notification clicked |
| Ключ строки / layout | `brick_when_notification_clicked` / `brick_when_notification_clicked` |
| Параметры | Formula: VALUE_1 |
| Дефолт в палитре | WhenNotificationClickedScript(Formula(123456)) |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/WhenNotificationClickedBrick.java:12` |
| Класс экшена | — (ScriptBrick: addActionToSequence пустой, экшена нет) |
| Описание | Запускает скрипт при нажатии на уведомление с указанным ID.Код: событийный ScriptBrick, `addActionToSequence` пустой. |

### 74. `WhenNotificationActionTriggeredBrick`

| Поле | Значение |
|---|---|
| Название RU | При действии уведомления |
| Название EN | When notification action |
| Ключ строки / layout | `brick_when_notification_action_triggered` / `brick_when_notification_action_triggered` |
| Параметры | Formula: VALUE_1 |
| Дефолт в палитре | WhenNotificationActionTriggeredScript(Formula(123456)) |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/WhenNotificationActionTriggeredBrick.java:12` |
| Класс экшена | — (ScriptBrick: addActionToSequence пустой, экшена нет) |
| Описание | Запускает скрипт при нажатии на кнопку действия уведомления с указанным ID действия.Код: событийный ScriptBrick, `addActionToSequence` пустой. |

### 75. `WhenClonedWithNameBrick`

| Поле | Значение |
|---|---|
| Название RU | При клонировании по имени |
| Название EN | When cloned with name |
| Ключ строки / layout | `brick_when_cloned_with_name_title` / `brick_when_cloned_with_name` |
| Параметры | Formula: VALUE_1 |
| Дефолт в палитре | WhenClonedWithNameScript(Formula("clone")) |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/WhenClonedWithNameBrick.java:12` |
| Класс экшена | — (ScriptBrick: addActionToSequence пустой, экшена нет) |
| Описание | Вызывается для клона, имя которого совпадает с указанным.Код: событийный ScriptBrick, `addActionToSequence` пустой. |

### 76. `WhenMqttMessageBrick`

| Поле | Значение |
|---|---|
| Название RU | При MQTT сообщении |
| Название EN | When MQTT message |
| Ключ строки / layout | `brick_when_mqtt_message_title` / `brick_when_mqtt_message` |
| Параметры | Formula: VALUE_1 |
| Дефолт в палитре | WhenMqttMessageScript(Formula("room_1")) |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/WhenMqttMessageBrick.java:12` |
| Класс экшена | — (ScriptBrick: addActionToSequence пустой, экшена нет) |
| Описание | Событие: запускает вложенные блоки при получении MQTT-сообщения из указанной комнаты.Код: событийный ScriptBrick, `addActionToSequence` пустой. |

---
Проверено по: `CategoryBricksFactory.kt`, `content/BrickInfo.java`, классам `content/bricks/*`, `content/actions/*` (через `content/ActionFactory.java`), `res/values/strings.xml`, `res/values-ru/strings.xml`.
