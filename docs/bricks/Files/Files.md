# Файлы / Files

Ключ категории: `category_file`. Источник состава и порядка: `CategoryBricksFactory.kt` (группированный режим).

### 1. WriteVariableToFileBrick

| Поле | Значение |
|---|---|
| Название (тексты лейаута) | `brick_write_variable_to_file_top`: RU «Сохранить переменную» / EN «Write variable»<br>`brick_write_variable_to_file_bottom`: RU «в файл» / EN «to file» |
| Параметры | BrickField: `WRITE_FILENAME` |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/WriteVariableToFileBrick.kt:31` |
| Класс экшена | `catroid/src/main/java/org/catrobat/catroid/content/actions/WriteVariableToFileAction.kt:32` (WriteVariableToFileAction) |
| Описание (BrickInfo.java) | RU: Записывает переменную в файл в \Загрузки\ пользователя<br>EN: Writes a variable to a file in the user's Downloads folder |


### 2. ReadVariableFromFileBrick

| Поле | Значение |
|---|---|
| Название (тексты лейаута) | `brick_read_variable_from_file_top`: RU «Прочитать переменную» / EN «Read variable»<br>`brick_read_variable_from_file_center`: RU «с файла» / EN «from file»<br>`brick_read_variable_from_file_bottom`: RU «и» / EN «and» |
| Параметры | BrickField: `READ_FILENAME`<br>спиннеры: `Spinner(см. код)` |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/ReadVariableFromFileBrick.kt:35` |
| Класс экшена | `catroid/src/main/java/org/catrobat/catroid/content/actions/ReadVariableFromFileAction.kt:54` (ReadVariableFromFileAction) |
| Описание (BrickInfo.java) | RU: Читает переменную из файла в \Загрузках\ пользователя<br>EN: Reads a variable from a file in the user's Downloads |


### 3. WriteToFilesBrick

| Поле | Значение |
|---|---|
| Название (тексты лейаута) | `brick_write_files`: RU «Записать переменную» / EN «Write variable»<br>`brick_write_files2`: RU «в файл проекта» / EN «to project file» |
| Параметры | BrickField: `VALUE` |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/WriteToFilesBrick.java:32` |
| Класс экшена | `catroid/src/main/java/org/catrobat/catroid/content/actions/WriteToFilesAction.kt:43` (WriteToFilesAction) |
| Описание (BrickInfo.java) | RU: Сохраняет переменную в файлы проекта<br>EN: Saves a variable to project files |


### 4. ReadFromFilesBrick

| Поле | Значение |
|---|---|
| Название (тексты лейаута) | `brick_read_files`: RU «Прочитать переменную» / EN «Read variable»<br>`brick_read_files2`: RU «из файла проекта» / EN «from project file» |
| Параметры | BrickField: `VALUE` |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/ReadFromFilesBrick.java:32` |
| Класс экшена | `catroid/src/main/java/org/catrobat/catroid/content/actions/ReadFromFilesAction.kt:42` (ReadFromFilesAction) |
| Описание (BrickInfo.java) | RU: Читает переменную из файлов проекта<br>EN: Reads a variable from project files |


### 5. AppendToFileBrick

| Поле | Значение |
|---|---|
| Название (тексты лейаута) | `brick_append_to_file`: RU «Дописать текст в файл» / EN «Append text to file»<br>`brick_append_to_file_name`: RU «Имя файла» / EN «File name»<br>`brick_append_to_file_text`: RU «Текст» / EN «Text» |
| Параметры | BrickField: `NAME`, `TEXT` |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/AppendToFileBrick.java:7` |
| Класс экшена | `catroid/src/main/java/org/catrobat/catroid/content/actions/AppendToFileAction.kt:21` (AppendToFileAction) |
| Описание (BrickInfo.java) | RU: Дописывает текст в конец файла в \Загрузках\ пользователя. Если файла нет, он будет создан<br>EN: Appends text to the end of a file in the user's Downloads. Creates the file if it does not exist |


### 6. ListFilesInFolderBrick

| Поле | Значение |
|---|---|
| Название (тексты лейаута) | `brick_list_files_in_folder`: RU «Список файлов в папке» / EN «List files in folder»<br>`brick_list_files_in_folder_folder`: RU «Папка в Загрузках» / EN «Folder in Downloads»<br>`brick_list_files_in_folder_into`: RU «в список» / EN «into list» |
| Параметры | BrickField: `VALUE` |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/ListFilesInFolderBrick.java:8` |
| Класс экшена | `catroid/src/main/java/org/catrobat/catroid/content/actions/ListFilesInFolderAction.kt:11` (ListFilesInFolderAction) |
| Описание (BrickInfo.java) | RU: Записывает имена файлов из папки в \Загрузках\ пользователя в список<br>EN: Writes file names from a folder in the user's Downloads into a list |


### 7. DeleteFilesBrick

| Поле | Значение |
|---|---|
| Название (тексты лейаута) | `brick_delete_files`: RU «Удалить файл проекта» / EN «Delete project file» |
| Параметры | BrickField: `VALUE` |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/DeleteFilesBrick.java:30` |
| Класс экшена | `catroid/src/main/java/org/catrobat/catroid/content/actions/DeleteFilesAction.kt:8` (DeleteFilesAction) |
| Описание (BrickInfo.java) | RU: Удаляет файл проекта ВАЖНО: может вызвать фатальную ошибку, если файла нет.<br>EN: Deletes a project file IMPORTANT: may cause a fatal error if the file does not exist. |


### 8. FileUrlBrick

| Поле | Значение |
|---|---|
| Название (тексты лейаута) | `brick_file_url`: RU «Сохранить файл (Загрузки)» / EN «Save file (Downloads)»<br>`brick_file_url2`: RU «с названием» / EN «with name» |
| Параметры | BrickField: `FILE_URL`, `FILE` |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/FileUrlBrick.java:30` |
| Класс экшена | `catroid/src/main/java/org/catrobat/catroid/content/actions/FileUrlAction.kt:60` (FileUrlAction) |
| Описание (BrickInfo.java) | RU: Сохраняет файл по ссылке в \Загрузки\ пользователя<br>EN: Saves a file from a URL to the user's Downloads |


### 9. FilesUrlBrick

| Поле | Значение |
|---|---|
| Название (тексты лейаута) | `brick_files_url`: RU «Сохранить файл (в проект)» / EN «Save file (in project)»<br>`brick_file_url2`: RU «с названием» / EN «with name» |
| Параметры | BrickField: `FILE_URL`, `FILE` |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/FilesUrlBrick.java:30` |
| Класс экшена | `catroid/src/main/java/org/catrobat/catroid/content/actions/FilesUrlAction.kt:63` (FilesUrlAction) |
| Описание (BrickInfo.java) | RU: Сохраняет файл по ссылке в файлы проекта<br>EN: Saves a file from a URL to project files |


### 10. ZipBrick

| Поле | Значение |
|---|---|
| Название (тексты лейаута) | `brick_zip`: RU «Запаковать Zip (В Загрузках)» / EN «Zip to (from Downloads)»<br>`brick_zip2`: RU «из файлов» / EN «files» |
| Параметры | BrickField: `FILE`, `TEXT` |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/ZipBrick.java:30` |
| Класс экшена | `catroid/src/main/java/org/catrobat/catroid/content/actions/ZipAction.kt:15` (ZipAction) |
| Описание (BrickInfo.java) | RU: Запаковывает файлы из \Загрузок\ пользователя в .zip архив. ВАЖНО: файлы вводятся через запятую. архив так же сохраняетя в \загрузки\<br>EN: Packs files from the user's Downloads into a .zip archive. IMPORTANT: file names are entered separated by commas. The archive is also saved to Downloads. |


### 11. GetZipFileNamesBrick

| Поле | Значение |
|---|---|
| Название (тексты лейаута) | `brick_get_zip_files_text`: RU «Имена файлов из ZIP» / EN «Get file names from ZIP»<br>`brick_get_zip_files_source`: RU «Файл проекта» / EN «Project file»<br>`brick_get_zip_files_variable`: RU «и сохранить в» / EN «and store in» |
| Параметры | BrickField: `NAME` |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/GetZipFileNamesBrick.java:31` |
| Класс экшена | `catroid/src/main/java/org/catrobat/catroid/content/actions/GetZipFileNamesAction.kt:10` (GetZipFileNamesAction) |
| Описание (BrickInfo.java) | RU: Возвращает имена всех файлов внутри zip архива ВАЖНО: архив из файлов проекта<br>EN: Returns the names of all files inside a zip archive IMPORTANT: archive is from project files |


### 12. UnzipBrick

| Поле | Значение |
|---|---|
| Название (тексты лейаута) | `brick_unzip`: RU «Распаковать ZIP (В Загрузках)» / EN «Unzip file (Downloads)» |
| Параметры | BrickField: `FILE` |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/UnzipBrick.java:30` |
| Класс экшена | `catroid/src/main/java/org/catrobat/catroid/content/actions/UnzipAction.kt:15` (UnzipAction) |
| Описание (BrickInfo.java) | RU: Распаковывает .zip архив и сохраняет все файлы в \Загрузки\ пользователя. ВАЖНО: архив из \Загрузок\ пользователя<br>EN: Unpacks a .zip archive and saves all files to the user's Downloads. IMPORTANT: archive is from the user's Downloads |


### 13. DownloadZippedLooksBrick

| Поле | Значение |
|---|---|
| Название (тексты лейаута) | `brick_download_zipped_looks`: RU «Скачать образы из zip» / EN «Download looks from zip»<br>`brick_download_zipped_looks_prefix`: RU «префикс имени» / EN «name prefix» |
| Параметры | BrickField: `DOWNLOAD_URL`, `NAME` |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/DownloadZippedLooksBrick.kt:9` |
| Класс экшена | `catroid/src/main/java/org/catrobat/catroid/content/actions/DownloadZippedLooksAction.kt:20` (DownloadZippedLooksAction) |
| Описание (BrickInfo.java) | — (в BrickInfo.java описания нет) |


### 14. OpenFileBrick

| Поле | Значение |
|---|---|
| Название (тексты лейаута) | `brick_open_file`: RU «Открыть файл из Загрузок» / EN «Open file from Downloads» |
| Параметры | BrickField: `FILE` |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/OpenFileBrick.java:30` |
| Класс экшена | `catroid/src/main/java/org/catrobat/catroid/content/actions/OpenFileAction.kt:52` (OpenFileAction) |
| Описание (BrickInfo.java) | RU: Открывает файл из папки \Загрузки\<br>EN: Opens a file from the \Downloads\ folder |


### 15. MoveFilesBrick

| Поле | Значение |
|---|---|
| Название (тексты лейаута) | `brick_move_files`: RU «Копировать файл (проект -> Загрузки)» / EN «Copy file (project -> Downloads)» |
| Параметры | BrickField: `FILE` |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/MoveFilesBrick.java:30` |
| Класс экшена | `catroid/src/main/java/org/catrobat/catroid/content/actions/MoveFilesAction.kt:17` (MoveFilesAction) |
| Описание (BrickInfo.java) | RU: Копирует файл проекта в \Загрузки\<br>EN: Copies a project file to \Downloads\ |


### 16. MoveDownloadsBrick

| Поле | Значение |
|---|---|
| Название (тексты лейаута) | `brick_move_downloads`: RU «Копировать файл (Загрузки -> проект)» / EN «Copy file (Downloads -> project)» |
| Параметры | BrickField: `FILE` |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/MoveDownloadsBrick.java:30` |
| Класс экшена | `catroid/src/main/java/org/catrobat/catroid/content/actions/MoveDownloadsAction.kt:16` (MoveDownloadsAction) |
| Описание (BrickInfo.java) | RU: Копирует файл из \Загрузок\ в файлы проекта<br>EN: Copies a file from \Downloads\ into project files |


### 17. CopyProjectFileBrick

| Поле | Значение |
|---|---|
| Название (тексты лейаута) | `brick_copy_file_text`: RU «Копировать файл проекта» / EN «Copy project file»<br>`brick_copy_file_source`: RU «Исходный файл» / EN «Source file»<br>`brick_copy_file_destination`: RU «Новое имя» / EN «New name» |
| Параметры | BrickField: `NAME`, `TEXT` |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/CopyProjectFileBrick.java:30` |
| Класс экшена | `catroid/src/main/java/org/catrobat/catroid/content/actions/CopyProjectFileAction.kt:11` (CopyProjectFileAction) |
| Описание (BrickInfo.java) | RU: Копирует файл проекта с новым именем<br>EN: Copies a project file with a new name |


### 18. CopyProjectFileToFolderBrick

| Поле | Значение |
|---|---|
| Название (тексты лейаута) | `brick_copy_project_file_to_folder`: RU «Скопировать файл проекта в папку» / EN «Copy project file to folder»<br>`brick_copy_project_file_to_folder_source`: RU «Исходный файл» / EN «Source file»<br>`brick_copy_project_file_to_folder_destination`: RU «Имя папки» / EN «Folder name» |
| Параметры | BrickField: `NAME`, `TEXT` |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/CopyProjectFileToFolderBrick.java:7` |
| Класс экшена | `catroid/src/main/java/org/catrobat/catroid/content/actions/CopyProjectFileToFolderAction.kt:9` (CopyProjectFileToFolderAction) |
| Описание (BrickInfo.java) | RU: Копирует файл проекта в указанную папку внутри проекта.<br>EN: Copies a project file into the specified folder inside the project. |


### 19. CopyProjectFileToPathBrick

| Поле | Значение |
|---|---|
| Название (тексты лейаута) | `brick_copy_project_file_to_path`: RU «Скопировать файл проекта по пути» / EN «Copy project file to path»<br>`brick_copy_project_file_to_path_source`: RU «Исходный файл» / EN «Source file»<br>`brick_copy_project_file_to_path_destination`: RU «Путь» / EN «Path» |
| Параметры | BrickField: `NAME`, `VALUE` |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/CopyProjectFileToPathBrick.java:7` |
| Класс экшена | `catroid/src/main/java/org/catrobat/catroid/content/actions/CopyProjectFileToPathAction.kt:9` (CopyProjectFileToPathAction) |
| Описание (BrickInfo.java) | RU: Копирует файл проекта по указанному пути внутри проекта. ВАЖНО: путь проверяется на выход за пределы проекта.<br>EN: Copies a project file to the specified path inside the project. IMPORTANT: the path is validated to stay within the project. |


### 20. PutFileIntoFolderBrick

| Поле | Значение |
|---|---|
| Название (тексты лейаута) | `brick_put_file_into_folder`: RU «Положить файл в папку» / EN «Put file into folder»<br>`brick_copy_project_file_to_folder_source`: RU «Исходный файл» / EN «Source file»<br>`brick_copy_project_file_to_folder_destination`: RU «Имя папки» / EN «Folder name» |
| Параметры | BrickField: `NAME`, `TEXT` |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/PutFileIntoFolderBrick.java:7` |
| Класс экшена | `catroid/src/main/java/org/catrobat/catroid/content/actions/PutFileIntoFolderAction.kt:9` (PutFileIntoFolderAction) |
| Описание (BrickInfo.java) | RU: Копирует файл проекта в указанную папку внутри файлов проекта. ВАЖНО: путь проверяется на выход за пределы файлов проекта.<br>EN: Copies a project file into the specified folder inside the project files. IMPORTANT: the path is validated against escaping the project files directory. |


### 21. PutFileIntoPathBrick

| Поле | Значение |
|---|---|
| Название (тексты лейаута) | `brick_put_file_into_path`: RU «Положить файл по пути» / EN «Put file to path»<br>`brick_copy_project_file_to_folder_source`: RU «Исходный файл» / EN «Source file»<br>`brick_copy_project_file_to_path_destination`: RU «Путь» / EN «Path» |
| Параметры | BrickField: `NAME`, `FILE_URL` |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/PutFileIntoPathBrick.java:7` |
| Класс экшена | `catroid/src/main/java/org/catrobat/catroid/content/actions/PutFileIntoPathAction.kt:9` (PutFileIntoPathAction) |
| Описание (BrickInfo.java) | RU: Копирует файл проекта по указанному пути внутри файлов проекта. ВАЖНО: путь проверяется на выход за пределы файлов проекта.<br>EN: Copies a project file to the specified path inside the project files. IMPORTANT: the path is validated against escaping the project files directory. |


### 22. CreateFolderBrick

| Поле | Значение |
|---|---|
| Название (тексты лейаута) | `brick_create_folder`: RU «Создать папку» / EN «Create folder» |
| Параметры | BrickField: `TEXT` |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/CreateFolderBrick.java:7` |
| Класс экшена | `catroid/src/main/java/org/catrobat/catroid/content/actions/CreateFolderAction.kt:9` (CreateFolderAction) |
| Описание (BrickInfo.java) | RU: Создает папку в файлах проекта с указанным именем.<br>EN: Creates a folder in the project files with the specified name. |


### 23. DeleteFolderBrick

| Поле | Значение |
|---|---|
| Название (тексты лейаута) | `brick_delete_folder`: RU «Удалить папку» / EN «Delete folder» |
| Параметры | BrickField: `TEXT` |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/DeleteFolderBrick.java:7` |
| Класс экшена | `catroid/src/main/java/org/catrobat/catroid/content/actions/DeleteFolderAction.kt:9` (DeleteFolderAction) |
| Описание (BrickInfo.java) | RU: Удаляет папку проекта по указанному имени.<br>EN: Deletes a project folder by the given name. |


### 24. CreateFolderByPathBrick

| Поле | Значение |
|---|---|
| Название (тексты лейаута) | `brick_create_folder_by_path`: RU «Создать папку по пути» / EN «Create folder by path» |
| Параметры | BrickField: `PATH`, `TEXT` |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/CreateFolderByPathBrick.java:7` |
| Класс экшена | `catroid/src/main/java/org/catrobat/catroid/content/actions/CreateFolderByPathAction.kt:9` (CreateFolderByPathAction) |
| Описание (BrickInfo.java) | RU: Создает папку по указанному пути внутри проекта. ВАЖНО: путь проверяется на выход за пределы проекта.<br>EN: Creates a folder at the specified path inside the project. IMPORTANT: the path is validated to stay within the project. |


### 25. DeleteFolderByPathBrick

| Поле | Значение |
|---|---|
| Название (тексты лейаута) | `brick_delete_folder_by_path`: RU «Удалить папку по пути» / EN «Delete folder by path» |
| Параметры | BrickField: `PATH`, `TEXT` |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/DeleteFolderByPathBrick.java:7` |
| Класс экшена | `catroid/src/main/java/org/catrobat/catroid/content/actions/DeleteFolderByPathAction.kt:9` (DeleteFolderByPathAction) |
| Описание (BrickInfo.java) | RU: Удаляет папку по указанному пути и имени. ВАЖНО: путь проверяется на выход за пределы каталога (path traversal).<br>EN: Deletes a folder at the given path and name. IMPORTANT: the path is validated against directory traversal. |


### 26. ChooseFileBrick

| Поле | Значение |
|---|---|
| Название (тексты лейаута) | `brick_choose_file`: RU «Выбрать файл» / EN «Choose file»<br>`brick_choose_file2`: RU «сохранить название в переменную» / EN «save name to variable»<br>`brick_choose_file3`: RU «и копировать в файлы проекта» / EN «and copy to project files» |
| Параметры | спиннеры: `Spinner(см. код)` |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/ChooseFileBrick.kt:33` |
| Класс экшена | `catroid/src/main/java/org/catrobat/catroid/content/actions/ChooseFileAction.kt:18` (ChooseFileAction) |
| Описание (BrickInfo.java) | RU: Открывает меню выбора файла и сохраняет выбранный файл в файлы проекта.<br>EN: Opens a file picker menu and saves the selected file to the project files. |


### 27. ExportProjectFileBrick

| Поле | Значение |
|---|---|
| Название (тексты лейаута) | `brick_export_file_text`: RU «Экспортировать файл» / EN «Export file to device»<br>`brick_export_file_source`: RU «Файл проекта» / EN «Project file» |
| Параметры | BrickField: `NAME` |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/ExportProjectFileBrick.java:30` |
| Класс экшена | `catroid/src/main/java/org/catrobat/catroid/content/actions/ExportProjectFileAction.kt:33` (ExportProjectFileAction) |
| Описание (BrickInfo.java) | RU: Открывает меню сохранения файла и сохраняет выбранный файл проекта туда, куда выбрал пользователь<br>EN: Opens a file save menu and saves the selected project file to the user-chosen location. |


### 28. SaveToInternalStorageBrick

| Поле | Значение |
|---|---|
| Название (тексты лейаута) | `brick_save_to_internal`: RU «Сохранить файл проекта во внутреннее хранилище» / EN «Save project file to internal storage»<br>`project_file_name`: RU «Имя файла в проекте» / EN «Project file name»<br>`internal_storage_path`: RU «Путь во внутр. хранилище» / EN «Internal storage path» |
| Параметры | BrickField: `PROJECT_FILE_NAME`, `FILE_NAME` |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/SaveToInternalStorageBrick.java:30` |
| Класс экшена | `catroid/src/main/java/org/catrobat/catroid/content/actions/SaveToInternalStorageAction.kt:10` (SaveToInternalStorageAction) |
| Описание (BrickInfo.java) | RU: Сохраняет файл проекта по пути внутрь хранилища приложения (data/data/...)<br>EN: Saves a project file to the application's internal storage (data/data/...). |


### 29. LoadFromInternalStorageBrick

| Поле | Значение |
|---|---|
| Название (тексты лейаута) | `brick_load_from_internal`: RU «Загрузить файл из внутр. хранилища» / EN «Load file from internal storage»<br>`internal_storage_path`: RU «Путь во внутр. хранилище» / EN «Internal storage path» |
| Параметры | BrickField: `FILE_NAME` |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/LoadFromInternalStorageBrick.java:30` |
| Класс экшена | `catroid/src/main/java/org/catrobat/catroid/content/actions/LoadFromInternalStorageAction.kt:10` (LoadFromInternalStorageAction) |
| Описание (BrickInfo.java) | RU: Копирует файл из хранилища приложения (data/data...) в файлы проекта<br>EN: Copies a file from the application's internal storage (data/data/...) to the project files. |


### 30. SaveLookBrick

| Поле | Значение |
|---|---|
| Название (тексты лейаута) | `brick_save_look`: RU «Образ в файл (Загрузки)» / EN «Look to file (Downloads)» |
| Параметры | BrickField: `FILE` |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/SaveLookBrick.java:30` |
| Класс экшена | `catroid/src/main/java/org/catrobat/catroid/content/actions/SaveLookAction.kt:59` (SaveLookAction) |
| Описание (BrickInfo.java) | RU: Сохраняет образ в \Загрузки\ пользователя<br>EN: Saves the look to the user's Downloads folder |


### 31. LookFileBrick

| Поле | Значение |
|---|---|
| Название (тексты лейаута) | `brick_look_file`: RU «Образ из файла (Загрузки)» / EN «Look from file (Downloads)» |
| Параметры | BrickField: `FILE` |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/LookFileBrick.java:30` |
| Класс экшена | `catroid/src/main/java/org/catrobat/catroid/content/actions/LookFileAction.kt:54` (LookFileAction) |
| Описание (BrickInfo.java) | RU: Использует образ из файла в папке \Загрузки\ пользователя<br>EN: Uses a look from a file in the user's \Downloads\ folder |


### 32. SaveLookFilesBrick

| Поле | Значение |
|---|---|
| Название (тексты лейаута) | `brick_save_look_files`: RU «Образ в файл (проект)» / EN «Look to file (project)» |
| Параметры | BrickField: `FILE` |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/SaveLookFilesBrick.java:30` |
| Класс экшена | `catroid/src/main/java/org/catrobat/catroid/content/actions/SaveLookFilesAction.kt:59` (SaveLookFilesAction) |
| Описание (BrickInfo.java) | RU: Сохраняет образ в файлы проекта<br>EN: Saves the look to project files |


### 33. SetLookFilesBrick

| Поле | Значение |
|---|---|
| Название (тексты лейаута) | `brick_set_look_files`: RU «Образ из файла (проект)» / EN «Look from file (project)» |
| Параметры | BrickField: `FILE` |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/SetLookFilesBrick.java:30` |
| Класс экшена | `catroid/src/main/java/org/catrobat/catroid/content/actions/SetLookFilesAction.kt:54` (SetLookFilesAction) |
| Описание (BrickInfo.java) | RU: Использует образ из файла проекта<br>EN: Uses a look from a project file |


### 34. SoundFileBrick

| Поле | Значение |
|---|---|
| Название (тексты лейаута) | `brick_sound_file`: RU «Воспроизвести звук (файл в Загрузках)» / EN «Play sound (file in Downloads)» |
| Параметры | BrickField: `FILE` |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/SoundFileBrick.java:30` |
| Класс экшена | `catroid/src/main/java/org/catrobat/catroid/content/actions/SoundFileAction.kt:45` (SoundFileAction) |
| Описание (BrickInfo.java) | RU: Играет звук из папки \Загрузки\ у пользователя<br>EN: Plays a sound file from the user's \Downloads\ folder |


### 35. SoundFilesBrick

| Поле | Значение |
|---|---|
| Название (тексты лейаута) | `brick_sound_files`: RU «Воспроизвести звук (файл проекта)» / EN «Play sound (project file)» |
| Параметры | BrickField: `FILE` |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/SoundFilesBrick.java:30` |
| Класс экшена | `catroid/src/main/java/org/catrobat/catroid/content/actions/SoundFilesAction.kt:45` (SoundFilesAction) |
| Описание (BrickInfo.java) | RU: Играет звук из файлов проекта<br>EN: Plays a sound from the project's sound files |


### 36. PrepareSoundBrick

| Поле | Значение |
|---|---|
| Название (тексты лейаута) | `brick_prepare_sound`: RU «Подготовить звук из файла» / EN «Prepare sound from file»<br>`brick_as`: RU «как» / EN «as» |
| Параметры | BrickField: `FILE_NAME`, `CACHE_NAME` |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/PrepareSoundBrick.java:7` |
| Класс экшена | `catroid/src/main/java/org/catrobat/catroid/content/actions/PrepareSoundAction.kt:8` (PrepareSoundAction) |
| Описание (BrickInfo.java) | RU: Подготавливает звук к воспроизведению (нужен, чтобы избежать лагов)<br>EN: Prepares a sound for playback (needed to avoid lag) |


### 37. StartRecordingBrick

| Поле | Значение |
|---|---|
| Название (тексты лейаута) | `brick_start_mic_recording`: RU «Начать запись микрофона» / EN «Start microphone recording» |
| Параметры | — (в коде брика полей BrickField нет) |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/StartRecordingBrick.java:6` |
| Класс экшена | `catroid/src/main/java/org/catrobat/catroid/content/actions/StartRecordingAction.kt:6` (StartRecordingAction) |
| Описание (BrickInfo.java) | RU: Начинает запись звука микрофона<br>EN: Starts recording sound from the microphone |


### 38. StopRecordingBrick

| Поле | Значение |
|---|---|
| Название (тексты лейаута) | `brick_stop_recording_and_save`: RU «Сохранить запись (файл проекта)» / EN «Save recording (project file)» |
| Параметры | BrickField: `FILE_NAME` |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/StopRecordingBrick.java:7` |
| Класс экшена | `catroid/src/main/java/org/catrobat/catroid/content/actions/StopRecordingAction.kt:9` (StopRecordingAction) |
| Описание (BrickInfo.java) | RU: Останавливает запись звука микрофона и сохраняет ее в файлы проекта.<br>EN: Stops recording sound from the microphone and saves it to project files |


### 39. LoadNNBrick

| Поле | Значение |
|---|---|
| Название (тексты лейаута) | `brick_load_nn`: RU «Загрузить .onnx модель из файла проекта» / EN «Load .onnx model from project file» |
| Параметры | BrickField: `FILE` |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/LoadNNBrick.java:30` |
| Класс экшена | `catroid/src/main/java/org/catrobat/catroid/content/actions/LoadNNAction.kt:41` (LoadNNAction) |
| Описание (BrickInfo.java) | RU: Загружает .onnx модель нейросети. ВАЖНО: во избежании утечек памяти - выгружайте ее при выходе.<br>EN: Loads a .onnx neural network model. IMPORTANT: to avoid memory leaks, unload it on exit. |


### 40. ResizeImgBrick

| Поле | Значение |
|---|---|
| Название (тексты лейаута) | `brick_resize_img`: RU «Изменить размер изображение» / EN «Resize IMG (from project files)»<br>`brick_resize_img2`: RU «файл проекта» / EN «file»<br>`brick_resize_img3`: RU «новый размер (x, y)» / EN «new size (x, y)» |
| Параметры | BrickField: `FILE`, `WIDTH`, `HEIGHT` |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/ResizeImgBrick.java:32` |
| Класс экшена | — (в `addActionToSequence` нет класса из `content/actions`) |
| Описание (BrickInfo.java) | RU: Изменяет размер изображения из файла и сохраняет в этот же файл.<br>EN: Resizes an image from a file and saves it to the same file. |


### 41. GrayscaleImgBrick

| Поле | Значение |
|---|---|
| Название (тексты лейаута) | `brick_grayscale_img`: RU «Сделать IMG чёрно-белым» / EN «Grayscale IMG (from project files)»<br>`brick_resize_img2`: RU «файл проекта» / EN «file» |
| Параметры | BrickField: `FILE` |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/GrayscaleImgBrick.java:29` |
| Класс экшена | — (в `addActionToSequence` нет класса из `content/actions`) |
| Описание (BrickInfo.java) | RU: Делает изображение черно-белым и сохраняет в этот же файл.<br>EN: Converts an image to black and white and saves it to the same file. |


### 42. NormalizeImgBrick

| Поле | Значение |
|---|---|
| Название (тексты лейаута) | `brick_normalize_img`: RU «Нормализовать IMG \(0\-1\)» / EN «Normalize IMG (from project files)»<br>`brick_resize_img2`: RU «файл проекта» / EN «file»<br>`brick_normalize_img2`: RU «И сохранить в таблицы» / EN «And save values to tables»<br>`brick_normalize_img3`: RU «R:» / EN «R:»<br>`brick_normalize_img4`: RU «G:» / EN «G:»<br>`brick_normalize_img5`: RU «B:» / EN «B:» |
| Параметры | BrickField: `FILE`, `RED`, `GREEN`, `BLUE` |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/NormalizeImgBrick.java:29` |
| Класс экшена | — (в `addActionToSequence` нет класса из `content/actions`) |
| Описание (BrickInfo.java) | RU: Нормализует изображение в диапозон от 0 до 1 и сохраняет каждый канал в таблицу.<br>EN: Normalizes an image to the range from 0 to 1 and saves each channel into a table. |


### 43. ApplyShaderToImageBrick

| Поле | Значение |
|---|---|
| Название (тексты лейаута) | `brick_apply_shader_to_image`: RU «?» / EN «?»<br>`brick_apply_shader_image_file`: RU «Файл проекта» / EN «Project file» |
| Параметры | BrickField: `NAME`, `TEXT`, `X_POSITION` |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/ApplyShaderToImageBrick.java:7` |
| Класс экшена | `catroid/src/main/java/org/catrobat/catroid/content/actions/ApplyShaderToImageAction.kt:7` (ApplyShaderToImageAction) |
| Описание (BrickInfo.java) | RU: Применяет шейдер к файлу проекта (изображению). Используйте это для генерации чего-либо или обработки изображений<br>EN: Applies a shader to a project file (image). Use this to generate something or process images |


### 44. UploadFileBrick

| Поле | Значение |
|---|---|
| Название (тексты лейаута) | `brick_upload_file`: RU «Отправить файл на сервер» / EN «Upload file to server»<br>`brick_upload_url`: RU «Ссылка» / EN «URL»<br>`brick_upload_project_file`: RU «Путь к файлу проекта» / EN «Project file path»<br>`brick_upload_file_type`: RU «Тип файла» / EN «File type»<br>`brick_upload_mime_type`: RU «Тип MIME» / EN «MIME type»<br>`brick_upload_storage_type`: RU «Тип хранения» / EN «Storage type» |
| Параметры | BrickField: `URL`, `FILE`, `TEXT`<br>спиннеры: `Spinner(см. код)` |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/UploadFileBrick.java:17` |
| Класс экшена | `catroid/src/main/java/org/catrobat/catroid/content/actions/UploadFileAction.java:21` (UploadFileAction) |
| Описание (BrickInfo.java) | RU: Отправляет файл проекта на сервер<br>EN: Sends a project file to the server |


### 45. CreateVideoBrick

| Поле | Значение |
|---|---|
| Название (тексты лейаута) | `brick_video_player`: RU «Создать VideoPlayer» / EN «Create VideoPlayer»<br>`project_file`: RU «файл проекта» / EN «project file»<br>`position`: RU «позиция (x, y)» / EN «position (x, y)»<br>`size_wh`: RU «размер (ширина, высота)» / EN «size (width, height)»<br>`loop_enabled`: RU «зациклить» / EN «loop video»<br>`control_enabled`: RU «показать управление» / EN «show controls»<br>`video_layer`: RU «слой (0/1=под 3D и 2D, 2=над 3D под 2D, 3=поверх 2D, 4=поверх всего)» / EN «layer (0/1=below 3D+2D, 2=above 3D below 2D, 3=above 2D, 4=above all)»<br>`video_key_color`: RU «цвет хромакея (0xRRGGBB)» / EN «chroma key color (0xRRGGBB)» |
| Параметры | BrickField: `NAME`, `FILE`, `POSX`, `POSY`, `WIDTH`, `HEIGHT`, `LOOPED`, `CONTROLS`, `VIDEO_LAYER`, `VIDEO_KEY_COLOR`, `VIDEO_KEY_TOLERANCE` |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/CreateVideoBrick.java:30` |
| Класс экшена | — (в `addActionToSequence` нет класса из `content/actions`) |
| Описание (BrickInfo.java) | RU: Создает VideoPlayer, который позволяет просматривать видео из файлов проекта. Слой: 0/1 - под 3D и 2D, 2 - над 3D под 2D, 3 - поверх 2D, 4 - поверх всего. Хромакей: цвет вырезания + допуск (0 - выкл, сквозь вырезанное видно то, что под видео)<br>EN: Creates a VideoPlayer that allows playing video from project files. Layer: 0/1 - below 3D and 2D, 2 - above 3D below 2D, 3 - above 2D, 4 - above everything. Chroma key: key-out color + tolerance (0 - off, what is below the video shows through keyed areas). |


> Условие: только если BuildConfig.FEATURE_PYQEMU_BRICKS_ENABLED.

### 46. LoadNativeModuleBrick

| Поле | Значение |
|---|---|
| Название (тексты лейаута) | `python_load_native_module`: RU «Загрузить модуль (Python)» / EN «Load Py native module»<br>`python_module_file_name`: RU «Файл проекта(.so)» / EN «project file (.so)» |
| Параметры | BrickField: `FILE_NAME` |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/LoadNativeModuleBrick.java:7` |
| Класс экшена | `catroid/src/main/java/org/catrobat/catroid/content/actions/LoadNativeModuleAction.kt:10` (LoadNativeModuleAction) |
| Описание (BrickInfo.java) | RU: Загружает нативный модуль (.so), нужно для некоторых Python библиотек<br>EN: Loads a native module (.so), needed for some Python libraries |


> Условие: только если BuildConfig.FEATURE_PYQEMU_BRICKS_ENABLED.

### 47. LoadPythonLibraryBrick

| Поле | Значение |
|---|---|
| Название (тексты лейаута) | `python_load_library`: RU «Загрузить библиотеку (Python)» / EN «Load Py library»<br>`python_library_file_name`: RU «Файл проекта (.whl, .zip)» / EN «project file (.whl, .zip)» |
| Параметры | BrickField: `FILE_NAME` |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/LoadPythonLibraryBrick.java:30` |
| Класс экшена | `catroid/src/main/java/org/catrobat/catroid/content/actions/LoadPythonLibraryAction.kt:13` (LoadPythonLibraryAction) |
| Описание (BrickInfo.java) | RU: Загружает библиотеку Python (.whl, .zip), позволяет подключать любые сторонние библиотеки<br>EN: Loads a Python library (.whl, .zip), allows connecting any third-party libraries |


> Условие: только если BuildConfig.FEATURE_PYQEMU_BRICKS_ENABLED.

### 48. RunVm2Brick

| Поле | Значение |
|---|---|
| Название (тексты лейаута) | `brick_run_vm2`: RU «Запустить ВМ» / EN «Run VM»<br>`brick_vm_arguments`: RU «Аргументы» / EN «Arguments» |
| Параметры | BrickField: `VM_ARGUMENTS` |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/RunVm2Brick.java:30` |
| Класс экшена | `catroid/src/main/java/org/catrobat/catroid/content/actions/RunVm2Action.kt:9` (RunVm2Action) |
| Описание (BrickInfo.java) | RU: Запускает виртуальную машину с помощью QEMU на архитектуре x86_64, при помощи аргументов (позволяет запускать Windows, Linux и др) ВАЖНО: обязательно выключайте VM, когда она не нужна<br>EN: Launches a virtual machine using QEMU on x86_64 architecture with arguments (allows running Windows, Linux, etc.). IMPORTANT: always shut down the VM when it is not needed. |


> Условие: только если BuildConfig.FEATURE_PYQEMU_BRICKS_ENABLED.

### 49. RunVMBrick

| Поле | Значение |
|---|---|
| Название (тексты лейаута) | `brick_run_vm`: RU «Запустить VM» / EN «Run VM»<br>`brick_vm_memory`: RU «память (МБ)» / EN «memory (MB)»<br>`brick_vm_cpu`: RU «ядра CPU» / EN «CPU cores»<br>`brick_vm_hda`: RU «диск (необяз.)» / EN «hda (optional)»<br>`brick_vm_cdrom`: RU «cdrom (необяз.)» / EN «cdrom (optional)» |
| Параметры | BrickField: `VM_MEMORY`, `VM_CPU`, `VM_HDA`, `VM_CDROM` |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/RunVMBrick.java:30` |
| Класс экшена | `catroid/src/main/java/org/catrobat/catroid/content/actions/RunVMAction.kt:7` (RunVMAction) |
| Описание (BrickInfo.java) | RU: Запускает виртуальную машину с помощью QEMU на архитектуре x86_64 (позволяет запускать Windows, Linux и др) ВАЖНО: обязательно выключайте VM, когда она не нужна<br>EN: Launches a virtual machine using QEMU on x86_64 architecture (allows running Windows, Linux, etc.). IMPORTANT: always shut down the VM when it is not needed. |


### 50. GenerateKeyBrick

| Поле | Значение |
|---|---|
| Название (тексты лейаута) | `brick_gen_key_title`: RU «Создать ключ» / EN «Generate KeyStore»<br>`brick_gen_key_file`: RU «Файл (.jks)» / EN «File (.jks)»<br>`brick_gen_key_pass`: RU «Пароль» / EN «Password»<br>`brick_gen_key_alias`: RU «Алиас» / EN «Alias»<br>`brick_gen_key_name`: RU «Ваше имя» / EN «Owner Name» |
| Параметры | BrickField: `TEXT_1`, `TEXT_2`, `TEXT_3`, `TEXT_4` |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/GenerateKeyBrick.java:7` |
| Класс экшена | `catroid/src/main/java/org/catrobat/catroid/content/actions/GenerateKeyAction.kt:8` (GenerateKeyAction) |
| Описание (BrickInfo.java) | RU: Генерирует keystore (пару RSA-ключей) с заданным паролем и алиасом для подписи APK. ВАЖНО: файл сохраняется в файлы проекта.<br>EN: Generates a keystore (RSA key pair) with the given password and alias for signing APKs. IMPORTANT: the file is saved into the project files. |


### 51. SignApkBrick

| Поле | Значение |
|---|---|
| Название (тексты лейаута) | `brick_sign_apk_title`: RU «Подписать APK» / EN «Sign APK»<br>`brick_sign_apk_in`: RU «Входной APK» / EN «Input APK»<br>`brick_sign_apk_out`: RU «Выходной APK» / EN «Output APK»<br>`brick_sign_apk_key`: RU «Ключ (.jks)» / EN «KeyStore»<br>`brick_sign_apk_pass`: RU «Пароль» / EN «Password»<br>`brick_sign_apk_alias`: RU «Алиас» / EN «Alias» |
| Параметры | BrickField: `TEXT_1`, `TEXT_2`, `TEXT_3`, `TEXT_4`, `TEXT_5` |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/SignApkBrick.java:30` |
| Класс экшена | `catroid/src/main/java/org/catrobat/catroid/content/actions/SignApkAction.kt:9` (SignApkAction) |
| Описание (BrickInfo.java) | RU: Подписывает APK-файл с помощью keystore (входной файл, выходной файл, путь к keystore, пароль и alias). ВАЖНО: выполняется асинхронно и не ждет завершения.<br>EN: Signs an APK file using a keystore (input file, output file, keystore path, password and alias). IMPORTANT: runs asynchronously and does not wait for completion. |


### 52. UpdateManifestBrick

| Поле | Значение |
|---|---|
| Название (тексты лейаута) | `brick_manif_title`: RU «Изменить Манифест» / EN «Update AndroidManifest»<br>`brick_manif_apk`: RU «Путь к APK» / EN «APK Path»<br>`brick_manif_pkg`: RU «Пакет (com.x.y)» / EN «Package (com.x.y)»<br>`brick_manif_name`: RU «Имя» / EN «App Name»<br>`brick_manif_vcode`: RU «Код версии» / EN «Ver Code (Int)»<br>`brick_manif_vname`: RU «Название версии» / EN «Ver Name (1.0)»<br>`brick_manif_min`: RU «Min SDK» / EN «Min SDK»<br>`brick_manif_target`: RU «Target SDK» / EN «Target SDK» |
| Параметры | BrickField: `TEXT_1`, `TEXT_2`, `TEXT_3`, `TEXT_4`, `TEXT_5`, `TEXT_6`, `TEXT_7`, `TEXT_8`, `TEXT_9`, `TEXT_10` |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/UpdateManifestBrick.java:30` |
| Класс экшена | `catroid/src/main/java/org/catrobat/catroid/content/actions/UpdateManifestAction.kt:9` (UpdateManifestAction) |
| Описание (BrickInfo.java) | RU: Обновляет AndroidManifest внутри указанного APK-файла: позволяет задать имя пакета, имя приложения, код и имя версии, min/target SDK, флаг debuggable, а также добавить или удалить разрешения. ВАЖНО: это блок для продвинутой модификации APK и требует корректных путей и значений.<br>EN: Updates the AndroidManifest inside the specified APK file: lets you set the package name, app name, version code and name, min/target SDK, the debuggable flag, and add or remove permissions. IMPORTANT: this is an advanced APK-modification brick and needs valid paths and values. |


### 53. ExtractFileBrick

| Поле | Значение |
|---|---|
| Название (тексты лейаута) | `brick_extract_title`: RU «Извлечь из APK» / EN «Extract from APK»<br>`brick_extract_apk`: RU «Путь к APK» / EN «APK Path»<br>`brick_extract_inner`: RU «Путь внутри APK» / EN «Path inside APK»<br>`brick_extract_dest`: RU «Сохранить как» / EN «Save to» |
| Параметры | BrickField: `TEXT_1`, `TEXT_2`, `TEXT_3` |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/ExtractFileBrick.java:7` |
| Класс экшена | `catroid/src/main/java/org/catrobat/catroid/content/actions/ExtractFileAction.kt:8` (ExtractFileAction) |
| Описание (BrickInfo.java) | RU: Извлекает файл изнутри APK по заданному внутреннему пути и сохраняет его по пути назначения. ВАЖНО: APK должен существовать в проекте.<br>EN: Extracts a file from inside an APK at the given inner path and saves it to the destination path. IMPORTANT: the APK must exist in the project. |


### 54. AddFileToApkBrick

| Поле | Значение |
|---|---|
| Название (тексты лейаута) | `brick_add_file_apk_title`: RU «Добавить в APK» / EN «Add to APK»<br>`brick_add_file_apk_path`: RU «Путь к APK» / EN «APK Path»<br>`brick_add_file_apk_src`: RU «Источ. (Файл/Папка)» / EN «Source (File/Dir)»<br>`brick_add_file_apk_dest`: RU «Путь внутри APK» / EN «Path inside APK» |
| Параметры | BrickField: `TEXT_1`, `TEXT_2`, `TEXT_3` |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/AddFileToApkBrick.java:30` |
| Класс экшена | `catroid/src/main/java/org/catrobat/catroid/content/actions/AddFileToApkAction.kt:9` (AddFileToApkAction) |
| Описание (BrickInfo.java) | RU: Добавляет файл или папку в APK по заданному пути. Поля: путь к APK, исходный файл/папка и путь внутри APK. ВАЖНО: изменяет APK на диске.<br>EN: Adds a file or folder into an APK at the given path. Fields: APK path, source file/folder and path inside the APK. IMPORTANT: modifies the APK on disk. |


### 55. DeleteFromApkBrick

| Поле | Значение |
|---|---|
| Название (тексты лейаута) | `brick_del_apk_title`: RU «Удалить из APK» / EN «Delete from APK»<br>`brick_del_apk_path`: RU «Путь к APK» / EN «APK Path»<br>`brick_del_apk_pattern`: RU «Путь внутри APK» / EN «Path inside APK» |
| Параметры | BrickField: `TEXT_1`, `TEXT_2` |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/DeleteFromApkBrick.java:30` |
| Класс экшена | `catroid/src/main/java/org/catrobat/catroid/content/actions/DeleteFromApkAction.kt:8` (DeleteFromApkAction) |
| Описание (BrickInfo.java) | RU: Удаляет файлы, совпадающие с шаблоном, из APK-файла по указанному пути.<br>EN: Deletes files matching a pattern from an APK file at the given path. |


### 56. UnzipProjectFilesBrick

| Поле | Значение |
|---|---|
| Название (тексты лейаута) | `brick_unzip_project_files_title`: RU «Разархивировать файлы» / EN «Unzip project files»<br>`brick_unzip_project_files_archive_name`: RU «имя архива» / EN «archive name» |
| Параметры | BrickField: `VALUE_1` |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/UnzipProjectFilesBrick.java:7` |
| Класс экшена | `catroid/src/main/java/org/catrobat/catroid/content/actions/UnzipProjectFilesAction.kt:10` (UnzipProjectFilesAction) |
| Описание (BrickInfo.java) | RU: Распаковывает указанный ZIP-архив проекта в его папку.<br>EN: Extracts the given ZIP archive of the project into its folder. |


### 57. Base64ToFileBrick

| Поле | Значение |
|---|---|
| Название (тексты лейаута) | `brick_base64_to_file_title`: RU «Base64 в файл» / EN «Base64 to file»<br>`brick_base64_to_file_string`: RU «строка» / EN «string»<br>`brick_base64_to_file_dest`: RU «путь» / EN «destination» |
| Параметры | BrickField: `VALUE_1`, `VALUE_2` |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/Base64ToFileBrick.java:7` |
| Класс экшена | `catroid/src/main/java/org/catrobat/catroid/content/actions/Base64ToFileAction.kt:8` (Base64ToFileAction) |
| Описание (BrickInfo.java) | RU: Декодирует Base64-строку и сохраняет результат в указанный файл проекта.<br>EN: Decodes a Base64 string and saves the result to the given project file. |

