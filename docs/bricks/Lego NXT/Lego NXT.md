# Lego NXT — блоки

- Строка категории: `category_lego_nxt` — RU «Лего NXT», EN «Lego NXT» (`catroid/src/main/res/values/strings.xml:2909`).
- Палитра: `setupLegoNxtCategoryList()` в `catroid/src/main/java/org/catrobat/catroid/ui/fragment/CategoryBricksFactory.kt:3116-3123` (порядок ниже — по ней).
- Описания RU/EN: `catroid/src/main/java/org/catrobat/catroid/content/BrickInfo.java:857-864`.
- Дефолты: `BrickValues.LEGO_ANGLE=180`, `LEGO_SPEED=100`, `LEGO_DURATION=1.0`, `LEGO_FREQUENCY=2.0` (`catroid/src/main/java/org/catrobat/catroid/common/BrickValues.java:93-96`).

| № | Название RU / EN | Параметры | Класс брика | Класс экшена | Описание |
|---|---|---|---|---|---|
| 1 | RU «Повернуть мотор NXT … на … °» / EN «Turn NXT motor … by … °» | Спиннер мотора `nxt_motor_chooser` (A, B, C, B+C); формула `LEGO_NXT_DEGREES` (градусы, дефолт 180) | `content/bricks/LegoNxtMotorTurnAngleBrick.java:38` | `content/actions/LegoNxtMotorTurnAngleAction.java:39` (`createLegoNxtMotorTurnAngleAction`) | Поворачивает выбранный мотор на заданные градусы. Требует Bluetooth-подключения к LEGO NXT. |
| 2 | RU «Остановить мотор NXT» / EN «Stop NXT motor» | Спиннер мотора `nxt_stop_motor_chooser` (A, B, C, B+C, все моторы); формул нет | `content/bricks/LegoNxtMotorStopBrick.java:37` | `content/actions/LegoNxtMotorStopAction.java:34` (`createLegoNxtMotorStopAction`) | Останавливает выбранный мотор (или все). Требует Bluetooth-подключения к LEGO NXT. |
| 3 | RU «Задать мотор NXT … скорость … со значением» / EN «Set NXT motor … speed … to» | Спиннер мотора `nxt_motor_chooser` (A, B, C, B+C); формула `LEGO_NXT_SPEED` (%, дефолт 100) | `content/bricks/LegoNxtMotorMoveBrick.java:38` | `content/actions/LegoNxtMotorMoveAction.java:39` (`createLegoNxtMotorMoveAction`) | Запускает выбранный мотор с заданной скоростью. Требует Bluetooth-подключения к LEGO NXT. |
| 4 | RU «Играть тон NXT … Частота … ×100Гц … Продолжительность» / EN «Play NXT tone … Frequency … ×100Hz … Duration» | Формулы `LEGO_NXT_FREQUENCY` (дефолт 2.0 = 200 Гц), `LEGO_NXT_DURATION_IN_SECONDS` (дефолт 1.0); спиннера нет | `content/bricks/LegoNxtPlayToneBrick.java:33` | `content/actions/LegoNxtPlayToneAction.java:38` (`createLegoNxtPlayToneAction`) | Проигрывает тон заданной частоты указанное время (сек). Требует Bluetooth-подключения к LEGO NXT. |

Все брики добавляют ресурс `BLUETOOTH_LEGO_NXT` (`addRequiredResources`).
