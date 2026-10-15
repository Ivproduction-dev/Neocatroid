# JSON / JSON

Ключ категории: `category_json`. Источник состава и порядка: `CategoryBricksFactory.kt` (группированный режим).

### 1. JsonParseBrick

| Поле | Значение |
|---|---|
| Название (тексты лейаута) | `brick_json_parse`: RU «Разобрать JSON в» / EN «Parse JSON into»<br>`brick_json_value`: RU «Текст JSON» / EN «JSON text» |
| Параметры | BrickField: `TEXT`, `VALUE` |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/JsonParseBrick.java:7` |
| Класс экшена | `catroid/src/main/java/org/catrobat/catroid/content/actions/JsonParseAction.kt:7` (JsonParseAction) |
| Описание (BrickInfo.java) | RU: Разбирает JSON-текст и сохраняет его под указанным именем.<br>EN: Parses a JSON text and stores it under the given name. |


### 2. JsonGetBrick

| Поле | Значение |
|---|---|
| Название (тексты лейаута) | `brick_json_get`: RU «Получить из JSON» / EN «Get from JSON»<br>`brick_json_key`: RU «ключ» / EN «key» |
| Параметры | BrickField: `NAME`, `KEY` |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/JsonGetBrick.java:8` |
| Класс экшена | `catroid/src/main/java/org/catrobat/catroid/content/actions/JsonGetAction.kt:9` (JsonGetAction) |
| Описание (BrickInfo.java) | — (в BrickInfo.java описания нет) |


### 3. JsonSetBrick

| Поле | Значение |
|---|---|
| Название (тексты лейаута) | `brick_json_set`: RU «Задать в JSON» / EN «Set in JSON»<br>`brick_json_key`: RU «ключ» / EN «key»<br>`brick_json_value`: RU «Текст JSON» / EN «JSON text» |
| Параметры | BrickField: `NAME`, `KEY`, `VALUE` |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/JsonSetBrick.java:7` |
| Класс экшена | `catroid/src/main/java/org/catrobat/catroid/content/actions/JsonSetAction.kt:7` (JsonSetAction) |
| Описание (BrickInfo.java) | — (в BrickInfo.java описания нет) |

