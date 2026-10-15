# Pen / Перо

CAT `category_pen` FAC `setupPenCategoryList` LINES 2446-2474
docs for bricks, order from CategoryBricksFactory

### 1. Опустить перо
RULAB Опустить перо
ENLAB Pen down
KEYS `brick_pen_down`
PARAMS noparams
BRICKF `catroid/src/main/java/org/catrobat/catroid/content/bricks/PenDownBrick.java:29`
ACT `catroid/src/main/java/org/catrobat/catroid/content/actions/PenDownAction.java:31 via createPenDownAction`
DESC Опускает перо спрайта: при движении спрайт будет оставлять след на холсте. / Lowers the sprite's pen: while the sprite moves it will leave a trail on the canvas.
COND `if (!isBackgroundSprite) {`

### 2. Поднять перо
RULAB Поднять перо
ENLAB Pen up
KEYS `brick_pen_up`
PARAMS noparams
BRICKF `catroid/src/main/java/org/catrobat/catroid/content/bricks/PenUpBrick.java:29`
ACT `catroid/src/main/java/org/catrobat/catroid/content/actions/PenUpAction.java:31 via createPenUpAction`
DESC Поднимает перо спрайта: спрайт перестает оставлять след при движении. / Raises the sprite's pen: the sprite stops leaving a trail while moving.
COND `if (!isBackgroundSprite) {`

### 3. Задать размер пера
RULAB Задать размер пера
ENLAB Set pen size to
KEYS `brick_pen_size`
PARAMS formula PEN_SIZE:brick_set_pen_size_edit_text; paldefaults BrickValues.PEN_SIZE
BRICKF `catroid/src/main/java/org/catrobat/catroid/content/bricks/SetPenSizeBrick.java:30`
ACT `catroid/src/main/java/org/catrobat/catroid/content/actions/SetPenSizeAction.java:33 via createSetPenSizeAction`
DESC Задает толщину пера для рисования. / Sets the pen size (thickness) used when drawing.
COND `if (!isBackgroundSprite) {`

### 4. Задать цвет пера красный зеленый синий
RULAB Задать цвет пера красный зеленый синий
ENLAB Set pen color to red green blue
KEYS `brick_pen_color, color_red, color_green, color_blue`
PARAMS formula PEN_COLOR_RED:brick_set_pen_color_action_red_edit_text; formula PEN_COLOR_GREEN:brick_set_pen_color_action_green_edit_text; formula PEN_COLOR_BLUE:brick_set_pen_color_action_blue_edit_text; paldefaults BrickValues.PEN_COLOR_R, BrickValues.PEN_COLOR_G, BrickValues.PEN_COLOR_B
BRICKF `catroid/src/main/java/org/catrobat/catroid/content/bricks/SetPenColorBrick.java:39`
ACT `catroid/src/main/java/org/catrobat/catroid/content/actions/SetPenColorAction.java:35 via createSetPenColorAction`
DESC Задает цвет пера (R, G, B) для рисования на холсте. / Sets the pen color (R, G, B) used when drawing on the canvas.
COND `if (!isBackgroundSprite) {`

### 5. Штамп
RULAB Штамп
ENLAB Stamp
KEYS `brick_stamp`
PARAMS noparams
BRICKF `catroid/src/main/java/org/catrobat/catroid/content/bricks/StampBrick.java:29`
ACT `catroid/src/main/java/org/catrobat/catroid/content/actions/StampAction.java:30 via createStampAction`
DESC Оставляет «отпечаток» текущего образа спрайта на сцене (как штамп). Отпечаток остается на месте, даже когда спрайт двигается. / Leaves a «stamp» of the sprite's current look on the stage. The stamp stays in place even when the sprite moves.
COND `if (!isBackgroundSprite) {`

### 6. Очистить
RULAB Очистить
ENLAB Clear
KEYS `brick_clear_background`
PARAMS noparams
BRICKF `catroid/src/main/java/org/catrobat/catroid/content/bricks/ClearBackgroundBrick.java:29`
ACT `catroid/src/main/java/org/catrobat/catroid/content/actions/ClearBackgroundAction.java:29 via createClearBackgroundAction`
DESC Очищает фон сцены (делает его пустым). / Clears the stage background (makes it empty).
COND `if (!isBackgroundSprite) {`

### 7. заполнить прямоугольник ширина высота
RULAB заполнить прямоугольник ширина высота
ENLAB fill rect width height
KEYS `brick_fill_rect, brick_label_width, brick_label_height`
PARAMS formula X:brick_fill_rect_edit_x; formula Y:brick_fill_rect_edit_y; formula WIDTH:brick_fill_rect_edit_width; formula HEIGHT:brick_fill_rect_edit_height; paldefaults Formula(0.0), Formula(0.0), Formula(100.0), Formula(100.0)
BRICKF `catroid/src/main/java/org/catrobat/catroid/content/bricks/FillRectBrick.java:30`
ACT `catroid/src/main/java/org/catrobat/catroid/content/actions/FillRectAction.kt:31 via createFillRectAction`
DESC Рисует закрашенный прямоугольник на холсте / Draws a filled rectangle on the canvas
COND `if (!isBackgroundSprite) {`

### 8. заполнить круг радиус
RULAB заполнить круг радиус
ENLAB fill circle radius
KEYS `brick_fill_circle, brick_label_radius`
PARAMS formula X:brick_fill_circle_edit_x; formula Y:brick_fill_circle_edit_y; formula SIZE:brick_fill_circle_edit_radius; paldefaults Formula(0.0), Formula(0.0), Formula(50.0)
BRICKF `catroid/src/main/java/org/catrobat/catroid/content/bricks/FillCircleBrick.java:30`
ACT `catroid/src/main/java/org/catrobat/catroid/content/actions/FillCircleAction.kt:31 via createFillCircleAction`
DESC Рисует закрашенный круг на холсте / Draws a filled circle on the canvas

### 9. заполнить многоугольник
RULAB заполнить многоугольник
ENLAB fill polygon
KEYS `brick_fill_polygon`
PARAMS formula TEXT:brick_fill_polygon_edit_text; paldefaults Formula("0,0;100,0;50,100")
BRICKF `catroid/src/main/java/org/catrobat/catroid/content/bricks/FillPolygonBrick.java:30`
ACT `catroid/src/main/java/org/catrobat/catroid/content/actions/FillPolygonAction.kt:31 via createFillPolygonAction`
DESC Рисует закрашенный многоугольник на холсте по координатам точек / Draws a filled polygon on the canvas using point coordinates

### 10. нарисовать прямоугольник ширина высота
RULAB нарисовать прямоугольник ширина высота
ENLAB draw rect width height
KEYS `brick_draw_rect, brick_label_width, brick_label_height`
PARAMS formula X:brick_draw_rect_edit_x; formula Y:brick_draw_rect_edit_y; formula WIDTH:brick_draw_rect_edit_width; formula HEIGHT:brick_draw_rect_edit_height; paldefaults Formula(0.0), Formula(0.0), Formula(100.0), Formula(100.0)
BRICKF `catroid/src/main/java/org/catrobat/catroid/content/bricks/DrawRectBrick.java:30`
ACT `catroid/src/main/java/org/catrobat/catroid/content/actions/DrawRectAction.kt:31 via createDrawRectAction`
DESC Рисует контур прямоугольника на холсте / Draws a rectangle outline on the canvas

### 11. нарисовать круг радиус
RULAB нарисовать круг радиус
ENLAB draw circle radius
KEYS `brick_draw_circle, brick_label_radius`
PARAMS formula X:brick_draw_circle_edit_x; formula Y:brick_draw_circle_edit_y; formula SIZE:brick_draw_circle_edit_radius; paldefaults Formula(0.0), Formula(0.0), Formula(50.0)
BRICKF `catroid/src/main/java/org/catrobat/catroid/content/bricks/DrawCircleBrick.java:30`
ACT `catroid/src/main/java/org/catrobat/catroid/content/actions/DrawCircleAction.kt:31 via createDrawCircleAction`
DESC Рисует контур круга на холсте / Draws a circle outline on the canvas

### 12. нарисовать линию
RULAB нарисовать линию
ENLAB draw line
KEYS `brick_draw_line`
PARAMS formula X1:brick_draw_line_edit_x1; formula Y1:brick_draw_line_edit_y1; formula X2:brick_draw_line_edit_x2; formula Y2:brick_draw_line_edit_y2; paldefaults Formula(0.0), Formula(0.0), Formula(100.0), Formula(100.0)
BRICKF `catroid/src/main/java/org/catrobat/catroid/content/bricks/DrawLineBrick.java:30`
ACT `catroid/src/main/java/org/catrobat/catroid/content/actions/DrawLineAction.kt:31 via createDrawLineAction`
DESC Рисует линию на холсте от точки до точки / Draws a line on the canvas from point to point

### 13. выбрать холст
RULAB выбрать холст
ENLAB set canvas
KEYS `brick_set_canvas`
PARAMS formula NAME:brick_set_canvas_edit_text; paldefaults Formula("canvas")
BRICKF `catroid/src/main/java/org/catrobat/catroid/content/bricks/SetCanvasBrick.java:30`
ACT `catroid/src/main/java/org/catrobat/catroid/content/actions/SetCanvasAction.kt:31 via createSetCanvasAction`
DESC Выбирает активный холст для рисования / Selects the active canvas for drawing

### 14. очистить холст
RULAB очистить холст
ENLAB clear canvas
KEYS `brick_clear_canvas`
PARAMS noparams
BRICKF `catroid/src/main/java/org/catrobat/catroid/content/bricks/ClearCanvasBrick.java:29`
ACT `catroid/src/main/java/org/catrobat/catroid/content/actions/ClearCanvasAction.kt:28 via createClearCanvasAction`
DESC Очищает все рисунки на холсте / Clears all drawings on the canvas

### 15. шрифт имя шрифта размер шрифта
RULAB шрифт имя шрифта размер шрифта
ENLAB set font font name font size
KEYS `brick_set_font, brick_label_font_name, brick_label_font_size`
PARAMS formula NAME:brick_set_font_edit_name; formula SIZE:brick_set_font_edit_size; paldefaults Formula("Arial"), Formula(12)
BRICKF `catroid/src/main/java/org/catrobat/catroid/content/bricks/SetFontBrick.java:30`
ACT `catroid/src/main/java/org/catrobat/catroid/content/actions/SetFontAction.kt:29 via createSetFontAction`
DESC Задает шрифт и его размер для текста на холсте / Sets the font and its size for text on the canvas

### 16. нарисовать текст текст
RULAB нарисовать текст текст
ENLAB draw text text
KEYS `brick_draw_text, brick_label_text`
PARAMS formula X:brick_draw_text_edit_x; formula Y:brick_draw_text_edit_y; formula TEXT:brick_draw_text_edit_text; paldefaults Formula(0.0), Formula(0.0), Formula("")
BRICKF `catroid/src/main/java/org/catrobat/catroid/content/bricks/DrawTextBrick.java:30`
ACT `catroid/src/main/java/org/catrobat/catroid/content/actions/DrawTextAction.kt:36 via createDrawTextAction`
DESC Рисует текст на холсте в указанных координатах / Draws text on the canvas at the specified coordinates

### 17. Изменить для пера Значение
RULAB Изменить для пера Значение
ENLAB Set for pen Value
KEYS `brick_set_pen_property_title, brick_set_pen_property_to`
PARAMS formula VALUE:brick_set_pen_property_value; paldefaults 0, "1"; spinner `brick_set_pen_property_spinner`
BRICKF `catroid/src/main/java/org/catrobat/catroid/content/bricks/SetPenPropertyBrick.java:14`
ACT `catroid/src/main/java/org/catrobat/catroid/content/actions/SetPenPropertyAction.java:11 via createSetPenPropertyAction`
DESC Изменяет указанное свойство пера (слой, прозрачность, режим смешивания, авто-обновление) / Changes the specified pen property (layer, transparency, blend mode, auto-update)

### 18. Нарисовать линию от X Y до X Y Толщина
RULAB Нарисовать линию от X Y до X Y Толщина
ENLAB Draw line from X Y to X Y Thickness
KEYS `brick_pen_draw_line, brick_pen_draw_line_y1, brick_pen_draw_line_x2, brick_pen_draw_line_y2, brick_pen_thickness`
PARAMS formula VALUE_1:brick_pen_line_x1; formula VALUE_2:brick_pen_line_y1; formula VALUE_3:brick_pen_line_x2; formula VALUE_4:brick_pen_line_y2; formula VALUE_5:brick_pen_line_thickness; paldefaults 0.0, 0.0, 100.0, 100.0, 5.0
BRICKF `catroid/src/main/java/org/catrobat/catroid/content/bricks/PenDrawLineBrick.java:8`
ACT `catroid/src/main/java/org/catrobat/catroid/content/actions/PenDrawLineAction.java:13 via createPenDrawLineAction`
DESC Быстро рисует векторную линию прямо на холсте пера / Fast draws a vector line directly on the pen canvas

### 19. Треугольник X1 Y1 X2 Y2 X3 Y3 Заливка (1/0)
RULAB Треугольник X1 Y1 X2 Y2 X3 Y3 Заливка (1/0)
ENLAB Triangle X1 Y1 X2 Y2 X3 Y3 Fill (1/0)
KEYS `brick_pen_draw_triangle, brick_pen_triangle_y1, brick_pen_triangle_x2, brick_pen_triangle_y2, brick_pen_triangle_x3, brick_pen_triangle_y3, brick_pen_fill`
PARAMS formula VALUE_1:brick_pen_tri_x1; formula VALUE_2:brick_pen_tri_y1; formula VALUE_3:brick_pen_tri_x2; formula VALUE_4:brick_pen_tri_y2; formula VALUE_5:brick_pen_tri_x3; formula VALUE_6:brick_pen_tri_y3; formula VALUE_7:brick_pen_tri_fill; paldefaults -50.0, -50.0, 0.0, 50.0, 50.0, -50.0, 1
BRICKF `catroid/src/main/java/org/catrobat/catroid/content/bricks/PenDrawTriangleBrick.java:8`
ACT `catroid/src/main/java/org/catrobat/catroid/content/actions/PenDrawTriangleAction.java:13 via createPenDrawTriangleAction`
DESC Рисует треугольник с заливкой или без (идеально для 3D графики) / Draws a triangle with or without fill (ideal for 3D graphics)

### 20. Прямоугольник X Y Ширина Высота Заливка (1/0)
RULAB Прямоугольник X Y Ширина Высота Заливка (1/0)
ENLAB Rectangle X Y Width Height Fill (1/0)
KEYS `brick_pen_draw_rect, brick_pen_rect_y, brick_pen_rect_w, brick_pen_rect_h, brick_pen_fill`
PARAMS formula VALUE_1:brick_pen_rect_x; formula VALUE_2:brick_pen_rect_y; formula VALUE_3:brick_pen_rect_w; formula VALUE_4:brick_pen_rect_h; formula VALUE_5:brick_pen_rect_fill; paldefaults 0.0, 0.0, 100.0, 100.0, 1
BRICKF `catroid/src/main/java/org/catrobat/catroid/content/bricks/PenDrawRectBrick.java:8`
ACT `catroid/src/main/java/org/catrobat/catroid/content/actions/PenDrawRectAction.java:13 via createPenDrawRectAction`
DESC Рисует прямоугольник с заливкой или без прямо на холсте пера / Draws a rectangle with or without fill directly on the pen canvas

### 21. Дуга/Круг X Y Радиус Угол Дуга (°) Заливка (1/0)
RULAB Дуга/Круг X Y Радиус Угол Дуга (°) Заливка (1/0)
ENLAB Arc/Circle X Y Radius Angle Arc (°) Fill (1/0)
KEYS `brick_pen_draw_circle, brick_pen_circle_y, brick_pen_circle_radius, brick_pen_circle_start_angle, brick_pen_circle_sweep_angle, brick_pen_fill`
PARAMS formula VALUE_1:brick_pen_circle_x; formula VALUE_2:brick_pen_circle_y; formula VALUE_3:brick_pen_circle_radius; formula VALUE_4:brick_pen_circle_start_angle; formula VALUE_5:brick_pen_circle_sweep_angle; formula VALUE_6:brick_pen_circle_fill; paldefaults 0.0, 0.0, 50.0, 0.0, 360.0, 1
BRICKF `catroid/src/main/java/org/catrobat/catroid/content/bricks/PenDrawCircleBrick.java:8`
ACT `catroid/src/main/java/org/catrobat/catroid/content/actions/PenDrawCircleAction.java:13 via createPenDrawCircleAction`
DESC Рисует круг, полукруг, сектор или конус с настройками направления и дуги / Draws a circle, semicircle, sector, or cone with direction and arc degree settings

### 22. Обновить экран
RULAB Обновить экран
ENLAB Flush screen
KEYS `brick_pen_flush`
PARAMS noparams
BRICKF `catroid/src/main/java/org/catrobat/catroid/content/bricks/PenFlushBrick.java:7`
ACT `catroid/src/main/java/org/catrobat/catroid/content/actions/PenFlushAction.java:9 via createPenFlushAction`
DESC Мгновенно рендерит все нарисованные линии и фигуры на экран / Instantly renders all drawn lines and shapes to the screen

### 23. Очистить цветом Прозрачность (%)
RULAB Очистить цветом Прозрачность (%)
ENLAB Clear with color Transparency (%)
KEYS `brick_pen_clear_color, brick_pen_clear_alpha`
PARAMS formula COLOR:brick_pen_clear_color_val; formula VALUE:brick_pen_clear_alpha_val; paldefaults "#FFFFFF", 50.0
BRICKF `catroid/src/main/java/org/catrobat/catroid/content/bricks/PenClearColorBrick.java:8`
ACT `catroid/src/main/java/org/catrobat/catroid/content/actions/PenClearColorAction.java:13 via createPenClearColorAction`
DESC Очищает холст пера указанным цветом и прозрачностью / Clears canvas with specified color and transparency
