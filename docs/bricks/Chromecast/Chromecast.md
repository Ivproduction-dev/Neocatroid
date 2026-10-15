# Chromecast / Chromecast

Ключ категории: `category_cast` (`res/values/strings.xml`: "Chromecast"; `res/values-ru/strings.xml`: "Chromecast").
Состав и порядок — строго по `CategoryBricksFactory.kt` → `setupChromecastCategoryList` (заголовки `SubCategoryHeaderBrick` не являются блоками и пропущены).

Блоков: 1.

## 1. `WhenGamepadButtonBrick`

| Поле | Значение |
|---|---|
| Название RU | Когда кнопка джойстика |
| Название EN | When gamepad button |
| Строки (`strings.xml` → `values-ru`) | `brick_when_gamepad_button`: EN="When gamepad button" / RU="Когда кнопка джойстика"; `brick_when_gamepad_button_tapped`: EN="tapped" / RU="нажата"; `cast_gamepad_A`: EN="A" / RU="А"; `cast_gamepad_B`: EN="B" / RU="Б" |
| Параметры | спиннер (`brick_when_gamepad_button_spinner`) |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/WhenGamepadButtonBrick.java:43` |
| Класс экшена (`content/actions`) | — (событийный брик/скрипт: `addActionToSequence` пуст, `content/actions` не используется) |
| Описание (строго `BrickInfo.java` + код) | RU: Вызывается, когда нажата кнопка геймпада (A, B, вверх, вниз, влево, вправо).<br>EN: Triggered when a gamepad button is pressed (A, B, up, down, left, right). |
| Вызов в фабрике | `WhenGamepadButtonBrick(WhenGamepadButtonScript(context.getString(R.string.cast_gamepad_A)` |
| Layout | `catroid/src/main/res/layout/brick_when_gamepad_button.xml` |
