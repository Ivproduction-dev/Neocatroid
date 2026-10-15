# Документация — индекс

Факты только из кода. Источники: `catroid/src/main/java/org/catrobat/catroid/ui/fragment/CategoryBricksFactory.kt` (состав и порядок палитр), `catroid/src/main/java/org/catrobat/catroid/content/BrickInfo.java` (описания RU/EN), `catroid/src/main/res/values/strings.xml` + `values-ru/strings.xml` (названия), `content/bricks/` + `content/actions/` (параметры и классы).

## Блоки (`docs/bricks/`)

Порядок — как категории перечислены в задаче (соответствие слага → строковый ресурс → файл):

| Категория | Ресурс (RU / EN) | Файл |
|---|---|---|
| `lego_nxt` | `category_lego_nxt` (Лего NXT / Lego NXT) | `docs/bricks/Lego NXT/Lego NXT.md` — 4 блока: TurnAngle, Stop, Move, PlayTone |
| `lego_ev3` | `category_lego_ev3` (Лего EV3 / Lego EV3) | `docs/bricks/Lego EV3/Lego EV3.md` — 5 блоков: + SetLed |
| `drone` | `category_drone` (AR.Drone 2.0) | `docs/bricks/AR.Drone 2.0/AR.Drone 2.0.md` — 13 блоков: взлет/посадка, авария, 8 движений/поворотов, флип, LED-анимация, камера |
| `drone_js` | `category_jumping_sumo` (Jumping Sumo) | `docs/bricks/Jumping Sumo/Jumping Sumo.md` — 11 блоков: движения, анимации, звук, прыжки, повороты, сальто, фото |
| `arduino` | `category_arduino` (Arduino) | `docs/bricks/Arduino/Arduino.md` — 2 блока: digital-пин, PWM-пин |
| `raspi` | `category_raspi` (Raspberry Pi) | `docs/bricks/Raspberry Pi/Raspberry Pi.md` — 4 блока: событие пина, If, digital-пин, PWM |
| `phiro` | `category_phiro` (Phiro) | `docs/bricks/Phiro/Phiro.md` — 6 блоков: моторы, тон, RGB-свет, If-сенсор (+ 6 сенсорных `SetVariableBrick`) |

В каждом файле таблица: название RU+EN | параметры | класс брика и экшена (путь:строка) | описание 1–2 строки.

## LunoScript

- `docs/lunoscript.md` — модель интерпретатора (`utils/lunoscript`: Lexer → Parser → AST → Interpreter, `LunoValue`, `Scope`) + таблица встроенных функций `Interpreter.kt` (`defineNative`, имя и арность).
