# Pathfinder / Поиск пути

Ключ категории: `category_pathfinder` (`res/values/strings.xml`: "Pathfinder"; `res/values-ru/strings.xml`: "Поиск пути").
Состав и порядок — строго по `CategoryBricksFactory.kt` → `setupPathfinderCategoryList` (заголовки `SubCategoryHeaderBrick` не являются блоками и пропущены).

Блоков: 7.

## 1. `GridBrick`

| Поле | Значение |
|---|---|
| Название RU | Создать сетку |
| Название EN | Create grid |
| Строки (`strings.xml` → `values-ru`) | `pathfinder_grid`: EN="Create grid" / RU="Создать сетку"; `pathfinder_grid_x`: EN="X" / RU="X"; `pathfinder_grid_y`: EN="Y" / RU="Y"; `pathfinder_grid_w`: EN="Width" / RU="Ширина" |
| Параметры | `POSX` (`brick_grid_edit_x`); `POSY` (`brick_grid_edit_y`); `SIZE_X` (`brick_grid_edit_width`); `SIZE_Y` (`brick_grid_edit_height`) |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/GridBrick.java:38` |
| Класс экшена (`content/actions`) | `GridAction` (`catroid/src/main/java/org/catrobat/catroid/content/actions/GridAction.kt:32`; via `createGridAction`, `ActionFactory.java:6017`) |
| Описание (строго `BrickInfo.java` + код) | RU: Создает сетку (grid) для поиска пути в Pathfinder'е по заданным координатам и размеру.<br>EN: Creates a grid for the Pathfinder pathfinding system at the given coordinates and size. |
| Вызов в фабрике | `GridBrick()` |
| Layout | `catroid/src/main/res/layout/brick_grid.xml` |

## 2. `MoveToObjectBrick`

| Поле | Значение |
|---|---|
| Название RU | Идти к |
| Название EN | Go to |
| Строки (`strings.xml` → `values-ru`) | `pathfinder_move_to`: EN="Go to" / RU="Идти к"; `pathfinder_avoid`: EN="Avoid" / RU="Избегать"; `pathfinder_mode`: EN="Mode" / RU="Режим"; `pathfinder_strategy`: EN="Path" / RU="Путь" |
| Параметры | `SPRITE` (`brick_move_to_avoid_edit`); `SPEED` (`brick_move_to_speed_edit`); `ROTATE_OBJECT` (`brick_move_to_rotate_edit`); `DYNAMIC_REPLANNING` (`brick_move_to_replan_edit`); спиннер (`brick_move_to_spinner`); спиннер (`brick_move_to_mode_spinner`); спиннер (`brick_move_to_strategy_spinner`); спиннер (`brick_move_to_size_spinner`); спиннер (`brick_move_to_blocked_spinner`) |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/MoveToObjectBrick.java:54` |
| Класс экшена (`content/actions`) | `MoveToObjectAction` (`catroid/src/main/java/org/catrobat/catroid/content/actions/MoveToObjectAction.kt:32`; via `createMoveToObjectAction`, `ActionFactory.java:8482`) |
| Описание (строго `BrickInfo.java` + код) | RU: Перемещает объект к выбранному целевому объекту с помощью поиска пути, с заданной скоростью и списком объектов для обхода. Можно выбрать режим движения (точно / до касания), учёт размера и поведение при заблокированном пути.<br>EN: Moves the object toward the selected target object using pathfinding, with the given speed and a list of objects to avoid. You can choose the movement mode (precise / until touching), size checking, and the behavior when the path is blocked. |
| Вызов в фабрике | `MoveToObjectBrick()` |
| Layout | `catroid/src/main/res/layout/brick_move_to_object.xml` |

## 3. `StopMovementBrick`

| Поле | Значение |
|---|---|
| Название RU | Остановить |
| Название EN | Stop |
| Строки (`strings.xml` → `values-ru`) | `pathfinder_stop`: EN="Stop" / RU="Остановить" |
| Параметры | — |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/StopMovementBrick.java:30` |
| Класс экшена (`content/actions`) | `StopMovementAction` (`catroid/src/main/java/org/catrobat/catroid/content/actions/StopMovementAction.kt:30`; via `createStopMovementAction`, `ActionFactory.java:6041`) |
| Описание (строго `BrickInfo.java` + код) | RU: Останавливает движение спрайта, заданное путевым поиском (Pathfinder).<br>EN: Stops the sprite's movement driven by the pathfinder. |
| Вызов в фабрике | `StopMovementBrick()` |
| Layout | `catroid/src/main/res/layout/brick_stop_movement.xml` |

## 4. `ContinueMovementBrick`

| Поле | Значение |
|---|---|
| Название RU | Продолжить |
| Название EN | Continue |
| Строки (`strings.xml` → `values-ru`) | `pathfinder_continue`: EN="Continue" / RU="Продолжить" |
| Параметры | — |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/ContinueMovementBrick.java:30` |
| Класс экшена (`content/actions`) | `ContinueMovementAction` (`catroid/src/main/java/org/catrobat/catroid/content/actions/ContinueMovementAction.kt:30`; via `createContinueMovementAction`, `ActionFactory.java:6048`) |
| Описание (строго `BrickInfo.java` + код) | RU: Возобновляет движение спрайта по пути (следование), если оно было приостановлено.<br>EN: Resumes the sprite's path following movement if it was paused. |
| Вызов в фабрике | `ContinueMovementBrick()` |
| Layout | `catroid/src/main/res/layout/brick_continue_movement.xml` |

## 5. `HasPathBrick`

| Поле | Значение |
|---|---|
| Название RU | Есть путь к |
| Название EN | Has path to |
| Строки (`strings.xml` → `values-ru`) | `pathfinder_has_path`: EN="Has path to" / RU="Есть путь к"; `pathfinder_has_path_var`: EN="Store in" / RU="Сохранить в" |
| Параметры | `VARIABLE` (`brick_has_path_var_edit`); спиннер (`brick_has_path_spinner`) |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/HasPathBrick.java:42` |
| Класс экшена (`content/actions`) | `HasPathAction` (`catroid/src/main/java/org/catrobat/catroid/content/actions/HasPathAction.kt:31`; via `createHasPathAction`, `ActionFactory.java:6055`) |
| Описание (строго `BrickInfo.java` + код) | RU: Проверяет, существует ли путь до выбранного обьекта (через Pathfinder), и записывает 1 если путь найден, иначе 0 в переменную.<br>EN: Checks whether a path to the chosen object exists (via Pathfinder) and writes 1 if a path is found, otherwise 0, into a variable. |
| Вызов в фабрике | `HasPathBrick()` |
| Layout | `catroid/src/main/res/layout/brick_has_path.xml` |

## 6. `SmoothPathBrick`

| Поле | Значение |
|---|---|
| Название RU | Сгладить путь |
| Название EN | Smooth path |
| Строки (`strings.xml` → `values-ru`) | `pathfinder_smooth`: EN="Smooth path" / RU="Сгладить путь" |
| Параметры | `SPRITE_NAME` (`brick_smooth_path_sprite_edit`) |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/SmoothPathBrick.kt:8` |
| Класс экшена (`content/actions`) | `SmoothPathAction` (`catroid/src/main/java/org/catrobat/catroid/content/actions/SmoothPathAction.kt:8`; via `createSmoothPathAction`, `ActionFactory.java:6160`) |
| Описание (строго `BrickInfo.java` + код) | — |
| Вызов в фабрике | `SmoothPathBrick()` |
| Layout | `catroid/src/main/res/layout/brick_smooth_path.xml` |

## 7. `EnableDynamicReplanningBrick`

| Поле | Значение |
|---|---|
| Название RU | Дин. перестроение |
| Название EN | Dynamic replan |
| Строки (`strings.xml` → `values-ru`) | `pathfinder_replan`: EN="Dynamic replan" / RU="Дин. перестроение"; `pathfinder_replan_enabled`: EN="Enabled (1/0)" / RU="Включено (1/0)" |
| Параметры | `SPRITE_NAME` (`brick_dynamic_replan_sprite_edit`); `ENABLED` (`brick_dynamic_replan_enabled_edit`) |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/EnableDynamicReplanningBrick.kt:8` |
| Класс экшена (`content/actions`) | `EnableDynamicReplanningAction` (`catroid/src/main/java/org/catrobat/catroid/content/actions/EnableDynamicReplanningAction.kt:8`; via `createEnableDynamicReplanningAction`, `ActionFactory.java:6168`) |
| Описание (строго `BrickInfo.java` + код) | — |
| Вызов в фабрике | `EnableDynamicReplanningBrick("sprite", "1")` |
| Layout | `catroid/src/main/res/layout/brick_dynamic_replanning.xml` |
