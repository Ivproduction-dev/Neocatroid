# Arduino — блоки

- Строка категории: `category_arduino` — RU «Arduino», EN «Arduino» (`catroid/src/main/res/values/strings.xml:2914`).
- Палитра: `setupArduinoCategoryList()` в `catroid/src/main/java/org/catrobat/catroid/ui/fragment/CategoryBricksFactory.kt:3186-3191` (порядок ниже — по ней).
- Описания RU/EN: `catroid/src/main/java/org/catrobat/catroid/content/BrickInfo.java:519-522`.
- Дефолты: digital пин 13 = 1 (`ARDUINO_DIGITAL_INITIAL_PIN_NUMBER/VALUE`), PWM пин 3 = 255 (`ARDUINO_PWM_INITIAL_PIN_NUMBER/VALUE`) (`catroid/src/main/java/org/catrobat/catroid/common/BrickValues.java:147-150`).

| № | Название RU / EN | Параметры | Класс брика | Класс экшена | Описание |
|---|---|---|---|---|---|
| 1 | RU «Задать Пин для Arduino … со значением» / EN «Set Arduino digital pin … to» | Формулы `ARDUINO_DIGITAL_PIN_NUMBER` (дефолт 13), `ARDUINO_DIGITAL_PIN_VALUE` (дефолт 1, 0/1) | `content/bricks/ArduinoSendDigitalValueBrick.java:30` | `content/actions/ArduinoSendDigitalValueAction.java:39` (фабрика `createSendDigitalArduinoValueAction`, `content/ActionFactory.java:4219`) | Шлет цифровое значение на пин по Bluetooth. Требует Arduino. |
| 2 | RU «Задать Пин для Arduino PWM~ … со значением» / EN «Set Arduino PWM~ pin … to» | Формулы `ARDUINO_ANALOG_PIN_NUMBER` (дефолт 3), `ARDUINO_ANALOG_PIN_VALUE` (дефолт 255, 0–255) | `content/bricks/ArduinoSendPWMValueBrick.java:32` | `content/actions/ArduinoSendPWMValueAction.java:39` (фабрика `createSendPWMArduinoValueAction`, `content/ActionFactory.java:4229`) | Шлет ШИМ-значение на пин по Bluetooth. Требует Arduino. |

Оба брика добавляют ресурс `BLUETOOTH_SENSORS_ARDUINO` (`addRequiredResources`).
