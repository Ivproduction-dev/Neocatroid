# 3D Legacy / 3D Legacy

Категория `category_threed`: RU «3D Legacy», EN «3D Legacy».

Источник состава и порядка: `catroid/src/main/java/org/catrobat/catroid/ui/fragment/CategoryBricksFactory.kt:3587`, метод `setupThreedCategoryList`.

Правила документа:

- Порядок бриков строго по фабрике; подгруппы — `SubCategoryHeaderBrick` из того же метода.
- Названия RU/EN — по первому `@string` главного layout (`getViewResource`) из `catroid/src/main/res/values/strings.xml` и `values-ru/strings.xml`; у бриков без текстовой подписи — значения `R.array.*` из `arrays.xml`.
- Параметры — `addAllowedBrickField(BrickField.*)` + спиннер по факту кода брика.
- Экшен — фактический класс из `content/actions` через `ActionFactory.create*` (резолв по `ActionFactory.java`); у ScriptBrick экшена нет.
- Описание — 1–2 строки строго по `BrickInfo.java` + факт кода; домыслов нет.

Всего бриков в этом файле: 118 (заголовки подгрупп не считаются).

## Объекты и меши / Objects & Meshes (`subcategory_3d_objects`)

### 1. `Create3dObjectBrick`

| Поле | Значение |
|---|---|
| Название RU | Создать 3D объект с ID |
| Название EN | Create 3D object with ID |
| Ключ строки / layout | `brick_create_3d_object_text_1` / `brick_create_3d_object` |
| Параметры | Formula: VALUE (`brick_create_3d_object_id`); Formula: VALUE_2 (`brick_create_3d_object_model_path`) |
| Дефолт в палитре | ("myObject", "model.obj") |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/Create3dObjectBrick.java:8` |
| Класс экшена | Create3dObjectAction (catroid/src/main/java/org/catrobat/catroid/content/actions/Create3dObjectAction.kt:11) — через createCreate3dObjectAction |
| Описание | Создает 3D обьект из файла. ВАЖНО: поддерживаемые форматы: obj, .glb, .gltf и некоторые другие EN: Creates a 3D object from a file. IMPORTANT: supported formats: obj, .glb, .gltf and some others. Код: создаёт `Create3dObjectAction` в `addActionToSequence` через `createCreate3dObjectAction`. |

### 2. `CreateCubeBrick`

| Поле | Значение |
|---|---|
| Название RU | Создать куб с ID |
| Название EN | Create cube with ID |
| Ключ строки / layout | `brick_create_cube` / `brick_create_cube` |
| Параметры | Formula: VALUE (`brick_create_cube_id`) |
| Дефолт в палитре | ("myObject") |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/CreateCubeBrick.java:8` |
| Класс экшена | CreateCubeAction (catroid/src/main/java/org/catrobat/catroid/content/actions/CreateCubeAction.java:8) — через createCube |
| Описание | Создает 3D куб ВАЖНО: не работает в рендере версии 2.0 (куб невидимый) EN: Creates a 3D cube. IMPORTANT: does not work in render version 2.0 (cube is invisible). Код: создаёт `CreateCubeAction` в `addActionToSequence` через `createCube`. |

### 3. `CreateSphereBrick`

| Поле | Значение |
|---|---|
| Название RU | Создать шар с ID |
| Название EN | Create sphere with ID |
| Ключ строки / layout | `brick_create_sphere` / `brick_create_sphere` |
| Параметры | Formula: VALUE_1 (`brick_create_sphere_id`) |
| Дефолт в палитре | ("myObject") |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/CreateSphereBrick.java:8` |
| Класс экшена | CreateSphereAction (catroid/src/main/java/org/catrobat/catroid/content/CreateSphereAction.java:9) — через createCreateSphereAction |
| Описание | Создает 3D сферу ВАЖНО: не работает в рендере версии 2.0 (сфера невидимая) EN: Creates a 3D sphere. IMPORTANT: does not work in render version 2.0 (sphere is invisible). Код: создаёт `CreateSphereAction` в `addActionToSequence` через `createCreateSphereAction`. |

### 4. `ThreedCreateCylinderBrick`

| Поле | Значение |
|---|---|
| Название RU | Создать цилиндр с ID |
| Название EN | Create cylinder with ID |
| Ключ строки / layout | `brick_threed_create_cylinder` / `brick_threed_create_cylinder` |
| Параметры | Formula: VALUE_1 (`brick_threed_create_cylinder_id`) |
| Дефолт в палитре | ("myObject") |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/ThreedCreateCylinderBrick.java:8` |
| Класс экшена | ThreedCreateCylinderAction (catroid/src/main/java/org/catrobat/catroid/content/actions/ThreedCreateCylinderAction.kt:8) — через createThreedCreateCylinderAction |
| Описание | Создаёт в 3D-сцене цилиндр с указанным ID, по которому к нему затем можно обращаться из других 3D-блоков. EN: Creates a cylinder in the 3D scene with the given ID, which can then be referenced by other 3D bricks. Код: создаёт `ThreedCreateCylinderAction` в `addActionToSequence` через `createThreedCreateCylinderAction`. |

### 5. `Remove3dObjectBrick`

| Поле | Значение |
|---|---|
| Название RU | Удалить 3D объект |
| Название EN | Remove 3D object |
| Ключ строки / layout | `brick_remove_3d_object_text` / `brick_remove_3d_object` |
| Параметры | Formula: VALUE (`brick_remove_3d_object_id`) |
| Дефолт в палитре | ("myObject") |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/Remove3dObjectBrick.java:8` |
| Класс экшена | Remove3dObjectAction (catroid/src/main/java/org/catrobat/catroid/content/actions/Remove3dObjectAction.java:9) — через createRemove3dObjectAction |
| Описание | Удаляет 3D обьект EN: Removes a 3D object. Код: создаёт `Remove3dObjectAction` в `addActionToSequence` через `createRemove3dObjectAction`. |

### 6. `CreateSpringConstraintBrick`

| Поле | Значение |
|---|---|
| Название RU | Создать пружинную связь |
| Название EN | Create spring constraint |
| Ключ строки / layout | `brick_create_spring_constraint_title` / `brick_create_spring_constraint` |
| Параметры | Formula: VALUE_1 (`brick_spring_constraint_edit_id`); Formula: VALUE_2 (`brick_spring_constraint_edit_a`); Formula: VALUE_3 (`brick_spring_constraint_edit_b`); Formula: VALUE_4 (`brick_spring_constraint_edit_pax`); Formula: VALUE_5 (`brick_spring_constraint_edit_pay`); Formula: VALUE_6 (`brick_spring_constraint_edit_paz`); Formula: VALUE_7 (`brick_spring_constraint_edit_pbx`); Formula: VALUE_8 (`brick_spring_constraint_edit_pby`); Formula: VALUE_9 (`brick_spring_constraint_edit_pbz`) |
| Дефолт в палитре | ("constraint1", "objectA", "objectB") |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/CreateSpringConstraintBrick.java:30` |
| Класс экшена | CreateSpringConstraintAction (catroid/src/main/java/org/catrobat/catroid/content/actions/CreateSpringConstraintAction.kt:11) — через createSpringConstraintAction |
| Описание | Создает пружинное соединение между двумя 3D-объектами с точками крепления A и B. EN: Creates a spring constraint between two 3D objects with anchor points A and B. Код: создаёт `CreateSpringConstraintAction` в `addActionToSequence` через `createSpringConstraintAction`. |

### 7. `RemoveObjectsByPrefixBrick`

| Поле | Значение |
|---|---|
| Название RU | Удалить 3D-объекты по префиксу |
| Название EN | Remove 3D objects by prefix |
| Ключ строки / layout | `remove_objects_by_prefix` / `brick_remove_objects_by_prefix` |
| Параметры | Formula: VALUE (`brick_remove_prefix_val`) |
| Дефолт в палитре | ("wall_") |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/RemoveObjectsByPrefixBrick.java:8` |
| Класс экшена | RemoveObjectsByPrefixAction (catroid/src/main/java/org/catrobat/catroid/content/actions/RemoveObjectsByPrefixAction.kt:8) — через createRemoveObjectsByPrefixAction |
| Описание | Удаляет все объекты с определенным префиксом EN: Removes all objects with a given prefix Код: создаёт `RemoveObjectsByPrefixAction` в `addActionToSequence` через `createRemoveObjectsByPrefixAction`. |

### 8. `CloneObjectBrick`

| Поле | Значение |
|---|---|
| Название RU | Клонировать обьект |
| Название EN | Clone object |
| Ключ строки / layout | `brick_clone_object` / `brick_clone_object` |
| Параметры | Formula: VALUE_1 (`brick_clone_source_edit`); Formula: VALUE_2 (`brick_clone_new_name_edit`) |
| Дефолт в палитре | ("myObject", "clonedObject") |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/CloneObjectBrick.java:7` |
| Класс экшена | CloneObjectAction (catroid/src/main/java/org/catrobat/catroid/content/actions/CloneObjectAction.kt:8) — через createCloneObjectAction |
| Описание | Клонирует 3D обьект EN: Clones a 3D object Код: создаёт `CloneObjectAction` в `addActionToSequence` через `createCloneObjectAction`. |

### 9. `SetSpawnInvisibleBrick`

| Поле | Значение |
|---|---|
| Название RU | Создать скрытым объект |
| Название EN | Spawn invisible object with ID |
| Ключ строки / layout | `brick_spawn_invisible_text` / `brick_set_spawn_invisible` |
| Параметры | Formula: STRING (`brick_spawn_invisible_edit_text`) |
| Дефолт в палитре | ("myObject") |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/SetSpawnInvisibleBrick.java:8` |
| Класс экшена | KeyframeAnimationAction (catroid/src/main/java/org/catrobat/catroid/content/actions/KeyframeAnimationAction.kt:8) — через createSetSpawnInvisibleAction |
| Описание | Помечает 3D-объект с указанным ID, чтобы он появлялся (спавнился) невидимым. EN: Marks the 3D object with the given ID to spawn (appear) invisible. Код: создаёт `KeyframeAnimationAction` в `addActionToSequence` через `createSetSpawnInvisibleAction`. |

### 10. `SetActiveBrick`

| Поле | Значение |
|---|---|
| Название RU | Сделать объект |
| Название EN | Set object |
| Ключ строки / layout | `brick_set_active` / `brick_set_active` |
| Параметры | Formula: OBJECT_NAME (`brick_set_active_object_name`); Spinner `R.array.brick_set_active_states` EN: Active / Inactive |
| Дефолт в палитре | ("myObject", true) |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/SetActiveBrick.java:14` |
| Класс экшена | SetActiveAction (catroid/src/main/java/org/catrobat/catroid/content/actions/SetActiveAction.kt:8) — через createSetActiveAction |
| Описание | Включает или выключает обьект (как в Unity). ВАЖНО: дети обьекта тоже выключаются вместе с ним EN: Enables or disables an object (like in Unity). IMPORTANT: the object's children are also disabled along with it Код: создаёт `SetActiveAction` в `addActionToSequence` через `createSetActiveAction`. |

### 11. `SetObjectColorBrick`

| Поле | Значение |
|---|---|
| Название RU | Задать цвет объекта |
| Название EN | Set color of object |
| Ключ строки / layout | `brick_set_object_color_of` / `brick_set_object_color` |
| Параметры | Formula: VALUE_1 (`brick_set_object_color_id`); Formula: VALUE_2 (`brick_set_object_color_r`); Formula: VALUE_3 (`brick_set_object_color_g`); Formula: VALUE_4 (`brick_set_object_color_b`) |
| Дефолт в палитре | ("myObject", 1.0, 0.0, 0.0) |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/SetObjectColorBrick.java:8` |
| Класс экшена | SetObjectColorAction (catroid/src/main/java/org/catrobat/catroid/content/actions/SetObjectColorAction.java:8) — через createSetObjectColorAction |
| Описание | Задает цвет обьекту ВАЖНО: не работает в рендере версии 2.0 EN: Sets the color of an object. IMPORTANT: does not work in render version 2.0. Код: создаёт `SetObjectColorAction` в `addActionToSequence` через `createSetObjectColorAction`. |

### 12. `SetObjectTextureBrick`

| Поле | Значение |
|---|---|
| Название RU | Задать текстуру объекта |
| Название EN | Set texture of object |
| Ключ строки / layout | `brick_set_object_texture_of` / `brick_set_object_texture` |
| Параметры | Formula: VALUE (`brick_object_texture_id`); Formula: VALUE_2 (`brick_object_texture_file`) |
| Дефолт в палитре | ("myObject", "texture.png") |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/SetObjectTextureBrick.java:8` |
| Класс экшена | SetObjectTextureAction (catroid/src/main/java/org/catrobat/catroid/content/actions/SetObjectTextureAction.java:9) — через createSetObjectTextureAction |
| Описание | Задает текстуру обьекту ВАЖНО: не работает в рендере версии 2.0\nЕсли в начале написать \"buffer://\" а потом имя буфера - текстура возьмется от туда (нужно чтоб отображать 2D сцены в 3D) EN: Sets the texture of an object. IMPORTANT: does not work in render version 2.0.\nIf you prefix with \"buffer://\" followed by the buffer name, the texture will be taken from there (needed to display 2D scenes in 3D). Код: создаёт `SetObjectTextureAction` в `addActionToSequence` через `createSetObjectTextureAction`. |

### 13. `Replace3DModelBrick`

| Поле | Значение |
|---|---|
| Название RU | Заменить 3D модель |
| Название EN | Replace 3D model |
| Ключ строки / layout | `brick_replace_3d_model_title` / `brick_replace_3d_model` |
| Параметры | — (без формульных полей) |
| Дефолт в палитре | ("myObject", "model.glb") |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/Replace3DModelBrick.java:8` |
| Класс экшена | Replace3DModelAction (catroid/src/main/java/org/catrobat/catroid/content/actions/Replace3DModelAction.kt:9) — через createReplace3DModelAction |
| Описание | Заменяет 3D-модель объекта по ID на модель из указанного файла. EN: Replaces the 3D model of the object with the given ID by the model from the specified file. Код: создаёт `Replace3DModelAction` в `addActionToSequence` через `createReplace3DModelAction`. |

### 14. `Export3dObjectToGlbBrick`

| Поле | Значение |
|---|---|
| Название RU | Экспорт 3D GLB |
| Название EN | Export 3D GLB |
| Ключ строки / layout | `brick_export_3d_glb_title` / `brick_export_3d_glb` |
| Параметры | Formula: VALUE_1 (`brick_export_3d_glb_id_field`); Formula: VALUE_2 (`brick_export_3d_glb_file_field`) |
| Дефолт в палитре | ("myObject", "export.glb") |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/Export3dObjectToGlbBrick.java:8` |
| Класс экшена | Export3dObjectToGlbAction (catroid/src/main/java/org/catrobat/catroid/content/actions/Export3dObjectToGlbAction.kt:17) — через createExport3dObjectToGlbAction |
| Описание | Экспортирует 3D-объект по ID в файл формата GLB в папке проекта. EN: Exports the 3D object with the given ID into a GLB file in the project folder. Код: создаёт `Export3dObjectToGlbAction` в `addActionToSequence` через `createExport3dObjectToGlbAction`. |

### 15. `BakeByPrefixBrick`

| Поле | Значение |
|---|---|
| Название RU | Запечь объекты с именем |
| Название EN | Bake objects with name |
| Ключ строки / layout | `brick_bake_prefix` / `brick_bake_by_prefix` |
| Параметры | Formula: VALUE_1 (`brick_bake_prefix_edit`); Formula: VALUE_2 (`brick_bake_result_edit`) |
| Дефолт в палитре | ("wall_", "bakedObject") |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/BakeByPrefixBrick.java:31` |
| Класс экшена | BakeByPrefixAction (catroid/src/main/java/org/catrobat/catroid/content/actions/BakeByPrefixAction.kt:8) — через createBakeByPrefixAction |
| Описание | 'Запекает' все статичные обьекты, имеющие в имени префикс, что вы ввели в 1 большой обьект. (Если у вас сотни статичных обьектов используйте это! это поможет в оптимизации) EN: 'Bakes' all static objects with the prefix you entered in their name into 1 large object. (If you have hundreds of static objects use this! it will help with optimization) Код: создаёт `BakeByPrefixAction` в `addActionToSequence` через `createBakeByPrefixAction`. |

## Движение и связь / Motion & Hierarchy (`subcategory_3d_transform`)

### 16. `Set3dPositionBrick`

| Поле | Значение |
|---|---|
| Название RU | Задать 3D позицию объекта |
| Название EN | Set 3D position of object |
| Ключ строки / layout | `brick_set_3d_position_of` / `brick_set_3d_position` |
| Параметры | Formula: VALUE_1 (`brick_set_3d_position_object_id`); Formula: VALUE_2 (`brick_set_3d_position_x`); Formula: VALUE_3 (`brick_set_3d_position_y`); Formula: VALUE_4 (`brick_set_3d_position_z`) |
| Дефолт в палитре | ("myObject", 10.0, -5.0, 0.0) |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/Set3dPositionBrick.java:8` |
| Класс экшена | Set3dPositionAction (catroid/src/main/java/org/catrobat/catroid/content/actions/Set3dPositionAction.java:8) — через createSet3dPositionAction |
| Описание | Задает позицию 3D обьекта по его ID (координаты X, Y, Z). EN: Sets the position of a 3D object by its ID (X, Y, Z coordinates). Код: создаёт `Set3dPositionAction` в `addActionToSequence` через `createSet3dPositionAction`. |

### 17. `GlideTo3DBrick`

| Поле | Значение |
|---|---|
| Название RU | Скользить 3D-объектом |
| Название EN | Glide 3D object |
| Ключ строки / layout | `brick_glide_3d` / `brick_glide_to_3d` |
| Параметры | Formula: VALUE_1 (`brick_glide_3d_object_id`); Formula: VALUE_2 (`brick_glide_3d_x`); Formula: VALUE_3 (`brick_glide_3d_y`); Formula: VALUE_4 (`brick_glide_3d_z`); Formula: DURATION_IN_SECONDS (`brick_glide_3d_duration`); Spinner `R.array.brick_easing_types` EN: None / Linear / Quad Ease In / Quad Ease Out / Quad Ease In Out / Cubic Ease In / Cubic Ease Out / Cubic Ease In Out / Quart Ease In / Quart Ease Out / Quart Ease In Out / Quint Ease In / Quint Ease Out / Quint Ease In Out / Sine Ease In / Sine Ease Out / Sine Ease In Out / Expo Ease In / Expo Ease Out / Expo Ease In Out / Circ Ease In / Circ Ease Out / Circ Ease In Out / Back Ease In / Back Ease Out / Back Ease In Out / Elastic Ease In / Elastic Ease Out / Elastic Ease In Out / Bounce Ease In / Bounce Ease Out / Bounce Ease In Out |
| Дефолт в палитре | ("myObject", 10.0, -5.0, 0.0, 2.0) |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/GlideTo3DBrick.java:14` |
| Класс экшена | GlideTo3DAction (catroid/src/main/java/org/catrobat/catroid/content/actions/GlideTo3DAction.kt:11) — через createGlideTo3DAction |
| Описание | Плавно перемещает 3D-объект в заданную позицию (X, Y, Z) за указанное время с функцией сглаживания. EN: Smoothly glides the 3D object to the given position (X, Y, Z) within the specified time using an easing function. Код: создаёт `GlideTo3DAction` в `addActionToSequence` через `createGlideTo3DAction`. |

### 18. `Set3dRotationBrick`

| Поле | Значение |
|---|---|
| Название RU | Задать вращение объекта |
| Название EN | Set 3D rotation of object |
| Ключ строки / layout | `brick_set_3d_rotation_of` / `brick_set_3d_rotation` |
| Параметры | Formula: VALUE_1 (`brick_set_3d_rotation_object_id`); Formula: VALUE_2 (`brick_set_3d_rotation_yaw`); Formula: VALUE_3 (`brick_set_3d_rotation_pitch`); Formula: VALUE_4 (`brick_set_3d_rotation_roll`) |
| Дефолт в палитре | ("myObject", 1.0, 0.0, 0.0) |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/Set3dRotationBrick.java:8` |
| Класс экшена | Set3dRotationAction (catroid/src/main/java/org/catrobat/catroid/content/actions/Set3dRotationAction.java:8) — через createSet3dRotationAction |
| Описание | Задает поворот 3D обьекта по его ID (рысканье/yaw, тангаж/pitch, крен/roll). EN: Sets the rotation of a 3D object by its ID (yaw, pitch, roll). Код: создаёт `Set3dRotationAction` в `addActionToSequence` через `createSet3dRotationAction`. |

### 19. `Set3dScaleBrick`

| Поле | Значение |
|---|---|
| Название RU | Задать 3D размер объекта |
| Название EN | Set 3D scale of object |
| Ключ строки / layout | `brick_set_3d_scale_of` / `brick_set_3d_scale` |
| Параметры | Formula: VALUE_1 (`brick_set_3d_scale_object_id`); Formula: VALUE_2 (`brick_set_3d_scale_x`); Formula: VALUE_3 (`brick_set_3d_scale_y`); Formula: VALUE_4 (`brick_set_3d_scale_z`) |
| Дефолт в палитре | ("myObject", 2.0, 1.0, 1.5) |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/Set3dScaleBrick.java:8` |
| Класс экшена | Set3dScaleAction (catroid/src/main/java/org/catrobat/catroid/content/actions/Set3dScaleAction.java:8) — через createSet3dScaleAction |
| Описание | Задает размер обьекта ВАЖНО: во избежании проблем используйте одинаковые размеры по всем осям, например: 1, 1, 1; 0.1, 0.1, 0.1; 3, 3, 3 и т.д. EN: Sets the size of an object. IMPORTANT: to avoid issues, use the same size on all axes, for example: 1, 1, 1; 0.1, 0.1, 0.1; 3, 3, 3, etc. Код: создаёт `Set3dScaleAction` в `addActionToSequence` через `createSet3dScaleAction`. |

### 20. `ObjectLookAtBrick`

| Поле | Значение |
|---|---|
| Название RU | Повернуть объект |
| Название EN | Point object |
| Ключ строки / layout | `brick_object_look_at_object` / `brick_object_look_at` |
| Параметры | Formula: VALUE_1 (`brick_object_look_at_id`); Formula: VALUE_2 (`brick_object_look_at_x`); Formula: VALUE_3 (`brick_object_look_at_y`); Formula: VALUE_4 (`brick_object_look_at_z`) |
| Дефолт в палитре | ("myObject", 0.0, 0.0, 0.0) |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/ObjectLookAtBrick.java:8` |
| Класс экшена | ObjectLookAtAction (catroid/src/main/java/org/catrobat/catroid/content/actions/ObjectLookAtAction.java:8) — через createObjectLookAtAction |
| Описание | Разворачивает указанный 3D-объект так, чтобы он был направлен на точку с координатами X, Y и Z (3D-сцена). EN: Rotates the specified 3D object so that it faces the point with coordinates X, Y and Z (3D scene). Код: создаёт `ObjectLookAtAction` в `addActionToSequence` через `createObjectLookAtAction`. |

### 21. `ObjectLookAtCorrectBrick`

| Поле | Значение |
|---|---|
| Название RU | Смотреть на |
| Название EN | Look at |
| Ключ строки / layout | `brick_object_look_at_correct_object` / `brick_object_look_at_correct` |
| Параметры | Formula: VALUE_1 (`brick_object_look_at_correct_id`); Formula: VALUE_2 (`brick_object_look_at_correct_x`); Formula: VALUE_3 (`brick_object_look_at_correct_y`); Formula: VALUE_4 (`brick_object_look_at_correct_z`) |
| Дефолт в палитре | ("myObject", 0.0, 0.0, 0.0) |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/ObjectLookAtCorrectBrick.java:8` |
| Класс экшена | ObjectLookAtCorrectAction (catroid/src/main/java/org/catrobat/catroid/content/actions/ObjectLookAtCorrectAction.kt:10) — через createObjectLookAtCorrectAction |
| Описание | Направляет 3D-объект на точку с заданными координатами X, Y, Z. EN: Points a 3D object at a position with the given X, Y, Z coordinates. Код: создаёт `ObjectLookAtCorrectAction` в `addActionToSequence` через `createObjectLookAtCorrectAction`. |

### 22. `ThreedAlignNormalBrick`

| Поле | Значение |
|---|---|
| Название RU | Выровнять по нормали |
| Название EN | Align normal |
| Ключ строки / layout | `brick_align_normal_1` / `brick_threed_align_normal` |
| Параметры | Formula: VALUE_1 (`brick_align_id`); Formula: VALUE_2 (`brick_align_nx`); Formula: VALUE_3 (`brick_align_ny`); Formula: VALUE_4 (`brick_align_nz`) |
| Дефолт в палитре | ("myObject", -1.0, 0.0, 0.0) |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/ThreedAlignNormalBrick.java:7` |
| Класс экшена | ThreedAlignNormalAction (catroid/src/main/java/org/catrobat/catroid/content/actions/ThreedAlignNormalAction.kt:9) — через createThreedAlignNormalAction |
| Описание | Выравнивает 3D-объект с заданным ID по вектору нормали поверхности с компонентами X, Y и Z. Используется для 3D-сцены (нужен активный 3D-менеджер). EN: Aligns the 3D object with the given ID to the surface normal vector defined by its X, Y and Z components. Used in the 3D scene (requires an active 3D manager). Код: создаёт `ThreedAlignNormalAction` в `addActionToSequence` через `createThreedAlignNormalAction`. |

### 23. `SetParentBrick`

| Поле | Значение |
|---|---|
| Название RU | Установить родителя |
| Название EN | Set parent |
| Ключ строки / layout | `brick_set_parent` / `brick_set_parent` |
| Параметры | Formula: CHILD_OBJECT (`brick_set_parent_child_edit`); Formula: PARENT_OBJECT (`brick_set_parent_parent_edit`) |
| Дефолт в палитре | ("child", "parent") |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/SetParentBrick.java:8` |
| Класс экшена | SetParentAction (catroid/src/main/java/org/catrobat/catroid/content/actions/SetParentAction.kt:8) — через createSetParentAction |
| Описание | Задает обьекту родителя (полезно для рук, шапок, камеры от 1 лица, ящиков и др.). P.S. обьект двигается и вращается вместо со своим родителем EN: Sets a parent for an object (useful for hands, hats, first-person camera, crates, etc.). P.S. the object moves and rotates together with its parent Код: создаёт `SetParentAction` в `addActionToSequence` через `createSetParentAction`. |

### 24. `RemoveParentBrick`

| Поле | Значение |
|---|---|
| Название RU | Убрать родителя у |
| Название EN | Remove parent from |
| Ключ строки / layout | `brick_remove_parent` / `brick_remove_parent` |
| Параметры | Formula: CHILD_OBJECT (`brick_remove_parent_object_edit`) |
| Дефолт в палитре | ("child") |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/RemoveParentBrick.java:8` |
| Класс экшена | RemoveParentAction (catroid/src/main/java/org/catrobat/catroid/content/actions/RemoveParentAction.kt:8) — через createRemoveParentAction |
| Описание | Удаляет родителя для обьекта EN: Removes an object's parent Код: создаёт `RemoveParentAction` в `addActionToSequence` через `createRemoveParentAction`. |

### 25. `AttachToCameraBrick`

| Поле | Значение |
|---|---|
| Название RU | Привязать объект к камере |
| Название EN | Attach object to camera |
| Ключ строки / layout | `brick_attach_to_camera` / `brick_attach_to_camera` |
| Параметры | Formula: NAME (`brick_attach_camera_object_name`) |
| Дефолт в палитре | ("myObject") |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/AttachToCameraBrick.java:8` |
| Класс экшена | AttachToCameraAction (catroid/src/main/java/org/catrobat/catroid/content/actions/AttachToCameraAction.kt:8) — через createAttachToCameraAction |
| Описание | Привязывает объект к камере (используйте для фонариков / обьектов в руках и др.) EN: Attaches an object to the camera (use for flashlights / objects in hands, etc.) Код: создаёт `AttachToCameraAction` в `addActionToSequence` через `createAttachToCameraAction`. |

### 26. `DetachFromCameraBrick`

| Поле | Значение |
|---|---|
| Название RU | Отвязать объект от камеры |
| Название EN | Detach object from camera |
| Ключ строки / layout | `brick_detach_from_camera` / `brick_detach_from_camera` |
| Параметры | Formula: NAME (`brick_detach_camera_object_name`) |
| Дефолт в палитре | ("myObject") |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/DetachFromCameraBrick.java:8` |
| Класс экшена | DetachFromCameraAction (catroid/src/main/java/org/catrobat/catroid/content/actions/DetachFromCameraAction.kt:8) — через createDetachFromCameraAction |
| Описание | Отвязывает объект от камеры (UNO Reverse для привязки, хахах) EN: Detaches an object from the camera (UNO Reverse for attachment, hahaha) Код: создаёт `DetachFromCameraAction` в `addActionToSequence` через `createDetachFromCameraAction`. |

## Управление камерой / Camera Control (`subcategory_3d_camera`)

### 27. `SetCameraPositionBrick`

| Поле | Значение |
|---|---|
| Название RU | Задать позицию камеры на |
| Название EN | Set camera position to |
| Ключ строки / layout | `brick_set_camera_position` / `brick_set_camera_position` |
| Параметры | Formula: VALUE (`brick_set_camera_position_x`); Formula: VALUE_2 (`brick_set_camera_position_y`); Formula: VALUE_3 (`brick_set_camera_position_z`) |
| Дефолт в палитре | (200.0, 200.0, 200.0) |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/SetCameraPositionBrick.java:8` |
| Класс экшена | SetCameraPosition2Action (catroid/src/main/java/org/catrobat/catroid/content/actions/SetCameraPosition2Action.kt:8) — через createSetCameraPositionAction |
| Описание | Задает позицию 3D камеры (координаты X, Y, Z). EN: Sets the position of the 3D camera (X, Y, Z coordinates). Код: создаёт `SetCameraPosition2Action` в `addActionToSequence` через `createSetCameraPositionAction`. |

### 28. `CameraLookAtBrick`

| Поле | Значение |
|---|---|
| Название RU | Направить камеру в точку |
| Название EN | Point camera to |
| Ключ строки / layout | `brick_camera_look_at` / `brick_camera_look_at` |
| Параметры | Formula: VALUE (`brick_camera_look_at_x`); Formula: VALUE_2 (`brick_camera_look_at_y`); Formula: VALUE_3 (`brick_camera_look_at_z`) |
| Дефолт в палитре | (0.0, 0.0, 0.0) |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/CameraLookAtBrick.java:8` |
| Класс экшена | CameraLookAtAction (catroid/src/main/java/org/catrobat/catroid/content/actions/CameraLookAtAction.java:8) — через createCameraLookAtAction |
| Описание | Направляет камеру на указанную точку в 3D пространстве (X, Y, Z). EN: Points the camera at the specified point in 3D space (X, Y, Z). Код: создаёт `CameraLookAtAction` в `addActionToSequence` через `createCameraLookAtAction`. |

### 29. `SetCameraRotationBrick`

| Поле | Значение |
|---|---|
| Название RU | Задать вращение камеры на |
| Название EN | Set camera rotation to |
| Ключ строки / layout | `brick_set_camera_rotation` / `brick_set_camera_rotation` |
| Параметры | Formula: VALUE_1 (`brick_set_camera_rotation_yaw`); Formula: VALUE_2 (`brick_set_camera_rotation_pitch`); Formula: VALUE_3 (`brick_set_camera_rotation_roll`) |
| Дефолт в палитре | (0.0, 180.0, 0.0) |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/SetCameraRotationBrick.java:8` |
| Класс экшена | SetCameraRotation2Action (catroid/src/main/java/org/catrobat/catroid/content/actions/SetCameraRotation2Action.kt:8) — через createSetCameraRotationAction |
| Описание | Задает поворот камеры (рысканье/yaw, тангаж/pitch, крен/roll). EN: Sets the rotation of the camera (yaw, pitch, roll). Код: создаёт `SetCameraRotation2Action` в `addActionToSequence` через `createSetCameraRotationAction`. |

### 30. `RotateCameraByBrick`

| Поле | Значение |
|---|---|
| Название RU | Повернуть камеру (относительно) |
| Название EN | Rotate camera (relevative) |
| Ключ строки / layout | `brick_rotate_camera_by` / `brick_rotate_camera_by` |
| Параметры | Formula: PITCH (`rotate_pitch_edit`); Formula: YAW (`rotate_yaw_edit`); Formula: ROLL (`rotate_roll_edit`) |
| Дефолт в палитре | (0.0f, 180.0f, 0.0f) |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/RotateCameraByBrick.java:8` |
| Класс экшена | RotateCameraByAction (catroid/src/main/java/org/catrobat/catroid/content/actions/RotateCameraByAction.kt:8) — через createRotateCameraByAction |
| Описание | Поворачивает камеру относительно ее текущего вращения EN: Rotates the camera relative to its current rotation Код: создаёт `RotateCameraByAction` в `addActionToSequence` через `createRotateCameraByAction`. |

### 31. `SetCameraRangeBrick`

| Поле | Значение |
|---|---|
| Название RU | Задать дальность камеры |
| Название EN | Set camera range |
| Ключ строки / layout | `brick_set_camera_range_header` / `brick_set_camera_range` |
| Параметры | Formula: VALUE_1 (`brick_set_camera_range_near_value`); Formula: VALUE_2 (`brick_set_camera_range_far_value`) |
| Дефолт в палитре | (0.1, 2500.0) |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/SetCameraRangeBrick.java:8` |
| Класс экшена | SetCameraRangeAction (catroid/src/main/java/org/catrobat/catroid/content/actions/SetCameraRangeAction.kt:9) — через createSetCameraRangeAction |
| Описание | Задает ближнюю (near) и дальнюю (far) границы отсечения камеры, за которыми обьекты не рисуются. EN: Sets the camera's near and far clipping planes, beyond which objects are not drawn. Код: создаёт `SetCameraRangeAction` в `addActionToSequence` через `createSetCameraRangeAction`. |

### 32. `CameraSettingsBrick`

| Поле | Значение |
|---|---|
| Название RU | Настроить камеру 3D |
| Название EN | 3D Camera settings |
| Ключ строки / layout | `brick_camera_settings` / `brick_camera_settings` |
| Параметры | Formula: FOV (`camera_fov_edit`); Formula: INTENSITY (`camera_shake_int_edit`); Formula: DURATION (`camera_shake_dur_edit`) |
| Дефолт в палитре | (67f, 2f, 2f) |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/CameraSettingsBrick.java:8` |
| Класс экшена | CameraSettingsAction (catroid/src/main/java/org/catrobat/catroid/content/actions/CameraSettingsAction.kt:8) — через createCameraSettingsAction |
| Описание | Настраивает поле зрения и тряску камеры EN: Configures the field of view and camera shake Код: создаёт `CameraSettingsAction` в `addActionToSequence` через `createCameraSettingsAction`. |

### 33. `SetThirdPersonCameraBrick`

| Поле | Значение |
|---|---|
| Название RU | Камера от 3-го лица |
| Название EN | Set 3rd person camera |
| Ключ строки / layout | `brick_set_third_person_camera` / `brick_set_third_person_camera` |
| Параметры | Formula: OBJECT_ID (`brick_third_person_camera_object_id`); Formula: DISTANCE (`brick_third_person_camera_distance`); Formula: HEIGHT (`brick_third_person_camera_height`); Formula: PITCH (`brick_third_person_camera_pitch`) |
| Дефолт в палитре | ("myObject", 10.0, 10.0, -20.0) |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/SetThirdPersonCameraBrick.java:8` |
| Класс экшена | SetThirdPersonCameraAction (catroid/src/main/java/org/catrobat/catroid/content/actions/SetThirdPersonCameraAction.kt:8) — через createSetThirdPersonCameraAction |
| Описание | Устанавливает камеру от 3-го лица. Я сам хз как, удачи разобраться EN: Sets the camera to third-person view. I have no idea how it works, good luck figuring it out Код: создаёт `SetThirdPersonCameraAction` в `addActionToSequence` через `createSetThirdPersonCameraAction`. |

### 34. `CameraTrackingBrick`

| Поле | Значение |
|---|---|
| Название RU | Привязать камеру к |
| Название EN | Attach camera to |
| Ключ строки / layout | `brick_camera_track_title` / `brick_camera_tracking` |
| Параметры | Formula: NAME (`brick_camera_track_id`); Formula: X_POSITION (`brick_camera_track_px`); Formula: Y_POSITION (`brick_camera_track_py`); Formula: Z_POSITION (`brick_camera_track_pz`); Formula: YAW (`brick_camera_track_ry`); Formula: PITCH (`brick_camera_track_rp`); Formula: ROLL (`brick_camera_track_rr`); Spinner `R.array.brick_camera_track_modes` |
| Дефолт в палитре | ("myObject", 0, 0f, 1f, 0f, 0f, 0f, 0f) |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/CameraTrackingBrick.java:14` |
| Класс экшена | CameraTrackingAction (catroid/src/main/java/org/catrobat/catroid/content/actions/CameraTrackingAction.kt:8) — через createCameraTrackingAction |
| Описание | Позволяет привязать камеру к объекту по позиции или вращению (ничего не будет дергаться! :>  ) EN: Allows attaching the camera to an object by position or rotation (nothing will jitter! :>  ) Код: создаёт `CameraTrackingAction` в `addActionToSequence` через `createCameraTrackingAction`. |

### 35. `SetFreeCameraBrick`

| Поле | Значение |
|---|---|
| Название RU | Свободная камера |
| Название EN | Set free camera |
| Ключ строки / layout | `brick_set_free_camera` / `brick_set_free_camera` |
| Параметры | — (без формульных полей) |
| Дефолт в палитре | () |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/SetFreeCameraBrick.java:7` |
| Класс экшена | SetFreeCameraAction (catroid/src/main/java/org/catrobat/catroid/content/actions/SetFreeCameraAction.kt:7) — через createSetFreeCameraAction |
| Описание | Возвращает камеру к свободному состоянию EN: Returns the camera to free state Код: создаёт `SetFreeCameraAction` в `addActionToSequence` через `createSetFreeCameraAction`. |

### 36. `CameraTouchControlBrick`

| Поле | Значение |
|---|---|
| Название RU | Управление камерой пальцем |
| Название EN | Touch camera control |
| Ключ строки / layout | `brick_camera_control` / `brick_camera_touch_control` |
| Параметры | Formula: ENABLED (`cam_enabled`); Formula: SENSITIVITY (`cam_sens`); Formula: X (`cam_area_x`); Formula: Y (`cam_area_y`); Formula: WIDTH (`cam_area_w`); Formula: HEIGHT (`cam_area_h`) |
| Дефолт в палитре | (1, 1f, 0f, 0f, 50f, 50f) |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/CameraTouchControlBrick.java:14` |
| Класс экшена | CameraTouchControlAction (catroid/src/main/java/org/catrobat/catroid/content/actions/CameraTouchControlAction.kt:8) — через createCameraTouchControlAction |
| Описание | Включает вращение камеры пальцем. X, Y, W (ширина), H (высота) ЗАДАЮТСЯ В ПРОЦЕНТАХ ОТ ВСЕГО ЭКРАНА! EN: Enables camera rotation with a finger. X, Y, W (width), H (height) ARE SET AS PERCENTAGES OF THE ENTIRE SCREEN! Код: создаёт `CameraTouchControlAction` в `addActionToSequence` через `createCameraTouchControlAction`. |

## Физика и суставы / Physics & Joints (`subcategory_3d_physics`)

### 37. `SetPhysicsStateBrick`

| Поле | Значение |
|---|---|
| Название RU | Задать физ. состояние |
| Название EN | Set physics state of |
| Ключ строки / layout | `brick_set_physics_state_of` / `brick_set_physics_state` |
| Параметры | Formula: VALUE_1 (`brick_set_physics_state_id`); Formula: VALUE_2 (`brick_set_physics_state_mass_value`); Spinner `R.array.brick_physics_shapes` EN: Cube / Sphere / Capsule; Spinner `R.array.brick_physics_states` EN: No physics / Static (Primitive) / Dynamic (Primitive) / Static complex / Kinematic |
| Дефолт в палитре | ("myObject", 2, 0, 1.0) |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/SetPhysicsStateBrick.java:14` |
| Класс экшена | SetPhysicsStateAction (catroid/src/main/java/org/catrobat/catroid/content/actions/SetPhysicsStateAction.java:9) — через createSetPhysicsStateAction |
| Описание | Задает физическое состояние обьекта: динамичный, статичный (примитив - куб), сложный статичный (повторяет форму модели) EN: Sets the physics state of an object: dynamic, static (primitive - cube), complex static (follows model shape). Код: создаёт `SetPhysicsStateAction` в `addActionToSequence` через `createSetPhysicsStateAction`. |

### 38. `SetRotationLockBrick`

| Поле | Значение |
|---|---|
| Название RU | Откл. физ. вращение |
| Название EN | Lock Physics Rotation |
| Ключ строки / layout | `brick_rotation_lock_title` / `brick_set_rotation_lock` |
| Параметры | Formula: STRING (`brick_rotation_lock_object_id`) |
| Дефолт в палитре | ("myObject", true, false, true) |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/SetRotationLockBrick.java:14` |
| Класс экшена | SetRotationLockAction (catroid/src/main/java/org/catrobat/catroid/content/actions/SetRotationLockAction.kt:8) — через createSetRotationLockAction |
| Описание | Блокирует вращение физического тела объекта по выбранным осям (X, Y, Z) по его ID. EN: Locks the physics rotation of an object (by ID) on the selected axes (X, Y, Z). Код: создаёт `SetRotationLockAction` в `addActionToSequence` через `createSetRotationLockAction`. |

### 39. `Set3dGravityBrick`

| Поле | Значение |
|---|---|
| Название RU | Задать гравитацию 3D миру |
| Название EN | Set 3D world gravity to |
| Ключ строки / layout | `brick_set_3d_gravity` / `brick_set_3d_gravity` |
| Параметры | Formula: VALUE_2 (`brick_set_3d_gravity_x`); Formula: VALUE_3 (`brick_set_3d_gravity_y`); Formula: VALUE_4 (`brick_set_3d_gravity_z`) |
| Дефолт в палитре | (0.0, -9.81, 0.0) |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/Set3dGravityBrick.java:8` |
| Класс экшена | не найден в коде брика |
| Описание | Задает гравитацию 3D-мира по осям X, Y и Z. EN: Sets the 3D world gravity along the X, Y and Z axes. Факт кода: прямой связи с ActionFactory нет. |

### 40. `Set3dVelocityBrick`

| Поле | Значение |
|---|---|
| Название RU | Задать скорость обьекту |
| Название EN | Set velocity to object |
| Ключ строки / layout | `brick_set_3d_velocity` / `brick_set_3d_velocity` |
| Параметры | Formula: VALUE_1 (`brick_set_3d_velocity_object_id`); Formula: VALUE_2 (`brick_set_3d_velocity_x`); Formula: VALUE_3 (`brick_set_3d_velocity_y`); Formula: VALUE_4 (`brick_set_3d_velocity_z`) |
| Дефолт в палитре | ("myObject", 0.0, 25.0, 0.0) |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/Set3dVelocityBrick.java:8` |
| Класс экшена | не найден в коде брика |
| Описание | Задает скорость 3D обьекту по его ID в виде вектора (X, Y, Z). EN: Sets the velocity of a 3D object by its ID as a vector (X, Y, Z). Факт кода: прямой связи с ActionFactory нет. |

### 41. `Apply3dForceBrick`

| Поле | Значение |
|---|---|
| Название RU | Применить силу к обьекту |
| Название EN | Apply force to object |
| Ключ строки / layout | `brick_apply_3d_force` / `brick_apply_3d_force` |
| Параметры | Formula: VALUE_1 (`brick_apply_3d_force_object_id`); Formula: VALUE_2 (`brick_apply_3d_force_x`); Formula: VALUE_3 (`brick_apply_3d_force_y`); Formula: VALUE_4 (`brick_apply_3d_force_z`) |
| Дефолт в палитре | ("myObject", 5.0, 0.0, 0.0) |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/Apply3dForceBrick.java:8` |
| Класс экшена | не найден в коде брика |
| Описание | Применяет силу к 3D объекту по его ID с заданным вектором (X, Y, Z). ВАЖНО: работает только в 3D рендере. EN: Applies a force to a 3D object by its ID using the given vector (X, Y, Z). IMPORTANT: only works in the 3D renderer. Факт кода: прямой связи с ActionFactory нет. |

### 42. `Apply3dTorqueBrick`

| Поле | Значение |
|---|---|
| Название RU | Крутить объект |
| Название EN | Apply torque to |
| Ключ строки / layout | `brick_apply_3d_torque_1` / `brick_apply_3d_torque` |
| Параметры | Formula: VALUE_1 (`brick_apply_3d_torque_edit_id`); Formula: VALUE_2 (`brick_apply_3d_torque_edit_x`); Formula: VALUE_3 (`brick_apply_3d_torque_edit_y`); Formula: VALUE_4 (`brick_apply_3d_torque_edit_z`) |
| Дефолт в палитре | ("myObject", 5.0, 0.0, 0.0) |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/Apply3dTorqueBrick.java:30` |
| Класс экшена | Apply3dTorqueAction (catroid/src/main/java/org/catrobat/catroid/content/actions/Apply3dTorqueAction.java:30) — через createApply3dTorqueAction |
| Описание | Прикладывает крутящий момент к 3D-объекту с вектором по осям X, Y и Z. EN: Applies a torque to the 3D object with a vector along the X, Y and Z axes. Код: создаёт `Apply3dTorqueAction` в `addActionToSequence` через `createApply3dTorqueAction`. |

### 43. `Set3dFrictionBrick`

| Поле | Значение |
|---|---|
| Название RU | Задать трение для |
| Название EN | Set friction of |
| Ключ строки / layout | `brick_set_friction_of` / `brick_set_friction` |
| Параметры | Formula: VALUE_1 (`brick_set_friction_id`); Formula: VALUE_2 (`brick_set_friction_value`) |
| Дефолт в палитре | ("myObject", 0.7) |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/Set3dFrictionBrick.java:8` |
| Класс экшена | Set3dFrictionAction (catroid/src/main/java/org/catrobat/catroid/content/actions/Set3dFrictionAction.java:10) — через createSet3dFrictionAction |
| Описание | Задает трение обьекту ВАЖНО: ЕСТЬ СПЕЦ. ФУНКЦЯ: если указать трение > 10 000, то будет задаваться затухание. например трение: 10 001.5 даст затухание 1.5, а 10 000 даст 0 EN: Sets friction for the object. IMPORTANT: THERE IS A SPECIAL FUNCTION: if you set friction > 10,000, damping will be applied instead. For example, friction: 10,001.5 gives damping 1.5, and 10,000 gives 0 Код: создаёт `Set3dFrictionAction` в `addActionToSequence` через `createSet3dFrictionAction`. |

### 44. `SetRestitutionBrick`

| Поле | Значение |
|---|---|
| Название RU | Задать упругость для |
| Название EN | Set bounciness of |
| Ключ строки / layout | `brick_set_restitution_of` / `brick_set_restitution` |
| Параметры | Formula: VALUE_1 (`brick_set_restitution_id`); Formula: VALUE_2 (`brick_set_restitution_value`) |
| Дефолт в палитре | ("myObject", 0.5) |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/SetRestitutionBrick.java:8` |
| Класс экшена | SetRestitutionAction (catroid/src/main/java/org/catrobat/catroid/content/actions/SetRestitutionAction.java:8) — через createSetRestitutionAction |
| Описание | Задает упругость обьекта EN: Sets the restitution of the object Код: создаёт `SetRestitutionAction` в `addActionToSequence` через `createSetRestitutionAction`. |

### 45. `SetCCDBrick`

| Поле | Значение |
|---|---|
| Название RU | Точное столкновение |
| Название EN | Set continuous collision |
| Ключ строки / layout | `brick_set_ccd` / `brick_set_ccd` |
| Параметры | Formula: THREED_OBJECT_ID (`brick_set_ccd_object_id`) |
| Дефолт в палитре | ("myObject", true) |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/SetCCDBrick.java:13` |
| Класс экшена | SetCCDAction (catroid/src/main/java/org/catrobat/catroid/content/actions/SetCCDAction.kt:8) — через createSetCCDAction |
| Описание | Включает или выключает CCD (точное столкновение) для обьекта (включайте для динамичных обьектов) EN: Turns CCD (continuous collision detection) on or off for an object (enable for dynamic objects). Код: создаёт `SetCCDAction` в `addActionToSequence` через `createSetCCDAction`. |

### 46. `CastRayBrick`

| Поле | Значение |
|---|---|
| Название RU | Выпустить луч с именем |
| Название EN | Cast ray named |
| Ключ строки / layout | `brick_cast_ray_name` / `brick_cast_ray` |
| Параметры | Formula: VALUE_1 (`brick_cast_ray_name`); Formula: VALUE_2 (`brick_cast_ray_from_x`); Formula: VALUE_3 (`brick_cast_ray_from_y`); Formula: VALUE_4 (`brick_cast_ray_from_z`); Formula: VALUE_5 (`brick_cast_ray_dir_x`); Formula: VALUE_6 (`brick_cast_ray_dir_y`); Formula: VALUE_7 (`brick_cast_ray_dir_z`) |
| Дефолт в палитре | ("ray", 100.0, 100.0, 100.0, -1.0, -1.0, -1.0) |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/CastRayBrick.java:8` |
| Класс экшена | CastRayAction (catroid/src/main/java/org/catrobat/catroid/content/actions/CastRayAction.java:10) — через createCastRayAction |
| Описание | Пускает физический луч (raycast) в 3D пространстве из точки в заданном направлении. Полезно для стрельбы или обнаружения препятствий. EN: Casts a physics ray in 3D space from a point in the given direction. Useful for shooting or obstacle detection. Код: создаёт `CastRayAction` в `addActionToSequence` через `createCastRayAction`. |

### 47. `AttachRaySensorBrick`

| Поле | Значение |
|---|---|
| Название RU | Имя луча |
| Название EN | Ray name |
| Ключ строки / layout | `brick_attach_ray_name` / `brick_attach_ray_sensor` |
| Параметры | Formula: VALUE_1 (`brick_attach_ray_name`); Formula: VALUE_2 (`brick_attach_ray_obj`); Formula: VALUE_3 (`brick_attach_ray_off_x`); Formula: VALUE_4 (`brick_attach_ray_off_y`); Formula: VALUE_5 (`brick_attach_ray_off_z`); Formula: VALUE_6 (`brick_attach_ray_dir_x`); Formula: VALUE_7 (`brick_attach_ray_dir_y`); Formula: VALUE_8 (`brick_attach_ray_dir_z`); Formula: VALUE_9 (`brick_attach_ray_dist`) |
| Дефолт в палитре | ("ray", "myObject", 1f, 1f, 1f, 0f, -1f, 0f, 100f) |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/AttachRaySensorBrick.java:8` |
| Класс экшена | AttachRaySensorAction (catroid/src/main/java/org/catrobat/catroid/content/actions/AttachRaySensorAction.kt:8) — через createAttachRaySensorAction |
| Описание | Привязывает луч к обьекту (используйте для сенсоров. Это оптимизированнее чем вечно пускать луч) EN: Attaches a ray to the object (use for sensors. This is more optimized than constantly casting a ray) Код: создаёт `AttachRaySensorAction` в `addActionToSequence` через `createAttachRaySensorAction`. |

### 48. `CreatePointJointBrick`

| Поле | Значение |
|---|---|
| Название RU | Создать шарнир-сферу |
| Название EN | Create ball joint |
| Ключ строки / layout | `brick_create_point_joint` / `brick_create_point_joint` |
| Параметры | Formula: JOINT_NAME (`brick_joint_name_edit`); Formula: OBJECT_A (`brick_joint_object_a_edit`); Formula: OBJECT_B (`brick_joint_object_b_edit`); Formula: PIVOT_A_X (`brick_joint_pivot_a_x`); Formula: PIVOT_A_Y (`brick_joint_pivot_a_y`); Formula: PIVOT_A_Z (`brick_joint_pivot_a_z`); Formula: PIVOT_B_X (`brick_joint_pivot_b_x`); Formula: PIVOT_B_Y (`brick_joint_pivot_b_y`); Formula: PIVOT_B_Z (`brick_joint_pivot_b_z`) |
| Дефолт в палитре | ("joint", "objA", "objB") |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/CreatePointJointBrick.java:8` |
| Класс экшена | CreatePointJointAction (catroid/src/main/java/org/catrobat/catroid/content/actions/CreatePointJointAction.kt:9) — через createCreatePointJointAction |
| Описание | Создает сферический шарнир для двух 3D обьектов EN: Creates a spherical joint for two 3D objects Код: создаёт `CreatePointJointAction` в `addActionToSequence` через `createCreatePointJointAction`. |

### 49. `ThreedCreateFixedConstraintBrick`

| Поле | Значение |
|---|---|
| Название RU | Создать Фикс. Шарнир |
| Название EN | Create Fixed Joint |
| Ключ строки / layout | `brick_threed_fixed_joint_title` / `brick_threed_create_fixed_constraint` |
| Параметры | Formula: VALUE_1 (`brick_fixed_joint_id`); Formula: VALUE_2 (`brick_fixed_joint_a`); Formula: VALUE_3 (`brick_fixed_joint_b`); Formula: X (`brick_fixed_joint_ax`); Formula: Y (`brick_fixed_joint_ay`); Formula: Z (`brick_fixed_joint_az`); Formula: VALUE_4 (`brick_fixed_joint_bx`); Formula: VALUE_5 (`brick_fixed_joint_by`); Formula: VALUE_6 (`brick_fixed_joint_bz`); Spinner `R.array.brick_threed_joint_modes` |
| Дефолт в палитре | ("joint", "objA", "objB") |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/ThreedCreateFixedConstraintBrick.java:15` |
| Класс экшена | ThreedCreateFixedConstraintAction (catroid/src/main/java/org/catrobat/catroid/content/actions/ThreedCreateFixedConstraintAction.java:8) — через createThreedCreateFixedConstraintAction |
| Описание | Создаёт фиксированное соединение (сустав) между двумя 3D-объектами A и B с указанным ID. В режиме Auto (сварка) точки крепления определяются автоматически, в режиме Manual можно вручную задать точки опоры (X, Y, Z) для A и B. EN: Creates a fixed constraint (joint) with the given ID between two 3D objects A and B. In Auto (weld) mode the pivots are determined automatically; in Manual mode you can set the pivot points (X, Y, Z) for A and B by hand. Код: создаёт `ThreedCreateFixedConstraintAction` в `addActionToSequence` через `createThreedCreateFixedConstraintAction`. |

### 50. `AddHingeBrick`

| Поле | Значение |
|---|---|
| Название RU | Шарнир-ось (Петля/Колесо) |
| Название EN | Add Hinge Joint |
| Ключ строки / layout | `threed_hinge_title` / `brick_threed_hinge` |
| Параметры | Formula: CONSTRAINT_ID (`brick_hinge_id`); Formula: OBJECT_A (`brick_hinge_obja`); Formula: OBJECT_B (`brick_hinge_objb`); Formula: PIVOT_A_X (`brick_hinge_pax`); Formula: PIVOT_A_Y (`brick_hinge_pay`); Formula: PIVOT_A_Z (`brick_hinge_paz`); Formula: AXIS_A_X (`brick_hinge_aax`); Formula: AXIS_A_Y (`brick_hinge_aay`); Formula: AXIS_A_Z (`brick_hinge_aaz`); Formula: PIVOT_B_X (`brick_hinge_pbx`); Formula: PIVOT_B_Y (`brick_hinge_pby`); Formula: PIVOT_B_Z (`brick_hinge_pbz`); Formula: AXIS_B_X (`brick_hinge_abx`); Formula: AXIS_B_Y (`brick_hinge_aby`); Formula: AXIS_B_Z (`brick_hinge_abz`) |
| Дефолт в палитре | () |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/AddHingeBrick.java:8` |
| Класс экшена | AddHingeAction (catroid/src/main/java/org/catrobat/catroid/content/actions/AddHingeAction.kt:11) — через createAddHingeAction |
| Описание | Создает шарнир, вращающийся только по выбранным осям. (например 0, 1, 0 будет работать как дверь. а 0, 0, 1 как люк) EN: Creates a hinge that rotates only along the selected axes. (e.g. 0, 1, 0 will work like a door, and 0, 0, 1 like a hatch) Код: создаёт `AddHingeAction` в `addActionToSequence` через `createAddHingeAction`. |

### 51. `SetHingeMotorBrick`

| Поле | Значение |
|---|---|
| Название RU | Включить мотор шарнира |
| Название EN | Set Hinge Motor |
| Ключ строки / layout | `threed_motor_title` / `brick_threed_hinge_motor` |
| Параметры | Formula: CONSTRAINT_ID (`brick_motor_id`); Formula: MOTOR_TARGET (`brick_motor_target`); Formula: MOTOR_MAX_FORCE (`brick_motor_max`) |
| Дефолт в палитре | ("joint", 45.0, 10.0) |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/SetHingeMotorBrick.java:8` |
| Класс экшена | SetHingeMotorAction (catroid/src/main/java/org/catrobat/catroid/content/actions/SetHingeMotorAction.kt:10) — через createSetHingeMotorAction |
| Описание | Задает угол для шарнира, который будет пытаться удержать EN: Sets the angle for a hinge that will try to maintain Код: создаёт `SetHingeMotorAction` в `addActionToSequence` через `createSetHingeMotorAction`. |

### 52. `RemoveJointBrick`

| Поле | Значение |
|---|---|
| Название RU | Удалить шарнир |
| Название EN | Remove joint |
| Ключ строки / layout | `brick_remove_joint` / `brick_remove_joint` |
| Параметры | Formula: JOINT_NAME (`brick_remove_joint_name_edit`) |
| Дефолт в палитре | ("joint") |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/RemoveJointBrick.java:8` |
| Класс экшена | RemoveJointAction (catroid/src/main/java/org/catrobat/catroid/content/actions/RemoveJointAction.kt:8) — через createRemoveJointAction |
| Описание | Удаляет шарнир для 3D обьектов EN: Removes a joint for 3D objects Код: создаёт `RemoveJointAction` в `addActionToSequence` через `createRemoveJointAction`. |

## Свет и окружение / Lights & Environment (`subcategory_3d_render`)

### 53. `SetAmbientLightBrick`

| Поле | Значение |
|---|---|
| Название RU | Задать фоновый свет (цвет 0-1) |
| Название EN | Set ambient light (color 0-1) |
| Ключ строки / layout | `brick_set_ambient_light` / `brick_set_ambient_light` |
| Параметры | Formula: VALUE_1 (`brick_set_ambient_light_r`); Formula: VALUE_2 (`brick_set_ambient_light_g`); Formula: VALUE_3 (`brick_set_ambient_light_b`) |
| Дефолт в палитре | (0.8, 0.8, 0.8) |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/SetAmbientLightBrick.java:8` |
| Класс экшена | SetAmbientLightAction (catroid/src/main/java/org/catrobat/catroid/content/actions/SetAmbientLightAction.java:8) — через createSetAmbientLightAction |
| Описание | Задает фоновый (ambient) свет сцены цветом (красный, зеленый, синий). ВАЖНО: значения обычно от 0 до 1. EN: Sets the scene's ambient light color (red, green, blue). IMPORTANT: values are usually from 0 to 1. Код: создаёт `SetAmbientLightAction` в `addActionToSequence` через `createSetAmbientLightAction`. |

### 54. `SetDirectionalLightBrick`

| Поле | Значение |
|---|---|
| Название RU | Направленный свет с ID |
| Название EN | Set directional light ID |
| Ключ строки / layout | `brick_set_directional_light_id` / `brick_set_directional_light` |
| Параметры | Formula: VALUE_1 (`brick_set_directional_light_id`); Formula: VALUE_2 (`brick_set_directional_light_r`); Formula: VALUE_3 (`brick_set_directional_light_g`); Formula: VALUE_4 (`brick_set_directional_light_b`); Formula: VALUE_5 (`brick_set_directional_light_x`); Formula: VALUE_6 (`brick_set_directional_light_y`); Formula: VALUE_7 (`brick_set_directional_light_z`) |
| Дефолт в палитре | ("sun", 0.8, 0.8, 0.8, -1.0, -0.8, -0.2) |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/SetDirectionalLightBrick.java:8` |
| Класс экшена | SetDirectionalLightAction (catroid/src/main/java/org/catrobat/catroid/content/actions/SetDirectionalLightAction.java:8) — через createSetDirectionalLightAction |
| Описание | Задает глобальный свет (отбрасывает тени) EN: Sets a global light (casts shadows) Код: создаёт `SetDirectionalLightAction` в `addActionToSequence` через `createSetDirectionalLightAction`. |

### 55. `SetSkyColorBrick`

| Поле | Значение |
|---|---|
| Название RU | Задать цвет неба на (0-1) |
| Название EN | Set sky color to (0-1) |
| Ключ строки / layout | `brick_set_sky_color` / `brick_set_sky_color` |
| Параметры | Formula: VALUE_1 (`brick_set_sky_color_r`); Formula: VALUE_2 (`brick_set_sky_color_g`); Formula: VALUE_3 (`brick_set_sky_color_b`) |
| Дефолт в палитре | (0.5, 0.6, 1.0) |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/SetSkyColorBrick.java:8` |
| Класс экшена | SetSkyColorAction (catroid/src/main/java/org/catrobat/catroid/content/actions/SetSkyColorAction.java:9) — через createSetSkyColorAction |
| Описание | Задает цвет неба сцены (R, G, B от 0 до 1). EN: Sets the scene sky color (R, G, B from 0 to 1). Код: создаёт `SetSkyColorAction` в `addActionToSequence` через `createSetSkyColorAction`. |

### 56. `SetFogBrick`

| Поле | Значение |
|---|---|
| Название RU | Задать туман |
| Название EN | Set Fog |
| Ключ строки / layout | `brick_set_3d_fog` / `brick_set_fog` |
| Параметры | Formula: FORMULA_1 (`brick_set_fog_r_edit`); Formula: FORMULA_2 (`brick_set_fog_g_edit`); Formula: FORMULA_3 (`brick_set_fog_b_edit`); Formula: FORMULA_4 (`brick_set_fog_density_edit`) |
| Дефолт в палитре | (0.5f, 0.6f, 1f, 0.1f) |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/SetFogBrick.java:13` |
| Класс экшена | SetFogAction (catroid/src/main/java/org/catrobat/catroid/content/actions/SetFogAction.java:13) — через createSetFogAction |
| Описание | Задает туман сцены: цвет (R, G, B от 0 до 1) и плотность (density). EN: Sets the scene fog color (R, G, B from 0 to 1) and density. Код: создаёт `SetFogAction` в `addActionToSequence` через `createSetFogAction`. |

### 57. `EnablePbrRenderBrick`

| Поле | Значение |
|---|---|
| Название RU | Включить рендер 2.0 |
| Название EN | Enable realistic render |
| Ключ строки / layout | `brick_enable_pbr_render` / `brick_enable_pbr_render` |
| Параметры | Spinner `R.array.brick_pbr_render_states` EN: Off / On |
| Дефолт в палитре | (1) |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/EnablePbrRenderBrick.java:18` |
| Класс экшена | EnablePbrRenderAction (catroid/src/main/java/org/catrobat/catroid/content/actions/EnablePbrRenderAction.kt:13) — через createEnablePbrRenderAction |
| Описание | Включает новый рендер 2.0, поддерживающий PBR EN: Enables the new render 2.0 that supports PBR Код: создаёт `EnablePbrRenderAction` в `addActionToSequence` через `createEnablePbrRenderAction`. |

### 58. `SetBackgroundLightBrick`

| Поле | Значение |
|---|---|
| Название RU | Задать фоновый свет |
| Название EN | Set background light |
| Ключ строки / layout | `brick_set_background_light_header` / `brick_set_background_light` |
| Параметры | Formula: VALUE_1 (`brick_set_background_light_intensity_value`) |
| Дефолт в палитре | (0.3) |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/SetBackgroundLightBrick.java:8` |
| Класс экшена | SetBackgroundLightAction (catroid/src/main/java/org/catrobat/catroid/content/actions/SetBackgroundLightAction.kt:8) — через createSetBackgroundLightAction |
| Описание | Задает интенсивность фонового света сцены. EN: Sets the intensity of the scene's background light. Код: создаёт `SetBackgroundLightAction` в `addActionToSequence` через `createSetBackgroundLightAction`. |

### 59. `SetSkyboxBrick`

| Поле | Значение |
|---|---|
| Название RU | Задать Скайбокс |
| Название EN | Set Skybox |
| Ключ строки / layout | `threed_skybox_set` / `brick_threed_set_skybox` |
| Параметры | Formula: FILENAME (`brick_threed_set_skybox_filename_edit`) |
| Дефолт в палитре | ("skybox.hdr") |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/SetSkyboxBrick.java:31` |
| Класс экшена | SetSkyboxAction (catroid/src/main/java/org/catrobat/catroid/content/actions/SetSkyboxAction.kt:31) — через createSetSkyboxAction |
| Описание | Устанавливает скайбокс (фон неба) сцены по имени файла. EN: Sets the scene skybox (sky background) by filename. Код: создаёт `SetSkyboxAction` в `addActionToSequence` через `createSetSkyboxAction`. |

### 60. `SetPointLightBrick`

| Поле | Значение |
|---|---|
| Название RU | Задать точечный свет |
| Название EN | Set point light |
| Ключ строки / layout | `brick_set_point_light_header` / `brick_set_point_light` |
| Параметры | Formula: VALUE_1 (`brick_set_point_light_id_value`); Formula: VALUE_2 (`brick_set_point_light_pos_x_value`); Formula: VALUE_3 (`brick_set_point_light_pos_y_value`); Formula: VALUE_4 (`brick_set_point_light_pos_z_value`); Formula: VALUE_5 (`brick_set_point_light_color_r_value`); Formula: VALUE_6 (`brick_set_point_light_color_g_value`); Formula: VALUE_7 (`brick_set_point_light_color_b_value`); Formula: VALUE_8 (`brick_set_point_light_intensity_value`); Formula: VALUE_9 (`brick_set_point_light_range_value`) |
| Дефолт в палитре | ("sun", 100.0, 100.0, 0.0, 255, 255, 230, 5.0, 300.0) |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/SetPointLightBrick.java:14` |
| Класс экшена | SetPointLightAction (catroid/src/main/java/org/catrobat/catroid/content/actions/SetPointLightAction.kt:14) — через createSetPointLightAction |
| Описание | Создает точечный свет (лампочка) EN: Creates a point light (bulb) Код: создаёт `SetPointLightAction` в `addActionToSequence` через `createSetPointLightAction`. |

### 61. `SetSpotLightBrick`

| Поле | Значение |
|---|---|
| Название RU | Задать прожектор |
| Название EN | Set spot light |
| Ключ строки / layout | `brick_set_spot_light_header` / `brick_set_spot_light` |
| Параметры | Formula: VALUE_1 (`brick_set_spot_light_id_value`); Formula: VALUE_2 (`brick_set_spot_light_pos_x_value`); Formula: VALUE_3 (`brick_set_spot_light_pos_y_value`); Formula: VALUE_4 (`brick_set_spot_light_pos_z_value`); Formula: VALUE_5 (`brick_set_spot_light_dir_x_value`); Formula: VALUE_6 (`brick_set_spot_light_dir_y_value`); Formula: VALUE_7 (`brick_set_spot_light_dir_z_value`); Formula: VALUE_8 (`brick_set_spot_light_color_r_value`); Formula: VALUE_9 (`brick_set_spot_light_color_g_value`); Formula: VALUE_10 (`brick_set_spot_light_color_b_value`); Formula: VALUE_11 (`brick_set_spot_light_intensity_value`); Formula: VALUE_12 (`brick_set_spot_light_angle_value`); Formula: VALUE_13 (`brick_set_spot_light_exponent_value`); Formula: VALUE_14 (`brick_set_spot_light_range_value`) |
| Дефолт в палитре | ("sun", 100.0, 100.0, 0.0, 0.3, -0.4, 0.2, 255, 255, 230, 5.0, 60.0, 1.0, 300.0) |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/SetSpotLightBrick.java:14` |
| Класс экшена | SetSpotLightAction (catroid/src/main/java/org/catrobat/catroid/content/actions/SetSpotLightAction.kt:14) — через createSetSpotLightAction |
| Описание | Создает направленный свет (прожектор) EN: Creates a directional light (spotlight) Код: создаёт `SetSpotLightAction` в `addActionToSequence` через `createSetSpotLightAction`. |

### 62. `SetDirectionalLight2Brick`

| Поле | Значение |
|---|---|
| Название RU | Задать направленный свет |
| Название EN | Set directional light |
| Ключ строки / layout | `brick_set_directional_light_header` / `brick_set_directional_light2` |
| Параметры | Formula: VALUE_1 (`brick_set_directional_light_dir_x_value`); Formula: VALUE_2 (`brick_set_directional_light_dir_y_value`); Formula: VALUE_3 (`brick_set_directional_light_dir_z_value`); Formula: VALUE_4 (`brick_set_directional_light_intensity_value`) |
| Дефолт в палитре | (0.3, -0.2, -0.3, 5.0) |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/SetDirectionalLight2Brick.java:14` |
| Класс экшена | SetDirectionalLight2Action (catroid/src/main/java/org/catrobat/catroid/content/actions/SetDirectionalLight2Action.kt:15) — через createSetDirectionalLight2Action |
| Описание | Задает направленный свет: направление (X, Y, Z) и интенсивность. Отбрасывает тени по всей сцене. EN: Sets a directional light: direction (X, Y, Z) and intensity. Casts shadows across the whole scene. Код: создаёт `SetDirectionalLight2Action` в `addActionToSequence` через `createSetDirectionalLight2Action`. |

### 63. `RemovePbrLightBrick`

| Поле | Значение |
|---|---|
| Название RU | Удалить свет |
| Название EN | Remove light |
| Ключ строки / layout | `brick_remove_pbr_light_header` / `brick_remove_pbr_light` |
| Параметры | Formula: VALUE_1 (`brick_remove_pbr_light_id_value`) |
| Дефолт в палитре | ("sun") |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/RemovePbrLightBrick.java:14` |
| Класс экшена | RemovePbrLightAction (catroid/src/main/java/org/catrobat/catroid/content/actions/RemovePbrLightAction.kt:14) — через createRemovePbrLightAction |
| Описание | Удаляет PBR-источник света по его ID (имени). EN: Removes the PBR light source by its ID (name). Код: создаёт `RemovePbrLightAction` в `addActionToSequence` через `createRemovePbrLightAction`. |

### 64. `PromoteLightBrick`

| Поле | Значение |
|---|---|
| Название RU | Превратить свет в объект |
| Название EN | Promote light to object |
| Ключ строки / layout | `brick_promote_light` / `brick_promote_light` |
| Параметры | Formula: NAME (`brick_promote_light_id_val`) |
| Дефолт в палитре | ("sun") |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/PromoteLightBrick.java:8` |
| Класс экшена | PromoteLightAction (catroid/src/main/java/org/catrobat/catroid/content/actions/PromoteLightAction.kt:8) — через createPromoteLightAction |
| Описание | Превращает ваш созданный свет в полноценный обьект (вы можете двигать его, вращать и взаимодействовать как с обычным GameObject из 3D редактора) EN: Turns your created light into a full-fledged object (you can move it, rotate it, and interact with it like a regular GameObject from the 3D editor) Код: создаёт `PromoteLightAction` в `addActionToSequence` через `createPromoteLightAction`. |

### 65. `ConfigureLightBrick`

| Поле | Значение |
|---|---|
| Название RU | Настроить свет |
| Название EN | Configure light |
| Ключ строки / layout | `brick_http_configure_light_title` / `brick_http_configure_light` |
| Параметры | Formula: VALUE_1 (`brick_http_configure_light_id_field`); Formula: VALUE_2 (`brick_http_configure_light_value_field`); Spinner `R.array.brick_http_configure_light_types` EN: Intensity / Range / Color (R,G,B) / Position (X,Y,Z) / Direction (X,Y,Z) / Cone angle / Softness |
| Дефолт в палитре | ("sun", 0, "1.0") |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/ConfigureLightBrick.java:13` |
| Класс экшена | ConfigureLightAction (catroid/src/main/java/org/catrobat/catroid/content/actions/ConfigureLightAction.kt:9) — через createConfigureLightAction |
| Описание | Настраивает выбранное свойство 3D-света (интенсивность, диапазон, цвет, позиция, направление, угол конуса, мягкость) по ID. EN: Sets the selected property of a 3D light (intensity, range, color, position, direction, cone angle, softness) by ID. Код: создаёт `ConfigureLightAction` в `addActionToSequence` через `createConfigureLightAction`. |

### 66. `ConfigureMaterialBrick`

| Поле | Значение |
|---|---|
| Название RU | Настроить материал |
| Название EN | Configure material |
| Ключ строки / layout | `brick_http_configure_material_title` / `brick_http_configure_material` |
| Параметры | Formula: VALUE_1 (`brick_http_configure_material_id_field`); Formula: VALUE_2 (`brick_http_configure_material_value_field`); Spinner `R.array.brick_http_configure_material_types` EN: Base color (R,G,B,A) / Metallic / Roughness / Texture scale (U,V) / Emissive color (R,G,B) / Emissive intensity |
| Дефолт в палитре | ("myObject", 0, "1.0") |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/ConfigureMaterialBrick.java:13` |
| Класс экшена | ConfigureMaterialAction (catroid/src/main/java/org/catrobat/catroid/content/actions/ConfigureMaterialAction.kt:11) — через createConfigureMaterialAction |
| Описание | Настраивает выбранное PBR-свойство материала 3D-объекта (базовый цвет, металличность, шероховатость, масштаб текстуры, свечение) по ID. EN: Sets the selected PBR property of a 3D object material (base color, metallic, roughness, texture scale, emissive) by ID. Код: создаёт `ConfigureMaterialAction` в `addActionToSequence` через `createConfigureMaterialAction`. |

### 67. `SetMaxPointLightsBrick`

| Поле | Значение |
|---|---|
| Название RU | Максимум точечных светов |
| Название EN | Set max active point lights |
| Ключ строки / layout | `brick_set_max_point_lights` / `brick_set_max_point_lights` |
| Параметры | Formula: MAX_LIGHTS (`brick_max_lights_val`) |
| Дефолт в палитре | (5) |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/SetMaxPointLightsBrick.java:8` |
| Класс экшена | SetMaxPointLightsAction (catroid/src/main/java/org/catrobat/catroid/content/actions/SetMaxPointLightsAction.kt:8) — через createSetMaxPointLightsAction |
| Описание | Задает сколько максимум может быть динамических точечных светов на сцене. Работают только N ближайших к камере EN: Sets the maximum number of dynamic point lights that can be on the scene. Only the N closest to the camera work Код: создаёт `SetMaxPointLightsAction` в `addActionToSequence` через `createSetMaxPointLightsAction`. |

### 68. `SetShadowQualityBrick`

| Поле | Значение |
|---|---|
| Название RU | Установить тени |
| Название EN | Set shadow |
| Ключ строки / layout | `brick_set_shadow_quality_prefix` / `brick_set_shadow_quality` |
| Параметры | Formula: SHADOW_SIZE (`brick_shadow_size_edit`); Formula: SHADOW_RESOLUTION (`brick_shadow_resolution_edit`) |
| Дефолт в палитре | (Formula(100), Formula(2048)) |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/SetShadowQualityBrick.java:8` |
| Класс экшена | AddHingeAction (catroid/src/main/java/org/catrobat/catroid/content/actions/AddHingeAction.kt:11) — через createSetShadowQualityAction |
| Описание | Изменяет параметры теней в сцене. ВАЖНО: В РАЗРЕШЕНИИ ЛУЧШЕ УКАЗЫВАТЬ СТЕПЕНЬ 2-ки! (128, 512, 2048, 4096...) EN: Changes shadow parameters in the scene. IMPORTANT: use powers of 2 for resolution! (128, 512, 2048, 4096...) Код: создаёт `AddHingeAction` в `addActionToSequence` через `createSetShadowQualityAction`. |

## Шейдеры / Shaders (`subcategory_shaders_3d`)

### 69. `DissolveShaderBrick`

| Поле | Значение |
|---|---|
| Название RU | Растворение (сгорание) |
| Название EN | Dissolve effect (burn out) |
| Ключ строки / layout | `brick_dissolve_shader` / `brick_dissolve_shader` |
| Параметры | Formula: NAME (`brick_dissolve_object_id`); Formula: VALUE (`brick_dissolve_progress`) |
| Дефолт в палитре | ("myObject", 0.0) |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/DissolveShaderBrick.java:30` |
| Класс экшена | DissolveShaderAction (catroid/src/main/java/org/catrobat/catroid/content/actions/DissolveShaderAction.kt:30) — через createDissolveShaderAction |
| Описание | Нет в `BrickInfo.java`. Код: создаёт `DissolveShaderAction` в `addActionToSequence` через `createDissolveShaderAction`. |

### 70. `WaveShaderBrick`

| Поле | Значение |
|---|---|
| Название RU | Искажение волнами |
| Название EN | Wave distortion |
| Ключ строки / layout | `brick_wave_shader` / `brick_wave_shader` |
| Параметры | Formula: NAME (`brick_wave_object_id`); Formula: VALUE (`brick_wave_amplitude`) |
| Дефолт в палитре | ("myObject", 30.0) |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/WaveShaderBrick.java:30` |
| Класс экшена | WaveShaderAction (catroid/src/main/java/org/catrobat/catroid/content/actions/WaveShaderAction.kt:30) — через createWaveShaderAction |
| Описание | Нет в `BrickInfo.java`. Код: создаёт `WaveShaderAction` в `addActionToSequence` через `createWaveShaderAction`. |

## Материалы и шейдеры / Materials & Shaders (`subcategory_3d_shaders`)

### 71. `SetShaderCodeBrick`

| Поле | Значение |
|---|---|
| Название RU | Задать 3D GLSL шейдер |
| Название EN | Set 3D GLSL Shader |
| Ключ строки / layout | `brick_set_shader_code` / `brick_set_shader_code` |
| Параметры | Formula: THREED_VERTEX_SHADER_CODE (`brick_shader_vertex_code`); Formula: THREED_FRAGMENT_SHADER_CODE (`brick_shader_fragment_code`) |
| Дефолт в палитре | ("""attribute vec3 a_position; attribute vec3 a_normal; attribute vec2 a_texCoord0; uniform mat4 u_worldTrans; uniform mat4 u_projViewTrans;… |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/SetShaderCodeBrick.java:8` |
| Класс экшена | SetShaderCodeAction (catroid/src/main/java/org/catrobat/catroid/content/actions/SetShaderCodeAction.kt:8) — через createSetShaderCodeAction |
| Описание | Задает вершинный и фрагментный шейдер. Гайды можно найти в интернете (ищите LibGDX, GLSL). ВАЖНО: ЕСЛИ ВЫ НЕ ЗНАЕТЕ КАК ИМИ ПОЛЬЗОВАТЬСЯ - НЕ ПОЛЬЗУЙТЕСЬ. В СЛУЧАЕ ГРАФИЧЕСКИХ БАГОВ - ПОПРОБУЙТЕ ОТКЛЮЧИТЬ ИЛИ УДАЛИТЬ ЭТОТ БЛОК! EN: Sets the vertex and fragment shader. Guides can be found online (search for LibGDX, GLSL). IMPORTANT: IF YOU DO NOT KNOW HOW TO USE THEM - DO NOT USE. IN CASE OF GRAPHICAL GLITCHES - TRY TO DISABLE OR DELETE THIS BLOCK! Код: создаёт `SetShaderCodeAction` в `addActionToSequence` через `createSetShaderCodeAction`. |

### 72. `SetScreenShaderBrick`

| Поле | Значение |
|---|---|
| Название RU | Шейдер Экрана (GLSL) |
| Название EN | Custom Screen Shader |
| Ключ строки / layout | `brick_screen_shader` / `brick_set_screen_shader` |
| Параметры | Formula: VERTEX (`brick_screen_shader_vertex`); Formula: FRAGMENT (`brick_screen_shader_fragment`) |
| Дефолт в палитре | ("""attribute vec4 a_position; attribute vec2 a_texCoord0; varying vec2 v_texCoords; void main() { v_texCoords = a_texCoord0; gl_Position = … |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/SetScreenShaderBrick.java:8` |
| Класс экшена | SetScreenShaderAction (catroid/src/main/java/org/catrobat/catroid/content/actions/SetScreenShaderAction.kt:8) — через createSetScreenShaderAction |
| Описание | Задает код 3D шейдера. (GLSL)  ВАЖНО: для нового рендера (2.0) EN: Sets 3D shader code. (GLSL)  IMPORTANT: for the new renderer (2.0) Код: создаёт `SetScreenShaderAction` в `addActionToSequence` через `createSetScreenShaderAction`. |

### 73. `SetObjectShaderBrick`

| Поле | Значение |
|---|---|
| Название RU | Задать шейдер для 3D-объекта |
| Название EN | Set shader for 3D object |
| Ключ строки / layout | `set_object_shader` / `brick_set_object_shader` |
| Параметры | Formula: NAME (`brick_object_shader_id`); Formula: TEXT (`brick_object_shader_vertex`); Formula: VALUE (`brick_object_shader_fragment`) |
| Дефолт в палитре | ("myObject", """attribute vec3 a_position; uniform mat4 u_projViewTrans; uniform mat4 u_worldTrans; uniform float u_time; uniform float u_wa… |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/SetObjectShaderBrick.java:8` |
| Класс экшена | SetObjectShaderAction (catroid/src/main/java/org/catrobat/catroid/content/actions/SetObjectShaderAction.kt:8) — через createSetObjectShaderAction |
| Описание | Задает шейдер для определенного обьекта. Используйте для волн, травинок и др. ВАЖНО: для рендера 2.0 EN: Sets a shader for a specific object. Use for waves, grass blades, etc. IMPORTANT: for renderer 2.0 Код: создаёт `SetObjectShaderAction` в `addActionToSequence` через `createSetObjectShaderAction`. |

### 74. `SetShaderUniformVec3Brick`

| Поле | Значение |
|---|---|
| Название RU | Задать uniform (вектор) |
| Название EN | Set Uniform (Vector) |
| Ключ строки / layout | `brick_set_shader_uniform_vec3` / `brick_set_shader_uniform_vec3` |
| Параметры | Formula: THREED_UNIFORM_NAME (`brick_uniform_name_edit`); Formula: VALUE_X (`brick_uniform_value_x_edit`); Formula: VALUE_Y (`brick_uniform_value_y_edit`); Formula: VALUE_Z (`brick_uniform_value_z_edit`) |
| Дефолт в палитре | ("lightColor", 0.4, 1.0, 0.4) |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/SetShaderUniformVec3Brick.java:8` |
| Класс экшена | SetShaderUniformAction (catroid/src/main/java/org/catrobat/catroid/content/actions/SetShaderUniformAction.kt:8) — через createSetShaderUniformAction |
| Описание | Задает векторную uniform-переменную (uniform, X/Y/Z) шейдера по имени. EN: Sets a vec3 (X/Y/Z) shader uniform by name. Код: создаёт `SetShaderUniformAction` в `addActionToSequence` через `createSetShaderUniformAction`. |

### 75. `SetShaderUniformFloatBrick`

| Поле | Значение |
|---|---|
| Название RU | Задать uniform (число) |
| Название EN | Set Uniform (Number) |
| Ключ строки / layout | `brick_set_shader_uniform_float` / `brick_set_shader_uniform_float` |
| Параметры | Formula: THREED_UNIFORM_NAME (`brick_uniform_name_edit`); Formula: VALUE_X (`brick_uniform_value_edit`) |
| Дефолт в палитре | ("meaningOfLife", 42.0) |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/SetShaderUniformFloatBrick.java:8` |
| Класс экшена | SetShaderUniformAction (catroid/src/main/java/org/catrobat/catroid/content/actions/SetShaderUniformAction.kt:8) — через createSetShaderUniformAction |
| Описание | Задает числовую uniform-переменную (uniform) шейдера по имени. EN: Sets a single float (number) shader uniform by name. Код: создаёт `SetShaderUniformAction` в `addActionToSequence` через `createSetShaderUniformAction`. |

### 76. `SetObjectShaderUniformBrick`

| Поле | Значение |
|---|---|
| Название RU | Задать параметр шейдера для 3D-объекта |
| Название EN | Set shader param for 3D object |
| Ключ строки / layout | `set_object_shader_uniform` / `brick_set_object_shader_uniform` |
| Параметры | Formula: NAME (`brick_object_shader_uniform_id`); Formula: TEXT (`brick_object_shader_uniform_name`); Formula: X (`brick_object_shader_uniform_val1`); Formula: Y (`brick_object_shader_uniform_val2`); Formula: Z (`brick_object_shader_uniform_val3`) |
| Дефолт в палитре | (Formula("myObject"), Formula("waveSpeed"), Formula(2.5f), Formula(""), Formula("")) |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/SetObjectShaderUniformBrick.java:8` |
| Класс экшена | SetObjectShaderUniformAction (catroid/src/main/java/org/catrobat/catroid/content/actions/SetObjectShaderUniformAction.kt:8) — через createSetObjectShaderUniformAction |
| Описание | Задает uniform'у (параметр) для шейдера объекта EN: Sets a uniform (parameter) for the object shader Код: создаёт `SetObjectShaderUniformAction` в `addActionToSequence` через `createSetObjectShaderUniformAction`. |

### 77. `SetMaterialBrick`

| Поле | Значение |
|---|---|
| Название RU | Настроить материал объекта |
| Название EN | Set material of object |
| Ключ строки / layout | `brick_set_material` / `brick_set_material` |
| Параметры | Formula: OBJECT_NAME (`brick_set_material_object_edit`); Formula: MATERIAL_R (`brick_set_material_r_edit`); Formula: MATERIAL_G (`brick_set_material_g_edit`); Formula: MATERIAL_B (`brick_set_material_b_edit`); Formula: MATERIAL_A (`brick_set_material_a_edit`); Formula: MATERIAL_METALLIC (`brick_set_material_metallic_edit`); Formula: MATERIAL_ROUGHNESS (`brick_set_material_roughness_edit`); Formula: MATERIAL_TEXTURE_COLOR (`brick_set_material_texture_color_edit`); Formula: MATERIAL_TEXTURE_NORMAL (`brick_set_material_texture_normal_edit`); Formula: MATERIAL_TEXTURE_MR (`brick_set_material_texture_mr_edit`) |
| Дефолт в палитре | ("myObject", 255.0, 0.0, 255.0, 255.0, 100.0, 0.0, "none.png", "none.png", "none.png") |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/SetMaterialBrick.java:8` |
| Класс экшена | SetMaterialAction (catroid/src/main/java/org/catrobat/catroid/content/actions/SetMaterialAction.kt:9) — через createSetMaterialAction |
| Описание | Задает PBR материал. ВАЖНО: только для рендера 2.0.\nЕсли в начале написать \"buffer://\" а потом имя буфера - текстура возьмется от туда (нужно чтоб отображать 2D сцены в 3D) EN: Sets a PBR material. IMPORTANT: for render 2.0 only.\nIf you write \"buffer://\" at the beginning followed by the buffer name, the texture will be taken from there (needed to display 2D scenes in 3D) Код: создаёт `SetMaterialAction` в `addActionToSequence` через `createSetMaterialAction`. |

### 78. `SetTextureTilingBrick`

| Поле | Значение |
|---|---|
| Название RU | Тайлинг (масштаб) текстуры |
| Название EN | Texture tiling (scale) |
| Ключ строки / layout | `brick_texture_tiling` / `brick_texture_tiling` |
| Параметры | Formula: OBJECT_ID (`tiling_obj_id`); Formula: X (`tiling_u`); Formula: Y (`tiling_v`) |
| Дефолт в палитре | ("myObject", 10f, 10f) |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/SetTextureTilingBrick.java:8` |
| Класс экшена | SetTextureTilingAction (catroid/src/main/java/org/catrobat/catroid/content/actions/SetTextureTilingAction.kt:8) — через createSetTextureTilingAction |
| Описание | Задает мозаичное повторение (tiling) текстуры 3D-объекта по осям U и V. EN: Sets the texture tiling (repeat scale) of a 3D object along the U and V axes. Код: создаёт `SetTextureTilingAction` в `addActionToSequence` через `createSetTextureTilingAction`. |

### 79. `SetEmissiveBrick`

| Поле | Значение |
|---|---|
| Название RU | Задать свечение материала |
| Название EN | Set Material Emissive |
| Ключ строки / layout | `brick_set_emissive` / `brick_set_emissive` |
| Параметры | Formula: OBJECT_NAME (`brick_set_emissive_object`); Formula: RED (`brick_set_emissive_r`); Formula: GREEN (`brick_set_emissive_g`); Formula: BLUE (`brick_set_emissive_b`); Formula: ALPHA (`brick_set_emissive_a`); Formula: INTENSITY (`brick_set_emissive_intensity`); Formula: MATERIAL_TEXTURE_COLOR (`brick_set_emissive_texture`) |
| Дефолт в палитре | ("myObject", 1f, 1f, 1f, 1f, 10f, "emissive_texture.png") |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/SetEmissiveBrick.java:8` |
| Класс экшена | SetEmissiveAction (catroid/src/main/java/org/catrobat/catroid/content/actions/SetEmissiveAction.kt:9) — через createSetEmissiveAction |
| Описание | Задает свечение (emissive) материала 3D объекта по имени: цвет (R, G, B, A), интенсивность свечения и текстуру свечения. EN: Sets the emissive (glow) properties of a 3D object's material by name: color (R, G, B, A), glow intensity and glow texture. Код: создаёт `SetEmissiveAction` в `addActionToSequence` через `createSetEmissiveAction`. |

### 80. `SetAnisotropicFilterBrick`

| Поле | Значение |
|---|---|
| Название RU | Задать качество текстур |
| Название EN | Set texture quality of |
| Ключ строки / layout | `brick_set_anisotropic_filter_header` / `brick_set_anisotropic_filter` |
| Параметры | Formula: VALUE_1 (`brick_set_anisotropic_filter_object_id_value`); Formula: VALUE_2 (`brick_set_anisotropic_filter_level_value`) |
| Дефолт в палитре | ("myObject", 2.0) |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/SetAnisotropicFilterBrick.java:14` |
| Класс экшена | SetAnisotropicFilterAction (catroid/src/main/java/org/catrobat/catroid/content/actions/SetAnisotropicFilterAction.kt:14) — через createSetAnisotropicFilterAction |
| Описание | Задает уровень анизотропной фильтрации EN: Sets the level of anisotropic filtering Код: создаёт `SetAnisotropicFilterAction` в `addActionToSequence` через `createSetAnisotropicFilterAction`. |

## Анимация и скелет / Animation & Bones (`subcategory_3d_animation`)

### 81. `PlayAnimationBrick`

| Поле | Значение |
|---|---|
| Название RU | Воспроизвести анимацию |
| Название EN | Play animation |
| Ключ строки / layout | `brick_play_animation_header` / `brick_play_animation` |
| Параметры | Formula: VALUE_1 (`brick_play_animation_object_id_value`); Formula: VALUE_2 (`brick_play_animation_name_value`); Formula: VALUE_3 (`brick_play_animation_loops_value`); Formula: VALUE_4 (`brick_play_animation_speed_value`); Formula: VALUE_5 (`brick_play_animation_transition_value`) |
| Дефолт в палитре | ("myObject", "idle", -1, 1.0, 0.2) |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/PlayAnimationBrick.java:14` |
| Класс экшена | PlayAnimationAction (catroid/src/main/java/org/catrobat/catroid/content/actions/PlayAnimationAction.kt:14) — через createPlayAnimationAction |
| Описание | Воспроизводит анимацию для обьекта ВАЖНО: только для GLB и GLTF моделей EN: Plays an animation for an object. IMPORTANT: only for GLB and GLTF models Код: создаёт `PlayAnimationAction` в `addActionToSequence` через `createPlayAnimationAction`. |

### 82. `Ease3DPropertyBrick`

| Поле | Значение |
|---|---|
| Название RU | Плавная 3D анимация |
| Название EN | Ease 3D animation |
| Ключ строки / layout | `brick_ease_3d_title` / `brick_ease_3d_property` |
| Параметры | Formula: VALUE_1 (`brick_ease_3d_property_object_id`); Formula: VALUE_2 (`brick_ease_3d_property_start_edit`); Formula: VALUE_3 (`brick_ease_3d_property_end_edit`); Formula: VALUE_4 (`brick_ease_3d_property_duration_edit`); Spinner `R.array.brick_ease_3d_properties` EN: Position X / Position Y / Position Z / Rotation X / Rotation Y / Rotation Z / Scale X / Scale Y / Scale Z; Spinner `R.array.brick_easing_types` EN: None / Linear / Quad Ease In / Quad Ease Out / Quad Ease In Out / Cubic Ease In / Cubic Ease Out / Cubic Ease In Out / Quart Ease In / Quart Ease Out / Quart Ease In Out / Quint Ease In / Quint Ease Out / Quint Ease In Out / Sine Ease In / Sine Ease Out / Sine Ease In Out / Expo Ease In / Expo Ease Out / Expo Ease In Out / Circ Ease In / Circ Ease Out / Circ Ease In Out / Back Ease In / Back Ease Out / Back Ease In Out / Elastic Ease In / Elastic Ease Out / Elastic Ease In Out / Bounce Ease In / Bounce Ease Out / Bounce Ease In Out |
| Дефолт в палитре | (Formula("myObject"), 0, 0, Formula(0.0), Formula(1.0), Formula(1.0)) |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/Ease3DPropertyBrick.java:14` |
| Класс экшена | Ease3DPropertyAction (catroid/src/main/java/org/catrobat/catroid/content/actions/Ease3DPropertyAction.kt:11) — через createEase3DPropertyAction |
| Описание | Плавно анимирует выбранное свойство 3D-объекта (позиция, поворот, масштаб) от начального до конечного значения с функцией сглаживания. EN: Smoothly eases the selected 3D object property (position, rotation, scale) from a start to an end value using an easing function. Код: создаёт `Ease3DPropertyAction` в `addActionToSequence` через `createEase3DPropertyAction`. |

### 83. `SetAnimationSpeedBrick`

| Поле | Значение |
|---|---|
| Название RU | Скорость 3D анимации |
| Название EN | 3D Animation Speed |
| Ключ строки / layout | `brick_animation_speed` / `brick_set_animation_speed` |
| Параметры | Formula: OBJECT_ID (`anim_obj_id`); Formula: SPEED (`anim_speed`) |
| Дефолт в палитре | ("myObject", 10f) |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/SetAnimationSpeedBrick.java:7` |
| Класс экшена | SetAnimationSpeedAction (catroid/src/main/java/org/catrobat/catroid/content/actions/SetAnimationSpeedAction.kt:7) — через createSetAnimationSpeedAction |
| Описание | Задает скорость воспроизведения анимации обьекта по его ID (1.0 = нормальная скорость). EN: Sets the animation playback speed of an object by its ID (1.0 = normal speed). Код: создаёт `SetAnimationSpeedAction` в `addActionToSequence` через `createSetAnimationSpeedAction`. |

### 84. `StopAnimationBrick`

| Поле | Значение |
|---|---|
| Название RU | Остановить анимацию |
| Название EN | Stop animation of |
| Ключ строки / layout | `brick_stop_animation_header` / `brick_stop_animation` |
| Параметры | Formula: VALUE_1 (`brick_stop_animation_object_id_value`) |
| Дефолт в палитре | ("myObject") |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/StopAnimationBrick.java:14` |
| Класс экшена | StopAnimationAction (catroid/src/main/java/org/catrobat/catroid/content/actions/StopAnimationAction.kt:14) — через createStopAnimationAction |
| Описание | Останавливает 3D-анимацию объекта по его ID. ВАЖНО: работает только в 3D-рендере. EN: Stops the 3D animation of an object by its ID. IMPORTANT: only works in the 3D renderer. Код: создаёт `StopAnimationAction` в `addActionToSequence` через `createStopAnimationAction`. |

### 85. `KeyframeAnimationBrick`

| Поле | Значение |
|---|---|
| Название RU | Кейфрейм анимация |
| Название EN | Keyframe Animation |
| Ключ строки / layout | `brick_keyframe_anim_title` / `brick_keyframe_animation` |
| Параметры | Formula: VALUE_1 (`brick_keyframe_anim_id`); Formula: VALUE_2 (`brick_keyframe_anim_time_value`); Spinner `R.array.keyframe_actions` EN: Add / Remove / Move |
| Дефолт в палитре | () |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/KeyframeAnimationBrick.java:14` |
| Класс экшена | KeyframeAnimationAction (catroid/src/main/java/org/catrobat/catroid/content/actions/KeyframeAnimationAction.kt:8) — через createKeyframeAnimationAction |
| Описание | Управляет покадровой (keyframe) 3D-анимацией объекта по его ID: воспроизвести, остановить или перейти к моменту времени. EN: Controls keyframe 3D animation of an object by its ID: play, stop, or jump to a time. Код: создаёт `KeyframeAnimationAction` в `addActionToSequence` через `createKeyframeAnimationAction`. |

### 86. `ThreedAttachObjectToBoneBrick`

| Поле | Значение |
|---|---|
| Название RU | Прикрепить |
| Название EN | Attach |
| Ключ строки / layout | `brick_threed_attach_obj_1` / `brick_threed_attach_object_to_bone` |
| Параметры | Formula: VALUE_1 (`brick_attach_child_id`); Formula: VALUE_2 (`brick_attach_parent_id`); Formula: VALUE_3 (`brick_attach_bone_name`); Formula: X (`brick_attach_ox`); Formula: Y (`brick_attach_oy`); Formula: Z (`brick_attach_oz`) |
| Дефолт в палитре | ("attachedObject", "hand", "Hand_R", 0.0, 0.0, 0.0) |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/ThreedAttachObjectToBoneBrick.java:8` |
| Класс экшена | ThreedAttachObjectToBoneAction (catroid/src/main/java/org/catrobat/catroid/content/actions/ThreedAttachObjectToBoneAction.java:8) — через createThreedAttachObjectToBoneAction |
| Описание | Прикрепляет 3D-объект (по ID) к кости модели: указываются ID дочернего объекта, ID модели, имя кости и смещение по X, Y, Z. Объект будет следовать за движением этой кости. EN: Attaches a 3D object (by ID) to a bone of a model: you specify the child object ID, the model ID, the bone name and an offset X, Y, Z. The object then follows that bone's movement. Код: создаёт `ThreedAttachObjectToBoneAction` в `addActionToSequence` через `createThreedAttachObjectToBoneAction`. |

### 87. `ThreedBindBoneToObjectBrick`

| Поле | Значение |
|---|---|
| Название RU | Привязать кость |
| Название EN | Bind bone |
| Ключ строки / layout | `brick_threed_bind_bone_1` / `brick_threed_bind_bone_to_object` |
| Параметры | Formula: VALUE_1 (`brick_bind_bone_name`); Formula: VALUE_2 (`brick_bind_model_id`); Formula: VALUE_3 (`brick_bind_target_id`) |
| Дефолт в палитре | ("Hand_R", "hand", "myObject") |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/ThreedBindBoneToObjectBrick.java:8` |
| Класс экшена | ThreedBindBoneToObjectAction (catroid/src/main/java/org/catrobat/catroid/content/actions/ThreedBindBoneToObjectAction.java:8) — через createThreedBindBoneToObjectAction |
| Описание | Привязывает кость (по имени) указанной 3D-модели к другому объекту, так что кость следует за этим объектом. Задаются имя кости, ID модели и ID целевого объекта. EN: Binds a bone (by name) of the specified 3D model to another object so the bone follows that object. You provide the bone name, the model ID and the target object ID. Код: создаёт `ThreedBindBoneToObjectAction` в `addActionToSequence` через `createThreedBindBoneToObjectAction`. |

## Воксельная графика / Voxel Graphics (`subcategory_3d_voxels`)

### 88. `VoxelCreateWorldBrick`

| Поле | Значение |
|---|---|
| Название RU | Voxel: Создать мир |
| Название EN | Voxel: Create world |
| Ключ строки / layout | `brick_voxel_create` / `brick_voxel_create_world` |
| Параметры | Formula: NAME (`brick_voxel_world_id`); Formula: X (`brick_voxel_size_x`); Formula: Y (`brick_voxel_size_y`); Formula: Z (`brick_voxel_size_z`); Formula: X_1 (`brick_voxel_world_x`); Formula: Y_1 (`brick_voxel_world_y`); Formula: Z_1 (`brick_voxel_world_z`) |
| Дефолт в палитре | (Formula("chunk1"), Formula(16), Formula(16), Formula(16), Formula(16), Formula(0), Formula(0)) |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/VoxelCreateWorldBrick.java:31` |
| Класс экшена | VoxelCreateWorldAction (catroid/src/main/java/org/catrobat/catroid/content/actions/VoxelCreateWorldAction.kt:8) — через createVoxelCreateWorldAction |
| Описание | Создает воксельный мир с заданным размером по осям X Y Z и позицией мира в пространстве. EN: Creates a voxel world with the given size along the X Y Z axes and the world position in space. Код: создаёт `VoxelCreateWorldAction` в `addActionToSequence` через `createVoxelCreateWorldAction`. |

### 89. `VoxelConfigBrick`

| Поле | Значение |
|---|---|
| Название RU | Voxel: Настроить тип блока |
| Название EN | Voxel: Configure block type |
| Ключ строки / layout | `brick_voxel_config` / `brick_voxel_config` |
| Параметры | Formula: NAME (`brick_voxel_config_edit_id`); Formula: X (`brick_voxel_config_edit_shape`); Formula: Y (`brick_voxel_config_edit_tx`); Formula: Z (`brick_voxel_config_edit_ty`) |
| Дефолт в палитре | ("1", 0, 0, 0) |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/VoxelConfigBrick.java:31` |
| Класс экшена | VoxelConfigAction (catroid/src/main/java/org/catrobat/catroid/content/actions/VoxelConfigAction.kt:8) — через createVoxelConfigAction |
| Описание | Настраивает тип блока: задает форму (0:куб, 1:крест, 2-7:плоскости) и координаты X/Y в атласе текстур для блока с указанным ID. EN: Configures a block type: sets the shape (0:Cube, 1:Cross, 2-7:Planes) and the X/Y atlas texture coordinates for the block with the given ID. Код: создаёт `VoxelConfigAction` в `addActionToSequence` через `createVoxelConfigAction`. |

### 90. `VoxelSetTransparentBrick`

| Поле | Значение |
|---|---|
| Название RU | Voxel: Прозрачность блока |
| Название EN | Voxel: Block Transparency |
| Ключ строки / layout | `brick_voxel_transp` / `brick_voxel_set_transparent` |
| Параметры | Formula: VALUE (`brick_voxel_transp_edit_id`); Formula: VALUE_1 (`brick_voxel_transp_edit_value`) |
| Дефолт в палитре | (1, 1) |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/VoxelSetTransparentBrick.java:31` |
| Класс экшена | VoxelSetTransparentAction (catroid/src/main/java/org/catrobat/catroid/content/actions/VoxelSetTransparentAction.kt:8) — через createVoxelSetTransparentAction |
| Описание | Включает или выключает прозрачность блока по его ID (1 — прозрачный, 0 — нет). EN: Enables or disables transparency for a block by its ID (1 = transparent, 0 = opaque). Код: создаёт `VoxelSetTransparentAction` в `addActionToSequence` через `createVoxelSetTransparentAction`. |

### 91. `VoxelSetBlockBrick`

| Поле | Значение |
|---|---|
| Название RU | Voxel: Установить блок |
| Название EN | Voxel: Set block |
| Ключ строки / layout | `brick_voxel_set` / `brick_voxel_set_block` |
| Параметры | Formula: NAME (`brick_voxel_set_edit_world`); Formula: X (`brick_voxel_set_edit_x`); Formula: Y (`brick_voxel_set_edit_y`); Formula: Z (`brick_voxel_set_edit_z`); Formula: VALUE (`brick_voxel_set_edit_type`); Formula: DATA (`brick_voxel_set_edit_data`) |
| Дефолт в палитре | ("chunk1", 0.0, 0.0, 0.0, 1, 0) |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/VoxelSetBlockBrick.java:31` |
| Класс экшена | VoxelSetBlockAction (catroid/src/main/java/org/catrobat/catroid/content/actions/VoxelSetBlockAction.kt:8) — через createVoxelSetBlockAction |
| Описание | Устанавливает блок в позиции X Y Z указанного мира с заданным типом и данными (0-255). EN: Sets a block at position X Y Z of the given world with the specified type and data (0-255). Код: создаёт `VoxelSetBlockAction` в `addActionToSequence` через `createVoxelSetBlockAction`. |

### 92. `VoxelLoadStringBrick`

| Поле | Значение |
|---|---|
| Название RU | Voxel: Загрузить из строки |
| Название EN | Voxel: Load from string |
| Ключ строки / layout | `brick_voxel_load_str` / `brick_voxel_load_string` |
| Параметры | Formula: NAME (`brick_voxel_load_id`); Formula: TEXT (`brick_voxel_load_data`); Formula: X (`brick_voxel_load_dx`); Formula: Y (`brick_voxel_load_dy`); Formula: Z (`brick_voxel_load_dz`) |
| Дефолт в палитре | (Formula("chunk1"), Formula("1$1#0$0&1$1#0$0"), Formula("$"), Formula("#"), Formula("&")) |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/VoxelLoadStringBrick.java:31` |
| Класс экшена | VoxelLoadStringAction (catroid/src/main/java/org/catrobat/catroid/content/actions/VoxelLoadStringAction.kt:8) — через createVoxelLoadStringAction |
| Описание | Загружает воксельные данные из строки в мир, используя разделители X, Y и Z между значениями блоков. EN: Loads voxel data from a string into a world, using the X, Y and Z delimiters between block values. Код: создаёт `VoxelLoadStringAction` в `addActionToSequence` через `createVoxelLoadStringAction`. |

### 93. `VoxelBuildBrick`

| Поле | Значение |
|---|---|
| Название RU | Voxel: Пересобрать меш |
| Название EN | Voxel: Build mesh |
| Ключ строки / layout | `brick_voxel_build` / `brick_voxel_build` |
| Параметры | Formula: NAME (`brick_voxel_world_id`); Formula: TEXT (`brick_voxel_atlas_text`); Formula: X (`brick_voxel_atlas_width`); Formula: Y (`brick_voxel_atlas_height`) |
| Дефолт в палитре | (Formula("chunk1"), Formula("atlas.png"), Formula(16), Formula(16)) |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/VoxelBuildBrick.java:31` |
| Класс экшена | VoxelBuildAction (catroid/src/main/java/org/catrobat/catroid/content/actions/VoxelBuildAction.kt:9) — через createVoxelBuildAction |
| Описание | Строит воксельную сетку (mesh) для мира по его имени из текстуры-атласа с указанным количеством блоков по ширине и высоте. EN: Builds the voxel mesh for a world by its name from the atlas texture with the given number of blocks in width and height. Код: создаёт `VoxelBuildAction` в `addActionToSequence` через `createVoxelBuildAction`. |

### 94. `VoxelDeleteBrick`

| Поле | Значение |
|---|---|
| Название RU | Voxel: Удалить мир |
| Название EN | Voxel: Delete world |
| Ключ строки / layout | `brick_voxel_delete` / `brick_voxel_delete` |
| Параметры | Formula: NAME (`brick_voxel_delete_edit_id`) |
| Дефолт в палитре | (Formula("chunk1")) |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/VoxelDeleteBrick.java:31` |
| Класс экшена | VoxelDeleteAction (catroid/src/main/java/org/catrobat/catroid/content/actions/VoxelDeleteAction.kt:8) — через createVoxelDeleteAction |
| Описание | Удаляет воксельный мир по его имени. EN: Deletes the voxel world by its name. Код: создаёт `VoxelDeleteAction` в `addActionToSequence` через `createVoxelDeleteAction`. |

## Пространственный звук / Spatial Sound (`subcategory_3d_sound`)

### 95. `PrepareSoundBrick2`

| Поле | Значение |
|---|---|
| Название RU | Подготовить звук (для 3D) |
| Название EN | Prepare audio (for 3D) |
| Ключ строки / layout | `brick_prepare_sound2` / `brick_prepare_sound2` |
| Параметры | Formula: FILE_NAME (`brick_prepare_sound_file_edit`); Formula: SOUND_NAME (`brick_prepare_sound_name_edit`) |
| Дефолт в палитре | ("sound.mp3", "sound") |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/PrepareSoundBrick2.java:8` |
| Класс экшена | PrepareSoundAction2 (catroid/src/main/java/org/catrobat/catroid/content/actions/PrepareSoundAction2.kt:8) — через createPrepareSound2Action |
| Описание | Подготавливает звук к воспроизведению ВАЖНО: КАК АУДИО! рекомендуется для sfx и коротких звуков EN: Prepares sound for playback IMPORTANT: AS AUDIO! recommended for SFX and short sounds Код: создаёт `PrepareSoundAction2` в `addActionToSequence` через `createPrepareSound2Action`. |

### 96. `PrepareMusicAs3DSoundBrick`

| Поле | Значение |
|---|---|
| Название RU | Подготовить музыку (для 3D) |
| Название EN | Prepare music file (for 3D) |
| Ключ строки / layout | `brick_prepare_music` / `brick_prepare_music` |
| Параметры | Formula: FILE_NAME (`brick_prepare_music_file_edit`); Formula: SOUND_NAME (`brick_prepare_music_name_edit`) |
| Дефолт в палитре | ("sound.mp3", "sound") |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/PrepareMusicAs3DSoundBrick.java:8` |
| Класс экшена | PrepareMusicAction (catroid/src/main/java/org/catrobat/catroid/content/actions/PrepareMusicAction.kt:8) — через createPrepareMusicAction |
| Описание | Подготавливает музыку к воспроизведению ВАЖНО: КАК МУЗЫКУ! рекомендуется для воспроизведения длинной музыки EN: Prepares music for playback IMPORTANT: AS MUSIC! recommended for long music playback Код: создаёт `PrepareMusicAction` в `addActionToSequence` через `createPrepareMusicAction`. |

### 97. `PlaySoundAtPositionBrick`

| Поле | Значение |
|---|---|
| Название RU | Проиграть 3D звук |
| Название EN | Play 3D sound |
| Ключ строки / layout | `brick_play_sound_3d` / `brick_play_sound_at_pos` |
| Параметры | Formula: SOUND_NAME (`brick_play_sound_3d_name_edit`); Formula: INSTANCE_NAME (`brick_play_sound_3d_instance_edit`); Formula: POS_X (`brick_play_sound_3d_x_edit`); Formula: POS_Y (`brick_play_sound_3d_y_edit`); Formula: POS_Z (`brick_play_sound_3d_z_edit`); Formula: VOLUME (`brick_play_sound_3d_volume_edit`); Formula: PITCH (`brick_play_sound_3d_pitch_edit`) |
| Дефолт в палитре | ("sound", "soundInstance") |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/PlaySoundAtPositionBrick.java:13` |
| Класс экшена | PlaySoundAtPositionAction (catroid/src/main/java/org/catrobat/catroid/content/actions/PlaySoundAtPositionAction.kt:8) — через createPlaySoundAtPositionAction |
| Описание | Воспроизводит подготовленный звук в 3D пространстве EN: Plays the prepared sound in 3D space Код: создаёт `PlaySoundAtPositionAction` в `addActionToSequence` через `createPlaySoundAtPositionAction`. |

### 98. `Set3DSoundMaxDistanceBrick`

| Поле | Значение |
|---|---|
| Название RU | Задать макс. дистанцию 3D звука |
| Название EN | Set 3D Sound Max Distance |
| Ключ строки / layout | `brick_3d_sound_max_distance` / `brick_set_3d_sound_max_distance` |
| Параметры | Formula: INSTANCE_NAME (`brick_set_3d_sound_max_distance_instance`); Formula: DISTANCE (`brick_set_3d_sound_max_distance_val`) |
| Дефолт в палитре | ("soundInstance", 250f) |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/Set3DSoundMaxDistanceBrick.java:8` |
| Класс экшена | Set3DSoundMaxDistanceAction (catroid/src/main/java/org/catrobat/catroid/content/actions/Set3DSoundMaxDistanceAction.kt:8) — через createSet3DSoundMaxDistanceAction |
| Описание | Задает максимальную дистанцию, на которой будет слышно звук EN: Sets the maximum distance at which the sound can be heard Код: создаёт `Set3DSoundMaxDistanceAction` в `addActionToSequence` через `createSet3DSoundMaxDistanceAction`. |

### 99. `SetSoundInstanceVolumeBrick`

| Поле | Значение |
|---|---|
| Название RU | Установить громкость звука |
| Название EN | Set sound volume |
| Ключ строки / layout | `brick_sound_inst_vol` / `brick_sound_volume` |
| Параметры | Formula: NAME (`sound_inst_name`); Formula: VOLUME (`sound_inst_vol`) |
| Дефолт в палитре | ("soundInstance", 60f) |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/SetSoundInstanceVolumeBrick.java:8` |
| Класс экшена | SetSoundInstanceVolumeAction (catroid/src/main/java/org/catrobat/catroid/content/actions/SetSoundInstanceVolumeAction.kt:7) — через createSetSoundInstanceVolumeAction |
| Описание | Задает громкость конкретного экземпляра звука по его имени. EN: Sets the volume of a specific sound instance by its name. Код: создаёт `SetSoundInstanceVolumeAction` в `addActionToSequence` через `createSetSoundInstanceVolumeAction`. |

### 100. `SetSoundInstancePitchBrick`

| Поле | Значение |
|---|---|
| Название RU | Установить тон звука |
| Название EN | Set sound pitch |
| Ключ строки / layout | `brick_sound_inst_pitch` / `brick_sound_pitch` |
| Параметры | Formula: NAME (`sound_inst_name_p`); Formula: PITCH (`sound_inst_pitch`) |
| Дефолт в палитре | ("soundInstance", 140f) |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/SetSoundInstancePitchBrick.java:8` |
| Класс экшена | SetSoundInstancePitchAction (catroid/src/main/java/org/catrobat/catroid/content/actions/SetSoundInstancePitchAction.kt:7) — через createSetSoundInstancePitchAction |
| Описание | Задает высоту тона (pitch) проигрываемому экземпляру звука по его имени. EN: Sets the pitch of a currently playing sound instance identified by name. Код: создаёт `SetSoundInstancePitchAction` в `addActionToSequence` через `createSetSoundInstancePitchAction`. |

### 101. `Set3DSoundPositionBrick`

| Поле | Значение |
|---|---|
| Название RU | Задать позицию 3D звука |
| Название EN | Set 3D sound position |
| Ключ строки / layout | `brick_set_3d_sound_position` / `brick_set_3d_sound_position` |
| Параметры | Formula: NAME (`brick_set_3d_sound_name`); Formula: VALUE_X (`brick_set_3d_sound_x`); Formula: VALUE_Y (`brick_set_3d_sound_y`); Formula: VALUE_Z (`brick_set_3d_sound_z`) |
| Дефолт в палитре | ("soundInstance", 10, 10, 10) |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/Set3DSoundPositionBrick.java:8` |
| Класс экшена | Set3DSoundPositionAction (catroid/src/main/java/org/catrobat/catroid/content/actions/Set3DSoundPositionAction.kt:8) — через createSet3DSoundPositionAction |
| Описание | Задает позицию 3D звуку EN: Sets the position of 3D sound Код: создаёт `Set3DSoundPositionAction` в `addActionToSequence` через `createSet3DSoundPositionAction`. |

### 102. `StopSoundBrick2`

| Поле | Значение |
|---|---|
| Название RU | Остановить 3D звук (экземпляр) |
| Название EN | Stop 3D sound (instance) |
| Ключ строки / layout | `brick_stop_sound_3d` / `brick_stop_sound_3d` |
| Параметры | Formula: INSTANCE_NAME (`brick_stop_sound_3d_instance_edit`) |
| Дефолт в палитре | ("soundInstance") |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/StopSoundBrick2.java:8` |
| Класс экшена | StopSoundAction2 (catroid/src/main/java/org/catrobat/catroid/content/actions/StopSoundAction2.kt:8) — через createStopSoundAction2 |
| Описание | Останавливает 3D звук EN: Stops 3D sound Код: создаёт `StopSoundAction2` в `addActionToSequence` через `createStopSoundAction2`. |

### 103. `SetGlobalSoundVolumeBrick`

| Поле | Значение |
|---|---|
| Название RU | Общая громкость 3D звуков |
| Название EN | Set global 3D sound volume to |
| Ключ строки / layout | `brick_set_global_sound_volume` / `brick_set_global_sound_volume` |
| Параметры | Formula: VOLUME (`brick_set_global_sound_volume_edit`) |
| Дефолт в палитре | (60.0) |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/SetGlobalSoundVolumeBrick.java:8` |
| Класс экшена | SetGlobalSoundVolumeAction (catroid/src/main/java/org/catrobat/catroid/content/actions/SetGlobalSoundVolumeAction.kt:8) — через createSetGlobalSoundVolumeAction |
| Описание | Задает глобальную громкость 3D звуков EN: Sets the global volume of 3D sounds Код: создаёт `SetGlobalSoundVolumeAction` в `addActionToSequence` через `createSetGlobalSoundVolumeAction`. |

## Эффекты, сцены и ИИ / Effects, Scenes & AI (`subcategory_3d_effects`)

### 104. `SetAIBrick`

| Поле | Значение |
|---|---|
| Название RU | ИИ для объекта |
| Название EN | AI for object |
| Ключ строки / layout | `brick_ai_set` / `brick_set_ai` |
| Параметры | Formula: OBJECT_ID (`brick_ai_obj_id`); Formula: MODE (`brick_ai_mode`); Formula: TARGET (`brick_ai_target`); Formula: SPEED (`brick_ai_speed`); Formula: DISTANCE (`brick_ai_stop`); Formula: RANGE (`brick_ai_range`); Formula: STEP_HEIGHT (`brick_ai_step`); Formula: AVOID_OBSTACLES (`brick_ai_avoid`) |
| Дефолт в палитре | ("myObject", 1, "followingObject", 2f, 1f, 10f, 1f, 1) |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/SetAIBrick.java:8` |
| Класс экшена | SetAIAction (catroid/src/main/java/org/catrobat/catroid/content/actions/SetAIAction.kt:9) — через createSetAIAction |
| Описание | Дает объекту ИИ для обхода препятствий. Может использоваться например для врага, который должен ходить по карте EN: Gives the object AI for obstacle avoidance. Can be used, for example, for an enemy that needs to walk around the map Код: создаёт `SetAIAction` в `addActionToSequence` через `createSetAIAction`. |

### 105. `LoadSceneBrick`

| Поле | Значение |
|---|---|
| Название RU | Загрузить 3D сцену |
| Название EN | Load 3D Scene |
| Ключ строки / layout | `brick_load_scene` / `brick_load_scene` |
| Параметры | Formula: VALUE (`brick_load_scene_edit_text`) |
| Дефолт в палитре | ("my_level.rscene") |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/LoadSceneBrick.java:8` |
| Класс экшена | LoadSceneAction (catroid/src/main/java/org/catrobat/catroid/content/actions/LoadSceneAction.kt:14) — через createLoadSceneAction |
| Описание | Загружает сцену из .rscene файла, созданную в редакторе. ВАЖНО: НЕОБХОДИМО ВКЛЮЧИТЬ РЕНДЕР 2.0! полностью заменяет текущую. EN: Loads a scene from a .rscene file created in the editor. IMPORTANT: RENDERER 2.0 MUST BE ENABLED! completely replaces the current one. Код: создаёт `LoadSceneAction` в `addActionToSequence` через `createLoadSceneAction`. |

### 106. `LoadSceneAdditiveBrick`

| Поле | Значение |
|---|---|
| Название RU | Загрузить сцену (добавить) |
| Название EN | Load scene additively |
| Ключ строки / layout | `brick_load_scene_additive` / `brick_load_scene_additive` |
| Параметры | Formula: TEXT (`brick_load_scene_additive_edit_text`) |
| Дефолт в палитре | ("my_level.rscene") |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/LoadSceneAdditiveBrick.java:8` |
| Класс экшена | LoadSceneAdditiveAction (catroid/src/main/java/org/catrobat/catroid/content/actions/LoadSceneAdditiveAction.kt:9) — через createLoadSceneAdditiveAction |
| Описание | Загружает сцену НЕ ЗАМЕНЯЯ ТЕКУЩУЮ! ВАЖНО: дублирующиеся имена будут с постфиксом (1), например был обьект 'player', при добавлении новой сцены с еще одним 'player' он будет переименован в 'player (1)' EN: Loads a scene WITHOUT replacing the current one! IMPORTANT: duplicate names get a (1) suffix; for example, if an object named 'player' already exists and a new scene with another 'player' is added, it will be renamed to 'player (1)' Код: создаёт `LoadSceneAdditiveAction` в `addActionToSequence` через `createLoadSceneAdditiveAction`. |

### 107. `SetPostProcessingBrick`

| Поле | Значение |
|---|---|
| Название RU | Задать эффект (старый) |
| Название EN | Set Effect (old) |
| Ключ строки / layout | `brick_set_pp_effect` / `brick_set_pp_effect` |
| Параметры | Formula: VALUE (`brick_pp_value_edit`); Spinner `R.array.pp_effect_params` EN: Enabled (1/0) / Intensity / Strength / Threshold / Radius / Angle / Rotation / Color (RGB) / Offset / Scale / Speed / Quality / Distance / Power / Center X / Center Y / Amount / Softness / Vignette; Spinner `R.array.pp_effect_types` EN: Global (Enable/Quality) / Bloom / Vignette / Levels (Color) / Film Grain / FXAA / Chromatic Aberration / Radial Blur / Old TV / CRT Monitor / Fisheye / Water Distortion / Motion Blur / Lens Flare / Gaussian Blur / Zoom Blur / ACES Tonemapping / Eye Adaptation / Ray Tracing (SSR) |
| Дефолт в палитре | (1, 4, Formula(2)) |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/SetPostProcessingBrick.java:13` |
| Класс экшена | SetPostProcessingAction (catroid/src/main/java/org/catrobat/catroid/content/actions/SetPostProcessingAction.kt:12) — через createSetPostProcessingAction |
| Описание | Может включить эффект для постобработки, настроить его параметры (ДАННЫЙ БЛОК УСТАРЕЛ. СОВЕТУЕТСЯ ИСПОЛЬЗОВАТЬ ДРУГОЙ АНАЛОГИЧНЫЙ БЛОК) EN: Can enable a post-processing effect and configure its parameters (THIS BRICK IS DEPRECATED. IT IS RECOMMENDED TO USE ANOTHER SIMILAR BRICK) Код: создаёт `SetPostProcessingAction` в `addActionToSequence` через `createSetPostProcessingAction`. |

### 108. `SetPostProcessingNewBrick`

| Поле | Значение |
|---|---|
| Название RU | Задать эффект |
| Название EN | Set post-processing effect |
| Ключ строки / layout | `brick_set_post_processing_new` / `brick_set_post_processing_new` |
| Параметры | Formula: VALUE (`brick_pp_value_edit`); Spinner `R.array.pp_new_effect_types`; Spinner `R.array.pp_params_aces`; Spinner `R.array.pp_params_bloom`; Spinner `R.array.pp_params_chromatic`; Spinner `R.array.pp_params_crt`; Spinner `R.array.pp_params_dof`; Spinner `R.array.pp_params_eyeadapt`; Spinner `R.array.pp_params_fisheye`; Spinner `R.array.pp_params_fsr`; Spinner `R.array.pp_params_fxaa`; Spinner `R.array.pp_params_gaussian`; Spinner `R.array.pp_params_global`; Spinner `R.array.pp_params_godrays`; Spinner `R.array.pp_params_grain`; Spinner `R.array.pp_params_heightfog`; Spinner `R.array.pp_params_lensflare`; Spinner `R.array.pp_params_levels`; Spinner `R.array.pp_params_motionblur`; Spinner `R.array.pp_params_oldtv`; Spinner `R.array.pp_params_radial`; Spinner `R.array.pp_params_ssao`; Spinner `R.array.pp_params_ssgi`; Spinner `R.array.pp_params_ssr`; Spinner `R.array.pp_params_vignette`; Spinner `R.array.pp_params_volfog`; Spinner `R.array.pp_params_water`; Spinner `R.array.pp_params_zoom` |
| Дефолт в палитре | () |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/SetPostProcessingNewBrick.java:13` |
| Класс экшена | AddHingeAction (catroid/src/main/java/org/catrobat/catroid/content/actions/AddHingeAction.kt:11) — через createSetPostProcessingNewAction |
| Описание | Может включить и настроить эффекты для постобработки. (для рендера 2.0) EN: Can enable and configure post-processing effects. (for renderer 2.0) Код: создаёт `AddHingeAction` в `addActionToSequence` через `createSetPostProcessingNewAction`. |

### 109. `SetRenderResolutionBrick`

| Поле | Значение |
|---|---|
| Название RU | Масштаб 3D рендера |
| Название EN | Set render scale to |
| Ключ строки / layout | `brick_render_res` / `brick_set_render_resolution` |
| Параметры | Formula: NAME (`brick_set_render_res_value`); Formula: VALUE (`brick_set_render_aspect_value`) |
| Дефолт в палитре | (0.5f, 1) |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/SetRenderResolutionBrick.java:8` |
| Класс экшена | SetRenderResolutionAction (catroid/src/main/java/org/catrobat/catroid/content/actions/SetRenderResolutionAction.kt:8) — через createSetRenderResolutionAction |
| Описание | Задает разрешение и соотношение сторон для 3D рендера (включает постобработку + работает только в 2.0) EN: Sets the resolution and aspect ratio for 3D rendering (enables post-processing and works only in 2.0) Код: создаёт `SetRenderResolutionAction` в `addActionToSequence` через `createSetRenderResolutionAction`. |

### 110. `SetFpsBrick`

| Поле | Значение |
|---|---|
| Название RU | Ограничить FPS до |
| Название EN | Limit FPS to |
| Ключ строки / layout | `brick_set_fps` / `brick_set_fps` |
| Параметры | Formula: VALUE (`brick_set_fps_edit_value`) |
| Дефолт в палитре | (24) |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/SetFpsBrick.java:8` |
| Класс экшена | SetFpsAction (catroid/src/main/java/org/catrobat/catroid/content/actions/SetFpsAction.kt:8) — через createSetFpsAction |
| Описание | Задает лимит FPS (кадров в секунду) для 3D рендера EN: Sets the FPS (frames per second) limit for 3D rendering Код: создаёт `SetFpsAction` в `addActionToSequence` через `createSetFpsAction`. |

### 111. `CreateParticlesBrick`

| Поле | Значение |
|---|---|
| Название RU | Создать Частицы |
| Название EN | Create Particles |
| Ключ строки / layout | `brick_create_particles_title` / `brick_create_particles` |
| Параметры | Formula: PARTICLE_ID (`brick_particle_id_edit`); Formula: PARTICLE_LOOPING (`brick_particle_looping_edit`); Formula: PARTICLE_DURATION (`brick_particle_duration_edit`); Formula: PARTICLE_LIFETIME (`brick_particle_lifetime_edit`); Formula: PARTICLE_SPEED (`brick_particle_speed_edit`); Formula: PARTICLE_SIZE (`brick_particle_size_edit`); Formula: PARTICLE_END_SIZE (`brick_particle_end_size_edit`); Formula: PARTICLE_GRAVITY (`brick_particle_gravity_edit`); Formula: PARTICLE_MAX_COUNT (`brick_particle_max_count_edit`); Formula: PARTICLE_RATE (`brick_particle_rate_edit`); Formula: PARTICLE_ANGLE (`brick_particle_angle_edit`); Formula: PARTICLE_RADIUS (`brick_particle_radius_edit`); Formula: PARTICLE_START_COLOR (`brick_particle_start_color_edit`); Formula: PARTICLE_END_COLOR (`brick_particle_end_color_edit`); Formula: PARTICLE_TEXTURE (`brick_particle_texture_edit`); Formula: PARTICLE_ADDITIVE (`brick_particle_additive_edit`); Formula: PARTICLE_ROTATION (`brick_particle_rotation_edit`); Formula: PARTICLE_ROTATION_SPEED (`brick_particle_rotation_speed_edit`); Formula: PARTICLE_POS_X (`brick_particle_pos_x_edit`); Formula: PARTICLE_POS_Y (`brick_particle_pos_y_edit`); Formula: PARTICLE_POS_Z (`brick_particle_pos_z_edit`); Formula: PARTICLE_ROT_PITCH (`brick_particle_rot_pitch_edit`); Formula: PARTICLE_ROT_YAW (`brick_particle_rot_yaw_edit`); Formula: PARTICLE_ROT_ROLL (`brick_particle_rot_roll_edit`) |
| Дефолт в палитре | ("particles") |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/CreateParticlesBrick.java:9` |
| Класс экшена | CreateParticlesAction (catroid/src/main/java/org/catrobat/catroid/content/actions/CreateParticlesAction.kt:12) — через createParticlesAction |
| Описание | Создает частицы ВАЖНО: так же создает GameObject с данным ID, указывайте уникальный ID EN: Creates particles IMPORTANT: also creates a GameObject with the given ID, use a unique ID Код: создаёт `CreateParticlesAction` в `addActionToSequence` через `createParticlesAction`. |

### 112. `CreateParticleSystemBrick`

| Поле | Значение |
|---|---|
| Название RU | Создать систему частиц |
| Название EN | Create particle system |
| Ключ строки / layout | `brick_create_particle_system` / `brick_create_particle_system` |
| Параметры | — (без формульных полей) |
| Дефолт в палитре | ("particles", 500.0, 2.0, 5.0) |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/CreateParticleSystemBrick.kt:8` |
| Класс экшена | CreateParticleSystemAction (catroid/src/main/java/org/catrobat/catroid/content/actions/CreateParticleSystemAction.kt:11) — через createParticleSystemAction |
| Описание | Создает систему частиц с заданными параметрами: ID, максимальное количество частиц, время жизни и скорость EN: Creates a particle system with specified parameters: ID, maximum particle count, lifetime, and speed Код: создаёт `CreateParticleSystemAction` в `addActionToSequence` через `createParticleSystemAction`. |

### 113. `SetParticleEmissionBrick`

| Поле | Значение |
|---|---|
| Название RU | Изменить плотность частиц |
| Название EN | Change particle emission |
| Ключ строки / layout | `brick_particle_emission` / `brick_set_particle_emission` |
| Параметры | Formula: OBJECT_ID (`particle_obj_id`); Formula: EMISSION_RATE (`particle_rate2`) |
| Дефолт в палитре | ("particles", 10f) |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/SetParticleEmissionBrick.java:7` |
| Класс экшена | SetParticleEmissionAction (catroid/src/main/java/org/catrobat/catroid/content/actions/SetParticleEmissionAction.kt:7) — через createSetParticleEmissionAction |
| Описание | Изменяет интенсивность (скорость) эмиссии частиц для системы частиц по её ID. EN: Changes the emission rate of a particle system identified by its ID. Код: создаёт `SetParticleEmissionAction` в `addActionToSequence` через `createSetParticleEmissionAction`. |

### 114. `SetParticleColorBrick`

| Поле | Значение |
|---|---|
| Название RU | Задать цвет частиц |
| Название EN | Set particle color to |
| Ключ строки / layout | `set_particle_color_to` / `brick_set_particle_color` |
| Параметры | Formula: COLOR (`brick_set_color_edit_text`) |
| Дефолт в палитре | ("#FFFF8800") |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/SetParticleColorBrick.kt:31` |
| Класс экшена | SetParticleColorAction (catroid/src/main/java/org/catrobat/catroid/content/actions/SetParticleColorAction.kt:32) — через createSetParticleColorAction |
| Описание | Задает цвет частиц EN: Sets the color of particles Код: создаёт `SetParticleColorAction` в `addActionToSequence` через `createSetParticleColorAction`. |

### 115. `FadeParticleEffectBrick`

| Поле | Значение |
|---|---|
| Название RU | Эффект частиц |
| Название EN | Fade particle effect |
| Ключ строки / layout | `fade_particle_effect` / `brick_fade_particle_effect` |
| Параметры | — (без формульных полей) |
| Дефолт в палитре | () |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/FadeParticleEffectBrick.kt:35` |
| Класс экшена | FadeParticleEffectAction (catroid/src/main/java/org/catrobat/catroid/content/actions/FadeParticleEffectAction.kt:29) — через createFadeParticleEffectsAction |
| Описание | Включает или выключает эффект частиц EN: Enables or disables the particle effect Код: создаёт `FadeParticleEffectAction` в `addActionToSequence` через `createFadeParticleEffectsAction`. |

### 116. `ParticleEffectAdditivityBrick`

| Поле | Значение |
|---|---|
| Название RU | Включить аддитивности эффекта частиц |
| Название EN | Turn particle effect additivity |
| Ключ строки / layout | `turn_particle_effect_additivity` / `brick_additive_particle_effect` |
| Параметры | — (без формульных полей) |
| Дефолт в палитре | () |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/ParticleEffectAdditivityBrick.kt:35` |
| Класс экшена | AdditiveParticleEffectAction (catroid/src/main/java/org/catrobat/catroid/content/actions/AdditiveParticleEffectAction.kt:29) — через createAdditiveParticleEffectsAction |
| Описание | Нет в `BrickInfo.java`. Код: создаёт `AdditiveParticleEffectAction` в `addActionToSequence` через `createAdditiveParticleEffectsAction`. |

### 117. `ConfigureParticlesBrick`

| Поле | Значение |
|---|---|
| Название RU | Настроить частицы |
| Название EN | Configure particles |
| Ключ строки / layout | `brick_http_configure_particles_title` / `brick_http_configure_particles` |
| Параметры | Formula: VALUE_1 (`brick_http_configure_particles_id_field`); Formula: VALUE_2 (`brick_http_configure_particles_value_field`); Spinner `R.array.brick_http_configure_particles_types` EN: Emission rate / Lifetime / Speed / Size / Color (R,G,B) / Gravity |
| Дефолт в палитре | ("particles", 0, "15.0") |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/ConfigureParticlesBrick.java:13` |
| Класс экшена | ConfigureParticlesAction (catroid/src/main/java/org/catrobat/catroid/content/actions/ConfigureParticlesAction.kt:9) — через createConfigureParticlesAction |
| Описание | Настраивает свойство системы частиц (время жизни, скорость, размер и др.) по ID. ВАЖНО: сейчас реализована только скорость эмиссии. EN: Sets the selected property of a particle system (lifetime, speed, size, etc.) by ID. NOTE: only the emission rate is currently implemented. Код: создаёт `ConfigureParticlesAction` в `addActionToSequence` через `createConfigureParticlesAction`. |

### 118. `DeleteParticlesBrick`

| Поле | Значение |
|---|---|
| Название RU | Удалить Частицы |
| Название EN | Delete Particles |
| Ключ строки / layout | `brick_delete_particles_title` / `brick_delete_particles` |
| Параметры | Formula: PARTICLE_ID (`brick_particle_id_edit`) |
| Дефолт в палитре | ("particles") |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/DeleteParticlesBrick.java:9` |
| Класс экшена | DeleteParticlesAction (catroid/src/main/java/org/catrobat/catroid/content/actions/DeleteParticlesAction.kt:8) — через createDeleteParticlesAction |
| Описание | Удаляет частицы EN: Deletes particles Код: создаёт `DeleteParticlesAction` в `addActionToSequence` через `createDeleteParticlesAction`. |
