# LunoScript

Исходники: `catroid/src/main/java/org/catrobat/catroid/utils/lunoscript/`. Блок: `LunoScriptBrick` (`content/bricks/LunoScriptBrick.java:31`, код — формула; `createLunoScriptAction`, `:55`).

## Модель интерпретатора

Конвейер (`LunoScriptEngine.execute`, `utils/lunoscript/LunoScriptEngine.kt:32-38`): `Lexer(script).scanTokens()` → `Parser(tokens).parse()` → `ProgramNode` → `Interpreter.interpret()`. Ошибки `LunoSyntaxError`/`LunoRuntimeError` (`LunoExceptions.kt`) ловятся, печатаются в stderr и показываются тостом (`:39-54`, `handleLunoError`). Свои нативные функции: конструктор `customNativeFunctions` и `registerNativeFunction(name, arity, func)` — кладутся в `interpreter.globals` (`:22-30`).

- `Lexer.kt` — ключевые слова (`:10-22`): `var`/`val`, `if`/`else`, `true`/`false`/`null`, `fun`/`return`, `class`/`this`/`super` (+`static`), `while`/`for`/`in`, `break`/`continue`, `switch`/`case`/`default`, `import`, `try`/`catch`/`finally`. Токены: `Token.kt`.
- `AstNodes.kt` — `ProgramNode`; выражения (`:12-38`): `Literal`, `Variable`, `This`, `Binary`, `Logical`, `Unary`, `InterpolatedString`, `Call`, `Lambda`, `Get`/`Set`, `IndexAccess`, `ListLiteral`, `MapLiteral`; statements (`:40-75`): `Expression`, `VarDeclaration`, `Assignment`, `Block`, `If`, `While`, `ForIn`, `FunDeclaration` (+`Return`/`Break`/`Continue`), `ClassDeclaration` (методы, суперкласс, static-блок), `Switch`, `Import`, `TryCatch`. Парсер: `Parser.kt`.
- `LunoValue.kt` — sealed-иерархия значений (`:11-297`): `Null`, `Number(Double)`, `Float`, `String`, `Boolean`, `List(MutableList)`, `LunoObject`, `LunoClass`, `LunoFunction(декларация + closure-Scope)`, `NativeClass`, `NativeObject`, `NativeCallable`, `BoundMethod`/`BoundMethod2`, базовый `Callable`.
- `Scope.kt` — цепочка областей (`enclosing`); запись `ScopeEntry(value, isConstant)`; `define/get/assign`, `getAt/assignAt` по глубине, `resolve(expr, depth)` + `lookUpVariable` (`:3-87`).
- `Interpreter.kt` — дерево-проходчик; нативы регистрируются `defineNative(name, arity: IntRange, fn)` (`:3850`); все ниже — `globals` интерпретатора. Доступ к Java: `NativeProxyFactory.kt`, `createListener`, `NativeObject`/`NativeClass` (рефлексия геттеров/сеттеров/полей/методов, `:4496-4573`); конструктор бриков `Brick(name, params)` — имя нижним регистром + список параметров (`:3504+`).
- Остальное: `LunoApi.kt`, `LunoDelayAction.kt`, `RenderManager.kt` (+ `render*`/`runInRenderThread`/`runInUIThread`/`createView`/`addView`/`removeView`), `baker/ProjectBaker.kt`, `security/LunoSecurity.kt`, `security/LunoSandbox.kt`, `CallableNativeLunoFunction.kt`.

## Встроенные функции (`Interpreter.kt`, `defineNative("имя", арность)`)

Ядро: `GetAppContext` 0, `GetStageListener` 0, `StageActivity` 0, `MakeToast` 1+, `delay` 1, `print` 0+, `error` 0+, `len` 1, `typeof` 1, `parseInt` 1–2, `parseFloat` 1, `isNaN` 1, `isFinite` 1, `currentTimeMillis` 0, `String`/`Number`/`Boolean`/`Formula` 1, `throw` 1, `getBytes` 1–2, `bytesSize` 1.

Математика: `sqrt`/`abs`/`round`/`floor`/`ceil` 1, `random` 0, `min`/`max` 1+, `pow` 2.

Строки (1-й аргумент — строка): `toUpperCase`/`toLowerCase`/`trim` 1, `startsWith`/`endsWith`/`stringContains` 2, `replace` 3, `split` 2, `substring` 2–3.

Списки: `listPush` 2+, `listPop`/`listShift` 1, `listUnshift` 2+, `listJoin` 1–2, `listSlice` 2–3, `listReverse` 1. Объекты/JSON: `ObjectKeys`/`ObjectValues` 1, `ObjectHasProperty` 2, `JSON_parse`/`JSON_stringify` 1.

Локальные переменные/скоуп: `SetLocalVar`/`GetLocalVar`/`DeleteLocalVar` 2/1/1, `DeleteLocalVars` 0, `GetLocalVarName`/`GetLocalVarValue` 1, `GetUserDefinedBrickField` 1, `GetScope` 0, `GetSprite` 1.

Проект (1-й аргумент — проект, если не `0`): `GetProject` 1, `ProjectGetDirectory`/`ProjectSetDirectory` 1, `ProjectGetSceneList`/`ProjectGetSceneNames` 1, `ProjectAddScene`/`ProjectRemoveScene` 2, `ProjectHasScene` 1, `ProjectGetDefaultScene` 1, `ProjectGetUserVariables(Copy)`/`ProjectGetUserVariable`/`ProjectAddUserVariable`/`ProjectRemoveUserVariable`, `UserVariable` 0–2, `ProjectGetUserLists(Copy)`/`ProjectGetUserList`/`ProjectAddUserList`/`ProjectRemoveUserList`, `UserList` 0–2, `ProjectResetUserData` 1, `ProjectGetSpriteListWithClones` 1, `ProjectGetName`/`SetName`, `ProjectGetDescription`/`SetDescription`, `ProjectGetNotesAndCredits`/`SetNotesAndCredits`, `ProjectGetCatrobatLanguageVersion`/`SetCatrobatLanguageVersion`, `ProjectGetXmlHeader`/`SetXmlHeader`, `ProjectGetFilesDir`/`ProjectGetLibsDir`, `ProjectGetFile`/`ProjectGetLib` 2, `ProjectCheckExtension` 3, `ProjectAddFile`/`ProjectDeleteFile` 2, `ProjectGetScreenRectangle` 1, `ProjectGetTags`/`ProjectSetTags`, `ProjectGetSceneByName`/`ProjectGetSceneById` 2. Файлы: `File` 1–2, `FilePath` 1, `Rectangle` 4.

Спрайт/образ: `Sprite` 1–2, `SpriteGetName` 1, `SpriteGetScriptList`/`LookList`/`SoundList`/`UserVariables`/`UserLists` 1, `SpriteGetUserVariable`/`SpriteAddUserVariable` 2, `SpriteGetUserList`/`SpriteAddUserList` 2, `SpriteAddScript` 2–3, `SpriteGetScript` 2, `SpriteRemoveScript` 2, `SpriteRemoveAllScripts` 1, `SpriteIsClone`/`SpriteOriginal`/`SpriteIsGliding`/`SpriteIsBackground` 1, `SpriteGetAllBricks` 1, `SpriteGetActionFactory` 1, `ActionFactory` 0, `Look` 1, `SpriteGetLook` 1, `LookRemove` 1, `LookGetLookData(2)`/`LookData` 2/`LookDataGetName`/`SetName`/`GetFile`/`SetFile`/`LookSetLookData(2)`, геттеры `LookGetX/Y/Width/Height/Rotation/Alpha/Brightness/Hue/Visible/LookVisible` 1, сеттеры `LookSetX/Y/Width/Height/Rotation/Alpha/Brightness/Hue/Visible/LookVisible` 2.

Скрипты/брики: `ScriptGetBrickList` 1, `ScriptGetBrick`/`ScriptRemoveBrick` 2, `ScriptAddBrick` 2–3, `ScriptRun` 3, `GetSequence` 1, `ScriptSequenceAction` 1, `UserVariableGetValue`/`SetValue` 1/2, `RunBrick` 3, `Brick` 2, `createListener` 2.

Рендер/UI-поток: `render` 1, `renderWidth`/`renderHeight` 0, `renderDelete` 1, `runInRenderThread`/`runInUIThread`/`createView`/`removeView` 1, `addView` 5. Контекст проекта: `CreateEmptyProjectContext` 0, `SetGlobalProject` 1.
