# Fast 2D / Fast 2D

Категория `fast2d`: RU «Fast 2D», EN «Fast 2D».

Источник состава и порядка: `catroid/src/main/java/org/catrobat/catroid/ui/fragment/CategoryBricksFactory.kt:3364`, метод `setupFast2dCategoryList`.

Правила документа:

- Порядок бриков строго по фабрике; подгруппы — `SubCategoryHeaderBrick` из того же метода.
- Названия RU/EN — по первому `@string` главного layout (`getViewResource`) из `catroid/src/main/res/values/strings.xml` и `values-ru/strings.xml`; у бриков без текстовой подписи — значения `R.array.*` из `arrays.xml`.
- Параметры — `addAllowedBrickField(BrickField.*)` + спиннер по факту кода брика.
- Экшен — фактический класс из `content/actions` через `ActionFactory.create*` (резолв по `ActionFactory.java`); у ScriptBrick экшена нет.
- Описание — 1–2 строки строго по `BrickInfo.java` + факт кода; домыслов нет.

Всего бриков в этом файле: 17 (заголовки подгрупп не считаются).

### 1. `Fast2DCreateBrick`

| Поле | Значение |
|---|---|
| Название RU | Создать объект |
| Название EN | Create entity |
| Ключ строки / layout | `fast2d_create` / `brick_fast2d_create` |
| Параметры | Formula: NAME (`brick_fast2d_create_edit_id`) |
| Дефолт в палитре | ("object") |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/Fast2DCreateBrick.java:8` |
| Класс экшена | Fast2DCreateAction (catroid/src/main/java/org/catrobat/catroid/content/actions/Fast2DCreateAction.kt:8) — через createFast2DCreateAction |
| Описание | Создает объект (сущность) в новой системе Fast 2D рендера по указанному ID. EN: Creates an entity in the new Fast 2D rendering system with the given ID. Код: создаёт `Fast2DCreateAction` в `addActionToSequence` через `createFast2DCreateAction`. |

### 2. `Fast2DDeleteBrick`

| Поле | Значение |
|---|---|
| Название RU | Удалить объект |
| Название EN | Delete entity |
| Ключ строки / layout | `fast2d_delete` / `brick_fast2d_delete` |
| Параметры | Formula: NAME (`brick_fast2d_delete_id`) |
| Дефолт в палитре | ("object") |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/Fast2DDeleteBrick.java:8` |
| Класс экшена | Fast2DDeleteAction (catroid/src/main/java/org/catrobat/catroid/content/actions/Fast2DDeleteAction.kt:8) — через createFast2DDeleteAction |
| Описание | Удаляет объект (сущность) из системы Fast 2D по указанному ID. EN: Deletes an entity from the Fast 2D system by the given ID. Код: создаёт `Fast2DDeleteAction` в `addActionToSequence` через `createFast2DDeleteAction`. |

### 3. `Fast2DSetPositionBrick`

| Поле | Значение |
|---|---|
| Название RU | Задать позицию |
| Название EN | Set position |
| Ключ строки / layout | `fast2d_set_position` / `brick_fast2d_set_position` |
| Параметры | Formula: NAME (`brick_fast2d_set_pos_edit_id`); Formula: X_POSITION (`brick_fast2d_set_pos_edit_x`); Formula: Y_POSITION (`brick_fast2d_set_pos_edit_y`) |
| Дефолт в палитре | ("object", 100.0, 200.0) |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/Fast2DSetPositionBrick.java:8` |
| Класс экшена | Fast2DSetPositionAction (catroid/src/main/java/org/catrobat/catroid/content/actions/Fast2DSetPositionAction.kt:8) — через createFast2DSetPositionAction |
| Описание | Задает позицию (X, Y) объекту Fast 2D по его ID. EN: Sets the X and Y position of a Fast 2D entity by its ID. Код: создаёт `Fast2DSetPositionAction` в `addActionToSequence` через `createFast2DSetPositionAction`. |

### 4. `Fast2DSetRotationBrick`

| Поле | Значение |
|---|---|
| Название RU | Повернуть |
| Название EN | Rotate |
| Ключ строки / layout | `fast2d_set_rotation` / `brick_fast2d_set_rotation` |
| Параметры | Formula: NAME (`brick_fast2d_set_rotation_id`); Formula: ROTATION (`brick_fast2d_set_rotation_angle`) |
| Дефолт в палитре | ("object", 90.0) |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/Fast2DSetRotationBrick.java:8` |
| Класс экшена | Fast2DSetRotationAction (catroid/src/main/java/org/catrobat/catroid/content/actions/Fast2DSetRotationAction.kt:8) — через createFast2DSetRotationAction |
| Описание | Поворачивает объект Fast 2D на указанный угол (в градусах) по его ID. EN: Rotates a Fast 2D entity to the given angle (in degrees) by its ID. Код: создаёт `Fast2DSetRotationAction` в `addActionToSequence` через `createFast2DSetRotationAction`. |

### 5. `Fast2DSetScaleBrick`

| Поле | Значение |
|---|---|
| Название RU | Размер |
| Название EN | Size |
| Ключ строки / layout | `fast2d_set_scale` / `brick_fast2d_set_scale` |
| Параметры | Formula: NAME (`brick_fast2d_set_scale_id`); Formula: X_SCALE (`brick_fast2d_set_scale_x`); Formula: Y_SCALE (`brick_fast2d_set_scale_y`) |
| Дефолт в палитре | ("object", 200.0, 15.0) |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/Fast2DSetScaleBrick.java:8` |
| Класс экшена | Fast2DSetScaleAction (catroid/src/main/java/org/catrobat/catroid/content/actions/Fast2DSetScaleAction.kt:8) — через createFast2DSetScaleAction |
| Описание | Задает размер (ширину и высоту) объекта Fast 2D по его ID. EN: Sets the scale (width and height) of a Fast 2D entity by its ID. Код: создаёт `Fast2DSetScaleAction` в `addActionToSequence` через `createFast2DSetScaleAction`. |

### 6. `Fast2DSetVelocityBrick`

| Поле | Значение |
|---|---|
| Название RU | Скорость (Простая) |
| Название EN | Velocity (simple) |
| Ключ строки / layout | `fast2d_set_velocity` / `brick_fast2d_set_velocity` |
| Параметры | Formula: NAME (`brick_fast2d_vel_id`); Formula: X_POSITION (`brick_fast2d_vel_x`); Formula: Y_POSITION (`brick_fast2d_vel_y`) |
| Дефолт в палитре | ("object", 10.0, 5.0) |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/Fast2DSetVelocityBrick.java:8` |
| Класс экшена | Fast2DSetVelocityAction (catroid/src/main/java/org/catrobat/catroid/content/actions/Fast2DSetVelocityAction.kt:8) — через createFast2DSetVelocityAction |
| Описание | Задает скорость (Vx, Vy) объекту Fast 2D по его ID (простая, без физики). EN: Sets the velocity (Vx, Vy) of a Fast 2D entity by its ID (simple, non-physics). Код: создаёт `Fast2DSetVelocityAction` в `addActionToSequence` через `createFast2DSetVelocityAction`. |

### 7. `Fast2DSetAngularVelocityBrick`

| Поле | Значение |
|---|---|
| Название RU | Скорость вращения |
| Название EN | Rotation speed |
| Ключ строки / layout | `fast2d_set_angular_velocity` / `brick_fast2d_set_angular_velocity` |
| Параметры | Formula: NAME (`brick_fast2d_ang_vel_id`); Formula: ROTATION (`brick_fast2d_ang_vel_v`) |
| Дефолт в палитре | ("object", 10.0) |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/Fast2DSetAngularVelocityBrick.java:8` |
| Класс экшена | Fast2DSetAngularVelocityAction (catroid/src/main/java/org/catrobat/catroid/content/actions/Fast2DSetAngularVelocityAction.kt:8) — через createFast2DSetAngularVelocityAction |
| Описание | Задает скорость вращения (град/сек) объекту Fast 2D по его ID. EN: Sets the rotation speed (deg/s) of a Fast 2D entity by its ID. Код: создаёт `Fast2DSetAngularVelocityAction` в `addActionToSequence` через `createFast2DSetAngularVelocityAction`. |

### 8. `Fast2DSetTextureBrick`

| Поле | Значение |
|---|---|
| Название RU | Текстура |
| Название EN | Texture |
| Ключ строки / layout | `fast2d_set_texture` / `brick_fast2d_set_texture` |
| Параметры | Formula: NAME (`brick_fast2d_set_texture_id`); Formula: STRING (`brick_fast2d_set_texture_file`) |
| Дефолт в палитре | ("object", "image.png") |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/Fast2DSetTextureBrick.java:8` |
| Класс экшена | Fast2DSetTextureAction (catroid/src/main/java/org/catrobat/catroid/content/actions/Fast2DSetTextureAction.kt:8) — через createFast2DSetTextureAction |
| Описание | Задает текстуру объекту Fast 2D из файла проекта по его ID. EN: Sets the texture of a Fast 2D entity from a project file by its ID. Код: создаёт `Fast2DSetTextureAction` в `addActionToSequence` через `createFast2DSetTextureAction`. |

### 9. `Fast2DSetColorBrick`

| Поле | Значение |
|---|---|
| Название RU | Цвет и Альфа |
| Название EN | Color and Alpha |
| Ключ строки / layout | `fast2d_set_color` / `brick_fast2d_set_color` |
| Параметры | Formula: NAME (`brick_fast2d_color_id`); Formula: X_POSITION (`brick_fast2d_color_r`); Formula: Y_POSITION (`brick_fast2d_color_g`); Formula: ROTATION (`brick_fast2d_color_b`); Formula: SIZE (`brick_fast2d_color_a`) |
| Дефолт в палитре | ("object", 255.0, 0.0, 0.0, 120.0) |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/Fast2DSetColorBrick.java:8` |
| Класс экшена | Fast2DSetColorAction (catroid/src/main/java/org/catrobat/catroid/content/actions/Fast2DSetColorAction.kt:8) — через createFast2DSetColorAction |
| Описание | Задает цвет и прозрачность (RGBA, значения 0-255) объекту Fast 2D по его ID. EN: Sets the color and alpha (RGBA, values 0-255) of a Fast 2D entity by its ID. Код: создаёт `Fast2DSetColorAction` в `addActionToSequence` через `createFast2DSetColorAction`. |

### 10. `Fast2DSetCameraBrick`

| Поле | Значение |
|---|---|
| Название RU | Камера |
| Название EN | Camera |
| Ключ строки / layout | `fast2d_set_camera` / `brick_fast2d_set_camera` |
| Параметры | Formula: X_POSITION (`brick_fast2d_cam_edit_x`); Formula: Y_POSITION (`brick_fast2d_cam_edit_y`); Formula: SIZE (`brick_fast2d_cam_edit_zoom`) |
| Дефолт в палитре | (100.0, 200.0, 2.0) |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/Fast2DSetCameraBrick.java:8` |
| Класс экшена | Fast2DSetCameraAction (catroid/src/main/java/org/catrobat/catroid/content/actions/Fast2DSetCameraAction.kt:8) — через createFast2DSetCameraAction |
| Описание | Задает позицию камеры (X, Y) и приближение (Zoom) в системе Fast 2D. EN: Sets the camera position (X, Y) and zoom in the Fast 2D system. Код: создаёт `Fast2DSetCameraAction` в `addActionToSequence` через `createFast2DSetCameraAction`. |

### 11. `Fast2DMakePhysicsBrick`

| Поле | Значение |
|---|---|
| Название RU | Физическое тело |
| Название EN | Physics body |
| Ключ строки / layout | `fast2d_make_physics` / `brick_fast2d_make_physics` |
| Параметры | Formula: NAME (`brick_fast2d_phys_id`); Formula: X_POSITION (`brick_fast2d_phys_dyn`); Formula: STRING (`brick_fast2d_phys_shape`); Formula: Y_POSITION (`brick_fast2d_phys_den`); Formula: ROTATION (`brick_fast2d_phys_fric`); Formula: SIZE (`brick_fast2d_phys_bounce`) |
| Дефолт в палитре | ("object", 1.0, "CIRCLE", 10.0, 0.7, 0.2) |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/Fast2DMakePhysicsBrick.java:8` |
| Класс экшена | Fast2DMakePhysicsAction (catroid/src/main/java/org/catrobat/catroid/content/actions/Fast2DMakePhysicsAction.kt:8) — через createFast2DMakePhysicsAction |
| Описание | Создает физическое тело для объекта Fast 2D: задает динамичность, форму (BOX/CIRCLE), массу, трение и прыгучесть по ID. EN: Creates a physics body for a Fast 2D entity: dynamic flag, shape (BOX/CIRCLE), mass, friction and bounce by ID. Код: создаёт `Fast2DMakePhysicsAction` в `addActionToSequence` через `createFast2DMakePhysicsAction`. |

### 12. `Fast2DSetCollisionFilterBrick`

| Поле | Значение |
|---|---|
| Название RU | Фильтр коллизий |
| Название EN | Collision filter |
| Ключ строки / layout | `fast2d_set_collision_filter` / `brick_fast2d_set_collision_filter` |
| Параметры | Formula: NAME (`brick_fast2d_col_filter_id`); Formula: X_POSITION (`brick_fast2d_col_filter_sensor`); Formula: Y_POSITION (`brick_fast2d_col_filter_group`) |
| Дефолт в палитре | ("object", 0.0, -1.0) |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/Fast2DSetCollisionFilterBrick.java:8` |
| Класс экшена | Fast2DSetCollisionFilterAction (catroid/src/main/java/org/catrobat/catroid/content/actions/Fast2DSetCollisionFilterAction.kt:8) — через createFast2DSetCollisionFilterAction |
| Описание | Настраивает фильтр коллизий объекта Fast 2D: сенсор (1/0) и группу (-1, 0, 1) по ID. EN: Configures the collision filter of a Fast 2D entity: sensor (1/0) and group (-1, 0, 1) by ID. Код: создаёт `Fast2DSetCollisionFilterAction` в `addActionToSequence` через `createFast2DSetCollisionFilterAction`. |

### 13. `Fast2DApplyForceBrick`

| Поле | Значение |
|---|---|
| Название RU | Применить силу |
| Название EN | Apply force |
| Ключ строки / layout | `fast2d_apply_force` / `brick_fast2d_apply_force` |
| Параметры | Formula: NAME (`brick_fast2d_force_id`); Formula: X_POSITION (`brick_fast2d_force_x`); Formula: Y_POSITION (`brick_fast2d_force_y`) |
| Дефолт в палитре | ("object", 100.0, 300.0) |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/Fast2DApplyForceBrick.java:8` |
| Класс экшена | Fast2DApplyForceAction (catroid/src/main/java/org/catrobat/catroid/content/actions/Fast2DApplyForceAction.kt:7) — через createFast2DApplyForceAction |
| Описание | Применяет силу (Fx, Fy) к физическому телу объекта Fast 2D по его ID. EN: Applies a force (Fx, Fy) to a Fast 2D entity's physics body by its ID. Код: создаёт `Fast2DApplyForceAction` в `addActionToSequence` через `createFast2DApplyForceAction`. |

### 14. `Fast2DApplyImpulseBrick`

| Поле | Значение |
|---|---|
| Название RU | Применить импульс |
| Название EN | Apply impulse |
| Ключ строки / layout | `fast2d_apply_impulse` / `brick_fast2d_apply_impulse` |
| Параметры | Formula: NAME (`brick_fast2d_impulse_id`); Formula: X_POSITION (`brick_fast2d_impulse_x`); Formula: Y_POSITION (`brick_fast2d_impulse_y`) |
| Дефолт в палитре | ("object", 10.0, 5.0) |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/Fast2DApplyImpulseBrick.java:8` |
| Класс экшена | Fast2DApplyImpulseAction (catroid/src/main/java/org/catrobat/catroid/content/actions/Fast2DApplyImpulseAction.kt:7) — через createFast2DApplyImpulseAction |
| Описание | Применяет импульс (Ix, Iy) к физическому телу объекта Fast 2D по его ID. EN: Applies an impulse (Ix, Iy) to a Fast 2D entity's physics body by its ID. Код: создаёт `Fast2DApplyImpulseAction` в `addActionToSequence` через `createFast2DApplyImpulseAction`. |

### 15. `Fast2DSetPhysicsVelocityBrick`

| Поле | Значение |
|---|---|
| Название RU | Скорость (Физика) |
| Название EN | Velocity (Physics) |
| Ключ строки / layout | `fast2d_set_physics_velocity` / `brick_fast2d_set_phys_velocity` |
| Параметры | Formula: NAME (`brick_fast2d_phys_vel_id`); Formula: X_POSITION (`brick_fast2d_phys_vel_x`); Formula: Y_POSITION (`brick_fast2d_phys_vel_y`) |
| Дефолт в палитре | ("object", 10.0, 2.0) |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/Fast2DSetPhysicsVelocityBrick.java:8` |
| Класс экшена | Fast2DSetPhysicsVelocityAction (catroid/src/main/java/org/catrobat/catroid/content/actions/Fast2DSetPhysicsVelocityAction.kt:7) — через createFast2DSetPhysicsVelocityAction |
| Описание | Задает скорость (Vx, Vy) физическому телу объекта Fast 2D по его ID. EN: Sets the velocity (Vx, Vy) of a Fast 2D entity's physics body by its ID. Код: создаёт `Fast2DSetPhysicsVelocityAction` в `addActionToSequence` через `createFast2DSetPhysicsVelocityAction`. |

### 16. `Fast2DSetGravityBrick`

| Поле | Значение |
|---|---|
| Название RU | Установить гравитацию |
| Название EN | Set gravity |
| Ключ строки / layout | `fast2d_set_gravity` / `brick_fast2d_set_gravity` |
| Параметры | Formula: VALUE_1 (`brick_fast2d_set_gravity_x`); Formula: VALUE_2 (`brick_fast2d_set_gravity_y`) |
| Дефолт в палитре | (0f, -9.8f) |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/Fast2DSetGravityBrick.java:8` |
| Класс экшена | Fast2DSetGravityAction (catroid/src/main/java/org/catrobat/catroid/content/actions/Fast2DSetGravityAction.kt:8) — через createFast2DSetGravityAction |
| Описание | Задает вектор гравитации (X, Y) в Fast2D-сцене. EN: Sets the gravity vector (X, Y) in the Fast2D scene. Код: создаёт `Fast2DSetGravityAction` в `addActionToSequence` через `createFast2DSetGravityAction`. |

### 17. `Fast2DSetZIndexBrick`

| Поле | Значение |
|---|---|
| Название RU | Слой (Z) |
| Название EN | Layer (Z) |
| Ключ строки / layout | `fast2d_set_z` / `brick_fast2d_set_z` |
| Параметры | Formula: NAME (`brick_fast2d_set_z_id`); Formula: VIBRATE_DURATION (`brick_fast2d_set_z_value`) |
| Дефолт в палитре | ("object", 0.0) |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/Fast2DSetZIndexBrick.java:8` |
| Класс экшена | Fast2DSetZIndexAction (catroid/src/main/java/org/catrobat/catroid/content/actions/Fast2DSetZIndexAction.kt:8) — через createFast2DSetZIndexAction |
| Описание | Задает слой (Z-индекс) объекта Fast 2D, определяющий порядок отрисовки. EN: Sets the layer (Z-index) of a Fast 2D entity, which determines draw order. Код: создаёт `Fast2DSetZIndexAction` в `addActionToSequence` через `createFast2DSetZIndexAction`. |
