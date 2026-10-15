# AR.Drone 2.0 — блоки

- Строка категории: `category_drone` — RU «AR.Drone 2.0», EN «AR.Drone 2.0» (`catroid/src/main/res/values/strings.xml:2911`).
- Палитра: `setupDroneCategoryList()` в `catroid/src/main/java/org/catrobat/catroid/ui/fragment/CategoryBricksFactory.kt:3135-3151` (порядок ниже — по ней).
- Описания RU/EN: `catroid/src/main/java/org/catrobat/catroid/content/BrickInfo.java:725-750`.
- Дефолты движений: время `BrickValues.DRONE_MOVE_BRICK_DEFAULT_TIME_MILLISECONDS=1000` (мс), мощность `DRONE_MOVE_BRICK_DEFAULT_POWER_PERCENT=20` (%) (`catroid/src/main/java/org/catrobat/catroid/common/BrickValues.java:99-100`).

| № | Название RU / EN | Параметры | Класс брика | Класс экшена | Описание |
|---|---|---|---|---|---|
| 1 | RU «Поднять вверх / посадить AR.Drone 2.0» / EN «Take off / land AR.Drone 2.0» | Нет | `content/bricks/DroneTakeOffLandBrick.java:29` | `content/actions/DroneTakeOffLandAction.java:32` | Взлет/посадка. Требует подключенного дрона. |
| 2 | RU «Авария AR.Drone 2.0» / EN «Emergency AR.Drone 2.0» | Нет | `content/bricks/DroneEmergencyBrick.java:29` | `content/actions/DroneEmergencyAction.java:32` | Аварийная остановка (немедленная посадка). |
| 3 | RU «Переместить AR.Drone 2.0 вверх» / EN «Move AR.Drone 2.0 up» | Формулы `DRONE_TIME_TO_FLY_IN_SECONDS` (сек), `DRONE_POWER_IN_PERCENT` (%) | `content/bricks/DroneMoveUpBrick.java:33` | `content/actions/DroneMoveUpAction.java:34` | Подъем заданное время с заданной мощностью. |
| 4 | RU «Переместить AR.Drone 2.0 вниз» / EN «Move AR.Drone 2.0 down» | Те же 2 формулы | `content/bricks/DroneMoveDownBrick.java:33` | `content/actions/DroneMoveDownAction.java:34` | Снижение заданное время с заданной мощностью. |
| 5 | RU «Переместить AR.Drone 2.0 влево» / EN «Move AR.Drone 2.0 left» | Те же 2 формулы | `content/bricks/DroneMoveLeftBrick.java:33` | `content/actions/DroneMoveLeftAction.java:34` | Движение влево заданное время с заданной мощностью. |
| 6 | RU «Переместить AR.Drone 2.0 вправо» / EN «Move AR.Drone 2.0 right» | Те же 2 формулы | `content/bricks/DroneMoveRightBrick.java:33` | `content/actions/DroneMoveRightAction.java:34` | Движение вправо заданное время с заданной мощностью. |
| 7 | RU «Переместить AR.Drone 2.0 вперед» / EN «Move AR.Drone 2.0 forward» | Те же 2 формулы | `content/bricks/DroneMoveForwardBrick.java:33` | `content/actions/DroneMoveForwardAction.java:34` | Движение вперед заданное время с заданной мощностью. |
| 8 | RU «Переместить AR.Drone 2.0 назад» / EN «Move AR.Drone 2.0 backward» | Те же 2 формулы | `content/bricks/DroneMoveBackwardBrick.java:33` | `content/actions/DroneMoveBackwardAction.java:34` | Движение назад заданное время с заданной мощностью. |
| 9 | RU «Повернуть AR.Drone 2.0 влево» / EN «Turn AR.Drone 2.0 left» | Те же 2 формулы | `content/bricks/DroneTurnLeftBrick.java:33` | `content/actions/DroneTurnLeftAction.java:34` | Поворот влево заданное время с заданной мощностью. |
| 10 | RU «Повернуть AR.Drone 2.0 вправо» / EN «Turn AR.Drone 2.0 right» | Те же 2 формулы | `content/bricks/DroneTurnRightBrick.java:33` | `content/actions/DroneTurnRightAction.java:34` | Поворот вправо заданное время с заданной мощностью. |
| 11 | RU «Перевернуть AR.Drone 2.0» / EN «Flip AR.Drone 2.0» | Нет | `content/bricks/DroneFlipBrick.java:29` | `content/actions/DroneFlipAction.java:32` | Флип (переворот) дрона. |
| 12 | RU «Воспроизвести флеш-анимацию AR.Drone 2.0» / EN «Play AR.Drone 2.0 flash animation» | Спиннер `R.array.brick_drone_play_led_animation_spinner` → строка `animationName` (фабрика `createDronePlayLedAnimationAction`, `content/ActionFactory.java:7726`) | `content/bricks/DronePlayLedAnimationBrick.java:41` | `content/actions/DronePlayLedAnimationAction.java:32` | Проигрывает выбранную LED-анимацию. Требует подключенного дрона. |
| 13 | RU «Переключить камеру AR.Drone 2.0» / EN «Switch AR.Drone 2.0 camera» | Нет | `content/bricks/DroneSwitchCameraBrick.java:29` | `content/actions/DroneSwitchCameraAction.java:32` | Переключает камеру (передняя/нижняя). Требует подключенного дрона. |
