# Neural Networks / Нейросети

Ключ категории: `category_neural` (`res/values/strings.xml`: "Neural Networks"; `res/values-ru/strings.xml`: "Нейросети").
Состав и порядок — строго по `CategoryBricksFactory.kt` → `setupNeuralCategoryList` (заголовки `SubCategoryHeaderBrick` не являются блоками и пропущены).
Примечание (факт кода): `getPreferenceKeyForCategory` не возвращает ключ для Neural, поэтому ветка `!isGroupingEnabled` с `AskAIBrick`/`AskAIVisionBrick` недостижима; ниже — активный сгруппированный список.

Блоков: 16.

## 1. `SetGeminiKeyBrick`

| Поле | Значение |
|---|---|
| Название RU | Задать API ключ (Gemini) |
| Название EN | Set API Key (Gemini) |
| Строки (`strings.xml` → `values-ru`) | `brick_set_gemini_key`: EN="Set API Key (Gemini)" / RU="Задать API ключ (Gemini)" |
| Параметры | `KEY` (`brick_set_gemini_key_edit_text`) |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/SetGeminiKeyBrick.java:33` |
| Класс экшена (`content/actions`) | `SetGeminiKeyAction` (`catroid/src/main/java/org/catrobat/catroid/content/actions/SetGeminiKeyAction.kt:17`; via `createSetGeminiKey`, `ActionFactory.java:3534`) |
| Описание (строго `BrickInfo.java` + код) | RU: Задает персональный API ключ Gemini. Получить его можно в Google AI Studio.<br>EN: Sets a personal Gemini API key. You can get it from Google AI Studio. |
| Вызов в фабрике | `SetGeminiKeyBrick("api_key")` |
| Layout | `catroid/src/main/res/layout/brick_set_gemini_key.xml` |

## 2. `AskGeminiBrick`

| Поле | Значение |
|---|---|
| Название RU | Спросить у Gemini |
| Название EN | Ask Gemini |
| Строки (`strings.xml` → `values-ru`) | `brick_gemini_request`: EN="Ask Gemini" / RU="Спросить у Gemini"; `brick_gemini_request2`: EN="and save answer to" / RU="и сохранить ответ в" |
| Параметры | `QUESTION` (`brick_ask_gemini_edit_url`); спиннер (`brick_ask_gemini_spinner`) |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/AskGeminiBrick.java:33` |
| Класс экшена (`content/actions`) | `AskGeminiAction` (`catroid/src/main/java/org/catrobat/catroid/content/actions/AskGeminiAction.kt:75`; via `createAskGeminiAction`, `ActionFactory.java:2117`) |
| Описание (строго `BrickInfo.java` + код) | RU: Задает вопрос к Gemini. ВАЖНО: не ждет ответа, код выполняется дальше.<br>EN: Sends a question to Gemini. IMPORTANT: does not wait for an answer, code continues executing. |
| Вызов в фабрике | `AskGeminiBrick("Hello!")` |
| Layout | `catroid/src/main/res/layout/brick_ask_gemini.xml` |

## 3. `AskGemini2Brick`

| Поле | Значение |
|---|---|
| Название RU | Спросить у Gemini |
| Название EN | Ask Gemini |
| Строки (`strings.xml` → `values-ru`) | `brick_gemini_request`: EN="Ask Gemini" / RU="Спросить у Gemini"; `gemini_model`: EN="model" / RU="модель"; `brick_gemini_request2`: EN="and save answer to" / RU="и сохранить ответ в" |
| Параметры | `QUESTION` (`brick_ask_gemini2_edit_url`); `MODEL` (`brick_ask_gemini2_edit_model`); спиннер (`brick_ask_gemini2_spinner`) |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/AskGemini2Brick.java:33` |
| Класс экшена (`content/actions`) | `AskGemini2Action` (`catroid/src/main/java/org/catrobat/catroid/content/actions/AskGemini2Action.kt:59`; via `createAskGemini2Action`, `ActionFactory.java:2127`) |
| Описание (строго `BrickInfo.java` + код) | RU: Задает вопрос к определенной модели Gemini. ВАЖНО: не ждет ответа, код выполняется дальше.<br>EN: Sends a question to a specific Gemini model. IMPORTANT: does not wait for an answer, code continues executing. |
| Вызов в фабрике | `AskGemini2Brick("Hello! How are you?", "models/gemini-2.5-flash")` |
| Layout | `catroid/src/main/res/layout/brick_ask_gemini2.xml` |

## 4. `AskGPTBrick`

| Поле | Значение |
|---|---|
| Название RU | Спросить ChatGPT |
| Название EN | Ask ChatGPT |
| Строки (`strings.xml` → `values-ru`) | `brick_ask_gpt`: EN="Ask ChatGPT" / RU="Спросить ChatGPT"; `brick_ask_gpt2`: EN="System prompt" / RU="Системный промпт"; `brick_gemini_request2`: EN="and save answer to" / RU="и сохранить ответ в" |
| Параметры | `TEXT` (`brick_ask_gpt_edit_prompt`); `BODY` (`brick_ask_gpt_edit_system`); спиннер (`brick_ask_gpt_spinner`) |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/AskGPTBrick.kt:32` |
| Класс экшена (`content/actions`) | `AskGPTAction` (`catroid/src/main/java/org/catrobat/catroid/content/actions/AskGPTAction.kt:30`; via `createAskGPTAction`, `ActionFactory.java:4491`) |
| Описание (строго `BrickInfo.java` + код) | RU: Задает вопрос к ChatGPT (через Pollinations.Ai).<br>EN: Sends a question to ChatGPT (via Pollinations.Ai). |
| Вызов в фабрике | `AskGPTBrick("Привет!", "Отвечай на все словом \"апельсин\"")` |
| Layout | `catroid/src/main/res/layout/brick_ask_gpt.xml` |

## 5. `LoadNNBrick`

| Поле | Значение |
|---|---|
| Название RU | Загрузить .onnx модель из файла проекта |
| Название EN | Load .onnx model from project file |
| Строки (`strings.xml` → `values-ru`) | `brick_load_nn`: EN="Load .onnx model from project file" / RU="Загрузить .onnx модель из файла проекта" |
| Параметры | `FILE` (`brick_load_nn_edit`) |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/LoadNNBrick.java:31` |
| Класс экшена (`content/actions`) | `LoadNNAction` (`catroid/src/main/java/org/catrobat/catroid/content/actions/LoadNNAction.kt:42`; via `createLoadNNAction`, `ActionFactory.java:2343`) |
| Описание (строго `BrickInfo.java` + код) | RU: Загружает .onnx модель нейросети. ВАЖНО: во избежании утечек памяти - выгружайте ее при выходе.<br>EN: Loads a .onnx neural network model. IMPORTANT: to avoid memory leaks, unload it on exit. |
| Вызов в фабрике | `LoadNNBrick("model.onnx")` |
| Layout | `catroid/src/main/res/layout/brick_load_nn.xml` |

## 6. `PredictNNBrick`

| Поле | Значение |
|---|---|
| Название RU | Предсказать .onnx |
| Название EN | Predict .onnx model |
| Строки (`strings.xml` → `values-ru`) | `brick_predict_nn`: EN="Predict .onnx model" / RU="Предсказать .onnx"; `brick_predict_nn2`: EN="FloatArray" / RU="вход (FloatArray)"; `brick_predict_nn3`: EN="and store result in" / RU="и сохранить результат в" |
| Параметры | `VALUE` (`brick_predict_nn_edit`); спиннер (`predict_nn_spinner`) |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/PredictNNBrick.java:33` |
| Класс экшена (`content/actions`) | `PredictNNAction` (`catroid/src/main/java/org/catrobat/catroid/content/actions/PredictNNAction.kt:14`; via `createPredictNNAction`, `ActionFactory.java:2359`) |
| Описание (строго `BrickInfo.java` + код) | RU: Принимает на вход FloatArray соответствующий размерам тензора, который принимает модель. Возвращает все значения выходного слоя и самое большое из них.<br>EN: Takes a FloatArray matching the tensor dimensions expected by the model as input. Returns all output layer values and the largest one among them. |
| Вызов в фабрике | `PredictNNBrick("FloatArray")` |
| Layout | `catroid/src/main/res/layout/brick_predict_nn.xml` |

## 7. `UnloadNNBrick`

| Поле | Значение |
|---|---|
| Название RU | Выгрузить .onnx модель |
| Название EN | Unload .onnx model |
| Строки (`strings.xml` → `values-ru`) | `brick_unload_nn`: EN="Unload .onnx model" / RU="Выгрузить .onnx модель" |
| Параметры | — |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/UnloadNNBrick.java:30` |
| Класс экшена (`content/actions`) | `UnloadNNAction` (`catroid/src/main/java/org/catrobat/catroid/content/actions/UnloadNNAction.kt:42`; via `createUnloadNNAction`, `ActionFactory.java:2352`) |
| Описание (строго `BrickInfo.java` + код) | RU: Выгружает модель, освобождая память. ВАЖНО: вызывайте после загрузки модели, когда она уже не нужна.<br>EN: Unloads the model, freeing memory. IMPORTANT: call after loading the model when it is no longer needed. |
| Вызов в фабрике | `UnloadNNBrick()` |
| Layout | `catroid/src/main/res/layout/brick_unload_nn.xml` |

## 8. `CreateFloatBrick`

| Поле | Значение |
|---|---|
| Название RU | Задать FloatArray с названием |
| Название EN | Set FloatArray with name |
| Строки (`strings.xml` → `values-ru`) | `brick_create_float`: EN="Set FloatArray with name" / RU="Задать FloatArray с названием" |
| Параметры | `FLOAT_ARRAY` (`brick_create_float_edit`) |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/CreateFloatBrick.java:31` |
| Класс экшена (`content/actions`) | `CreateFloatAction` (`catroid/src/main/java/org/catrobat/catroid/content/actions/CreateFloatAction.kt:42`; via `createFloat`, `ActionFactory.java:2965`) |
| Описание (строго `BrickInfo.java` + код) | RU: Создает FloatArray в памяти устройства.<br>EN: Creates a FloatArray in device memory. |
| Вызов в фабрике | `CreateFloatBrick("FloatArray")` |
| Layout | `catroid/src/main/res/layout/brick_create_float.xml` |

## 9. `PutFloatBrick`

| Поле | Значение |
|---|---|
| Название RU | Вставить |
| Название EN | Insert |
| Строки (`strings.xml` → `values-ru`) | `brick_put_float`: EN="Insert" / RU="Вставить"; `brick_put_float2`: EN="to FloatArray" / RU="во FloatArray"; `brick_put_float3`: EN="with index" / RU="под индексом" |
| Параметры | `FLOAT_ARRAY` (`brick_put_float_float`); `VALUE` (`brick_put_float_value`); `LOOK_INDEX` (`brick_put_float_index`) |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/PutFloatBrick.java:31` |
| Класс экшена (`content/actions`) | `PutFloatAction` (`catroid/src/main/java/org/catrobat/catroid/content/actions/PutFloatAction.kt:42`; via `createPutFloat`, `ActionFactory.java:2984`) |
| Описание (строго `BrickInfo.java` + код) | RU: Вставляет значение во FloatArray.<br>EN: Inserts a value into a FloatArray. |
| Вызов в фабрике | `PutFloatBrick("FloatArray", 1, 0)` |
| Layout | `catroid/src/main/res/layout/brick_put_float.xml` |

## 10. `TableToFloatBrick`

| Поле | Значение |
|---|---|
| Название RU | Добавить таблицу |
| Название EN | Add Table |
| Строки (`strings.xml` → `values-ru`) | `brick_table_to_float`: EN="Add Table" / RU="Добавить таблицу"; `brick_table_to_float2`: EN="To FloatArray" / RU="во FloatArray" |
| Параметры | `TABLE_NAME` (`brick_table_to_float_table`); `FLOAT_ARRAY` (`brick_table_to_float_float`) |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/TableToFloatBrick.java:31` |
| Класс экшена (`content/actions`) | `TableToFloatAction` (`catroid/src/main/java/org/catrobat/catroid/content/actions/TableToFloatAction.kt:42`; via `createTableToFloat`, `ActionFactory.java:2974`) |
| Описание (строго `BrickInfo.java` + код) | RU: Добавляет всю таблицу во FloatArray.<br>EN: Adds an entire table into a FloatArray. |
| Вызов в фабрике | `TableToFloatBrick("myTable", "FloatArray")` |
| Layout | `catroid/src/main/res/layout/brick_table_to_float.xml` |

## 11. `DeleteFloatBrick`

| Поле | Значение |
|---|---|
| Название RU | Удалить элемент с индексом |
| Название EN | Delete element at index |
| Строки (`strings.xml` → `values-ru`) | `brick_delete_float`: EN="Delete element at index" / RU="Удалить элемент с индексом"; `brick_delete_float2`: EN="from FloatArray" / RU="из FloatArray" |
| Параметры | `LOOK_INDEX` (`brick_delete_float_index`); `FLOAT_ARRAY` (`brick_delete_float_float`) |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/DeleteFloatBrick.java:31` |
| Класс экшена (`content/actions`) | `DeleteFloatAction` (`catroid/src/main/java/org/catrobat/catroid/content/actions/DeleteFloatAction.kt:42`; via `createDeleteFloat`, `ActionFactory.java:2995`) |
| Описание (строго `BrickInfo.java` + код) | RU: Удаляет элемент из FloatArray.<br>EN: Deletes an element from a FloatArray. |
| Вызов в фабрике | `DeleteFloatBrick("FloatArray", 0)` |
| Layout | `catroid/src/main/res/layout/brick_delete_float.xml` |

## 12. `ResizeImgBrick`

| Поле | Значение |
|---|---|
| Название RU | Изменить размер изображение |
| Название EN | Resize IMG (from project files) |
| Строки (`strings.xml` → `values-ru`) | `brick_resize_img`: EN="Resize IMG (from project files)" / RU="Изменить размер изображение"; `brick_resize_img2`: EN="file" / RU="файл проекта"; `brick_resize_img3`: EN="new size (x, y)" / RU="новый размер (x, y)" |
| Параметры | `FILE` (`brick_resize_img_file`); `WIDTH` (`brick_resize_img_x`); `HEIGHT` (`brick_resize_img_y`) |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/ResizeImgBrick.java:33` |
| Класс экшена (`content/actions`) | `ResizeImgAction` (`catroid/src/main/java/org/catrobat/catroid/content/actions/ResizeImgAction.kt:49`; via `createResizeImg`, `ActionFactory.java:2066`) |
| Описание (строго `BrickInfo.java` + код) | RU: Изменяет размер изображения из файла и сохраняет в этот же файл.<br>EN: Resizes an image from a file and saves it to the same file. |
| Вызов в фабрике | `ResizeImgBrick("image.png", 64, 64)` |
| Layout | `catroid/src/main/res/layout/brick_resize_img.xml` |

## 13. `GrayscaleImgBrick`

| Поле | Значение |
|---|---|
| Название RU | Сделать IMG чёрно-белым |
| Название EN | Grayscale IMG (from project files) |
| Строки (`strings.xml` → `values-ru`) | `brick_grayscale_img`: EN="Grayscale IMG (from project files)" / RU="Сделать IMG чёрно-белым"; `brick_resize_img2`: EN="file" / RU="файл проекта" |
| Параметры | `FILE` (`brick_grayscale_img_file`) |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/GrayscaleImgBrick.java:30` |
| Класс экшена (`content/actions`) | `GrayscaleImgAction` (`catroid/src/main/java/org/catrobat/catroid/content/actions/GrayscaleImgAction.kt:49`; via `createGrayscaleImg`, `ActionFactory.java:2076`) |
| Описание (строго `BrickInfo.java` + код) | RU: Делает изображение черно-белым и сохраняет в этот же файл.<br>EN: Converts an image to black and white and saves it to the same file. |
| Вызов в фабрике | `GrayscaleImgBrick("image.png")` |
| Layout | `catroid/src/main/res/layout/brick_grayscale_img.xml` |

## 14. `NormalizeImgBrick`

| Поле | Значение |
|---|---|
| Название RU | Нормализовать IMG \(0\-1\) |
| Название EN | Normalize IMG (from project files) |
| Строки (`strings.xml` → `values-ru`) | `brick_normalize_img`: EN="Normalize IMG (from project files)" / RU="Нормализовать IMG \(0\-1\)"; `brick_resize_img2`: EN="file" / RU="файл проекта"; `brick_normalize_img2`: EN="And save values to tables" / RU="И сохранить в таблицы"; `brick_normalize_img3`: EN="R:" / RU="R:" |
| Параметры | `FILE` (`brick_normalize_img_file`); `RED` (`brick_normalize_img_r`); `GREEN` (`brick_normalize_img_g`); `BLUE` (`brick_normalize_img_b`) |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/NormalizeImgBrick.java:30` |
| Класс экшена (`content/actions`) | `NormalizeImgAction` (`catroid/src/main/java/org/catrobat/catroid/content/actions/NormalizeImgAction.kt:50`; via `createNormalizeImg`, `ActionFactory.java:2084`) |
| Описание (строго `BrickInfo.java` + код) | RU: Нормализует изображение в диапозон от 0 до 1 и сохраняет каждый канал в таблицу.<br>EN: Normalizes an image to the range from 0 to 1 and saves each channel into a table. |
| Вызов в фабрике | `NormalizeImgBrick("image.png", "rTable", "gTable", "bTable")` |
| Layout | `catroid/src/main/res/layout/brick_normalize_img.xml` |

## 15. `CutLookBrick`

| Поле | Значение |
|---|---|
| Название RU | Обрезать образ |
| Название EN | Crop look |
| Строки (`strings.xml` → `values-ru`) | `brick_cut_look`: EN="Crop look" / RU="Обрезать образ"; `brick_cut_look1`: EN="from (x, y)" / RU="начало (x, y)"; `brick_cut_look2`: EN="to (x, y)" / RU="конец (x, y)" |
| Параметры | `X1` (`brick_cut_look_fromx`); `Y1` (`brick_cut_look_fromy`); `X2` (`brick_cut_look_tox`); `Y2` (`brick_cut_look_toy`) |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/CutLookBrick.java:31` |
| Класс экшена (`content/actions`) | `CutLookAction` (`catroid/src/main/java/org/catrobat/catroid/content/actions/CutLookAction.kt:49`; via `createCutLook`, `ActionFactory.java:2331`) |
| Описание (строго `BrickInfo.java` + код) | RU: Обрезает образие спрайта.<br>EN: Crops a sprite costume. |
| Вызов в фабрике | `CutLookBrick(100, 200, 300, 400)` |
| Layout | `catroid/src/main/res/layout/brick_cut_look.xml` |

## 16. `ApplyShaderToImageBrick`

| Поле | Значение |
|---|---|
| Название RU | Применить шейдер к картинке |
| Название EN | Apply shader to image file |
| Строки (`strings.xml` → `values-ru`) | `brick_apply_shader_to_image`: EN="Apply shader to image file" / RU="Применить шейдер к картинке"; `brick_apply_shader_image_file`: EN="Project file" / RU="Файл проекта" |
| Параметры | `NAME` (`brick_apply_shader_filename`); `TEXT` (`brick_apply_shader_vsh`); `X_POSITION` (`brick_apply_shader_fsh`) |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/ApplyShaderToImageBrick.java:8` |
| Класс экшена (`content/actions`) | `ApplyShaderToImageAction` (`catroid/src/main/java/org/catrobat/catroid/content/actions/ApplyShaderToImageAction.kt:8`; via `createApplyShaderToImageAction`, `ActionFactory.java:5774`) |
| Описание (строго `BrickInfo.java` + код) | RU: Применяет шейдер к файлу проекта (изображению). Используйте это для генерации чего-либо или обработки изображений<br>EN: Applies a shader to a project file (image). Use this to generate something or process images |
| Вызов в фабрике | `—` |
| Layout | `catroid/src/main/res/layout/brick_apply_shader_to_image.xml` |
