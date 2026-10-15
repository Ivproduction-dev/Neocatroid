# Raspberry Pi — блоки

- Строка категории: `category_raspi` — RU «Raspberry Pi», EN «Raspberry Pi» (`catroid/src/main/res/values/strings.xml:2916`).
- Палитра: `setupRaspiCategoryList()` в `catroid/src/main/java/org/catrobat/catroid/ui/fragment/CategoryBricksFactory.kt:3193-3201` (порядок ниже — по ней; событие по дефолту `RaspiInterruptScript("3", "pressed")`).
- Описания RU/EN: `catroid/src/main/java/org/catrobat/catroid/content/BrickInfo.java:945-950` (+ событие `:1211-1212`).
- Дефолты: digital пин 3 = 1 (`RASPI_DIGITAL_INITIAL_PIN_NUMBER/VALUE`), PWM 100 Гц / 50% (`RASPI_PWM_INITIAL_FREQUENCY/PERCENTAGE`), события `RASPI_EVENTS = {"pressed","released"}` (`catroid/src/main/java/org/catrobat/catroid/common/BrickValues.java:152-156`).

| № | Название RU / EN | Параметры | Класс брика | Класс экшена | Описание |
|---|---|---|---|---|---|
| 1 | RU «Когда Пин для Raspberry Pi … изменится на …» / EN «When Raspberry Pi pin … changes to …» | Спиннер пина (динамический список) + спиннер события (pressed/released); хранит `RaspiInterruptScript`. Экшена нет — стартовый скрипт (`ScriptBrickBaseType`) | `content/bricks/WhenRaspiPinChangedBrick.java:43` | — | Событие: пин GPIO перешел в high/low. Требует сеть к Raspberry Pi. |
| 2 | RU «Если Пин для Raspberry Pi … истина, то … Иначе» / EN «If Raspberry Pi pin … is true then … Else» | Формула `IF_CONDITION` (номер пина, дефолт 3); ветки if/else. Фабрика `createRaspiIfLogicActionAction` (`content/ActionFactory.java:7332`) | `content/bricks/RaspiIfLogicBeginBrick.java:33` | `content/actions/RaspiIfLogicAction.java:36` (extends `Action`) | Ветвление по значению пина. Требует сеть к Raspberry Pi. |
| 3 | RU «Задать Пин для Raspberry Pi … со значением» / EN «Set Raspberry Pi pin … to» | Формулы `RASPI_DIGITAL_PIN_NUMBER` (дефолт 3), `RASPI_DIGITAL_PIN_VALUE` (дефолт 1). Фабрика `createSendDigitalRaspiValueAction` (`content/ActionFactory.java:4239`) | `content/bricks/RaspiSendDigitalValueBrick.java:30` | `content/actions/RaspiSendDigitalValueAction.java:36` | Шлет цифровое значение (0/1) на пин. Требует сеть к Raspberry Pi. |
| 4 | RU «Задать Пин для Raspberry Pi PWM …» / EN «Set Raspberry Pi PWM pin …» | Формулы `RASPI_DIGITAL_PIN_NUMBER` (дефолт 3), `RASPI_PWM_FREQUENCY` (Гц, дефолт 100.0), `RASPI_PWM_PERCENTAGE` (%, дефолт 50.0). Фабрика `createSendRaspiPwmValueAction` (`content/ActionFactory.java:4249`) | `content/bricks/RaspiPwmBrick.java:30` | `content/actions/RaspiPwmAction.java:36` | Ставит ШИМ на пин с частотой и скважностью. Требует сеть к Raspberry Pi. |
