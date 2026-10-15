# Устройство / Device

Ключ категории: `category_device`. Источник состава и порядка: `CategoryBricksFactory.kt` (группированный режим).

## Скрипты и код / Scripting & Code

### 1. RunJSBrick

| Поле | Значение |
|---|---|
| Название (тексты лейаута) | `brick_run_js`: RU «Выполнить Micro-JavaScript код» / EN «Run Micro-JavaScript code»<br>`brick_run_js2`: RU «и сохранить результат в» / EN «and store result in» |
| Параметры | BrickField: `CODE` |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/RunJSBrick.java:32` |
| Класс экшена | `catroid/src/main/java/org/catrobat/catroid/content/actions/RunJSAction.kt:26` (RunJSAction) |
| Описание (BrickInfo.java) | RU: Выполняет JavaScript и сохраняет результат<br>EN: Executes JavaScript and saves the result |


### 2. RunLuaBrick

| Поле | Значение |
|---|---|
| Название (тексты лейаута) | `brick_run_lua`: RU «Выполнить Lua код» / EN «Run Lua code»<br>`brick_run_lua2`: RU «и сохранить результат в» / EN «and store result in» |
| Параметры | BrickField: `CODE` |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/RunLuaBrick.java:32` |
| Класс экшена | `catroid/src/main/java/org/catrobat/catroid/content/actions/RunLuaAction.kt:39` (RunLuaAction) |
| Описание (BrickInfo.java) | RU: Выполняет ЧИСТЫЙ Lua и сохраняет результат ВАЖНО: Lua в Pocket Up и CCode и Lua в NeoCatroid - НЕ ОДНО И ТО ЖЕ! в NeoCatroid он не умеет отрисовывать что-то<br>EN: Executes PLAIN Lua and saves the result IMPORTANT: Lua in Pocket Up and CCode and Lua in NeoCatroid are NOT THE SAME! in NeoCatroid it cannot render anything |


### 3. LunoScriptBrick

| Поле | Значение |
|---|---|
| Название (тексты лейаута) | `brick_lunoscript`: RU «Выполнить LunoScript» / EN «Execute LunoScript» |
| Параметры | BrickField: `CODE` |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/LunoScriptBrick.java:30` |
| Класс экшена | `catroid/src/main/java/org/catrobat/catroid/content/actions/LunoscriptAction.kt:43` (LunoscriptAction) |
| Описание (BrickInfo.java) | RU: Выполняет LunoScript, это один из самых мощных инструментов, позволяет выполнять почти любой Java код внутри приложения. Документация есть на GitHub: Danveyd/LunoScript<br>EN: Executes LunoScript, one of the most powerful tools, allows running almost any Java code inside the application. Documentation is available on GitHub: Danveyd/LunoScript |


> Условие: только если BuildConfig.FEATURE_PYQEMU_BRICKS_ENABLED.

### 4. RunPythonScriptBrick

| Поле | Значение |
|---|---|
| Название (тексты лейаута) | `python_run_script`: RU «Запустить Python» / EN «Run Python script»<br>`python_script_content`: RU «Скрипт» / EN «Script»<br>`python_save_log_to`: RU «вывод в локальную переменную» / EN «output to local var» |
| Параметры | BrickField: `SCRIPT`, `VARIABLE_NAME` |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/RunPythonScriptBrick.java:30` |
| Класс экшена | `catroid/src/main/java/org/catrobat/catroid/content/actions/RunPythonScriptAction.kt:9` (RunPythonScriptAction) |
| Описание (BrickInfo.java) | RU: Запускает Python 3.12 скрипт ВАЖНО: Python работает только на архитектуре arm64_v8a, на сторонних он не запустится!<br>EN: Runs Python 3.12 script IMPORTANT: Python only works on arm64_v8a architecture, it will not run on others! |


> Условие: только если BuildConfig.FEATURE_PYQEMU_BRICKS_ENABLED.

### 5. LoadNativeModuleBrick

| Поле | Значение |
|---|---|
| Название (тексты лейаута) | `python_load_native_module`: RU «Загрузить модуль (Python)» / EN «Load Py native module»<br>`python_module_file_name`: RU «Файл проекта(.so)» / EN «project file (.so)» |
| Параметры | BrickField: `FILE_NAME` |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/LoadNativeModuleBrick.java:7` |
| Класс экшена | `catroid/src/main/java/org/catrobat/catroid/content/actions/LoadNativeModuleAction.kt:10` (LoadNativeModuleAction) |
| Описание (BrickInfo.java) | RU: Загружает нативный модуль (.so), нужно для некоторых Python библиотек<br>EN: Loads a native module (.so), needed for some Python libraries |


> Условие: только если BuildConfig.FEATURE_PYQEMU_BRICKS_ENABLED.

### 6. LoadPythonLibraryBrick

| Поле | Значение |
|---|---|
| Название (тексты лейаута) | `python_load_library`: RU «Загрузить библиотеку (Python)» / EN «Load Py library»<br>`python_library_file_name`: RU «Файл проекта (.whl, .zip)» / EN «project file (.whl, .zip)» |
| Параметры | BrickField: `FILE_NAME` |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/LoadPythonLibraryBrick.java:30` |
| Класс экшена | `catroid/src/main/java/org/catrobat/catroid/content/actions/LoadPythonLibraryAction.kt:13` (LoadPythonLibraryAction) |
| Описание (BrickInfo.java) | RU: Загружает библиотеку Python (.whl, .zip), позволяет подключать любые сторонние библиотеки<br>EN: Loads a Python library (.whl, .zip), allows connecting any third-party libraries |


> Условие: только если BuildConfig.FEATURE_PYQEMU_BRICKS_ENABLED.

### 7. ClearPythonEnvironmentBrick

| Поле | Значение |
|---|---|
| Название (тексты лейаута) | `python_clear_environment`: RU «Очистить окружение (Python)» / EN «Clear Python environment» |
| Параметры | — (в коде брика полей BrickField нет) |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/ClearPythonEnvironmentBrick.java:29` |
| Класс экшена | `catroid/src/main/java/org/catrobat/catroid/content/actions/ClearPythonEnvironmentAction.kt:6` (ClearPythonEnvironmentAction) |
| Описание (BrickInfo.java) | RU: Очищает окружение Python<br>EN: Clears the Python environment |


> Условие: только если BuildConfig.FEATURE_PYQEMU_BRICKS_ENABLED.

### 8. RunShellBrick

| Поле | Значение |
|---|---|
| Название (тексты лейаута) | `brick_run_shell`: RU «Выполнить команду» / EN «Execute command»<br>`brick_run_shell2`: RU «и сохранить вывод в» / EN «and save output to» |
| Параметры | BrickField: `CODE` |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/RunShellBrick.java:32` |
| Класс экшена | — (в `addActionToSequence` нет класса из `content/actions`) |
| Описание (BrickInfo.java) | RU: Запускает команду в терминале и сохраняет результат ВАЖНО: не ждет окончания, продолжает выполнять код дальше<br>EN: Runs a terminal command and saves the result IMPORTANT: does not wait for completion, continues executing code |


## Виртуальные машины / Virtual Machines

> Условие: только если BuildConfig.FEATURE_PYQEMU_BRICKS_ENABLED.

### 9. RunVMBrick

| Поле | Значение |
|---|---|
| Название (тексты лейаута) | `brick_run_vm`: RU «Запустить VM» / EN «Run VM»<br>`brick_vm_memory`: RU «память (МБ)» / EN «memory (MB)»<br>`brick_vm_cpu`: RU «ядра CPU» / EN «CPU cores»<br>`brick_vm_hda`: RU «диск (необяз.)» / EN «hda (optional)»<br>`brick_vm_cdrom`: RU «cdrom (необяз.)» / EN «cdrom (optional)» |
| Параметры | BrickField: `VM_MEMORY`, `VM_CPU`, `VM_HDA`, `VM_CDROM` |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/RunVMBrick.java:30` |
| Класс экшена | `catroid/src/main/java/org/catrobat/catroid/content/actions/RunVMAction.kt:7` (RunVMAction) |
| Описание (BrickInfo.java) | RU: Запускает виртуальную машину с помощью QEMU на архитектуре x86_64 (позволяет запускать Windows, Linux и др) ВАЖНО: обязательно выключайте VM, когда она не нужна<br>EN: Launches a virtual machine using QEMU on x86_64 architecture (allows running Windows, Linux, etc.). IMPORTANT: always shut down the VM when it is not needed. |


> Условие: только если BuildConfig.FEATURE_PYQEMU_BRICKS_ENABLED.

### 10. RunVm2Brick

| Поле | Значение |
|---|---|
| Название (тексты лейаута) | `brick_run_vm2`: RU «Запустить ВМ» / EN «Run VM»<br>`brick_vm_arguments`: RU «Аргументы» / EN «Arguments» |
| Параметры | BrickField: `VM_ARGUMENTS` |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/RunVm2Brick.java:30` |
| Класс экшена | `catroid/src/main/java/org/catrobat/catroid/content/actions/RunVm2Action.kt:9` (RunVm2Action) |
| Описание (BrickInfo.java) | RU: Запускает виртуальную машину с помощью QEMU на архитектуре x86_64, при помощи аргументов (позволяет запускать Windows, Linux и др) ВАЖНО: обязательно выключайте VM, когда она не нужна<br>EN: Launches a virtual machine using QEMU on x86_64 architecture with arguments (allows running Windows, Linux, etc.). IMPORTANT: always shut down the VM when it is not needed. |


> Условие: только если BuildConfig.FEATURE_PYQEMU_BRICKS_ENABLED.

### 11. StopVMBrick

| Поле | Значение |
|---|---|
| Название (тексты лейаута) | `brick_stop_vm`: RU «Остановить VM» / EN «Stop VM» |
| Параметры | — (в коде брика полей BrickField нет) |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/StopVMBrick.java:29` |
| Класс экшена | `catroid/src/main/java/org/catrobat/catroid/content/actions/StopVMAction.kt:6` (StopVMAction) |
| Описание (BrickInfo.java) | RU: Останавливает VM<br>EN: Stops the VM. |


> Условие: только если BuildConfig.FEATURE_PYQEMU_BRICKS_ENABLED.

### 12. CreateDiskBrick

| Поле | Значение |
|---|---|
| Название (тексты лейаута) | `brick_create_disk`: RU «Создать диск» / EN «Create disk»<br>`brick_disk_name`: RU «имя файла» / EN «file name»<br>`brick_disk_size`: RU «размер (напр. 10G)» / EN «size (e.g. 10G)» |
| Параметры | BrickField: `VM_DISK_NAME`, `VM_DISK_SIZE` |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/CreateDiskBrick.java:8` |
| Класс экшена | — (в `addActionToSequence` нет класса из `content/actions`) |
| Описание (BrickInfo.java) | RU: Создает виртуальный жесткий диск для VM<br>EN: Creates a virtual hard disk for the VM. |


> Условие: только если BuildConfig.FEATURE_PYQEMU_BRICKS_ENABLED.

### 13. ToggleDisplayBrick

| Поле | Значение |
|---|---|
| Название (тексты лейаута) | `brick_toggle_display`: RU «Показать монитор» / EN «Set display visible» |
| Параметры | BrickField: `VALUE` |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/ToggleDisplayBrick.java:7` |
| Класс экшена | `catroid/src/main/java/org/catrobat/catroid/content/actions/ToggleDisplayAction.kt:7` (ToggleDisplayAction) |
| Описание (BrickInfo.java) | RU: Определяет: показывать ли изображение VM<br>EN: Determines whether to show the VM display. |


> Условие: только если BuildConfig.FEATURE_PYQEMU_BRICKS_ENABLED.

### 14. VmSetMonitorSizeBrick

| Поле | Значение |
|---|---|
| Название (тексты лейаута) | `brick_vm_monitor_size`: RU «Задать размер монитора VM» / EN «Set VM monitor screen size»<br>`brick_vm_width`: RU «Ширина» / EN «Width»<br>`brick_vm_height`: RU «Высота» / EN «Height» |
| Параметры | BrickField: `WIDTH`, `HEIGHT` |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/VmSetMonitorSizeBrick.java:30` |
| Класс экшена | `catroid/src/main/java/org/catrobat/catroid/content/actions/VmSetMonitorSizeAction.kt:7` (VmSetMonitorSizeAction) |
| Описание (BrickInfo.java) | RU: Задает размер монитора VM (VNC)<br>EN: Sets the monitor size of the VM (VNC). |


> Условие: только если BuildConfig.FEATURE_PYQEMU_BRICKS_ENABLED.

### 15. MouseEventBrick

| Поле | Значение |
|---|---|
| Название (тексты лейаута) | `brick_mouse_event`: RU «Событие мыши» / EN «Mouse event»<br>`brick_mouse_x`: RU «Позиция X» / EN «X position»<br>`brick_mouse_y`: RU «Позиция Y» / EN «Y position»<br>`brick_mouse_state`: RU «маска (0:Отпущена, 1:Нажата)» / EN «mask (0:Up, 1:Down)» |
| Параметры | BrickField: `X_POSITION`, `Y_POSITION`, `VALUE` |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/MouseEventBrick.java:7` |
| Класс экшена | `catroid/src/main/java/org/catrobat/catroid/content/actions/MouseEventAction.kt:7` (MouseEventAction) |
| Описание (BrickInfo.java) | RU: Эмулирует событие мыши для VM (VNC). ВАЖНО: Маска - побитовая информация о кнопках: + 1: Левая кнопка мыши (Button 1 Pressed/Released). + 2: Средняя кнопка мыши (Button 2 Pressed/Released). + 4: Правая кнопка мыши (Button 3 Pressed/Released). + 8: Кнопка 4 (прокрутка вверх, если поддерживается). + 16: Кнопка 5 (прокрутка вниз, если поддерживается). + Например, нажатие ЛКМ будет 1, а нажатие одновременно ЛКМ и ПКМ будет: 1 + 4 = 5 (т.к. ЛКМ: 1, а ПКМ: 4)<br>EN: - |


> Условие: только если BuildConfig.FEATURE_PYQEMU_BRICKS_ENABLED.

### 16. VmRelativeMouseMoveBrick

| Поле | Значение |
|---|---|
| Название (тексты лейаута) | `brick_vm_mouse_relative`: RU «Событие мыши (аддитивное)» / EN «Mouse event (additive)»<br>`brick_vm_mouse_dx`: RU «Позиция X» / EN «Position X»<br>`brick_vm_mouse_dy`: RU «Позиция Y» / EN «Position Y»<br>`brick_mouse_state`: RU «маска (0:Отпущена, 1:Нажата)» / EN «mask (0:Up, 1:Down)» |
| Параметры | BrickField: `X_POSITION`, `Y_POSITION`, `MASK` |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/VmRelativeMouseMoveBrick.java:30` |
| Класс экшена | `catroid/src/main/java/org/catrobat/catroid/content/actions/VmRelativeMouseMoveAction.kt:7` (VmRelativeMouseMoveAction) |
| Описание (BrickInfo.java) | RU: Эмулирует событие мыши для VM (VNC), НО СМЕЩАЕТ МЫШЬ ОТНОСИТЕЛЬНО ТЕКУЩЕЙ ПОЗИЦИИ!. ВАЖНО: Маска - побитовая информация о кнопках: + 1: Левая кнопка мыши (Button 1 Pressed/Released). + 2: Средняя кнопка мыши (Button 2 Pressed/Released). + 4: Правая кнопка мыши (Button 3 Pressed/Released). + 8: Кнопка 4 (прокрутка вверх, если поддерживается). + 16: Кнопка 5 (прокрутка вниз, если поддерживается). + Например, нажатие ЛКМ будет 1, а нажатие одновременно ЛКМ и ПКМ будет: 1 + 4 = 5 (т.к. ЛКМ: 1, а ПКМ: 4)<br>EN: - |


> Условие: только если BuildConfig.FEATURE_PYQEMU_BRICKS_ENABLED.

### 17. KeyEventBrick

| Поле | Значение |
|---|---|
| Название (тексты лейаута) | `brick_key_event`: RU «Событие клавиатуры» / EN «Key event»<br>`brick_key_char`: RU «символ» / EN «character»<br>`brick_key_is_down`: RU «нажата (1/0)» / EN «is down (1/0)» |
| Параметры | BrickField: `VALUE`, `VM_KEY_DOWN` |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/KeyEventBrick.java:7` |
| Класс экшена | `catroid/src/main/java/org/catrobat/catroid/content/actions/KeyEventAction.kt:8` (KeyEventAction) |
| Описание (BrickInfo.java) | RU: Эмулирует событие клавиш для VM (VNC). ВАЖНО: специальные клавиши обозначаются так: <CTRL_L>, <CTRL_R>, <ALT_L>, <ALT_R>, <WIN_L>, <WIN_R>, <SHIFT_L>, <SHIFT_R>, <DEL>, <BACKSPACE>, <ENTER>, <ESC>, <TAB>, <UP>, <DOWN>, <LEFT>, <RIGHT>, <UP>, <DOWN><br>EN: Emulates a key event for the VM (VNC). IMPORTANT: special keys are denoted as follows: <CTRL_L>, <CTRL_R>, <ALT_L>, <ALT_R>, <WIN_L>, <WIN_R>, <SHIFT_L>, <SHIFT_R>, <DEL>, <BACKSPACE>, <ENTER>, <ESC>, <TAB>, <UP>, <DOWN>, <LEFT>, <RIGHT>, <UP>, <DOWN> |


> Условие: только если BuildConfig.FEATURE_PYQEMU_BRICKS_ENABLED.

### 18. SendVmInputBrick

| Поле | Значение |
|---|---|
| Название (тексты лейаута) | `brick_send_vm_input`: RU «Отправить ввод в ВМ» / EN «Send input to VM»<br>`input_text`: RU «Текст ввода» / EN «Input text» |
| Параметры | BrickField: `INPUT_TEXT` |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/SendVmInputBrick.java:30` |
| Класс экшена | `catroid/src/main/java/org/catrobat/catroid/content/actions/SendVmInputAction.kt:9` (SendVmInputAction) |
| Описание (BrickInfo.java) | RU: Отправляет клавиши как физическая клавиатура (может пригодиться для систем без дисплея) ВАЖНО: спец. символы: <CTRL+ANY> (ANY - любой символ, тобеж: <CTRL+C>, <CTRL+O>), <ESC>, <TAB>, <ENTER>, <BACKSPACE>, <UP>, <DOWN>, <RIGHT>, <LEFT><br>EN: Sends keystrokes as a physical keyboard (may be useful for headless systems). IMPORTANT: special characters: <CTRL+ANY> (ANY - any character, e.g.: <CTRL+C>, <CTRL+O>), <ESC>, <TAB>, <ENTER>, <BACKSPACE>, <UP>, <DOWN>, <RIGHT>, <LEFT> |


> Условие: только если BuildConfig.FEATURE_PYQEMU_BRICKS_ENABLED.

### 19. BindVmOutputBrick

| Поле | Значение |
|---|---|
| Название (тексты лейаута) | `brick_bind_vm_output`: RU «Привязать вывод ВМ к переменной» / EN «Bind VM output to variable» |
| Параметры | — (в коде брика полей BrickField нет) |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/BindVmOutputBrick.java:30` |
| Класс экшена | `catroid/src/main/java/org/catrobat/catroid/content/actions/BindVmOutputAction.kt:9` (BindVmOutputAction) |
| Описание (BrickInfo.java) | RU: Привязывает вывод консоли VM к переменной. ВАЖНО: сначала привяжите, и только потом запускайте VM, иначе она запустится без этого. Честно, я пока-что сам хз как этим пользоваться, но мне сказали, что это надо :/<br>EN: Binds VM console output to a variable. IMPORTANT: bind first, then start the VM, otherwise it will run without it. Honestly, I don't quite know how to use this myself yet, but I was told it's needed :/ |


### 20. RunChip8Brick

| Поле | Значение |
|---|---|
| Название (тексты лейаута) | `brick_run_chip8`: RU «Запустить CHIP-8 игру: %1$s» / EN «Run CHIP-8 ROM: %1$s» |
| Параметры | BrickField: `TEXT` |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/RunChip8Brick.java:7` |
| Класс экшена | `catroid/src/main/java/org/catrobat/catroid/content/actions/RunChip8Action.kt:12` (RunChip8Action) |
| Описание (BrickInfo.java) | RU: Запускает ROM эмулируя Chip-8. Позволяет запускать ретро-игры. После запуска создаст переменные. значения по умолчанию (чтоб все работало): chip8_speed = 10 - скорость процессора + chip8_x = -320 - позиция X окна с игрой + chip8_y = -160 - позиция Y окна с игрой + chip8_width = 640 - ширина окна с игрой + chip8_height = 320 - высота окна с игрой<br>EN: - |


## Пользовательские интерфейсы / Custom Views & UI

### 21. CreateWebUrlBrick

| Поле | Значение |
|---|---|
| Название (тексты лейаута) | `create_web_url`: RU «Создать WebView с названием» / EN «Create WebView with name»<br>`create_web_url2`: RU «URL» / EN «URL»<br>`create_web_url3`: RU «позиция (x, y)» / EN «position (x, y)»<br>`create_web_url4`: RU «размер (ширина, высота)» / EN «size (width, height)»<br>`create_web_z`: RU «Z» / EN «Z» |
| Параметры | BrickField: `NAME`, `URL`, `POSX`, `POSY`, `WIDTH`, `HEIGHT`, `Z_INDEX` |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/CreateWebUrlBrick.java:30` |
| Класс экшена | — (в `addActionToSequence` нет класса из `content/actions`) |
| Описание (BrickInfo.java) | RU: Создает WebView по ссылке<br>EN: Creates a WebView from a URL. |


### 22. CreateWebFileBrick

| Поле | Значение |
|---|---|
| Название (тексты лейаута) | `create_web_file`: RU «Создать WebView с названием» / EN «Create WebView with name»<br>`create_web_file2`: RU «HTML код» / EN «HTML code»<br>`create_web_file3`: RU «позиция (x, y)» / EN «position (x, y)»<br>`create_web_file4`: RU «размер (ширина, высота)» / EN «size (width, height)»<br>`create_web_z`: RU «Z» / EN «Z» |
| Параметры | BrickField: `NAME`, `HTML`, `POSX`, `POSY`, `WIDTH`, `HEIGHT`, `Z_INDEX` |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/CreateWebFileBrick.java:30` |
| Класс экшена | — (в `addActionToSequence` нет класса из `content/actions`) |
| Описание (BrickInfo.java) | RU: Создает WebView из HTML кода<br>EN: Creates a WebView from HTML code. |


### 23. SetWebBrick

| Поле | Значение |
|---|---|
| Название (тексты лейаута) | `brick_set_callback`: RU «Добавить к WebView» / EN «Add to WebView»<br>`brick_set_callback2`: RU «обработчик сообщений» / EN «callback to variable» |
| Параметры | BrickField: `NAME` |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/SetWebBrick.java:32` |
| Класс экшена | — (в `addActionToSequence` нет класса из `content/actions`) |
| Описание (BrickInfo.java) | RU: Задает callback к WebView, чтобы из WebView можно было отправить значение в переменную NeoCatroid, через Android.postMessage(\Значение\<br>EN: Sets a callback to the WebView, so a value can be sent from the WebView to a NeoCatroid variable via Android.postMessage(\Value\ |


### 24. EvalWebBrick

| Поле | Значение |
|---|---|
| Название (тексты лейаута) | `brick_eval_web`: RU «Выполнить JS» / EN «Run JS»<br>`brick_eval_web2`: RU «внутри WebView» / EN «in WebView» |
| Параметры | BrickField: `CODE`, `NAME` |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/EvalWebBrick.java:30` |
| Класс экшена | — (в `addActionToSequence` нет класса из `content/actions`) |
| Описание (BrickInfo.java) | RU: Выполняет JavaScript в контексте WebView<br>EN: Executes JavaScript in the context of a WebView. |


### 25. CreateVideoBrick

| Поле | Значение |
|---|---|
| Название (тексты лейаута) | `brick_video_player`: RU «Создать VideoPlayer» / EN «Create VideoPlayer»<br>`project_file`: RU «файл проекта» / EN «project file»<br>`position`: RU «позиция (x, y)» / EN «position (x, y)»<br>`size_wh`: RU «размер (ширина, высота)» / EN «size (width, height)»<br>`loop_enabled`: RU «зациклить» / EN «loop video»<br>`control_enabled`: RU «показать управление» / EN «show controls»<br>`video_layer`: RU «слой (0/1=под 3D и 2D, 2=над 3D под 2D, 3=поверх 2D, 4=поверх всего)» / EN «layer (0/1=below 3D+2D, 2=above 3D below 2D, 3=above 2D, 4=above all)»<br>`video_key_color`: RU «цвет хромакея (0xRRGGBB)» / EN «chroma key color (0xRRGGBB)» |
| Параметры | BrickField: `NAME`, `FILE`, `POSX`, `POSY`, `WIDTH`, `HEIGHT`, `LOOPED`, `CONTROLS`, `VIDEO_LAYER`, `VIDEO_KEY_COLOR`, `VIDEO_KEY_TOLERANCE` |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/CreateVideoBrick.java:30` |
| Класс экшена | — (в `addActionToSequence` нет класса из `content/actions`) |
| Описание (BrickInfo.java) | RU: Создает VideoPlayer, который позволяет просматривать видео из файлов проекта. Слой: 0/1 - под 3D и 2D, 2 - над 3D под 2D, 3 - поверх 2D, 4 - поверх всего. Хромакей: цвет вырезания + допуск (0 - выкл, сквозь вырезанное видно то, что под видео)<br>EN: Creates a VideoPlayer that allows playing video from project files. Layer: 0/1 - below 3D and 2D, 2 - above 3D below 2D, 3 - above 2D, 4 - above everything. Chroma key: key-out color + tolerance (0 - off, what is below the video shows through keyed areas). |


### 26. PlayVideoBrick

| Поле | Значение |
|---|---|
| Название (тексты лейаута) | `play_video`: RU «Запустить VideoPlayer» / EN «Start VideoPlayer» |
| Параметры | BrickField: `NAME` |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/PlayVideoBrick.java:30` |
| Класс экшена | — (в `addActionToSequence` нет класса из `content/actions`) |
| Описание (BrickInfo.java) | RU: Запускает воспроизведение видеоплеера с указанным именем.<br>EN: Starts playback of the VideoPlayer with the given name. |


### 27. PauseVideoBrick

| Поле | Значение |
|---|---|
| Название (тексты лейаута) | `pause_video`: RU «Остановить VideoPlayer» / EN «Pause VideoPlayer» |
| Параметры | BrickField: `NAME` |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/PauseVideoBrick.java:30` |
| Класс экшена | — (в `addActionToSequence` нет класса из `content/actions`) |
| Описание (BrickInfo.java) | RU: Ставит на паузу видеоплеер с указанным именем.<br>EN: Pauses the VideoPlayer with the given name. |


### 28. SeekVideoBrick

| Поле | Значение |
|---|---|
| Название (тексты лейаута) | `seek_video`: RU «Перемотать VideoPlayer» / EN «Seek VideoPlayer»<br>`seek_video2`: RU «на (секунды)» / EN «to (seconds)» |
| Параметры | BrickField: `NAME`, `TIME` |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/SeekVideoBrick.java:30` |
| Класс экшена | — (в `addActionToSequence` нет класса из `content/actions`) |
| Описание (BrickInfo.java) | RU: Перематывает видеоплеер с заданным именем к указанному времени (в секундах).<br>EN: Seeks the video player with the given name to the specified time (in seconds). |


### 29. CreateTextFieldBrick

| Поле | Значение |
|---|---|
| Название (тексты лейаута) | `brick_create_textfield`: RU «Создать TextField» / EN «Create TextField»<br>`default_text`: RU «текст по умолчанию» / EN «default text»<br>`type`: RU «тип (text, number)» / EN «type (text, number)»<br>`max_length`: RU «макс. длина» / EN «max length»<br>`is_password`: RU «является паролем» / EN «is password»<br>`position`: RU «позиция (x, y)» / EN «position (x, y)»<br>`size_wh`: RU «размер (ширина, высота)» / EN «size (width, height)»<br>`save_to_variable`: RU «сохранять в переменную» / EN «store to variable» |
| Параметры | BrickField: `NAME`, `TEXT`, `X_POSITION`, `Y_POSITION`, `WIDTH`, `HEIGHT`, `TEXTSIZE`, `TEXTCOLOR`, `BGCOLOR`, `HINT_TEXT`, `HINT_COLOR`, `ALIGNMENT`, `PASSWORD`, `FILE`, `CORNER`, `MAX_LEN`, `TYPE` |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/CreateTextFieldBrick.java:32` |
| Класс экшена | — (в `addActionToSequence` нет класса из `content/actions`) |
| Описание (BrickInfo.java) | RU: Создает TextField (текстовое поле), которое позволяет вводить текст<br>EN: Creates a TextField that allows entering text. |


### 30. AttachSOBrick

| Поле | Значение |
|---|---|
| Название (тексты лейаута) | `gl_attach_so`: RU «Подключить C++ к GL Сцене» / EN «Attach C++ to GL View»<br>`gl_name`: RU «имя» / EN «name»<br>`gl_so_name`: RU «имя .so файла» / EN «.so file name» |
| Параметры | BrickField: `GL_VIEW_NAME`, `FILE` |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/AttachSOBrick.java:7` |
| Класс экшена | `catroid/src/main/java/org/catrobat/catroid/content/actions/AttachSOAction.kt:7` (AttachSOAction) |
| Описание (BrickInfo.java) | RU: Подключает .so файл к GL сцене. ВАЖНО: файл должен соответствовать API и быть той же архитектуры, что и устройство.<br>EN: Attaches a .so file to the GL scene. IMPORTANT: the file must match the API and be of the same architecture as the device. |


### 31. CreateGLViewBrick

| Поле | Значение |
|---|---|
| Название (тексты лейаута) | `gl_create_view`: RU «Создать GL Сцену» / EN «Create GL View»<br>`gl_name`: RU «имя» / EN «name»<br>`gl_x`: RU «x» / EN «x»<br>`gl_y`: RU «y» / EN «y»<br>`gl_width`: RU «ширина» / EN «width»<br>`gl_height`: RU «высота» / EN «height» |
| Параметры | BrickField: `GL_VIEW_NAME`, `X_POSITION`, `Y_POSITION`, `WIDTH`, `HEIGHT` |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/CreateGLViewBrick.java:8` |
| Класс экшена | — (в `addActionToSequence` нет класса из `content/actions`) |
| Описание (BrickInfo.java) | RU: Создает GL сцену, позволяя создавать высокооптимизированные сцены<br>EN: Creates a GL scene, allowing highly optimized scenes. |


### 32. SetViewPositionBrick

| Поле | Значение |
|---|---|
| Название (тексты лейаута) | `brick_set_view_position`: RU «Задать позицию View» / EN «Set position of view»<br>`brick_x_label`: RU «X» / EN «X»<br>`brick_y_label`: RU «Y» / EN «Y» |
| Параметры | BrickField: `VIEW_ID`, `X_POSITION`, `Y_POSITION` |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/SetViewPositionBrick.java:7` |
| Класс экшена | `catroid/src/main/java/org/catrobat/catroid/content/actions/SetViewPositionAction.kt:8` (SetViewPositionAction) |
| Описание (BrickInfo.java) | RU: Задает позицию элемента интерфейса (View, например WebView или видео) по координатам X и Y.<br>EN: Sets the position of a UI view (e.g. WebView or video) at the given X and Y coordinates. |


### 33. DeleteWebBrick

| Поле | Значение |
|---|---|
| Название (тексты лейаута) | `delete_web`: RU «Удалить нативный View» / EN «Delete Native View» |
| Параметры | BrickField: `NAME` |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/DeleteWebBrick.java:30` |
| Класс экшена | `catroid/src/main/java/org/catrobat/catroid/content/actions/DeleteWebAction.kt:7` (DeleteWebAction) |
| Описание (BrickInfo.java) | RU: Удаляет любой View: WebView, VideoPlayer, TextField, GL сцену и др.<br>EN: Deletes any View: WebView, VideoPlayer, TextField, GL scene, etc. |


## Файлы и хранилище / Files & Storage

### 34. OpenFileBrick

| Поле | Значение |
|---|---|
| Название (тексты лейаута) | `brick_open_file`: RU «Открыть файл из Загрузок» / EN «Open file from Downloads» |
| Параметры | BrickField: `FILE` |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/OpenFileBrick.java:30` |
| Класс экшена | `catroid/src/main/java/org/catrobat/catroid/content/actions/OpenFileAction.kt:52` (OpenFileAction) |
| Описание (BrickInfo.java) | RU: Открывает файл из папки \Загрузки\<br>EN: Opens a file from the \Downloads\ folder |


### 35. ChooseFileBrick

| Поле | Значение |
|---|---|
| Название (тексты лейаута) | `brick_choose_file`: RU «Выбрать файл» / EN «Choose file»<br>`brick_choose_file2`: RU «сохранить название в переменную» / EN «save name to variable»<br>`brick_choose_file3`: RU «и копировать в файлы проекта» / EN «and copy to project files» |
| Параметры | спиннеры: `Spinner(см. код)` |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/ChooseFileBrick.kt:33` |
| Класс экшена | `catroid/src/main/java/org/catrobat/catroid/content/actions/ChooseFileAction.kt:18` (ChooseFileAction) |
| Описание (BrickInfo.java) | RU: Открывает меню выбора файла и сохраняет выбранный файл в файлы проекта.<br>EN: Opens a file picker menu and saves the selected file to the project files. |


### 36. MoveFilesBrick

| Поле | Значение |
|---|---|
| Название (тексты лейаута) | `brick_move_files`: RU «Копировать файл (проект -> Загрузки)» / EN «Copy file (project -> Downloads)» |
| Параметры | BrickField: `FILE` |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/MoveFilesBrick.java:30` |
| Класс экшена | `catroid/src/main/java/org/catrobat/catroid/content/actions/MoveFilesAction.kt:17` (MoveFilesAction) |
| Описание (BrickInfo.java) | RU: Копирует файл проекта в \Загрузки\<br>EN: Copies a project file to \Downloads\ |


### 37. MoveDownloadsBrick

| Поле | Значение |
|---|---|
| Название (тексты лейаута) | `brick_move_downloads`: RU «Копировать файл (Загрузки -> проект)» / EN «Copy file (Downloads -> project)» |
| Параметры | BrickField: `FILE` |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/MoveDownloadsBrick.java:30` |
| Класс экшена | `catroid/src/main/java/org/catrobat/catroid/content/actions/MoveDownloadsAction.kt:16` (MoveDownloadsAction) |
| Описание (BrickInfo.java) | RU: Копирует файл из \Загрузок\ в файлы проекта<br>EN: Copies a file from \Downloads\ into project files |


### 38. CopyProjectFileBrick

| Поле | Значение |
|---|---|
| Название (тексты лейаута) | `brick_copy_file_text`: RU «Копировать файл проекта» / EN «Copy project file»<br>`brick_copy_file_source`: RU «Исходный файл» / EN «Source file»<br>`brick_copy_file_destination`: RU «Новое имя» / EN «New name» |
| Параметры | BrickField: `NAME`, `TEXT` |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/CopyProjectFileBrick.java:30` |
| Класс экшена | `catroid/src/main/java/org/catrobat/catroid/content/actions/CopyProjectFileAction.kt:11` (CopyProjectFileAction) |
| Описание (BrickInfo.java) | RU: Копирует файл проекта с новым именем<br>EN: Copies a project file with a new name |


### 39. ExportProjectFileBrick

| Поле | Значение |
|---|---|
| Название (тексты лейаута) | `brick_export_file_text`: RU «Экспортировать файл» / EN «Export file to device»<br>`brick_export_file_source`: RU «Файл проекта» / EN «Project file» |
| Параметры | BrickField: `NAME` |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/ExportProjectFileBrick.java:30` |
| Класс экшена | `catroid/src/main/java/org/catrobat/catroid/content/actions/ExportProjectFileAction.kt:33` (ExportProjectFileAction) |
| Описание (BrickInfo.java) | RU: Открывает меню сохранения файла и сохраняет выбранный файл проекта туда, куда выбрал пользователь<br>EN: Opens a file save menu and saves the selected project file to the user-chosen location. |


### 40. ShareBrick

| Поле | Значение |
|---|---|
| Название (тексты лейаута) | `brick_share_title`: RU «Поделиться» / EN «Share» |
| Параметры | BrickField: `VALUE_1` |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/ShareBrick.java:7` |
| Класс экшена | `catroid/src/main/java/org/catrobat/catroid/content/actions/ShareAction.kt:11` (ShareAction) |
| Описание (BrickInfo.java) | RU: Открывает окно отправки. ВАЖНО: если указано имя файла проекта — отправит файл, иначе текст.<br>EN: Opens the share dialog. IMPORTANT: if a project file name is given, the file is shared, otherwise the text. |


### 41. SaveToInternalStorageBrick

| Поле | Значение |
|---|---|
| Название (тексты лейаута) | `brick_save_to_internal`: RU «Сохранить файл проекта во внутреннее хранилище» / EN «Save project file to internal storage»<br>`project_file_name`: RU «Имя файла в проекте» / EN «Project file name»<br>`internal_storage_path`: RU «Путь во внутр. хранилище» / EN «Internal storage path» |
| Параметры | BrickField: `PROJECT_FILE_NAME`, `FILE_NAME` |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/SaveToInternalStorageBrick.java:30` |
| Класс экшена | `catroid/src/main/java/org/catrobat/catroid/content/actions/SaveToInternalStorageAction.kt:10` (SaveToInternalStorageAction) |
| Описание (BrickInfo.java) | RU: Сохраняет файл проекта по пути внутрь хранилища приложения (data/data/...)<br>EN: Saves a project file to the application's internal storage (data/data/...). |


### 42. LoadFromInternalStorageBrick

| Поле | Значение |
|---|---|
| Название (тексты лейаута) | `brick_load_from_internal`: RU «Загрузить файл из внутр. хранилища» / EN «Load file from internal storage»<br>`internal_storage_path`: RU «Путь во внутр. хранилище» / EN «Internal storage path» |
| Параметры | BrickField: `FILE_NAME` |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/LoadFromInternalStorageBrick.java:30` |
| Класс экшена | `catroid/src/main/java/org/catrobat/catroid/content/actions/LoadFromInternalStorageAction.kt:10` (LoadFromInternalStorageAction) |
| Описание (BrickInfo.java) | RU: Копирует файл из хранилища приложения (data/data...) в файлы проекта<br>EN: Copies a file from the application's internal storage (data/data/...) to the project files. |


### 43. WriteVariableOnDeviceBrick

| Поле | Значение |
|---|---|
| Название (тексты лейаута) | `brick_write_variable_on_device`: RU «Сохранить переменную на
        устройство» / EN «Write variable on device» |
| Параметры | — (в коде брика полей BrickField нет) |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/WriteVariableOnDeviceBrick.java:28` |
| Класс экшена | — (в `addActionToSequence` нет класса из `content/actions`) |
| Описание (BrickInfo.java) | RU: Записывает переменную в специальный файл в проекте<br>EN: Writes a variable to a special file in the project |


### 44. SecureSaveVariableBrick

| Поле | Значение |
|---|---|
| Название (тексты лейаута) | `brick_secure_save_variable`: RU «Сохранить защищенную переменную» / EN «Write secure variable» |
| Параметры | — (в коде брика полей BrickField нет) |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/SecureSaveVariableBrick.java:17` |
| Класс экшена | `catroid/src/main/java/org/catrobat/catroid/content/actions/SecureSaveVariableAction.kt:21` (SecureSaveVariableAction) |
| Описание (BrickInfo.java) | RU: Сохраняет переменную в зашифрованное хранилище (AES-256, ключ привязан к устройству)<br>EN: Saves a variable to AES-256 encrypted storage (key derived from device ANDROID_ID, anti-cheat) |


### 45. ReadVariableFromDeviceBrick

| Поле | Значение |
|---|---|
| Название (тексты лейаута) | `brick_read_variable_from_device`: RU «Прочитать переменную с устройства» / EN «Read variable from device» |
| Параметры | — (в коде брика полей BrickField нет) |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/ReadVariableFromDeviceBrick.java:29` |
| Класс экшена | `catroid/src/main/java/org/catrobat/catroid/content/actions/ReadVariableFromDeviceAction.kt:33` (ReadVariableFromDeviceAction) |
| Описание (BrickInfo.java) | RU: Читает переменную из специального файла в проекте<br>EN: Reads a variable from a special file in the project |


### 46. SecureReadVariableBrick

| Поле | Значение |
|---|---|
| Название (тексты лейаута) | `brick_secure_read_variable`: RU «Прочитать защищенную переменную» / EN «Read secure variable» |
| Параметры | — (в коде брика полей BrickField нет) |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/SecureReadVariableBrick.java:17` |
| Класс экшена | `catroid/src/main/java/org/catrobat/catroid/content/actions/SecureReadVariableAction.kt:21` (SecureReadVariableAction) |
| Описание (BrickInfo.java) | RU: Читает зашифрованную переменную с устройства (AES-256, ключ привязан к устройству)<br>EN: Reads an AES-256 encrypted variable from device storage (key derived from device ANDROID_ID) |


### 47. WriteVariableToFileBrick

| Поле | Значение |
|---|---|
| Название (тексты лейаута) | `brick_write_variable_to_file_top`: RU «Сохранить переменную» / EN «Write variable»<br>`brick_write_variable_to_file_bottom`: RU «в файл» / EN «to file» |
| Параметры | BrickField: `WRITE_FILENAME` |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/WriteVariableToFileBrick.kt:31` |
| Класс экшена | `catroid/src/main/java/org/catrobat/catroid/content/actions/WriteVariableToFileAction.kt:32` (WriteVariableToFileAction) |
| Описание (BrickInfo.java) | RU: Записывает переменную в файл в \Загрузки\ пользователя<br>EN: Writes a variable to a file in the user's Downloads folder |


### 48. ReadVariableFromFileBrick

| Поле | Значение |
|---|---|
| Название (тексты лейаута) | `brick_read_variable_from_file_top`: RU «Прочитать переменную» / EN «Read variable»<br>`brick_read_variable_from_file_center`: RU «с файла» / EN «from file»<br>`brick_read_variable_from_file_bottom`: RU «и» / EN «and» |
| Параметры | BrickField: `READ_FILENAME`<br>спиннеры: `Spinner(см. код)` |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/ReadVariableFromFileBrick.kt:35` |
| Класс экшена | `catroid/src/main/java/org/catrobat/catroid/content/actions/ReadVariableFromFileAction.kt:54` (ReadVariableFromFileAction) |
| Описание (BrickInfo.java) | RU: Читает переменную из файла в \Загрузках\ пользователя<br>EN: Reads a variable from a file in the user's Downloads |


### 49. WriteListOnDeviceBrick

| Поле | Значение |
|---|---|
| Название (тексты лейаута) | `brick_write_list_on_device`: RU «Сохранить список на
        устройство» / EN «Write list on device» |
| Параметры | — (в коде брика полей BrickField нет) |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/WriteListOnDeviceBrick.java:29` |
| Класс экшена | — (в `addActionToSequence` нет класса из `content/actions`) |
| Описание (BrickInfo.java) | RU: Записывает список в специальный файл внутри проекта на устройстве.<br>EN: Writes the list to a special file inside the project on the device. |


### 50. ReadListFromDeviceBrick

| Поле | Значение |
|---|---|
| Название (тексты лейаута) | `brick_read_list_from_device`: RU «Прочитать список с устройства» / EN «Read list from device» |
| Параметры | — (в коде брика полей BrickField нет) |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/ReadListFromDeviceBrick.java:29` |
| Класс экшена | `catroid/src/main/java/org/catrobat/catroid/content/actions/ReadListFromDeviceAction.kt:33` (ReadListFromDeviceAction) |
| Описание (BrickInfo.java) | RU: Считывает список из специального файла в папке проекта на устройстве и записывает его в выбранный список.<br>EN: Reads a user list from a special file in the project directory on the device and writes it into the chosen list. |


## Оборудование и камера / Hardware & Camera

### 51. OrientationBrick

| Поле | Значение |
|---|---|
| Название (тексты лейаута) | `brick_orientation`: RU «Сменить ориентацию» / EN «Change orientation» |
| Параметры | — (в коде брика полей BrickField нет) |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/OrientationBrick.java:29` |
| Класс экшена | `catroid/src/main/java/org/catrobat/catroid/content/actions/OrientationAction.kt:42` (OrientationAction) |
| Описание (BrickInfo.java) | RU: Меняет ориентацию проекта ВАЖНО: блок тут вообще по приколу и работает плохо<br>EN: Changes the project orientation. IMPORTANT: this block is just for fun and works poorly. |


### 52. VibrationBrick

| Поле | Значение |
|---|---|
| Название (тексты лейаута) | `brick_vibration`: RU «Вибрировать» / EN «Vibrate for» |
| Параметры | BrickField: `VIBRATE_DURATION_IN_SECONDS` |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/VibrationBrick.java:32` |
| Класс экшена | `catroid/src/main/java/org/catrobat/catroid/content/actions/VibrateAction.kt:34` (VibrateAction) |
| Описание (BrickInfo.java) | RU: Заставляет устройство вибрировать<br>EN: Makes the device vibrate |


### 53. ScreenShotBrick

| Поле | Значение |
|---|---|
| Название (тексты лейаута) | `brick_screenshot`: RU «Сделать скриншот и использовать как образ» / EN «Take screenshot and store it as look» |
| Параметры | — (в коде брика полей BrickField нет) |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/ScreenShotBrick.java:29` |
| Класс экшена | `catroid/src/main/java/org/catrobat/catroid/content/actions/ScreenShotAction.kt:39` (ScreenShotAction) |
| Описание (BrickInfo.java) | RU: Делает скриншот сцены и использует его как образ<br>EN: Takes a screenshot of the scene and uses it as a look |


### 54. HideStatusBarBrick

| Поле | Значение |
|---|---|
| Название (тексты лейаута) | `hide_status_bar`: RU «Спрятать интерфейс системы» / EN «Hide system interface» |
| Параметры | — (в коде брика полей BrickField нет) |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/HideStatusBarBrick.java:30` |
| Класс экшена | — (в `addActionToSequence` нет класса из `content/actions`) |
| Описание (BrickInfo.java) | RU: Скрывает системный интерфейс (статус-бар) во время выполнения проекта.<br>EN: Hides the system UI (status bar) during playback. |


> Условие: только если проект не cast-проект.

### 55. CameraBrick

| Поле | Значение |
|---|---|
| Название (тексты лейаута) | — (в лейауте нет `@string/` или лейаут не найден) |
| Параметры | спиннеры: `Spinner(см. код)` |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/CameraBrick.kt:34` |
| Класс экшена | — (в `addActionToSequence` нет класса из `content/actions`) |
| Описание (BrickInfo.java) | RU: Включает или выключает камеру устройства (и отображает ее на заднем плане)<br>EN: Turns the device camera on or off (and displays it in the background) |


> Условие: только если проект не cast-проект.

### 56. ChooseCameraBrick

| Поле | Значение |
|---|---|
| Название (тексты лейаута) | `brick_choose_camera`: RU «Использовать камеру» / EN «Use camera» |
| Параметры | спиннеры: `Spinner(см. код)` |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/ChooseCameraBrick.kt:34` |
| Класс экшена | — (в `addActionToSequence` нет класса из `content/actions`) |
| Описание (BrickInfo.java) | RU: Определяет, какую камеру использовать: переднюю или заднюю<br>EN: Determines which camera to use: front or rear |


> Условие: только если проект не cast-проект.

### 57. FlashBrick

| Поле | Значение |
|---|---|
| Название (тексты лейаута) | `brick_flash`: RU «Включить фонарик» / EN «Turn flashlight» |
| Параметры | спиннеры: `Spinner(см. код)` |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/FlashBrick.java:36` |
| Класс экшена | `catroid/src/main/java/org/catrobat/catroid/content/actions/FlashAction.kt:27` (FlashAction) |
| Описание (BrickInfo.java) | RU: Включает или выключает фонарик<br>EN: Turns the flashlight on or off |


## Речь и взаимодействие / Speech & Interaction

### 58. ShowToastBrick

| Поле | Значение |
|---|---|
| Название (тексты лейаута) | `brick_show_toast`: RU «Показать тост» / EN «Show toast»<br>`brick_show_toast2`: RU «» / EN «» |
| Параметры | BrickField: `TOAST` |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/ShowToastBrick.java:30` |
| Класс экшена | `catroid/src/main/java/org/catrobat/catroid/content/actions/ShowToastAction.kt:34` (ShowToastAction) |
| Описание (BrickInfo.java) | RU: Показывает сообщение снизу<br>EN: Shows a message at the bottom |


### 59. CopyTextBrick

| Поле | Значение |
|---|---|
| Название (тексты лейаута) | `brick_copy_text`: RU «Скопировать» / EN «Copy»<br>`brick_copy_text2`: RU «в буфер обмена» / EN «to clipboard» |
| Параметры | BrickField: `TEXT` |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/CopyTextBrick.java:30` |
| Класс экшена | `catroid/src/main/java/org/catrobat/catroid/content/actions/CopyTextAction.kt:41` (CopyTextAction) |
| Описание (BrickInfo.java) | RU: Копирует текст в буфер обмена<br>EN: Copies text to the clipboard |


### 60. ListenMicroBrick

| Поле | Значение |
|---|---|
| Название (тексты лейаута) | `brick_listen_micro`: RU «Начать слушать микрофон в отрезках
        по» / EN «Start listen micro in times»<br>`brick_listen_micro2`: RU «милисекунд» / EN «ms» |
| Параметры | BrickField: `TIME` |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/ListenMicroBrick.java:30` |
| Класс экшена | `catroid/src/main/java/org/catrobat/catroid/content/actions/ListenMicroAction.kt:12` (ListenMicroAction) |
| Описание (BrickInfo.java) | RU: Начинает прослушивание микрофона в течение заданного времени (в миллисекундах). ВАЖНО: требуется доступ к микрофону.<br>EN: Starts listening to the microphone for the given amount of time (in milliseconds). IMPORTANT: requires microphone access. |


### 61. AskBrick

| Поле | Значение |
|---|---|
| Название (тексты лейаута) | `brick_ask_label`: RU «Спросить» / EN «Ask»<br>`brick_ask_background`: RU «Цвет фона (пусто = по умолчанию)» / EN «Background color (empty = default)»<br>`brick_ask_buttons`: RU «Цвет кнопок (пусто = по умолчанию)» / EN «Buttons color (empty = default)»<br>`brick_ask_store`: RU «и сохранить написанный ответ в» / EN «and store written answer in» |
| Параметры | BrickField: `ASK_QUESTION`, `COLOR`, `COLOR_CHANGE` |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/AskBrick.java:30` |
| Класс экшена | `catroid/src/main/java/org/catrobat/catroid/content/actions/AskAction.kt:33` (AskAction) |
| Описание (BrickInfo.java) | RU: Спрашивает вопрос и сохраняет введенное значение. Можно задать цвет фона и кнопок (пусто = по умолчанию)<br>EN: Asks a question and saves the entered value. Background and buttons color can be set (empty = default) |


> Условие: только если SettingsFragment.isAISpeechSynthetizationSharedPreferenceEnabled(context).

### 62. SpeakBrick

| Поле | Значение |
|---|---|
| Название (тексты лейаута) | `brick_speak`: RU «Сказать» / EN «Speak» |
| Параметры | BrickField: `SPEAK` |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/SpeakBrick.java:29` |
| Класс экшена | `catroid/src/main/java/org/catrobat/catroid/content/actions/SpeakAction.kt:31` (SpeakAction) |
| Описание (BrickInfo.java) | RU: Синтезирует речь и произносит текст<br>EN: Synthesizes speech and speaks the text |


> Условие: только если SettingsFragment.isAISpeechSynthetizationSharedPreferenceEnabled(context).

### 63. SpeakAndWaitBrick

| Поле | Значение |
|---|---|
| Название (тексты лейаута) | `brick_speak`: RU «Сказать» / EN «Speak»<br>`brick_speak_and_wait`: RU «и ждать» / EN «and wait» |
| Параметры | BrickField: `SPEAK` |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/SpeakAndWaitBrick.java:30` |
| Класс экшена | `catroid/src/main/java/org/catrobat/catroid/content/actions/SpeakAndWaitAction.kt:36` (SpeakAndWaitAction) |
| Описание (BrickInfo.java) | RU: Синтезирует речь, произносит текст и ждет окончания<br>EN: Synthesizes speech, speaks the text and waits for completion |


> Условие: только если SettingsFragment.isAISpeechRecognitionSharedPreferenceEnabled(context).

### 64. AskSpeechBrick

| Поле | Значение |
|---|---|
| Название (тексты лейаута) | `brick_ask_speech_label`: RU «Спросить» / EN «Ask»<br>`brick_ask_speech_store`: RU «и сохранить озвученый ответ в» / EN «and store spoken answer in» |
| Параметры | BrickField: `ASK_SPEECH_QUESTION` |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/AskSpeechBrick.java:31` |
| Класс экшена | `catroid/src/main/java/org/catrobat/catroid/content/actions/AskSpeechAction.kt:37` (AskSpeechAction) |
| Описание (BrickInfo.java) | RU: Включает распознование речи, чтобы спросить вопрос у пользователя<br>EN: Enables speech recognition to ask the user a question |


> Условие: только если SettingsFragment.isAISpeechRecognitionSharedPreferenceEnabled(context).

### 65. StartListeningBrick

| Поле | Значение |
|---|---|
| Название (тексты лейаута) | `brick_start_listening_to_voice`: RU «Начать слушать голос и хранить сказанные слова в» / EN «Start listening to voice and store spoken words in» |
| Параметры | — (в коде брика полей BrickField нет) |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/StartListeningBrick.kt:30` |
| Класс экшена | `catroid/src/main/java/org/catrobat/catroid/content/actions/StartListeningAction.kt:31` (StartListeningAction) |
| Описание (BrickInfo.java) | RU: Начинает прослушивание микрофона в отрезках по выбранному времени и сохраняет громкость в формулу<br>EN: Starts listening to the microphone in intervals at the chosen time and saves the volume into a formula |


## Эмуляция и сеть / Emulation & Network

### 66. TapAtBrick

| Поле | Значение |
|---|---|
| Название (тексты лейаута) | `brick_tap_at`: RU «"Нажать один раз в"» / EN «"Single tap at"»<br>`x_label`: RU «"X: "» / EN «"x:"»<br>`y_label`: RU «"Y: "» / EN «"y:"» |
| Параметры | BrickField: `X_POSITION`, `Y_POSITION` |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/TapAtBrick.java:29` |
| Класс экшена | `catroid/src/main/java/org/catrobat/catroid/content/actions/TapAtAction.kt:33` (TapAtAction) |
| Описание (BrickInfo.java) | RU: Имитирует одиночное нажатие (тап) в указанных координатах экрана. Можно использовать для автокликов.<br>EN: Simulates a single tap/click at the given screen coordinates. Can be used for auto-clicking. |


### 67. TapForBrick

| Поле | Значение |
|---|---|
| Название (тексты лейаута) | `brick_touch_at`: RU «"Нажать в"» / EN «"Touch at"»<br>`x_label`: RU «"X: "» / EN «"x:"»<br>`y_label`: RU «"Y: "» / EN «"y:"»<br>`brick_tap_for`: RU «"в тeчении"» / EN «"for"» |
| Параметры | BrickField: `X_POSITION`, `Y_POSITION`, `DURATION_IN_SECONDS` |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/TapForBrick.kt:32` |
| Класс экшена | — (в `addActionToSequence` нет класса из `content/actions`) |
| Описание (BrickInfo.java) | — (в BrickInfo.java описания нет) |


### 68. TouchAndSlideBrick

| Поле | Значение |
|---|---|
| Название (тексты лейаута) | `brick_touch_slide`: RU «"Коснуться и перелистывать"» / EN «"Touch and slide"»<br>`brick_touch_from_x`: RU «"от X:"» / EN «"from x:"»<br>`y_label`: RU «"Y: "» / EN «"y:"»<br>`brick_glide_to_x`: RU «по X:» / EN «to x:»<br>`brick_loop_in`: RU «в» / EN «in» |
| Параметры | BrickField: `X_POSITION`, `Y_POSITION`, `X_POSITION_CHANGE`, `Y_POSITION_CHANGE`, `DURATION_IN_SECONDS` |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/TouchAndSlideBrick.kt:32` |
| Класс экшена | — (в `addActionToSequence` нет класса из `content/actions`) |
| Описание (BrickInfo.java) | — (в BrickInfo.java описания нет) |


### 69. WebRequestBrick

| Поле | Значение |
|---|---|
| Название (тексты лейаута) | `brick_web_request`: RU «Отправить GET веб-запрос» / EN «Send GET web request to»<br>`brick_web_request_result`: RU «и сохранить ответ в» / EN «and store answer in» |
| Параметры | BrickField: `WEB_REQUEST` |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/WebRequestBrick.kt:31` |
| Класс экшена | `catroid/src/main/java/org/catrobat/catroid/content/actions/WebRequestAction.kt:29` (WebRequestAction) |
| Описание (BrickInfo.java) | RU: Отправляет GET запрос на сайт и получает результат<br>EN: Sends a GET request to a website and receives the result |


### 70. OpenUrlBrick

| Поле | Значение |
|---|---|
| Название (тексты лейаута) | `brick_open`: RU «Открыть» / EN «Open»<br>`brick_in_browser`: RU «в браузере» / EN «in browser» |
| Параметры | BrickField: `OPEN_URL` |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/OpenUrlBrick.kt:32` |
| Класс экшена | `catroid/src/main/java/org/catrobat/catroid/content/actions/OpenUrlAction.kt:32` (OpenUrlAction) |
| Описание (BrickInfo.java) | — (в BrickInfo.java описания нет) |


### 71. OpenAppBrick

| Поле | Значение |
|---|---|
| Название (тексты лейаута) | `brick_open_app_title`: RU «Открыть приложение» / EN «Open app»<br>`brick_open_app_pkg`: RU «пакет» / EN «package» |
| Параметры | BrickField: `VALUE_1` |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/OpenAppBrick.java:7` |
| Класс экшена | `catroid/src/main/java/org/catrobat/catroid/content/actions/OpenAppAction.kt:10` (OpenAppAction) |
| Описание (BrickInfo.java) | RU: Открывает установленное приложение по имени пакета.<br>EN: Opens an installed app by its package name. |


> Условие: ветка when: объект НЕ фоновый (!isBackgroundSprite); иначе — BackgroundRequestBrick.

### 72. LookRequestBrick

| Поле | Значение |
|---|---|
| Название (тексты лейаута) | `brick_look_request`: RU «Получить изображение из» / EN «Get image from»<br>`brick_look_request_result`: RU «и использовать как текущий образ» / EN «and use as current look» |
| Параметры | BrickField: `LOOK_REQUEST` |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/LookRequestBrick.kt:31` |
| Класс экшена | `catroid/src/main/java/org/catrobat/catroid/content/actions/LookRequestAction.kt:37` (LookRequestAction) |
| Описание (BrickInfo.java) | RU: Получает изображение по ссылке и пытается использовать как образ<br>EN: Gets an image from a URL and tries to use it as a look |


> Условие: ветка when: объект фоновый.

### 73. BackgroundRequestBrick

| Поле | Значение |
|---|---|
| Название (тексты лейаута) | `brick_look_request`: RU «Получить изображение из» / EN «Get image from»<br>`brick_background_request_result`: RU «и использовать как текущий фон» / EN «and use as current background» |
| Параметры | BrickField: `BACKGROUND_REQUEST` |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/BackgroundRequestBrick.kt:32` |
| Класс экшена | `catroid/src/main/java/org/catrobat/catroid/content/actions/LookRequestAction.kt:37` (LookRequestAction) |
| Описание (BrickInfo.java) | — (в BrickInfo.java описания нет) |


## Разное / Miscellaneous

### 74. EnableBackgroundBrick

| Поле | Значение |
|---|---|
| Название (тексты лейаута) | `brick_enable_background`: RU «Включить фоновую работу» / EN «Enable background work»<br>`brick_prepare_notification_id`: RU «ID» / EN «ID»<br>`brick_prepare_notification_channel`: RU «Канал» / EN «Channel»<br>`brick_prepare_notification_title`: RU «Заголовок» / EN «Title»<br>`brick_prepare_notification_text`: RU «Текст» / EN «Text»<br>`brick_prepare_notification_icon`: RU «Иконка» / EN «Icon»<br>`brick_prepare_notification_importance`: RU «Важность» / EN «Importance» |
| Параметры | BrickField: `NOTIFICATION_ID`, `NOTIFICATION_CHANNEL`, `NOTIFICATION_TITLE`, `NOTIFICATION_TEXT`, `NOTIFICATION_ICON`<br>спиннеры: `Spinner(см. код)` |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/EnableBackgroundBrick.java:17` |
| Класс экшена | `catroid/src/main/java/org/catrobat/catroid/content/actions/EnableBackgroundAction.kt:16` (EnableBackgroundAction) |
| Описание (BrickInfo.java) | RU: Запускает фоновую службу (foreground service) с постоянным уведомлением, позволяя приложению работать в фоне. ВАЖНО: на Android 13+ требуется разрешение на отправку уведомлений.<br>EN: Starts a foreground service with a persistent notification, allowing the app to run in the background. IMPORTANT: on Android 13+ the notifications permission is required. |


### 75. StopBackgroundBrick

| Поле | Значение |
|---|---|
| Название (тексты лейаута) | `brick_stop_background`: RU «Выключить фоновую работу» / EN «Stop background work» |
| Параметры | — (в коде брика полей BrickField нет) |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/StopBackgroundBrick.java:6` |
| Класс экшена | `catroid/src/main/java/org/catrobat/catroid/content/actions/StopBackgroundAction.kt:9` (StopBackgroundAction) |
| Описание (BrickInfo.java) | RU: Останавливает фоновую службу (Foreground Service), запущенную в приложении.<br>EN: Stops the background (foreground) service running in the app. |


### 76. ResetTimerBrick

| Поле | Значение |
|---|---|
| Название (тексты лейаута) | `brick_reset_timer`: RU «Сбросить таймер» / EN «Reset timer» |
| Параметры | — (в коде брика полей BrickField нет) |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/ResetTimerBrick.java:28` |
| Класс экшена | `catroid/src/main/java/org/catrobat/catroid/content/actions/ResetTimerAction.java:30` (ResetTimerAction) |
| Описание (BrickInfo.java) | RU: Сбрасывает таймер: сенсор \таймер\ начинает отсчет заново с нуля.<br>EN: Resets the timer: the \timer\ sensor starts counting from zero again. |


### 77. TestBrick

| Поле | Значение |
|---|---|
| Название (тексты лейаута) | `brick_test`: RU «Показать интерфейс системы» / EN «Show system interface» |
| Параметры | — (в коде брика полей BrickField нет) |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/TestBrick.java:28` |
| Класс экшена | `catroid/src/main/java/org/catrobat/catroid/content/actions/TestAction.kt:11` (TestAction) |
| Описание (BrickInfo.java) | RU: Служебный блок (не показывается в списке блоков): показывает системный интерфейс (статус-бар и панель навигации).<br>EN: Internal helper (not shown in the block list): shows the system interface (status bar and navigation bar). |


### 78. ApplyShaderToImageBrick

| Поле | Значение |
|---|---|
| Название (тексты лейаута) | `brick_apply_shader_to_image`: RU «?» / EN «?»<br>`brick_apply_shader_image_file`: RU «Файл проекта» / EN «Project file» |
| Параметры | BrickField: `NAME`, `TEXT`, `X_POSITION` |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/ApplyShaderToImageBrick.java:7` |
| Класс экшена | `catroid/src/main/java/org/catrobat/catroid/content/actions/ApplyShaderToImageAction.kt:7` (ApplyShaderToImageAction) |
| Описание (BrickInfo.java) | RU: Применяет шейдер к файлу проекта (изображению). Используйте это для генерации чего-либо или обработки изображений<br>EN: Applies a shader to a project file (image). Use this to generate something or process images |


### 79. WhenBrick

| Поле | Значение |
|---|---|
| Название (тексты лейаута) | `brick_when`: RU «При нажатии» / EN «When tapped» |
| Параметры | — (в коде брика полей BrickField нет) |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/WhenBrick.java:32` |
| Класс экшена | — (в `addActionToSequence` нет класса из `content/actions`) |
| Описание (BrickInfo.java) | RU: Вызывается при нажатии пальца на спрайт.<br>EN: Called when a finger taps on the sprite. |


### 80. WhenTouchDownBrick

| Поле | Значение |
|---|---|
| Название (тексты лейаута) | `brick_when_touched`: RU «При нажатии на экран» / EN «When stage is tapped» |
| Параметры | — (в коде брика полей BrickField нет) |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/WhenTouchDownBrick.java:30` |
| Класс экшена | — (в `addActionToSequence` нет класса из `content/actions`) |
| Описание (BrickInfo.java) | RU: Вызывается, когда палец касается экрана.<br>EN: Called when a finger touches the screen. |


> Условие: только если SettingsFragment.isNfcSharedPreferenceEnabled(context).

### 81. WhenNfcBrick

| Поле | Значение |
|---|---|
| Название (тексты лейаута) | `brick_when_nfc`: RU «Когда NFC метка обнаружена» / EN «When NFC» |
| Параметры | спиннеры: `NfcTagData` |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/WhenNfcBrick.java:47` |
| Класс экшена | — (в `addActionToSequence` нет класса из `content/actions`) |
| Описание (BrickInfo.java) | RU: Вызывается, когда обнаружен NFC-тег (можно выбрать конкретный тег или все теги).<br>EN: Triggered when an NFC tag is detected (you can select a specific tag or all tags). |


> Условие: только если SettingsFragment.isNfcSharedPreferenceEnabled(context).

### 82. SetNfcTagBrick

| Поле | Значение |
|---|---|
| Название (тексты лейаута) | `brick_set_nfc_tag`: RU «Записать на NFC метку» / EN «Wait for next NFC tag to write»<br>`ndef_record_label`: RU «тип записи» / EN «as record type» |
| Параметры | BrickField: `NFC_NDEF_MESSAGE`<br>спиннеры: `NfcTypeOption` |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/SetNfcTagBrick.java:40` |
| Класс экшена | `catroid/src/main/java/org/catrobat/catroid/content/actions/SetNfcTagAction.java:34` (SetNfcTagAction) |
| Описание (BrickInfo.java) | RU: Ожидает следующий NFC-тег и записывает в него сообщение (NDEF) выбранного типа. ВАЖНО: требуется NFC-адаптер устройства.<br>EN: Waits for the next NFC tag and writes an NDEF message of the selected record type to it. IMPORTANT: requires an NFC adapter on the device. |


### 83. TimerStartBrick

| Поле | Значение |
|---|---|
| Название (тексты лейаута) | `brick_timer_start`: RU «запустить таймер» / EN «start timer» |
| Параметры | — (в коде брика полей BrickField нет) |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/TimerStartBrick.java:28` |
| Класс экшена | `catroid/src/main/java/org/catrobat/catroid/content/actions/TimerStartAction.java:30` (TimerStartAction) |
| Описание (BrickInfo.java) | RU: Запускает или перезапускает таймер<br>EN: Starts or restarts the timer |


### 84. TimerStopBrick

| Поле | Значение |
|---|---|
| Название (тексты лейаута) | `brick_timer_stop`: RU «остановить таймер» / EN «stop timer» |
| Параметры | — (в коде брика полей BrickField нет) |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/TimerStopBrick.java:28` |
| Класс экшена | `catroid/src/main/java/org/catrobat/catroid/content/actions/TimerStopAction.java:30` (TimerStopAction) |
| Описание (BrickInfo.java) | RU: Останавливает таймер<br>EN: Stops the timer |


### 85. TimerResetBrick

| Поле | Значение |
|---|---|
| Название (тексты лейаута) | `brick_timer_reset`: RU «сбросить таймер» / EN «reset timer» |
| Параметры | — (в коде брика полей BrickField нет) |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/TimerResetBrick.java:28` |
| Класс экшена | `catroid/src/main/java/org/catrobat/catroid/content/actions/TimerResetAction.java:30` (TimerResetAction) |
| Описание (BrickInfo.java) | RU: Сбрасывает таймер<br>EN: Resets the timer |


### 86. ScheduleBrick

| Поле | Значение |
|---|---|
| Название (тексты лейаута) | `brick_schedule`: RU «запланировать через» / EN «schedule after»<br>`brick_schedule_script`: RU «скрипт» / EN «script» |
| Параметры | BrickField: `TIME_TO_WAIT_IN_SECONDS` |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/ScheduleBrick.java:38` |
| Класс экшена | `catroid/src/main/java/org/catrobat/catroid/content/actions/ScriptSequenceAction.java:33` (ScriptSequenceAction), `catroid/src/main/java/org/catrobat/catroid/content/actions/ScheduleAction.java:33` (ScheduleAction) |
| Описание (BrickInfo.java) | RU: Планирует выполнение блоков через определенное количество секунд<br>EN: Schedules execution of blocks after a specified number of seconds |


### 87. VibratePatternBrick

| Поле | Значение |
|---|---|
| Название (тексты лейаута) | `brick_vibrate_pattern`: RU «вибрация паттерн» / EN «vibrate pattern» |
| Параметры | BrickField: `TEXT` |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/VibratePatternBrick.java:29` |
| Класс экшена | `catroid/src/main/java/org/catrobat/catroid/content/actions/VibratePatternAction.java:38` (VibratePatternAction) |
| Описание (BrickInfo.java) | RU: Вызывает вибрацию устройства с указанным паттерном<br>EN: Vibrates the device with the specified pattern |


### 88. KeepScreenOnBrick

| Поле | Значение |
|---|---|
| Название (тексты лейаута) | `brick_keep_screen_on`: RU «не выключать экран» / EN «keep screen on» |
| Параметры | — (в коде брика полей BrickField нет) |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/KeepScreenOnBrick.java:28` |
| Класс экшена | `catroid/src/main/java/org/catrobat/catroid/content/actions/KeepScreenOnAction.kt:6` (KeepScreenOnAction) |
| Описание (BrickInfo.java) | RU: Не дает экрану гаснуть<br>EN: Prevents the screen from turning off |


### 89. KeepScreenOffBrick

| Поле | Значение |
|---|---|
| Название (тексты лейаута) | `brick_keep_screen_off`: RU «разрешить выключение экрана» / EN «keep screen off» |
| Параметры | — (в коде брика полей BrickField нет) |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/KeepScreenOffBrick.java:28` |
| Класс экшена | `catroid/src/main/java/org/catrobat/catroid/content/actions/KeepScreenOffAction.kt:6` (KeepScreenOffAction) |
| Описание (BrickInfo.java) | RU: Разрешает экрану гаснуть (обычный режим)<br>EN: Allows the screen to turn off (normal mode) |


### 90. ScreenBrightnessBrick

| Поле | Значение |
|---|---|
| Название (тексты лейаута) | `brick_screen_brightness`: RU «яркость экрана» / EN «screen brightness» |
| Параметры | BrickField: `BRIGHTNESS` |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/ScreenBrightnessBrick.java:29` |
| Класс экшена | `catroid/src/main/java/org/catrobat/catroid/content/actions/ScreenBrightnessAction.kt:10` (ScreenBrightnessAction) |
| Описание (BrickInfo.java) | RU: Задает яркость экрана устройства<br>EN: Sets the device screen brightness |


### 91. RunOnUiThreadBrick

| Поле | Значение |
|---|---|
| Название (тексты лейаута) | `brick_run_on_ui_thread`: RU «выполнить на UI потоке» / EN «run on UI thread» |
| Параметры | — (в коде брика полей BrickField нет) |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/RunOnUiThreadBrick.java:34` |
| Класс экшена | `catroid/src/main/java/org/catrobat/catroid/content/actions/ScriptSequenceAction.java:33` (ScriptSequenceAction), `catroid/src/main/java/org/catrobat/catroid/content/actions/RunOnUiThreadAction.kt:6` (RunOnUiThreadAction) |
| Описание (BrickInfo.java) | RU: Выполняет блоки внутри на главном потоке (UI потоке)<br>EN: Executes blocks on the main thread (UI thread) |


### 92. ShowNotificationBrick

| Поле | Значение |
|---|---|
| Название (тексты лейаута) | `brick_show_notification_title`: RU «Показать уведомление» / EN «Show notification»<br>`brick_show_notification_id`: RU «ID» / EN «ID»<br>`brick_show_notification_channel`: RU «канал» / EN «channel»<br>`brick_show_notification_header`: RU «заголовок» / EN «header»<br>`brick_show_notification_text`: RU «текст» / EN «text»<br>`brick_show_notification_large_icon`: RU «большая иконка» / EN «large icon»<br>`brick_show_notification_importance`: RU «важность» / EN «importance»<br>`brick_show_notification_ongoing`: RU «постоянное» / EN «ongoing» |
| Параметры | BrickField: `VALUE_1`, `VALUE_2`, `VALUE_3`, `VALUE_4`, `VALUE_5`<br>спиннеры: `Spinner(см. код)` |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/ShowNotificationBrick.java:13` |
| Класс экшена | `catroid/src/main/java/org/catrobat/catroid/content/actions/ShowNotificationAction.kt:18` (ShowNotificationAction) |
| Описание (BrickInfo.java) | RU: Создает и сразу показывает уведомление с ID, каналом, заголовком, текстом и иконкой. ВАЖНО: при нажатии на уведомление открывается сцена проекта.<br>EN: Creates and immediately shows a notification with ID, channel, title, text and icon. IMPORTANT: tapping the notification opens the project stage. |


### 93. EnableBackgroundModeBrick

| Поле | Значение |
|---|---|
| Название (тексты лейаута) | `brick_system_enable_background_title`: RU «Включить фон» / EN «Enable background»<br>`brick_system_enable_background_header`: RU «заголовок» / EN «header»<br>`brick_system_enable_background_text`: RU «текст» / EN «text» |
| Параметры | BrickField: `VALUE_1`, `VALUE_2` |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/EnableBackgroundModeBrick.java:7` |
| Класс экшена | `catroid/src/main/java/org/catrobat/catroid/content/actions/EnableBackgroundModeAction.kt:12` (EnableBackgroundModeAction) |
| Описание (BrickInfo.java) | RU: Включает фоновый режим: запускает фоновую службу с уведомлением, содержащим заданные заголовок и текст. ВАЖНО: на Android 13+ требуется разрешение на отправку уведомлений.<br>EN: Enables the background mode: starts a foreground service with a notification containing the given title and text. IMPORTANT: on Android 13+ the notifications permission is required. |


### 94. DisableBackgroundModeBrick

| Поле | Значение |
|---|---|
| Название (тексты лейаута) | `brick_system_disable_background`: RU «Отключить фон» / EN «Disable background» |
| Параметры | — (в коде брика полей BrickField нет) |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/DisableBackgroundModeBrick.java:6` |
| Класс экшена | `catroid/src/main/java/org/catrobat/catroid/content/actions/DisableBackgroundModeAction.kt:9` (DisableBackgroundModeAction) |
| Описание (BrickInfo.java) | RU: Отключает фоновый режим и останавливает фоновую службу.<br>EN: Disables the background mode and stops the background service. |


### 95. SetNativeParentBrick

| Поле | Значение |
|---|---|
| Название (тексты лейаута) | `brick_set_native_parent_title`: RU «Установить родителя вида» / EN «Set view parent»<br>`brick_set_native_parent_id`: RU «ID» / EN «ID»<br>`brick_set_native_parent_target`: RU «родитель» / EN «parent» |
| Параметры | BrickField: `VALUE_1`, `VALUE_2` |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/SetNativeParentBrick.java:7` |
| Класс экшена | `catroid/src/main/java/org/catrobat/catroid/content/actions/SetNativeParentAction.kt:9` (SetNativeParentAction) |
| Описание (BrickInfo.java) | RU: Помещает нативный элемент внутрь другого элемента-контейнера. ВАЖНО: пустой ID родителя возвращает элемент на сцену.<br>EN: Places a native view inside another container view. IMPORTANT: an empty parent ID moves the view back to the stage. |


### 96. CreateSliderBrick

| Поле | Значение |
|---|---|
| Название (тексты лейаута) | `brick_create_slider_title`: RU «Создать слайдер» / EN «Create slider»<br>`brick_create_slider_id`: RU «ID» / EN «ID»<br>`brick_create_slider_min`: RU «мин» / EN «min»<br>`brick_create_slider_max`: RU «макс» / EN «max»<br>`brick_create_slider_val`: RU «значение» / EN «value»<br>`brick_coord_x_label`: RU «X:» / EN «X:»<br>`brick_coord_y_label`: RU «Y:» / EN «Y:»<br>`brick_coord_w_label`: RU «W:» / EN «W:» |
| Параметры | BrickField: `VALUE_1`, `VALUE_2`, `VALUE_3`, `VALUE_4`, `X_POSITION`, `Y_POSITION`, `WIDTH`, `HEIGHT` |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/CreateSliderBrick.java:7` |
| Класс экшена | `catroid/src/main/java/org/catrobat/catroid/content/actions/CreateSliderAction.kt:11` (CreateSliderAction) |
| Описание (BrickInfo.java) | RU: Создает на сцене ползунок (слайдер) с заданным ID и диапазоном значений.<br>EN: Creates a slider (SeekBar) on the stage with the given ID and value range. |


### 97. NativeViewConfigBrick

| Поле | Значение |
|---|---|
| Название (тексты лейаута) | `brick_native_view_config_title`: RU «Настроить вид» / EN «Configure view»<br>`brick_native_view_config_id`: RU «ID» / EN «ID»<br>`brick_native_view_config_prop`: RU «свойство» / EN «property»<br>`brick_native_view_config_val`: RU «значение» / EN «value» |
| Параметры | BrickField: `VALUE_1`, `VALUE_2`<br>спиннеры: `Spinner(см. код)` |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/NativeViewConfigBrick.java:12` |
| Класс экшена | `catroid/src/main/java/org/catrobat/catroid/content/actions/NativeViewConfigAction.kt:92` (NativeViewConfigAction) |
| Описание (BrickInfo.java) | RU: Настраивает свойство нативного элемента: видимость, прозрачность, поворот, масштаб, текст, настройки WebView и другое.<br>EN: Configures a native view property: visibility, alpha, rotation, scale, text, WebView settings and more. |


### 98. NativeViewControlBrick

| Поле | Значение |
|---|---|
| Название (тексты лейаута) | `brick_native_view_control_title`: RU «Управлять видом» / EN «Control view»<br>`brick_native_view_control_id`: RU «ID» / EN «ID»<br>`brick_native_view_control_cmd`: RU «команда» / EN «command»<br>`brick_native_view_control_param`: RU «параметр» / EN «param» |
| Параметры | BrickField: `VALUE_1`, `VALUE_2`<br>спиннеры: `Spinner(см. код)` |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/NativeViewControlBrick.java:12` |
| Класс экшена | `catroid/src/main/java/org/catrobat/catroid/content/actions/NativeViewControlAction.kt:17` (NativeViewControlAction) |
| Описание (BrickInfo.java) | RU: Выполняет команду над нативным элементом: навигация в WebView, воспроизведение видео, изменение текста, прокрутка, клавиатура.<br>EN: Runs a command on a native view: WebView navigation, video playback, text editing, scrolling, keyboard. |


### 99. NativeViewListenerBrick

| Поле | Значение |
|---|---|
| Название (тексты лейаута) | `brick_native_view_listener_title`: RU «Событие вида» / EN «On view event»<br>`brick_native_view_listener_id`: RU «ID» / EN «ID»<br>`brick_native_view_listener_evt`: RU «событие» / EN «event»<br>`brick_native_view_listener_var`: RU «переменная» / EN «variable» |
| Параметры | BrickField: `VALUE_1`<br>спиннеры: `Spinner(см. код)` |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/NativeViewListenerBrick.java:13` |
| Класс экшена | `catroid/src/main/java/org/catrobat/catroid/content/actions/NativeViewListenerAction.kt:25` (NativeViewListenerAction) |
| Описание (BrickInfo.java) | RU: Назначает обработчик события нативного элемента (касание, изменение текста, ползунок и т.п.) и записывает результат в переменную.<br>EN: Attaches an event listener to a native view (click, text change, slider, etc.) and writes the result into a user variable. |


### 100. NativeViewAnimateBrick

| Поле | Значение |
|---|---|
| Название (тексты лейаута) | `brick_native_view_animate_title`: RU «Анимировать вид» / EN «Animate view»<br>`brick_native_view_animate_id`: RU «ID» / EN «ID»<br>`brick_native_view_animate_prop`: RU «свойство» / EN «property»<br>`brick_native_view_animate_val`: RU «значение» / EN «value»<br>`brick_native_view_animate_duration`: RU «длительность» / EN «duration»<br>`brick_native_view_animate_easing`: RU «плавность» / EN «easing» |
| Параметры | BrickField: `VALUE_1`, `VALUE_2`, `VALUE_3`<br>спиннеры: `Spinner(см. код)` |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/NativeViewAnimateBrick.java:12` |
| Класс экшена | `catroid/src/main/java/org/catrobat/catroid/content/actions/NativeViewAnimateAction.kt:9` (NativeViewAnimateAction) |
| Описание (BrickInfo.java) | RU: Плавно анимирует свойство нативного элемента (прозрачность, поворот, масштаб или позицию) до заданного значения за указанное время.<br>EN: Smoothly animates a native view property (alpha, rotation, scale or position) to the given value over the specified duration. |


### 101. NativeViewBindSpriteBrick

| Поле | Значение |
|---|---|
| Название (тексты лейаута) | `brick_native_view_bind_title`: RU «Привязать вид к спрайту» / EN «Bind view to sprite»<br>`brick_native_view_bind_id`: RU «ID» / EN «ID»<br>`brick_native_view_bind_sprite`: RU «спрайт» / EN «sprite»<br>`brick_native_view_bind_offset`: RU «смещение» / EN «offset»<br>`brick_coord_y_label`: RU «Y:» / EN «Y:»<br>`brick_native_view_bind_align`: RU «выравнивание» / EN «align» |
| Параметры | BrickField: `VALUE_1`, `VALUE_2`, `X_POSITION`, `Y_POSITION`<br>спиннеры: `Spinner(см. код)` |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/NativeViewBindSpriteBrick.java:12` |
| Класс экшена | `catroid/src/main/java/org/catrobat/catroid/content/actions/NativeViewBindSpriteAction.kt:8` (NativeViewBindSpriteAction) |
| Описание (BrickInfo.java) | RU: Привязывает нативный элемент к спрайту, чтобы он следовал за ним со смещением. ВАЖНО: пустое имя спрайта отвязывает элемент.<br>EN: Binds a native view to a sprite so it follows it with an offset. IMPORTANT: an empty sprite name unbinds the view. |

