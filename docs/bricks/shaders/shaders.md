# shaders / shaders

CAT `category_shaders` FAC `setupShadersCategoryList` LINES 2428-2443
docs for bricks, order from CategoryBricksFactory

## Готовые эффекты / Presets

### 1. Эффект ЭЛТ-экрана Интенсивность %
RULAB Эффект ЭЛТ-экрана Интенсивность %
ENLAB CRT screen effect Intensity %
KEYS `brick_crt_shader, crt_shader_intensity`
PARAMS formula VALUE:brick_crt_intensity; paldefaults 80.0
BRICKF `catroid/src/main/java/org/catrobat/catroid/content/bricks/CrtScreenShaderBrick.java:30`
ACT `catroid/src/main/java/org/catrobat/catroid/content/actions/CrtScreenShaderAction.kt:35 via createCrtScreenShaderAction`
DESC Creates CrtScreenShaderAction, SpriteDissolveShaderAction, SpriteWaveShaderAction (no BrickInfo, from code)

### 2. Растворение спрайта Прогресс %
RULAB Растворение спрайта Прогресс %
ENLAB Sprite dissolve (burn) Progress %
KEYS `brick_sprite_dissolve_shader, dissolve_shader_progress`
PARAMS formula VALUE:brick_sprite_dissolve_progress; paldefaults 0.0
BRICKF `catroid/src/main/java/org/catrobat/catroid/content/bricks/SpriteDissolveShaderBrick.java:30`
ACT `catroid/src/main/java/org/catrobat/catroid/content/actions/SpriteDissolveShaderAction.kt:34 via createSpriteDissolveShaderAction`
DESC Creates SpriteDissolveShaderAction, SpriteWaveShaderAction, Set3DSoundMaxDistanceAction (no BrickInfo, from code)

### 3. Волны спрайта Амплитуда % Фаза (град)
RULAB Волны спрайта Амплитуда % Фаза (град)
ENLAB Sprite wave Amplitude % Phase (deg)
KEYS `brick_sprite_wave_shader, wave_shader_amplitude, wave_phase_degrees`
PARAMS formula VALUE:brick_sprite_wave_amplitude; formula TEXT:brick_sprite_wave_phase; paldefaults 30.0, 0.0
BRICKF `catroid/src/main/java/org/catrobat/catroid/content/bricks/SpriteWaveShaderBrick.java:30`
ACT `catroid/src/main/java/org/catrobat/catroid/content/actions/SpriteWaveShaderAction.kt:34 via createSpriteWaveShaderAction`
DESC Creates SpriteWaveShaderAction, Set3DSoundMaxDistanceAction, SetEmissiveAction (no BrickInfo, from code)

### 4. Нормализовать объект
RULAB Нормализовать объект
ENLAB Normalize object
KEYS `brick_normalize_sprite_shader`
PARAMS noparams
BRICKF `catroid/src/main/java/org/catrobat/catroid/content/bricks/NormalizeSpriteShaderBrick.java:29`
ACT `catroid/src/main/java/org/catrobat/catroid/content/actions/NormalizeSpriteShaderAction.kt:28 via createNormalizeSpriteShaderAction`
DESC Creates NormalizeSpriteShaderAction, DarkGrayscaleShaderAction, InvertColorsShaderAction (no BrickInfo, from code)

### 5. Сделать тёмно-серым объект
RULAB Сделать тёмно-серым объект
ENLAB Set dark grayscale
KEYS `brick_dark_grayscale_shader`
PARAMS noparams
BRICKF `catroid/src/main/java/org/catrobat/catroid/content/bricks/DarkGrayscaleShaderBrick.java:29`
ACT `catroid/src/main/java/org/catrobat/catroid/content/actions/DarkGrayscaleShaderAction.kt:32 via createDarkGrayscaleShaderAction`
DESC Creates DarkGrayscaleShaderAction, InvertColorsShaderAction, TintShaderAction (no BrickInfo, from code)

### 6. эффект пикселизации
RULAB эффект пикселизации
ENLAB set filter pixelate
KEYS `brick_set_filter_pixelate`
PARAMS formula SIZE:brick_set_filter_pixelate_edit_text; paldefaults Formula(8.0)
BRICKF `catroid/src/main/java/org/catrobat/catroid/content/bricks/SetFilterPixelateBrick.java:30`
ACT `catroid/src/main/java/org/catrobat/catroid/content/actions/SetFilterPixelateAction.kt:33 via createSetFilterPixelateAction`
DESC Применяет эффект пикселизации к спрайту / Applies a pixelate effect to the sprite

### 7. Инвертировать цвета объекта
RULAB Инвертировать цвета объекта
ENLAB Invert colors
KEYS `brick_invert_colors_shader`
PARAMS noparams
BRICKF `catroid/src/main/java/org/catrobat/catroid/content/bricks/InvertColorsShaderBrick.java:29`
ACT `catroid/src/main/java/org/catrobat/catroid/content/actions/InvertColorsShaderAction.kt:32 via createInvertColorsShaderAction`
DESC Creates InvertColorsShaderAction, TintShaderAction, SetFilterPixelateAction (no BrickInfo, from code)

### 8. Окрасить объект Красный Зелёный Синий Интенсивность %
RULAB Окрасить объект Красный Зелёный Синий Интенсивность %
ENLAB Tint object Red Green Blue Intensity %
KEYS `brick_tint_shader, brick_tint_shader_red, brick_tint_shader_green, brick_tint_shader_blue, brick_tint_shader_amount`
PARAMS formula RED:brick_tint_shader_red; formula GREEN:brick_tint_shader_green; formula BLUE:brick_tint_shader_blue; formula INTENSITY:brick_tint_shader_amount; paldefaults Formula(255.0), Formula(0.0), Formula(0.0), Formula(50.0)
BRICKF `catroid/src/main/java/org/catrobat/catroid/content/bricks/TintShaderBrick.java:30`
ACT `catroid/src/main/java/org/catrobat/catroid/content/actions/TintShaderAction.kt:33 via createTintShaderAction`
DESC Creates TintShaderAction, SetFilterPixelateAction, SetFilterBlurAction (no BrickInfo, from code)

### 9. эффект размытия
RULAB эффект размытия
ENLAB set filter blur
KEYS `brick_set_filter_blur`
PARAMS formula INTENSITY:brick_set_filter_blur_edit_text; paldefaults Formula(3.0)
BRICKF `catroid/src/main/java/org/catrobat/catroid/content/bricks/SetFilterBlurBrick.java:30`
ACT `catroid/src/main/java/org/catrobat/catroid/content/actions/SetFilterBlurAction.kt:33 via createSetFilterBlurAction`
DESC Применяет эффект размытия к спрайту / Applies a blur effect to the sprite

### 10. эффект сепии
RULAB эффект сепии
ENLAB set filter sepia
KEYS `brick_set_filter_sepia`
PARAMS formula VALUE_1:brick_set_filter_sepia_edit_text; paldefaults Formula(0.8)
BRICKF `catroid/src/main/java/org/catrobat/catroid/content/bricks/SetFilterSepiaBrick.java:30`
ACT `catroid/src/main/java/org/catrobat/catroid/content/actions/SetFilterSepiaAction.kt:33 via createSetFilterSepiaAction`
DESC Применяет эффект сепии к спрайту / Applies a sepia effect to the sprite

### 11. Запомнить цвет образа
RULAB Запомнить цвет образа
ENLAB Save look color
KEYS `brick_capture_look_color_profile`
PARAMS noparams
BRICKF `catroid/src/main/java/org/catrobat/catroid/content/bricks/CaptureLookColorProfileBrick.java:10`
ACT `catroid/src/main/java/org/catrobat/catroid/content/actions/CaptureLookColorProfileAction.java:9 via createCaptureLookColorProfileAction`
DESC Creates CaptureLookColorProfileAction, ApplyLookColorProfileAction, SetLookByIndexAction (no BrickInfo, from code)

### 12. Окрасить образ сохранённым цветом
RULAB Окрасить образ сохранённым цветом
ENLAB Colorize look from saved color
KEYS `brick_apply_look_color_profile`
PARAMS spinner `brick_apply_look_color_profile_spinner` gen LookData; src looks
BRICKF `catroid/src/main/java/org/catrobat/catroid/content/bricks/ApplyLookColorProfileBrick.java:19`
ACT `catroid/src/main/java/org/catrobat/catroid/content/actions/ApplyLookColorProfileAction.java:11 via createApplyLookColorProfileAction`
DESC Creates ApplyLookColorProfileAction, SetLookByIndexAction, SetLookByNameAction (no BrickInfo, from code)
