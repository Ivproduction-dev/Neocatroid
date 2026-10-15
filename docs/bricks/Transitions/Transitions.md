# Transitions / Переходы

CAT `category_transitions` FAC `setupTransitionsCategoryList` LINES 3510-3528
docs for bricks, order from CategoryBricksFactory

### 1. Появление, скорость
RULAB Появление, скорость
ENLAB Fade in at speed
KEYS `brick_fade_in`
PARAMS formula SPEED:brick_fade_in_edit_text; paldefaults 1.0
BRICKF `catroid/src/main/java/org/catrobat/catroid/content/bricks/FadeInBrick.java:30`
ACT `catroid/src/main/java/org/catrobat/catroid/content/actions/TransitionAction.kt:32 via createFadeInAction`
DESC Плавно проявляет спрайт с указанной скоростью. / Fades in the sprite at the given speed.

### 2. Затемнение, скорость
RULAB Затемнение, скорость
ENLAB Fade out at speed
KEYS `brick_fade_out`
PARAMS formula SPEED:brick_fade_out_edit_text; paldefaults 1.0
BRICKF `catroid/src/main/java/org/catrobat/catroid/content/bricks/FadeOutBrick.java:30`
ACT `catroid/src/main/java/org/catrobat/catroid/content/actions/TransitionAction.kt:32 via createFadeOutAction`
DESC Плавно затемняет спрайт с указанной скоростью. / Fades out the sprite at the given speed.

### 3. Зум-появление, скорость
RULAB Зум-появление, скорость
ENLAB Zoom in at speed
KEYS `brick_zoom_in`
PARAMS formula SPEED:brick_zoom_in_edit_text; paldefaults 1.0
BRICKF `catroid/src/main/java/org/catrobat/catroid/content/bricks/ZoomInBrick.java:30`
ACT `catroid/src/main/java/org/catrobat/catroid/content/actions/TransitionAction.kt:32 via createZoomInAction`
DESC Плавно увеличивает размер спрайта с указанной скоростью. / Zooms in the sprite at the given speed.

### 4. Зум-исчезание, скорость
RULAB Зум-исчезание, скорость
ENLAB Zoom out at speed
KEYS `brick_zoom_out`
PARAMS formula SPEED:brick_zoom_out_edit_text; paldefaults 1.0
BRICKF `catroid/src/main/java/org/catrobat/catroid/content/bricks/ZoomOutBrick.java:30`
ACT `catroid/src/main/java/org/catrobat/catroid/content/actions/TransitionAction.kt:32 via createZoomOutAction`
DESC Плавно уменьшает размер спрайта с указанной скоростью. / Zooms out the sprite at the given speed.

### 5. Выезд из-за края скорость
RULAB Выезд из-за края скорость
ENLAB Slide in from at speed
KEYS `brick_slide_in, brick_transition_over`
PARAMS formula SPEED:brick_slide_in_edit_text; paldefaults 1.0; spinner `brick_slide_in_spinner`
BRICKF `catroid/src/main/java/org/catrobat/catroid/content/bricks/SlideInBrick.java:38`
ACT `catroid/src/main/java/org/catrobat/catroid/content/actions/TransitionAction.kt:32 via createSlideInAction`
DESC Плавно въезжает спрайтом с выбранного края экрана с указанной скоростью. / Slides the sprite in from the chosen screen edge at the given speed.

### 6. Уезд за край скорость
RULAB Уезд за край скорость
ENLAB Slide out to at speed
KEYS `brick_slide_out, brick_transition_over`
PARAMS formula SPEED:brick_slide_out_edit_text; paldefaults 1.0; spinner `brick_slide_out_spinner`
BRICKF `catroid/src/main/java/org/catrobat/catroid/content/bricks/SlideOutBrick.java:38`
ACT `catroid/src/main/java/org/catrobat/catroid/content/actions/TransitionAction.kt:32 via createSlideOutAction`
DESC Плавно уезжает спрайтом за выбранный край экрана с указанной скоростью. / Slides the sprite out to the chosen screen edge at the given speed.

### 7. Выезд сверху Сцена
RULAB Выезд сверху Сцена
ENLAB Slide Up Scene
KEYS `transition_slide_up, transition_scene_param`
PARAMS formula TEXT_1:brick_slide_up_edit_1; paldefaults ""
BRICKF `catroid/src/main/java/org/catrobat/catroid/content/bricks/SlideUpBrick.java:31`
ACT `catroid/src/main/java/org/catrobat/catroid/content/actions/SlideUpAction.kt:33 via createSlideUpAction`
DESC Плавно переключает сцену с эффектом выдвижения снизу вверх. Имя целевой сцены задается параметром. / Smoothly switches to a scene with a slide-up transition effect. The target scene name is given as a parameter.

### 8. Выезд снизу Сцена
RULAB Выезд снизу Сцена
ENLAB Slide Down Scene
KEYS `transition_slide_down, transition_scene_param`
PARAMS formula TEXT_1:brick_slide_down_edit_1; paldefaults ""
BRICKF `catroid/src/main/java/org/catrobat/catroid/content/bricks/SlideDownBrick.java:31`
ACT `catroid/src/main/java/org/catrobat/catroid/content/actions/SlideDownAction.kt:33 via createSlideDownAction`
DESC Плавно переключает сцену с эффектом выдвижения сверху вниз. Имя целевой сцены задается параметром. / Smoothly switches to a scene with a slide-down transition effect. The target scene name is given as a parameter.

### 9. Выезд слева Сцена
RULAB Выезд слева Сцена
ENLAB Slide Left Scene
KEYS `transition_slide_left, transition_scene_param`
PARAMS formula TEXT_1:brick_slide_left_edit_1; paldefaults ""
BRICKF `catroid/src/main/java/org/catrobat/catroid/content/bricks/SlideLeftBrick.java:31`
ACT `catroid/src/main/java/org/catrobat/catroid/content/actions/SlideLeftAction.kt:33 via createSlideLeftAction`
DESC Плавно переключает сцену с эффектом выдвижения справа налево. Имя целевой сцены задается параметром. / Smoothly switches to a scene with a slide-left transition effect. The target scene name is given as a parameter.

### 10. Выезд справа Сцена
RULAB Выезд справа Сцена
ENLAB Slide Right Scene
KEYS `transition_slide_right, transition_scene_param`
PARAMS formula TEXT_1:brick_slide_right_edit_1; paldefaults ""
BRICKF `catroid/src/main/java/org/catrobat/catroid/content/bricks/SlideRightBrick.java:31`
ACT `catroid/src/main/java/org/catrobat/catroid/content/actions/SlideRightAction.kt:33 via createSlideRightAction`
DESC Плавно переключает сцену с эффектом выдвижения слева направо. Имя целевой сцены задается параметром. / Smoothly switches to a scene with a slide-right transition effect. The target scene name is given as a parameter.

### 11. Кроссфейд Сцена
RULAB Кроссфейд Сцена
ENLAB Crossfade Scene
KEYS `transition_crossfade, transition_scene_param`
PARAMS formula TEXT_1:brick_crossfade_edit_1; paldefaults ""
BRICKF `catroid/src/main/java/org/catrobat/catroid/content/bricks/CrossfadeBrick.java:31`
ACT `catroid/src/main/java/org/catrobat/catroid/content/actions/CrossfadeAction.kt:33 via createCrossfadeAction`
DESC Плавно переключает сцену на указанную с эффектом перекрёстного затухания (crossfade). / Smoothly switches to the specified scene using a crossfade transition effect.

### 12. Появление Сцена
RULAB Появление Сцена
ENLAB Fade from Black Scene
KEYS `transition_fade_from_black, transition_scene_param`
PARAMS formula TEXT_1:brick_fade_from_black_edit_1; paldefaults ""
BRICKF `catroid/src/main/java/org/catrobat/catroid/content/bricks/FadeFromBlackBrick.java:31`
ACT `catroid/src/main/java/org/catrobat/catroid/content/actions/FadeFromBlackAction.kt:33 via createFadeFromBlackAction`
DESC Плавно убирает чёрный экран, выполняя переход к сцене с указанным именем. ВАЖНО: имя сцены должно существовать. / Smoothly reveals the screen from black, transitioning to the scene with the given name. IMPORTANT: the scene name must exist.

### 13. Затемнение Сцена
RULAB Затемнение Сцена
ENLAB Fade to Black Scene
KEYS `transition_fade_to_black, transition_scene_param`
PARAMS formula TEXT_1:brick_fade_to_black_edit_1; paldefaults ""
BRICKF `catroid/src/main/java/org/catrobat/catroid/content/bricks/FadeToBlackBrick.java:31`
ACT `catroid/src/main/java/org/catrobat/catroid/content/actions/FadeToBlackAction.kt:33 via createFadeToBlackAction`
DESC Плавно затемняет экран до чёрного, выполняя переход к сцене с указанным именем. ВАЖНО: имя сцены должно существовать. / Smoothly fades the screen to black, transitioning to the scene with the given name. IMPORTANT: the scene name must exist.

### 14. Мгновенно Сцена
RULAB Мгновенно Сцена
ENLAB Instant Scene
KEYS `transition_instant, transition_scene_param`
PARAMS formula TEXT_1:brick_instant_transition_edit_1; paldefaults ""
BRICKF `catroid/src/main/java/org/catrobat/catroid/content/bricks/InstantTransitionBrick.java:31`
ACT `catroid/src/main/java/org/catrobat/catroid/content/actions/InstantTransitionAction.kt:33 via createInstantTransitionAction`
DESC Мгновенно переключает сцену на указанную (без анимации). / Instantly switches to the specified scene (without any animation).

### 15. Кросс-фейд из в длительность (сек)
RULAB Кросс-фейд из в длительность (сек)
ENLAB Cross-fade from to duration (sec)
KEYS `brick_cross_fade_sound, brick_cross_fade_sound_to, brick_cross_fade_sound_duration`
PARAMS formula DURATION:brick_cross_fade_duration_edit; paldefaults 2.0; spinner `brick_cross_fade_from_spinner` gen SoundInfo; spinner `brick_cross_fade_to_spinner` gen SoundInfo; src sprite-sounds
BRICKF `catroid/src/main/java/org/catrobat/catroid/content/bricks/CrossFadeSoundBrick.java:22`
ACT `catroid/src/main/java/org/catrobat/catroid/content/actions/CrossFadeSoundAction.kt:10 via createCrossFadeSoundAction`
DESC Плавно переключает звук с одного файла на другой за указанное время (кросс-фейд). / Smoothly crossfades the sound from one file to another over the given duration.
