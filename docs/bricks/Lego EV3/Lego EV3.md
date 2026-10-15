# Lego EV3 — блоки

- Строка категории: `category_lego_ev3` — RU «Лего EV3», EN «Lego EV3» (`catroid/src/main/res/values/strings.xml:2910`).
- Палитра: `setupLegoEv3CategoryList()` в `catroid/src/main/java/org/catrobat/catroid/ui/fragment/CategoryBricksFactory.kt:3125-3133` (порядок ниже — по ней).
- Описания RU/EN: `catroid/src/main/java/org/catrobat/catroid/content/BrickInfo.java:847-856`.
- Дефолты: `BrickValues.LEGO_ANGLE=180`, `LEGO_SPEED=100`, `LEGO_DURATION=1.0`, `LEGO_FREQUENCY=2.0`, `LEGO_VOLUME=100.0` (`catroid/src/main/java/org/catrobat/catroid/common/BrickValues.java:93-97`).

| № | Название RU / EN | Параметры | Класс брика | Класс экшена | Описание |
|---|---|---|---|---|---|
| 1 | RU «Повернуть мотор EV3 … на … °» / EN «Turn EV3 motor … by … °» | Спиннер `ev3_motor_chooser`, enum `Motor` (A, B, C, D, B+C, все); формула `LEGO_EV3_DEGREES` (дефолт 180) | `content/bricks/LegoEv3MotorTurnAngleBrick.java:38` | `content/actions/LegoEv3MotorTurnAngleAction.java:39` (`createLegoEv3MotorTurnAngleAction`) | Поворачивает мотор на заданные градусы. Требует EV3 по Bluetooth. |
| 2 | RU «Задать мотор EV3 … скорость … со значением» / EN «Set EV3 motor … speed … to» | Спиннер `ev3_motor_chooser`, enum `Motor` (A, B, C, D, B+C); формула `LEGO_EV3_SPEED` (дефолт 100) | `content/bricks/LegoEv3MotorMoveBrick.java:38` | `content/actions/LegoEv3MotorMoveAction.java:39` (фабрика `createLegoEv3SingleMotorMoveAction`, `content/ActionFactory.java:1158`) | Крутит один мотор с заданной скоростью. Требует EV3 по Bluetooth. |
| 3 | RU «Остановить мотор EV3» / EN «Stop EV3 motor» | Спиннер `ev3_stop_motor_chooser`, enum `Motor` (A, B, C, D, B+C, ALL_MOTORS); формул нет | `content/bricks/LegoEv3MotorStopBrick.java:37` | `content/actions/LegoEv3MotorStopAction.java:34` (фабрика `createLegoEv3MotorStopAction`, `content/ActionFactory.java:1168`) | Останавливает выбранный мотор(ы). Требует EV3 по Bluetooth. |
| 4 | RU «Играть тон EV3 … Частота … в течении … Громкость» / EN «Play EV3 tone … Frequency … for … Volume» | Формулы `LEGO_EV3_FREQUENCY` (дефолт 2.0), `LEGO_EV3_DURATION_IN_SECONDS` (дефолт 1.0), `LEGO_EV3_VOLUME` (дефолт 100.0); спиннера нет | `content/bricks/LegoEv3PlayToneBrick.java:33` | `content/actions/LegoEv3PlayToneAction.java:38` (`createLegoEv3PlayToneAction`) | Проигрывает тон на блоке EV3. Требует EV3 по Bluetooth. |
| 5 | RU «Задать статус светодиода EV3» / EN «Set EV3 LED status» | Спиннер `ev3_led_status_chooser`, enum `LedStatus` (OFF, GREEN/RED/ORANGE, FLASHING ×3, PULSE ×3); формул нет | `content/bricks/LegoEv3SetLedBrick.java:37` | `content/actions/LegoEv3SetLedAction.java:34` (`createLegoEv3SetLedAction`) | Задает состояние LED блока EV3. Требует EV3 по Bluetooth. |
