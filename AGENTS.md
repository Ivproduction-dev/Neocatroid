# NeoCatroid — гайд для разработчиков

Данный проект - NeoCatroid. Форк Danveyd/NewCatroid. Форк Catrobat/Catroid (Pocket Code).

То есть цепочка такая: Pocket Code (Catrobat) = оригинал, NewCatroid (Danveyd) = форк оригинала,
NeoCatroid (мы) = форк форка. Основной код, структура и соглашения — от оригинала,
поэтому этот файл повторяет структуру `C:\Users\ivanp\ньюкатриод\AGENTS.md`, а ниже —
только то, чем мы отличаемся.

Это универсальный конструктор приложений / игр с визуальным программированием через блоки.

**Чем NeoCatroid отличается от NewCatroid (коротко):**
*   **APK Builder V3:** сборка автономной игры из `template_runtime.apk` с переименованием пакета.
*   **Neo3D + Jolt:** новый 3D-движок (`neo3d/`), физика Jolt v5.2.0 из исходников (`catroid/src/main/cpp/jolt/`).
*   **Свет 2D (`twodlight/`):** overlay-освещение без новых зависимостей.
*   **NeoScript (`.neoscript`):** переиспользуемые модули скриптов (экспорт/импорт + рантайм-брики).
*   **Совместное редактирование (`collab/`):** presence + локи скриптов + хост-шлюз синхронизация через Firestore.
*   **AI-блок один вместо четырёх:** `AskAIBrick` + событие `WhenAIResponseBrick`.
*   **Scene-переменные:** переменные уровня сцены (списков сцен нет).
*   **Касание спрайтов:** события `WhenTouchingSprite / WhenTouchingSpriteByName` без физики.
*   Палитра выровнена под Danveyd, наши кастомы — в конец.

# Теперь гайд по проекту #

Пути внутри синхронизированного проекта Android Studio. Предполагается, что проект уже настроен.

`res/values` - тут все глобальные значения: цвета, строки и др.

`res/values/strings` - языки, основные английский и русский. Английский обновлять обязательно, без него не будет работать. Английский никак не обозначен, русский подписан ru.

`res/layout` - все лайауты, то есть xml файлы, где прописано как выглядят блоки, менюшки и др.

`assets` - ассеты. То что зеленое - тестовое, при релизном билде не будет включено (так везде).

`kotlin+java/org.catrobat.catroid/` - основные скрипты, тут все .java, .kt файлы.

`kotlin+java/org.catrobat.catroid/content` - основной контент (блоки, действия блоков и др.), контроллеры (микрофон, Firebase, ИИ и т.д.).

`kotlin+java/org.catrobat.catroid/content/actions` - действия блоков, то есть по сути их код.

`kotlin+java/org.catrobat.catroid/content/bricks` - классы блоков, они соединяют действия, лайауты и т.д. воедино.

`kotlin+java/org.catrobat.catroid/content/ActionFactory.java` - "Фабрика" блоков, тут берем действие, запихиваем в него введенные значения и возвращаем готовое к выполнению действие.

`kotlin+java/org.catrobat.catroid/content/GlobalManager.kt` - глобальный менеджер для глобальных переменных. Сейчас там `stopSounds` и `saveScenes`.

`kotlin+java/org.catrobat.catroid/raptor` - СТАРЫЙ 3D (ThreeDManager, SceneManager). Не путать с новым `neo3d/`. Legacy-брики (`SetPhysicsStateBrick`) трогать только для старых проектов.

`kotlin+java/org.catrobat.catroid/neo3d` - НОВЫЙ 3D-движок (Engine, Facade, Persistence, FormulaBridge, physics-бэкенды). Активная разработка здесь.

`kotlin+java/org.catrobat.catroid/twodlight` - свет 2D (модель, менеджер, рендер lightmap, тени через raycast Box2D-мира).

`kotlin+java/org.catrobat.catroid/collab` - совместное редактирование (сессии, presence, локи, Sync-потоки, транспорт Firestore).

`kotlin+java/org.catrobat.catroid/apkbuildV3` - сборщик игр V3 (`V3ApkAssembler`, `TemplateManagerV3`, `FirebaseConfigManager`).

`kotlin+java/org.catrobat.catroid/neoscript` - модули `.neoscript` (модель, сериализация, экспорт/импорт, релинк переменных).

`kotlin+java/org.catrobat.catroid/fast2d` - 2D рендер (ECS-based).

`kotlin+java/org.catrobat.catroid/editor` - 3D редактор, все классы для него - тут.

`kotlin+java/org.catrobat.catroid/stage` - StageActivity, рендер-луп, события.

`kotlin+java/org.catrobat.catroid/formulaeditor` - FormulaElement, Functions, парсер формул.

`kotlin+java/org.catrobat.catroid/utils/lunoscript` - исходники LunoScript, в Interpreter объявления всех встроенных функций.

`kotlin+java/org.catrobat.catroid/ui` - Activity, Fragment'ы, адаптеры, диалоги.

# Гайд на добавление какого-либо блока:

О файлах проекта: в проектах есть свое отдельное хранилище - файлы проекта. Получить файл можно через `scope?.project?.getFile(String)`.
Важно Neo: `scope.project` — это живой канонический `Project`. Мутация модели + опциональный `ProjectSaver.saveProjectAsync` = персист на диск (так работают `CreateObjectBrick` / `AssignScriptsBrick`).

1. создать Action. Вот пример:

```kotlin
class MyAction : TemporalAction() {
    var scope: Scope? = null
    var myParam: Formula? = null

    override fun update(percent: Float) {
        val valStr = myParam?.interpretString(scope) ?: ""
        // логика блока (выполняется 1 раз)
    }
}
```

2. добавляем переводы (английский обязательно, русский обязательно):

```xml
<string formatted="false" name="my_block_label">Do something</string>
```

ВАЖНО: текст в блоке должен быть максимально коротким, иначе не влезет на экран.

3. добавляем лайаут, например `brick_my_block.xml`:

```xml
<LinearLayout ...>
    <!-- вот этот чекбокс очень важен! без него - вылеты -->
    <CheckBox android:id="@+id/brick_checkbox" android:visibility="gone" />
    <org.catrobat.catroid.ui.BrickLayout style="@style/BrickContainer.Look.Small">
        <include layout="@layout/icon_brick_category_..." />
        <TextView style="@style/BrickText.SingleLine" android:text="@string/my_block_label" />
        <TextView android:id="@+id/brick_my_edit" style="@style/BrickEditText" />
    </org.catrobat.catroid.ui.BrickLayout>
</LinearLayout>
```

В качестве категории и иконки используй категорию, которую скажут, а размер такой:
1 параметр: Small, 2-3 параметра: Medium, 4 и более - Big.
Каждый параметр советую ставить на новую строчку.

Спиннеры (пример — выбор режима как в `SetPhysicsStateBrick`): `BrickSpinner<StringOption>` или обычный `Spinner` во `getView()`, дефолт спиннера в поле = 0 (наименее деструктивный), видимость зависимых полей переключать в `updateVisibility()`.

4. добавляем в ActionFactory:

```java
public Action createMyAction(Sprite sprite, SequenceAction sequence, Formula param) {
    MyAction action = action(MyAction.class);
    Scope scope = new Scope(ProjectManager.getInstance().getCurrentProject(), sprite, sequence);
    action.setScope(scope);
    action.setMyParam(param);
    return action;
}
```

5. создаем финальный Brick:

```java
public class MyBrick extends FormulaBrick {
    private static final long serialVersionUID = 1L;

    public MyBrick() {
        addAllowedBrickField(BrickField.TEXT, R.id.brick_my_edit);
    }
    public MyBrick(String value) { this(new Formula(value)); }
    public MyBrick(Formula formula) {
        this();
        setFormulaWithBrickField(BrickField.TEXT, formula);
    }

    @Override public int getViewResource() { return R.layout.brick_my_block; }
    @Override public void addActionToSequence(Sprite sprite, ScriptSequenceAction sequence) {
        sequence.addAction(sprite.getActionFactory()
            .createMyAction(sprite, sequence, getFormulaWithBrickField(BrickField.TEXT)));
    }
}
```

Обязательно создай конструктор, чтобы можно было создать блок из обычных строк / чисел, а не только из формул.

6. добавляем описание блока в BrickInfo (русский + английский, тексты короткие):

```java
add(MyBrick.class, "Делает что-то");
addEn(MyBrick.class, "Does something");
```

7. XStreamSerializer: явная регистрация НЕ требуется. `XStreamBrickConverter` находит все Brick-классы
по имени в пакетах `org.catrobat.catroid.content.bricks` и `org.catrobat.catroid.physics.content.bricks`,
неизвестные типы при загрузке становятся `UnknownBrick` (проект не ломается).
Исключение: legacy-переименования — добавлять в `LEGACY_BRICK_ALIASES` (пример: `ShowToastBlock` -> `ShowToastBrick`).

8. ну и в CategoryBricksFactory в нужный список:

```kotlin
myBrickList.add(MyBrick("1"))
```

Правило палитры Neo: общие брики — строго в порядке Danveyd (эталон — NewCatroid, мышечная память),
наши кастомы — в конец в текущем относительном порядке. Группированные ветки (SubCategoryHeader) не трогать.
Проверка после правки: multiset `add` против HEAD (без потерь/дублей).
Скрытые из палитры классы (8 native-диалогов + 8 story-диалогов) живы для старых проектов — не удалять.

# Гайд как добавлять формулы:

1. добавляем переводы (русский и английский):

```xml
<string name="formula_my_func" formatted="false">MyFunc</string>
```

Пробелы заменяем на нижние подчеркивания.

2. в тех же переводах делаем строку параметров. Апострофы экранировать `\'\'`, иначе aapt падает:

```xml
<string name="formula_my_func_param" formatted="false">(\'value\')</string>
```

3. добавляем в Functions.java в enum + в нужный сет (обычно TEXT):

```java
MY_FUNC,
```

4. добавляем в InternFormulaKeyboardAdapter (там огромный switch case):

```java
case R.string.formula_my_func:
    return buildSingleParameterFunction(Functions.MY_FUNC, STRING, "value");
```

5. в InternToExternGenerator:

```java
INTERN_EXTERN_LANGUAGE_CONVERTER_MAP.put(Functions.MY_FUNC.name(), R.string.formula_my_func);
```

6. в CategoryListFragment в соответствующий список FUNCTIONS/PARAMS (списки идут парами, размеры должны совпадать):

```java
private static final List<Integer> MY_FUNCTIONS = asList(R.string.formula_my_func);
private static final List<Integer> MY_PARAMS = asList(R.string.formula_my_func_param);
```

7. основной кейс в FormulaElement:

```java
case MY_FUNC: {
    return interpretMyFunc(arguments);
}
```

Neo-раздел «Neo3D» в OBJECT_TAG — пример массовой регистрации: `NEO3D_X/Y/Z`, `NEO3D_YAW/PITCH/ROLL`,
`NEO3D_SCALE_X/Y/Z`, `NEO3D_BODY_COUNT`, `NEO3D_DISTANCE`, `NEO3D_SPEED`, `NEO3D_EXISTS` через `Neo3DFormulaBridge`
(нет движка/объекта → 0.0, масштаб → 1.0, без побочных эффектов).

# Neo-специфика (то, чего нет в оригинале, коротко):

*   **V3 шаблон:** игры собираются из `template_runtime.apk`. Кэш `filesDir/v3_template/template_runtime_v3.apk`
    используется как есть, при пустом — скачивание с GitHub (`Neocatroid-Template`, LFS-детект). Регенерация:
    `./gradlew copyTemplateApk -x uploadCrashlyticsMappingFileRuntimeTemplate`. После правок лоадера/света
    (`ProjectLoaderV3`, `twodlight`) template ОБЯЗАТЕЛЬНО перегенерить, иначе игра потеряет файлы (дедуп-манифест).
    Переименование пакета: сначала `ensureFullClassNames()` против СТАРОГО пакета, потом смена + замена authority
    провайдера. Подпись: экземпляр `BouncyCastleProvider()`, НЕ имя "BC" (на Android там урезанный провайдер).
    `syncPermissions()` сначала удаляет ВСЕ permissions темплейта, потом добавляет выбранные. Иконка: только
    `ic_launcher*.png`, `mipmap-anydpi-v26/*.xml` не трогать (иначе битая adaptive-иконка на API 26+).
*   **Neo3D/Jolt:** Jolt v5.2.0 из исходников (`catroid/src/main/cpp/jolt/`), т.к. AAR требует minSdk 33 (нам надо 31).
    Ordinal в натив: motion 0=None/1=Static/2=Kinematic/3=Dynamic, shape 0=Auto/1=Box/2=Sphere/3=Capsule/4=Cylinder.
    `BoxShape` — от `Vec3(halfExtent)`, `MapObjectToBroadPhaseLayer` — явный `BroadPhaseLayer(0)`.
    Камера от пальца: `StageListener.touchDragged` → `facadeDragCameraLook`, FPS-кламп min/max, free без клампа.
    Событие касания: `WhenNeo3DCollidesScript` (NAME+TARGET, пустой TARGET = любой), edge-trigger в `StageListener`.
*   **Свет 2D:** без box2dlights, тени — raycast существующего Box2D-мира. Без светов проходов нет (старые проекты
    не меняются). Proxy-тела: `categoryBits=0/maskBits=0`, `userData=Sprite`. Диагностика: logcat `Light2D`
    (`pass active` → `multiply pass` → `shader=false`). После правок `twodlight` — реген template (proguard держит пакет).
*   **Collab:** RTDB НЕ используется (нет `firebase_url`). Firestore-проект `privacy-neocatroid`, отдельный
    FirebaseApp `collab` (`collab/CollabFirebase.kt`). Telemetry-Firestore не трогать. Модель «хост-шлюз»: PAT
    только у хоста, гости по коду комнаты, `.git` только в `filesDir/collab-git/{sid}` (не в папке проекта).
    Локи: heartbeat 10с, TTL 30с; presence: heartbeat 5с, TTL 20с на клиенте. Offline = fail-open.
*   **NeoScript:** scene хранится именем (`null`/empty = Current scene), поиск объекта — в резолвленной сцене.
    `ImportStrategy`: 0 = least destructive по дефолту; `REPLACE_ALL` — атомарный. `ImportScriptBrick`
    (overwrite дубликатов) не путать с `AssignScriptsBrick` (replace всех скриптов). Persist-флаг: только решает,
    писать ли канонический проект на диск (`saveProjectAsync`, best-effort), рантайм одинаковый. `UnknownBrick`
    при импорте → замена на `NoteBrick`.
*   **Scene-переменные:** порядок `UserDataWrapper.getUserVariable`: sprite → scene → project → multiplayer.
    Сценных списков нет. Сброс при старте проекта (`resetAllUserData`) и при смене сцены (`resetLeavingSceneVariables`).
*   **AI:** один `AskAIBrick` (провайдер + промпт + системный + модель + переменная ответа) + событие
    `WhenAIResponseBrick` (пустой provider = любой). Старые `AskGPT/AskGemini/SetGeminiKey` оставлены для
    десериализации, убраны только из палитры. `AiProvider` — единственный источник списка провайдеров.
*   **Касание спрайтов:** AABB без физики, edge-trigger (fire при ВХОДЕ, reset при расхождении).
    `reactToBackground=false` по дефолту. Desktop: `DesktopScriptEngine` → `"touching_sprite"`.
*   **Регдолл:** `Sprite.ragdollMode` 0=выкл/1=регдолл/2=следование. При `>0` сеттеры позиции/вращения/масштаба
    не пишут в тело, геттеры читают с тела; режим 2 — P-контроллер догона цели в `PhysicsLook.draw()`.
    Формула `SPRITE_RAGDOLLED` = `ragdollMode > 0`.
*   **GLB/GLTF:** `ThreeDManager.createObject/replaceModel` ловят `Throwable` (OOM тоже), preflight отклоняет
    `KHR_draco_mesh_compression` / `EXT_meshopt_compression` / `KHR_texture_basisu`, при отказе — куб-примитив,
    рендер-цикл упавший инстанс удаляет после `modelBatch.end()`. Логкаты: `3DManager_PBR` / `3DManager`.
*   **Crash:** `BaseExceptionHandler` в `CatroidApplication.onCreate` + повторно в `MainMenuActivity.onCreate`,
    отчет в `cacheDir/crashReports/` + `last_crash_log.txt`, отправка в Firestore `crashes`. `EditorActivity` —
    свой handler с emergency-save сцены. Настройка `setting_enable_crash_reports` (default true).
*   **Backpack:** JSON + файлы, звуки/значения внутри скрипт-группы (`backpackedScriptSounds`,
    `backpackedVariableValues/ListValues`), dedup звуков по имени при unpack.
*   **Paintroid:** сейв из Catroid-режима копируется в `Download/Paintroid` (MediaStore), возвращаемый URI не меняется.

# Сборка / проверка:

```bash
./gradlew copyTemplateApk -x uploadCrashlyticsMappingFileRuntimeTemplate  # реген template_runtime.apk
./gradlew :catroid:compileCatroidDebugKotlin :catroid:compileCatroidDebugJavaWithJavac --offline  # быстрая проверка
./gradlew :catroid:testCatroidDebugUnitTest --tests "*AskAIBrickTest*" --tests "*WhenAIResponseBrickTest*"
./gradlew :catroid:testCatroidDebugUnitTest --tests "*twodlight*" --tests "*Light2DBrick*" --tests "*ShadowCasting2D*"
```

Протухший инкрементальный kapt (`cannot find symbol` на ровном месте): `./gradlew --stop` + удалить `catroid/build/tmp/kapt3`.
Jolt abi: `armeabi-v7a` + `arm64-v8a` + `x86_64`, проверять `:catroid:buildCMakeDebug[<abi>]`.
