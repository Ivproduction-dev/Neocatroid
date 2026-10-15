# Свет (2D) / Light 2D

Категория `category_light2d`: RU «Свет (2D)», EN «Light 2D».

Источник состава и порядка: `catroid/src/main/java/org/catrobat/catroid/ui/fragment/CategoryBricksFactory.kt:3530`, метод `setupLight2DCategoryList`.

Правила документа:

- Порядок бриков строго по фабрике; подгруппы — `SubCategoryHeaderBrick` из того же метода.
- Названия RU/EN — по первому `@string` главного layout (`getViewResource`) из `catroid/src/main/res/values/strings.xml` и `values-ru/strings.xml`; у бриков без текстовой подписи — значения `R.array.*` из `arrays.xml`.
- Параметры — `addAllowedBrickField(BrickField.*)` + спиннер по факту кода брика.
- Экшен — фактический класс из `content/actions` через `ActionFactory.create*` (резолв по `ActionFactory.java`); у ScriptBrick экшена нет.
- Описание — 1–2 строки строго по `BrickInfo.java` + факт кода; домыслов нет.

Всего бриков в этом файле: 5 (заголовки подгрупп не считаются).

### 1. `CreateLight2DBrick`

| Поле | Значение |
|---|---|
| Название RU | Создать 2D свет |
| Название EN | Create 2D light |
| Ключ строки / layout | `brick_create_light_2d_title` / `brick_create_light_2d` |
| Параметры | Formula: TEXT (`brick_create_light_2d_name`); Formula: X_POSITION (`brick_create_light_2d_x`); Formula: Y_POSITION (`brick_create_light_2d_y`); Formula: VALUE_1 (`brick_create_light_2d_intensity`); Formula: VALUE_2 (`brick_create_light_2d_radius`); Spinner `R.array.light2d_types` EN: Point Light / Astra Light / Spotlight; RU: Точечный свет (Point Light) / Астральный свет (Astra Light) / Прожектор (Spotlight) |
| Дефолт в палитре | ("light1", 0.0, 0.0, 0, 1.0, 400.0) |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/CreateLight2DBrick.java:14` |
| Класс экшена | CreateLight2DAction (catroid/src/main/java/org/catrobat/catroid/content/actions/CreateLight2DAction.java:9) — через createLight2DCreateAction |
| Описание | Создает 2D источник света с именем, позицией, типом, яркостью и радиусом EN: Creates a 2D light source with name, position, type, intensity and radius Код: создаёт `CreateLight2DAction` в `addActionToSequence` через `createLight2DCreateAction`. |

### 2. `ModifyLight2DPropertyBrick`

| Поле | Значение |
|---|---|
| Название RU | Изменить свойство 2D света |
| Название EN | Change 2D light property |
| Ключ строки / layout | `brick_light_2d_modify_title` / `brick_modify_light_2d_property` |
| Параметры | Formula: TEXT (`brick_modify_light_2d_name`); Formula: OBJECT_NAME (`brick_modify_light_2d_object`); Formula: X_POSITION (`brick_modify_light_2d_x`); Formula: Y_POSITION (`brick_modify_light_2d_y`); Formula: VALUE (`brick_modify_light_2d_value`); Spinner `R.array.light2d_modify_properties` EN: Position X/Y / Radius / Intensity / Color / Ambient brightness |
| Дефолт в палитре | ("light1", 0, "", "0", "0", "1.0") |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/ModifyLight2DPropertyBrick.java:15` |
| Класс экшена | DeleteCloneByNumberAction (catroid/src/main/java/org/catrobat/catroid/content/actions/DeleteCloneByNumberAction.kt:8) — через createModifyLight2DPropertyAction |
| Описание | Изменяет свойство 2D света: координаты X/Y, радиус, интенсивность, цвет или общую темноту (ambient). EN: Changes a 2D light property: position X/Y, radius, intensity, color or ambient brightness. Код: создаёт `DeleteCloneByNumberAction` в `addActionToSequence` через `createModifyLight2DPropertyAction`. |

### 3. `Light2DControlBrick`

| Поле | Значение |
|---|---|
| Название RU | Управлять 2D светом |
| Название EN | Control 2D light |
| Ключ строки / layout | `brick_light_2d_control_title` / `brick_light_2d_control` |
| Параметры | Formula: TEXT (`brick_light_2d_control_name`); Formula: VALUE_1 (`brick_light_2d_control_value`); Spinner `R.array.light2d_control_actions` EN: Turn on / Turn off / Delete / Set radius / Set intensity / Set color / Set X / Set Y / Attach to sprite / Detach from sprite / Shadows on / Shadows off / Set ambient light; RU: Включить / Выключить / Удалить / Задать радиус / Задать интенсивность / Задать цвет / Задать X / Задать Y / Привязать к объекту / Отвязать от объекта / Тени вкл / Тени выкл / Задать фоновый свет |
| Дефолт в палитре | ("light1", 0, "1.0", "") |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/Light2DControlBrick.java:20` |
| Класс экшена | ControlLight2DAction (catroid/src/main/java/org/catrobat/catroid/content/actions/ControlLight2DAction.java:8) — через createLight2DControlAction |
| Описание | Управляет 2D светом: вкл/выкл, удаление, радиус, цвет, привязка к объекту, тени EN: Controls a 2D light: on/off, delete, radius, color, sprite attach, shadows Код: создаёт `ControlLight2DAction` в `addActionToSequence` через `createLight2DControlAction`. |

### 4. `ShadowCasting2DBrick`

| Поле | Значение |
|---|---|
| Название RU | Отбрасывание тени |
| Название EN | Shadow casting |
| Ключ строки / layout | `brick_shadow_casting_2d_title` / `brick_shadow_casting_2d` |
| Параметры | Spinner `R.array.light2d_shadow_modes` EN: Cast shadow / No shadow; RU: Отбрасывает тень / Без тени |
| Дефолт в палитре | ("", 0) |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/ShadowCasting2DBrick.java:19` |
| Класс экшена | ShadowCasting2DAction (catroid/src/main/java/org/catrobat/catroid/content/actions/ShadowCasting2DAction.java:6) — через createShadowCasting2DAction |
| Описание | Включает или выключает отбрасывание тени объектом для 2D света EN: Enables or disables shadow casting for a sprite in 2D light Код: создаёт `ShadowCasting2DAction` в `addActionToSequence` через `createShadowCasting2DAction`. |

### 5. `ToggleLight2DBrick`

| Поле | Значение |
|---|---|
| Название RU | Включить/выключить свет под именем |
| Название EN | Toggle light with name |
| Ключ строки / layout | `brick_toggle_light_2d_label` / `brick_toggle_light_2d` |
| Параметры | Formula: TEXT (`brick_toggle_light_2d_name`) |
| Дефолт в палитре | ("light1") |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/ToggleLight2DBrick.java:8` |
| Класс экшена | ToggleLight2DAction (catroid/src/main/java/org/catrobat/catroid/content/actions/ToggleLight2DAction.java:8) — через createToggleLight2DAction |
| Описание | Переключает 2D свет: если свет с именем выключен — включает его, если включен — выключает EN: Toggles 2D light state: turns on if off, turns off if on Код: создаёт `ToggleLight2DAction` в `addActionToSequence` через `createToggleLight2DAction`. |
