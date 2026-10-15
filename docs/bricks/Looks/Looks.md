# Looks / Образы

CAT `category_looks` FAC `setupLooksCategoryList` LINES 2257-2421
docs for bricks, order from CategoryBricksFactory

## Образы и фоны / Looks & Backgrounds

### 1. Задать образ
RULAB Задать образ
ENLAB Switch to look
KEYS `brick_set_look`
PARAMS spinner `brick_set_look_spinner` gen LookData; src looks
BRICKF `catroid/src/main/java/org/catrobat/catroid/content/bricks/SetLookBrick.java:46`
ACT `catroid/src/main/java/org/catrobat/catroid/content/actions/SetLookAction.kt:30 via createSetLookAction`
DESC Изменяет внешний вид спрайта на выбранный из списка. / Changes the sprite's appearance to the selected look from the list.
COND `if (!isBackgroundSprite) {`

### 2. Задать образ под номером
RULAB Задать образ под номером
ENLAB Switch to look with number
KEYS `brick_set_look_by_index`
PARAMS formula LOOK_INDEX:brick_set_look_by_index_edit_text; paldefaults BrickValues.SET_LOOK_BY_INDEX
BRICKF `catroid/src/main/java/org/catrobat/catroid/content/bricks/SetLookByIndexBrick.java:31`
ACT `catroid/src/main/java/org/catrobat/catroid/content/actions/SetLookByIndexAction.kt:32 via createSetLookByIndexAction`
DESC Задает образ спрайта под индексом / Sets the look of the sprite by index
COND `if (!isBackgroundSprite) {`

### 3. Задать образ по имени
RULAB Задать образ по имени
ENLAB Switch to look by name
KEYS `brick_set_look_by_name`
PARAMS formula NAME:brick_set_look_by_name_edit_text; paldefaults context.getString(R.string.brick_set_look_by_name_default)
BRICKF `catroid/src/main/java/org/catrobat/catroid/content/bricks/SetLookByNameBrick.java:30`
ACT `catroid/src/main/java/org/catrobat/catroid/content/actions/SetLookByNameAction.kt:32 via createSetLookByNameAction`
DESC Creates SetLookByNameAction, SetLookAction, SetLookByIndexAction (no BrickInfo, from code)
COND `if (!isBackgroundSprite) {`

### 4. Следующий фон
RULAB Следующий фон
ENLAB Next background
KEYS `brick_next_look, brick_next_background`
PARAMS noparams
BRICKF `catroid/src/main/java/org/catrobat/catroid/content/bricks/NextLookBrick.kt:33`
ACT `catroid/src/main/java/org/catrobat/catroid/content/actions/SetNextLookAction.kt:25 via createSetNextLookAction`
DESC Задает следующий образ / Sets the next look
COND `if (!isBackgroundSprite) {`

### 5. Предыдущий фон
RULAB Предыдущий фон
ENLAB Previous background
KEYS `brick_previous_look, brick_previous_background`
PARAMS noparams
BRICKF `catroid/src/main/java/org/catrobat/catroid/content/bricks/PreviousLookBrick.kt:33`
ACT `catroid/src/main/java/org/catrobat/catroid/content/actions/SetPreviousLookAction.kt:25 via createSetPreviousLookAction`
DESC Задает предыдущий образ / Sets the previous look
COND `if (!isBackgroundSprite) {`

### 6. Когда фон изменится на
RULAB Когда фон изменится на
ENLAB When background changes to
KEYS `brick_when_background`
PARAMS spinner `brick_when_background_spinner` gen LookData; src looks
BRICKF `catroid/src/main/java/org/catrobat/catroid/content/bricks/WhenBackgroundChangesBrick.java:49`
ACT `catroid/src/main/java/org/catrobat/catroid/content/actions/SetLookAction.kt:30 via createSetLookAction`
DESC Вызывается, когда фон изменит образ на выбранный. / Called when the background changes its look to the selected one.
COND `if (!isBackgroundSprite) {`

### 7. Задать фон
RULAB Задать фон
ENLAB Set background
KEYS `brick_set_background`
PARAMS spinner `brick_set_background_spinner` gen LookData; src looks
BRICKF `catroid/src/main/java/org/catrobat/catroid/content/bricks/SetBackgroundBrick.java:47`
ACT `catroid/src/main/java/org/catrobat/catroid/content/actions/SetLookAction.kt:30 via createSetBackgroundAction`
DESC Задает образ фону / Sets the background image
COND `if (!isBackgroundSprite) {`

### 8. Задать фон под номером
RULAB Задать фон под номером
ENLAB Set background to number
KEYS `brick_set_background_by_index`
PARAMS formula BACKGROUND_INDEX:brick_set_background_by_index_edit_text; paldefaults BrickValues.SET_LOOK_BY_INDEX
BRICKF `catroid/src/main/java/org/catrobat/catroid/content/bricks/SetBackgroundByIndexBrick.kt:31`
ACT `catroid/src/main/java/org/catrobat/catroid/content/actions/SetLookByIndexAction.kt:32 via createSetBackgroundByIndexAction`
DESC Задает образ фону по номером / Sets the background image by index

### 9. Задать фон и ждать
RULAB Задать фон и ждать
ENLAB Set background and wait
KEYS `brick_set_background, brick_set_background_and_wait`
PARAMS spinner `brick_set_background_spinner` gen LookData; src looks
BRICKF `catroid/src/main/java/org/catrobat/catroid/content/bricks/SetBackgroundAndWaitBrick.java:34`
ACT `catroid/src/main/java/org/catrobat/catroid/content/actions/SetLookAction.kt:30 via createSetBackgroundAction`
DESC Задает образ фону и ждет окончания всех событий, слушающих это / Sets the background image and waits for all events listening to it to finish

### 10. Задать фон под номером и ждать
RULAB Задать фон под номером и ждать
ENLAB Set background to number and wait
KEYS `brick_set_background_by_index, brick_and_wait`
PARAMS paldefaults BrickValues.SET_LOOK_BY_INDEX
BRICKF `catroid/src/main/java/org/catrobat/catroid/content/bricks/SetBackgroundByIndexAndWaitBrick.kt:31`
ACT `catroid/src/main/java/org/catrobat/catroid/content/actions/SetLookByIndexAction.kt:32 via createSetBackgroundByIndexAction`
DESC Задает образ фону по номеру и ждет окончания всех событий, слушающих это / Sets the background image by index and waits for all events listening to it to finish

### 11. Нарисовать новый фон
RULAB Нарисовать новый фон
ENLAB Paint new background
KEYS `brick_paint_new_look, brick_paint_new_background`
PARAMS paldefaults context.getString(R.string.brick_paint_new_look_name)
BRICKF `catroid/src/main/java/org/catrobat/catroid/content/bricks/PaintNewLookBrick.kt:36`
ACT `catroid/src/main/java/org/catrobat/catroid/content/actions/SetNextLookAction.kt:25 via createSetNextLookAction`; `catroid/src/main/java/org/catrobat/catroid/content/actions/PaintNewLookAction.kt:42 via createPaintNewLookAction`
DESC Открывает Pocket Paint, чтобы пользователь нарисовал образ / Opens Pocket Paint for the user to draw a look

### 12. Изменить фон
RULAB Изменить фон
ENLAB Edit background
KEYS `brick_edit_look, brick_edit_background`
PARAMS noparams
BRICKF `catroid/src/main/java/org/catrobat/catroid/content/bricks/EditLookBrick.kt:35`
ACT `catroid/src/main/java/org/catrobat/catroid/content/actions/SetNextLookAction.kt:25 via createSetNextLookAction`; `catroid/src/main/java/org/catrobat/catroid/content/actions/EditLookAction.kt:39 via createEditLookAction`
DESC Открывает Pocket Paint, чтобы пользователь изменил текущий образ / Opens Pocket Paint for the user to edit the current look

### 13. Скопировать фон и назвать как
RULAB Скопировать фон и назвать как
ENLAB Copy background and name it
KEYS `brick_copy_look, brick_copy_background`
PARAMS paldefaults context.getString(R.string.brick_copy_look_name)
BRICKF `catroid/src/main/java/org/catrobat/catroid/content/bricks/CopyLookBrick.kt:36`
ACT `catroid/src/main/java/org/catrobat/catroid/content/actions/SetNextLookAction.kt:25 via createSetNextLookAction`; `catroid/src/main/java/org/catrobat/catroid/content/actions/CopyLookAction.kt:43 via createCopyLookAction`
DESC Копирует образ / Copies a look

### 14. Удалить фон
RULAB Удалить фон
ENLAB Delete background
KEYS `brick_delete_look, brick_delete_background`
PARAMS noparams
BRICKF `catroid/src/main/java/org/catrobat/catroid/content/bricks/DeleteLookBrick.kt:34`
ACT `catroid/src/main/java/org/catrobat/catroid/content/actions/DeleteLookAction.kt:33 via createDeleteLookAction`
DESC Удаляет образ ВАЖНО: навсегда / Deletes a look IMPORTANT: permanently

## Видимость и размер / Visibility & Size

### 15. Показать
RULAB Показать
ENLAB Show
KEYS `brick_show`
PARAMS noparams
BRICKF `catroid/src/main/java/org/catrobat/catroid/content/bricks/ShowBrick.java:29`
ACT `catroid/src/main/java/org/catrobat/catroid/content/actions/SetVisibleAction.java:31 via createShowAction`
DESC Показывает спрайт (он снова видимый) / Shows the sprite (it becomes visible again)

### 16. Скрыть
RULAB Скрыть
ENLAB Hide
KEYS `brick_hide`
PARAMS noparams
BRICKF `catroid/src/main/java/org/catrobat/catroid/content/bricks/HideBrick.java:29`
ACT `catroid/src/main/java/org/catrobat/catroid/content/actions/SetVisibleAction.java:31 via createHideAction`
DESC Скрывает спрайт (он становится невидимым) / Hides the sprite (it becomes invisible)

### 17. Задать всем размер %
RULAB Задать всем размер %
ENLAB Set size for all to %
KEYS `brick_set_size_for_all, percent_symbol`
PARAMS formula SIZE:brick_set_size_for_all_edit_text; paldefaults BrickValues.SET_SIZE_TO
BRICKF `catroid/src/main/java/org/catrobat/catroid/content/bricks/SetSizeForAllBrick.java:30`
ACT `catroid/src/main/java/org/catrobat/catroid/content/actions/ForAllObjectsAction.kt:31 via createSetSizeForAllAction`
DESC Задает размер всем объектам сцены. / Sets the size for all sprites of the scene.

### 18. Задать всем прозрачность %
RULAB Задать всем прозрачность %
ENLAB Set transparency for all to %
KEYS `brick_set_transparency_for_all, percent_symbol`
PARAMS formula TRANSPARENCY:brick_set_transparency_for_all_edit_text; paldefaults BrickValues.SET_TRANSPARENCY
BRICKF `catroid/src/main/java/org/catrobat/catroid/content/bricks/SetTransparencyForAllBrick.java:30`
ACT `catroid/src/main/java/org/catrobat/catroid/content/actions/ForAllObjectsAction.kt:31 via createSetTransparencyForAllAction`
DESC Задает прозрачность всем объектам сцены. / Sets the transparency for all sprites of the scene.

### 19. Задать всем яркость %
RULAB Задать всем яркость %
ENLAB Set brightness for all to %
KEYS `brick_set_brightness_for_all, percent_symbol`
PARAMS formula BRIGHTNESS:brick_set_brightness_for_all_edit_text; paldefaults BrickValues.SET_BRIGHTNESS_TO
BRICKF `catroid/src/main/java/org/catrobat/catroid/content/bricks/SetBrightnessForAllBrick.java:30`
ACT `catroid/src/main/java/org/catrobat/catroid/content/actions/ForAllObjectsAction.kt:31 via createSetBrightnessForAllAction`
DESC Задает яркость всем объектам сцены (включая клоны). / Sets the brightness for all sprites of the scene (including clones).

### 20. Задать всем цвет
RULAB Задать всем цвет
ENLAB Set color for all to
KEYS `brick_set_color_for_all`
PARAMS formula COLOR:brick_set_color_for_all_edit_text; paldefaults BrickValues.SET_COLOR_TO
BRICKF `catroid/src/main/java/org/catrobat/catroid/content/bricks/SetColorForAllBrick.java:30`
ACT `catroid/src/main/java/org/catrobat/catroid/content/actions/ForAllObjectsAction.kt:31 via createSetColorForAllAction`
DESC Задает цвет всем объектам сцены (меняет HUE). / Sets the color (HUE) for all sprites of the scene.

### 21. Задать всем X
RULAB Задать всем X
ENLAB Set X for all to
KEYS `brick_set_x_for_all`
PARAMS formula X_POSITION:brick_set_x_for_all_edit_text; paldefaults Formula(BrickValues.X_POSITION)
BRICKF `catroid/src/main/java/org/catrobat/catroid/content/bricks/SetXForAllBrick.java:30`
ACT `catroid/src/main/java/org/catrobat/catroid/content/actions/ForAllObjectsAction.kt:31 via createSetXForAllAction`
DESC Задает позицию X всем объектам сцены. / Sets the X position for all sprites of the scene.

### 22. Задать всем Y
RULAB Задать всем Y
ENLAB Set Y for all to
KEYS `brick_set_y_for_all`
PARAMS formula Y_POSITION:brick_set_y_for_all_edit_text; paldefaults Formula(BrickValues.Y_POSITION)
BRICKF `catroid/src/main/java/org/catrobat/catroid/content/bricks/SetYForAllBrick.java:30`
ACT `catroid/src/main/java/org/catrobat/catroid/content/actions/ForAllObjectsAction.kt:31 via createSetYForAllAction`
DESC Задает позицию Y всем объектам сцены. / Sets the Y position for all sprites of the scene.

### 23. Направить всех в \u00B0
RULAB Направить всех в \u00B0
ENLAB Point all in direction \u00B0
KEYS `brick_point_in_direction_for_all, degree_symbol`
PARAMS formula DEGREES:brick_point_in_direction_for_all_edit_text; paldefaults BrickValues.POINT_IN_DIRECTION
BRICKF `catroid/src/main/java/org/catrobat/catroid/content/bricks/PointInDirectionForAllBrick.java:30`
ACT `catroid/src/main/java/org/catrobat/catroid/content/actions/ForAllObjectsAction.kt:31 via createPointInDirectionForAllAction`
DESC Задает направление (угол) всем объектам сцены. / Sets the direction (angle) for all sprites of the scene.

### 24. Показать все объекты
RULAB Показать все объекты
ENLAB Show all objects
KEYS `brick_show_all`
PARAMS noparams
BRICKF `catroid/src/main/java/org/catrobat/catroid/content/bricks/ShowAllBrick.java:29`
ACT `catroid/src/main/java/org/catrobat/catroid/content/actions/ForAllObjectsAction.kt:31 via createShowAllAction`
DESC Показывает все объекты сцены (они снова видимы). / Shows all sprites of the scene (they become visible again).

### 25. Скрыть все объекты
RULAB Скрыть все объекты
ENLAB Hide all objects
KEYS `brick_hide_all`
PARAMS noparams
BRICKF `catroid/src/main/java/org/catrobat/catroid/content/bricks/HideAllBrick.java:29`
ACT `catroid/src/main/java/org/catrobat/catroid/content/actions/ForAllObjectsAction.kt:31 via createHideAllAction`
DESC Скрывает все объекты сцены (они становятся невидимыми). / Hides all sprites of the scene (they become invisible).

### 26. Показать спрайты по префиксу
RULAB Показать спрайты по префиксу
ENLAB Show sprites by prefix
KEYS `brick_show_sprites_by_prefix`
PARAMS formula TEXT:brick_sprites_by_prefix_edit_text; paldefaults "show_"
BRICKF `catroid/src/main/java/org/catrobat/catroid/content/bricks/ShowSpritesByPrefixBrick.java:8`
ACT `catroid/src/main/java/org/catrobat/catroid/content/actions/SetSpritesVisibleByPrefixAction.java:12 via createSetSpritesVisibleByPrefixAction`
DESC Показывает все спрайты, чьё имя начинается с префикса. / Shows all sprites whose name starts with the prefix.

### 27. Скрыть спрайты по префиксу
RULAB Скрыть спрайты по префиксу
ENLAB Hide sprites by prefix
KEYS `brick_hide_sprites_by_prefix`
PARAMS formula TEXT:brick_sprites_by_prefix_edit_text; paldefaults "hide_"
BRICKF `catroid/src/main/java/org/catrobat/catroid/content/bricks/HideSpritesByPrefixBrick.java:8`
ACT `catroid/src/main/java/org/catrobat/catroid/content/actions/SetSpritesVisibleByPrefixAction.java:12 via createSetSpritesVisibleByPrefixAction`
DESC Скрывает все спрайты, чьё имя начинается с префикса. / Hides all sprites whose name starts with the prefix.

### 28. Задать размер %
RULAB Задать размер %
ENLAB Set size to %
KEYS `brick_set_size_to, percent_symbol`
PARAMS formula SIZE:brick_set_size_to_edit_text; paldefaults BrickValues.SET_SIZE_TO
BRICKF `catroid/src/main/java/org/catrobat/catroid/content/bricks/SetSizeToBrick.java:30`
ACT `catroid/src/main/java/org/catrobat/catroid/content/actions/SetSizeToAction.java:33 via createSetSizeToAction`
DESC Задает размер спрайта / Sets the size of the sprite

### 29. Изменить размер на
RULAB Изменить размер на
ENLAB Change size by
KEYS `brick_change_size_by`
PARAMS formula SIZE_CHANGE:brick_change_size_by_edit_text; paldefaults BrickValues.CHANGE_SIZE_BY
BRICKF `catroid/src/main/java/org/catrobat/catroid/content/bricks/ChangeSizeByNBrick.java:30`
ACT `catroid/src/main/java/org/catrobat/catroid/content/actions/ChangeSizeByNAction.java:33 via createChangeSizeByNAction`
DESC Изменяет размер спрайта / Changes the size of the sprite

### 30. Задать ширину %
RULAB Задать ширину %
ENLAB Set width to %
KEYS `brick_set_width, percent_symbol`
PARAMS formula WIDTH:brick_set_width_edit_text; paldefaults BrickValues.SET_SIZE_TO
BRICKF `catroid/src/main/java/org/catrobat/catroid/content/bricks/SetWidthBrick.java:30`
ACT `catroid/src/main/java/org/catrobat/catroid/content/actions/SetWidthAction.java:33 via createSetWidthAction`
DESC Задает ширину спрайта / Sets the width of the sprite

### 31. Задать высоту %
RULAB Задать высоту %
ENLAB Set height to %
KEYS `brick_set_height, percent_symbol`
PARAMS formula HEIGHT:brick_set_height_edit_text; paldefaults BrickValues.SET_SIZE_TO
BRICKF `catroid/src/main/java/org/catrobat/catroid/content/bricks/SetHeightBrick.java:30`
ACT `catroid/src/main/java/org/catrobat/catroid/content/actions/SetHeightAction.java:33 via createSetHeightAction`
DESC Задает высоту спрайта / Sets the height of the sprite

### 32. Изменить ширину на %
RULAB Изменить ширину на %
ENLAB Change width to %
KEYS `brick_change_width, percent_symbol`
PARAMS formula WIDTH_CHANGE:brick_change_width_edit_text; paldefaults BrickValues.CHANGE_SIZE_BY
BRICKF `catroid/src/main/java/org/catrobat/catroid/content/bricks/ChangeWidthBrick.java:30`
ACT `catroid/src/main/java/org/catrobat/catroid/content/actions/ChangeWidthAction.java:33 via createChangeWidthAction`
DESC Изменяет ширину спрайта / Changes the width of the sprite

### 33. Изменить высоту на %
RULAB Изменить высоту на %
ENLAB Change height to %
KEYS `brick_change_height, percent_symbol`
PARAMS formula HEIGHT_CHANGE:brick_change_height_edit_text; paldefaults BrickValues.CHANGE_SIZE_BY
BRICKF `catroid/src/main/java/org/catrobat/catroid/content/bricks/ChangeHeightBrick.java:30`
ACT `catroid/src/main/java/org/catrobat/catroid/content/actions/ChangeHeightAction.java:33 via createChangeHeightAction`
DESC Изменяет высоту спрайта / Changes the height of the sprite

### 34. Задать ширину % в сторону
RULAB Задать ширину % в сторону
ENLAB Set width to % towards
KEYS `brick_set_width_direction, percent_symbol, brick_direction_towards`
PARAMS formula WIDTH:brick_set_width_direction_edit_text; paldefaults BrickValues.SET_SIZE_TO; spinner `brick_set_width_direction_spinner` gen DirectionOption
BRICKF `catroid/src/main/java/org/catrobat/catroid/content/bricks/SetWidthDirectionBrick.java:41`
ACT `catroid/src/main/java/org/catrobat/catroid/content/actions/SetWidthDirectionAction.kt:33 via createSetWidthDirectionAction`
DESC Creates SetWidthDirectionAction, ChangeWidthDirectionAction, SetHeightDirectionAction (no BrickInfo, from code)

### 35. Задать высоту % в сторону
RULAB Задать высоту % в сторону
ENLAB Set height to % towards
KEYS `brick_set_height_direction, percent_symbol, brick_direction_towards`
PARAMS formula HEIGHT:brick_set_height_direction_edit_text; paldefaults BrickValues.SET_SIZE_TO; spinner `brick_set_height_direction_spinner` gen DirectionOption
BRICKF `catroid/src/main/java/org/catrobat/catroid/content/bricks/SetHeightDirectionBrick.java:41`
ACT `catroid/src/main/java/org/catrobat/catroid/content/actions/SetHeightDirectionAction.kt:33 via createSetHeightDirectionAction`
DESC Creates SetHeightDirectionAction, ChangeHeightDirectionAction, GlideToPhysicsAction (no BrickInfo, from code)

### 36. Изменить ширину на % в сторону
RULAB Изменить ширину на % в сторону
ENLAB Change width by % towards
KEYS `brick_change_width_direction, percent_symbol, brick_direction_towards`
PARAMS formula WIDTH:brick_change_width_direction_edit_text; paldefaults BrickValues.CHANGE_SIZE_BY; spinner `brick_change_width_direction_spinner` gen DirectionOption
BRICKF `catroid/src/main/java/org/catrobat/catroid/content/bricks/ChangeWidthDirectionBrick.java:41`
ACT `catroid/src/main/java/org/catrobat/catroid/content/actions/ChangeWidthDirectionAction.kt:33 via createChangeWidthDirectionAction`
DESC Creates ChangeWidthDirectionAction, SetHeightDirectionAction, ChangeHeightDirectionAction (no BrickInfo, from code)

### 37. Изменить высоту на % в сторону
RULAB Изменить высоту на % в сторону
ENLAB Change height by % towards
KEYS `brick_change_height_direction, percent_symbol, brick_direction_towards`
PARAMS formula HEIGHT:brick_change_height_direction_edit_text; paldefaults BrickValues.CHANGE_SIZE_BY; spinner `brick_change_height_direction_spinner` gen DirectionOption
BRICKF `catroid/src/main/java/org/catrobat/catroid/content/bricks/ChangeHeightDirectionBrick.java:41`
ACT `catroid/src/main/java/org/catrobat/catroid/content/actions/ChangeHeightDirectionAction.kt:33 via createChangeHeightDirectionAction`
DESC Creates ChangeHeightDirectionAction, GlideToPhysicsAction, SetVolumeToAction (no BrickInfo, from code)

### 38. Отразить образ
RULAB Отразить образ
ENLAB Flip look
KEYS `brick_flip_look`
PARAMS spinner `brick_flip_look_spinner` gen FlipOption
BRICKF `catroid/src/main/java/org/catrobat/catroid/content/bricks/FlipLookBrick.java:39`
ACT `catroid/src/main/java/org/catrobat/catroid/content/actions/FlipLookAction.kt:28 via createFlipLookAction`
DESC Creates FlipLookAction, FlashColorAction, BlinkSpriteAction (no BrickInfo, from code)

### 39. Мигать раз(а) с интервалом сек
RULAB Мигать раз(а) с интервалом сек
ENLAB Blink times with interval second(s)
KEYS `brick_blink_sprite, brick_blink_times, brick_blink_interval`
PARAMS formula TIMES_TO_REPEAT:brick_blink_sprite_times_edit_text; formula TIME:brick_blink_sprite_interval_edit_text; paldefaults 3, 0.2
BRICKF `catroid/src/main/java/org/catrobat/catroid/content/bricks/BlinkSpriteBrick.java:30`
ACT `catroid/src/main/java/org/catrobat/catroid/content/actions/BlinkSpriteAction.kt:31 via createBlinkSpriteAction`
DESC Creates BlinkSpriteAction, MoveTowardsPointAction, RotateTowardsTargetAction (no BrickInfo, from code)

## Графические эффекты / Graphic Effects

### 40. "Задать прозрачность " значение %
RULAB "Задать прозрачность " значение %
ENLAB "Set transparency" to %
KEYS `brick_set_transparency, to_label, percent_symbol`
PARAMS formula TRANSPARENCY:brick_set_transparency_to_edit_text; paldefaults BrickValues.SET_TRANSPARENCY
BRICKF `catroid/src/main/java/org/catrobat/catroid/content/bricks/SetTransparencyBrick.java:30`
ACT `catroid/src/main/java/org/catrobat/catroid/content/actions/SetTransparencyAction.java:33 via createSetTransparencyAction`
DESC Задает прозрачность спрайту / Sets the transparency of the sprite

### 41. "Изменить прозрачность " на
RULAB "Изменить прозрачность " на
ENLAB "Change transparency" by
KEYS `brick_change_ghost_effect, by_label`
PARAMS formula TRANSPARENCY_CHANGE:brick_change_transparency_edit_text; paldefaults BrickValues.CHANGE_TRANSPARENCY_EFFECT
BRICKF `catroid/src/main/java/org/catrobat/catroid/content/bricks/ChangeTransparencyByNBrick.java:30`
ACT `catroid/src/main/java/org/catrobat/catroid/content/actions/ChangeTransparencyByNAction.java:33 via createChangeTransparencyByNAction`
DESC Изменяет прозрачность спрайта / Changes the transparency of the sprite

### 42. "Задать яркость " значение %
RULAB "Задать яркость " значение %
ENLAB "Set brightness" to %
KEYS `brick_set_brightness, to_label, percent_symbol`
PARAMS formula BRIGHTNESS:brick_set_brightness_edit_text; paldefaults BrickValues.SET_BRIGHTNESS_TO
BRICKF `catroid/src/main/java/org/catrobat/catroid/content/bricks/SetBrightnessBrick.java:30`
ACT `catroid/src/main/java/org/catrobat/catroid/content/actions/SetBrightnessAction.kt:31 via createSetBrightnessAction`
DESC Задает яркость спрайта / Sets the brightness of the sprite

### 43. "Изменить яркость " на
RULAB "Изменить яркость " на
ENLAB "Change brightness" by
KEYS `brick_change_brightness, by_label`
PARAMS formula BRIGHTNESS_CHANGE:brick_change_brightness_edit_text; paldefaults BrickValues.CHANGE_BRIGHTNESS_BY
BRICKF `catroid/src/main/java/org/catrobat/catroid/content/bricks/ChangeBrightnessByNBrick.java:30`
ACT `catroid/src/main/java/org/catrobat/catroid/content/actions/ChangeBrightnessByNAction.kt:31 via createChangeBrightnessByNAction`
DESC Изменяет яркость спрайта / Changes the brightness of the sprite

### 44. "Задать цвет " значение
RULAB "Задать цвет " значение
ENLAB "Set color" to
KEYS `brick_set_color, to_label`
PARAMS formula COLOR:brick_set_color_edit_text; paldefaults BrickValues.SET_COLOR_TO
BRICKF `catroid/src/main/java/org/catrobat/catroid/content/bricks/SetColorBrick.java:31`
ACT `catroid/src/main/java/org/catrobat/catroid/content/actions/SetColorAction.java:34 via createSetColorAction`
DESC Задает цвет спрайта (меняет HUE) / Sets the color of the sprite (changes HUE)

### 45. "Изменить цвет " на
RULAB "Изменить цвет " на
ENLAB "Change color" by
KEYS `brick_change_color, by_label`
PARAMS formula COLOR_CHANGE:brick_change_color_by_edit_text; paldefaults BrickValues.CHANGE_COLOR_BY
BRICKF `catroid/src/main/java/org/catrobat/catroid/content/bricks/ChangeColorByNBrick.java:30`
ACT `catroid/src/main/java/org/catrobat/catroid/content/actions/ChangeColorByNAction.java:34 via createChangeColorByNAction`
DESC Изменяет цвет спрайта (меняет HUE) / Changes the color of the sprite (changes HUE)

### 46. Вспышка цветом на сек
RULAB Вспышка цветом на сек
ENLAB Flash color for second(s)
KEYS `brick_flash_color, brick_flash_color_for, brick_flash_color_seconds`
PARAMS formula COLOR:brick_flash_color_edit_text; formula TIME:brick_flash_color_duration_edit_text; paldefaults 0.0, 1.0
BRICKF `catroid/src/main/java/org/catrobat/catroid/content/bricks/FlashColorBrick.java:30`
ACT `catroid/src/main/java/org/catrobat/catroid/content/actions/FlashColorAction.kt:29 via createFlashColorAction`
DESC Creates FlashColorAction, BlinkSpriteAction, MoveTowardsPointAction (no BrickInfo, from code)

### 47. Эффект частиц
RULAB Эффект частиц
ENLAB Fade particle effect
KEYS `fade_particle_effect`
PARAMS spinner `brick_fade_particle_effect_spinner`
BRICKF `catroid/src/main/java/org/catrobat/catroid/content/bricks/FadeParticleEffectBrick.kt:35`
ACT `catroid/src/main/java/org/catrobat/catroid/content/actions/FadeParticleEffectAction.kt:29 via createFadeParticleEffectsAction`
DESC Включает или выключает эффект частиц / Enables or disables the particle effect

### 48. Включить аддитивности эффекта частиц
RULAB Включить аддитивности эффекта частиц
ENLAB Turn particle effect additivity
KEYS `turn_particle_effect_additivity`
PARAMS spinner `brick_additive_particle_effect_spinner`
BRICKF `catroid/src/main/java/org/catrobat/catroid/content/bricks/ParticleEffectAdditivityBrick.kt:35`
ACT `catroid/src/main/java/org/catrobat/catroid/content/actions/AdditiveParticleEffectAction.kt:29 via createAdditiveParticleEffectsAction`
DESC Creates AdditiveParticleEffectAction, SetParticleColorAction, ChooseCameraAction (no BrickInfo, from code)

### 49. Задать цвет частиц
RULAB Задать цвет частиц
ENLAB Set particle color to
KEYS `set_particle_color_to`
PARAMS formula COLOR:brick_set_color_edit_text; paldefaults BrickValues.PARTICLE_COLOR
BRICKF `catroid/src/main/java/org/catrobat/catroid/content/bricks/SetParticleColorBrick.kt:31`
ACT `catroid/src/main/java/org/catrobat/catroid/content/actions/SetParticleColorAction.kt:32 via createSetParticleColorAction`
DESC Задает цвет частиц / Sets the color of particles

### 50. Очистить графические эффекты
RULAB Очистить графические эффекты
ENLAB Clear graphic effects
KEYS `brick_clear_graphic_effect`
PARAMS noparams
BRICKF `catroid/src/main/java/org/catrobat/catroid/content/bricks/ClearGraphicEffectBrick.java:29`
ACT `catroid/src/main/java/org/catrobat/catroid/content/actions/ClearGraphicEffectAction.java:29 via createClearGraphicEffectAction`
DESC Очищает все графические эффекты (частицы и др.) / Clears all graphic effects (particles, etc.)

## Текст и диалоги / Text & Dialogs

### 51. Cказать
RULAB Cказать
ENLAB Say
KEYS `brick_say_bubble`
PARAMS paldefaults context.getString(R.string.brick_say_bubble_default_value)
BRICKF `catroid/src/main/java/org/catrobat/catroid/content/bricks/SayBubbleBrick.java:33`
ACT `catroid/src/main/java/org/catrobat/catroid/content/actions/ThinkSayBubbleAction.java:36 via createThinkSayBubbleAction`
DESC Показывает речевой пузырь сообщения ("Сказать") у спрайта. / Shows a speech bubble ("Say") above the sprite.
COND `if (!isBackgroundSprite) {`

### 52. Cказать в течении секунды
RULAB Cказать в течении секунды
ENLAB Say for seconds
KEYS `brick_say_bubble, brick_think_say_for_text, phiro_seconds`
PARAMS paldefaults context.getString(R.string.brick_say_bubble_default_value), 1.0f
BRICKF `catroid/src/main/java/org/catrobat/catroid/content/bricks/SayForBubbleBrick.java:33`
ACT `catroid/src/main/java/org/catrobat/catroid/content/actions/ThinkSayBubbleAction.java:36 via createThinkSayForBubbleAction`; `catroid/src/main/java/org/catrobat/catroid/content/actions/WaitForBubbleBrickAction.java:27 via createWaitForBubbleBrickAction`
DESC Показывает речевой пузырь сообщения ("Сказать") на указанное время, затем скрывает его. / Shows a speech bubble ("Say") for the given duration, then hides it.
COND `if (!isBackgroundSprite) {`

### 53. Думать
RULAB Думать
ENLAB Think
KEYS `brick_think_bubble`
PARAMS formula STRING:brick_bubble_edit_text; paldefaults context.getString(R.string.brick_think_bubble_default_value)
BRICKF `catroid/src/main/java/org/catrobat/catroid/content/bricks/ThinkBubbleBrick.java:36`
ACT `catroid/src/main/java/org/catrobat/catroid/content/actions/ThinkSayBubbleAction.java:36 via createThinkSayBubbleAction`
DESC Показывает над спрайтом «облачко мысли» с заданным текстом. / Shows a thought bubble with the given text above the sprite.
COND `if (!isBackgroundSprite) {`

### 54. Думать в течении секунды
RULAB Думать в течении секунды
ENLAB Think for seconds
KEYS `brick_think_bubble, brick_think_say_for_text, seconds_string`
PARAMS formula STRING:brick_for_bubble_edit_text_text; formula DURATION_IN_SECONDS:brick_for_bubble_edit_text_duration; paldefaults context.getString(R.string.brick_think_bubble_default_value), 1.0f
BRICKF `catroid/src/main/java/org/catrobat/catroid/content/bricks/ThinkForBubbleBrick.java:37`
ACT `catroid/src/main/java/org/catrobat/catroid/content/actions/ThinkSayBubbleAction.java:36 via createThinkSayForBubbleAction`; `catroid/src/main/java/org/catrobat/catroid/content/actions/WaitForBubbleBrickAction.java:27 via createWaitForBubbleBrickAction`
DESC Показывает над актёром облачко с мыслями с заданным текстом в течение указанного времени (в секундах), после чего облачко исчезает и выполнение скрипта продолжается. / Shows a thought bubble with the given text above the actor for the specified time (in seconds); after that the bubble disappears and the script continues.
COND `if (!isBackgroundSprite) {`

### 55. Спросить Цвет фона (пусто = по умолчанию) Цвет кнопок (пусто = по умолчанию) и сохранить написанный ответ в
RULAB Спросить Цвет фона (пусто = по умолчанию) Цвет кнопок (пусто = по умолчанию) и сохранить написанный ответ в
ENLAB Ask Background color (empty = default) Buttons color (empty = default) and store written answer in
KEYS `brick_ask_label, brick_ask_background, brick_ask_buttons, brick_ask_store`
PARAMS formula ASK_QUESTION:brick_ask_question_edit_text; formula COLOR:brick_ask_background_edit_text; formula COLOR_CHANGE:brick_ask_buttons_edit_text; paldefaults context.getString(R.string.brick_ask_default_question); spinner `brick_ask_spinner`
BRICKF `catroid/src/main/java/org/catrobat/catroid/content/bricks/AskBrick.java:31`
ACT `catroid/src/main/java/org/catrobat/catroid/content/actions/AskAction.kt:34 via createAskAction`
DESC Спрашивает вопрос и сохраняет введенное значение. Можно задать цвет фона и кнопок (пусто = по умолчанию) / Asks a question and saves the entered value. Background and buttons color can be set (empty = default)
COND `if (!isBackgroundSprite) {`

### 56. Спросить Задать подсказку Задать текст "ОК" Задать текст "Отмена" Значение по умолчанию и сохранить написанный ответ в
RULAB Спросить Задать подсказку Задать текст "ОК" Задать текст "Отмена" Значение по умолчанию и сохранить написанный ответ в
ENLAB Ask Set clue Set text "Ok" Set text "Cancel" Default value and store written answer in
KEYS `brick_ask_label, brick_ask_label2, brick_ask_label3, brick_ask_label4, brick_ask_label5, brick_ask_store`
PARAMS formula ASK_QUESTION:brick_big_ask_question; formula TEXT:brick_big_ask_clue; formula OK:brick_big_ask_ok; formula CANEL:brick_big_ask_canel; formula DEFAULT:brick_big_ask_default; paldefaults context.getString(R.string.brick_ask_default_question), "Введите ответ:", "OK", "Отмена", "Ваш ответ"; spinner `brick_big_ask_spinner`
BRICKF `catroid/src/main/java/org/catrobat/catroid/content/bricks/BigAskBrick.java:31`
ACT `catroid/src/main/java/org/catrobat/catroid/content/actions/BigAskAction.kt:34 via createBigAskAction`
DESC Расширенный блок "Спросить" / Extended "Ask" block
COND `if (!isBackgroundSprite) {`

### 57. Показать переменную на "X: " "Y: "
RULAB Показать переменную на "X: " "Y: "
ENLAB Show variable at "x:" "y:"
KEYS `brick_show_variable, brick_show_variable_position, x_label, y_label`
PARAMS formula X_POSITION:brick_show_variable_edit_text_x; formula Y_POSITION:brick_show_variable_edit_text_y; paldefaults BrickValues.X_POSITION, BrickValues.Y_POSITION; spinner `show_variable_spinner`
BRICKF `catroid/src/main/java/org/catrobat/catroid/content/bricks/ShowTextBrick.java:34`
ACT `catroid/src/main/java/org/catrobat/catroid/content/actions/ShowTextAction.kt:35 via createShowVariableAction`
DESC Показывает значение переменной в указанной позиции на сцене (базовый блок). / Shows the value of a variable at the given position on the stage (basic block).
COND `if (!isBackgroundSprite) {`

### 58. Показать переменную на "X: " "Y: " размер: % цвет: выравнивание
RULAB Показать переменную на "X: " "Y: " размер: % цвет: выравнивание
ENLAB Show variable at "x:" "y:" size: % color: aligned
KEYS `brick_show_variable, brick_show_variable_position, x_label, y_label, brick_show_variable_size, percent_symbol, brick_show_variable_color, brick_show_variable_aligned`
PARAMS formula X_POSITION:brick_show_variable_color_size_edit_text_x; formula Y_POSITION:brick_show_variable_color_size_edit_text_y; formula SIZE:brick_show_variable_color_size_edit_relative_size; formula COLOR:brick_show_variable_color_size_edit_color; paldefaults BrickValues.X_POSITION, BrickValues.Y_POSITION, BrickValues.RELATIVE_SIZE_IN_PERCENT, BrickValues.SHOW_VARIABLE_COLOR; spinner `brick_show_variable_color_size_align_spinner` gen AlignmentStyle; spinner `show_variable_color_size_spinner` gen AlignmentStyle
BRICKF `catroid/src/main/java/org/catrobat/catroid/content/bricks/ShowTextColorSizeAlignmentBrick.java:63`
ACT `catroid/src/main/java/org/catrobat/catroid/content/actions/ShowTextColorSizeAlignmentAction.java:40 via createShowVariableColorAndSizeAction`
DESC Показывает значение переменной с заданными размером, цветом и выравниванием. / Shows a variable's value with a chosen size, color and alignment.

### 59. Показать текст с названием на "X: " "Y: " размер: % цвет: шрифт (файлы проекта) направление выравнивание
RULAB Показать текст с названием на "X: " "Y: " размер: % цвет: шрифт (файлы проекта) направление выравнивание
ENLAB Show text with name at "x:" "y:" size: % color: font (project files) rotation aligned
KEYS `brick_show_text, brick_show_name, brick_show_variable_position, x_label, y_label, brick_show_variable_size, percent_symbol, brick_show_variable_color, brick_show_text_font, brick_show_text_rotation, brick_show_variable_aligned`
PARAMS formula TEXT:brick_show_text_rotation_edit_text; formula NAME:brick_show_text_rotation_edit_name; formula FILE:brick_show_text_rotation_edit_font; formula X_POSITION:brick_show_text_rotation_edit_x; formula Y_POSITION:brick_show_text_rotation_edit_y; formula SIZE:brick_show_text_rotation_edit_size; formula COLOR:brick_show_text_rotation_edit_color; formula VALUE:brick_show_text_rotation_edit_rotation; spinner `brick_show_text_rotation_spinner` gen AlignmentStyle
BRICKF `catroid/src/main/java/org/catrobat/catroid/content/bricks/ShowTextRotationBrick.java:63`
ACT `catroid/src/main/java/org/catrobat/catroid/content/actions/ShowTextRotationAction.java:42 via createShowTextRotationAction`
DESC Показывает текст на экране в указанной позиции с заданным поворотом, размером, цветом и шрифтом. Полезно для вывода подписей и надписей. / Shows a text on the screen at the specified position with the given rotation, size, color and font. Useful for labels and captions.

### 60. Текст Х: "Y: "
RULAB Текст Х: "Y: "
ENLAB Text X: "y:"
KEYS `drone_brick_set_text, drone_brick_set_x, y_label`
PARAMS formula X_DESTINATION:brick_set_text_edit_text_x; formula Y_DESTINATION:brick_set_text_edit_text_y; formula STRING:brick_set_text_edit_text; paldefaults 0, 0, "Hello!"
BRICKF `catroid/src/main/java/org/catrobat/catroid/content/bricks/SetTextBrick.java:33`
ACT `catroid/src/main/java/org/catrobat/catroid/content/actions/SetTextAction.java:35 via createSetTextAction`
DESC Отображает текст в указанной точке сцены по координатам X и Y. / Displays a text at the given stage position using X and Y coordinates.

### 61. Локализовать спрайты на язык в переменную
RULAB Локализовать спрайты на язык в переменную
ENLAB Localize sprites to language to variable
KEYS `brick_localize_sprites, brick_localize_language, brick_dialogue_variable`
PARAMS formula STRING:brick_localize_edit; spinner `brick_localize_lang_spinner`
BRICKF `catroid/src/main/java/org/catrobat/catroid/content/bricks/LocalizeSpritesBrick.java:14`
ACT `catroid/src/main/java/org/catrobat/catroid/content/actions/LocalizeSpritesAction.kt:10 via createLocalizeSpritesAction`
DESC Локализует (переводит) спрайты проекта на выбранный язык и сохраняет отчёт в переменную. ВАЖНО: блокирует выполнение на время локализации (до 120 секунд). / Localizes (translates) the project's sprites to the chosen language and saves a report into a variable. NOTE: blocks execution while localizing (up to 120 seconds).

## Камера 2D / Camera 2D

### 62. Задать фокус камере с % горизонтальной и вертикальной гибкостью
RULAB Задать фокус камере с % горизонтальной и вертикальной гибкостью
ENLAB Become focus point with % horizontal and vertical flexibility
KEYS `brick_focus_point, percent_symbol, brick_horizontal_and, brick_vertical_flex`
PARAMS noparams
BRICKF `catroid/src/main/java/org/catrobat/catroid/content/bricks/SetCameraFocusPointBrick.kt:31`
ACT `catroid/src/main/java/org/catrobat/catroid/content/actions/SetCameraFocusPointAction.kt:31 via createSetCameraFocusPointAction`
DESC Фокусирует камеру на спрайте / Focuses the camera on the sprite

### 63. Задать позицию камере X Y
RULAB Задать позицию камере X Y
ENLAB Set camera position X Y
KEYS `brick_set_camera_position2, brick_x_label, brick_y_label`
PARAMS formula X_POSITION:brick_set_camera_pos_x; formula Y_POSITION:brick_set_camera_pos_y; paldefaults 100, 300
BRICKF `catroid/src/main/java/org/catrobat/catroid/content/bricks/SetCameraPosition2Brick.java:8`
ACT `catroid/src/main/java/org/catrobat/catroid/content/actions/SetCameraPositionAction.java:9 via createSetCameraPositionAction`
DESC Задает позицию камере / Sets the camera position

### 64. Задать вращение камере градусов
RULAB Задать вращение камере градусов
ENLAB Set camera rotation degrees
KEYS `brick_set_camera_rotation2, brick_rotation_label`
PARAMS formula DEGREES:brick_set_camera_rotation_value; paldefaults 90.0
BRICKF `catroid/src/main/java/org/catrobat/catroid/content/bricks/SetCameraRotation2Brick.java:8`
ACT `catroid/src/main/java/org/catrobat/catroid/content/actions/SetCameraRotationAction.java:8 via createSetCameraRotationAction`
DESC задает вращение камеры / Sets the camera rotation

### 65. Задать зум камере зум
RULAB Задать зум камере зум
ENLAB Set camera zoom to zoom
KEYS `brick_set_camera_zoom, brick_zoom_label`
PARAMS formula VALUE:brick_set_camera_zoom_value; paldefaults 3.0
BRICKF `catroid/src/main/java/org/catrobat/catroid/content/bricks/SetCameraZoomBrick.java:8`
ACT `catroid/src/main/java/org/catrobat/catroid/content/actions/SetCameraZoomAction.kt:8 via createSetCameraZoomAction`
DESC Задает приближение камере / Sets the camera zoom

### 66. Привязать спрайт к камере
RULAB Привязать спрайт к камере
ENLAB Pin sprite to camera
KEYS `brick_pin_to_camera`
PARAMS noparams
BRICKF `catroid/src/main/java/org/catrobat/catroid/content/bricks/PinToCameraBrick.java:7`
ACT `catroid/src/main/java/org/catrobat/catroid/content/actions/PinToCameraAction.kt:7 via createPinToCameraAction`
DESC Фиксирует спрайт на сцене (полезно для UI) / Pins the sprite on the scene (useful for UI)

### 67. Отвязать спрайт от камеры
RULAB Отвязать спрайт от камеры
ENLAB Unpin sprite from camera
KEYS `brick_unpin_from_camera`
PARAMS noparams
BRICKF `catroid/src/main/java/org/catrobat/catroid/content/bricks/UnpinFromCameraBrick.java:7`
ACT `catroid/src/main/java/org/catrobat/catroid/content/actions/UnpinFromCameraAction.kt:7 via createUnpinFromCameraAction`
DESC Открепляет спрайт от камеры / Unpins the sprite from the camera

### 68. Привязать к камере со смещением ID объекта X: Y: Z:
RULAB Привязать к камере со смещением ID объекта X: Y: Z:
ENLAB Attach to camera with offset Object ID X: Y: Z:
KEYS `brick_attach_camera_offset, brick_3d_object_name, brick_offset_x, brick_offset_y, brick_offset_z`
PARAMS formula NAME:brick_attach_camera_offset_edit_name; formula X_POSITION:brick_attach_camera_offset_edit_x; formula Y_POSITION:brick_attach_camera_offset_edit_y; formula Z_POSITION:brick_attach_camera_offset_edit_z; paldefaults "camera_object", 0f, 0f, 0f
BRICKF `catroid/src/main/java/org/catrobat/catroid/content/bricks/AttachToCameraWithOffsetBrick.java:8`
ACT `catroid/src/main/java/org/catrobat/catroid/content/actions/AttachToCameraWithOffsetAction.java:10 via createAttachToCameraWithOffsetAction`
DESC Привязывает 3D объект к камере с заданным смещением по X, Y, Z. Полезно для фонариков или объектов в руках. / Attaches a 3D object to the camera with the given X, Y, Z offset. Useful for flashlights or held objects.

## Камера устройства и медиа / Device Camera & Media

### 69. Привязать камеру к Режим
RULAB Привязать камеру к Режим
ENLAB Attach camera to Mode
KEYS `brick_camera_track_title, brick_camera_track_mode`
PARAMS spinner `brick_video_spinner`
BRICKF `catroid/src/main/java/org/catrobat/catroid/content/bricks/CameraBrick.kt:35`
ACT `catroid/src/main/java/org/catrobat/catroid/content/actions/CameraBrickAction.kt:28 via createUpdateCameraPreviewAction`
DESC Включает или выключает камеру устройства (и отображает ее на заднем плане) / Turns the device camera on or off (and displays it in the background)
COND `if (!ProjectManager.getInstance().currentProject.isCastProject) {`

### 70. Использовать камеру
RULAB Использовать камеру
ENLAB Use camera
KEYS `brick_choose_camera`
PARAMS spinner `brick_choose_camera_spinner`
BRICKF `catroid/src/main/java/org/catrobat/catroid/content/bricks/ChooseCameraBrick.kt:35`
ACT `catroid/src/main/java/org/catrobat/catroid/content/actions/ChooseCameraAction.kt:28 via createSetFrontCameraAction`; `catroid/src/main/java/org/catrobat/catroid/content/actions/ChooseCameraAction.kt:28 via createSetBackCameraAction`
DESC Определяет, какую камеру использовать: переднюю или заднюю / Determines which camera to use: front or rear
COND `if (!ProjectManager.getInstance().currentProject.isCastProject) {`

### 71. Включить фонарик
RULAB Включить фонарик
ENLAB Turn flashlight
KEYS `brick_flash`
PARAMS spinner `brick_flash_spinner`
BRICKF `catroid/src/main/java/org/catrobat/catroid/content/bricks/FlashBrick.java:37`
ACT `catroid/src/main/java/org/catrobat/catroid/content/actions/FlashAction.kt:28 via createFlashAction`
DESC Включает или выключает фонарик / Turns the flashlight on or off
COND `if (!ProjectManager.getInstance().currentProject.isCastProject) {`

### 72. Получить изображение из и использовать как текущий образ
RULAB Получить изображение из и использовать как текущий образ
ENLAB Get image from and use as current look
KEYS `brick_look_request, brick_look_request_result`
PARAMS formula LOOK_REQUEST:brick_look_request_edit_text; paldefaults BrickValues.LOOK_REQUEST
BRICKF `catroid/src/main/java/org/catrobat/catroid/content/bricks/LookRequestBrick.kt:32`
ACT `catroid/src/main/java/org/catrobat/catroid/content/actions/LookRequestAction.kt:38 via createLookRequestAction`
DESC Получает изображение по ссылке и пытается использовать как образ / Gets an image from a URL and tries to use it as a look
COND `!isBackgroundSprite -> looksBrickList.add(LookRequestBrick(BrickValues.LOOK_REQUEST))`

### 73. Получить изображение из и использовать как текущий фон
RULAB Получить изображение из и использовать как текущий фон
ENLAB Get image from and use as current background
KEYS `brick_look_request, brick_background_request_result`
PARAMS formula BACKGROUND_REQUEST:brick_background_request_edit_text; paldefaults BrickValues.BACKGROUND_REQUEST_LANDSCAPE
BRICKF `catroid/src/main/java/org/catrobat/catroid/content/bricks/BackgroundRequestBrick.kt:33`
ACT `catroid/src/main/java/org/catrobat/catroid/content/actions/LookRequestAction.kt:38 via createLookRequestAction`
DESC Creates LookRequestAction, LookFromUrlAction, DownloadZippedLooksAction (no BrickInfo, from code)
COND `ProjectManager.getInstance().currentProject.xmlHeader.islandscapeMode() -> looksBrickList.add(BackgroundRequestBrick(BrickValues.BACKGROUND_REQUEST_LA`
ALT `else -> looksBrickList.add(BackgroundRequestBrick(BrickValues.BACKGROUND_REQUEST))`

### 74. Получить образ по URL как
RULAB Получить образ по URL как
ENLAB Get look from URL as
KEYS `brick_look_from_url, brick_look_from_url_as`
PARAMS formula URL:brick_look_from_url_edit; formula NAME:brick_look_from_url_name_edit; paldefaults "https://example.com/image.png", "myLook"
BRICKF `catroid/src/main/java/org/catrobat/catroid/content/bricks/LookFromUrlBrick.kt:32`
ACT `catroid/src/main/java/org/catrobat/catroid/content/actions/LookFromUrlAction.kt:42 via createLookFromUrlAction`
DESC Creates LookFromUrlAction, DownloadZippedLooksAction, OpenUrlAction (no BrickInfo, from code)
COND `if (!isBackgroundSprite) {`

### 75. Сделать скриншот и использовать как образ
RULAB Сделать скриншот и использовать как образ
ENLAB Take screenshot and store it as look
KEYS `brick_screenshot`
PARAMS noparams
BRICKF `catroid/src/main/java/org/catrobat/catroid/content/bricks/ScreenShotBrick.java:30`
ACT `catroid/src/main/java/org/catrobat/catroid/content/actions/ScreenShotAction.kt:39 via createScreenShotAction`
DESC Делает скриншот сцены и использует его как образ / Takes a screenshot of the scene and uses it as a look
COND `if (!isBackgroundSprite) {`

### 76. Сделать снимок и использовать как образ
RULAB Сделать снимок и использовать как образ
ENLAB Take photo from camera and use as look
KEYS `brick_photo`
PARAMS noparams
BRICKF `catroid/src/main/java/org/catrobat/catroid/content/bricks/PhotoBrick.java:31`
ACT `catroid/src/main/java/org/catrobat/catroid/content/actions/PhotoAction.kt:11 via createPhoto`
DESC Делает снимок с камеры и использует его как образ / Takes a photo with the camera and uses it as a look
COND `if (!isBackgroundSprite) {`

### 77. Обрезать образ начало (x, y) конец (x, y)
RULAB Обрезать образ начало (x, y) конец (x, y)
ENLAB Crop look from (x, y) to (x, y)
KEYS `brick_cut_look, brick_cut_look1, brick_cut_look2`
PARAMS formula X1:brick_cut_look_fromx; formula Y1:brick_cut_look_fromy; formula X2:brick_cut_look_tox; formula Y2:brick_cut_look_toy; paldefaults 100, 200, 300, 400
BRICKF `catroid/src/main/java/org/catrobat/catroid/content/bricks/CutLookBrick.java:31`
ACT `catroid/src/main/java/org/catrobat/catroid/content/actions/CutLookAction.kt:49 via createCutLook`
DESC Обрезает образие спрайта. / Crops a sprite costume.
COND `if (!isBackgroundSprite) {`

### 78. Образ в файл (Загрузки)
RULAB Образ в файл (Загрузки)
ENLAB Look to file (Downloads)
KEYS `brick_save_look`
PARAMS formula FILE:brick_save_look_edit_text; paldefaults "my_actor.png"
BRICKF `catroid/src/main/java/org/catrobat/catroid/content/bricks/SaveLookBrick.java:31`
ACT `catroid/src/main/java/org/catrobat/catroid/content/actions/SaveLookAction.kt:60 via createSaveLookAction`
DESC Сохраняет образ в "Загрузки" пользователя / Saves the look to the user's Downloads folder
COND `if (!isBackgroundSprite) {`

### 79. Образ из файла (Загрузки)
RULAB Образ из файла (Загрузки)
ENLAB Look from file (Downloads)
KEYS `brick_look_file`
PARAMS paldefaults "my_actor.png"
BRICKF `catroid/src/main/java/org/catrobat/catroid/content/bricks/LookFileBrick.java:31`
ACT `catroid/src/main/java/org/catrobat/catroid/content/actions/LookFileAction.kt:55 via createLookFileAction`
DESC Использует образ из файла в папке "Загрузки" пользователя / Uses a look from a file in the user's "Downloads" folder
COND `if (!isBackgroundSprite) {`

### 80. Образ в файл (проект)
RULAB Образ в файл (проект)
ENLAB Look to file (project)
KEYS `brick_save_look_files`
PARAMS paldefaults "look.png"
BRICKF `catroid/src/main/java/org/catrobat/catroid/content/bricks/SaveLookFilesBrick.java:31`
ACT `catroid/src/main/java/org/catrobat/catroid/content/actions/SaveLookFilesAction.kt:60 via createSaveLookFilesAction`
DESC Сохраняет образ в файлы проекта / Saves the look to project files
COND `if (!isBackgroundSprite) {`

### 81. Образ из файла (проект)
RULAB Образ из файла (проект)
ENLAB Look from file (project)
KEYS `brick_set_look_files`
PARAMS formula FILE:brick_set_look_files_edit_text; paldefaults "look.png"
BRICKF `catroid/src/main/java/org/catrobat/catroid/content/bricks/SetLookFilesBrick.java:31`
ACT `catroid/src/main/java/org/catrobat/catroid/content/actions/SetLookFilesAction.kt:55 via createSetLookFilesAction`
DESC Использует образ из файла проекта / Uses a look from a project file

### 82. Образ в таблицы Таблица (красный) Таблица (зелёный) Таблица (синий) Таблица (прозрачность)
RULAB Образ в таблицы Таблица (красный) Таблица (зелёный) Таблица (синий) Таблица (прозрачность)
ENLAB Look to tables Table (red) Table (green) Table (blue) Table (alpha)
KEYS `look_to_table, table_r, table_g, table_b, table_a`
PARAMS paldefaults "rTable", "gTable", "bTable", "aTable"
BRICKF `catroid/src/main/java/org/catrobat/catroid/content/bricks/LookToTableBrick.java:31`
ACT `catroid/src/main/java/org/catrobat/catroid/content/actions/LookToTableAction.kt:63 via createLookToTableAction`
DESC Сохраняет образ по пикселям в таблицы / Saves the look pixel by pixel into tables

### 83. Образ из таблиц Таблица (красный) Таблица (зелёный) Таблица (синий) Таблица (прозрачность)
RULAB Образ из таблиц Таблица (красный) Таблица (зелёный) Таблица (синий) Таблица (прозрачность)
ENLAB Look from tables Table (red) Table (green) Table (blue) Table (alpha)
KEYS `look_from_table, table_r, table_g, table_b, table_a`
PARAMS paldefaults "rTable", "gTable", "bTable", "aTable"
BRICKF `catroid/src/main/java/org/catrobat/catroid/content/bricks/LookFromTableBrick.java:31`
ACT `catroid/src/main/java/org/catrobat/catroid/content/actions/LookFromTableAction.kt:15 via createLookFromTableAction`
DESC Загружает образ из таблиц по пикселям / Loads a look from tables pixel by pixel

## Примитивы / Primitives

### 84. Create square Название Позиция (X, Y) Ширина, Высота Цвет Прозрачность Направление Закругленные углы
RULAB Create square Название Позиция (X, Y) Ширина, Высота Цвет Прозрачность Направление Закругленные углы
ENLAB Create square Name Position (X, Y) Width, Height Color Transparency Rotation Border radius
KEYS `brick_square, brick_square_name, brick_square_pos, brick_square_size, brick_square_color, brick_square_trans, brick_square_rot, brick_square_borders`
PARAMS paldefaults "square", "#ff0000", 0f, 0f, 100f, 100f, 1f, 0f, 0f
BRICKF `catroid/src/main/java/org/catrobat/catroid/content/bricks/SquareBrick.java:40`
ACT `catroid/src/main/java/org/catrobat/catroid/content/actions/SquareAction.kt:42 via createSquareAction`
DESC Создает квадрат / Creates a square

### 85. Удалить квадрат Название
RULAB Удалить квадрат Название
ENLAB Delete square Name
KEYS `brick_del_square, brick_square_name`
PARAMS formula NAME:brick_del_square_edit_name; paldefaults "square"
BRICKF `catroid/src/main/java/org/catrobat/catroid/content/bricks/DelSquareBrick.java:31`
ACT `catroid/src/main/java/org/catrobat/catroid/content/actions/DelSquareAction.kt:42 via createDelSquareAction`
DESC Удаляет квадрат / Deletes a square

### 86. Открыть в браузере
RULAB Открыть в браузере
ENLAB Open in browser
KEYS `brick_open, brick_in_browser`
PARAMS formula OPEN_URL:brick_open_url_edit_text; paldefaults BrickValues.OPEN_IN_BROWSER
BRICKF `catroid/src/main/java/org/catrobat/catroid/content/bricks/OpenUrlBrick.kt:33`
ACT `catroid/src/main/java/org/catrobat/catroid/content/actions/OpenUrlAction.kt:33 via createOpenUrlAction`
DESC Creates OpenUrlAction, AskSpeechAction, StartListeningAction (no BrickInfo, from code)

### 87. Задать свет Phiro Красный Зеленый Синий
RULAB Задать свет Phiro Красный Зеленый Синий
ENLAB Set Phiro light Red Green Blue
KEYS `brick_phiro_rgb_led_action, phiro_rgb_led_red, phiro_rgb_led_green, phiro_rgb_led_blue`
PARAMS formula PHIRO_LIGHT_RED:brick_phiro_rgb_led_action_red_edit_text; formula PHIRO_LIGHT_GREEN:brick_phiro_rgb_led_action_green_edit_text; formula PHIRO_LIGHT_BLUE:brick_phiro_rgb_led_action_blue_edit_text; paldefaults PhiroRGBLightBrick.Eye.BOTH, BrickValues.PHIRO_VALUE_RED, BrickValues.PHIRO_VALUE_GREEN, BrickValues.PHIRO_VALUE_BLUE; spinner `brick_phiro_rgb_light_spinner`
BRICKF `catroid/src/main/java/org/catrobat/catroid/content/bricks/PhiroRGBLightBrick.java:44`
ACT `catroid/src/main/java/org/catrobat/catroid/content/actions/PhiroRGBLightAction.java:39 via createPhiroRgbLedEyeAction`
DESC Зажигает RGB-светодиод(ы) робота Phiro выбранным цветом (красный, зеленый, синий каналы). ВАЖНО: требует подключения по Bluetooth. / Lights up the Phiro robot RGB LED(s) with the chosen color (red, green, blue channels). IMPORTANT: requires a Bluetooth connection.
COND `if (SettingsFragment.isPhiroSharedPreferenceEnabled(context)) {`

## Буферы / Buffers

### 88. Создать буфер
RULAB Создать буфер
ENLAB Create Buffer
KEYS `buffer_create`
PARAMS formula NAME:brick_buffer_name; formula WIDTH:brick_buffer_width; formula HEIGHT:brick_buffer_height; paldefaults "Map", "512", "512"
BRICKF `catroid/src/main/java/org/catrobat/catroid/content/bricks/CreateBufferBrick.java:4`
ACT `catroid/src/main/java/org/catrobat/catroid/content/actions/CreateBufferAction.kt:8 via createBufferAction`
DESC Создает графический буфер (RenderTarget) указанного размера для отрисовки 2D сцены в текстуру. / Creates a render buffer (render target) of the given size, used to render a 2D scene into a texture.
COND `if (SettingsFragment.isPhiroSharedPreferenceEnabled(context)) {`

### 89. Режим буфера
RULAB Режим буфера
ENLAB Buffer mode
KEYS `brick_buffer_mode`
PARAMS formula NAME:brick_buffer_name; formula X_POSITION:brick_buffer_2d; formula Y_POSITION:brick_buffer_3d; paldefaults Formula("Map"), Formula(1), Formula(0)
BRICKF `catroid/src/main/java/org/catrobat/catroid/content/bricks/SetBufferModeBrick.java:8`
ACT `catroid/src/main/java/org/catrobat/catroid/content/actions/SetBufferModeAction.kt:7 via createSetBufferModeAction`
DESC Задает режим отрисовки буфера: рендерить ли в него 2D сцену и/или 3D сцену (введите 0 или 1 для каждого). / Sets the render mode of a buffer: whether to render the 2D scene and/or the 3D scene into it (enter 0 or 1 for each).
COND `if (SettingsFragment.isPhiroSharedPreferenceEnabled(context)) {`

### 90. Автообновление буфера
RULAB Автообновление буфера
ENLAB Buffer auto-update
KEYS `buffer_auto_update`
PARAMS formula NAME:brick_buffer_name; formula TIME:brick_buffer_state; paldefaults Formula("Map"), Formula(1)
BRICKF `catroid/src/main/java/org/catrobat/catroid/content/bricks/SetBufferAutoUpdateBrick.java:3`
ACT `catroid/src/main/java/org/catrobat/catroid/content/actions/SetBufferAutoUpdateAction.kt:7 via createSetBufferAutoUpdateAction`
DESC Включает или выключает автоматическое обновление буфера (offscreen-рендер цели) по его имени. 0 = выкл, 1 = вкл. / Enables or disables automatic updating of a render buffer (off-screen render target) by its name. 0 = off, 1 = on.
COND `if (SettingsFragment.isPhiroSharedPreferenceEnabled(context)) {`

### 91. Добавить в буфер
RULAB Добавить в буфер
ENLAB Add to Buffer
KEYS `buffer_add`
PARAMS formula NAME:brick_buffer_name; paldefaults "Map"
BRICKF `catroid/src/main/java/org/catrobat/catroid/content/bricks/AddToBufferBrick.java:4`
ACT `catroid/src/main/java/org/catrobat/catroid/content/actions/AddToBufferAction.kt:8 via createAddToBufferAction`
DESC Добавляет спрайт в именованный буфер (off-screen render target). Позже буфер можно использовать как текстуру через префикс buffer://. ВАЖНО: буфер должен быть создан заранее. / Adds the sprite to a named buffer (off-screen render target). The buffer can later be used as a texture via the buffer:// prefix. IMPORTANT: the buffer must be created beforehand.
COND `if (SettingsFragment.isPhiroSharedPreferenceEnabled(context)) {`

### 92. Удалить из буфера
RULAB Удалить из буфера
ENLAB Remove from Buffer
KEYS `buffer_remove`
PARAMS formula NAME:brick_buffer_name; paldefaults Formula("Map")
BRICKF `catroid/src/main/java/org/catrobat/catroid/content/bricks/RemoveFromBufferBrick.java:4`
ACT `catroid/src/main/java/org/catrobat/catroid/content/actions/RemoveFromBufferAction.kt:7 via removeBufferAction`
DESC Убирает текущий спрайт из буфера отрисовки (RenderTexture) с заданным именем. / Removes the current sprite from the render buffer (RenderTexture) with the given name.

### 93. Рисовать только в буфере (1/0)
RULAB Рисовать только в буфере (1/0)
ENLAB Draw only in buffer (1/0)
KEYS `brick_buffer_only`
PARAMS formula TIME:brick_buffer_state; paldefaults Formula(1)
BRICKF `catroid/src/main/java/org/catrobat/catroid/content/bricks/SetBufferOnlyBrick.java:7`
ACT `catroid/src/main/java/org/catrobat/catroid/content/actions/SetBufferOnlyAction.kt:7 via createSetBufferOnlyAction`
DESC Включает или выключает режим, при котором сцена рисуется только в буфер, а не на экран (0 = выкл, 1 = вкл). / Enables or disables the mode where the scene is drawn only into a buffer instead of the screen (0 = off, 1 = on).

### 94. 2D Камера буфера
RULAB 2D Камера буфера
ENLAB 2D Buffer Camera
KEYS `buffer_cam`
PARAMS formula NAME:brick_buffer_name; formula X_POSITION:brick_buffer_x; formula Y_POSITION:brick_buffer_y; formula ZOOM:brick_buffer_zoom; formula DEGREES:brick_buffer_rot; paldefaults "Map", "100", "200", "1", "0"
BRICKF `catroid/src/main/java/org/catrobat/catroid/content/bricks/SetBufferCameraBrick.java:4`
ACT `catroid/src/main/java/org/catrobat/catroid/content/actions/SetBufferCameraAction.kt:8 via createSetBufferCameraAction`
DESC Задает 2D камеру для буфера: позиция (X, Y), зум и вращение. / Sets the 2D camera for a buffer: position (X, Y), zoom and rotation.

### 95. Буфер камеры 3D
RULAB Буфер камеры 3D
ENLAB Camera 3D buffer
KEYS `brick_buffer_cam_3d`
PARAMS formula NAME:brick_buffer_cam_3d_name; formula X_POSITION:brick_buffer_cam_3d_x; formula Y_POSITION:brick_buffer_cam_3d_y; formula Z_POSITION:brick_buffer_cam_3d_z; formula DEGREES:brick_buffer_cam_3d_yaw; formula DEGREES_BY:brick_buffer_cam_3d_pitch; formula ROTATION:brick_buffer_cam_3d_roll; formula SIZE:brick_buffer_cam_3d_fov; paldefaults Formula("Map"), Formula(100), Formula(100), Formula(100), Formula(120), Formula(-20), Formula(0), Formula(67)
BRICKF `catroid/src/main/java/org/catrobat/catroid/content/bricks/SetBufferCamera3DBrick.java:8`
ACT `catroid/src/main/java/org/catrobat/catroid/content/actions/SetBufferCamera3DAction.kt:7 via createSetBufferCamera3DAction`
DESC Задает 3D камеру для буфера: позиция (X, Y, Z), поворот (yaw, pitch, roll) и угол обзора (FOV). / Sets the 3D camera for a buffer: position (X, Y, Z), rotation (yaw, pitch, roll) and field of view (FOV).

### 96. Установить образ из буфера
RULAB Установить образ из буфера
ENLAB Set look from Buffer
KEYS `buffer_apply`
PARAMS formula NAME:brick_buffer_name; paldefaults "Map"
BRICKF `catroid/src/main/java/org/catrobat/catroid/content/bricks/ApplyBufferLookBrick.java:4`
ACT `catroid/src/main/java/org/catrobat/catroid/content/actions/ApplyBufferLookAction.kt:10 via createApplyBufferLookAction`
DESC Применяет к спрайту изображение из графического буфера (render texture) по его имени как текущий образ. / Applies the image from a render buffer (render texture) by its name as the sprite's current look.

### 97. Сохранить буфер в файл
RULAB Сохранить буфер в файл
ENLAB Save Buffer to file
KEYS `buffer_save`
PARAMS formula NAME:brick_buffer_name; formula STRING_VALUE:brick_buffer_file; paldefaults Formula("Map"), Formula("buffer.png")
BRICKF `catroid/src/main/java/org/catrobat/catroid/content/bricks/SaveBufferBrick.java:3`
ACT `catroid/src/main/java/org/catrobat/catroid/content/actions/SaveBufferAction.kt:7 via createSaveBufferAction`
DESC Сохраняет буфер отрисовки (RenderTexture) с заданным именем в файл. / Saves the render buffer (RenderTexture) with the given name to a file.

### 98. цвет границы
RULAB цвет границы
ENLAB set border color
KEYS `brick_set_border_color`
PARAMS formula COLOR:brick_set_border_color_edit_text; paldefaults Formula(0)
BRICKF `catroid/src/main/java/org/catrobat/catroid/content/bricks/SetBorderColorBrick.java:30`
ACT `catroid/src/main/java/org/catrobat/catroid/content/actions/SetBorderColorAction.kt:30 via createSetBorderColorAction`
DESC Задает цвет границы спрайта / Sets the border color of the sprite

### 99. толщина границы
RULAB толщина границы
ENLAB set border width
KEYS `brick_set_border_width`
PARAMS formula WIDTH:brick_set_border_width_edit_text; paldefaults Formula(1.0)
BRICKF `catroid/src/main/java/org/catrobat/catroid/content/bricks/SetBorderWidthBrick.java:30`
ACT `catroid/src/main/java/org/catrobat/catroid/content/actions/SetBorderWidthAction.kt:29 via createSetBorderWidthAction`
DESC Задает толщину границы спрайта / Sets the border thickness of the sprite

### 100. радиус скругления
RULAB радиус скругления
ENLAB set corner radius
KEYS `brick_set_corner_radius`
PARAMS formula CORNER:brick_set_corner_radius_edit_text; paldefaults Formula(0.0)
BRICKF `catroid/src/main/java/org/catrobat/catroid/content/bricks/SetCornerRadiusBrick.java:30`
ACT `catroid/src/main/java/org/catrobat/catroid/content/actions/SetCornerRadiusAction.kt:29 via createSetCornerRadiusAction`
DESC Задает радиус скругления углов спрайта / Sets the corner radius of the sprite

### 101. эффект размытия
RULAB эффект размытия
ENLAB set filter blur
KEYS `brick_set_filter_blur`
PARAMS formula INTENSITY:brick_set_filter_blur_edit_text; paldefaults Formula(0.0)
BRICKF `catroid/src/main/java/org/catrobat/catroid/content/bricks/SetFilterBlurBrick.java:30`
ACT `catroid/src/main/java/org/catrobat/catroid/content/actions/SetFilterBlurAction.kt:33 via createSetFilterBlurAction`
DESC Применяет эффект размытия к спрайту / Applies a blur effect to the sprite

### 102. эффект сепии
RULAB эффект сепии
ENLAB set filter sepia
KEYS `brick_set_filter_sepia`
PARAMS formula VALUE_1:brick_set_filter_sepia_edit_text; paldefaults Formula(0.0)
BRICKF `catroid/src/main/java/org/catrobat/catroid/content/bricks/SetFilterSepiaBrick.java:30`
ACT `catroid/src/main/java/org/catrobat/catroid/content/actions/SetFilterSepiaAction.kt:33 via createSetFilterSepiaAction`
DESC Применяет эффект сепии к спрайту / Applies a sepia effect to the sprite

### 103. эффект пикселизации
RULAB эффект пикселизации
ENLAB set filter pixelate
KEYS `brick_set_filter_pixelate`
PARAMS formula SIZE:brick_set_filter_pixelate_edit_text; paldefaults Formula(0.0)
BRICKF `catroid/src/main/java/org/catrobat/catroid/content/bricks/SetFilterPixelateBrick.java:30`
ACT `catroid/src/main/java/org/catrobat/catroid/content/actions/SetFilterPixelateAction.kt:33 via createSetFilterPixelateAction`
DESC Применяет эффект пикселизации к спрайту / Applies a pixelate effect to the sprite

### 104. Воспроизвести GIF
RULAB Воспроизвести GIF
ENLAB Play GIF
KEYS `brick_play_gif_title`
PARAMS formula VALUE_1:brick_play_gif_edit; paldefaults "cat.gif"
BRICKF `catroid/src/main/java/org/catrobat/catroid/content/bricks/PlayGifBrick.java:8`
ACT `catroid/src/main/java/org/catrobat/catroid/content/actions/PlayGifAction.kt:7 via createPlayGifAction`
DESC Воспроизводит GIF-анимацию на спрайте. / Plays a GIF animation on the sprite.

### 105. Остановить GIF
RULAB Остановить GIF
ENLAB Stop GIF
KEYS `brick_stop_gif_title`
PARAMS noparams
BRICKF `catroid/src/main/java/org/catrobat/catroid/content/bricks/StopGifBrick.java:7`
ACT `catroid/src/main/java/org/catrobat/catroid/content/actions/StopGifAction.kt:6 via createStopGifAction`
DESC Останавливает воспроизведение GIF-анимации на спрайте. / Stops playing the GIF animation on the sprite.

### 106. Воспроизвести спрайт-лист сетка строка кадры скорость
RULAB Воспроизвести спрайт-лист сетка строка кадры скорость
ENLAB Play spritesheet grid row frames speed
KEYS `brick_play_spritesheet_title, brick_play_spritesheet_grid, brick_play_spritesheet_row, brick_play_spritesheet_frames, brick_play_spritesheet_speed`
PARAMS formula VALUE_1:brick_play_spritesheet_rows; formula VALUE_2:brick_play_spritesheet_cols; formula VALUE_3:brick_play_spritesheet_selected_row; formula VALUE_4:brick_play_spritesheet_frames_count; formula VALUE_5:brick_play_spritesheet_speed_val; paldefaults Formula(4), Formula(8), Formula(1), Formula(8), Formula(0.1)
BRICKF `catroid/src/main/java/org/catrobat/catroid/content/bricks/PlaySpritesheetBrick.java:8`
ACT `catroid/src/main/java/org/catrobat/catroid/content/actions/PlaySpritesheetAction.kt:7 via createPlaySpritesheetAction`
DESC Воспроизводит на спрайте последовательность кадров из спрайт-листа (сетка, ряд, число кадров, скорость). / Plays a frame sequence from a spritesheet on the sprite (grid, row, frame count, speed).

### 107. Остановить спрайт-лист
RULAB Остановить спрайт-лист
ENLAB Stop spritesheet
KEYS `brick_stop_spritesheet_title`
PARAMS noparams
BRICKF `catroid/src/main/java/org/catrobat/catroid/content/bricks/StopSpritesheetBrick.java:7`
ACT `catroid/src/main/java/org/catrobat/catroid/content/actions/StopSpritesheetAction.kt:6 via createStopSpritesheetAction`
DESC Останавливает воспроизведение спрайт-листа на спрайте. / Stops playing the spritesheet on the sprite.

### 108. Угловые смещения верх-лево верх-право низ-право низ-лево
RULAB Угловые смещения верх-лево верх-право низ-право низ-лево
ENLAB Set corner offsets top-left top-right bottom-right bottom-left
KEYS `brick_set_corner_offsets_title, brick_set_corner_offsets_top_left, brick_set_corner_offsets_top_right, brick_set_corner_offsets_bottom_right, brick_set_corner_offsets_bottom_left`
PARAMS formula VALUE_1:brick_set_corner_offsets_tlx; formula VALUE_2:brick_set_corner_offsets_tly; formula VALUE_3:brick_set_corner_offsets_trx; formula VALUE_4:brick_set_corner_offsets_try; formula VALUE_5:brick_set_corner_offsets_brx; formula VALUE_6:brick_set_corner_offsets_bry; formula VALUE_7:brick_set_corner_offsets_blx; formula VALUE_8:brick_set_corner_offsets_bly; paldefaults Formula(0), Formula(0), Formula(50), Formula(100), Formula(0), Formula(0), Formula(0), Formula(0)
BRICKF `catroid/src/main/java/org/catrobat/catroid/content/bricks/SetCornerOffsetsBrick.java:8`
ACT `catroid/src/main/java/org/catrobat/catroid/content/actions/SetCornerOffsetsAction.kt:7 via createSetCornerOffsetsAction`
DESC Искажает образ спрайта, сдвигая его четыре угла. / Deforms the sprite's look by shifting its four corners.

### 109. Плавная анимация Тип начало конец длительность
RULAB Плавная анимация Тип начало конец длительность
ENLAB Ease animation Type start end duration
KEYS `brick_ease_title, brick_easing_type_label, brick_ease_start_label, brick_ease_end_label, brick_ease_duration_label`
PARAMS formula IF_CONDITION:brick_ease_property_start_edit; formula INSERT_ITEM_INTO_USERLIST_VALUE:brick_ease_property_end_edit; formula REPLACE_ITEM_IN_USERLIST_VALUE:brick_ease_property_duration_edit; paldefaults 0, 0, 100f, 250f, 1f; spinner `ease_property_target_spinner`; spinner `ease_property_type_spinner`
BRICKF `catroid/src/main/java/org/catrobat/catroid/content/bricks/EasePropertyBrick.java:14`
ACT `catroid/src/main/java/org/catrobat/catroid/content/actions/EasePropertyAction.kt:10 via createEasePropertyAction`
DESC Плавно анимирует выбранное свойство спрайта (позиция, размер, прозрачность, цвет, яркость и др.) от начального до конечного значения с функцией сглаживания. / Smoothly eases the selected sprite property (position, size, transparency, color, brightness, etc.) from a start to an end value using an easing function.

### 110. Добавить текст в буфер в
RULAB Добавить текст в буфер в
ENLAB Add text to buffer to
KEYS `brick_add_text_buffer_title, brick_add_text_buffer_to`
PARAMS formula IF_CONDITION:brick_add_text_buffer_name; formula INSERT_ITEM_INTO_USERLIST_VALUE:brick_add_text_var_name; paldefaults "myText", "Map"
BRICKF `catroid/src/main/java/org/catrobat/catroid/content/bricks/AddTextToBufferBrick.java:8`
ACT `catroid/src/main/java/org/catrobat/catroid/content/actions/AddTextToBufferAction.kt:8 via createAddTextToBufferAction`
DESC Добавляет текст переменной в буфер (рендер-текстуру). / Adds a variable's text to the buffer (render texture).

### 111. Установить текстовый буфер имя состояние
RULAB Установить текстовый буфер имя состояние
ENLAB Set text buffer name state
KEYS `brick_set_text_buffer_only_title, brick_set_text_buffer_only_name_label, brick_set_text_buffer_only_state_label`
PARAMS formula VALUE_1:brick_set_text_buffer_only_name; formula VALUE_2:brick_set_text_buffer_only_state; paldefaults "myText", 1
BRICKF `catroid/src/main/java/org/catrobat/catroid/content/bricks/SetTextBufferOnlyBrick.java:8`
ACT `catroid/src/main/java/org/catrobat/catroid/content/actions/SetTextBufferOnlyAction.kt:8 via createSetTextBufferOnlyAction`
DESC Задает, отображать ли в буфере только текст (0/1). / Sets whether the buffer renders only text (0/1).

### 112. Маска буфера режим
RULAB Маска буфера режим
ENLAB Buffer mask mode
KEYS `brick_buffer_mask_title, brick_buffer_mask_mode`
PARAMS formula VALUE_1:brick_buffer_mask_name_field; paldefaults "Map", 1; spinner `brick_buffer_mask_spinner`
BRICKF `catroid/src/main/java/org/catrobat/catroid/content/bricks/BufferMaskBrick.java:13`
ACT `catroid/src/main/java/org/catrobat/catroid/content/actions/BufferMaskAction.kt:7 via createBufferMaskAction`
DESC Применяет буфер как маску для образа спрайта (режимы: растягивание или экран). / Applies a buffer as a mask for the sprite's look (Stretch or Screen mode).

### 113. Шейдер буфера
RULAB Шейдер буфера
ENLAB Buffer shader
KEYS `brick_buffer_shader_title`
PARAMS formula IF_CONDITION:brick_buffer_shader_name; formula INSERT_ITEM_INTO_USERLIST_VALUE:brick_buffer_shader_vsh; formula REPLACE_ITEM_IN_USERLIST_VALUE:brick_buffer_shader_fsh; paldefaults "Map", "", ""
BRICKF `catroid/src/main/java/org/catrobat/catroid/content/bricks/SetBufferShaderBrick.java:8`
ACT `catroid/src/main/java/org/catrobat/catroid/content/actions/SetBufferShaderAction.kt:8 via createSetBufferShaderAction`
DESC Задает кастомный вершинный и фрагментный шейдер (GLSL) для буфера. / Sets a custom vertex and fragment shader (GLSL) for the buffer.

### 114. Uniform буфера в
RULAB Uniform буфера в
ENLAB Buffer uniform in
KEYS `brick_buf_uniform_title, brick_buf_uniform_in_buf`
PARAMS formula IF_CONDITION:brick_buf_uniform_buf_name; formula INSERT_ITEM_INTO_USERLIST_VALUE:brick_buf_uniform_name; formula REPLACE_ITEM_IN_USERLIST_VALUE:brick_buf_uniform_v1; formula INSERT_ITEM_INTO_USERLIST_INDEX:brick_buf_uniform_v2; formula TIMES_TO_REPEAT:brick_buf_uniform_v3; paldefaults "Map", "time", 1f, 0f, 0f, 1
BRICKF `catroid/src/main/java/org/catrobat/catroid/content/bricks/SetBufferShaderUniformBrick.java:8`
ACT `catroid/src/main/java/org/catrobat/catroid/content/actions/SetBufferShaderUniformAction.kt:10 via createSetBufferShaderUniformAction`
DESC Задает значение uniform (v1–v3) шейдера буфера. / Sets a uniform value (v1–v3) of the buffer's shader.

### 115. Начать запись буфера буфер файл FPS битрейт
RULAB Начать запись буфера буфер файл FPS битрейт
ENLAB Start buffer recording buffer file FPS bitrate
KEYS `brick_start_buffer_recording_title, brick_start_buffer_recording_buf, brick_start_buffer_recording_file, brick_start_buffer_recording_fps, brick_start_buffer_recording_bitrate`
PARAMS formula VALUE_1:brick_start_buffer_recording_edit_buf; formula VALUE_2:brick_start_buffer_recording_edit_file; formula VALUE_3:brick_start_buffer_recording_edit_fps; formula VALUE_4:brick_start_buffer_recording_edit_bitrate; paldefaults "myBuffer", "output.mp4", 30, 2000000
BRICKF `catroid/src/main/java/org/catrobat/catroid/content/bricks/StartBufferRecordingBrick.java:8`
ACT `catroid/src/main/java/org/catrobat/catroid/content/actions/StartBufferRecordingAction.kt:8 via createStartBufferRecordingAction`
DESC Начинает запись буфера в видеофайл с заданными FPS и битрейтом. / Starts recording the buffer to a video file with the given FPS and bitrate.

### 116. Остановить запись буфера
RULAB Остановить запись буфера
ENLAB Stop buffer recording
KEYS `brick_stop_buffer_recording_title`
PARAMS noparams
BRICKF `catroid/src/main/java/org/catrobat/catroid/content/bricks/StopBufferRecordingBrick.java:7`
ACT `catroid/src/main/java/org/catrobat/catroid/content/actions/StopBufferRecordingAction.kt:8 via createStopBufferRecordingAction`
DESC Останавливает запись буфера и сохраняет видеофайл. / Stops recording the buffer and saves the video file.

### 117. Циклы рендера
RULAB Циклы рендера
ENLAB Render loops
KEYS `brick_render_loops_title`
PARAMS formula IF_CONDITION:brick_render_loop_2d; formula INSERT_ITEM_INTO_USERLIST_VALUE:brick_render_loop_fast2d; formula REPLACE_ITEM_IN_USERLIST_VALUE:brick_render_loop_3d; paldefaults 1f, 1f, 1f
BRICKF `catroid/src/main/java/org/catrobat/catroid/content/bricks/SetMainRenderLoopsBrick.java:8`
ACT `catroid/src/main/java/org/catrobat/catroid/content/actions/SetMainRenderLoopsAction.kt:8 via createSetMainRenderLoopsAction`
DESC Включает или отключает главные циклы рендера 2D, Fast-2D и 3D (0 — выключено, иначе — включено). / Enables or disables the main 2D, Fast-2D and 3D render loops (0 = off, any other value = on).

### 118. FX буфера
RULAB FX буфера
ENLAB Buffer FX
KEYS `brick_buffer_fx_title`
PARAMS formula IF_CONDITION:brick_buffer_fx_name; formula INSERT_ITEM_INTO_USERLIST_VALUE:brick_buffer_fx_vfx; formula REPLACE_ITEM_IN_USERLIST_VALUE:brick_buffer_fx_mip; paldefaults "Map", 1f, 1f
BRICKF `catroid/src/main/java/org/catrobat/catroid/content/bricks/SetBufferEffectsBrick.java:8`
ACT `catroid/src/main/java/org/catrobat/catroid/content/actions/SetBufferEffectsAction.kt:8 via createSetBufferEffectsAction`
DESC Включает или выключает 3D-постобработку и мипмаппинг для буфера (0/1). / Turns 3D post-processing and mipmapping for the buffer on or off (0/1).

### 119. Preset Target Strength
RULAB Preset Target Strength
ENLAB Preset Target Strength
KEYS `brick_apply_shader_id, brick_apply_shader_preset, brick_apply_shader_target, brick_apply_shader_strength`
PARAMS formula NAME:brick_apply_shader_id; formula TEXT:brick_apply_shader_target; formula VALUE_1:brick_apply_shader_strength; paldefaults "shader1", 0, "", 0.5; spinner `brick_apply_shader_spinner`
BRICKF `catroid/src/main/java/org/catrobat/catroid/content/bricks/ApplyShaderBrick.java:14`
ACT `catroid/src/main/java/org/catrobat/catroid/content/actions/ApplyShaderAction.kt:7 via createApplyShaderAction`
DESC Применяет шейдер-эффект к экрану, спрайту или буферу (для спрайта введи имя, для экрана оставь пусто, для буфера — buffer://имя_буфера). / Applies a shader effect to the screen, sprite, or buffer (for sprite enter its name, for screen leave empty, for buffer enter buffer://buffer_name).

## Партиклы / Particles

### 120. Create particles from file: Particle Editor
RULAB Create particles from file: Particle Editor
ENLAB Create particles from file: Particle Editor
KEYS `brick_create_particle, brick_edit_particles`
PARAMS formula FILE:brick_particle_file_edit_text; paldefaults "fire.particle"
BRICKF `catroid/src/main/java/org/catrobat/catroid/content/bricks/CreateParticleEffectBrick.java:13`
ACT `catroid/src/main/java/org/catrobat/catroid/content/actions/CreateParticleEffectAction.kt:11 via createCreateParticleEffectAction`
DESC Создаёт шаблон эффекта частиц из файла проекта. / Creates a particle effect template from a project file.

### 121. Spawn particles from file: instance:
RULAB Spawn particles from file: instance:
ENLAB Spawn particles from file: instance:
KEYS `brick_spawn_particle_effect, brick_spawn_particle_instance`
PARAMS formula FILE:brick_spawn_particle_effect_file; formula NAME:brick_spawn_particle_instance_id; formula X:brick_spawn_particle_x; formula Y:brick_spawn_particle_y; paldefaults "fire.particle", "fire1", 0.0, 0.0
BRICKF `catroid/src/main/java/org/catrobat/catroid/content/bricks/SpawnParticleInstanceBrick.java:8`
ACT `catroid/src/main/java/org/catrobat/catroid/content/actions/SpawnParticleInstanceAction.kt:9 via createSpawnParticleInstanceAction`
DESC Создаёт экземпляр частиц из шаблона файла. / Spawns a particle instance from a template file.

### 122. Particles instance: render to buffer: mode:
RULAB Particles instance: render to buffer: mode:
ENLAB Particles instance: render to buffer: mode:
KEYS `brick_particle_buffer_for, brick_particle_buffer_target, brick_particle_buffer_mode`
PARAMS formula NAME:brick_particle_buffer_instance_id; formula TEXT:brick_particle_buffer_name; paldefaults "fire1", "myBuffer", 0; spinner `brick_particle_buffer_mode_spinner`
BRICKF `catroid/src/main/java/org/catrobat/catroid/content/bricks/SetParticleBufferRenderBrick.java:14`
ACT `catroid/src/main/java/org/catrobat/catroid/content/actions/SetParticleBufferRenderAction.kt:10 via createSetParticleBufferRenderAction`
DESC Задаёт режим рендера буфера для экземпляра частиц. / Sets buffer render mode for a particle instance.

### 123. Transform particles: Width (Scale X): Height (Scale Y): Rotation:
RULAB Transform particles: Width (Scale X): Height (Scale Y): Rotation:
ENLAB Transform particles: Width (Scale X): Height (Scale Y): Rotation:
KEYS `brick_set_particle_transform, brick_particle_scale_x, brick_particle_scale_y, brick_particle_rotation_2d`
PARAMS formula NAME:brick_particle_transform_instance_id; formula X:brick_particle_transform_x; formula Y:brick_particle_transform_y; formula VALUE_1:brick_particle_transform_scale_x; formula VALUE_2:brick_particle_transform_scale_y; formula VALUE_3:brick_particle_transform_rotation; paldefaults "fire1", 0.0, 0.0, 1.0, 1.0, 0.0
BRICKF `catroid/src/main/java/org/catrobat/catroid/content/bricks/SetParticleTransformBrick.java:8`
ACT `catroid/src/main/java/org/catrobat/catroid/content/actions/SetParticleTransformAction.kt:9 via createSetParticleTransformAction`
DESC Задаёт позицию, масштаб и поворот экземпляра частиц. / Sets position, scale and rotation of a particle instance.

### 124. Set particle property: property:
RULAB Set particle property: property:
ENLAB Set particle property: property:
KEYS `brick_set_particle_property, brick_particle_property_label`
PARAMS formula NAME:brick_particle_property_instance_id; formula VALUE_1:brick_particle_property_value; paldefaults "fire1", 0, 50.0; spinner `brick_particle_property_spinner`
BRICKF `catroid/src/main/java/org/catrobat/catroid/content/bricks/SetParticlePropertyBrick.java:14`
ACT `catroid/src/main/java/org/catrobat/catroid/content/actions/SetParticlePropertyAction.kt:9 via createSetParticlePropertyAction`
DESC Задаёт свойство экземпляра частиц (скорость, гравитация и т.д.). / Sets a particle instance property (speed, gravity, etc.).

### 125. Emit particle burst: count:
RULAB Emit particle burst: count:
ENLAB Emit particle burst: count:
KEYS `brick_emit_particle_burst, brick_particle_burst_count`
PARAMS formula NAME:brick_particle_burst_instance_id; formula VALUE_1:brick_particle_burst_count_edit_text; paldefaults "fire1", 20.0
BRICKF `catroid/src/main/java/org/catrobat/catroid/content/bricks/EmitParticleBurstBrick.java:8`
ACT `catroid/src/main/java/org/catrobat/catroid/content/actions/EmitParticleBurstAction.kt:9 via createEmitParticleBurstAction`
DESC Выпускает вспышку частиц у экземпляра. / Emits a particle burst from an instance.

### 126. Stop particles: mode:
RULAB Stop particles: mode:
ENLAB Stop particles: mode:
KEYS `brick_stop_particle_instance, brick_particle_buffer_mode`
PARAMS formula NAME:brick_particle_stop_instance_id; paldefaults "fire1", 0; spinner `brick_particle_stop_mode_spinner`
BRICKF `catroid/src/main/java/org/catrobat/catroid/content/bricks/StopParticleInstanceBrick.java:14`
ACT `catroid/src/main/java/org/catrobat/catroid/content/actions/StopParticleInstanceAction.kt:9 via createStopParticleInstanceAction`
DESC Останавливает экземпляр частиц (плавно или сразу). / Stops a particle instance (smoothly or immediately).

## Тайлмап / Tilemap

### 127. установить тайл в столбце строке в индекс тайла
RULAB установить тайл в столбце строке в индекс тайла
ENLAB set tile at column row to tile index
KEYS `brick_set_tile, brick_set_tile_row, brick_set_tile_index`
PARAMS formula TILEMAP_COLUMN:brick_set_tile_column; formula TILEMAP_ROW:brick_set_tile_row; formula TILEMAP_TILE_INDEX:brick_set_tile_index; paldefaults 0, 0, 0
BRICKF `catroid/src/main/java/org/catrobat/catroid/content/bricks/SetTileBrick.java:30`
ACT `catroid/src/main/java/org/catrobat/catroid/content/actions/SetTileAction.java:37 via createSetTileAction`
DESC Устанавливает тайл с указанным индексом в столбце и строке тайловой карты. / Places the tile with the given index at the specified column and row of the tilemap.

### 128. очистить тайл в столбце строке
RULAB очистить тайл в столбце строке
ENLAB clear tile at column row
KEYS `brick_clear_tile, brick_clear_tile_row`
PARAMS formula TILEMAP_COLUMN:brick_clear_tile_column; formula TILEMAP_ROW:brick_clear_tile_row; paldefaults 0, 0
BRICKF `catroid/src/main/java/org/catrobat/catroid/content/bricks/ClearTileBrick.java:30`
ACT `catroid/src/main/java/org/catrobat/catroid/content/actions/ClearTileAction.java:37 via createClearTileAction`
DESC Удаляет тайл в указанных столбце и строке тайловой карты. / Removes the tile at the specified column and row of the tilemap.

### 129. сделать тайл с индексом
RULAB сделать тайл с индексом
ENLAB set tile index as
KEYS `brick_set_tilemap_solid, brick_set_tilemap_solid_state`
PARAMS formula TILEMAP_TILE_INDEX:brick_set_tilemap_solid_index; formula TILEMAP_SOLID:brick_set_tilemap_solid_value; paldefaults 0, true
BRICKF `catroid/src/main/java/org/catrobat/catroid/content/bricks/SetTilemapSolidBrick.java:30`
ACT `catroid/src/main/java/org/catrobat/catroid/content/actions/SetTilemapSolidAction.java:37 via createSetTilemapSolidAction`
DESC Задает, является ли тайл с указанным индексом твердым (препятствием). / Sets whether the tile with the given index is solid (an obstacle).
