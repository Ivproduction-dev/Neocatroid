# PockeTensor / PockeTensor

Ключ категории: `pocketensor` (`res/values/strings.xml`: "PockeTensor"; `res/values-ru/strings.xml`: "PockeTensor").
Состав и порядок — строго по `CategoryBricksFactory.kt` → `setupPocketensorCategoryList` (заголовки `SubCategoryHeaderBrick` не являются блоками и пропущены).

Блоков: 25.

## 1. `PtSetTrainingBrick`

| Поле | Значение |
|---|---|
| Название RU | Режим обучения |
| Название EN | Set Training mode |
| Строки (`strings.xml` → `values-ru`) | `pt_set_training`: EN="Set Training mode" / RU="Режим обучения" |
| Параметры | `VALUE_1` (`brick_pt_set_training_text`) |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/PtSetTrainingBrick.java:31` |
| Класс экшена (`content/actions`) | `PtSetTrainingAction` (`catroid/src/main/java/org/catrobat/catroid/content/actions/PtSetTrainingAction.kt:30`; via `createPtSetTrainingAction`, `ActionFactory.java:5017`) |
| Описание (строго `BrickInfo.java` + код) | RU: Включает или выключает режим обучения (training/eval) для модели PyTorch. ВАЖНО: требуется поддержка машинного обучения (MLBridge).<br>EN: Enables or disables the training/eval mode for the PyTorch model. IMPORTANT: requires the machine learning backend (MLBridge). |
| Вызов в фабрике | `PtSetTrainingBrick(1)` |
| Layout | `catroid/src/main/res/layout/brick_pt_set_training.xml` |

## 2. `PtCreateTensorBrick`

| Поле | Значение |
|---|---|
| Название RU | Создать тензор |
| Название EN | Create Tensor |
| Строки (`strings.xml` → `values-ru`) | `pt_create_tensor`: EN="Create Tensor" / RU="Создать тензор"; `pt_name`: EN="Name" / RU="Имя:"; `pt_shape`: EN="Shape" / RU="Форма:"; `pt_value`: EN="Value" / RU="Значение:" |
| Параметры | `NAME` (`brick_pt_create_name`); `VALUE_1` (`brick_pt_create_shape`); `VALUE_2` (`brick_pt_create_value`); `VALUE_3` (`brick_pt_create_trainable`) |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/PtCreateTensorBrick.java:31` |
| Класс экшена (`content/actions`) | `PtCreateTensorAction` (`catroid/src/main/java/org/catrobat/catroid/content/actions/PtCreateTensorAction.kt:8`; via `createPtCreateTensorAction`, `ActionFactory.java:4956`) |
| Описание (строго `BrickInfo.java` + код) | RU: Создает тензор PyTorch с заданным именем, формой (через запятую, например 2,3), начальным значением (или RANDOM для случайных чисел) и флагом обучаемости. ВАЖНО: требуется поддержка машинного обучения (MLBridge).<br>EN: Creates a PyTorch tensor with the given name, shape (comma-separated, e.g. 2,3), initial value (or RANDOM for random values) and a trainable flag. IMPORTANT: requires the machine learning backend (MLBridge). |
| Вызов в фабрике | `PtCreateTensorBrick("tensor", "10,4", 0f, true)` |
| Layout | `catroid/src/main/res/layout/brick_pt_create_tensor.xml` |

## 3. `PtCreateNormalTensorBrick`

| Поле | Значение |
|---|---|
| Название RU | Создать тензор Гаусса |
| Название EN | Create Gaussian Tensor |
| Строки (`strings.xml` → `values-ru`) | `pt_create_normal_tensor`: EN="Create Gaussian Tensor" / RU="Создать тензор Гаусса"; `pt_tensor_shape`: EN="Shape" / RU="Размер"; `pt_tensor_mean`: EN="Mean" / RU="Среднее"; `pt_tensor_std`: EN="StdDev" / RU="Отклонение" |
| Параметры | `NAME` (`brick_pt_normal_name`); `VALUE_1` (`brick_pt_normal_shape`); `VALUE_2` (`brick_pt_normal_mean`); `VALUE_3` (`brick_pt_normal_std`); `VALUE_4` (`brick_pt_normal_trainable`) |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/PtCreateNormalTensorBrick.java:8` |
| Класс экшена (`content/actions`) | `PtCreateNormalTensorAction` (`catroid/src/main/java/org/catrobat/catroid/content/actions/PtCreateNormalTensorAction.kt:8`; via `createPtCreateNormalTensorAction`, `ActionFactory.java:8530`) |
| Описание (строго `BrickInfo.java` + код) | RU: Создаёт тензор с нормальным распределением (mean, std); trainable включает градиенты.<br>EN: Creates a normal-distribution tensor (mean, std); trainable enables gradients. |
| Вызов в фабрике | `PtCreateNormalTensorBrick("noise", "1,10", 0f, 1f, false)` |
| Layout | `catroid/src/main/res/layout/brick_pt_create_normal_tensor.xml` |

## 4. `PtOpBrick`

| Поле | Значение |
|---|---|
| Название RU | Операция |
| Название EN | Operation |
| Строки (`strings.xml` → `values-ru`) | `pt_operation`: EN="Operation" / RU="Операция"; `pt_res`: EN="Result" / RU="Результат:" |
| Параметры | `VALUE_1` (`brick_pt_op_res`); `VALUE_2` (`brick_pt_op_a`); `VALUE_3` (`brick_pt_op_b`); спиннер (`brick_pt_op_spinner`) |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/PtOpBrick.java:38` |
| Класс экшена (`content/actions`) | `PtOpAction` (`catroid/src/main/java/org/catrobat/catroid/content/actions/PtOpAction.kt:8`; via `createPtOpAction`, `ActionFactory.java:4967`) |
| Описание (строго `BrickInfo.java` + код) | RU: Выполняет выбранную операцию над двумя тензорами PyTorch (A и B) и сохраняет результат в тензор с указанным именем. ВАЖНО: требуется поддержка машинного обучения (MLBridge).<br>EN: Performs the selected operation on two PyTorch tensors (A and B) and stores the result in the tensor with the given name. IMPORTANT: requires the machine learning backend (MLBridge). |
| Вызов в фабрике | `PtOpBrick()` |
| Layout | `catroid/src/main/res/layout/brick_pt_op.xml` |

## 5. `PtLinearBrick`

| Поле | Значение |
|---|---|
| Название RU | Линейный слой |
| Название EN | Linear layer |
| Строки (`strings.xml` → `values-ru`) | `pt_linear`: EN="Linear layer" / RU="Линейный слой"; `pt_layer_name`: EN="Layer name" / RU="Имя слоя"; `pt_input`: EN="Input tensor" / RU="Входной тензор"; `pt_output`: EN="Output tensor" / RU="Выходной тензор" |
| Параметры | `NAME` (`brick_pt_linear_name`); `VALUE_1` (`brick_pt_linear_input`); `VALUE_2` (`brick_pt_linear_output`); `VALUE_3` (`brick_pt_linear_in_features`); `VALUE_4` (`brick_pt_linear_out_features`) |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/PtLinearBrick.java:31` |
| Класс экшена (`content/actions`) | `PtLinearAction` (`catroid/src/main/java/org/catrobat/catroid/content/actions/PtLinearAction.kt:8`; via `createPtLinearAction`, `ActionFactory.java:5024`) |
| Описание (строго `BrickInfo.java` + код) | RU: Применяет полносвязный (линейный) слой: создаёт/использует веса и смещение с заданным именем слоя, берёт входной тензор и записывает результат в выходной тензор. Число входов и выходов задаёт размерности. Основной блок для нейросетей и трансформеров. ВАЖНО: требуется поддержка машинного обучения (MLBridge).<br>EN: Applies a fully-connected (linear) layer: creates/uses weights and bias under the given layer name, takes the input tensor and writes the result to the output tensor. In/out features set the dimensions. Core building block for neural networks and transformers. IMPORTANT: requires the machine learning backend (MLBridge). |
| Вызов в фабрике | `PtLinearBrick("linear1", "input", "hidden", 4, 8)` |
| Layout | `catroid/src/main/res/layout/brick_pt_linear.xml` |

## 6. `PtBackwardBrick`

| Поле | Значение |
|---|---|
| Название RU | Backward (ошибка) |
| Название EN | Backward (error) |
| Строки (`strings.xml` → `values-ru`) | `pt_backward`: EN="Backward (error)" / RU="Backward (ошибка)" |
| Параметры | `NAME` (`brick_pt_backward_loss`) |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/PtBackwardBrick.java:31` |
| Класс экшена (`content/actions`) | `PtBackwardAction` (`catroid/src/main/java/org/catrobat/catroid/content/actions/PtBackwardAction.kt:8`; via `createPtBackwardAction`, `ActionFactory.java:4978`) |
| Описание (строго `BrickInfo.java` + код) | RU: Выполняет обратное распространение ошибки (backward) для указанного тензора потерь, вычисляя градиенты для обучаемых тензоров. ВАЖНО: требуется поддержка машинного обучения (MLBridge).<br>EN: Runs backpropagation (backward) on the given loss tensor, computing gradients for the trainable tensors. IMPORTANT: requires the machine learning backend (MLBridge). |
| Вызов в фабрике | `PtBackwardBrick("loss")` |
| Layout | `catroid/src/main/res/layout/brick_pt_backward.xml` |

## 7. `PtStepBrick`

| Поле | Значение |
|---|---|
| Название RU | Шаг обучения (Step) |
| Название EN | Learning step |
| Строки (`strings.xml` → `values-ru`) | `pt_step`: EN="Learning step" / RU="Шаг обучения (Step)"; `pt_lr`: EN="Learning rate" / RU="Скорость (LR):" |
| Параметры | `VALUE_1` (`brick_pt_step_lr`) |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/PtStepBrick.java:31` |
| Класс экшена (`content/actions`) | `PtStepAction` (`catroid/src/main/java/org/catrobat/catroid/content/actions/PtStepAction.kt:8`; via `createPtStepAction`, `ActionFactory.java:4985`) |
| Описание (строго `BrickInfo.java` + код) | RU: Делает один шаг обучения (обновляет веса через оптимизатор) с заданной скоростью обучения (learning rate). Обычно вызывается после блока обратного распространения. ВАЖНО: требуется поддержка машинного обучения (MLBridge).<br>EN: Performs one training step (updates weights via the optimizer) with the given learning rate. Usually called after the backward block. IMPORTANT: requires the machine learning backend (MLBridge). |
| Вызов в фабрике | `PtStepBrick(0.01f)` |
| Layout | `catroid/src/main/res/layout/brick_pt_step.xml` |

## 8. `MLStepAdamBrick`

| Поле | Значение |
|---|---|
| Название RU | Умный шаг (Adam) |
| Название EN | Smart step (Adam) |
| Строки (`strings.xml` → `values-ru`) | `brick_ml_step_adam`: EN="Smart step (Adam)" / RU="Умный шаг (Adam)"; `brick_ml_lr`: EN="Learning rate" / RU="Скорость" |
| Параметры | `VALUE` (`brick_ml_edit_lr`) |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/MLStepAdamBrick.java:31` |
| Класс экшена (`content/actions`) | `MLStepAdamAction` (`catroid/src/main/java/org/catrobat/catroid/content/actions/MLStepAdamAction.kt:8`; via `createMLStepAdamAction`, `ActionFactory.java:5035`) |
| Описание (строго `BrickInfo.java` + код) | RU: Выполняет один шаг обучения нейронной сети методом оптимизации Adam с заданной скоростью обучения (learning rate).<br>EN: Performs one training step of the neural network using the Adam optimizer with the given learning rate. |
| Вызов в фабрике | `MLStepAdamBrick(Formula(0.01f)` |
| Layout | `catroid/src/main/res/layout/brick_ml_step_adam.xml` |

## 9. `MLStepAdamWBrick`

| Поле | Значение |
|---|---|
| Название RU | Шаг AdamW |
| Название EN | AdamW Step |
| Строки (`strings.xml` → `values-ru`) | `pt_step_adamw`: EN="AdamW Step" / RU="Шаг AdamW"; `pt_lr`: EN="Learning rate" / RU="Скорость (LR):"; `pt_weight_decay`: EN="Weight decay" / RU="Затухание весов (decay)" |
| Параметры | `VALUE_1` (`brick_ml_adamw_lr`); `VALUE_2` (`brick_ml_adamw_decay`) |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/MLStepAdamWBrick.java:8` |
| Класс экшена (`content/actions`) | `MLStepAdamWAction` (`catroid/src/main/java/org/catrobat/catroid/content/actions/MLStepAdamWAction.kt:8`; via `createMLStepAdamWAction`, `ActionFactory.java:8615`) |
| Описание (строго `BrickInfo.java` + код) | RU: Шаг обучения нейросети оптимизатором AdamW с заданным learning rate и weight decay.<br>EN: Performs one training step of the neural network using the AdamW optimizer with the given learning rate and weight decay. |
| Вызов в фабрике | `MLStepAdamWBrick(0.001f, 0.01f)` |
| Layout | `catroid/src/main/res/layout/brick_ml_step_adamw.xml` |

## 10. `PtSetTensorBrick`

| Поле | Значение |
|---|---|
| Название RU | Заполнить тензор |
| Название EN | fill Tensor |
| Строки (`strings.xml` → `values-ru`) | `fill_tensor`: EN="fill Tensor" / RU="Заполнить тензор"; `from_string`: EN="from string" / RU="из строки" |
| Параметры | `NAME` (`brick_pt_set_tensor_name`); `VALUE_1` (`brick_pt_set_tensor_data`) |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/PtSetTensorBrick.java:31` |
| Класс экшена (`content/actions`) | `PtSetTensorAction` (`catroid/src/main/java/org/catrobat/catroid/content/actions/PtSetTensorAction.kt:8`; via `createPtSetTensorAction`, `ActionFactory.java:4992`) |
| Описание (строго `BrickInfo.java` + код) | RU: Заполняет существующий тензор PyTorch данными из строки (через запятую). ВАЖНО: требуется поддержка машинного обучения (MLBridge).<br>EN: Fills an existing PyTorch tensor with data from a string (comma-separated). IMPORTANT: requires the machine learning backend (MLBridge). |
| Вызов в фабрике | `PtSetTensorBrick("tensor", "1,2,3")` |
| Layout | `catroid/src/main/res/layout/brick_pt_set_tensor.xml` |

## 11. `PtSetByIndexBrick`

| Поле | Значение |
|---|---|
| Название RU | в тензоре |
| Название EN | In Tensor |
| Строки (`strings.xml` → `values-ru`) | `in_tensor`: EN="In Tensor" / RU="в тензоре"; `from_index`: EN="index" / RU="по индексу"; `set_tensor`: EN="set" / RU="задать" |
| Параметры | `NAME` (`brick_pt_set_index_name`); `VALUE_1` (`brick_pt_set_index_idx`); `VALUE_2` (`brick_pt_set_index_val`) |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/PtSetByIndexBrick.java:31` |
| Класс экшена (`content/actions`) | `PtSetByIndexAction` (`catroid/src/main/java/org/catrobat/catroid/content/actions/PtSetByIndexAction.kt:8`; via `createPtSetByIndexAction`, `ActionFactory.java:5000`) |
| Описание (строго `BrickInfo.java` + код) | RU: Записывает значение в тензор PyTorch по указанному индексу. ВАЖНО: требуется поддержка машинного обучения (MLBridge).<br>EN: Writes a value into a PyTorch tensor at the given index. IMPORTANT: requires the machine learning backend (MLBridge). |
| Вызов в фабрике | `PtSetByIndexBrick("tensor", 0, 1f)` |
| Layout | `catroid/src/main/res/layout/brick_pt_set_by_index.xml` |

## 12. `PtReshapeBrick`

| Поле | Значение |
|---|---|
| Название RU | Изменить форму |
| Название EN | Change shape of |
| Строки (`strings.xml` → `values-ru`) | `pt_reshape`: EN="Change shape of" / RU="Изменить форму"; `pt_reshape_to`: EN="to" / RU="на" |
| Параметры | `NAME` (`brick_pt_reshape_name`); `VALUE_1` (`brick_pt_reshape_shape`) |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/PtReshapeBrick.java:31` |
| Класс экшена (`content/actions`) | `PtReshapeAction` (`catroid/src/main/java/org/catrobat/catroid/content/actions/PtReshapeAction.kt:8`; via `createPtReshapeAction`, `ActionFactory.java:5009`) |
| Описание (строго `BrickInfo.java` + код) | RU: Меняет форму (размерность) существующего тензора PyTorch на новую, заданную через запятую. ВАЖНО: требуется поддержка машинного обучения (MLBridge).<br>EN: Changes the shape (dimensions) of an existing PyTorch tensor to a new one given as comma-separated values. IMPORTANT: requires the machine learning backend (MLBridge). |
| Вызов в фабрике | `PtReshapeBrick("tensor", "20,2")` |
| Layout | `catroid/src/main/res/layout/brick_pt_reshape.xml` |

## 13. `PtSliceBrick`

| Поле | Значение |
|---|---|
| Название RU | Срез тензора |
| Название EN | Slice tensor |
| Строки (`strings.xml` → `values-ru`) | `pt_slice_res`: EN="Slice tensor" / RU="Срез тензора"; `pt_slice_input`: EN="From tensor" / RU="Из тензора"; `pt_slice_from`: EN="Column from" / RU="Столбец от"; `pt_slice_to`: EN="To" / RU="До" |
| Параметры | `NAME` (`brick_pt_slice_res`); `VALUE_1` (`brick_pt_slice_input`); `VALUE_2` (`brick_pt_slice_start`); `VALUE_3` (`brick_pt_slice_end`) |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/PtSliceBrick.java:8` |
| Класс экшена (`content/actions`) | `PtSliceAction` (`catroid/src/main/java/org/catrobat/catroid/content/actions/PtSliceAction.kt:8`; via `createPtSliceAction`, `ActionFactory.java:8518`) |
| Описание (строго `BrickInfo.java` + код) | RU: Срез тензора по столбцам: res = input[:, start:end].<br>EN: Slices a tensor by columns: res = input[:, start:end]. |
| Вызов в фабрике | `PtSliceBrick("res", "in", 0, 5)` |
| Layout | `catroid/src/main/res/layout/brick_pt_slice.xml` |

## 14. `PtDropoutBrick`

| Поле | Значение |
|---|---|
| Название RU | Dropout результат |
| Название EN | Dropout result |
| Строки (`strings.xml` → `values-ru`) | `pt_dropout_res`: EN="Dropout result" / RU="Dropout результат"; `pt_slice_input`: EN="From tensor" / RU="Из тензора"; `pt_drop_prob`: EN="Drop rate (0-1)" / RU="Шанс отбросить (0-1)" |
| Параметры | `NAME` (`brick_pt_drop_res`); `VALUE_1` (`brick_pt_drop_input`); `VALUE_2` (`brick_pt_drop_prob`) |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/PtDropoutBrick.java:8` |
| Класс экшена (`content/actions`) | `PtDropoutAction` (`catroid/src/main/java/org/catrobat/catroid/content/actions/PtDropoutAction.kt:8`; via `createPtDropoutAction`, `ActionFactory.java:8604`) |
| Описание (строго `BrickInfo.java` + код) | RU: Дропаут с вероятностью p (0..1).<br>EN: Dropout with probability p (0..1). |
| Вызов в фабрике | `PtDropoutBrick("drop", "x", 0.2f)` |
| Layout | `catroid/src/main/res/layout/brick_pt_dropout.xml` |

## 15. `PtZeroGradBrick`

| Поле | Значение |
|---|---|
| Название RU | Сбросить градиенты (Zero Grad) |
| Название EN | Clear Gradients (Zero Grad) |
| Строки (`strings.xml` → `values-ru`) | `pt_zero_grad`: EN="Clear Gradients (Zero Grad)" / RU="Сбросить градиенты (Zero Grad)" |
| Параметры | — |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/PtZeroGradBrick.java:7` |
| Класс экшена (`content/actions`) | `PtZeroGradAction` (`catroid/src/main/java/org/catrobat/catroid/content/actions/PtZeroGradAction.kt:6`; via `createPtZeroGradAction`, `ActionFactory.java:8513`) |
| Описание (строго `BrickInfo.java` + код) | RU: Обнуляет градиенты всех тензоров (перед backward).<br>EN: Zeroes gradients of all tensors (before backward). |
| Вызов в фабрике | `PtZeroGradBrick()` |
| Layout | `catroid/src/main/res/layout/brick_pt_zero_grad.xml` |

## 16. `PtClipGradBrick`

| Поле | Значение |
|---|---|
| Название RU | Ограничить градиенты (ClipGrad) |
| Название EN | Clip Gradients (ClipGrad) |
| Строки (`strings.xml` → `values-ru`) | `pt_clip_grad`: EN="Clip Gradients (ClipGrad)" / RU="Ограничить градиенты (ClipGrad)" |
| Параметры | `VALUE_1` (`brick_pt_clip_max_norm`) |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/PtClipGradBrick.java:8` |
| Класс экшена (`content/actions`) | `PtClipGradAction` (`catroid/src/main/java/org/catrobat/catroid/content/actions/PtClipGradAction.kt:8`; via `createPtClipGradAction`, `ActionFactory.java:8568`) |
| Описание (строго `BrickInfo.java` + код) | RU: Клипирует градиенты по норме (max_norm).<br>EN: Clips gradients by their norm (max_norm). |
| Вызов в фабрике | `PtClipGradBrick(5f)` |
| Layout | `catroid/src/main/res/layout/brick_pt_clip_grad.xml` |

## 17. `PtLayerLinearBrick`

| Поле | Значение |
|---|---|
| Название RU | Слой Linear |
| Название EN | Linear Layer |
| Строки (`strings.xml` → `values-ru`) | `pt_layer_linear`: EN="Linear Layer" / RU="Слой Linear"; `pt_layer_linear_in`: EN="Input tensor" / RU="Входной тензор"; `pt_layer_linear_out`: EN="Output tensor" / RU="Выходной тензор"; `pt_layer_linear_in_f`: EN="In features" / RU="Входов (in_features)" |
| Параметры | `NAME` (`brick_pt_linear_layer_name`); `VALUE_1` (`brick_pt_linear_input`); `VALUE_2` (`brick_pt_linear_output`); `VALUE_3` (`brick_pt_linear_in_f`); `VALUE_4` (`brick_pt_linear_out_f`) |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/PtLayerLinearBrick.java:8` |
| Класс экшена (`content/actions`) | `PtLayerLinearAction` (`catroid/src/main/java/org/catrobat/catroid/content/actions/PtLayerLinearAction.kt:8`; via `createPtLayerLinearAction`, `ActionFactory.java:8500`) |
| Описание (строго `BrickInfo.java` + код) | RU: Линейный слой нейросети: вход, выход, in_features, out_features (Pocketensor).<br>EN: Linear layer: input, output, in_features, out_features (Pocketensor). |
| Вызов в фабрике | `PtLayerLinearBrick("fc1", "x", "out", 10, 5)` |
| Layout | `catroid/src/main/res/layout/brick_pt_layer_linear.xml` |

## 18. `PtConv2DBrick`

| Поле | Значение |
|---|---|
| Название RU | Слой Conv2D |
| Название EN | Conv2D Layer |
| Строки (`strings.xml` → `values-ru`) | `pt_layer_conv2d`: EN="Conv2D Layer" / RU="Слой Conv2D"; `pt_layer_linear_in`: EN="Input tensor" / RU="Входной тензор"; `pt_layer_linear_out`: EN="Output tensor" / RU="Выходной тензор"; `pt_conv_in_c`: EN="In channels" / RU="Каналов входа" |
| Параметры | `NAME` (`brick_pt_conv_name`); `VALUE_1` (`brick_pt_conv_input`); `VALUE_2` (`brick_pt_conv_output`); `VALUE_3` (`brick_pt_conv_in_c`); `VALUE_4` (`brick_pt_conv_out_c`); `VALUE_5` (`brick_pt_conv_ksize`); `VALUE_6` (`brick_pt_conv_stride`) |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/PtConv2DBrick.java:8` |
| Класс экшена (`content/actions`) | `PtConv2DAction` (`catroid/src/main/java/org/catrobat/catroid/content/actions/PtConv2DAction.kt:8`; via `createPtConv2DAction`, `ActionFactory.java:8576`) |
| Описание (строго `BrickInfo.java` + код) | RU: Свёрточный слой: in_channels, out_channels, kernel_size, stride.<br>EN: Conv2D layer: in_channels, out_channels, kernel_size, stride. |
| Вызов в фабрике | `PtConv2DBrick("conv1", "img", "features", 1, 16, 3, 1)` |
| Layout | `catroid/src/main/res/layout/brick_pt_conv2d.xml` |

## 19. `PtMaxPool2DBrick`

| Поле | Значение |
|---|---|
| Название RU | MaxPool2D результат |
| Название EN | MaxPool2D result |
| Строки (`strings.xml` → `values-ru`) | `pt_maxpool2d_res`: EN="MaxPool2D result" / RU="MaxPool2D результат"; `pt_slice_input`: EN="From tensor" / RU="Из тензора"; `pt_pool_size`: EN="Pool size" / RU="Размер окна (pool)"; `pt_conv_stride`: EN="Stride" / RU="Шаг (stride)" |
| Параметры | `NAME` (`brick_pt_pool_res`); `VALUE_1` (`brick_pt_pool_input`); `VALUE_2` (`brick_pt_pool_size`); `VALUE_3` (`brick_pt_pool_stride`) |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/PtMaxPool2DBrick.java:8` |
| Класс экшена (`content/actions`) | `PtMaxPool2DAction` (`catroid/src/main/java/org/catrobat/catroid/content/actions/PtMaxPool2DAction.kt:8`; via `createPtMaxPool2DAction`, `ActionFactory.java:8592`) |
| Описание (строго `BrickInfo.java` + код) | RU: Макс-пулинг с размером окна и шагом.<br>EN: Max-pooling with pool size and stride. |
| Вызов в фабрике | `PtMaxPool2DBrick("pooled", "features", 2, 2)` |
| Layout | `catroid/src/main/res/layout/brick_pt_maxpool2d.xml` |

## 20. `PtGruCellBrick`

| Поле | Значение |
|---|---|
| Название RU | Ячейка GRU |
| Название EN | GRU Cell |
| Строки (`strings.xml` → `values-ru`) | `pt_layer_gru_cell`: EN="GRU Cell" / RU="Ячейка GRU"; `pt_layer_linear_in`: EN="Input tensor" / RU="Входной тензор"; `pt_lstm_hin`: EN="Input H (H_in)" / RU="Вход H (H_in)"; `pt_lstm_hout`: EN="Output H (H_out)" / RU="Выход H (H_out)" |
| Параметры | `NAME` (`brick_pt_gru_name`); `VALUE_1` (`brick_pt_gru_input`); `VALUE_2` (`brick_pt_gru_hin`); `VALUE_3` (`brick_pt_gru_hout`); `VALUE_4` (`brick_pt_gru_indim`); `VALUE_5` (`brick_pt_gru_hiddendim`) |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/PtGruCellBrick.java:8` |
| Класс экшена (`content/actions`) | `PtGruCellAction` (`catroid/src/main/java/org/catrobat/catroid/content/actions/PtGruCellAction.kt:8`; via `createPtGruCellAction`, `ActionFactory.java:8642`) |
| Описание (строго `BrickInfo.java` + код) | RU: GRU-ячейка: x, h_in в h_out, input_size, hidden_size.<br>EN: GRU cell: x, h_in to h_out, input_size, hidden_size. |
| Вызов в фабрике | `PtGruCellBrick("gru1", "x", "hin", "hout", 8, 16)` |
| Layout | `catroid/src/main/res/layout/brick_pt_gru_cell.xml` |

## 21. `PtLstmCellBrick`

| Поле | Значение |
|---|---|
| Название RU | Ячейка LSTM |
| Название EN | LSTM Cell |
| Строки (`strings.xml` → `values-ru`) | `pt_layer_lstm_cell`: EN="LSTM Cell" / RU="Ячейка LSTM"; `pt_layer_linear_in`: EN="Input tensor" / RU="Входной тензор"; `pt_lstm_hin`: EN="Input H (H_in)" / RU="Вход H (H_in)"; `pt_lstm_cin`: EN="Input C (C_in)" / RU="Вход C (C_in)" |
| Параметры | `NAME` (`brick_pt_lstm_name`); `VALUE_1` (`brick_pt_lstm_input`); `VALUE_2` (`brick_pt_lstm_hin`); `VALUE_3` (`brick_pt_lstm_cin`); `VALUE_4` (`brick_pt_lstm_hout`); `VALUE_5` (`brick_pt_lstm_cout`); `VALUE_6` (`brick_pt_lstm_indim`); `VALUE_7` (`brick_pt_lstm_hiddendim`) |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/PtLstmCellBrick.java:8` |
| Класс экшена (`content/actions`) | `PtLstmCellAction` (`catroid/src/main/java/org/catrobat/catroid/content/actions/PtLstmCellAction.kt:8`; via `createPtLstmCellAction`, `ActionFactory.java:8625`) |
| Описание (строго `BrickInfo.java` + код) | RU: LSTM-ячейка: x, h_in/c_in в h_out/c_out, input_size, hidden_size.<br>EN: LSTM cell: x, h_in/c_in to h_out/c_out, input_size, hidden_size. |
| Вызов в фабрике | `PtLstmCellBrick("lstm1", "x", "hin", "cin", "hout", "cout", 8, 16)` |
| Layout | `catroid/src/main/res/layout/brick_pt_lstm_cell.xml` |

## 22. `PtEmbeddingBrick`

| Поле | Значение |
|---|---|
| Название RU | Слой Embedding |
| Название EN | Embedding Layer |
| Строки (`strings.xml` → `values-ru`) | `pt_layer_embedding`: EN="Embedding Layer" / RU="Слой Embedding"; `pt_layer_linear_in`: EN="Input tensor" / RU="Входной тензор"; `pt_layer_linear_out`: EN="Output tensor" / RU="Выходной тензор"; `pt_emb_vocab`: EN="Vocab size" / RU="Размер словаря" |
| Параметры | `NAME` (`brick_pt_emb_name`); `VALUE_1` (`brick_pt_emb_input`); `VALUE_2` (`brick_pt_emb_output`); `VALUE_3` (`brick_pt_emb_vocab`); `VALUE_4` (`brick_pt_emb_dim`) |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/PtEmbeddingBrick.java:8` |
| Класс экшена (`content/actions`) | `PtEmbeddingAction` (`catroid/src/main/java/org/catrobat/catroid/content/actions/PtEmbeddingAction.kt:8`; via `createPtEmbeddingAction`, `ActionFactory.java:8543`) |
| Описание (строго `BrickInfo.java` + код) | RU: Embedding-слой: токены в векторы (vocab_size, emb_dim).<br>EN: Embedding layer: tokens to vectors (vocab_size, emb_dim). |
| Вызов в фабрике | `PtEmbeddingBrick("emb1", "tokens", "vectors", 100, 16)` |
| Layout | `catroid/src/main/res/layout/brick_pt_embedding.xml` |

## 23. `PtAttentionBrick`

| Поле | Значение |
|---|---|
| Название RU | Слой Self-Attention |
| Название EN | Self-Attention Layer |
| Строки (`strings.xml` → `values-ru`) | `pt_layer_attention`: EN="Self-Attention Layer" / RU="Слой Self-Attention"; `pt_layer_linear_in`: EN="Input tensor" / RU="Входной тензор"; `pt_layer_linear_out`: EN="Output tensor" / RU="Выходной тензор"; `pt_attn_dim`: EN="Feature dimension" / RU="Размер фич (embed_dim)" |
| Параметры | `NAME` (`brick_pt_attn_name`); `VALUE_1` (`brick_pt_attn_input`); `VALUE_2` (`brick_pt_attn_output`); `VALUE_3` (`brick_pt_attn_dim`) |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/PtAttentionBrick.java:8` |
| Класс экшена (`content/actions`) | `PtAttentionAction` (`catroid/src/main/java/org/catrobat/catroid/content/actions/PtAttentionAction.kt:8`; via `createPtAttentionAction`, `ActionFactory.java:8556`) |
| Описание (строго `BrickInfo.java` + код) | RU: Слой самовнимания с заданной размерностью (embed_dim).<br>EN: Self-attention layer with the given embedding dimension. |
| Вызов в фабрике | `PtAttentionBrick("attn1", "x", "context", 8)` |
| Layout | `catroid/src/main/res/layout/brick_pt_attention.xml` |

## 24. `MLSaveBrick`

| Поле | Значение |
|---|---|
| Название RU | Сохранить веса |
| Название EN | Save weights |
| Строки (`strings.xml` → `values-ru`) | `brick_ml_save`: EN="Save weights" / RU="Сохранить веса"; `brick_ml_file`: EN="Project file" / RU="Файл проекта" |
| Параметры | `STRING` (`brick_ml_edit_filename`) |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/MLSaveBrick.java:31` |
| Класс экшена (`content/actions`) | `MLFileAction` (`catroid/src/main/java/org/catrobat/catroid/content/actions/MLFileAction.kt:9`; via `createMLFileAction`, `ActionFactory.java:5042`) |
| Описание (строго `BrickInfo.java` + код) | RU: Сохраняет веса нейронной сети в файл проекта с указанным именем (машинное обучение, Pocketensor).<br>EN: Saves neural network weights to the project file with the given name (machine learning, Pocketensor). |
| Вызов в фабрике | `MLSaveBrick("model.bin")` |
| Layout | `catroid/src/main/res/layout/brick_ml_file_save.xml` |

## 25. `MLLoadBrick`

| Поле | Значение |
|---|---|
| Название RU | Загрузить веса |
| Название EN | Load weights |
| Строки (`strings.xml` → `values-ru`) | `brick_ml_load`: EN="Load weights" / RU="Загрузить веса"; `brick_ml_file`: EN="Project file" / RU="Файл проекта" |
| Параметры | `STRING` (`brick_ml_edit_filename`) |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/MLLoadBrick.java:31` |
| Класс экшена (`content/actions`) | `MLFileAction` (`catroid/src/main/java/org/catrobat/catroid/content/actions/MLFileAction.kt:9`; via `createMLFileAction`, `ActionFactory.java:5042`) |
| Описание (строго `BrickInfo.java` + код) | RU: Загружает веса нейронной сети из файла проекта с указанным именем (машинное обучение, Pocketensor).<br>EN: Loads neural network weights from the project file with the given name (machine learning, Pocketensor). |
| Вызов в фабрике | `MLLoadBrick("model.bin")` |
| Layout | `catroid/src/main/res/layout/brick_ml_file_load.xml` |
