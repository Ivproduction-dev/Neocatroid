# МЕГА-ПРОМПТ ДЛЯ ИИ-СИСТЕМЫ: РАЗРАБОТКА НАЦИВНОГО C++ КЛИЕНТА (РАНТАЙМА) ДЛЯ ПРОЕКТОВ NEOCATROID / CATROID

> **Назначение документа**: Данный документ — исчерпывающее техническое задание и системный мастер-промпт для автономного ИИ (или группы субагентов).
> **Цель**: Разработать с нуля высокопроизводительный, легковесный, кроссплатформенный (Windows / Linux / macOS) нативный клиент на **чистом C++** для запуска и воспроизведения проектов `.neotrobat` / `.catrobat` (`code.xml`).
> **Главный закон**: 100% идентичность логики поведения оригинальному Java/Android рантайму Catroid, zero-dependency от JVM/Android, мгновенный запуск, 60/120+ FPS.

---

## 1. ОБЩИЙ КОНТЕКСТ И АРХИТЕКТУРНЫЕ ТРЕБОВАНИЯ

### 1.1 Что такое проект Catroid / NeoCatroid?
Проект представляет собой конструктор 2D/3D игр. Экспортируемый проект — это zip-архив (`.catrobat` или `.neotrobat`), содержащий:
1. `code.xml` — полный граф сцены, объектов (`Sprite`), образов (`Look`), звуков (`Sound`) и скриптов (`Script`), состоящих из кирпичиков (`Brick`) и деревьев формул (`Formula`). Сериализован Java-библиотекой XStream.
2. `images/` — графические ассеты (PNG, JPG, WebP).
3. `sounds/` — звуковые файлы (MP3, WAV, OGG).
4. `automatic_screenshot.png` — превью проекта.

### 1.2 Цель C++ Runner (ПК-клиента)
Создать исполняемое приложение (Player / Runner), принимающее путь к `.neotrobat` / `.catrobat` архиву (или распакованной папке проекта), которое:
1. Распаковывает проект в память или кэш.
2. Быстро парсит `code.xml` через `pugixml`.
3. Строит дерево объектов и компилирует цепочки блоков в эффективный байткод / цепочки инструкций.
4. Запускает игровой цикл: отрисовка спрайтов, физический мир Box2D, обработка звуков, таймеров, корутин, ввода (мышь, клавиатура, сенсор).
5. Обеспечивает **1-в-1 совпадение логики** с оригинальным Java/Android рантаймом.

### 1.3 Расположение эталонного исходного кода (Reference Repository)
Все оригинальные исходники находятся на локальной машине по адресу:
`c:\Users\ivanp\NewCatroid\catroid\src\main\java\org\catrobat\catroid\`

Ключевые директории для анализа:
- `content/actions/` — **ГЛАВНЫЙ ИСТОЧНИК ПРАВДЫ**. Здесь находится фактическая реализация каждого действия (229 Java-классов). То, как блок мутирует спрайт, считает время, вызывает физику или звук.
- `content/bricks/` — описание блоков (973 Java-класса). Здесь смотреть имена XML-тегов, разрешённые поля (`BrickField`), соответствие параметров и формул.
- `formulaeditor/` — реализация вычисления выражений (`FormulaElement.java`, `Functions.java`, `InternFormulaParser.java`).
- `content/` — базовые сущности (`Sprite.java`, `Project.java`, `Scene.java`, `LookData.java`, `Script.java`).
- `stage/` — логика сцены, экранные координаты, порядок отрисовки, сенсорные события (`StageActivity.java`, `StageListener.java`).
- `physics/` — интеграция с 2D физикой Box2D (`PhysicsWorld.java`, `PhysicsObject.java`).
- `neo3d/` — новый 3D движок на базе Jolt Physics (`c:\Users\ivanp\NewCatroid\catroid\src\main\cpp\jolt\`).

---

## 2. СТЕК ТЕХНОЛОГИЙ И СТРУКТУРА C++ ПРОЕКТА

### 2.1 Стек библиотек (Минимум внешних зависимостей, максимум производительности)
- **Стандарт языка**: C++20 (активное использование `std::string_view`, `std::variant`, `std::span`, лямбд, конечных автоматов).
- **Сборка**: CMake (3.22+).
- **Парсинг XML**: `pugixml` (быстрейший header-only / 2-file XML парсер).
- **Распаковка архивов**: `miniz` (однофайловая реализация zip).
- **Окно, ввод, 2D рендер**: **Raylib** (или **SDL3 + OpenGL**) — аппаратно ускоренный спрайт-батчинг, трансформы, шейдеры.
- **Звук**: `miniaudio` (однофайловый C-аудиодвижок, играет WAV, MP3, FLAC, OGG).
- **2D Физика**: `box2d` (официальный C++ Box2D 2.4.x / 3.x — в Catroid используется именно он).
- **Сериализация сейвов / JSON**: `nlohmann/json` (для сохранения локальных переменных и настроек на диск).
- **Сеть / HTTP**: `cpp-httplib` (однофайловый клиент для сетевых блоков).

### 2.2 Структура папок C++ клиента
```text
neocatroid-player/
├── CMakeLists.txt
├── vendor/
│   ├── pugixml/
│   ├── miniz/
│   ├── miniaudio/
│   ├── nlohmann/
│   ├── httplib/
│   └── box2d/
├── include/
│   └── neocatroid/
└── src/
    ├── main.cpp                     # Точка входа, запуск окна, аргументы командной строки
    ├── core/
    │   ├── Value.hpp / .cpp         # Динамический тип переменной (число, строка, булево, список)
    │   ├── VariableManager.hpp/.cpp # Локальные, сценные, глобальные переменные
    │   ├── Sprite.hpp / .cpp        # Состояние спрайта (координаты, угол, размер, образы, слои)
    │   ├── Scene.hpp / .cpp         # Сцена, менеджер спрайтов, Z-сортировка
    │   ├── Project.hpp / .cpp       # Проект, метаданные, ресурсы
    │   └── EventBus.hpp / .cpp      # Шина событий (Broadcasts, Start, Tapped, Collide)
    ├── formula/
    │   ├── FormulaNode.hpp          # Узел AST формулы
    │   ├── FormulaParser.hpp / .cpp # Парсинг формулы из XML-ноды Catroid
    │   └── FormulaEvaluator.hpp/.cpp# Вычисление формулы (математика, строки, логика)
    ├── parser/
    │   ├── ProjectPackage.hpp/.cpp  # Распаковка zip и чтение файлов в память
    │   └── XmlProjectParser.hpp/.cpp# pugixml десериализатор code.xml в структуры C++
    ├── script/
    │   ├── Instruction.hpp          # Команда виртуальной машины / байткод
    │   ├── ScriptFiber.hpp / .cpp   # Кооперативный поток исполнения скрипта
    │   ├── ScriptEngine.hpp / .cpp  # Планировщик выполнения скриптов (Scheduler)
    │   └── BrickCompiler.hpp / .cpp # Трансляция XML Bricks в Instruction
    ├── actions/
    │   ├── ActionRegistry.hpp / .cpp# Диспетчер действий и таблица маппинга
    │   ├── MotionActions.cpp        # Движение, вращение, скольжение, координаты
    │   ├── LookActions.cpp          # Образы, размеры, прозрачность, видимость, слои
    │   ├── SoundActions.cpp         # Воспроизведение звуков, громкость, темп
    │   ├── ControlActions.cpp       # Управление потоком (Wait, Repeat, If, Stop)
    │   ├── DataActions.cpp          # Переменные, списки
    │   ├── FileActions.cpp          # Запись/чтение файлов игры, сейвы
    │   ├── PhysicsActions.cpp       # Силы, импульсы, скорости Box2D
    │   └── StubActions.cpp          # Заглушки для Android-only блоков (Admob, Lego, Sensors)
    ├── physics/
    │   ├── PhysicsWorld.hpp / .cpp  # Обертка над b2World, шаги симуляции
    │   └── PhysicsContactListener.hpp # Обработка столкновений между спрайтами
    ├── render/
    │   ├── Renderer.hpp / .cpp      # Отрисовка сцены, камера, масштабирование под экран
    │   └── TextureCache.hpp / .cpp  # Загрузка и кэширование PNG/JPG
    └── audio/
        └── AudioEngine.hpp / .cpp   # miniaudio интеграция, воспроизведение звуков
```

---

## 3. КРИТИЧЕСКИЕ ТОЧКИ СОВМЕСТИМОСТИ (1-В-1 С CATROID)

### 3.1 Система координат и экран
1. **Экран Catroid**: Виртуальная декартова система координат. Центр экрана — строго `(0, 0)`.
   - Влево: отрицательный X. Вправо: положительный X.
   - Вниз: отрицательный Y. Вверх: положительный Y.
2. **Углы поворота**:
   - `0°` — смотрит строго **ВВЕРХ** (положительное направление оси Y).
   - `90°` — смотрит **ВПРАВО** (ось X).
   - `180°` — смотрит **ВНИЗ**.
   - `270°` (-90°) — смотрит **ВЛЕВО**.
   - Поворот по часовой стрелке.
   - Стили вращения: `AllAround` (полное вращение), `LeftRight` (только отражение по горизонтали), `Don'tRotate` (без вращения).
3. **Размер спрайта**: `100.0%` — оригинальный размер текстуры с учетом коэффициента виртуального экрана проекта.
4. **Прозрачность**: в Catroid transparency идет от `0.0` (полностью видимый) до `100.0` (полностью прозрачный). Это инверсия альфа-канала:
   $$\text{alpha} = \frac{100.0 - \text{transparency}}{100.0}$$

### 3.2 Динамическая система типов `Value` (как в Catroid)
В Catroid переменная может хранить число или строку, при этом динамически каститься:
```cpp
class Value {
public:
    enum class Type { Number, String, Boolean, List };
    // Автоматические преобразования:
    double asDouble() const;      // "123" -> 123.0; "hello" -> 0.0
    std::string asString() const; // 123.0 -> "123" (если целое, без .0!); 123.5 -> "123.5"
    bool asBool() const;          // 0 -> false; "false" -> false; "" -> false; иначе true
    
    // Операторы:
    Value operator+(const Value& other) const; // Если оба числа -> сложение, иначе конкатенация строк
    Value operator-(const Value& other) const;
    Value operator*(const Value& other) const;
    Value operator/(const Value& other) const; // Деление на 0 в Catroid возвращает 0.0, не крашит!
};
```
> **Внимание**: Изучи реализацию в `formulaeditor/FormulaElement.java` метод `interpret()`. Все правила приведения типов брать строго оттуда!

### 3.3 Формулы (`<formulaTree>` в XML)
В `code.xml` формулы хранятся в разобранном виде (AST):
```xml
<formulaTree>
    <type>FUNCTION</type>
    <value>PLUS</value>
    <leftChild>
        <type>USER_VARIABLE</type>
        <value>score</value>
    </leftChild>
    <rightChild>
        <type>NUMBER</type>
        <value>10</value>
    </rightChild>
</formulaTree>
```
ИИ-субагенту парсера **не нужно** писать текстовый парсер выражений! Нужно просто рекурсивно преобразовать XML-дерево в структуру `FormulaNode`:
```cpp
struct FormulaNode {
    enum class NodeType { Number, String, Variable, Function, Operator, Bracket };
    NodeType type;
    std::string value;
    std::vector<FormulaNode> children;
    
    Value evaluate(const EvaluationContext& ctx) const;
};
```

---

## 4. СТРАТЕГИЯ РАЗДЕЛЕНИЯ НА СУБАГЕНТОВ (ОРКЕСТРАЦИЯ РАБОТЫ)

Для реализации полнофункционального прототипа задача делится на **7 специализированных субагентов**. Каждый агент выполняет строгую функцию и валидирует результат по оригинальным Java-файлам.

---

### СУБАГЕНТ 1: Парсер пакета проекта и движок формул (Parser & Formula Engine)
- **Целевые файлы**: `src/parser/*`, `src/formula/*`.
- **Источники для изучения в Java**:
  - `org/catrobat/catroid/formulaeditor/FormulaElement.java`
  - `org/catrobat/catroid/formulaeditor/Functions.java`
  - `org/catrobat/catroid/formulaeditor/InternFormulaParser.java`
  - Любой `code.xml` из тестового проекта.
- **Обязанности**:
  1. Реализовать распаковку `.neotrobat` / `.catrobat` через `miniz`.
  2. Реализовать чтение `code.xml` через `pugixml`.
  3. Преобразовать `<formulaTree>` в `FormulaNode`.
  4. Реализовать вычисление всех встроенных функций из `Functions.java`:
     - Математика: `SIN`, `COS`, `TAN`, `LN`, `LOG`, `SQRT`, `ABS`, `ROUND`, `FLOOR`, `CEIL`, `RAND`, `MOD`.
     - Строки: `LENGTH`, `LETTER`, `JOIN`, `INDEX_OF`, `SUBSTRING`, `LOWERCASE`, `UPPERCASE`.
     - Логика: `EQUAL`, `NOT_EQUAL`, `SMALLER_THAN`, `SMALLER_OR_EQUAL`, `GREATER_THAN`, `GREATER_OR_EQUAL`, `AND`, `OR`, `NOT`.
     - Системные сенсоры: `TIMER`, `SCREEN_WIDTH`, `SCREEN_HEIGHT`, `X_POSITION`, `Y_POSITION`, `DIRECTION`, `SIZE`, `TRANSPARENCY`.

---

### СУБАГЕНТ 2: Действия Движения и Внешнего вида (Motion & Looks Actions)
- **Целевые файлы**: `src/actions/MotionActions.cpp`, `src/actions/LookActions.cpp`.
- **Источники для изучения в Java**:
  - `content/actions/SetXByAmountAction.java`, `ChangeXByNAction.java`, `PlaceAtAction.java`, `GlideToAction.java`, `TurnLeftAction.java`, `TurnRightAction.java`, `PointInDirectionAction.java`, `PointToAction.java`, `IfOnEdgeBounceAction.java`.
  - `content/actions/SetLookAction.java`, `NextLookAction.java`, `PreviousLookAction.java`, `SetSizeToPercentAction.java`, `ChangeSizeByNAction.java`, `HideAction.java`, `ShowAction.java`, `SetTransparencyAction.java`, `ChangeTransparencyByNAction.java`, `SetBrightnessAction.java`, `ClearGraphicEffectAction.java`, `ComeToFrontAction.java`, `GoNStepsBackAction.java`.
  - Соответствующие Bricks: `SetXBrick.java`, `PlaceAtBrick.java`, `GlideToBrick.java`, `SetLookBrick.java`, etc.
- **Обязанности**:
  1. Реализовать мгновенные действия (Set, Change, Next, Show, Hide).
  2. Реализовать темпоральные действия с интерполяцией во времени (GlideTo: плавное перемещение из (x0, y0) в (x1, y1) за время T).
  3. Реализовать `IfOnEdgeBounceAction` (отскок от границ виртуального экрана проекта с правильным расчетом нормали и разворотом угла спрайта).
  4. Реализовать сортировку Z-слоев спрайтов (`ComeToFront`, `GoNStepsBack`).

---

### СУБАГЕНТ 3: Поток управления, События и Планировщик (Control, Events & Scheduler)
- **Целевые файлы**: `src/script/*`, `src/actions/ControlActions.cpp`, `src/core/EventBus.*`.
- **Источники для изучения в Java**:
  - `content/actions/WaitAction.java`, `ForeverAction.java`, `RepeatAction.java`, `IfLogicBeginAction.java`, `StopScriptAction.java`.
  - `content/actions/BroadcastAction.java`, `BroadcastWaitAction.java`.
  - `content/actions/CloneAction.java`, `DeleteThisCloneAction.java`.
  - `content/bricks/WhenProgramStartsBrick.java`, `WhenTappedBrick.java`, `WhenBroadcastReceivedBrick.java`, `WhenClonedBrick.java`, `WhenCollisionBrick.java`.
- **Обязанности**:
  1. Реализовать кооперативный диспетчер скриптов (`ScriptEngine`):
     - Каждый активный скрипт имеет программный счетчик (PC) и локальное состояние (таймер ожидания, счетчик цикла).
     - За один тик кадра `update(deltaTime)` планировщик продвигает все активные скрипты до тех пор, пока они не упрутся в `WaitAction`, конец кадра или завершение.
  2. Реализовать циклы:
     - `Forever` — зацикливание назад. Обязательно уступает управление кадру (yield), чтобы не подвесить процесс!
     - `Repeat N` — цикл с локальным счетчиком.
     - `If / Else` — условный переход по результату формулы.
  3. Реализовать Broadcast шину:
     - `Broadcast(msg)` — запускает все скрипты с `WhenBroadcastReceived(msg)`.
     - `BroadcastWait(msg)` — запускает скрипты и приостанавливает текущий поток, пока все вызванные скрипты не завершатся!
  4. Реализовать клонирование:
     - Создание глубокой копии `Sprite` (со своими координатами, образами, локальными переменными).
     - Запуск события `WhenCloned` для созданного клона.
     - `DeleteThisClone` — удаление клона из сцены и остановка его скриптов.

---

### СУБАГЕНТ 4: Переменные, Списки, Файловая система и Сейвы (Data & Persistence)
- **Целевые файлы**: `src/core/VariableManager.*`, `src/actions/DataActions.cpp`, `src/actions/FileActions.cpp`.
- **Источники для изучения в Java**:
  - `content/actions/SetVariableAction.java`, `ChangeVariableAction.java`, `AddItemToUserListAction.java`, `DeleteItemOfUserListAction.java`, `InsertItemIntoUserListAction.java`, `ReplaceItemInUserListAction.java`, `ClearUserListAction.java`.
  - `content/actions/WriteVariableOnDeviceAction.java`, `ReadVariableFromDeviceAction.java`.
  - `content/actions/WriteToFilesAction.java`, `ReadFromFilesAction.java`, `AppendToFileAction.java`, `DeleteFilesAction.java`.
- **Обязанности**:
  1. Реализовать 3 уровня видимости переменных:
     - **Локальные (Sprite Variables)**: принадлежат конкретному спрайту и его клонам.
     - **Сценные (Scene Variables)**: сбрасываются при смене сцены.
     - **Глобальные (Project Variables)**: живут всё время работы игры.
  2. Реализовать списки (UserList): добавление, удаление по индексу, получение элемента по индексу, проверка наличия элемента, очистка.
  3. Реализовать сохранение на диск (`WriteVariableOnDevice`):
     - Сохранять в папку сейвов приложения `AppData/Roaming/NeoCatroid/<ProjectName>/saved_vars.json` (или `~/.local/share/neocatroid/...` на Linux).
     - `ReadVariableFromDevice` загружает сохраненное значение или возвращает дефолтное, если сейва нет.
  4. Реализовать файловые операции (`WriteToFiles`, `ReadFromFiles`, `AppendToFile`) с использованием `std::filesystem`. Изолировать пути в песочнице проекта, предотвращая выход за пределы папки сейвов/проекта.

---

### СУБАГЕНТ 5: 2D Физика Box2D (Physics 2D)
- **Целевые файлы**: `src/physics/*`, `src/actions/PhysicsActions.cpp`.
- **Источники для изучения в Java**:
  - `catroid/src/main/java/org/catrobat/catroid/physics/PhysicsWorld.java`
  - `catroid/src/main/java/org/catrobat/catroid/physics/PhysicsObject.java`
  - `content/actions/SetPhysicsVelocityAction.java`, `SetGravityAction.java`, `SetBounceAction.java`, `SetFrictionAction.java`, `SetMassAction.java`.
  - `content/bricks/SetPhysicsObjectTypeBrick.java` (Static, Dynamic, None/Disable).
- **Обязанности**:
  1. Интеграция `b2World` (шаг симуляции 60 Hz).
  2. Масштаб единиц: согласовать пиксели Catroid и метры Box2D (изучить коэффициент масштабирования в `PhysicsWorld.java`).
  3. Создание физических тел `b2Body`:
     - Типы: Static, Dynamic, Kinematic.
     - Формы коллизий: Box (по размерам образа спрайта) или Circle.
  4. Применение сил и импульсов к телам.
  5. Синхронизация: после каждого `b2World::Step()` переносить новые `(x, y)` и угол поворота из `b2Body` обратно в свойства `Sprite`.
  6. Обработка коллизий через `b2ContactListener`: при столкновении генерировать событие `WhenCollision(targetSpriteName)`.

---

### СУБАГЕНТ 6: Рендерер, Аудио и Платформа (Render, Audio & Platform)
- **Целевые файлы**: `src/render/*`, `src/audio/*`, `src/main.cpp`.
- **Обязанности**:
  1. Инициализация окна Raylib (или SDL3 + OpenGL). Поддержка ресайза окна, полноэкранного режима, VSync.
  2. Виртуальное разрешение (Letterboxing):
     - Если проект сделан под экран 1080x1920 (вертикальный) или 1920x1080 (горизонтальный), рендерер масштабирует виртуальный экран с сохранением пропорций (черные полосы по краям).
  3. Отрисовка спрайтов:
     - Отрисовка текстуры текущего образа (`currentLook`) с учетом `x, y`, поворота, масштаба (`size`), прозрачности и эффекта зеркального отражения (`LeftRight`).
     - Правильный учет опорной точки (Anchor / Center) спрайта.
  4. Обработка ввода:
     - Клик левой кнопкой мыши транслируется в координату виртуального экрана проекта.
     - Проверка попадания в спрайт (Hit-test / Point-in-polygon / AABB) -> запуск события `WhenTapped`.
     - Маппинг клавиатуры (Стрелки, WASD, Пробел) на виртуальные действия.
  5. Аудиосистема (`miniaudio`):
     - Загрузка аудиофайлов из архива проекта.
     - `PlaySoundAction`, `StopAllSoundsAction`, громкость звуков.

---

### СУБАГЕНТ 7: Реестр Заглушек и Telemetry (Stub Registry & Fallbacks)
- **Целевые файлы**: `src/actions/StubActions.cpp`, `src/actions/ActionRegistry.cpp`.
- **Обязанности**:
  1. Собрать полный список всех 973 названий тегов Bricks.
  2. Все теги, относящиеся к:
     - AdMob (`Admob*`)
     - Bluetooth / Робототехнике (`Lego*`, `Phiro*`, `Arduino*`, `Drone*`)
     - Сенсорам телефона (`Nfc*`, `Flash*`, `Vibrate*`, `Compass*`)
     - Android-специфичным диалогам
     маппить на `StubAction`.
  3. `StubAction` выполняет безопасный no-op (пропуск действия) и регистрирует статистику:
     `[STUB LOG] Unhandled mobile block ignored: 'AdmobLoadBanner' (called 1 time)`.
  4. Гарантировать, что ни один неизвестный тег XML **никогда не приводит к аварийному завершению (crash)** приложения.

---

## 5. ПРОТОКОЛ ПРОВЕРКИ 1-В-1 (VERIFICATION PROTOCOL)

Каждый субагент обязан соблюдать строгий протокол верификации при переносе любого действия:
1. **Открыть эталонный класс Java**: например, `catroid/src/main/java/org/catrobat/catroid/content/actions/ChangeSizeByNAction.java`.
2. **Изучить метод `act(float delta)` / `update(float percent)`**:
   - Какие поля читаются?
   - Как обрабатываются граничные случаи (например, размер меньше 0 — Catroid клампит размер до 0.0 или разрешает отрицательный? Проверить в Java!).
   - Как мутируется объект `Sprite`?
3. **Написать C++ реализацию**: вставить эквивалентную математику.
4. **Проверить соответствие типов в формуле**: имена параметров в XML должны строго совпадать с ключами в `BrickField` соответствующего `*Brick.java`.

---

## 6. ПОШАГОВЫЙ ПЛАН ВЫПОЛНЕНИЯ (MILESTONES)

- [ ] **Этап 1: Каркас и Парсер (Milestone 1)**
  - Собрать проект с `miniz` и `pugixml`.
  - Научить плеер открывать любой `.neotrobat` файл, распаковывать картинки в память и парсить список спрайтов и образов.
  - Открыть окно Raylib и отобразить статичный фон и спрайты в координатах (0, 0).
- [ ] **Этап 2: Вычисление Формул (Milestone 2)**
  - Реализовать дерево формул и вычисление математических выражений.
  - Прогнать unit-тесты на формулы (сложение, строки, функции `sin/cos`, логические условия).
- [ ] **Этап 3: Базовые Скрипты и Движение (Milestone 3)**
  - Реализовать события `WhenProgramStarts`, `WhenTapped`.
  - Подключить `SetX`, `ChangeX`, `SetY`, `ChangeY`, `SetSize`, `Hide`, `Show`.
  - Реализовать циклы `Forever`, `Repeat` и `Wait`.
  - **Контрольная точка**: Запуск первого простейшего кликера/анимации!
- [ ] **Этап 4: Переменные, Списки и Условия (Milestone 4)**
  - Логика `If / Else`.
  - Переменные и списки, вывод их значений.
  - Сохранения `WriteVariableOnDevice` на диск.
- [ ] **Этап 5: Физика Box2D и Звук (Milestone 5)**
  - Интеграция `miniaudio`, воспроизведение звуков.
  - Подключение Box2D, симуляция гравитации, прыжков, отскоков и коллизий.
  - **Контрольная точка**: Полноценный запуск сложных физических платформеров!

---

## 7. РЕЗЮМЕ ДЛЯ ИИ

> Приступай к реализации по указанным шагам. Создавай чистый, современный код на C++20 с понятной архитектурой, без оверинжиниринга. Используй макросы или таблицы диспетчеризации для однотипных блоков. Твоя главная цель — дать пользователю на ПК возможность дважды кликнуть на `.neotrobat` игру и получить мгновенный, плавный геймплей с идеальной физикой и звуком.

