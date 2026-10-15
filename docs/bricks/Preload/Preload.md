# Предзагрузка / Preload

Ключ категории: `category_preload`. Источник состава и порядка: `CategoryBricksFactory.kt` (группированный режим).

### 1. PreloadSceneBrick

| Поле | Значение |
|---|---|
| Название (тексты лейаута) | `brick_preload_scene`: RU «Предзагрузить сцену» / EN «Preload scene» |
| Параметры | спиннеры: `Scene` |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/PreloadSceneBrick.java:41` |
| Класс экшена | `catroid/src/main/java/org/catrobat/catroid/content/actions/PreloadSceneAction.kt:29` (PreloadSceneAction) |
| Описание (BrickInfo.java) | RU: Предзагружает выбранную сцену в память до её запуска, чтобы переключение между сценами происходило быстрее и без задержек.<br>EN: Preloads the selected scene into memory before it is started, so switching between scenes happens faster and without lag. |


### 2. ScenePreloadedBrick

| Поле | Значение |
|---|---|
| Название (тексты лейаута) | `brick_scene_preloaded`: RU «Сцена предзагружена?» / EN «Scene preloaded?»<br>`brick_scene_preloaded_save_to`: RU «сохранить в» / EN «save to» |
| Параметры | спиннеры: `Scene`, `UserVariable` |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/ScenePreloadedBrick.java:42` |
| Класс экшена | `catroid/src/main/java/org/catrobat/catroid/content/actions/ScenePreloadedAction.kt:28` (ScenePreloadedAction) |
| Описание (BrickInfo.java) | RU: Проверяет, была ли сцена предзагружена, и записывает 1 (да) или 0 (нет) в выбранную переменную.<br>EN: Checks whether the scene was preloaded and stores 1 (yes) or 0 (no) into the chosen variable. |

