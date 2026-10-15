# Data (Данные)

Категория: `R.string.category_data` — EN «Data» / RU «Данные».
Состав и порядок: `CategoryBricksFactory.kt` → `setupDataCategoryList(context, isBackgroundSprite)`.

У категории два режима палитры (переключатель группировки `pref_grouping_data`, по умолчанию **выключен**):

* **Плоский режим** (`isGroupingEnabled == false`) — одна таблица, порядок как в `setupDataCategoryList` строки 2489–2599.
* **Режим с подкатегориями** (группировка включена) — тот же набор, разбитый на `SubCategoryHeaderBrick` (строки 2603–2714).

Оба режима используют один и тот же список режимов ветвлений: часть блоков добавляется только при выполнении условий в `SettingsFragment` (`isAISpeechRecognitionSharedPreferenceEnabled`, `isEmroiderySharedPreferenceEnabled`, `isNfcSharedPreferenceEnabled`), а `LookRequestBrick`/`BackgroundRequestBrick` выбираются по признаку фонового спрайта/альбомной ориентации.

---

## Плоский режим (группировка выключена)

Порядок ровно как в `dataBrickList.add(...)`, строки 2489–2599.

| # | Блок (RU / EN) | Параметры | Класс брика | Класс экшена | Описание |
|---|---|---|---|---|---|
| 1 | Задать значение переменной / Set variable | переменная, значение | `content/bricks/SetVariableBrick.java:33` | `content/actions/SetVariableAction.kt:41` | Задаёт переменной новое значение (`BrickValues.SET_VARIABLE`). |
| 2 | Изменить значение переменной / Change variable | переменная, на что изменить | `content/bricks/ChangeVariableBrick.java:31` | `content/actions/ChangeVariableAction.kt:37` | Изменяет значение переменной на заданное (`BrickValues.CHANGE_VARIABLE`). |
| 3 | Создать сессионную переменную / Create session variable | имя, значение: | `content/bricks/CreateSessionVariableBrick.java:8` | `content/actions/CreateSessionVariableAction.java:10` | Создаёт сессионную переменную с именем и значением. |
| 4 | Задать сессионную переменную / Set session variable | имя, значение: | `content/bricks/SetSessionVariableBrick.java:8` | `content/actions/SetSessionVariableAction.java:10` | Задаёт значение сессионной переменной. |
| 5 | Изменить сессионную переменную / Change session variable | имя, значение: | `content/bricks/ChangeSessionVariableBrick.java:8` | `content/actions/ChangeSessionVariableAction.java:10` | Изменяет значение сессионной переменной. |
| 6 | Удалить сессионную переменную / Delete session variable | имя | `content/bricks/DeleteSessionVariableBrick.java:8` | `content/actions/DeleteSessionVariableAction.java:10` | Удаляет сессионную переменную по имени. |
| 7 | Анимация (Easing) / Easing Animation | Тип, Текущее Время, Длительность, Начало, Конец | `content/bricks/SetVariableEasingBrick.java:15` | `content/actions/SetVariableEasingAction.kt:9` | Анимирует переменную по типу easing за интервал времени. |
| 8 | Показать переменную / Show variable | переменная, на X: / Y: | `content/bricks/ShowTextBrick.java:34` | `content/actions/ShowTextAction.kt:35` (через `ActionFactory.createShowVariableAction`) | Показывает значение переменной на сцене в точке X/Y. |
| 9 | Показать переменную (цвет/размер/выравнивание) / Show variable + color,sze,align | переменная, X, Y, размер: %, цвет:, выравнивание | `content/bricks/ShowTextColorSizeAlignmentBrick.java:63` | `content/actions/ShowTextColorSizeAlignmentAction.java:40` | То же, но с размером, цветом и выравниванием текста. |
| 10 | Показать переменную + шрифт / Show variable + font | переменная, X, Y, размер %, цвет, шрифт (файлы проекта), выравнивание | `content/bricks/ShowVarFontBrick.java:63` | `content/actions/ShowVarFontAction.java:42` | То же, что п.9, плюс шрифт из файлов проекта. |
| 11 | Показать текст с названием / Show text with name | название, текст, X, Y, размер %, цвет, выравнивание | `content/bricks/ShowText3Brick.java:63` | `content/actions/Showtext3Action.java:40` | Показывает именованный текст на сцене. |
| 12 | Показать текст с названием + шрифт / Show text with name + font | название, текст, шрифт, X, Y, размер %, цвет, выравнивание | `content/bricks/ShowTextFontBrick.java:63` | `content/actions/ShowTextFontAction.java:42` | Показывает именованный текст с шрифтом из файлов проекта. |
| 13 | Показать текст с названием + направление / Show text with name + rotation | название, текст, шрифт, направление, X, Y, размер %, цвет, выравнивание | `content/bricks/ShowTextRotationBrick.java:63` | `content/actions/ShowTextRotationAction.java:42` | Показывает именованный текст с заданным поворотом. |
| 14 | Изменить для текста / Set for text | название, свойство, Значение | `content/bricks/SetTextPropertyBrick.java:14` | `content/actions/SetTextPropertyAction.java:16` | Меняет свойство ранее показанного текста. |
| 15 | Скрыть переменную / Hide variable | переменная | `content/bricks/HideTextBrick.java:33` | `content/actions/HideTextAction.kt:36` (через `ActionFactory.createHideVariableAction`) | Скрывает ранее показанную переменную. |
| 16 | Скрыть текст с названием / Hide text with name | название | `content/bricks/HideText3Brick.java:33` | `content/actions/HideText3Action.java:41` | Скрывает ранее показанный именованный текст. |
| 17 | Сохранить переменную на устройство / Write variable on device | переменная | `content/bricks/WriteVariableOnDeviceBrick.java:29` | `content/actions/WriteUserDataOnDeviceAction.kt:12` | Записывает переменную в специальный файл в проекте. |
| 18 | Прочитать переменную с устройства / Read variable from device | переменная | `content/bricks/ReadVariableFromDeviceBrick.java:30` | `content/actions/ReadVariableFromDeviceAction.kt:34` | Читает переменную из специального файла в проекте. |
| 19 | Сохранить переменную в файл / Write variable to file | переменная, имя файла | `content/bricks/WriteVariableToFileBrick.kt:32` | `content/actions/WriteVariableToFileAction.kt:33` | Записывает переменную в файл в «Загрузках». |
| 20 | Прочитать переменную с файла / Read variable from file | переменная, имя файла, действие | `content/bricks/ReadVariableFromFileBrick.kt:36` | `content/actions/ReadVariableFromFileAction.kt:55` | Читает переменную из файла в «Загрузках». |
| 21 | Записать переменную в файл проекта / Write variable to project file | переменная, имя файла | `content/bricks/WriteToFilesBrick.java:33` | `content/actions/WriteToFilesAction.kt:44` | Сохраняет переменную в файл внутри файлов проекта. |
| 22 | Прочитать переменную из файла проекта / Read variable from project file | переменная, имя файла, действие | `content/bricks/ReadFromFilesBrick.java:33` | `content/actions/ReadFromFilesAction.kt:43` | Читает переменную из файла внутри файлов проекта. |
| 23 | Дописать текст в файл / Append text to file | Имя файла, Текст | `content/bricks/AppendToFileBrick.java:8` | `content/actions/AppendToFileAction.kt:22` | Дописывает текст в конец файла; создаёт файл, если его нет. |
| 24 | Список файлов в папке / List files in folder | Папка в Загрузках, в список | `content/bricks/ListFilesInFolderBrick.java:9` | `content/actions/ListFilesInFolderAction.kt:12` | Записывает имена файлов из папки в список. |
| 25 | Удалить файл проекта / Delete project file | имя файла | `content/bricks/DeleteFilesBrick.java:31` | `content/actions/DeleteFilesAction.kt:9` | Удаляет файл проекта. |
| 26 | Сохранить файл (Загрузки) / Save file (Downloads) | URL, с названием | `content/bricks/FileUrlBrick.java:31` | `content/actions/FileUrlAction.kt:61` | Скачивает файл по ссылке в «Загрузки». |
| 27 | Сохранить файл (в проект) / Save file (in project) | URL, с названием | `content/bricks/FilesUrlBrick.java:31` | `content/actions/FilesUrlAction.kt:64` | Скачивает файл по ссылке в файлы проекта. |
| 28 | Заархивировать файлы / Zip project files | список файлов, имя архива | `content/bricks/ZipProjectFilesBrick.java:8` | `content/actions/ZipProjectFilesAction.kt:13` | Пакует файлы проекта в zip-архив. |
| 29 | Разархивировать файлы / Unzip project files | имя архива | `content/bricks/UnzipProjectFilesBrick.java:8` | `content/actions/UnzipProjectFilesAction.kt:11` | Распаковывает zip-архив в файлы проекта. |
| 30 | Имена файлов из ZIP / Get file names from ZIP | Файл проекта, и сохранить в | `content/bricks/GetZipFileNamesBrick.java:32` | `content/actions/GetZipFileNamesAction.kt:11` | Возвращает имена файлов внутри zip-архива. |
| 31 | Запаковать Zip (В Загрузках) / Zip to (from Downloads) | из файлов | `content/bricks/ZipBrick.java:31` | `content/actions/ZipAction.kt:16` | Запаковывает файлы из «Загрузок» в zip-архив. |
| 32 | Распаковать ZIP (В Загрузках) / Unzip file (Downloads) | имя архива | `content/bricks/UnzipBrick.java:31` | `content/actions/UnzipAction.kt:16` | Распаковывает zip-архив в «Загрузки». |
| 33 | Добавить в список / Add to list | значение, список | `content/bricks/AddItemToUserListBrick.java:30` | `content/actions/AddItemToUserListAction.kt:31` | Добавляет элемент в пользовательский список. |
| 34 | Удалить элемент из списка / Delete item from list | индекс, список | `content/bricks/DeleteItemOfUserListBrick.java:31` | `content/actions/DeleteItemOfUserListAction.kt:32` | Удаляет элемент списка по позиции. |
| 35 | Очистить список / Delete all items from list | список | `content/bricks/ClearUserListBrick.java:30` | `content/actions/ClearUserListAction.java:30` | Удаляет все элементы списка. |
| 36 | Сумма списка / Sum of list | список, и сохранить результат в переменную | `content/bricks/SumOfListBrick.java:27` | `content/actions/SumOfListAction.kt:7` | Считает сумму элементов списка. |
| 37 | Среднее списка / Average of list | список, и сохранить результат в переменную | `content/bricks/AverageOfListBrick.java:27` | `content/actions/AverageOfListAction.kt:7` | Считает среднее элементов списка. |
| 38 | Минимум списка / Minimum of list | список, и сохранить результат в переменную | `content/bricks/MinOfListBrick.java:27` | `content/actions/MinOfListAction.kt:7` | Находит минимум списка. |
| 39 | Максимум списка / Maximum of list | список, и сохранить результат в переменную | `content/bricks/MaxOfListBrick.java:27` | `content/actions/MaxOfListAction.kt:7` | Находит максимум списка. |
| 40 | Сортировать список / Sort list | список | `content/bricks/SortListBrick.java:29` | `content/actions/SortListAction.kt:7` | Сортирует список. |
| 41 | Развернуть список / Reverse list | список | `content/bricks/ReverseListBrick.java:26` | `content/actions/ReverseListAction.kt:7` | Разворачивает порядок элементов списка. |
| 42 | Перемешать список / Shuffle list | список | `content/bricks/ListShuffleBrick.java:7` | `content/actions/ShuffleListAction.kt:8` | Перемешивает список случайно. |
| 43 | Вставить в список / Insert into list | значение, список, на позицию | `content/bricks/InsertItemIntoUserListBrick.java:31` | `content/actions/InsertItemIntoUserListAction.java:34` | Вставляет элемент в список на указанную позицию. |
| 44 | Заменить предмет в списке / Replace item in list | значение, список, с позиции, на | `content/bricks/ReplaceItemInUserListBrick.java:31` | `content/actions/ReplaceItemInUserListAction.java:34` | Заменяет элемент списка по позиции. |
| 45 | Сохранить список на устройство / Write list on device | список | `content/bricks/WriteListOnDeviceBrick.java:30` | `content/actions/WriteUserDataOnDeviceAction.kt:12` | Записывает список в специальный файл в проекте. |
| 46 | Прочитать список с устройства / Read list from device | список | `content/bricks/ReadListFromDeviceBrick.java:30` | `content/actions/ReadListFromDeviceAction.kt:34` | Читает список из специального файла в проекте. |
| 47 | Сохранить в столбец из значения через запятую в список / Store column of CSV into list | номер столбца, CSV-строка, список | `content/bricks/StoreCSVIntoUserListBrick.java:31` | `content/actions/StoreCSVIntoUserListAction.kt:39` | Разбивает строку как CSV и сохраняет столбец в список. |
| 48 | Разделить через и сохранить в / Split by and store in | строка, разделитель, переменная | `content/bricks/SplitBrick.java:30` | `content/actions/SplitAction.kt:41` | Делит строку по разделителю, пишет результат в список. |
| 49 | Разделить в таблицу / Split into table | строка, разделитель x, разделитель y, имя таблицы | `content/bricks/StringToTableBrick.java:31` | `content/actions/StringToTableAction.kt:42` | Разбивает строку на таблицу по двум разделителям. |
| 50 | Выполнить регулярное выражение / Execute regex | текст, выражение, и сохранить результат в | `content/bricks/RegexBrick.java:30` | `content/actions/RegexAction.kt:10` | Выполняет regex, сохраняет все совпадения в таблицу. |
| 51 | Отправить GET веб-запрос / Send GET web request | URL, и сохранить ответ в | `content/bricks/WebRequestBrick.kt:32` | `content/actions/WebRequestAction.kt:30` | Отправляет GET-запрос и сохраняет ответ. |
| 52 | Отправить POST веб-запрос / POST web request | URL, заголовок, тело, переменная | `content/bricks/PostWebRequestBrick.java:31` | `content/actions/PostWebRequestAction.kt:55` | Отправляет POST-запрос; не ждёт ответа. |
| 53 | PUT запрос к / PUT web request | URL, заголовок, данные, таймаут (с), переменная | `content/bricks/PutWebRequestBrick.java:9` | `content/actions/PutWebRequestAction.kt:19` | Отправляет PUT-запрос. |
| 54 | PATCH запрос к / PATCH web request | URL, заголовок, данные, таймаут (с), переменная | `content/bricks/PatchWebRequestBrick.java:9` | `content/actions/PatchWebRequestAction.kt:19` | Отправляет PATCH-запрос. |
| 55 | DELETE запрос к / DELETE web request | URL, заголовок, таймаут (с), переменная | `content/bricks/DeleteWebRequestBrick.java:9` | `content/actions/DeleteWebRequestAction.kt:18` | Отправляет DELETE-запрос. |
| 56 | HEAD запрос к / HEAD web request | URL, заголовок, таймаут (с), переменная | `content/bricks/HeadWebRequestBrick.java:9` | `content/actions/HeadWebRequestAction.kt:17` | Отправляет HEAD-запрос, сохраняет заголовки. |
| 57 | OPTIONS запрос к / OPTIONS web request | URL, заголовок, таймаут (с), переменная | `content/bricks/OptionsWebRequestBrick.java:9` | `content/actions/OptionsWebRequestAction.kt:18` | Отправляет OPTIONS-запрос, сохраняет разрешённые методы. |
| 58 | Создать/Задать локальную переменную / Create/Set Local Variable | имя, значение | `content/bricks/CreateVarBrick.java:30` | `content/actions/CreateVarAction.kt:33` | Создаёт или задаёт локальную переменную по ключу. |
| 59 | Изменить переменную по имени / Set Variable by Name | имя, на | `content/bricks/SetVariableByNameBrick.java:31` | `content/actions/SetVariableByNameAction.kt:30` | Задаёт переменную по имени (формула); создаёт, если нет. |
| 60 | Удалить локальную переменную / Delete local variable | с именем | `content/bricks/DeleteVarBrick.java:30` | `content/actions/DeleteVarAction.kt:33` | Удаляет локальную переменную по имени. |
| 61 | Удалить все локальные переменные / Delete all local variables | — | `content/bricks/DeleteVarsBrick.java:30` | `content/actions/DeleteVarsAction.kt:33` | Удаляет все локальные переменные. |
| 62 | Создать таблицу с названием / Create table with name | имя, размер (X, Y) | `content/bricks/CreateTableBrick.java:31` | `content/actions/CreateTableAction.kt:41` | Создаёт таблицу заданного размера. |
| 63 | Вставить в таблицу / Insert into table | значение, имя таблицы, позиция (X, Y) | `content/bricks/InsertTableBrick.java:31` | `content/actions/InsertTableAction.kt:41` | Вставляет значение в таблицу по позиции. |
| 64 | Удалить таблицу с именем / Delete table with name | имя таблицы | `content/bricks/DeleteTableBrick.java:31` | `content/actions/DeleteTableAction.kt:41` | Удаляет таблицу. |
| 65 | Удалить все таблицы / Delete all tables | — | `content/bricks/DeleteAllTablesBrick.java:31` | `content/actions/DeleteAllTablesAction.kt:41` | Удаляет все таблицы. |
| 66 | Показать тост / Show toast | текст | `content/bricks/ShowToastBrick.java:31` | `content/actions/ShowToastAction.kt:35` | Показывает всплывающее сообщение внизу экрана. |
| 67 | Скопировать в буфер обмена / Copy to clipboard | текст | `content/bricks/CopyTextBrick.java:31` | `content/actions/CopyTextAction.kt:43` | Копирует текст в буфер обмена. |
| 68 | Начать слушать микрофон / Start listen micro | отрезок в мс | `content/bricks/ListenMicroBrick.java:31` | `content/actions/ListenMicroAction.kt:13` | Слушает микрофон отрезками и пишет громкость в формулу. |
| 69 | Создать квадрат / Create square | Название, Позиция (X, Y), Ширина, Высота, Цвет, Прозрачность, Направление, Закругленные углы | `content/bricks/SquareBrick.java:40` | `content/actions/SquareAction.kt:42` | Создаёт квадрат на сцене. |
| 70 | Удалить квадрат / Delete square | Название | `content/bricks/DelSquareBrick.java:31` | `content/actions/DelSquareAction.kt:42` | Удаляет созданный квадрат. |
| 71 | Получить изображение из / Get image from | URL (+ результат) | `content/bricks/LookRequestBrick.kt:32` (фон: `BackgroundRequestBrick.kt:33`) | `content/actions/LookRequestAction.kt:38` | Получает изображение по ссылке и ставит его образом/фоном. |
| 72 | Спросить / Ask | вопрос, Цвет фона, Цвет кнопок, и сохранить написанный ответ в | `content/bricks/AskBrick.java:31` | `content/actions/AskAction.kt:34` | Спрашивает вопрос и сохраняет введённое значение. |
| * | Спросить / Ask (речь) | вопрос, и сохранить озвученый ответ в | `content/bricks/AskSpeechBrick.java:32` | `content/actions/AskSpeechAction.kt:38` | Только при `SettingsFragment.isAISpeechRecognitionSharedPreferenceEnabled`. |
| * | Записать данные вышивки в файл / Write embroidery data to file | имя файла | `content/bricks/WriteEmbroideryToFileBrick.kt:32` | `content/actions/WriteEmbroideryToFileAction.kt:50` | Только при `isEmroiderySharedPreferenceEnabled`. |
| * | Начать слушать голос / Start listening to voice | переменная | `content/bricks/StartListeningBrick.kt:31` | `content/actions/StartListeningAction.kt:32` | Только при `isAISpeechRecognitionSharedPreferenceEnabled`. |
| * | Записать на NFC метку / Wait for next NFC tag | тип записи | `content/bricks/SetNfcTagBrick.java:41` | `content/actions/SetNfcTagAction.java:35` | Только при `isNfcSharedPreferenceEnabled`. |
| 73 | создать таблицу / create map | — | `content/bricks/MapCreateBrick.java:30` | `content/actions/MapCreateAction.kt:29` | Создаёт map (словарь). |
| 74 | таблица записать / map set | ключ, значение | `content/bricks/MapSetBrick.java:31` | `content/actions/MapSetAction.kt:30` | Записывает значение в map по ключу. |
| 75 | таблица прочитать / map get | ключ, в переменную | `content/bricks/MapGetBrick.java:42` | `content/actions/MapGetAction.kt:30` | Читает значение из map по ключу. |
| 76 | таблица удалить / map delete | ключ | `content/bricks/MapDeleteBrick.java:31` | `content/actions/MapDeleteAction.kt:30` | Удаляет пару ключ/значение из map. |
| 77 | множество добавить / set add | значение | `content/bricks/SetAddBrick.java:31` | `content/actions/SetAddAction.kt:30` | Добавляет значение в set. |
| 78 | множество удалить / set remove | значение | `content/bricks/SetRemoveBrick.java:31` | `content/actions/SetRemoveAction.kt:30` | Удаляет значение из set. |
| 79 | множество содержит / set contains | значение, в переменную | `content/bricks/SetContainsBrick.java:42` | `content/actions/SetContainsAction.kt:30` | Проверяет, есть ли значение в set. |
| 80 | очередь добавить / queue enqueue | значение | `content/bricks/QueueEnqueueBrick.java:31` | `content/actions/QueueEnqueueAction.kt:30` | Добавляет элемент в очередь. |
| 81 | очередь извлечь / queue dequeue | в переменную | `content/bricks/QueueDequeueBrick.java:41` | `content/actions/QueueDequeueAction.kt:29` | Извлекает элемент из очереди. |
| 82 | стек положить / stack push | значение | `content/bricks/StackPushBrick.java:31` | `content/actions/StackPushAction.kt:30` | Кладёт элемент в стек. |
| 83 | стек извлечь / stack pop | в переменную | `content/bricks/StackPopBrick.java:41` | `content/actions/StackPopAction.kt:29` | Снимает элемент со стека. |
| 84 | Сохранить защищенную переменную / Write secure variable | переменная | `content/bricks/SecureSaveVariableBrick.java:18` | `content/actions/SecureSaveVariableAction.kt:22` | Сохраняет переменную в зашифрованном хранилище (AES-256). |
| 85 | Прочитать защищенную переменную / Read secure variable | переменная | `content/bricks/SecureReadVariableBrick.java:18` | `content/actions/SecureReadVariableAction.kt:22` | Читает зашифрованную переменную (AES-256). |

Блоки с `*` — условные (настройки/проект), их состав в плоском режиме зависит от `SettingsFragment`.

---

## Режим с подкатегориями

Те же блоки, сгруппированные заголовками `SubCategoryHeaderBrick`.

### Переменные / Variables (`subcategory_data_variables`)

SetVariableBrick, ChangeVariableBrick, CreateSessionVariableBrick, SetSessionVariableBrick, ChangeSessionVariableBrick, DeleteSessionVariableBrick, SetVariableEasingBrick, CreateVarBrick, SetVariableByNameBrick, DeleteVarBrick, DeleteVarsBrick.

### Списки и таблицы / Lists & Tables (`subcategory_data_lists`)

AddItemToUserListBrick, DeleteItemOfUserListBrick, InsertItemIntoUserListBrick, ReplaceItemInUserListBrick, ClearUserListBrick, SumOfListBrick, AverageOfListBrick, MinOfListBrick, MaxOfListBrick, SortListBrick, ReverseListBrick, ListShuffleBrick, CreateTableBrick, InsertTableBrick, DeleteTableBrick, DeleteAllTablesBrick.

### Отображение данных / Data Visualization (`subcategory_data_visuals`)

ShowTextBrick, ShowTextColorSizeAlignmentBrick, ShowVarFontBrick, ShowText3Brick, ShowTextFontBrick, SetTextPropertyBrick, HideTextBrick, HideText3Brick, ShowToastBrick, SquareBrick, DelSquareBrick.

### Обработка текста / Text Processing (`subcategory_data_processing`)

StoreCSVIntoUserListBrick, SplitBrick, StringToTableBrick, RegexBrick, CopyTextBrick, WriteVariableOnDeviceBrick, SecureSaveVariableBrick, ReadVariableFromDeviceBrick, SecureReadVariableBrick.

### Файловая система и архивы / File System & Archives (`subcategory_data_files`)

WriteVariableOnDeviceBrick, ReadVariableFromDeviceBrick, WriteVariableToFileBrick, ReadVariableFromFileBrick, WriteToFilesBrick, ReadFromFilesBrick, AppendToFileBrick, ListFilesInFolderBrick, DeleteFilesBrick, WriteListOnDeviceBrick, ReadListFromDeviceBrick, ZipBrick, GetZipFileNamesBrick, UnzipBrick.

### Сеть и взаимодействие / Network & Interaction (`subcategory_data_web`)

WebRequestBrick, PostWebRequestBrick, FileUrlBrick, FilesUrlBrick, LookRequestBrick / BackgroundRequestBrick, AskBrick, ListenMicroBrick, [AskSpeechBrick, StartListeningBrick]* , [WriteEmbroideryToFileBrick]*, [SetNfcTagBrick]*, MapCreateBrick, MapSetBrick, MapGetBrick, MapDeleteBrick, SetAddBrick, SetRemoveBrick, SetContainsBrick, QueueEnqueueBrick, QueueDequeueBrick, StackPushBrick, StackPopBrick, SetSeedBrick.

> Примечание: в этой ветке `SetSeedBrick` (`content/bricks/SetSeedBrick.java:8` / `content/actions/SetSeedAction.kt:8`, «Задать зерно случайности») присутствует только в режиме подкатегорий; в плоском режиме его нет.

---

## Дополнительно

V2-группировка (`appendGroupingV2Events`) для `category_data` не добавляет блоки — в `when (category)` для data нет ветки.
