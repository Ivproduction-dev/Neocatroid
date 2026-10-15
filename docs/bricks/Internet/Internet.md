# Internet / Сеть

Ключ категории: `category_internet` (`res/values/strings.xml`: "Internet"; `res/values-ru/strings.xml`: "Сеть").
Состав и порядок — строго по `CategoryBricksFactory.kt` → `setupInternetCategoryList` (заголовки `SubCategoryHeaderBrick` не являются блоками и пропущены).
Примечание (факт кода): запись `SubCategoryHeaderBrick("UDP", StartUdpBrick(...))` — только заголовок секции UDP; сам блок `StartUdpBrick` идёт следующей строкой `.add(...)`.

Блоков: 74.

## 1. `StartServerBrick`

| Поле | Значение |
|---|---|
| Название RU | Запустить TCP сервер с портом |
| Название EN | Start TCP server with port |
| Строки (`strings.xml` → `values-ru`) | `brick_start_server`: EN="Start TCP server with port" / RU="Запустить TCP сервер с портом" |
| Параметры | `PORT` (`brick_start_server_edit_text`) |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/StartServerBrick.java:31` |
| Класс экшена (`content/actions`) | `StartServerAction` (`catroid/src/main/java/org/catrobat/catroid/content/actions/StartServerAction.kt:41`; via `createStartServerAction`, `ActionFactory.java:3127`) |
| Описание (строго `BrickInfo.java` + код) | RU: Запускает локальный сервер (сервер только в вашей Wi-Fi точке)<br>EN: Starts a local server (server only within your Wi-Fi hotspot) |
| Вызов в фабрике | `StartServerBrick("1234")` |
| Layout | `catroid/src/main/res/layout/brick_start_server.xml` |

## 2. `ConnectServerBrick`

| Поле | Значение |
|---|---|
| Название RU | Подключиться к TCP серверу |
| Название EN | Connect to TCP server |
| Строки (`strings.xml` → `values-ru`) | `brick_connect_server`: EN="Connect to TCP server" / RU="Подключиться к TCP серверу"; `brick_connect_server2`: EN="with ip" / RU="с IP"; `brick_connect_server3`: EN="and port" / RU="и портом" |
| Параметры | `IP` (`brick_connect_server_edit_ip`); `PORT` (`brick_connect_server_edit_port`) |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/ConnectServerBrick.java:31` |
| Класс экшена (`content/actions`) | `ConnectServerAction` (`catroid/src/main/java/org/catrobat/catroid/content/actions/ConnectServerAction.kt:41`; via `createConnectServerAction`, `ActionFactory.java:3175`) |
| Описание (строго `BrickInfo.java` + код) | RU: Подключается к локальному серверу (только в вашей Wi-Fi точке)<br>EN: Connects to a local server (only within your Wi-Fi hotspot) |
| Вызов в фабрике | `ConnectServerBrick("127.0.0.1", "1234")` |
| Layout | `catroid/src/main/res/layout/brick_connect_server.xml` |

## 3. `ListenServerBrick`

| Поле | Значение |
|---|---|
| Название RU | Слушать TCP сервер и хранить значения в |
| Название EN | Listen TCP server and store |
| Строки (`strings.xml` → `values-ru`) | `brick_listen_server`: EN="Listen TCP server and store" / RU="Слушать TCP сервер и хранить значения в" |
| Параметры | спиннер (`listen_server_mode_spinner`); спиннер (`listen_server_spinner`) |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/ListenServerBrick.java:33` |
| Класс экшена (`content/actions`) | `ListenServerAction` (`catroid/src/main/java/org/catrobat/catroid/content/actions/ListenServerAction.kt:35`; via `createListenServerAction`, `ActionFactory.java:3314`) |
| Описание (строго `BrickInfo.java` + код) | RU: Постоянно слушает сообщения сервера и сохраняет значения в переменную<br>EN: Constantly listens to server messages and saves values into a variable |
| Вызов в фабрике | `ListenServerBrick()` |
| Layout | `catroid/src/main/res/layout/brick_listen_server.xml` |

## 4. `SendServerBrick`

| Поле | Значение |
|---|---|
| Название RU | Отправить на TCP сервер |
| Название EN | Send to TCP server |
| Строки (`strings.xml` → `values-ru`) | `brick_send_server`: EN="Send to TCP server" / RU="Отправить на TCP сервер" |
| Параметры | `VALUE` (`brick_send_server_edit_text`) |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/SendServerBrick.java:31` |
| Класс экшена (`content/actions`) | `SendServerAction` (`catroid/src/main/java/org/catrobat/catroid/content/actions/SendServerAction.kt:41`; via `createSendServerAction`, `ActionFactory.java:3136`) |
| Описание (строго `BrickInfo.java` + код) | RU: Отправляет значение на локальный сервер ВАЖНО: оно так же придет и вам<br>EN: Sends a value to the local server IMPORTANT: it will also be sent to you |
| Вызов в фабрике | `SendServerBrick("Hi")` |
| Layout | `catroid/src/main/res/layout/brick_send_server.xml` |

## 5. `StopServerBrick`

| Поле | Значение |
|---|---|
| Название RU | Остановить TCP сервер |
| Название EN | Stop TCP server |
| Строки (`strings.xml` → `values-ru`) | `brick_stop_server`: EN="Stop TCP server" / RU="Остановить TCP сервер" |
| Параметры | — |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/StopServerBrick.java:31` |
| Класс экшена (`content/actions`) | `StopServerAction` (`catroid/src/main/java/org/catrobat/catroid/content/actions/StopServerAction.kt:41`; via `createStopServerAction`, `ActionFactory.java:3145`) |
| Описание (строго `BrickInfo.java` + код) | RU: Останавливает локальный сервер ВАЖНО: только для хоста<br>EN: Stops the local server IMPORTANT: host only |
| Вызов в фабрике | `StopServerBrick()` |
| Layout | `catroid/src/main/res/layout/brick_stop_server.xml` |

## 6. `DisconnectServerBrick`

| Поле | Значение |
|---|---|
| Название RU | Отключиться от TCP сервера |
| Название EN | Disconnect from TCP server |
| Строки (`strings.xml` → `values-ru`) | `brick_disconnect_server`: EN="Disconnect from TCP server" / RU="Отключиться от TCP сервера" |
| Параметры | — |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/DisconnectServerBrick.java:30` |
| Класс экшена (`content/actions`) | `DisconnectServerAction` (`catroid/src/main/java/org/catrobat/catroid/content/actions/DisconnectServerAction.kt:30`; via `createDisconnectServerAction`, `ActionFactory.java:3152`) |
| Описание (строго `BrickInfo.java` + код) | — |
| Вызов в фабрике | `DisconnectServerBrick()` |
| Layout | `catroid/src/main/res/layout/brick_disconnect_server.xml` |

## 7. `ClearServerMessagesBrick`

| Поле | Значение |
|---|---|
| Название RU | Очистить последнее сообщение |
| Название EN | Clear last message |
| Строки (`strings.xml` → `values-ru`) | `brick_clear_server_messages`: EN="Clear last message" / RU="Очистить последнее сообщение" |
| Параметры | — |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/ClearServerMessagesBrick.java:30` |
| Класс экшена (`content/actions`) | `ClearServerMessagesAction` (`catroid/src/main/java/org/catrobat/catroid/content/actions/ClearServerMessagesAction.kt:30`; via `createClearServerMessagesAction`, `ActionFactory.java:3159`) |
| Описание (строго `BrickInfo.java` + код) | — |
| Вызов в фабрике | `ClearServerMessagesBrick()` |
| Layout | `catroid/src/main/res/layout/brick_clear_server_messages.xml` |

## 8. `GetFromPastebinBrick`

| Поле | Значение |
|---|---|
| Название RU | Получить из Pastebin |
| Название EN | Get from Pastebin |
| Строки (`strings.xml` → `values-ru`) | `brick_get_from_pastebin`: EN="Get from Pastebin" / RU="Получить из Pastebin" |
| Параметры | `URL` (`brick_get_pastebin_edit_url`); спиннер (`get_pastebin_spinner`) |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/GetFromPastebinBrick.java:9` |
| Класс экшена (`content/actions`) | `GetFromPastebinAction` (`catroid/src/main/java/org/catrobat/catroid/content/actions/GetFromPastebinAction.kt:10`; via `createGetFromPastebinAction`, `ActionFactory.java:3185`) |
| Описание (строго `BrickInfo.java` + код) | — |
| Вызов в фабрике | `GetFromPastebinBrick("https://pastebin.com/raw/...")` |
| Layout | `catroid/src/main/res/layout/brick_get_from_pastebin.xml` |

## 9. `CheckPortBrick`

| Поле | Значение |
|---|---|
| Название RU | Проверить порт |
| Название EN | Check port |
| Строки (`strings.xml` → `values-ru`) | `brick_check_port`: EN="Check port" / RU="Проверить порт" |
| Параметры | `PORT` (`brick_check_port_edit`); спиннер (`check_port_spinner`) |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/CheckPortBrick.java:9` |
| Класс экшена (`content/actions`) | `CheckPortAction` (`catroid/src/main/java/org/catrobat/catroid/content/actions/CheckPortAction.kt:9`; via `createCheckPortAction`, `ActionFactory.java:3195`) |
| Описание (строго `BrickInfo.java` + код) | — |
| Вызов в фабрике | `CheckPortBrick("8888")` |
| Layout | `catroid/src/main/res/layout/brick_check_port.xml` |

## 10. `SetTcpServerClientLimitBrick`

| Поле | Значение |
|---|---|
| Название RU | Лимит клиентов на TCP сервере |
| Название EN | TCP server client limit |
| Строки (`strings.xml` → `values-ru`) | `brick_set_tcp_client_limit`: EN="TCP server client limit" / RU="Лимит клиентов на TCP сервере"; `brick_tcp_server_name`: EN="server" / RU="сервер" |
| Параметры | `TEXT` (`brick_tcp_limit_name_edit`); `VALUE` (`brick_tcp_limit_edit`) |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/SetTcpServerClientLimitBrick.java:8` |
| Класс экшена (`content/actions`) | `SetTcpServerClientLimitAction` (`catroid/src/main/java/org/catrobat/catroid/content/actions/SetTcpServerClientLimitAction.kt:8`; via `createSetTcpServerClientLimitAction`, `ActionFactory.java:3294`) |
| Описание (строго `BrickInfo.java` + код) | RU: Задает лимит клиентов именованного TCP сервера<br>EN: Sets the client limit of a named TCP server |
| Вызов в фабрике | `SetTcpServerClientLimitBrick(10)` |
| Layout | `catroid/src/main/res/layout/brick_set_tcp_client_limit.xml` |

## 11. `SetTcpServerTimeoutBrick`

| Поле | Значение |
|---|---|
| Название RU | Таймаут TCP сервера (секунды) |
| Название EN | TCP server timeout (seconds) |
| Строки (`strings.xml` → `values-ru`) | `brick_set_tcp_timeout`: EN="TCP server timeout (seconds)" / RU="Таймаут TCP сервера (секунды)"; `brick_tcp_server_name`: EN="server" / RU="сервер" |
| Параметры | `TEXT` (`brick_tcp_timeout_name_edit`); `VALUE` (`brick_tcp_timeout_edit`) |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/SetTcpServerTimeoutBrick.java:8` |
| Класс экшена (`content/actions`) | `SetTcpServerTimeoutAction` (`catroid/src/main/java/org/catrobat/catroid/content/actions/SetTcpServerTimeoutAction.kt:8`; via `createSetTcpServerTimeoutAction`, `ActionFactory.java:3304`) |
| Описание (строго `BrickInfo.java` + код) | RU: Задает таймаут именованного TCP сервера в секундах<br>EN: Sets the timeout of a named TCP server in seconds |
| Вызов в фабрике | `SetTcpServerTimeoutBrick(30)` |
| Layout | `catroid/src/main/res/layout/brick_set_tcp_timeout.xml` |

## 12. `SendToTcpServerBrick`

| Поле | Значение |
|---|---|
| Название RU | Отправить на TCP сервер |
| Название EN | Send to TCP server |
| Строки (`strings.xml` → `values-ru`) | `brick_send_to_tcp_server`: EN="Send to TCP server" / RU="Отправить на TCP сервер"; `brick_tcp_server_name`: EN="server" / RU="сервер" |
| Параметры | `TEXT` (`brick_send_tcp_name_edit`); `VALUE` (`brick_send_tcp_edit`); `VALUE_2` (`brick_send_tcp_edit2`); `VALUE_3` (`brick_send_tcp_edit3`); `VALUE_4` (`brick_send_tcp_edit4`); `VALUE_5` (`brick_send_tcp_edit5`); `VALUE_6` (`brick_send_tcp_edit6`); `VALUE_7` (`brick_send_tcp_edit7`); `VALUE_8` (`brick_send_tcp_edit8`); `VALUE_9` (`brick_send_tcp_edit9`); `VALUE_10` (`brick_send_tcp_edit10`); `VALUE_11` (`brick_send_tcp_edit11`); `VALUE_12` (`brick_send_tcp_edit12`) |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/SendToTcpServerBrick.java:14` |
| Класс экшена (`content/actions`) | `SendToTcpServerAction` (`catroid/src/main/java/org/catrobat/catroid/content/actions/SendToTcpServerAction.kt:8`; via `createSendToTcpServerAction`, `ActionFactory.java:3273`) |
| Описание (строго `BrickInfo.java` + код) | RU: Отправляет одно или несколько значений на TCP сервер<br>EN: Sends one or several values to the TCP server |
| Вызов в фабрике | `SendToTcpServerBrick("okay")` |
| Layout | `catroid/src/main/res/layout/brick_send_to_tcp_server.xml` |

## 13. `SendToTcpClientsBrick`

| Поле | Значение |
|---|---|
| Название RU | Отправить TCP клиентам |
| Название EN | Send TCP to clients |
| Строки (`strings.xml` → `values-ru`) | `brick_send_to_tcp_clients`: EN="Send TCP to clients" / RU="Отправить TCP клиентам"; `brick_tcp_server_name`: EN="server" / RU="сервер"; `brick_send_to_tcp_clients_echo_no`: EN="without echo to self" / RU="без эха себе"; `brick_send_to_tcp_clients_echo_yes`: EN="with echo to self" / RU="с эхом себе" |
| Параметры | `TEXT` (`brick_send_tcp_clients_name_edit`); `VALUE` (`brick_send_tcp_clients_edit`); `VALUE_2` (`brick_send_tcp_clients_edit2`); `VALUE_3` (`brick_send_tcp_clients_edit3`); `VALUE_4` (`brick_send_tcp_clients_edit4`); `VALUE_5` (`brick_send_tcp_clients_edit5`); `VALUE_6` (`brick_send_tcp_clients_edit6`); `VALUE_7` (`brick_send_tcp_clients_edit7`); `VALUE_8` (`brick_send_tcp_clients_edit8`); `VALUE_9` (`brick_send_tcp_clients_edit9`); `VALUE_10` (`brick_send_tcp_clients_edit10`); `VALUE_11` (`brick_send_tcp_clients_edit11`); `VALUE_12` (`brick_send_tcp_clients_edit12`); спиннер (`brick_send_tcp_clients_echo_spinner`) |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/SendToTcpClientsBrick.java:16` |
| Класс экшена (`content/actions`) | `SendToTcpClientsAction` (`catroid/src/main/java/org/catrobat/catroid/content/actions/SendToTcpClientsAction.kt:8`; via `createSendToTcpClientsAction`, `ActionFactory.java:3283`) |
| Описание (строго `BrickInfo.java` + код) | RU: Отправляет одно или несколько значений всем подключенным TCP клиентам (только для хоста)<br>EN: Sends one or several values to all connected TCP clients (host only) |
| Вызов в фабрике | `SendToTcpClientsBrick("hello")` |
| Layout | `catroid/src/main/res/layout/brick_send_to_tcp_clients.xml` |

## 14. `ListenTcpServerBrick`

| Поле | Значение |
|---|---|
| Название RU | Слушать TCP сервер и сохранить в |
| Название EN | Listen TCP server and store |
| Строки (`strings.xml` → `values-ru`) | `brick_listen_tcp_server`: EN="Listen TCP server and store" / RU="Слушать TCP сервер и сохранить в"; `brick_tcp_server_name`: EN="server" / RU="сервер"; `new_option`: EN="new…" / RU="новое…"; `variable_locked_enter_password`: EN="Enter variable password" / RU="Введите пароль переменной" |
| Параметры | `TEXT` (`listen_tcp_name_edit`); спиннер (`listen_tcp_mode_spinner`); спиннер (`listen_tcp_spinner1`); спиннер (`listen_tcp_spinner2`); спиннер (`listen_tcp_spinner3`); спиннер (`listen_tcp_spinner4`); спиннер (`listen_tcp_spinner5`); спиннер (`listen_tcp_spinner6`); спиннер (`listen_tcp_spinner7`); спиннер (`listen_tcp_spinner8`); спиннер (`listen_tcp_spinner9`); спиннер (`listen_tcp_spinner10`); спиннер (`listen_tcp_spinner11`); спиннер (`listen_tcp_spinner12`) |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/ListenTcpServerBrick.java:29` |
| Класс экшена (`content/actions`) | `ListenTcpServerAction` (`catroid/src/main/java/org/catrobat/catroid/content/actions/ListenTcpServerAction.kt:13`; via `createListenTcpServerAction`, `ActionFactory.java:3262`) |
| Описание (строго `BrickInfo.java` + код) | RU: Слушает TCP сервер и раскладывает строки сообщения по переменным<br>EN: Listens to a TCP server and splits message rows into variables |
| Вызов в фабрике | `ListenTcpServerBrick()` |
| Layout | `catroid/src/main/res/layout/brick_listen_tcp_server.xml` |

## 15. `WriteBaseBrick`

| Поле | Значение |
|---|---|
| Название RU | Записать в FireBase |
| Название EN | Write to FireBase |
| Строки (`strings.xml` → `values-ru`) | `firebase_write`: EN="Write to FireBase" / RU="Записать в FireBase"; `firebase_base`: EN="FireBase ID" / RU="Айди базы"; `firebase_key`: EN="Key" / RU="Ключ"; `firebase_value`: EN="Value" / RU="Значение" |
| Параметры | `FIREBASE_ID` (`brick_write_base_edit_base`); `FIREBASE_KEY` (`brick_write_base_edit_key`); `FIREBASE_VALUE` (`brick_write_base_edit_value`); спиннер (`brick_write_base_wait_spinner`) |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/WriteBaseBrick.java:38` |
| Класс экшена (`content/actions`) | `WriteBaseAction` (`catroid/src/main/java/org/catrobat/catroid/content/actions/WriteBaseAction.kt:41`; via `createWriteBaseAction`, `ActionFactory.java:2639`) |
| Описание (строго `BrickInfo.java` + код) | RU: Записывает значение в FireBase ВАЖНО: если айди базы неправильный - может вызывать фатальную ошибку<br>EN: Writes a value to Firebase IMPORTANT: if the database ID is incorrect, may cause a fatal error |
| Вызов в фабрике | `WriteBaseBrick("firebase_id", "key", "hello")` |
| Layout | `catroid/src/main/res/layout/brick_write_base.xml` |

## 16. `ReadBaseBrick`

| Поле | Значение |
|---|---|
| Название RU | Прочитать из FireBase |
| Название EN | Read from FireBase |
| Строки (`strings.xml` → `values-ru`) | `firebase_read`: EN="Read from FireBase" / RU="Прочитать из FireBase"; `firebase_base`: EN="FireBase ID" / RU="Айди базы"; `firebase_key`: EN="Key" / RU="Ключ"; `firebase_store`: EN="and store in" / RU="и сохранить в" |
| Параметры | `FIREBASE_ID` (`brick_read_base_edit_base`); `FIREBASE_KEY` (`brick_read_base_edit_key`); спиннер (`brick_read_base_wait_spinner`); спиннер (`brick_read_base_spinner`) |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/ReadBaseBrick.java:38` |
| Класс экшена (`content/actions`) | `ReadBaseAction` (`catroid/src/main/java/org/catrobat/catroid/content/actions/ReadBaseAction.kt:42`; via `createReadBaseAction`, `ActionFactory.java:2622`) |
| Описание (строго `BrickInfo.java` + код) | RU: Читает значение из FireBase ВАЖНО: если айди базы неправильный - может вызывать фатальную ошибку<br>EN: Reads a value from Firebase IMPORTANT: if the database ID is incorrect, may cause a fatal error |
| Вызов в фабрике | `ReadBaseBrick("firebase_id", "key")` |
| Layout | `catroid/src/main/res/layout/brick_read_base.xml` |

## 17. `DeleteBaseBrick`

| Поле | Значение |
|---|---|
| Название RU | Удалить из FireBase |
| Название EN | Delete from FireBase |
| Строки (`strings.xml` → `values-ru`) | `firebase_delete`: EN="Delete from FireBase" / RU="Удалить из FireBase"; `firebase_base`: EN="FireBase ID" / RU="Айди базы"; `firebase_key`: EN="Key" / RU="Ключ"; `firebase_wait_for_response`: EN="Wait for response" / RU="Ждать ответа" |
| Параметры | `FIREBASE_ID` (`brick_delete_base_edit_base`); `FIREBASE_KEY` (`brick_delete_base_edit_key`); спиннер (`brick_delete_base_wait_spinner`) |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/DeleteBaseBrick.java:38` |
| Класс экшена (`content/actions`) | `DeleteBaseAction` (`catroid/src/main/java/org/catrobat/catroid/content/actions/DeleteBaseAction.kt:41`; via `createDeleteBaseAction`, `ActionFactory.java:2656`) |
| Описание (строго `BrickInfo.java` + код) | RU: Удаляет ключ из FireBase ВАЖНО: если айди базы неправильный - может вызывать фатальную ошибку<br>EN: Deletes a key from Firebase IMPORTANT: if the database ID is incorrect, may cause a fatal error |
| Вызов в фабрике | `DeleteBaseBrick("firebase_id", "key")` |
| Layout | `catroid/src/main/res/layout/brick_delete_base.xml` |

## 18. `UploadFileToFirebaseBrick`

| Поле | Значение |
|---|---|
| Название RU | Загрузить в Firebase Storage |
| Название EN | Upload to Firebase Storage |
| Строки (`strings.xml` → `values-ru`) | `firebase_upload`: EN="Upload to Firebase Storage" / RU="Загрузить в Firebase Storage"; `firebase_bucket`: EN="Bucket URL" / RU="URL хранилища"; `firebase_storage_path`: EN="Remote path" / RU="Путь на сервере"; `firebase_local_file`: EN="Local file" / RU="Локальный файл" |
| Параметры | `FIREBASE_BUCKET` (`brick_upload_firebase_bucket`); `FIREBASE_STORAGE_PATH` (`brick_upload_firebase_path`); `FILE` (`brick_upload_firebase_file`) |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/UploadFileToFirebaseBrick.java:14` |
| Класс экшена (`content/actions`) | `UploadFileToFirebaseAction` (`catroid/src/main/java/org/catrobat/catroid/content/actions/UploadFileToFirebaseAction.kt:10`; via `createUploadFileToFirebaseAction`, `ActionFactory.java:7820`) |
| Описание (строго `BrickInfo.java` + код) | RU: Загружает локальный файл в облачное хранилище Firebase Storage: указываются URL бакета, удалённый путь в хранилище и путь к локальному файлу. ВАЖНО: требуется доступ к интернету и корректно настроенный проект Firebase.<br>EN: Uploads a local file to Firebase Storage cloud storage: you specify the bucket URL, the remote storage path and the local file path. IMPORTANT: requires internet access and a properly configured Firebase project. |
| Вызов в фабрике | `UploadFileToFirebaseBrick("bucket", "images/photo.jpg", "screenshot.png")` |
| Layout | `catroid/src/main/res/layout/brick_upload_firebase.xml` |

## 19. `DownloadFileFromFirebaseBrick`

| Поле | Значение |
|---|---|
| Название RU | Скачать из Firebase Storage |
| Название EN | Download from Firebase Storage |
| Строки (`strings.xml` → `values-ru`) | `firebase_download`: EN="Download from Firebase Storage" / RU="Скачать из Firebase Storage"; `firebase_bucket`: EN="Bucket URL" / RU="URL хранилища"; `firebase_storage_path`: EN="Remote path" / RU="Путь на сервере"; `firebase_local_file`: EN="Local file" / RU="Локальный файл" |
| Параметры | `FIREBASE_BUCKET` (`brick_download_firebase_bucket`); `FIREBASE_STORAGE_PATH` (`brick_download_firebase_path`); `DOWNLOAD_PATH` (`brick_download_firebase_dest`); спиннер (`brick_download_firebase_spinner`) |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/DownloadFileFromFirebaseBrick.java:15` |
| Класс экшена (`content/actions`) | `DownloadFileFromFirebaseAction` (`catroid/src/main/java/org/catrobat/catroid/content/actions/DownloadFileFromFirebaseAction.kt:10`; via `createDownloadFileFromFirebaseAction`, `ActionFactory.java:7831`) |
| Описание (строго `BrickInfo.java` + код) | RU: Скачивает файл из Firebase Storage (по URL ведра и удаленному пути) в локальный файл и сохраняет результат в переменную.<br>EN: Downloads a file from Firebase Storage (bucket URL and remote path) to a local file and stores the result in a variable. |
| Вызов в фабрике | `DownloadFileFromFirebaseBrick("bucket", "images/photo.jpg", "downloaded.png")` |
| Layout | `catroid/src/main/res/layout/brick_download_firebase.xml` |

## 20. `ListFirebaseFilesBrick`

| Поле | Значение |
|---|---|
| Название RU | Список файлов в Firebase Storage |
| Название EN | List Firebase Storage files |
| Строки (`strings.xml` → `values-ru`) | `firebase_list`: EN="List Firebase Storage files" / RU="Список файлов в Firebase Storage"; `firebase_bucket`: EN="Bucket URL" / RU="URL хранилища"; `firebase_storage_prefix`: EN="Prefix" / RU="Префикс"; `firebase_store`: EN="and store in" / RU="и сохранить в" |
| Параметры | `FIREBASE_BUCKET` (`brick_list_firebase_bucket`); `FIREBASE_STORAGE_PATH` (`brick_list_firebase_prefix`); спиннер (`brick_list_firebase_spinner`) |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/ListFirebaseFilesBrick.java:15` |
| Класс экшена (`content/actions`) | `ListFirebaseFilesAction` (`catroid/src/main/java/org/catrobat/catroid/content/actions/ListFirebaseFilesAction.kt:9`; via `createListFirebaseFilesAction`, `ActionFactory.java:7844`) |
| Описание (строго `BrickInfo.java` + код) | RU: Получает список файлов из хранилища Firebase Storage по указанному бакету и пути (префиксу) и сохраняет результат в выбранную переменную.<br>EN: Retrieves the list of files from Firebase Storage for the given bucket and path (prefix) and stores the result in the selected variable. |
| Вызов в фабрике | `ListFirebaseFilesBrick("bucket", "images/")` |
| Layout | `catroid/src/main/res/layout/brick_list_firebase.xml` |

## 21. `DeleteFirebaseFileBrick`

| Поле | Значение |
|---|---|
| Название RU | Удалить из Firebase Storage |
| Название EN | Delete from Firebase Storage |
| Строки (`strings.xml` → `values-ru`) | `firebase_delete_storage`: EN="Delete from Firebase Storage" / RU="Удалить из Firebase Storage"; `firebase_bucket`: EN="Bucket URL" / RU="URL хранилища"; `firebase_storage_path`: EN="Remote path" / RU="Путь на сервере" |
| Параметры | `FIREBASE_BUCKET` (`brick_delete_firebase_bucket`); `FIREBASE_STORAGE_PATH` (`brick_delete_firebase_path`) |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/DeleteFirebaseFileBrick.java:14` |
| Класс экшена (`content/actions`) | `DeleteFirebaseFileAction` (`catroid/src/main/java/org/catrobat/catroid/content/actions/DeleteFirebaseFileAction.kt:8`; via `createDeleteFirebaseFileAction`, `ActionFactory.java:7856`) |
| Описание (строго `BrickInfo.java` + код) | RU: Удаляет файл из Firebase Storage по указанному бакету и пути. ВАЖНО: при неверном бакете или пути файл не будет удален.<br>EN: Deletes a file from Firebase Storage by the specified bucket and path. IMPORTANT: if the bucket or path is invalid the file will not be deleted. |
| Вызов в фабрике | `DeleteFirebaseFileBrick("bucket", "images/old.jpg")` |
| Layout | `catroid/src/main/res/layout/brick_delete_firebase.xml` |

## 22. `FirebaseSignInAnonymouslyBrick`

| Поле | Значение |
|---|---|
| Название RU | Войти в FireBase анонимно |
| Название EN | Sign in anonymously to FireBase |
| Строки (`strings.xml` → `values-ru`) | `firebase_sign_in_anonymous`: EN="Sign in anonymously to FireBase" / RU="Войти в FireBase анонимно"; `firebase_wait_for_response`: EN="Wait for response" / RU="Ждать ответа" |
| Параметры | спиннер (`brick_firebase_sign_in_anonymous_wait_spinner`) |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/FirebaseSignInAnonymouslyBrick.java:35` |
| Класс экшена (`content/actions`) | `FirebaseSignInAnonymouslyAction` (`catroid/src/main/java/org/catrobat/catroid/content/actions/FirebaseSignInAnonymouslyAction.kt:30`; via `createFirebaseSignInAnonymouslyAction`, `ActionFactory.java:2672`) |
| Описание (строго `BrickInfo.java` + код) | RU: Входит в Firebase анонимно. Нужен, если правила базы требуют авторизацию.<br>EN: Signs in to Firebase anonymously. Needed when the database rules require authentication. |
| Вызов в фабрике | `FirebaseSignInAnonymouslyBrick()` |
| Layout | `catroid/src/main/res/layout/brick_firebase_sign_in_anonymous.xml` |

## 23. `FirebaseSignInEmailPasswordBrick`

| Поле | Значение |
|---|---|
| Название RU | Войти в FireBase по email |
| Название EN | Sign in to FireBase with email |
| Строки (`strings.xml` → `values-ru`) | `firebase_sign_in_email`: EN="Sign in to FireBase with email" / RU="Войти в FireBase по email"; `firebase_email`: EN="Email" / RU="Email"; `firebase_password`: EN="Password" / RU="Пароль"; `firebase_wait_for_response`: EN="Wait for response" / RU="Ждать ответа" |
| Параметры | `FIREBASE_EMAIL` (`brick_firebase_sign_in_email_password_edit_email`); `FIREBASE_PASSWORD` (`brick_firebase_sign_in_email_password_edit_password`); спиннер (`brick_firebase_sign_in_email_password_wait_spinner`) |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/FirebaseSignInEmailPasswordBrick.java:36` |
| Класс экшена (`content/actions`) | `FirebaseSignInEmailPasswordAction` (`catroid/src/main/java/org/catrobat/catroid/content/actions/FirebaseSignInEmailPasswordAction.kt:31`; via `createFirebaseSignInEmailPasswordAction`, `ActionFactory.java:2681`) |
| Описание (строго `BrickInfo.java` + код) | RU: Входит в Firebase по email и паролю.<br>EN: Signs in to Firebase with email and password. |
| Вызов в фабрике | `FirebaseSignInEmailPasswordBrick("email@example.com", "password")` |
| Layout | `catroid/src/main/res/layout/brick_firebase_sign_in_email_password.xml` |

## 24. `FirebaseSignOutBrick`

| Поле | Значение |
|---|---|
| Название RU | Выйти из FireBase |
| Название EN | Sign out of FireBase |
| Строки (`strings.xml` → `values-ru`) | `firebase_sign_out`: EN="Sign out of FireBase" / RU="Выйти из FireBase"; `firebase_wait_for_response`: EN="Wait for response" / RU="Ждать ответа" |
| Параметры | спиннер (`brick_firebase_sign_out_wait_spinner`) |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/FirebaseSignOutBrick.java:35` |
| Класс экшена (`content/actions`) | `FirebaseSignOutAction` (`catroid/src/main/java/org/catrobat/catroid/content/actions/FirebaseSignOutAction.kt:30`; via `createFirebaseSignOutAction`, `ActionFactory.java:2697`) |
| Описание (строго `BrickInfo.java` + код) | RU: Выходит из Firebase. После этого доступ к закрытым базам прекращается.<br>EN: Signs out of Firebase. Access to protected databases stops afterwards. |
| Вызов в фабрике | `FirebaseSignOutBrick()` |
| Layout | `catroid/src/main/res/layout/brick_firebase_sign_out.xml` |

## 25. `FirebaseGetUserIdBrick`

| Поле | Значение |
|---|---|
| Название RU | Получить айди пользователя FireBase |
| Название EN | Get FireBase user ID |
| Строки (`strings.xml` → `values-ru`) | `firebase_get_user_id`: EN="Get FireBase user ID" / RU="Получить айди пользователя FireBase"; `firebase_store`: EN="and store in" / RU="и сохранить в" |
| Параметры | спиннер (`brick_firebase_get_user_id_spinner`) |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/FirebaseGetUserIdBrick.java:31` |
| Класс экшена (`content/actions`) | `FirebaseGetUserIdAction` (`catroid/src/main/java/org/catrobat/catroid/content/actions/FirebaseGetUserIdAction.kt:31`; via `createFirebaseGetUserIdAction`, `ActionFactory.java:2706`) |
| Описание (строго `BrickInfo.java` + код) | RU: Сохраняет UID текущего пользователя Firebase в переменную (пусто, если не вошёл).<br>EN: Stores the current Firebase user UID in a variable (empty if not signed in). |
| Вызов в фабрике | `FirebaseGetUserIdBrick()` |
| Layout | `catroid/src/main/res/layout/brick_firebase_get_user_id.xml` |

## 26. `FirebaseIsSignedInBrick`

| Поле | Значение |
|---|---|
| Название RU | FireBase вход выполнен? |
| Название EN | FireBase signed in? |
| Строки (`strings.xml` → `values-ru`) | `firebase_is_signed_in`: EN="FireBase signed in?" / RU="FireBase вход выполнен?"; `firebase_store`: EN="and store in" / RU="и сохранить в" |
| Параметры | спиннер (`brick_firebase_is_signed_in_spinner`) |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/FirebaseIsSignedInBrick.java:31` |
| Класс экшена (`content/actions`) | `FirebaseIsSignedInAction` (`catroid/src/main/java/org/catrobat/catroid/content/actions/FirebaseIsSignedInAction.kt:31`; via `createFirebaseIsSignedInAction`, `ActionFactory.java:2715`) |
| Описание (строго `BrickInfo.java` + код) | RU: Сохраняет 1, если пользователь вошёл в Firebase, иначе 0.<br>EN: Stores 1 if the user is signed in to Firebase, otherwise 0. |
| Вызов в фабрике | `FirebaseIsSignedInBrick()` |
| Layout | `catroid/src/main/res/layout/brick_firebase_is_signed_in.xml` |

## 27. `PushBaseBrick`

| Поле | Значение |
|---|---|
| Название RU | Добавить в FireBase |
| Название EN | Push to FireBase |
| Строки (`strings.xml` → `values-ru`) | `firebase_push`: EN="Push to FireBase" / RU="Добавить в FireBase"; `firebase_base`: EN="FireBase ID" / RU="Айди базы"; `firebase_key`: EN="Key" / RU="Ключ"; `firebase_value`: EN="Value" / RU="Значение" |
| Параметры | `FIREBASE_ID` (`brick_push_base_edit_base`); `FIREBASE_KEY` (`brick_push_base_edit_key`); `FIREBASE_VALUE` (`brick_push_base_edit_value`); спиннер (`brick_push_base_wait_spinner`); спиннер (`brick_push_base_spinner`) |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/PushBaseBrick.java:36` |
| Класс экшена (`content/actions`) | `PushBaseAction` (`catroid/src/main/java/org/catrobat/catroid/content/actions/PushBaseAction.kt:32`; via `createPushBaseAction`, `ActionFactory.java:2724`) |
| Описание (строго `BrickInfo.java` + код) | RU: Добавляет данные в Firebase с автогенерируемым ключом и сохраняет этот ключ в переменную.<br>EN: Adds data to Firebase with an auto-generated key and stores that key in a variable. |
| Вызов в фабрике | `PushBaseBrick("firebase_id", "messages", "hello")` |
| Layout | `catroid/src/main/res/layout/brick_push_base.xml` |

## 28. `UpdateBaseBrick`

| Поле | Значение |
|---|---|
| Название RU | Обновить FireBase |
| Название EN | Update FireBase |
| Строки (`strings.xml` → `values-ru`) | `firebase_update`: EN="Update FireBase" / RU="Обновить FireBase"; `firebase_base`: EN="FireBase ID" / RU="Айди базы"; `firebase_key`: EN="Key" / RU="Ключ"; `firebase_value`: EN="Value" / RU="Значение" |
| Параметры | `FIREBASE_ID` (`brick_update_base_edit_base`); `FIREBASE_KEY` (`brick_update_base_edit_key`); `FIREBASE_VALUE` (`brick_update_base_edit_value`); спиннер (`brick_update_base_wait_spinner`) |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/UpdateBaseBrick.java:35` |
| Класс экшена (`content/actions`) | `UpdateBaseAction` (`catroid/src/main/java/org/catrobat/catroid/content/actions/UpdateBaseAction.kt:31`; via `createUpdateBaseAction`, `ActionFactory.java:2738`) |
| Описание (строго `BrickInfo.java` + код) | RU: Обновляет данные Firebase: JSON-объект сливается с существующими полями (updateChildren), иначе значение заменяется.<br>EN: Updates Firebase data: a JSON object is merged with existing fields (updateChildren), otherwise the value is replaced. |
| Вызов в фабрике | `UpdateBaseBrick("firebase_id", "user/name", "{\"nick\":\"Tom\"}")` |
| Layout | `catroid/src/main/res/layout/brick_update_base.xml` |

## 29. `QueryBaseBrick`

| Поле | Значение |
|---|---|
| Название RU | Запросить FireBase |
| Название EN | Query FireBase |
| Строки (`strings.xml` → `values-ru`) | `firebase_query`: EN="Query FireBase" / RU="Запросить FireBase"; `firebase_base`: EN="FireBase ID" / RU="Айди базы"; `firebase_key`: EN="Key" / RU="Ключ"; `firebase_query_order`: EN="Order by child field" / RU="Сортировать по полю" |
| Параметры | `FIREBASE_ID` (`brick_query_base_edit_base`); `FIREBASE_KEY` (`brick_query_base_edit_key`); `FIREBASE_QUERY_ORDER` (`brick_query_base_edit_order`); `FIREBASE_QUERY_LIMIT` (`brick_query_base_edit_limit`); `FIREBASE_QUERY_EQUAL` (`brick_query_base_edit_equal`); спиннер (`brick_query_base_spinner`) |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/QueryBaseBrick.java:31` |
| Класс экшена (`content/actions`) | `QueryBaseAction` (`catroid/src/main/java/org/catrobat/catroid/content/actions/QueryBaseAction.kt:32`; via `createQueryBaseAction`, `ActionFactory.java:2755`) |
| Описание (строго `BrickInfo.java` + код) | RU: Запрашивает данные Firebase по пути с сортировкой (orderByChild), лимитом и фильтром equalTo; результат (JSON) сохраняется в переменную.<br>EN: Queries Firebase data by path with ordering (orderByChild), limit and equalTo filter; the result (JSON) is stored in a variable. |
| Вызов в фабрике | `QueryBaseBrick("firebase_id", "messages", "score", "10", "")` |
| Layout | `catroid/src/main/res/layout/brick_query_base.xml` |

## 30. `WriteFirestoreBrick`

| Поле | Значение |
|---|---|
| Название RU | Записать в Firestore |
| Название EN | Write to Firestore |
| Строки (`strings.xml` → `values-ru`) | `firestore_write`: EN="Write to Firestore" / RU="Записать в Firestore"; `firestore_base`: EN="FireStore ID" / RU="Айди базы FireStore"; `firestore_path`: EN="Path" / RU="Путь"; `firestore_value`: EN="Value (JSON)" / RU="Значение (JSON)" |
| Параметры | `FIRESTORE_BASE` (`brick_write_firestore_edit_base`); `FIRESTORE_PATH` (`brick_write_firestore_edit_path`); `FIRESTORE_VALUE` (`brick_write_firestore_edit_value`); спиннер (`brick_write_firestore_wait_spinner`) |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/WriteFirestoreBrick.java:36` |
| Класс экшена (`content/actions`) | `WriteFirestoreAction` (`catroid/src/main/java/org/catrobat/catroid/content/actions/WriteFirestoreAction.kt:31`; via `createWriteFirestoreAction`, `ActionFactory.java:2770`) |
| Описание (строго `BrickInfo.java` + код) | RU: Записывает документ Firestore по пути коллекция/документ. Значение — JSON-объект, число или строка.<br>EN: Writes a Firestore document at path collection/document. Value can be a JSON object, number or string. |
| Вызов в фабрике | `WriteFirestoreBrick("firebase_id", "users/user1", "{\"nick\":\"Tom\"}")` |
| Layout | `catroid/src/main/res/layout/brick_write_firestore.xml` |

## 31. `ReadFirestoreBrick`

| Поле | Значение |
|---|---|
| Название RU | Прочитать из Firestore |
| Название EN | Read from Firestore |
| Строки (`strings.xml` → `values-ru`) | `firestore_read`: EN="Read from Firestore" / RU="Прочитать из Firestore"; `firestore_base`: EN="FireStore ID" / RU="Айди базы FireStore"; `firestore_path`: EN="Path" / RU="Путь"; `firestore_store`: EN="and store in" / RU="и сохранить в" |
| Параметры | `FIRESTORE_BASE` (`brick_read_firestore_edit_base`); `FIRESTORE_PATH` (`brick_read_firestore_edit_path`); спиннер (`brick_read_firestore_wait_spinner`); спиннер (`brick_read_firestore_spinner`) |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/ReadFirestoreBrick.java:37` |
| Класс экшена (`content/actions`) | `ReadFirestoreAction` (`catroid/src/main/java/org/catrobat/catroid/content/actions/ReadFirestoreAction.kt:32`; via `createReadFirestoreAction`, `ActionFactory.java:2837`) |
| Описание (строго `BrickInfo.java` + код) | RU: Читает документ Firestore и сохраняет его содержимое (JSON) в переменную.<br>EN: Reads a Firestore document and stores its content (JSON) in a variable. |
| Вызов в фабрике | `ReadFirestoreBrick("firebase_id", "users/user1")` |
| Layout | `catroid/src/main/res/layout/brick_read_firestore.xml` |

## 32. `UpdateFirestoreBrick`

| Поле | Значение |
|---|---|
| Название RU | Обновить Firestore |
| Название EN | Update Firestore |
| Строки (`strings.xml` → `values-ru`) | `firestore_update`: EN="Update Firestore" / RU="Обновить Firestore"; `firestore_base`: EN="FireStore ID" / RU="Айди базы FireStore"; `firestore_path`: EN="Path" / RU="Путь"; `firestore_value`: EN="Value (JSON)" / RU="Значение (JSON)" |
| Параметры | `FIRESTORE_BASE` (`brick_update_firestore_edit_base`); `FIRESTORE_PATH` (`brick_update_firestore_edit_path`); `FIRESTORE_VALUE` (`brick_update_firestore_edit_value`); спиннер (`brick_update_firestore_wait_spinner`) |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/UpdateFirestoreBrick.java:36` |
| Класс экшена (`content/actions`) | `UpdateFirestoreAction` (`catroid/src/main/java/org/catrobat/catroid/content/actions/UpdateFirestoreAction.kt:31`; via `createUpdateFirestoreAction`, `ActionFactory.java:2792`) |
| Описание (строго `BrickInfo.java` + код) | RU: Обновляет поля документа Firestore (слияние с существующими полями).<br>EN: Updates Firestore document fields (merges with existing fields). |
| Вызов в фабрике | `UpdateFirestoreBrick("firebase_id", "users/user1", "{\"nick\":\"Tom\"}")` |
| Layout | `catroid/src/main/res/layout/brick_update_firestore.xml` |

## 33. `DeleteFirestoreBrick`

| Поле | Значение |
|---|---|
| Название RU | Удалить из Firestore |
| Название EN | Delete from Firestore |
| Строки (`strings.xml` → `values-ru`) | `firestore_delete`: EN="Delete from Firestore" / RU="Удалить из Firestore"; `firestore_base`: EN="FireStore ID" / RU="Айди базы FireStore"; `firestore_path`: EN="Path" / RU="Путь"; `firebase_wait_for_response`: EN="Wait for response" / RU="Ждать ответа" |
| Параметры | `FIRESTORE_BASE` (`brick_delete_firestore_edit_base`); `FIRESTORE_PATH` (`brick_delete_firestore_edit_path`); спиннер (`brick_delete_firestore_wait_spinner`) |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/DeleteFirestoreBrick.java:36` |
| Класс экшена (`content/actions`) | `DeleteFirestoreAction` (`catroid/src/main/java/org/catrobat/catroid/content/actions/DeleteFirestoreAction.kt:31`; via `createDeleteFirestoreAction`, `ActionFactory.java:2859`) |
| Описание (строго `BrickInfo.java` + код) | RU: Удаляет документ Firestore по пути коллекция/документ.<br>EN: Deletes a Firestore document at path collection/document. |
| Вызов в фабрике | `DeleteFirestoreBrick("firebase_id", "users/user1")` |
| Layout | `catroid/src/main/res/layout/brick_delete_firestore.xml` |

## 34. `QueryFirestoreBrick`

| Поле | Значение |
|---|---|
| Название RU | Запросить Firestore |
| Название EN | Query Firestore |
| Строки (`strings.xml` → `values-ru`) | `firestore_query`: EN="Query Firestore" / RU="Запросить Firestore"; `firestore_base`: EN="FireStore ID" / RU="Айди базы FireStore"; `firestore_collection`: EN="Collection" / RU="Коллекция"; `firestore_field`: EN="Field" / RU="Поле" |
| Параметры | `FIRESTORE_BASE` (`brick_query_firestore_edit_base`); `FIRESTORE_COLLECTION` (`brick_query_firestore_edit_collection`); `FIRESTORE_FIELD` (`brick_query_firestore_edit_field`); `FIRESTORE_VALUE` (`brick_query_firestore_edit_value`); `FIRESTORE_LIMIT` (`brick_query_firestore_edit_limit`); спиннер (`brick_query_firestore_operator_spinner`); спиннер (`brick_query_firestore_wait_spinner`); спиннер (`brick_query_firestore_spinner`) |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/QueryFirestoreBrick.java:37` |
| Класс экшена (`content/actions`) | `QueryFirestoreAction` (`catroid/src/main/java/org/catrobat/catroid/content/actions/QueryFirestoreAction.kt:32`; via `createQueryFirestoreAction`, `ActionFactory.java:2880`) |
| Описание (строго `BrickInfo.java` + код) | RU: Запрашивает документы коллекции по условию (поле, оператор, значение, лимит) и сохраняет результат (JSON-массив) в переменную.<br>EN: Queries collection documents by condition (field, operator, value, limit) and stores the result (JSON array) in a variable. |
| Вызов в фабрике | `QueryFirestoreBrick("firebase_id", "users", "score", "==", "10", "10")` |
| Layout | `catroid/src/main/res/layout/brick_query_firestore.xml` |

## 35. `AddFirestoreDocumentBrick`

| Поле | Значение |
|---|---|
| Название RU | Добавить документ Firestore |
| Название EN | Add Firestore document |
| Строки (`strings.xml` → `values-ru`) | `firestore_add`: EN="Add Firestore document" / RU="Добавить документ Firestore"; `firestore_base`: EN="FireStore ID" / RU="Айди базы FireStore"; `firestore_collection`: EN="Collection" / RU="Коллекция"; `firestore_value`: EN="Value (JSON)" / RU="Значение (JSON)" |
| Параметры | `FIRESTORE_BASE` (`brick_add_firestore_edit_base`); `FIRESTORE_COLLECTION` (`brick_add_firestore_edit_collection`); `FIRESTORE_VALUE` (`brick_add_firestore_edit_value`); спиннер (`brick_add_firestore_wait_spinner`); спиннер (`brick_add_firestore_spinner`) |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/AddFirestoreDocumentBrick.java:37` |
| Класс экшена (`content/actions`) | `AddFirestoreDocumentAction` (`catroid/src/main/java/org/catrobat/catroid/content/actions/AddFirestoreDocumentAction.kt:32`; via `createAddFirestoreDocumentAction`, `ActionFactory.java:2814`) |
| Описание (строго `BrickInfo.java` + код) | RU: Добавляет новый документ с авто-идентификатором в коллекцию Firestore и сохраняет его ID в переменную.<br>EN: Adds a new document with an auto ID to a Firestore collection and stores its ID in a variable. |
| Вызов в фабрике | `AddFirestoreDocumentBrick("firebase_id", "messages", "{\"text\":\"hello\"}")` |
| Layout | `catroid/src/main/res/layout/brick_add_firestore_document.xml` |

## 36. `UploadFileBrick`

| Поле | Значение |
|---|---|
| Название RU | Отправить файл на сервер |
| Название EN | Upload file to server |
| Строки (`strings.xml` → `values-ru`) | `brick_upload_file`: EN="Upload file to server" / RU="Отправить файл на сервер"; `brick_upload_url`: EN="URL" / RU="Ссылка"; `brick_upload_project_file`: EN="Project file path" / RU="Путь к файлу проекта"; `brick_upload_file_type`: EN="File type" / RU="Тип файла" |
| Параметры | `URL` (`brick_upload_url_edit`); `FILE` (`brick_upload_file_path_edit`); `TEXT` (`brick_upload_mime_type_edit`); спиннер (`brick_upload_file_type_spinner`); спиннер (`brick_upload_storage_type_spinner`) |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/UploadFileBrick.java:18` |
| Класс экшена (`content/actions`) | `UploadFileAction` (`catroid/src/main/java/org/catrobat/catroid/content/actions/UploadFileAction.java:22`; via `createUploadFileAction`, `ActionFactory.java:656`) |
| Описание (строго `BrickInfo.java` + код) | RU: Отправляет файл проекта на сервер<br>EN: Sends a project file to the server |
| Вызов в фабрике | `UploadFileBrick(Formula("https://")` |
| Layout | `catroid/src/main/res/layout/brick_upload_file.xml` |

## 37. `WebRequestBrick`

| Поле | Значение |
|---|---|
| Название RU | Отправить GET веб-запрос |
| Название EN | Send GET web request to |
| Строки (`strings.xml` → `values-ru`) | `brick_web_request`: EN="Send GET web request to" / RU="Отправить GET веб-запрос"; `brick_web_request_result`: EN="and store answer in" / RU="и сохранить ответ в" |
| Параметры | `WEB_REQUEST` (`brick_web_request_edit_text`); спиннер (`web_request_spinner`) |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/WebRequestBrick.kt:32` |
| Класс экшена (`content/actions`) | `WebRequestAction` (`catroid/src/main/java/org/catrobat/catroid/content/actions/WebRequestAction.kt:30`; via `createWebRequestAction`, `ActionFactory.java:4481`) |
| Описание (строго `BrickInfo.java` + код) | RU: Отправляет GET запрос на сайт и получает результат<br>EN: Sends a GET request to a website and receives the result |
| Вызов в фабрике | `WebRequestBrick(context.getString(R.string.brick_web_request_default_value)` |
| Layout | `catroid/src/main/res/layout/brick_web_request.xml` |

## 38. `PostWebRequestBrick`

| Поле | Значение |
|---|---|
| Название RU | Отправить POST веб-запрос на |
| Название EN | POST web request to |
| Строки (`strings.xml` → `values-ru`) | `post_web_request`: EN="POST web request to" / RU="Отправить POST веб-запрос на"; `post_web_request_header`: EN="with header" / RU="с заголовком (header)"; `post_web_request_body`: EN="and data" / RU="и содержанием (body)"; `post_web_request_var`: EN="and store response in" / RU="и сохранить ответ в" |
| Параметры | `URL` (`brick_post_web_request_edit_url`); `HEADER` (`brick_post_web_request_edit_header`); `BODY` (`brick_post_web_request_edit_body`); спиннер (`post_web_request_spinner`) |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/PostWebRequestBrick.java:31` |
| Класс экшена (`content/actions`) | `PostWebRequestAction` (`catroid/src/main/java/org/catrobat/catroid/content/actions/PostWebRequestAction.kt:55`; via `createPostWebRequestAction`, `ActionFactory.java:2036`) |
| Описание (строго `BrickInfo.java` + код) | RU: Отправляет POST запрос на сайт и получает результат ВАЖНО: не ждет окончания, продолжает выполнять код<br>EN: Sends a POST request to a website and receives the result IMPORTANT: does not wait for completion, continues executing code |
| Вызов в фабрике | `PostWebRequestBrick("https://api.calfire.com/v2/texts?limit=50&offset=200", "Content-Type:application/json", "{\nusername=password\n}")` |
| Layout | `catroid/src/main/res/layout/brick_post_web_request.xml` |

## 39. `WebSocketConnectBrick`

| Поле | Значение |
|---|---|
| Название RU | WebSocket подключиться |
| Название EN | WebSocket connect |
| Строки (`strings.xml` → `values-ru`) | `brick_websocket_connect`: EN="WebSocket connect" / RU="WebSocket подключиться"; `brick_websocket_connect_url`: EN="to URL" / RU="к URL" |
| Параметры | `WS_URL` (`brick_websocket_connect_url_edit`) |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/WebSocketConnectBrick.java:30` |
| Класс экшена (`content/actions`) | `WebSocketConnectAction` (`catroid/src/main/java/org/catrobat/catroid/content/actions/WebSocketConnectAction.kt:33`; via `createWebSocketConnectAction`, `ActionFactory.java:7567`) |
| Описание (строго `BrickInfo.java` + код) | RU: Подключается к WebSocket-серверу по указанному URL.<br>EN: Connects to a WebSocket server at the given URL. |
| Вызов в фабрике | `WebSocketConnectBrick("ws://echo.websocket.org")` |
| Layout | `catroid/src/main/res/layout/brick_websocket_connect.xml` |

## 40. `WebSocketSendBrick`

| Поле | Значение |
|---|---|
| Название RU | WebSocket отправить |
| Название EN | WebSocket send |
| Строки (`strings.xml` → `values-ru`) | `brick_websocket_send`: EN="WebSocket send" / RU="WebSocket отправить"; `brick_websocket_send_message`: EN="message" / RU="сообщение" |
| Параметры | `WS_MESSAGE` (`brick_websocket_send_message_edit`) |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/WebSocketSendBrick.java:30` |
| Класс экшена (`content/actions`) | `WebSocketSendAction` (`catroid/src/main/java/org/catrobat/catroid/content/actions/WebSocketSendAction.kt:31`; via `createWebSocketSendAction`, `ActionFactory.java:7575`) |
| Описание (строго `BrickInfo.java` + код) | RU: Отправляет сообщение через открытое WebSocket-соединение.<br>EN: Sends a message through the open WebSocket connection. |
| Вызов в фабрике | `WebSocketSendBrick("Hello!")` |
| Layout | `catroid/src/main/res/layout/brick_websocket_send.xml` |

## 41. `WebSocketReceiveBrick`

| Поле | Значение |
|---|---|
| Название RU | WebSocket получить |
| Название EN | WebSocket receive |
| Строки (`strings.xml` → `values-ru`) | `brick_websocket_receive`: EN="WebSocket receive" / RU="WebSocket получить"; `brick_websocket_receive_variable`: EN="store in" / RU="сохранить в" |
| Параметры | спиннер (`brick_websocket_receive_spinner`) |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/WebSocketReceiveBrick.java:30` |
| Класс экшена (`content/actions`) | `WebSocketReceiveAction` (`catroid/src/main/java/org/catrobat/catroid/content/actions/WebSocketReceiveAction.kt:30`; via `createWebSocketReceiveAction`, `ActionFactory.java:7583`) |
| Описание (строго `BrickInfo.java` + код) | RU: Сохраняет последнее полученное WebSocket-сообщение в выбранную переменную.<br>EN: Stores the latest received WebSocket message into the chosen variable. |
| Вызов в фабрике | `WebSocketReceiveBrick()` |
| Layout | `catroid/src/main/res/layout/brick_websocket_receive.xml` |

## 42. `WebSocketCloseBrick`

| Поле | Значение |
|---|---|
| Название RU | WebSocket закрыть |
| Название EN | WebSocket close |
| Строки (`strings.xml` → `values-ru`) | `brick_websocket_close`: EN="WebSocket close" / RU="WebSocket закрыть" |
| Параметры | — |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/WebSocketCloseBrick.java:29` |
| Класс экшена (`content/actions`) | `WebSocketCloseAction` (`catroid/src/main/java/org/catrobat/catroid/content/actions/WebSocketCloseAction.kt:29`; via `createWebSocketCloseAction`, `ActionFactory.java:7589`) |
| Описание (строго `BrickInfo.java` + код) | RU: Закрывает текущее WebSocket-соединение.<br>EN: Closes the current WebSocket connection. |
| Вызов в фабрике | `WebSocketCloseBrick()` |
| Layout | `catroid/src/main/res/layout/brick_websocket_close.xml` |

## 43. `DownloadFileBrick`

| Поле | Значение |
|---|---|
| Название RU | Скачать файл |
| Название EN | Download file |
| Строки (`strings.xml` → `values-ru`) | `brick_download_file`: EN="Download file" / RU="Скачать файл"; `brick_download_file_url`: EN="from URL" / RU="с URL"; `brick_download_file_name`: EN="save as" / RU="сохранить как" |
| Параметры | `URL` (`brick_download_file_url_edit`); `DOWNLOAD_FILENAME` (`brick_download_file_name_edit`) |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/DownloadFileBrick.java:30` |
| Класс экшена (`content/actions`) | `DownloadFileAction` (`catroid/src/main/java/org/catrobat/catroid/content/actions/DownloadFileAction.kt:38`; via `createDownloadFileAction`, `ActionFactory.java:7594`) |
| Описание (строго `BrickInfo.java` + код) | RU: Скачивает файл по URL и сохраняет его под указанным именем в загрузки проекта.<br>EN: Downloads a file from the URL and saves it under the given name in the project downloads. |
| Вызов в фабрике | `DownloadFileBrick("https://example.com/file.zip", "file.zip")` |
| Layout | `catroid/src/main/res/layout/brick_download_file.xml` |

## 44. `DownloadToPathBrick`

| Поле | Значение |
|---|---|
| Название RU | Скачать по пути |
| Название EN | Download to path |
| Строки (`strings.xml` → `values-ru`) | `brick_download_to_path`: EN="Download to path" / RU="Скачать по пути"; `brick_download_to_path_url`: EN="from URL" / RU="с URL"; `brick_download_to_path_label`: EN="to path" / RU="в путь" |
| Параметры | `URL` (`brick_download_to_path_url_edit`); `DOWNLOAD_PATH` (`brick_download_to_path_edit`) |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/DownloadToPathBrick.java:30` |
| Класс экшена (`content/actions`) | `DownloadToPathAction` (`catroid/src/main/java/org/catrobat/catroid/content/actions/DownloadToPathAction.kt:38`; via `createDownloadToPathAction`, `ActionFactory.java:7603`) |
| Описание (строго `BrickInfo.java` + код) | RU: Скачивает файл по URL в указанный путь.<br>EN: Downloads a file from the URL to the given path. |
| Вызов в фабрике | `DownloadToPathBrick("https://example.com/file.zip", "/storage/emulated/0/Download/file.zip")` |
| Layout | `catroid/src/main/res/layout/brick_download_to_path.xml` |

## 45. `CancelDownloadBrick`

| Поле | Значение |
|---|---|
| Название RU | Отменить скачивание |
| Название EN | Cancel download |
| Строки (`strings.xml` → `values-ru`) | `brick_cancel_download`: EN="Cancel download" / RU="Отменить скачивание" |
| Параметры | — |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/CancelDownloadBrick.java:29` |
| Класс экшена (`content/actions`) | `CancelDownloadAction` (`catroid/src/main/java/org/catrobat/catroid/content/actions/CancelDownloadAction.kt:28`; via `createCancelDownloadAction`, `ActionFactory.java:7612`) |
| Описание (строго `BrickInfo.java` + код) | RU: Отменяет текущую загрузку файла.<br>EN: Cancels the current file download. |
| Вызов в фабрике | `CancelDownloadBrick()` |
| Layout | `catroid/src/main/res/layout/brick_cancel_download.xml` |

## 46. `PingBrick`

| Поле | Значение |
|---|---|
| Название RU | Ping |
| Название EN | Ping |
| Строки (`strings.xml` → `values-ru`) | `brick_ping`: EN="Ping" / RU="Ping"; `brick_ping2`: EN="host" / RU="хост"; `brick_ping3`: EN="store result in" / RU="сохранить результат в" |
| Параметры | `PING_HOST` (`brick_ping_host_edit`); спиннер (`brick_ping_spinner`) |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/PingBrick.java:31` |
| Класс экшена (`content/actions`) | `PingAction` (`catroid/src/main/java/org/catrobat/catroid/content/actions/PingAction.kt:33`; via `createPingAction`, `ActionFactory.java:7617`) |
| Описание (строго `BrickInfo.java` + код) | RU: Проверяет доступность хоста (ping) и сохраняет результат ("true" или "false") в переменную.<br>EN: Checks whether a host is reachable (ping) and stores the result ("true" or "false") into a variable. |
| Вызов в фабрике | `PingBrick("8.8.8.8")` |
| Layout | `catroid/src/main/res/layout/brick_ping.xml` |

## 47. `HttpCreateBrick`

| Поле | Значение |
|---|---|
| Название RU | HTTP запрос |
| Название EN | HTTP request |
| Строки (`strings.xml` → `values-ru`) | `brick_http_create_title`: EN="HTTP request" / RU="HTTP запрос"; `brick_http_create_id`: EN="ID" / RU="ID"; `brick_http_create_method`: EN="method" / RU="метод"; `brick_http_create_url`: EN="URL" / RU="URL" |
| Параметры | `VALUE_1` (`brick_http_create_id_field`); `VALUE_2` (`brick_http_create_method_field`); `VALUE_3` (`brick_http_create_url_field`) |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/HttpCreateBrick.java:8` |
| Класс экшена (`content/actions`) | `HttpCreateAction` (`catroid/src/main/java/org/catrobat/catroid/content/actions/HttpCreateAction.kt:8`; via `createHttpCreateAction`, `ActionFactory.java:7955`) |
| Описание (строго `BrickInfo.java` + код) | RU: Создаёт HTTP-запрос с указанным методом и URL под заданным ID.<br>EN: Creates an HTTP request with the given method and URL under the specified ID. |
| Вызов в фабрике | `HttpCreateBrick("my_request", "GET", "https://api.example.com/data")` |
| Layout | `catroid/src/main/res/layout/brick_http_create.xml` |

## 48. `HttpConfigBrick`

| Поле | Значение |
|---|---|
| Название RU | Конфиг HTTP |
| Название EN | HTTP config |
| Строки (`strings.xml` → `values-ru`) | `brick_http_config_title`: EN="HTTP config" / RU="Конфиг HTTP"; `brick_http_config_id`: EN="ID" / RU="ID"; `brick_http_config_type`: EN="type" / RU="тип"; `brick_http_config_key`: EN="key" / RU="ключ" |
| Параметры | `VALUE_1` (`brick_http_config_id_field`); `VALUE_2` (`brick_http_config_key_field`); `VALUE_3` (`brick_http_config_value_field`); спиннер (`brick_http_config_type_spinner`) |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/HttpConfigBrick.java:14` |
| Класс экшена (`content/actions`) | `HttpConfigAction` (`catroid/src/main/java/org/catrobat/catroid/content/actions/HttpConfigAction.kt:8`; via `createHttpConfigAction`, `ActionFactory.java:7987`) |
| Описание (строго `BrickInfo.java` + код) | RU: Задаёт настройку HTTP-запроса: заголовок, параметр запроса, таймауты, cookies и др.<br>EN: Sets an HTTP request option: header, query parameter, timeouts, cookies, etc. |
| Вызов в фабрике | `HttpConfigBrick("my_request", 0, "User-Agent", "NewCatroidClient")` |
| Layout | `catroid/src/main/res/layout/brick_http_config.xml` |

## 49. `HttpBodyBrick`

| Поле | Значение |
|---|---|
| Название RU | Тело HTTP |
| Название EN | HTTP body |
| Строки (`strings.xml` → `values-ru`) | `brick_http_body_title`: EN="HTTP body" / RU="Тело HTTP"; `brick_http_body_id`: EN="ID" / RU="ID"; `brick_http_body_text`: EN="body" / RU="тело"; `brick_http_body_content_type`: EN="content type" / RU="тип контента" |
| Параметры | `VALUE_1` (`brick_http_body_id_field`); `VALUE_2` (`brick_http_body_text_field`); `VALUE_3` (`brick_http_body_content_type_field`) |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/HttpBodyBrick.java:8` |
| Класс экшена (`content/actions`) | `HttpBodyAction` (`catroid/src/main/java/org/catrobat/catroid/content/actions/HttpBodyAction.kt:8`; via `createHttpBodyAction`, `ActionFactory.java:7998`) |
| Описание (строго `BrickInfo.java` + код) | RU: Задаёт текст и тип содержимого HTTP-запроса.<br>EN: Sets the body text and content type of the HTTP request. |
| Вызов в фабрике | `HttpBodyBrick("my_request", "{\"text\": \"hello\"}", "application/json")` |
| Layout | `catroid/src/main/res/layout/brick_http_body.xml` |

## 50. `HttpAttachFileBrick`

| Поле | Значение |
|---|---|
| Название RU | Прикрепить файл HTTP |
| Название EN | HTTP attach file |
| Строки (`strings.xml` → `values-ru`) | `brick_http_attach_file_title`: EN="HTTP attach file" / RU="Прикрепить файл HTTP"; `brick_http_attach_file_id`: EN="ID" / RU="ID"; `brick_http_attach_file_name`: EN="name" / RU="имя"; `brick_http_attach_file_parameter`: EN="parameter" / RU="параметр" |
| Параметры | `VALUE_1` (`brick_http_attach_file_id_field`); `VALUE_2` (`brick_http_attach_file_name_field`); `VALUE_3` (`brick_http_attach_file_parameter_field`); `VALUE_4` (`brick_http_attach_file_mime_field`) |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/HttpAttachFileBrick.java:8` |
| Класс экшена (`content/actions`) | `HttpAttachFileAction` (`catroid/src/main/java/org/catrobat/catroid/content/actions/HttpAttachFileAction.kt:8`; via `createHttpAttachFileAction`, `ActionFactory.java:8008`) |
| Описание (строго `BrickInfo.java` + код) | RU: Прикрепляет файл проекта к HTTP-запросу как параметр (multipart) с указанным MIME-типом.<br>EN: Attaches a project file to the HTTP request as a multipart parameter with the given MIME type. |
| Вызов в фабрике | `HttpAttachFileBrick("my_request", "image.png", "file", "image/png")` |
| Layout | `catroid/src/main/res/layout/brick_http_attach_file.xml` |

## 51. `HttpSendBrick`

| Поле | Значение |
|---|---|
| Название RU | Отправить HTTP |
| Название EN | Send HTTP |
| Строки (`strings.xml` → `values-ru`) | `brick_http_send_title`: EN="Send HTTP" / RU="Отправить HTTP"; `brick_http_send_id`: EN="ID" / RU="ID" |
| Параметры | `VALUE_1` (`brick_http_send_id_field`) |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/HttpSendBrick.java:8` |
| Класс экшена (`content/actions`) | `HttpSendAction` (`catroid/src/main/java/org/catrobat/catroid/content/actions/HttpSendAction.kt:9`; via `createHttpSendAction`, `ActionFactory.java:7965`) |
| Описание (строго `BrickInfo.java` + код) | RU: Отправляет HTTP-запрос с указанным ID. ВАЖНО: ждёт завершения запроса.<br>EN: Sends the HTTP request with the given ID. IMPORTANT: waits for the request to finish. |
| Вызов в фабрике | `HttpSendBrick("my_request")` |
| Layout | `catroid/src/main/res/layout/brick_http_send.xml` |

## 52. `HttpSaveFileBrick`

| Поле | Значение |
|---|---|
| Название RU | Сохранить файл HTTP |
| Название EN | HTTP save file |
| Строки (`strings.xml` → `values-ru`) | `brick_http_save_file_title`: EN="HTTP save file" / RU="Сохранить файл HTTP"; `brick_http_save_file_id`: EN="ID" / RU="ID"; `brick_http_save_file_dest`: EN="destination" / RU="путь" |
| Параметры | `VALUE_1` (`brick_http_save_file_id_field`); `VALUE_2` (`brick_http_save_file_dest_field`) |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/HttpSaveFileBrick.java:8` |
| Класс экшена (`content/actions`) | `HttpSaveFileAction` (`catroid/src/main/java/org/catrobat/catroid/content/actions/HttpSaveFileAction.kt:8`; via `createHttpSaveFileAction`, `ActionFactory.java:8019`) |
| Описание (строго `BrickInfo.java` + код) | RU: Сохраняет ответ HTTP-запроса в указанный файл проекта.<br>EN: Saves the HTTP request response to the given project file. |
| Вызов в фабрике | `HttpSaveFileBrick("my_request", "downloaded_image.png")` |
| Layout | `catroid/src/main/res/layout/brick_http_save_file.xml` |

## 53. `HttpCancelBrick`

| Поле | Значение |
|---|---|
| Название RU | Отменить HTTP-запрос |
| Название EN | Cancel HTTP request |
| Строки (`strings.xml` → `values-ru`) | `brick_http_cancel`: EN="Cancel HTTP request" / RU="Отменить HTTP-запрос" |
| Параметры | `VALUE_1` (`brick_http_cancel_id_field`) |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/HttpCancelBrick.java:8` |
| Класс экшена (`content/actions`) | `HttpCancelAction` (`catroid/src/main/java/org/catrobat/catroid/content/actions/HttpCancelAction.kt:8`; via `createHttpCancelAction`, `ActionFactory.java:7980`) |
| Описание (строго `BrickInfo.java` + код) | RU: Отменяет выполняющийся HTTP-запрос по ID.<br>EN: Cancels an active HTTP request by ID. |
| Вызов в фабрике | `HttpCancelBrick("my_request")` |
| Layout | `catroid/src/main/res/layout/brick_http_cancel.xml` |

## 54. `HttpClearBrick`

| Поле | Значение |
|---|---|
| Название RU | Очистить HTTP-запрос |
| Название EN | Clear HTTP request |
| Строки (`strings.xml` → `values-ru`) | `brick_http_clear`: EN="Clear HTTP request" / RU="Очистить HTTP-запрос" |
| Параметры | `VALUE_1` (`brick_http_clear_id_field`) |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/HttpClearBrick.java:8` |
| Класс экшена (`content/actions`) | `HttpClearAction` (`catroid/src/main/java/org/catrobat/catroid/content/actions/HttpClearAction.kt:8`; via `createHttpClearAction`, `ActionFactory.java:7973`) |
| Описание (строго `BrickInfo.java` + код) | RU: Удаляет подготовленный HTTP-запрос и его ответ из памяти.<br>EN: Clears a prepared HTTP request and its response from memory. |
| Вызов в фабрике | `HttpClearBrick("my_request")` |
| Layout | `catroid/src/main/res/layout/brick_http_clear.xml` |

## 55. `MqttConnectBrick`

| Поле | Значение |
|---|---|
| Название RU | MQTT подключение |
| Название EN | MQTT connect |
| Строки (`strings.xml` → `values-ru`) | `brick_mqtt_connect_title`: EN="MQTT connect" / RU="MQTT подключение"; `brick_mqtt_connect_id`: EN="ID" / RU="ID"; `brick_mqtt_connect_host`: EN="host" / RU="хост"; `brick_mqtt_connect_port`: EN="port" / RU="порт" |
| Параметры | `VALUE_1` (`brick_mqtt_connect_id`); `VALUE_2` (`brick_mqtt_connect_host`); `VALUE_3` (`brick_mqtt_connect_port`) |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/MqttConnectBrick.java:8` |
| Класс экшена (`content/actions`) | `MqttConnectAction` (`catroid/src/main/java/org/catrobat/catroid/content/actions/MqttConnectAction.kt:9`; via `createMqttConnectAction`, `ActionFactory.java:8244`) |
| Описание (строго `BrickInfo.java` + код) | RU: Подключается к MQTT-брокеру под указанным ID клиента, хостом и портом. ВАЖНО: ждёт завершения подключения.<br>EN: Connects to the MQTT broker with the given client ID, host and port. IMPORTANT: waits until the connection is established. |
| Вызов в фабрике | `MqttConnectBrick("my_lobby", "broker.emqx.io", 1883)` |
| Layout | `catroid/src/main/res/layout/brick_mqtt_connect.xml` |

## 56. `MqttJoinRoomBrick`

| Поле | Значение |
|---|---|
| Название RU | MQTT войти в комнату |
| Название EN | MQTT join room |
| Строки (`strings.xml` → `values-ru`) | `brick_mqtt_join_room_title`: EN="MQTT join room" / RU="MQTT войти в комнату"; `brick_mqtt_join_room_client_id`: EN="client ID" / RU="ID клиента"; `brick_mqtt_join_room_id`: EN="room" / RU="комната"; `brick_mqtt_join_room_salt`: EN="salt" / RU="соль" |
| Параметры | `VALUE_1` (`brick_mqtt_join_room_client_id`); `VALUE_2` (`brick_mqtt_join_room_id`); `VALUE_3` (`brick_mqtt_join_room_salt`) |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/MqttJoinRoomBrick.java:8` |
| Класс экшена (`content/actions`) | `MqttJoinRoomAction` (`catroid/src/main/java/org/catrobat/catroid/content/actions/MqttJoinRoomAction.kt:9`; via `createMqttJoinRoomAction`, `ActionFactory.java:8258`) |
| Описание (строго `BrickInfo.java` + код) | RU: Входит в комнату (подписывается на её MQTT-топик). ВАЖНО: требуется уже установленное соединение.<br>EN: Joins a room (subscribes to its MQTT topic). IMPORTANT: a connection must already be established. |
| Вызов в фабрике | `MqttJoinRoomBrick("my_lobby", "room_1", "my_secret_game_salt")` |
| Layout | `catroid/src/main/res/layout/brick_mqtt_join_room.xml` |

## 57. `MqttPublishBrick`

| Поле | Значение |
|---|---|
| Название RU | MQTT публикация |
| Название EN | MQTT publish |
| Строки (`strings.xml` → `values-ru`) | `brick_mqtt_publish_title`: EN="MQTT publish" / RU="MQTT публикация"; `brick_mqtt_publish_client_id`: EN="client ID" / RU="ID клиента"; `brick_mqtt_publish_room_id`: EN="room" / RU="комната"; `brick_mqtt_publish_salt`: EN="salt" / RU="соль" |
| Параметры | `VALUE_1` (`brick_mqtt_publish_client_id`); `VALUE_2` (`brick_mqtt_publish_room_id`); `VALUE_3` (`brick_mqtt_publish_salt`); `VALUE_4` (`brick_mqtt_publish_message`) |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/MqttPublishBrick.java:8` |
| Класс экшена (`content/actions`) | `MqttPublishAction` (`catroid/src/main/java/org/catrobat/catroid/content/actions/MqttPublishAction.kt:8`; via `createMqttPublishAction`, `ActionFactory.java:8265`) |
| Описание (строго `BrickInfo.java` + код) | RU: Публикует сообщение в MQTT-комнату. ВАЖНО: не ждёт подтверждения отправки.<br>EN: Publishes a message to the MQTT room. IMPORTANT: does not wait for the send confirmation. |
| Вызов в фабрике | `MqttPublishBrick("my_lobby", "room_1", "my_secret_game_salt", "Hello!")` |
| Layout | `catroid/src/main/res/layout/brick_mqtt_publish.xml` |

## 58. `MqttDisconnectBrick`

| Поле | Значение |
|---|---|
| Название RU | MQTT отключение |
| Название EN | MQTT disconnect |
| Строки (`strings.xml` → `values-ru`) | `brick_mqtt_disconnect_title`: EN="MQTT disconnect" / RU="MQTT отключение" |
| Параметры | `VALUE_1` (`brick_mqtt_disconnect_client_id`) |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/MqttDisconnectBrick.java:8` |
| Класс экшена (`content/actions`) | `MqttDisconnectAction` (`catroid/src/main/java/org/catrobat/catroid/content/actions/MqttDisconnectAction.kt:8`; via `createMqttDisconnectAction`, `ActionFactory.java:8251`) |
| Описание (строго `BrickInfo.java` + код) | RU: Отключает MQTT-клиент с указанным ID.<br>EN: Disconnects the MQTT client with the given ID. |
| Вызов в фабрике | `MqttDisconnectBrick("my_lobby")` |
| Layout | `catroid/src/main/res/layout/brick_mqtt_disconnect.xml` |

## 59. `StartTcpServerNamedBrick`

| Поле | Значение |
|---|---|
| Название RU | Запустить TCP сервер |
| Название EN | Start TCP server |
| Строки (`strings.xml` → `values-ru`) | `brick_start_tcp_server_named`: EN="Start TCP server" / RU="Запустить TCP сервер"; `brick_start_tcp_server_named2`: EN="with port" / RU="с портом" |
| Параметры | `TEXT` (`brick_start_tcp_server_named_name_edit`); `PORT` (`brick_start_tcp_server_named_port_edit`) |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/StartTcpServerNamedBrick.java:8` |
| Класс экшена (`content/actions`) | `StartTcpServerNamedAction` (`catroid/src/main/java/org/catrobat/catroid/content/actions/StartTcpServerNamedAction.kt:8`; via `createStartTcpServerNamedAction`, `ActionFactory.java:3324`) |
| Описание (строго `BrickInfo.java` + код) | RU: Запускает именованный TCP сервер с портом<br>EN: Starts a named TCP server with port |
| Вызов в фабрике | `StartTcpServerNamedBrick("server", "1234")` |
| Layout | `catroid/src/main/res/layout/brick_start_tcp_server_named.xml` |

## 60. `StopTcpServerNamedBrick`

| Поле | Значение |
|---|---|
| Название RU | Остановить TCP сервер |
| Название EN | Stop TCP server |
| Строки (`strings.xml` → `values-ru`) | `brick_stop_tcp_server_named`: EN="Stop TCP server" / RU="Остановить TCP сервер" |
| Параметры | `TEXT` (`brick_stop_tcp_server_named_name_edit`) |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/StopTcpServerNamedBrick.java:8` |
| Класс экшена (`content/actions`) | `StopTcpServerNamedAction` (`catroid/src/main/java/org/catrobat/catroid/content/actions/StopTcpServerNamedAction.kt:8`; via `createStopTcpServerNamedAction`, `ActionFactory.java:3334`) |
| Описание (строго `BrickInfo.java` + код) | RU: Останавливает именованный TCP сервер<br>EN: Stops a named TCP server |
| Вызов в фабрике | `StopTcpServerNamedBrick("server")` |
| Layout | `catroid/src/main/res/layout/brick_stop_tcp_server_named.xml` |

## 61. `ConnectTcpServerNamedBrick`

| Поле | Значение |
|---|---|
| Название RU | Подключиться к TCP серверу |
| Название EN | Connect to TCP server |
| Строки (`strings.xml` → `values-ru`) | `brick_connect_tcp_server_named`: EN="Connect to TCP server" / RU="Подключиться к TCP серверу"; `brick_connect_server2`: EN="with ip" / RU="с IP"; `brick_connect_server3`: EN="and port" / RU="и портом" |
| Параметры | `TEXT` (`brick_connect_tcp_server_named_name_edit`); `IP` (`brick_connect_tcp_server_named_ip_edit`); `PORT` (`brick_connect_tcp_server_named_port_edit`) |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/ConnectTcpServerNamedBrick.java:8` |
| Класс экшена (`content/actions`) | `ConnectTcpServerNamedAction` (`catroid/src/main/java/org/catrobat/catroid/content/actions/ConnectTcpServerNamedAction.kt:8`; via `createConnectTcpServerNamedAction`, `ActionFactory.java:3343`) |
| Описание (строго `BrickInfo.java` + код) | RU: Подключается к TCP серверу под именем<br>EN: Connects to a TCP server under a name |
| Вызов в фабрике | `ConnectTcpServerNamedBrick("server", "127.0.0.1", "1234")` |
| Layout | `catroid/src/main/res/layout/brick_connect_tcp_server_named.xml` |

## 62. `DisconnectTcpServerNamedBrick`

| Поле | Значение |
|---|---|
| Название RU | Отключиться от TCP сервера |
| Название EN | Disconnect from TCP server |
| Строки (`strings.xml` → `values-ru`) | `brick_disconnect_tcp_server_named`: EN="Disconnect from TCP server" / RU="Отключиться от TCP сервера" |
| Параметры | `TEXT` (`brick_disconnect_tcp_server_named_name_edit`) |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/DisconnectTcpServerNamedBrick.java:8` |
| Класс экшена (`content/actions`) | `DisconnectTcpServerNamedAction` (`catroid/src/main/java/org/catrobat/catroid/content/actions/DisconnectTcpServerNamedAction.kt:8`; via `createDisconnectTcpServerNamedAction`, `ActionFactory.java:3354`) |
| Описание (строго `BrickInfo.java` + код) | RU: Отключается от именованного TCP сервера<br>EN: Disconnects from a named TCP server |
| Вызов в фабрике | `DisconnectTcpServerNamedBrick("server")` |
| Layout | `catroid/src/main/res/layout/brick_disconnect_tcp_server_named.xml` |

## 63. `ClearTcpVariableBrick`

| Поле | Значение |
|---|---|
| Название RU | Очистить TCP сообщение в |
| Название EN | Clear TCP message in |
| Строки (`strings.xml` → `values-ru`) | `brick_clear_tcp_variable`: EN="Clear TCP message in" / RU="Очистить TCP сообщение в" |
| Параметры | спиннер (`brick_clear_tcp_variable_spinner`) |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/ClearTcpVariableBrick.java:8` |
| Класс экшена (`content/actions`) | `ClearTcpVariableAction` (`catroid/src/main/java/org/catrobat/catroid/content/actions/ClearTcpVariableAction.kt:7`; via `createClearTcpVariableAction`, `ActionFactory.java:3363`) |
| Описание (строго `BrickInfo.java` + код) | RU: Очищает TCP сообщение из переменной<br>EN: Clears the TCP message stored in a variable |
| Вызов в фабрике | `ClearTcpVariableBrick()` |
| Layout | `catroid/src/main/res/layout/brick_clear_tcp_variable.xml` |

## 64. `TcpPingBrick`

| Поле | Значение |
|---|---|
| Название RU | PING-PONG TCP сервера |
| Название EN | PING-PONG TCP server |
| Строки (`strings.xml` → `values-ru`) | `brick_tcp_ping`: EN="PING-PONG TCP server" / RU="PING-PONG TCP сервера"; `brick_tcp_ping_timeout`: EN="wait reply (ms)" / RU="ждать ответ (мс)"; `brick_tcp_ping2`: EN="store answer in" / RU="сохранить ответ в" |
| Параметры | `IP` (`brick_tcp_ping_ip_edit`); `PORT` (`brick_tcp_ping_port_edit`); `VALUE` (`brick_tcp_ping_timeout_edit`); спиннер (`brick_tcp_ping_spinner`) |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/TcpPingBrick.java:9` |
| Класс экшена (`content/actions`) | `TcpPingAction` (`catroid/src/main/java/org/catrobat/catroid/content/actions/TcpPingAction.kt:11`; via `createTcpPingAction`, `ActionFactory.java:3372`) |
| Описание (строго `BrickInfo.java` + код) | RU: PING-PONG TCP сервера по IP и порту, ответ true/false в переменную<br>EN: PING-PONG of a TCP server by IP and port, true/false answer into a variable |
| Вызов в фабрике | `TcpPingBrick("127.0.0.1", "1234")` |
| Layout | `catroid/src/main/res/layout/brick_tcp_ping.xml` |

## 65. `TcpPingNamedBrick`

| Поле | Значение |
|---|---|
| Название RU | PING-PONG TCP сервера |
| Название EN | PING-PONG TCP server |
| Строки (`strings.xml` → `values-ru`) | `brick_tcp_ping`: EN="PING-PONG TCP server" / RU="PING-PONG TCP сервера"; `brick_tcp_ping_timeout`: EN="wait reply (ms)" / RU="ждать ответ (мс)"; `brick_tcp_ping2`: EN="store answer in" / RU="сохранить ответ в" |
| Параметры | `TEXT` (`brick_tcp_ping_named_name_edit`); `VALUE` (`brick_tcp_ping_named_timeout_edit`); спиннер (`brick_tcp_ping_named_spinner`) |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/TcpPingNamedBrick.java:9` |
| Класс экшена (`content/actions`) | `TcpPingAction` (`catroid/src/main/java/org/catrobat/catroid/content/actions/TcpPingAction.kt:11`; via `createTcpPingAction`, `ActionFactory.java:3372`) |
| Описание (строго `BrickInfo.java` + код) | RU: PING-PONG именованного TCP сервера, ответ true/false в переменную<br>EN: PING-PONG of a named TCP server, true/false answer into a variable |
| Вызов в фабрике | `TcpPingNamedBrick("server")` |
| Layout | `catroid/src/main/res/layout/brick_tcp_ping_named.xml` |

## 66. `TcpMeasurePingBrick`

| Поле | Значение |
|---|---|
| Название RU | Измерить PING-PONG TCP сервера |
| Название EN | Measure PING-PONG of TCP server |
| Строки (`strings.xml` → `values-ru`) | `brick_tcp_measure_ping`: EN="Measure PING-PONG of TCP server" / RU="Измерить PING-PONG TCP сервера"; `brick_tcp_ping_timeout`: EN="wait reply (ms)" / RU="ждать ответ (мс)"; `brick_tcp_measure_ping2`: EN="store value in" / RU="сохранить значение в" |
| Параметры | `IP` (`brick_tcp_measure_ping_ip_edit`); `PORT` (`brick_tcp_measure_ping_port_edit`); `VALUE` (`brick_tcp_measure_ping_timeout_edit`); спиннер (`brick_tcp_measure_ping_spinner`) |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/TcpMeasurePingBrick.java:9` |
| Класс экшена (`content/actions`) | `TcpPingAction` (`catroid/src/main/java/org/catrobat/catroid/content/actions/TcpPingAction.kt:11`; via `createTcpPingAction`, `ActionFactory.java:3372`) |
| Описание (строго `BrickInfo.java` + код) | RU: Измеряет PING-PONG TCP сервера по IP и порту в мс в переменную<br>EN: Measures PING-PONG of a TCP server by IP and port in ms into a variable |
| Вызов в фабрике | `TcpMeasurePingBrick("127.0.0.1", "1234")` |
| Layout | `catroid/src/main/res/layout/brick_tcp_measure_ping.xml` |

## 67. `TcpMeasurePingNamedBrick`

| Поле | Значение |
|---|---|
| Название RU | Измерить PING-PONG TCP сервера |
| Название EN | Measure PING-PONG of TCP server |
| Строки (`strings.xml` → `values-ru`) | `brick_tcp_measure_ping`: EN="Measure PING-PONG of TCP server" / RU="Измерить PING-PONG TCP сервера"; `brick_tcp_ping_timeout`: EN="wait reply (ms)" / RU="ждать ответ (мс)"; `brick_tcp_measure_ping2`: EN="store value in" / RU="сохранить значение в" |
| Параметры | `TEXT` (`brick_tcp_measure_ping_named_name_edit`); `VALUE` (`brick_tcp_measure_ping_named_timeout_edit`); спиннер (`brick_tcp_measure_ping_named_spinner`) |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/TcpMeasurePingNamedBrick.java:9` |
| Класс экшена (`content/actions`) | `TcpPingAction` (`catroid/src/main/java/org/catrobat/catroid/content/actions/TcpPingAction.kt:11`; via `createTcpPingAction`, `ActionFactory.java:3372`) |
| Описание (строго `BrickInfo.java` + код) | RU: Измеряет PING-PONG именованного TCP сервера в мс в переменную<br>EN: Measures PING-PONG of a named TCP server in ms into a variable |
| Вызов в фабрике | `TcpMeasurePingNamedBrick("server")` |
| Layout | `catroid/src/main/res/layout/brick_tcp_measure_ping_named.xml` |

## 68. `StartUdpBrick`

| Поле | Значение |
|---|---|
| Название RU | Запустить UDP точку |
| Название EN | Start UDP endpoint |
| Строки (`strings.xml` → `values-ru`) | `brick_start_udp`: EN="Start UDP endpoint" / RU="Запустить UDP точку"; `brick_start_udp2`: EN="with port" / RU="с портом" |
| Параметры | `TEXT` (`brick_start_udp_name_edit`); `PORT` (`brick_start_udp_port_edit`) |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/StartUdpBrick.java:8` |
| Класс экшена (`content/actions`) | `StartUdpAction` (`catroid/src/main/java/org/catrobat/catroid/content/actions/StartUdpAction.kt:8`; via `createStartUdpAction`, `ActionFactory.java:3387`) |
| Описание (строго `BrickInfo.java` + код) | RU: Запускает UDP точку с портом<br>EN: Starts a UDP endpoint with port |
| Вызов в фабрике | `StartUdpBrick("udp", "1234")` |
| Layout | `catroid/src/main/res/layout/brick_start_udp.xml` |

## 69. `StopUdpBrick`

| Поле | Значение |
|---|---|
| Название RU | Остановить UDP точку |
| Название EN | Stop UDP endpoint |
| Строки (`strings.xml` → `values-ru`) | `brick_stop_udp`: EN="Stop UDP endpoint" / RU="Остановить UDP точку" |
| Параметры | `TEXT` (`brick_stop_udp_name_edit`) |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/StopUdpBrick.java:8` |
| Класс экшена (`content/actions`) | `StopUdpAction` (`catroid/src/main/java/org/catrobat/catroid/content/actions/StopUdpAction.kt:8`; via `createStopUdpAction`, `ActionFactory.java:3397`) |
| Описание (строго `BrickInfo.java` + код) | RU: Останавливает UDP точку<br>EN: Stops a UDP endpoint |
| Вызов в фабрике | `StopUdpBrick("udp")` |
| Layout | `catroid/src/main/res/layout/brick_stop_udp.xml` |

## 70. `ConnectUdpBrick`

| Поле | Значение |
|---|---|
| Название RU | Подключить UDP |
| Название EN | Connect UDP |
| Строки (`strings.xml` → `values-ru`) | `brick_connect_udp`: EN="Connect UDP" / RU="Подключить UDP"; `brick_connect_server2`: EN="with ip" / RU="с IP"; `brick_connect_server3`: EN="and port" / RU="и портом" |
| Параметры | `TEXT` (`brick_connect_udp_name_edit`); `IP` (`brick_connect_udp_ip_edit`); `PORT` (`brick_connect_udp_port_edit`) |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/ConnectUdpBrick.java:8` |
| Класс экшена (`content/actions`) | `ConnectUdpAction` (`catroid/src/main/java/org/catrobat/catroid/content/actions/ConnectUdpAction.kt:8`; via `createConnectUdpAction`, `ActionFactory.java:3406`) |
| Описание (строго `BrickInfo.java` + код) | RU: Запоминает UDP адрес под именем<br>EN: Remembers a UDP peer address under a name |
| Вызов в фабрике | `ConnectUdpBrick("udp", "127.0.0.1", "1234")` |
| Layout | `catroid/src/main/res/layout/brick_connect_udp.xml` |

## 71. `DisconnectUdpBrick`

| Поле | Значение |
|---|---|
| Название RU | Отключить UDP |
| Название EN | Disconnect UDP |
| Строки (`strings.xml` → `values-ru`) | `brick_disconnect_udp`: EN="Disconnect UDP" / RU="Отключить UDP" |
| Параметры | `TEXT` (`brick_disconnect_udp_name_edit`) |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/DisconnectUdpBrick.java:8` |
| Класс экшена (`content/actions`) | `DisconnectUdpAction` (`catroid/src/main/java/org/catrobat/catroid/content/actions/DisconnectUdpAction.kt:8`; via `createDisconnectUdpAction`, `ActionFactory.java:3417`) |
| Описание (строго `BrickInfo.java` + код) | RU: Забывает запомненный UDP адрес<br>EN: Forgets the remembered UDP peer address |
| Вызов в фабрике | `DisconnectUdpBrick("udp")` |
| Layout | `catroid/src/main/res/layout/brick_disconnect_udp.xml` |

## 72. `SendUdpBrick`

| Поле | Значение |
|---|---|
| Название RU | Отправить UDP |
| Название EN | Send UDP |
| Строки (`strings.xml` → `values-ru`) | `brick_send_udp`: EN="Send UDP" / RU="Отправить UDP"; `brick_tcp_server_name`: EN="server" / RU="сервер" |
| Параметры | `TEXT` (`brick_send_udp_name_edit`); `IP` (`brick_send_udp_ip_edit`); `PORT` (`brick_send_udp_port_edit`); `VALUE` (`brick_send_udp_edit`); `VALUE_2` (`brick_send_udp_edit2`); `VALUE_3` (`brick_send_udp_edit3`); `VALUE_4` (`brick_send_udp_edit4`); `VALUE_5` (`brick_send_udp_edit5`); `VALUE_6` (`brick_send_udp_edit6`); `VALUE_7` (`brick_send_udp_edit7`); `VALUE_8` (`brick_send_udp_edit8`); `VALUE_9` (`brick_send_udp_edit9`); `VALUE_10` (`brick_send_udp_edit10`); `VALUE_11` (`brick_send_udp_edit11`); `VALUE_12` (`brick_send_udp_edit12`); спиннер (`brick_send_udp_mode_spinner`) |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/SendUdpBrick.java:14` |
| Класс экшена (`content/actions`) | `SendUdpAction` (`catroid/src/main/java/org/catrobat/catroid/content/actions/SendUdpAction.kt:9`; via `createSendUdpAction`, `ActionFactory.java:3426`) |
| Описание (строго `BrickInfo.java` + код) | RU: Отправляет значения UDP пакетом запомненному адресату или своему адресу<br>EN: Sends values as a UDP packet to the remembered peer or a custom address |
| Вызов в фабрике | `SendUdpBrick("Hi")` |
| Layout | `catroid/src/main/res/layout/brick_send_udp.xml` |

## 73. `ListenUdpBrick`

| Поле | Значение |
|---|---|
| Название RU | Слушать UDP и сохранить в |
| Название EN | Listen UDP and store |
| Строки (`strings.xml` → `values-ru`) | `brick_listen_udp`: EN="Listen UDP and store" / RU="Слушать UDP и сохранить в"; `brick_tcp_server_name`: EN="server" / RU="сервер"; `new_option`: EN="new…" / RU="новое…"; `variable_locked_enter_password`: EN="Enter variable password" / RU="Введите пароль переменной" |
| Параметры | `TEXT` (`listen_udp_name_edit`); спиннер (`listen_udp_mode_spinner`); спиннер (`listen_udp_spinner1`); спиннер (`listen_udp_spinner2`); спиннер (`listen_udp_spinner3`); спиннер (`listen_udp_spinner4`); спиннер (`listen_udp_spinner5`); спиннер (`listen_udp_spinner6`); спиннер (`listen_udp_spinner7`); спиннер (`listen_udp_spinner8`); спиннер (`listen_udp_spinner9`); спиннер (`listen_udp_spinner10`); спиннер (`listen_udp_spinner11`); спиннер (`listen_udp_spinner12`) |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/ListenUdpBrick.java:29` |
| Класс экшена (`content/actions`) | `ListenUdpAction` (`catroid/src/main/java/org/catrobat/catroid/content/actions/ListenUdpAction.kt:14`; via `createListenUdpAction`, `ActionFactory.java:3440`) |
| Описание (строго `BrickInfo.java` + код) | RU: Слушает UDP пакеты и сохраняет значения в переменные<br>EN: Listens for UDP packets and stores values into variables |
| Вызов в фабрике | `ListenUdpBrick()` |
| Layout | `catroid/src/main/res/layout/brick_listen_udp.xml` |

## 74. `WhenUdpMessageBrick`

| Поле | Значение |
|---|---|
| Название RU | Когда получено UDP сообщение |
| Название EN | When UDP message received |
| Строки (`strings.xml` → `values-ru`) | `brick_when_udp_message`: EN="When UDP message received" / RU="Когда получено UDP сообщение"; `brick_tcp_server_name`: EN="server" / RU="сервер"; `new_option`: EN="new…" / RU="новое…"; `variable_locked_enter_password`: EN="Enter variable password" / RU="Введите пароль переменной" |
| Параметры | `TEXT` (`brick_when_udp_message_name_edit`); спиннер (`brick_when_udp_message_spinner`); спиннер (`brick_when_udp_message_spinner2`); спиннер (`brick_when_udp_message_spinner3`); спиннер (`brick_when_udp_message_spinner4`); спиннер (`brick_when_udp_message_spinner5`); спиннер (`brick_when_udp_message_spinner6`); спиннер (`brick_when_udp_message_spinner7`); спиннер (`brick_when_udp_message_spinner8`); спиннер (`brick_when_udp_message_spinner9`); спиннер (`brick_when_udp_message_spinner10`); спиннер (`brick_when_udp_message_spinner11`); спиннер (`brick_when_udp_message_spinner12`) |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/WhenUdpMessageBrick.java:32` |
| Класс экшена (`content/actions`) | — (событийный брик/скрипт: `addActionToSequence` пуст, `content/actions` не используется) |
| Описание (строго `BrickInfo.java` + код) | RU: Вызывается, когда получен UDP пакет, и сохраняет его в переменные<br>EN: Triggered when a UDP packet is received and stores it into variables |
| Вызов в фабрике | `WhenUdpMessageBrick()` |
| Layout | `catroid/src/main/res/layout/brick_when_udp_message.xml` |
