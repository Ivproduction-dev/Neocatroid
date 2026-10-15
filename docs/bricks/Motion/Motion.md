# Физика / Motion

Категория `category_motion`: RU «Физика», EN «Motion».

Источник состава и порядка: `catroid/src/main/java/org/catrobat/catroid/ui/fragment/CategoryBricksFactory.kt:1807`, метод `setupMotionCategoryList` (сгруппированный список, ветка grouping enabled).

Правила документа:

- Порядок бриков строго по фабрике; подгруппы — `SubCategoryHeaderBrick` из того же метода.
- Условные брики помечены (фон/global-сцена/NFC/Phiro) — позиция сохранена по фабрике.
- Кастомный хвост из конца палитры не трогаем и сюда не включаем (только состав `setup*` выше).
- Названия RU/EN — по первому `@string` главного layout (`getViewResource`) из `catroid/src/main/res/values/strings.xml` и `values-ru/strings.xml`.
- Параметры — `addAllowedBrickField(BrickField.*)` + спиннер/сообщение/спрайт по факту кода брика.
- Экшен — фактический класс из `content/actions` через `ActionFactory.create*` (резолв по `ActionFactory.java`); у ScriptBrick экшена нет.
- Описание — 1–2 строки строго по `BrickInfo.java` + факт кода; домыслов нет.

Всего бриков в этом файле: 61 (заголовки подгрупп не считаются).

## Координаты и плавность / Coordinates & Sliding (`subcategory_motion_coordinates`)

### 1. `PlaceAtBrick`

| Поле | Значение |
|---|---|
| Название RU | "Поместить в " |
| Название EN | "Place at" |
| Ключ строки / layout | `brick_place_at` / `brick_place_at` |
| Параметры | Formula: X_POSITION, Y_POSITION |
| Дефолт в палитре | X/Y_POSITION |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/PlaceAtBrick.java:33` |
| Класс экшена | GlideToAction (catroid/src/main/java/org/catrobat/catroid/content/actions/conditional/GlideToAction.java:34) — через createPlaceAtAction |
| Описание | Задает позицию спрайтуКод: создаёт `GlideToAction` в `addActionToSequence`. |

### 2. `SetXBrick`

| Поле | Значение |
|---|---|
| Название RU | "Установить X в " |
| Название EN | "Set x to" |
| Ключ строки / layout | `brick_set_x` / `brick_set_x` |
| Параметры | Formula: X_POSITION |
| Дефолт в палитре | X_POSITION |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/SetXBrick.java:30` |
| Класс экшена | SetXAction (catroid/src/main/java/org/catrobat/catroid/content/actions/SetXAction.java:33) — через createSetXAction |
| Описание | Задает позицию X спрайтуКод: создаёт `SetXAction` в `addActionToSequence`. |

### 3. `SetYBrick`

| Поле | Значение |
|---|---|
| Название RU | "Установить Y в " |
| Название EN | "Set y to" |
| Ключ строки / layout | `brick_set_y` / `brick_set_y` |
| Параметры | Formula: Y_POSITION |
| Дефолт в палитре | Y_POSITION |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/SetYBrick.java:30` |
| Класс экшена | SetYAction (catroid/src/main/java/org/catrobat/catroid/content/actions/SetYAction.java:33) — через createSetYAction |
| Описание | Задает позицию Y спрайтуКод: создаёт `SetYAction` в `addActionToSequence`. |

### 4. `ChangeXByNBrick`

| Поле | Значение |
|---|---|
| Название RU | "Изменить X на" |
| Название EN | "Change x by" |
| Ключ строки / layout | `brick_change_x_by` / `brick_change_x` |
| Параметры | Formula: X_POSITION_CHANGE |
| Дефолт в палитре | CHANGE_X_BY |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/ChangeXByNBrick.java:30` |
| Класс экшена | ChangeXByNAction (catroid/src/main/java/org/catrobat/catroid/content/actions/ChangeXByNAction.java:33) — через createChangeXByNAction |
| Описание | Изменяет позицию X спрайта на определенное значениеКод: создаёт `ChangeXByNAction` в `addActionToSequence`. |

### 5. `ChangeYByNBrick`

| Поле | Значение |
|---|---|
| Название RU | "Изменить Y на" |
| Название EN | "Change y by" |
| Ключ строки / layout | `brick_change_y_by` / `brick_change_y` |
| Параметры | Formula: Y_POSITION_CHANGE |
| Дефолт в палитре | CHANGE_Y_BY |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/ChangeYByNBrick.java:30` |
| Класс экшена | ChangeYByNAction (catroid/src/main/java/org/catrobat/catroid/content/actions/ChangeYByNAction.java:33) — через createChangeYByNAction |
| Описание | Изменяет позицию Y спрайта на определенное значениеКод: создаёт `ChangeYByNAction` в `addActionToSequence`. |

### 6. `GoToBrick`

| Поле | Значение |
|---|---|
| Название RU | Перейти в |
| Название EN | Go to |
| Ключ строки / layout | `brick_go_to` / `brick_go_to` |
| Параметры | спрайт-получатель (выбор спрайта, destinationSprite + спиннер) |
| Дефолт в палитре | (null) |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/GoToBrick.java:43` |
| Класс экшена | GoToTouchPositionAction (catroid/src/main/java/org/catrobat/catroid/content/actions/GoToTouchPositionAction.java:31); GoToRandomPositionAction (catroid/src/main/java/org/catrobat/catroid/content/actions/GoToRandomPositionAction.java:31); GoToOtherSpritePositionAction (catroid/src/main/java/org/catrobat/catroid/content/actions/GoToOtherSpritePositionAction.kt:28) — через createGoToAction |
| Описание | Помещает спрайт на позициюКод: создаёт `GoToTouchPositionAction` в `addActionToSequence` (+ ещё 2). |

### 7. `GlideToBrick`

| Поле | Значение |
|---|---|
| Название RU | Скользить |
| Название EN | Glide |
| Ключ строки / layout | `brick_glide` / `brick_glide_to` |
| Параметры | Formula: X_DESTINATION, Y_DESTINATION, DURATION_IN_SECONDS; спиннер (выбор из списка в UI) |
| Дефолт в палитре | X/Y_POSITION, GLIDE_SECONDS |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/GlideToBrick.java:36` |
| Класс экшена | GlideToAction (catroid/src/main/java/org/catrobat/catroid/content/actions/conditional/GlideToAction.java:34) — через createGlideToAction |
| Описание | Плавно перемещает спрайт к позицииКод: создаёт `GlideToAction` в `addActionToSequence`. |

### 8. `TweenPositionBrick`

| Поле | Значение |
|---|---|
| Название RU | Плавно переместить по X: |
| Название EN | Tween to x: |
| Ключ строки / layout | `brick_tween_position` / `brick_tween_position` |
| Параметры | Formula: X_DESTINATION, Y_DESTINATION, DURATION_IN_SECONDS, EASING_TYPE |
| Дефолт в палитре | 100.0, 100.0, 1.0, 0.0 |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/TweenPositionBrick.kt:8` |
| Класс экшена | TweenPositionAction (catroid/src/main/java/org/catrobat/catroid/content/actions/TweenPositionAction.kt:31) — через createTweenPositionAction |
| Описание | Плавно перемещает спрайт к позиции с различными типами анимации (easing). 25 типов: linear, sine, quad, cubic, pow2, pow3, bounce, elastic, swing и др.Код: создаёт `TweenPositionAction` в `addActionToSequence`. |

### 9. `MoveTowardsPointBrick`

| Поле | Значение |
|---|---|
| Название RU | Двигаться к |
| Название EN | Move towards |
| Ключ строки / layout | `brick_move_towards_point` / `brick_move_towards_point` |
| Параметры | Formula: X_POSITION, Y_POSITION, STEPS |
| Дефолт в палитре | 0.0, 0.0, 10.0 |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/MoveTowardsPointBrick.java:30` |
| Класс экшена | MoveTowardsPointAction (catroid/src/main/java/org/catrobat/catroid/content/actions/MoveTowardsPointAction.kt:30) — через createMoveTowardsPointAction |
| Описание | Нет в `BrickInfo.java`. Факт кода: создаёт `MoveTowardsPointAction`. |

### 10. `ClampPositionBrick`

| Поле | Значение |
|---|---|
| Название RU | Ограничить позицию |
| Название EN | Clamp position |
| Ключ строки / layout | `brick_clamp_position` / `brick_clamp_position` |
| Параметры | Formula: X1, X2, Y1, Y2 |
| Дефолт в палитре | -540, 540, -960, 960 |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/ClampPositionBrick.java:30` |
| Класс экшена | ClampPositionAction (catroid/src/main/java/org/catrobat/catroid/content/actions/ClampPositionAction.kt:29) — через createClampPositionAction |
| Описание | Нет в `BrickInfo.java`. Факт кода: создаёт `ClampPositionAction`. |

## Вращение и направление / Rotation & Direction (`subcategory_motion_rotation`)

### 11. `MoveNStepsBrick`

| Поле | Значение |
|---|---|
| Название RU | Идти |
| Название EN | Move |
| Ключ строки / layout | `brick_move` / `brick_move_n_steps` |
| Параметры | Formula: STEPS |
| Дефолт в палитре | MOVE_STEPS |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/MoveNStepsBrick.java:39` |
| Класс экшена | MoveNStepsAction (catroid/src/main/java/org/catrobat/catroid/content/actions/MoveNStepsAction.java:33) — через createMoveNStepsAction |
| Описание | Перемещает спрайт на определенное количество шагов вперед по направлениюКод: создаёт `MoveNStepsAction` в `addActionToSequence`. |

### 12. `SetStepSizeBrick`

| Поле | Значение |
|---|---|
| Название RU | Задать размер шага |
| Название EN | Set step size |
| Ключ строки / layout | `brick_set_step_size` / `brick_set_step_size` |
| Параметры | Formula: VALUE |
| Дефолт в палитре | 1f |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/SetStepSizeBrick.java:8` |
| Класс экшена | SetStepSizeAction (catroid/src/main/java/org/catrobat/catroid/content/actions/SetStepSizeAction.java:11) — через createSetStepSizeAction |
| Описание | Задаёт глобальный множитель длины шага для блока «сделать шаг».Код: создаёт `SetStepSizeAction` в `addActionToSequence`. |

### 13. `TurnLeftBrick`

| Поле | Значение |
|---|---|
| Название RU | Повернуть налево на |
| Название EN | Turn left |
| Ключ строки / layout | `brick_turn_left` / `brick_turn_left` |
| Параметры | Formula: TURN_LEFT_DEGREES |
| Дефолт в палитре | TURN_DEGREES |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/TurnLeftBrick.java:30` |
| Класс экшена | TurnLeftAction (catroid/src/main/java/org/catrobat/catroid/content/actions/TurnLeftAction.java:34) — через createTurnLeftAction |
| Описание | Поворачивает спрайт влево на определенное количество градусовКод: создаёт `TurnLeftAction` в `addActionToSequence`. |

### 14. `TurnRightBrick`

| Поле | Значение |
|---|---|
| Название RU | Повернуть направо на |
| Название EN | Turn right |
| Ключ строки / layout | `brick_turn_right` / `brick_turn_right` |
| Параметры | Formula: TURN_RIGHT_DEGREES |
| Дефолт в палитре | TURN_DEGREES |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/TurnRightBrick.java:30` |
| Класс экшена | TurnRightAction (catroid/src/main/java/org/catrobat/catroid/content/actions/TurnRightAction.java:34) — через createTurnRightAction |
| Описание | Поворачивает спрайт вправо на определенное количество градусовКод: создаёт `TurnRightAction` в `addActionToSequence`. |

### 15. `PointInDirectionBrick`

| Поле | Значение |
|---|---|
| Название RU | Указать в направлении |
| Название EN | Point in direction |
| Ключ строки / layout | `brick_point_in_direction` / `brick_point_in_direction` |
| Параметры | Formula: DEGREES |
| Дефолт в палитре | POINT_IN_DIRECTION |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/PointInDirectionBrick.java:30` |
| Класс экшена | PointInDirectionAction (catroid/src/main/java/org/catrobat/catroid/content/actions/PointInDirectionAction.java:34) — через createPointInDirectionAction |
| Описание | Задает направление спрайтуКод: создаёт `PointInDirectionAction` в `addActionToSequence`. |

### 16. `PointToBrick`

| Поле | Значение |
|---|---|
| Название RU | Указать в направлении к |
| Название EN | Point towards |
| Ключ строки / layout | `brick_point_to` / `brick_point_to` |
| Параметры | спрайт-получатель (выбор спрайта, destinationSprite + спиннер) |
| Дефолт в палитре | (null) |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/PointToBrick.java:45` |
| Класс экшена | PointToAction (catroid/src/main/java/org/catrobat/catroid/content/actions/PointToAction.java:31) — через createPointToAction |
| Описание | Поворачивает спрайт к другому спрайтуКод: создаёт `PointToAction` в `addActionToSequence`. |

### 17. `RotateTowardsTargetBrick`

| Поле | Значение |
|---|---|
| Название RU | Повернуть к |
| Название EN | Rotate towards |
| Ключ строки / layout | `brick_rotate_towards_target` / `brick_rotate_towards_target` |
| Параметры | Formula: X_POSITION, Y_POSITION, DEGREES |
| Дефолт в палитре | 0.0, 0.0, 10.0 |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/RotateTowardsTargetBrick.java:30` |
| Класс экшена | RotateTowardsTargetAction (catroid/src/main/java/org/catrobat/catroid/content/actions/RotateTowardsTargetAction.kt:31) — через createRotateTowardsTargetAction |
| Описание | Нет в `BrickInfo.java`. Факт кода: создаёт `RotateTowardsTargetAction`. |

### 18. `TouchDirectionBrick`

| Поле | Значение |
|---|---|
| Название RU | Направить на касание |
| Название EN | Point to touch |
| Ключ строки / layout | `brick_point_touch_direction` / `brick_touch_direction` |
| Параметры | — (без формульных полей) |
| Дефолт в палитре | — |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/TouchDirectionBrick.java:7` |
| Класс экшена | TouchDirectionAction (catroid/src/main/java/org/catrobat/catroid/content/actions/TouchDirectionAction.java:32) — через createTouchDirectionAction |
| Описание | Поворачивает актёра так, чтобы он указывал на текущее касание экрана (на первый палец). Если касаний нет, ничего не происходит.Код: создаёт `TouchDirectionAction` в `addActionToSequence`. |

### 19. `SetRotationStyleBrick`

| Поле | Значение |
|---|---|
| Название RU | "Задать тип вращения " |
| Название EN | "Set rotation style" |
| Ключ строки / layout | `brick_set_rotation_style` / `brick_set_rotation_style` |
| Параметры | — (без формульных полей); спиннер (выбор из списка в UI) |
| Дефолт в палитре | — |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/SetRotationStyleBrick.java:42` |
| Класс экшена | SetRotationStyleAction (catroid/src/main/java/org/catrobat/catroid/content/actions/SetRotationStyleAction.java:30) — через createSetRotationStyleAction |
| Описание | Задает тип вращения.Код: создаёт `SetRotationStyleAction` в `addActionToSequence`. |

## Слои и камера / Layers & Camera (`subcategory_motion_layers`)

### 20. `GoNStepsBackBrick` — только если спрайт не фоновый

| Поле | Значение |
|---|---|
| Название RU | Вернуться на |
| Название EN | Go back |
| Ключ строки / layout | `brick_go_back` / `brick_go_back` |
| Параметры | Formula: STEPS |
| Дефолт в палитре | GO_BACK |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/GoNStepsBackBrick.java:39` |
| Класс экшена | GoNStepsBackAction (catroid/src/main/java/org/catrobat/catroid/content/actions/GoNStepsBackAction.java:35) — через createGoNStepsBackAction |
| Описание | Изменяет слой обьекта в меньшую сторонуКод: создаёт `GoNStepsBackAction` в `addActionToSequence`. |

### 21. `ComeToFrontBrick` — только если спрайт не фоновый

| Поле | Значение |
|---|---|
| Название RU | Переместить на передний слой |
| Название EN | Go to front |
| Ключ строки / layout | `brick_come_to_front` / `brick_go_to_front` |
| Параметры | — (без формульных полей) |
| Дефолт в палитре | — |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/ComeToFrontBrick.java:29` |
| Класс экшена | ComeToFrontAction (catroid/src/main/java/org/catrobat/catroid/content/actions/ComeToFrontAction.java:33) — через createComeToFrontAction |
| Описание | Перемещает обьект на передний планКод: создаёт `ComeToFrontAction` в `addActionToSequence`. |

### 22. `SetCameraFocusPointBrick`

| Поле | Значение |
|---|---|
| Название RU | Задать фокус камере с |
| Название EN | Become focus point with |
| Ключ строки / layout | `brick_focus_point` / `brick_set_camera_focus` |
| Параметры | Formula: HORIZONTAL_FLEXIBILITY, VERTICAL_FLEXIBILITY |
| Дефолт в палитре | — |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/SetCameraFocusPointBrick.kt:31` |
| Класс экшена | SetCameraFocusPointAction (catroid/src/main/java/org/catrobat/catroid/content/actions/SetCameraFocusPointAction.kt:31) — через createSetCameraFocusPointAction |
| Описание | Фокусирует камеру на спрайтеКод: создаёт `SetCameraFocusPointAction` в `addActionToSequence`. |

## Физика - Общее / Physics - General (`subcategory_motion_physics_core`)

### 23. `SetPhysicsObjectTypeBrick`

| Поле | Значение |
|---|---|
| Название RU | Задать тип движения |
| Название EN | Set your motion type to |
| Ключ строки / layout | `brick_set_physics_object_type` / `brick_physics_set_physics_object_type` |
| Параметры | конструктор: PhysicsObject.Type type; спиннер (выбор из списка в UI); поле type: PhysicsObject.Type |
| Дефолт в палитре | PHYSIC_TYPE |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/SetPhysicsObjectTypeBrick.java:39` |
| Класс экшена | — (createSetPhysicsObjectTypeAction (в ActionFactory класс *Action не найден)) |
| Описание | Задает тип движения обьекта: динамичный - падает и подчиняется законам ньютона. статический - не падает, но от него отталкиваются динамические |

### 24. `SetRagdollBrick`

| Поле | Значение |
|---|---|
| Название RU | Зарегдолить (1=вкл, 0=выкл): |
| Название EN | Set ragdoll (1=on, 0=off): |
| Ключ строки / layout | `brick_set_ragdoll` / `brick_set_ragdoll` |
| Параметры | Formula: PHYSICS_TOGGLE |
| Дефолт в палитре | 1 |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/SetRagdollBrick.java:8` |
| Класс экшена | SetRagdollAction (catroid/src/main/java/org/catrobat/catroid/content/actions/SetRagdollAction.kt:7) — через createSetRagdollAction |
| Описание | Зарегдолить спрайт: 1 = включить режим регдолла (свободная физика), 0 = выключить. Комбинируйте с формулой Sprite_ragdolled для проверки состояния.Код: создаёт `SetRagdollAction` в `addActionToSequence`. |

### 25. `SetHitboxBrick`

| Поле | Значение |
|---|---|
| Название RU | Задать образ, оставив хитбокс |
| Название EN | Set look, but keep hitbox |
| Ключ строки / layout | `brick_set_hitbox` / `brick_set_hitbox` |
| Параметры | — (без формульных полей); спиннер (выбор из списка в UI) |
| Дефолт в палитре | — |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/SetHitboxBrick.java:46` |
| Класс экшена | SetHitboxAction (catroid/src/main/java/org/catrobat/catroid/content/actions/SetHitboxAction.kt:55) — через createSetHitboxAction |
| Описание | Оставляет хитбокс текущего образа, но изменяет видимый образ. ВАЖНО: плохо работаетКод: создаёт `SetHitboxAction` в `addActionToSequence`. |

### 26. `CameraFollowBrick`

| Поле | Значение |
|---|---|
| Название RU | Камера следует за актёром |
| Название EN | Camera follows actor |
| Ключ строки / layout | `brick_camera_follow` / `brick_camera_follow` |
| Параметры | Formula: STRING, VALUE, X_POSITION_CHANGE, Y_POSITION_CHANGE |
| Дефолт в палитре | "sprite", 0f |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/CameraFollowBrick.java:8` |
| Класс экшена | CameraFollowAction (catroid/src/main/java/org/catrobat/catroid/content/actions/CameraFollowAction.kt:8) — через createCameraFollowAction |
| Описание | Нет в `BrickInfo.java`. Факт кода: создаёт `CameraFollowAction`. |

### 27. `CameraBoundsBrick`

| Поле | Значение |
|---|---|
| Название RU | Границы камеры |
| Название EN | Camera bounds |
| Ключ строки / layout | `brick_camera_bounds` / `brick_camera_bounds` |
| Параметры | Formula: X1, Y1, X2, Y2 |
| Дефолт в палитре | -540, -960, 540, 960 |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/CameraBoundsBrick.java:8` |
| Класс экшена | CameraBoundsAction (catroid/src/main/java/org/catrobat/catroid/content/actions/CameraBoundsAction.kt:8) — через createCameraBoundsAction |
| Описание | Нет в `BrickInfo.java`. Факт кода: создаёт `CameraBoundsAction`. |

### 28. `IfOnEdgeBounceBrick` — только если спрайт не фоновый

| Поле | Значение |
|---|---|
| Название RU | Оттолкнуться, если на краю |
| Название EN | If on edge, bounce |
| Ключ строки / layout | `brick_if_on_edge_bounce` / `brick_if_on_edge_bounce` |
| Параметры | — (без формульных полей) |
| Дефолт в палитре | — |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/IfOnEdgeBounceBrick.java:29` |
| Класс экшена | IfOnEdgeBounceAction (catroid/src/main/java/org/catrobat/catroid/content/actions/conditional/IfOnEdgeBounceAction.java:32) — через createIfOnEdgeBounceAction |
| Описание | Изменяет направление спрайта, если он на краю экранаКод: создаёт `IfOnEdgeBounceAction` в `addActionToSequence`. |

### 29. `WhenBounceOffBrick` — только если спрайт не фоновый

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

### 30. `VibrationBrick`

| Поле | Значение |
|---|---|
| Название RU | Вибрировать |
| Название EN | Vibrate for |
| Ключ строки / layout | `brick_vibration` / `brick_vibration` |
| Параметры | Formula: VIBRATE_DURATION_IN_SECONDS |
| Дефолт в палитре | VIBRATE_SECONDS |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/VibrationBrick.java:33` |
| Класс экшена | VibrateAction (catroid/src/main/java/org/catrobat/catroid/content/actions/VibrateAction.kt:35) — через createVibrateAction |
| Описание | Заставляет устройство вибрироватьКод: создаёт `VibrateAction` в `addActionToSequence`. |

## Физика - Силы и скорость / Physics - Forces & Speed (`subcategory_motion_physics_forces`)

### 31. `SetVelocityBrick`

| Поле | Значение |
|---|---|
| Название RU | "Задать скорость " |
| Название EN | "Set velocity to" |
| Ключ строки / layout | `brick_set_velocity_to` / `brick_physics_set_velocity` |
| Параметры | Formula: PHYSICS_VELOCITY_X, PHYSICS_VELOCITY_Y |
| Дефолт в палитре | PHYSIC_VELOCITY |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/SetVelocityBrick.java:32` |
| Класс экшена | — (createSetVelocityAction (в ActionFactory класс *Action не найден)) |
| Описание | Задает скорость динамическому телу |

### 32. `ApplyForceBrick`

| Поле | Значение |
|---|---|
| Название RU | Приложить силу |
| Название EN | Apply Force |
| Ключ строки / layout | `brick_apply_force` / `brick_apply_force` |
| Параметры | Formula: FORCE_X, FORCE_Y |
| Дефолт в палитре | 10, 100 |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/ApplyForceBrick.java:8` |
| Класс экшена | ApplyForceAction (catroid/src/main/java/org/catrobat/catroid/content/actions/ApplyForceAction.kt:10) — через createApplyForceAction |
| Описание | Применяет силу к спрайтуКод: создаёт `ApplyForceAction` в `addActionToSequence`. |

### 33. `ApplyImpulseBrick`

| Поле | Значение |
|---|---|
| Название RU | Придать импульс |
| Название EN | Apply Impulse |
| Ключ строки / layout | `brick_apply_impulse` / `brick_apply_impulse` |
| Параметры | Formula: IMPULSE_X, IMPULSE_Y |
| Дефолт в палитре | 100, 10 |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/ApplyImpulseBrick.java:8` |
| Класс экшена | ApplyImpulseAction (catroid/src/main/java/org/catrobat/catroid/content/actions/ApplyImpulseAction.kt:10) — через createApplyImpulseAction |
| Описание | Применяет импульс к спрайтуКод: создаёт `ApplyImpulseAction` в `addActionToSequence`. |

### 34. `ApplyTorqueBrick`

| Поле | Значение |
|---|---|
| Название RU | Приложить вращение |
| Название EN | Apply Torque (Spin) |
| Ключ строки / layout | `brick_apply_torque` / `brick_apply_torque` |
| Параметры | Formula: TORQUE |
| Дефолт в палитре | 15 |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/ApplyTorqueBrick.java:8` |
| Класс экшена | ApplyTorqueAction (catroid/src/main/java/org/catrobat/catroid/content/actions/ApplyTorqueAction.kt:9) — через createApplyTorqueAction |
| Описание | Прилагает вращение к спрайтуКод: создаёт `ApplyTorqueAction` в `addActionToSequence`. |

### 35. `ApplyAngularImpulseBrick`

| Поле | Значение |
|---|---|
| Название RU | Придать крутящий импульс |
| Название EN | Apply Angular Impulse (Kick) |
| Ключ строки / layout | `brick_apply_angular_impulse` / `brick_apply_angular_impulse` |
| Параметры | Formula: ANGULAR_IMPULSE |
| Дефолт в палитре | 30 |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/ApplyAngularImpulseBrick.java:8` |
| Класс экшена | ApplyAngularImpulseAction (catroid/src/main/java/org/catrobat/catroid/content/actions/ApplyAngularImpulseAction.kt:8) — через createApplyAngularImpulseAction |
| Описание | Придает крутящий импульс спрайтуКод: создаёт `ApplyAngularImpulseAction` в `addActionToSequence`. |

### 36. `TurnLeftSpeedBrick`

| Поле | Значение |
|---|---|
| Название RU | "Вращать влево " |
| Название EN | "Spin left" |
| Ключ строки / layout | `brick_rotate_left` / `brick_physics_turn_left_speed` |
| Параметры | Formula: PHYSICS_TURN_LEFT_SPEED |
| Дефолт в палитре | PHYSIC_TURN_DEGREES |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/TurnLeftSpeedBrick.java:30` |
| Класс экшена | — (createTurnLeftSpeedAction (в ActionFactory класс *Action не найден)) |
| Описание | Задает скорость вращения |

### 37. `TurnRightSpeedBrick`

| Поле | Значение |
|---|---|
| Название RU | "Вращать вправо " |
| Название EN | "Spin right" |
| Ключ строки / layout | `brick_rotate_right` / `brick_physics_turn_right_speed` |
| Параметры | Formula: PHYSICS_TURN_RIGHT_SPEED |
| Дефолт в палитре | PHYSIC_TURN_DEGREES |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/TurnRightSpeedBrick.java:30` |
| Класс экшена | — (createTurnRightSpeedAction (в ActionFactory класс *Action не найден)) |
| Описание | Задает скорость вращения |

### 38. `SetGravityBrick`

| Поле | Значение |
|---|---|
| Название RU | "Задать силу тяжести сцены" |
| Название EN | "Set gravity for all actors and objects to" |
| Ключ строки / layout | `brick_set_gravity_to` / `brick_physics_set_gravity` |
| Параметры | Formula: PHYSICS_GRAVITY_X, PHYSICS_GRAVITY_Y |
| Дефолт в палитре | PHYSIC_GRAVITY |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/SetGravityBrick.java:32` |
| Класс экшена | — (createSetGravityAction (в ActionFactory класс *Action не найден)) |
| Описание | Задает гравитацию всему физическому миру |

### 39. `SetMassBrick`

| Поле | Значение |
|---|---|
| Название RU | "Задать массу " |
| Название EN | "Set mass to" |
| Ключ строки / layout | `brick_set_mass` / `brick_physics_set_mass` |
| Параметры | Formula: PHYSICS_MASS |
| Дефолт в палитре | PHYSIC_MASS |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/SetMassBrick.java:30` |
| Класс экшена | — (createSetMassAction (в ActionFactory класс *Action не найден)) |
| Описание | Задает массу динамическому обьекту |

### 40. `SetDampingBrick`

| Поле | Значение |
|---|---|
| Название RU | Установить затухание |
| Название EN | Set Damping |
| Ключ строки / layout | `brick_set_damping` / `brick_set_damping` |
| Параметры | Formula: LINEAR_DAMPING, ANGULAR_DAMPING |
| Дефолт в палитре | 10f, 10f |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/SetDampingBrick.java:8` |
| Класс экшена | SetDampingAction (catroid/src/main/java/org/catrobat/catroid/content/actions/SetDampingAction.kt:8) — через createSetDampingAction |
| Описание | Задает затухание, имитируя воду или сопротивление воздухаКод: создаёт `SetDampingAction` в `addActionToSequence`. |

### 41. `SetBounceBrick`

| Поле | Значение |
|---|---|
| Название RU | "Задать упругость " |
| Название EN | "Set bounce factor to" |
| Ключ строки / layout | `brick_set_bounce_factor` / `brick_physics_set_bounce_factor` |
| Параметры | Formula: PHYSICS_BOUNCE_FACTOR |
| Дефолт в палитре | PHYSIC_BOUNCE_FACTOR x MULTIPLIER |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/SetBounceBrick.java:30` |
| Класс экшена | — (createSetBounceFactorAction (в ActionFactory класс *Action не найден)) |
| Описание | Задает упругость (коэффициент отскока) физическому обьекту. 0 = не отскакивает, 1 = полностью упругий. |

### 42. `SetFrictionBrick`

| Поле | Значение |
|---|---|
| Название RU | "Задать трение " |
| Название EN | "Set friction to" |
| Ключ строки / layout | `brick_set_friction` / `brick_physics_set_friction` |
| Параметры | Formula: PHYSICS_FRICTION |
| Дефолт в палитре | PHYSIC_FRICTION x MULTIPLIER |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/SetFrictionBrick.java:30` |
| Класс экшена | — (createSetFrictionAction (в ActionFactory класс *Action не найден)) |
| Описание | Задает трение обьекта |

### 43. `SetLinearDampingBrick`

| Поле | Значение |
|---|---|
| Название RU | Задать лин. торможение |
| Название EN | Set linear damping to |
| Ключ строки / layout | `brick_set_linear_damping` / `brick_set_linear_damping` |
| Параметры | Formula: PHYSICS_DAMPING |
| Дефолт в палитре | 0.0 |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/SetLinearDampingBrick.java:7` |
| Класс экшена | SetLinearDampingAction (catroid/src/main/java/org/catrobat/catroid/content/actions/SetLinearDampingAction.kt:8) — через createSetLinearDampingAction |
| Описание | Задает линейное затухание движения объекта, имитируя воду или сопротивление воздуха.Код: создаёт `SetLinearDampingAction` в `addActionToSequence`. |

### 44. `SetAngularDampingBrick`

| Поле | Значение |
|---|---|
| Название RU | Задать угл. торможение |
| Название EN | Set angular damping to |
| Ключ строки / layout | `brick_set_angular_damping` / `brick_set_angular_damping` |
| Параметры | Formula: PHYSICS_DAMPING |
| Дефолт в палитре | 0.0 |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/SetAngularDampingBrick.java:7` |
| Класс экшена | SetAngularDampingAction (catroid/src/main/java/org/catrobat/catroid/content/actions/SetAngularDampingAction.kt:8) — через createSetAngularDampingAction |
| Описание | Задает угловое затухание вращения объекта (сопротивление вращению).Код: создаёт `SetAngularDampingAction` в `addActionToSequence`. |

### 45. `SetGravityScaleBrick`

| Поле | Значение |
|---|---|
| Название RU | Задать масштаб гравитации |
| Название EN | Set gravity scale to |
| Ключ строки / layout | `brick_set_gravity_scale` / `brick_set_gravity_scale` |
| Параметры | Formula: PHYSICS_GRAVITY_SCALE |
| Дефолт в палитре | 1.0 |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/SetGravityScaleBrick.java:7` |
| Класс экшена | SetGravityScaleAction (catroid/src/main/java/org/catrobat/catroid/content/actions/SetGravityScaleAction.kt:8) — через createSetGravityScaleAction |
| Описание | Задает, насколько сильно гравитация действует на объект (0 — не действует, 1 — полная).Код: создаёт `SetGravityScaleAction` в `addActionToSequence`. |

### 46. `SetPhysicsBulletBrick`

| Поле | Значение |
|---|---|
| Название RU | Режим пули |
| Название EN | Set bullet mode |
| Ключ строки / layout | `brick_set_bullet` / `brick_set_bullet` |
| Параметры | Formula: PHYSICS_TOGGLE |
| Дефолт в палитре | 1.0 |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/SetPhysicsBulletBrick.java:7` |
| Класс экшена | SetBulletAction (catroid/src/main/java/org/catrobat/catroid/content/actions/SetBulletAction.kt:8) — через createSetBulletAction |
| Описание | Включает режим пули для объекта, чтобы он не проваливался сквозь стены при высокой скорости.Код: создаёт `SetBulletAction` в `addActionToSequence`. |

### 47. `SetPhysicsFixedRotationBrick`

| Поле | Значение |
|---|---|
| Название RU | Зафиксировать вращение |
| Название EN | Set fixed rotation |
| Ключ строки / layout | `brick_set_fixed_rotation` / `brick_set_fixed_rotation` |
| Параметры | Formula: PHYSICS_TOGGLE |
| Дефолт в палитре | 1.0 |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/SetPhysicsFixedRotationBrick.java:7` |
| Класс экшена | SetFixedRotationAction (catroid/src/main/java/org/catrobat/catroid/content/actions/SetFixedRotationAction.kt:8) — через createSetFixedRotationAction |
| Описание | Фиксирует вращение объекта — он перестает вращаться от столкновений.Код: создаёт `SetFixedRotationAction` в `addActionToSequence`. |

### 48. `SetPhysicsSensorBrick`

| Поле | Значение |
|---|---|
| Название RU | Сделать сенсором |
| Название EN | Set as sensor |
| Ключ строки / layout | `brick_set_physics_sensor` / `brick_set_physics_sensor` |
| Параметры | Formula: PHYSICS_TOGGLE |
| Дефолт в палитре | 1.0 |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/SetPhysicsSensorBrick.java:7` |
| Класс экшена | SetPhysicsSensorAction (catroid/src/main/java/org/catrobat/catroid/content/actions/SetPhysicsSensorAction.kt:8) — через createSetPhysicsSensorAction |
| Описание | Делает объект датчиком: он обнаруживает касания, но не сталкивается физически.Код: создаёт `SetPhysicsSensorAction` в `addActionToSequence`. |

### 49. `ApplyForceAtPointBrick`

| Поле | Значение |
|---|---|
| Название RU | Приложить силу |
| Название EN | Apply force |
| Ключ строки / layout | `brick_apply_force_at_point` / `brick_apply_force_at_point` |
| Параметры | Formula: PHYSICS_FORCE_X, PHYSICS_FORCE_Y, PHYSICS_POINT_X, PHYSICS_POINT_Y |
| Дефолт в палитре | 100.0, 0.0, 0.0, 0.0 |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/ApplyForceAtPointBrick.java:7` |
| Класс экшена | ApplyForceAtPointAction (catroid/src/main/java/org/catrobat/catroid/content/actions/ApplyForceAtPointAction.kt:10) — через createApplyForceAtPointAction |
| Описание | Прикладывает силу к спрайту в указанной точке (сила и точка по X/Y).Код: создаёт `ApplyForceAtPointAction` в `addActionToSequence`. |

### 50. `PerformRayCastBrick`

| Поле | Значение |
|---|---|
| Название RU | Пустить луч |
| Название EN | Perform Ray-Cast |
| Ключ строки / layout | `brick_raycast` / `brick_perform_raycast` |
| Параметры | Formula: RAY_ID, X_START, Y_START, X_END, Y_END |
| Дефолт в палитре | "ray", 0, 0, 300, 500 |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/PerformRayCastBrick.java:8` |
| Класс экшена | — (в addActionToSequence создание Action не найдено) |
| Описание | Пускает луч, может пригодиться для врагов, или стрельбы. ну а так же и др. |

## Физика - Соединения / Physics - Joints (`subcategory_motion_physics_joints`)

### 51. `CreateRevoluteJointBrick`

| Поле | Значение |
|---|---|
| Название RU | Создать шарнир |
| Название EN | Create Hinge Joint |
| Ключ строки / layout | `brick_create_revolute_joint` / `brick_create_revolute_joint` |
| Параметры | Formula: JOINT_ID, SPRITE, X_POSITION, Y_POSITION |
| Дефолт в палитре | "joint","sprite2",10,200 |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/CreateRevoluteJointBrick.java:9` |
| Класс экшена | CreateRevoluteJointAction (catroid/src/main/java/org/catrobat/catroid/content/actions/CreateRevoluteJointAction.kt:9) — через createRevoluteJointAction |
| Описание | Создает шарнир для 2-х спрайтов со смещениемКод: создаёт `CreateRevoluteJointAction` в `addActionToSequence`. |

### 52. `CreateDistanceJointBrick`

| Поле | Значение |
|---|---|
| Название RU | Создать пружину/стержень |
| Название EN | Create Spring/Rod Joint |
| Ключ строки / layout | `brick_create_distance_joint` / `brick_create_distance_joint` |
| Параметры | Formula: JOINT_ID, SPRITE, JOINT_LENGTH, JOINT_FREQUENCY, JOINT_DAMPING |
| Дефолт в палитре | "joint","sprite2","100","5","0.3" |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/CreateDistanceJointBrick.java:8` |
| Класс экшена | CreateDistanceJointAction (catroid/src/main/java/org/catrobat/catroid/content/actions/CreateDistanceJointAction.kt:9) — через createDistanceJointAction |
| Описание | Создает мягкий шарнир для 2-х спрайтов со смещением (как пружина)Код: создаёт `CreateDistanceJointAction` в `addActionToSequence`. |

### 53. `CreateWeldJointBrick`

| Поле | Значение |
|---|---|
| Название RU | Создать сварку |
| Название EN | Create Weld Joint |
| Ключ строки / layout | `brick_create_weld_joint` / `brick_create_weld_joint` |
| Параметры | Formula: JOINT_ID, SPRITE, X_POSITION, Y_POSITION |
| Дефолт в палитре | "joint","sprite2",10,200 |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/CreateWeldJointBrick.java:8` |
| Класс экшена | CreateWeldJointAction (catroid/src/main/java/org/catrobat/catroid/content/actions/CreateWeldJointAction.kt:10) — через createWeldJointAction |
| Описание | Создает жесткий шарнир для 2-х спрайтов со смещением (как сварка, например скрепили 2 обьекта палкой)Код: создаёт `CreateWeldJointAction` в `addActionToSequence`. |

### 54. `CreatePrismaticJointBrick`

| Поле | Значение |
|---|---|
| Название RU | Создать поршень/слайдер |
| Название EN | Create Piston/Slider |
| Ключ строки / layout | `brick_create_prismatic_joint` / `brick_create_prismatic_joint` |
| Параметры | Formula: JOINT_ID, SPRITE, X_POSITION, Y_POSITION, AXIS_X, AXIS_Y |
| Дефолт в палитре | "joint","sprite2",10,100,20,30 |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/CreatePrismaticJointBrick.java:8` |
| Класс экшена | CreatePrismaticJointAction (catroid/src/main/java/org/catrobat/catroid/content/actions/CreatePrismaticJointAction.kt:10) — через createPrismaticJointAction |
| Описание | Создает поршневой шарнир для 2-х спрайтов со смещением (фиксирует по оси)Код: создаёт `CreatePrismaticJointAction` в `addActionToSequence`. |

### 55. `CreatePulleyJointBrick`

| Поле | Значение |
|---|---|
| Название RU | Создать блок/полиспаст |
| Название EN | Create Pulley Joint |
| Ключ строки / layout | `brick_create_pulley_joint` / `brick_create_pulley_joint` |
| Параметры | Formula: JOINT_ID, SPRITE_A, SPRITE_B, GROUND_ANCHOR_A_X, GROUND_ANCHOR_A_Y, GROUND_ANCHOR_B_X, GROUND_ANCHOR_B_Y, RATIO |
| Дефолт в палитре | "joint","sprite1","sprite2",10,100,0,0,1f |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/CreatePulleyJointBrick.java:8` |
| Класс экшена | CreatePulleyJointAction (catroid/src/main/java/org/catrobat/catroid/content/actions/CreatePulleyJointAction.kt:11) — через createPulleyJointAction |
| Описание | Создает шарнир для 2-х спрайтов, который можно представить как лифт с противовесом\nТочка опоры А Точка опоры Б\n (gA)---------------------(gB)\n \| \|\n \| \|\n [Спрайт А] [Спрайт Б]Код: создаёт `CreatePulleyJointAction` в `addActionToSequence`. |

### 56. `CreateGearJointBrick`

| Поле | Значение |
|---|---|
| Название RU | Создать шестерню |
| Название EN | Create Gear Joint |
| Ключ строки / layout | `brick_create_gear_joint` / `brick_create_gear_joint` |
| Параметры | Formula: JOINT_ID, JOINT_A_ID, JOINT_B_ID, RATIO |
| Дефолт в палитре | "joint","jointA","jointB",1f |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/CreateGearJointBrick.java:8` |
| Класс экшена | CreateGearJointAction (catroid/src/main/java/org/catrobat/catroid/content/actions/CreateGearJointAction.kt:8) — через createGearJointAction |
| Описание | Создает шарнир для 2-х других шарниров, можно представить как шестерни. 1 шарнир будет вращаться в зависимости от другого, и наоборотКод: создаёт `CreateGearJointAction` в `addActionToSequence`. |

### 57. `DestroyJointBrick`

| Поле | Значение |
|---|---|
| Название RU | Удалить соединение |
| Название EN | Destroy Joint |
| Ключ строки / layout | `brick_destroy_joint` / `brick_destroy_joint` |
| Параметры | Formula: JOINT_ID |
| Дефолт в палитре | "joint" |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/DestroyJointBrick.java:9` |
| Класс экшена | DestroyJointAction (catroid/src/main/java/org/catrobat/catroid/content/actions/DestroyJointAction.kt:8) — через createDestroyJointAction |
| Описание | Удаляет соединение (шарнир)Код: создаёт `DestroyJointAction` в `addActionToSequence`. |

## Роботы и эффекты / Robots & Particles (`subcategory_motion_robots`)

### 58. `FadeParticleEffectBrick`

| Поле | Значение |
|---|---|
| Название RU | Эффект частиц |
| Название EN | Fade particle effect |
| Ключ строки / layout | `fade_particle_effect` / `brick_fade_particle_effect` |
| Параметры | конструктор: fadeType: Int = FADE_IN; спиннер (выбор из списка в UI) |
| Дефолт в палитре | — |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/FadeParticleEffectBrick.kt:35` |
| Класс экшена | FadeParticleEffectAction (catroid/src/main/java/org/catrobat/catroid/content/actions/FadeParticleEffectAction.kt:29) — через createFadeParticleEffectsAction |
| Описание | Включает или выключает эффект частицКод: создаёт `FadeParticleEffectAction` в `addActionToSequence`. |

### 59. `PhiroMotorMoveForwardBrick` — только если Phiro включён

| Поле | Значение |
|---|---|
| Название RU | Двигать мотор Phiro вперед |
| Название EN | Move Phiro motor forward |
| Ключ строки / layout | `brick_phiro_motor_forward_action` / `brick_phiro_motor_forward` |
| Параметры | Formula: PHIRO_SPEED; спиннер (выбор из списка в UI) |
| Дефолт в палитре | MOTOR_LEFT, PHIRO_SPEED |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/PhiroMotorMoveForwardBrick.java:41` |
| Класс экшена | PhiroMotorMoveForwardAction (catroid/src/main/java/org/catrobat/catroid/content/actions/PhiroMotorMoveForwardAction.java:39) — через createPhiroMotorMoveForwardAction |
| Описание | Заставляет выбранный мотор робота Phiro крутиться вперед с заданной скоростью. ВАЖНО: требует подключения по Bluetooth.Код: создаёт `PhiroMotorMoveForwardAction` в `addActionToSequence`. |

### 60. `PhiroMotorMoveBackwardBrick` — только если Phiro включён

| Поле | Значение |
|---|---|
| Название RU | Двигать мотор Phiro назад |
| Название EN | Move Phiro motor backward |
| Ключ строки / layout | `brick_phiro_motor_backward_action` / `brick_phiro_motor_backward` |
| Параметры | Formula: PHIRO_SPEED; спиннер (выбор из списка в UI) |
| Дефолт в палитре | MOTOR_LEFT, PHIRO_SPEED |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/PhiroMotorMoveBackwardBrick.java:41` |
| Класс экшена | PhiroMotorMoveBackwardAction (catroid/src/main/java/org/catrobat/catroid/content/actions/PhiroMotorMoveBackwardAction.java:39) — через createPhiroMotorMoveBackwardAction |
| Описание | Заставляет выбранный мотор робота Phiro крутиться назад с заданной скоростью. ВАЖНО: требует подключения по Bluetooth.Код: создаёт `PhiroMotorMoveBackwardAction` в `addActionToSequence`. |

### 61. `PhiroMotorStopBrick` — только если Phiro включён

| Поле | Значение |
|---|---|
| Название RU | Остановить мотор Phiro |
| Название EN | Stop Phiro motor |
| Ключ строки / layout | `phiro_motor_stop` / `brick_phiro_motor_stop` |
| Параметры | конструктор: Motor motorEnum; спиннер (выбор из списка в UI) |
| Дефолт в палитре | MOTOR_BOTH |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/PhiroMotorStopBrick.java:37` |
| Класс экшена | PhiroMotorStopAction (catroid/src/main/java/org/catrobat/catroid/content/actions/PhiroMotorStopAction.java:34) — через createPhiroMotorStopAction |
| Описание | Останавливает выбранный мотор робота Phiro. ВАЖНО: требует подключения по Bluetooth.Код: создаёт `PhiroMotorStopAction` в `addActionToSequence`. |

---
Проверено по: `CategoryBricksFactory.kt`, `content/BrickInfo.java`, классам `content/bricks/*`, `content/actions/*` (через `content/ActionFactory.java`), `res/values/strings.xml`, `res/values-ru/strings.xml`.
