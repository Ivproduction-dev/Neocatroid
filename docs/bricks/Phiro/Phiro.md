# Phiro — блоки

- Строка категории: `category_phiro` — RU «Phiro», EN «Phiro» (`catroid/src/main/res/values/strings.xml:2913`).
- Палитра: `setupPhiroProCategoryList()` в `catroid/src/main/java/org/catrobat/catroid/ui/fragment/CategoryBricksFactory.kt:3169-3184` (порядок ниже — по ней).
- Описания RU/EN: `catroid/src/main/java/org/catrobat/catroid/content/BrickInfo.java:897-908`.
- Дефолты: `PHIRO_SPEED=100`, `PHIRO_DURATION=1`, свет R/G/B `0/255/255`, сенсор дефолт «Front Left Sensor» (`catroid/src/main/java/org/catrobat/catroid/common/BrickValues.java:140-145`).

| № | Название RU / EN | Параметры | Класс брика | Класс экшена | Описание |
|---|---|---|---|---|---|
| 1 | RU «Двигать мотор Phiro вперед … Скорость» / EN «Move Phiro motor forward … Speed» | Спиннер enum `Motor` (LEFT дефолт, RIGHT, BOTH); формула `PHIRO_SPEED` (дефолт 100). Фабрика `createPhiroMotorMoveForwardAction` (`content/ActionFactory.java:1211`) | `content/bricks/PhiroMotorMoveForwardBrick.java:41` | `content/actions/PhiroMotorMoveForwardAction.java:39` | Крутит мотор вперед с заданной скоростью. Требует Bluetooth. |
| 2 | RU «Двигать мотор Phiro назад … Скорость» / EN «Move Phiro motor backward … Speed» | Спиннер enum `Motor` (LEFT дефолт, RIGHT, BOTH); формула `PHIRO_SPEED`. Фабрика `createPhiroMotorMoveBackwardAction` (`content/ActionFactory.java:1221`) | `content/bricks/PhiroMotorMoveBackwardBrick.java:41` | `content/actions/PhiroMotorMoveBackwardAction.java:39` | Крутит мотор назад с заданной скоростью. Требует Bluetooth. |
| 3 | RU «Остановить мотор Phiro» / EN «Stop Phiro motor» | Спиннер enum `Motor` (BOTH дефолт); формул нет. Фабрика `createPhiroMotorStopAction` (`content/ActionFactory.java:1254`) | `content/bricks/PhiroMotorStopBrick.java:37` | `content/actions/PhiroMotorStopAction.java:34` | Останавливает выбранный мотор. Требует Bluetooth. |
| 4 | RU «Воспроизвести музыку Phiro … тон … Продолжительность» / EN «Play Phiro music … tone … Duration» | Спиннер enum `Tone` (DO дефолт, RE, MI, FA, SO, LA, TI); формула `PHIRO_DURATION_IN_SECONDS` (дефолт 1). Фабрика `createPhiroPlayToneActionAction` (`content/ActionFactory.java:1201`) | `content/bricks/PhiroPlayToneBrick.java:38` | `content/actions/PhiroPlayToneAction.java:39` | Играет ноту заданное время. Требует Bluetooth. |
| 5 | RU «Задать свет Phiro … Красный … Зеленый … Синий» / EN «Set Phiro light … Red … Green … Blue» | Спиннер enum `Eye` (BOTH дефолт, LEFT, RIGHT); формулы `PHIRO_LIGHT_RED/GREEN/BLUE` (дефолты 0/255/255). Фабрика `createPhiroRgbLedEyeAction` (`content/ActionFactory.java:1231`) | `content/bricks/PhiroRGBLightBrick.java:44` | `content/actions/PhiroRGBLightAction.java:39` | Зажигает RGB-LED выбранным цветом. Требует Bluetooth. |
| 6 | RU «Если Phiro … активен … Иначе» / EN «If Phiro … is activated … Else» | Спиннер `R.array.brick_phiro_select_sensor_spinner` (позиция сенсора); ветки if/else (`ScriptSequenceAction`). Фабрика `createPhiroSendSelectedSensorAction` (`content/ActionFactory.java:1243`) | `content/bricks/PhiroIfLogicBeginBrick.java:45` | `content/actions/PhiroSensorAction.java:36` (extends `Action`) | Ветвление по сенсору Phiro. Требует Bluetooth. |

Дополнительно палитра добавляет 6 × `SetVariableBrick` с сенсорами `Sensors.PHIRO_FRONT_LEFT/RIGHT`, `PHIRO_SIDE_LEFT/RIGHT`, `PHIRO_BOTTOM_LEFT/RIGHT` (чтение сенсоров в переменную, `CategoryBricksFactory.kt:3177-3182`).
