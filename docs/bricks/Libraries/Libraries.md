# Libraries / Библиотеки

Ключ категории: `category_libraries` (EN «Libraries», `catroid/src/main/res/values/strings.xml:2459`; RU «Библиотеки», `values-ru/strings.xml:2531`).
Состав и порядок — строго по `setupLibrariesCategoryList`, `catroid/src/main/java/org/catrobat/catroid/ui/fragment/CategoryBricksFactory.kt:1258-1269`: список полностью динамический — по одному блоку на каждое определение из `CustomBrickManager.getAllDefinitions()`, порядок = порядок списка определений.

| № | Название RU | Название EN | Параметры | Класс брика | Класс экшена | Описание |
|---|-------------|-------------|-----------|-------------|--------------|----------|
| 1..N | `headerText` определения с подставленными значениями параметров (`{0}`…`{n}` заменяются полями ввода) | `headerText` определения с подставленными значениями параметров | До 10 формул: `BrickField.CUSTOM_PARAM_1` … `CUSTOM_PARAM_10` по числу `definition.parameters` (`CustomBrick.kt:138-148`); лишние параметры сверх 10 не отображаются | `catroid/src/main/java/org/catrobat/catroid/content/bricks/CustomBrick.kt:21` (создаётся как `CustomBrick(def.id)` для каждого определения) | `catroid/src/main/java/org/catrobat/catroid/content/actions/CustomAction.kt:33` (через `createCustomAction`, `ActionFactory.java:2152`) | Выполняет пользовательское определение библиотечного блока: находит его по `definitionId` (`CustomBrickManager.findDefinitionById`) и исполняет с введёнными параметрами. Запись в `BrickInfo.java` отсутствует. |

## Связанные классы

- Определение блока: `catroid/src/main/java/org/catrobat/catroid/libraries/CustomBrickDefinition.kt:10` (`data class CustomBrickDefinition`).
- Реестр/поставщик определений: `catroid/src/main/java/org/catrobat/catroid/libraries/CustomBrickManager.kt:5` (`object CustomBrickManager`; `getAllDefinitions()`, `findDefinitionById()`).
