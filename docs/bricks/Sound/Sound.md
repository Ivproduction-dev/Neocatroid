# Sound / Звук

CAT `category_sound` FAC `setupSoundCategoryList` LINES 2036-2096
docs for bricks, order from CategoryBricksFactory

## Воспроизведение / Playback

### 1. Воспроизвести звук
RULAB Воспроизвести звук
ENLAB Start sound
KEYS `brick_play_sound`
PARAMS spinner `brick_play_sound_spinner` gen SoundInfo; src sprite-sounds
BRICKF `catroid/src/main/java/org/catrobat/catroid/content/bricks/PlaySoundBrick.java:46`
ACT `catroid/src/main/java/org/catrobat/catroid/content/actions/PlaySoundAction.kt:34 via createPlaySoundAction`
DESC Играет звук / Plays a sound

### 2. Воспроизвести звук и ждать
RULAB Воспроизвести звук и ждать
ENLAB Start sound and wait
KEYS `brick_play_sound, brick_play_sound_and_wait`
PARAMS spinner `brick_play_sound_spinner` gen SoundInfo; src sprite-sounds
BRICKF `catroid/src/main/java/org/catrobat/catroid/content/bricks/PlaySoundAndWaitBrick.java:35`
ACT `catroid/src/main/java/org/catrobat/catroid/content/actions/PlaySoundAction.kt:34 via createPlaySoundAction`; `catroid/src/main/java/org/catrobat/catroid/content/actions/WaitForSoundAction.java:28 via createWaitForSoundAction`
DESC Играет звук и ждет окончания / Plays a sound and waits for it to finish

### 3. Воспроизвести звук на секунды
RULAB Воспроизвести звук на секунды
ENLAB Start sound at seconds
KEYS `brick_play_sound, at_string, seconds_string`
PARAMS formula PLAY_SOUND_AT:brick_play_sound_at_edit_text; paldefaults BrickValues.PLAY_AT_DEFAULT_OFFSET; spinner `brick_play_sound_at_spinner` gen SoundInfo; src sprite-sounds
BRICKF `catroid/src/main/java/org/catrobat/catroid/content/bricks/PlaySoundAtBrick.java:48`
ACT `catroid/src/main/java/org/catrobat/catroid/content/actions/PlaySoundAtAction.kt:41 via createPlaySoundAtAction`
DESC Играет звук с определенного момента / Plays a sound from a specific playback position

### 4. Подготовить звук из файла как
RULAB Подготовить звук из файла как
ENLAB Prepare sound from file as
KEYS `brick_prepare_sound, brick_as`
PARAMS formula FILE_NAME:brick_prepare_sound_file_name; formula CACHE_NAME:brick_prepare_sound_cache_name; paldefaults "my_sound.mp3", "sound"
BRICKF `catroid/src/main/java/org/catrobat/catroid/content/bricks/PrepareSoundBrick.java:8`
ACT `catroid/src/main/java/org/catrobat/catroid/content/actions/PrepareSoundAction.kt:9 via createPrepareSoundAction`
DESC Подготавливает звук к воспроизведению (нужен, чтобы избежать лагов) / Prepares a sound for playback (needed to avoid lag)

### 5. Играть подготовленный звук
RULAB Играть подготовленный звук
ENLAB Play prepared sound
KEYS `brick_play_prepared_sound`
PARAMS formula CACHE_NAME:brick_play_prepared_sound_cache_name; paldefaults "sound"
BRICKF `catroid/src/main/java/org/catrobat/catroid/content/bricks/PlayPreparedSoundBrick.java:8`
ACT `catroid/src/main/java/org/catrobat/catroid/content/actions/PlayPreparedSoundAction.kt:9 via createPlayPreparedSoundAction`
DESC Играет подготовленный звук / Plays the prepared sound

### 6. Остановить звук
RULAB Остановить звук
ENLAB Stop sound
KEYS `brick_stop_sound`
PARAMS spinner `brick_stop_sound_spinner` gen SoundInfo; src sprite-sounds
BRICKF `catroid/src/main/java/org/catrobat/catroid/content/bricks/StopSoundBrick.kt:39`
ACT `catroid/src/main/java/org/catrobat/catroid/content/actions/StopSoundAction.kt:31 via createStopSoundAction`
DESC Останавливает звук / Stops a sound

### 7. Остановить все звуки
RULAB Остановить все звуки
ENLAB Stop all sounds
KEYS `brick_stop_all_sounds`
PARAMS spinner `brick_stop_all_sounds_mode_spinner` gen StringOption
BRICKF `catroid/src/main/java/org/catrobat/catroid/content/bricks/StopAllSoundsBrick.java:39`
ACT `catroid/src/main/java/org/catrobat/catroid/content/actions/Sound_StopAllAction.java:31 via createStopAllSoundsAction`
DESC Останваливает все звуки на сцене / Stops all sounds on the stage

### 8. Пауза звука
RULAB Пауза звука
ENLAB Pause sound
KEYS `brick_pause_sound`
PARAMS spinner `brick_pause_sound_spinner` gen SoundInfo; src sprite-sounds
BRICKF `catroid/src/main/java/org/catrobat/catroid/content/bricks/PauseSoundBrick.kt:17`
ACT `catroid/src/main/java/org/catrobat/catroid/content/actions/PauseSoundAction.kt:8 via createPauseSoundAction`
DESC Creates PauseSoundAction, ResumeSoundAction, SetSoundLoopAction (no BrickInfo, from code)

### 9. Продолжить звук
RULAB Продолжить звук
ENLAB Resume sound
KEYS `brick_resume_sound`
PARAMS spinner `brick_resume_sound_spinner` gen SoundInfo; src sprite-sounds
BRICKF `catroid/src/main/java/org/catrobat/catroid/content/bricks/ResumeSoundBrick.kt:17`
ACT `catroid/src/main/java/org/catrobat/catroid/content/actions/ResumeSoundAction.kt:8 via createResumeSoundAction`
DESC Creates ResumeSoundAction, SetSoundLoopAction, PlaySoundWithSpeedAction (no BrickInfo, from code)

### 10. Зациклить
RULAB Зациклить
ENLAB Set loop for
KEYS `brick_set_sound_loop`
PARAMS spinner `brick_set_sound_loop_spinner` gen SoundInfo; src sprite-sounds
BRICKF `catroid/src/main/java/org/catrobat/catroid/content/bricks/SetSoundLoopBrick.kt:19`
ACT `catroid/src/main/java/org/catrobat/catroid/content/actions/SetSoundLoopAction.kt:8 via createSetSoundLoopAction`
DESC Creates SetSoundLoopAction, PlaySoundWithSpeedAction, CrossFadeSoundAction (no BrickInfo, from code)

### 11. Играть звук со скоростью
RULAB Играть звук со скоростью
ENLAB Play sound at speed
KEYS `brick_play_sound_with_speed, brick_play_sound_with_speed_param`
PARAMS paldefaults 1.0; spinner `brick_play_sound_speed_spinner` gen SoundInfo; src sprite-sounds
BRICKF `catroid/src/main/java/org/catrobat/catroid/content/bricks/PlaySoundWithSpeedBrick.kt:18`
ACT `catroid/src/main/java/org/catrobat/catroid/content/actions/PlaySoundWithSpeedAction.kt:11 via createPlaySoundWithSpeedAction`
DESC Creates PlaySoundWithSpeedAction, CrossFadeSoundAction, SetGameVolumeAction (no BrickInfo, from code)

### 12. Кросс-фейд из в длительность (сек)
RULAB Кросс-фейд из в длительность (сек)
ENLAB Cross-fade from to duration (sec)
KEYS `brick_cross_fade_sound, brick_cross_fade_sound_to, brick_cross_fade_sound_duration`
PARAMS formula DURATION:brick_cross_fade_duration_edit; paldefaults 2.0; spinner `brick_cross_fade_from_spinner` gen SoundInfo; spinner `brick_cross_fade_to_spinner` gen SoundInfo; src sprite-sounds
BRICKF `catroid/src/main/java/org/catrobat/catroid/content/bricks/CrossFadeSoundBrick.java:22`
ACT `catroid/src/main/java/org/catrobat/catroid/content/actions/CrossFadeSoundAction.kt:10 via createCrossFadeSoundAction`
DESC Плавно переключает звук с одного файла на другой за указанное время (кросс-фейд). / Smoothly crossfades the sound from one file to another over the given duration.

## Громкость и настройки / Volume & Settings

### 13. Задать громкость %
RULAB Задать громкость %
ENLAB Set volume to %
KEYS `brick_set_volume_to, percent_symbol`
PARAMS formula VOLUME:brick_set_volume_to_edit_text; paldefaults BrickValues.SET_VOLUME_TO
BRICKF `catroid/src/main/java/org/catrobat/catroid/content/bricks/SetVolumeToBrick.java:30`
ACT `catroid/src/main/java/org/catrobat/catroid/content/actions/SetVolumeToAction.java:34 via createSetVolumeToAction`
DESC Задает глобальную громкость всех звуков / Sets the global volume of all sounds

### 14. Громкость для звука Громкость (%)
RULAB Громкость для звука Громкость (%)
ENLAB Set volume for sound Volume (%)
KEYS `brick_set_sound_volume_text, brick_set_sound_volume_volume`
PARAMS formula VOLUME:brick_set_sound_volume_edit_text; paldefaults 50.0; spinner `brick_set_sound_volume_spinner` gen SoundInfo; src sprite-sounds
BRICKF `catroid/src/main/java/org/catrobat/catroid/content/bricks/SetSoundVolumeBrick.java:23`
ACT `catroid/src/main/java/org/catrobat/catroid/content/actions/SetSoundVolumeAction.kt:11 via createSetSoundVolumeAction`
DESC Задает громкость определенного звука / Sets the volume of a specific sound

### 15. Изменить громкость на
RULAB Изменить громкость на
ENLAB Change volume by
KEYS `brick_change_volume_by`
PARAMS formula VOLUME_CHANGE:brick_change_volume_by_edit_text; paldefaults Formula(BrickValues.CHANGE_VOLUME_BY)
BRICKF `catroid/src/main/java/org/catrobat/catroid/content/bricks/ChangeVolumeByNBrick.java:30`
ACT `catroid/src/main/java/org/catrobat/catroid/content/actions/ChangeVolumeByNAction.java:34 via createChangeVolumeByNAction`
DESC Изменяет громкость всех звуков / Changes the volume of all sounds

### 16. Задать громкость игры %
RULAB Задать громкость игры %
ENLAB Set game volume to %
KEYS `brick_set_game_volume, brick_set_game_volume_percent`
PARAMS formula VOLUME:brick_set_game_volume_edit; paldefaults BrickValues.SET_VOLUME_TO
BRICKF `catroid/src/main/java/org/catrobat/catroid/content/bricks/SetGameVolumeBrick.java:8`
ACT `catroid/src/main/java/org/catrobat/catroid/content/actions/SetGameVolumeAction.kt:9 via createSetGameVolumeAction`
DESC Задаёт общую громкость игры (0–100 %). / Sets the overall game volume (0-100%).

### 17. Задать остановку звуков
RULAB Задать остановку звуков
ENLAB Set \"Stop Sounds\" to
KEYS `brick_set_stop_sounds`
PARAMS formula VALUE:brick_set_stop_sounds_edit_text; paldefaults 1
BRICKF `catroid/src/main/java/org/catrobat/catroid/content/bricks/SetStopSoundsBrick.java:32`
ACT `catroid/src/main/java/org/catrobat/catroid/content/actions/SetStopSoundsAction.kt:41 via createSetStopSoundsAction`
DESC Определяет остановку звуков, при переходе на другую сцену / Determines whether to stop sounds when switching to another scene.

## Файлы и запись / Files & Recording

### 18. Воспроизвести звук (файл в Загрузках)
RULAB Воспроизвести звук (файл в Загрузках)
ENLAB Play sound (file in Downloads)
KEYS `brick_sound_file`
PARAMS paldefaults "my_sound.mp3"
BRICKF `catroid/src/main/java/org/catrobat/catroid/content/bricks/SoundFileBrick.java:31`
ACT `catroid/src/main/java/org/catrobat/catroid/content/actions/SoundFileAction.kt:46 via createSoundFileAction`
DESC Играет звук из папки "Загрузки" у пользователя / Plays a sound file from the user's "Downloads" folder

### 19. Воспроизвести звук (файл проекта)
RULAB Воспроизвести звук (файл проекта)
ENLAB Play sound (project file)
KEYS `brick_sound_files`
PARAMS paldefaults "my_sound_FROM_PROJECT_FILES.mp3"
BRICKF `catroid/src/main/java/org/catrobat/catroid/content/bricks/SoundFilesBrick.java:31`
ACT `catroid/src/main/java/org/catrobat/catroid/content/actions/SoundFilesAction.kt:46 via createSoundFilesAction`
DESC Играет звук из файлов проекта / Plays a sound from the project's sound files

### 20. Начать запись микрофона
RULAB Начать запись микрофона
ENLAB Start microphone recording
KEYS `brick_start_mic_recording`
PARAMS noparams
BRICKF `catroid/src/main/java/org/catrobat/catroid/content/bricks/StartRecordingBrick.java:7`
ACT `catroid/src/main/java/org/catrobat/catroid/content/actions/StartRecordingAction.kt:7 via createStartRecordingAction`
DESC Начинает запись звука микрофона / Starts recording sound from the microphone

### 21. Сохранить запись (файл проекта)
RULAB Сохранить запись (файл проекта)
ENLAB Save recording (project file)
KEYS `brick_stop_recording_and_save`
PARAMS formula FILE_NAME:brick_recording_filename; paldefaults "audio.mp3"
BRICKF `catroid/src/main/java/org/catrobat/catroid/content/bricks/StopRecordingBrick.java:8`
ACT `catroid/src/main/java/org/catrobat/catroid/content/actions/StopRecordingAction.kt:10 via createStopRecordingAction`
DESC Останавливает запись звука микрофона и сохраняет ее в файлы проекта. / Stops recording sound from the microphone and saves it to project files

## Речь и микрофон / Speech & Mic

### 22. Начать слушать микрофон в отрезках по милисекунд
RULAB Начать слушать микрофон в отрезках по милисекунд
ENLAB Start listen micro in times ms
KEYS `brick_listen_micro, brick_listen_micro2`
PARAMS paldefaults "100"
BRICKF `catroid/src/main/java/org/catrobat/catroid/content/bricks/ListenMicroBrick.java:31`
ACT `catroid/src/main/java/org/catrobat/catroid/content/actions/ListenMicroAction.kt:13 via createListenMicroAction`
DESC Начинает прослушивание микрофона в течение заданного времени (в миллисекундах). ВАЖНО: требуется доступ к микрофону. / Starts listening to the microphone for the given amount of time (in milliseconds). IMPORTANT: requires microphone access.

### 23. Сказать
RULAB Сказать
ENLAB Speak
KEYS `brick_speak`
PARAMS formula SPEAK:brick_speak_edit_text; paldefaults context.getString(R.string.brick_speak_default_value)
BRICKF `catroid/src/main/java/org/catrobat/catroid/content/bricks/SpeakBrick.java:30`
ACT `catroid/src/main/java/org/catrobat/catroid/content/actions/SpeakAction.kt:32 via createSpeakAction`
DESC Синтезирует речь и произносит текст / Synthesizes speech and speaks the text
COND `if (SettingsFragment.isAISpeechSynthetizationSharedPreferenceEnabled(context)) {`

### 24. Сказать и ждать
RULAB Сказать и ждать
ENLAB Speak and wait
KEYS `brick_speak, brick_speak_and_wait`
PARAMS formula SPEAK:brick_speak_and_wait_edit_text; paldefaults context.getString(R.string.brick_speak_default_value)
BRICKF `catroid/src/main/java/org/catrobat/catroid/content/bricks/SpeakAndWaitBrick.java:31`
ACT `catroid/src/main/java/org/catrobat/catroid/content/actions/SpeakAndWaitAction.kt:37 via createSpeakAndWaitAction`
DESC Синтезирует речь, произносит текст и ждет окончания / Synthesizes speech, speaks the text and waits for completion
COND `if (SettingsFragment.isAISpeechSynthetizationSharedPreferenceEnabled(context)) {`

### 25. Сказать скорость тон
RULAB Сказать скорость тон
ENLAB Speak rate pitch
KEYS `brick_speak_with_rate, brick_speak_with_rate_rate, brick_speak_with_rate_pitch`
PARAMS formula SPEAK:brick_speak_with_rate_edit_text; formula SPEECH_RATE:brick_speak_with_rate_rate_edit_text; formula SPEECH_PITCH:brick_speak_with_rate_pitch_edit_text; paldefaults context.getString(R.string.brick_speak_default_value), 1.0, 1.0
BRICKF `catroid/src/main/java/org/catrobat/catroid/content/bricks/SpeakWithRateBrick.java:30`
ACT `catroid/src/main/java/org/catrobat/catroid/content/actions/SpeakWithRateAction.kt:33 via createSpeakWithRateAction`
DESC Creates SpeakWithRateAction, StopAllSoundsAction, Sound_StopAllAction (no BrickInfo, from code)
COND `if (SettingsFragment.isAISpeechSynthetizationSharedPreferenceEnabled(context)) {`

### 26. Спросить и сохранить озвученый ответ в
RULAB Спросить и сохранить озвученый ответ в
ENLAB Ask and store spoken answer in
KEYS `brick_ask_speech_label, brick_ask_speech_store`
PARAMS formula ASK_SPEECH_QUESTION:brick_ask_speech_question_edit_text; paldefaults context.getString(R.string.brick_ask_speech_default_question); spinner `brick_ask_speech_spinner`
BRICKF `catroid/src/main/java/org/catrobat/catroid/content/bricks/AskSpeechBrick.java:32`
ACT `catroid/src/main/java/org/catrobat/catroid/content/actions/AskSpeechAction.kt:38 via createAskSpeechAction`
DESC Включает распознование речи, чтобы спросить вопрос у пользователя / Enables speech recognition to ask the user a question
COND `if (SettingsFragment.isAISpeechRecognitionSharedPreferenceEnabled(context)) {`

### 27. Начать слушать голос и хранить сказанные слова в
RULAB Начать слушать голос и хранить сказанные слова в
ENLAB Start listening to voice and store spoken words in
KEYS `brick_start_listening_to_voice`
PARAMS spinner `brick_start_listening_spinner`
BRICKF `catroid/src/main/java/org/catrobat/catroid/content/bricks/StartListeningBrick.kt:31`
ACT `catroid/src/main/java/org/catrobat/catroid/content/actions/AskSpeechAction.kt:38 via createStartListeningAction`; `catroid/src/main/java/org/catrobat/catroid/content/actions/StartListeningAction.kt:32 via createStartListeningAction`
DESC Начинает прослушивание микрофона в отрезках по выбранному времени и сохраняет громкость в формулу / Starts listening to the microphone in intervals at the chosen time and saves the volume into a formula
COND `if (SettingsFragment.isAISpeechRecognitionSharedPreferenceEnabled(context)) {`

### 28. Задать язык прослушивания
RULAB Задать язык прослушивания
ENLAB Set listening language to
KEYS `brick_set_listening_language`
PARAMS spinner `brick_set_listening_language_spinner` gen LanguageObject
BRICKF `catroid/src/main/java/org/catrobat/catroid/content/bricks/SetListeningLanguageBrick.kt:37`
ACT `catroid/src/main/java/org/catrobat/catroid/content/actions/SetListeningLanguageAction.kt:31 via createSetListeningLanguageAction`
DESC Creates SetListeningLanguageAction, PaintNewLookAction, CopyLookAction (no BrickInfo, from code)
COND `if (SettingsFragment.isAISpeechRecognitionSharedPreferenceEnabled(context)) {`

## Музыка и инструменты / Music & Instruments

### 29. Установить инструмент
RULAB Установить инструмент
ENLAB Set instrument to
KEYS `set_instrument_to`
PARAMS spinner `set_instrument_spinner` gen PickableMusicalInstrument
BRICKF `catroid/src/main/java/org/catrobat/catroid/content/bricks/SetInstrumentBrick.java:41`
ACT `catroid/src/main/java/org/catrobat/catroid/content/actions/SetInstrumentAction.java:31 via createSetInstrumentAction`
DESC Задает музыкальный инструмент, которым будут проигрываться ноты (блок Play Note). / Sets the musical instrument used to play subsequent notes (Play Note brick).
COND `if (SettingsFragment.isAISpeechRecognitionSharedPreferenceEnabled(context)) {`

### 30. Воспроизвести ноты в течении бит
RULAB Воспроизвести ноты в течении бит
ENLAB Play note for beats
KEYS `play_note, for_string, beats`
PARAMS formula NOTE_TO_PLAY:brick_play_note_for_beats_note_edit_text; formula BEATS_TO_PLAY_NOTE:brick_play_note_for_beats_beats_edit_text; paldefaults BrickValues.DEFAULT_NOTE, BrickValues.PAUSED_BEATS_INT
BRICKF `catroid/src/main/java/org/catrobat/catroid/content/bricks/PlayNoteForBeatsBrick.java:38`
ACT `catroid/src/main/java/org/catrobat/catroid/content/actions/PlayNoteForBeatsAction.java:34 via createPlayNoteForBeatsAction`
DESC Проигрывает выбранную MIDI-ноту в течение заданного количества долей (битов). / Plays the selected MIDI note for the given number of beats.
COND `if (SettingsFragment.isAISpeechRecognitionSharedPreferenceEnabled(context)) {`

### 31. Воспроизвести барабан на каждый в течении бит
RULAB Воспроизвести барабан на каждый в течении бит
ENLAB Play drum for beats
KEYS `play_drum, for_string, beats`
PARAMS formula PLAY_DRUM:brick_play_drum_for_beats_edit_text; paldefaults BrickValues.PAUSED_BEATS_INT; spinner `play_drum_for_beats_spinner` gen PickableDrum
BRICKF `catroid/src/main/java/org/catrobat/catroid/content/bricks/PlayDrumForBeatsBrick.java:41`
ACT `catroid/src/main/java/org/catrobat/catroid/content/actions/PlayDrumForBeatsAction.java:36 via createPlayDrumForBeatsAction`
DESC Проигрывает выбранный ударный инструмент (барабан) в течение заданного количества долей (битов). / Plays the selected drum instrument for the given number of beats.

### 32. Задать темп
RULAB Задать темп
ENLAB Set tempo to
KEYS `set_tempo_to`
PARAMS formula TEMPO:brick_set_tempo_edit_text; paldefaults BrickValues.DEFAULT_TEMPO
BRICKF `catroid/src/main/java/org/catrobat/catroid/content/bricks/SetTempoBrick.java:33`
ACT `catroid/src/main/java/org/catrobat/catroid/content/actions/SetTempoAction.java:34 via createSetTempoAction`
DESC Задает темп музыки (ударов в минуту) для проигрывателя музыки. / Sets the music tempo (beats per minute) for the music player.

### 33. Изменить темп на
RULAB Изменить темп на
ENLAB Change tempo by
KEYS `change_tempo_by`
PARAMS formula TEMPO_CHANGE:brick_change_tempo_edit_text; paldefaults BrickValues.CHANGE_TEMPO
BRICKF `catroid/src/main/java/org/catrobat/catroid/content/bricks/ChangeTempoByNBrick.java:33`
ACT `catroid/src/main/java/org/catrobat/catroid/content/actions/ChangeTempoByAction.kt:32 via createChangeTempoAction`
DESC Изменяет темп (скорость) музыки на указанное число ударов в минуту. / Changes the music tempo by the specified number of beats per minute.

### 34. Остановить на бит
RULAB Остановить на бит
ENLAB Pause for beats
KEYS `pause_for, beats`
PARAMS formula BEATS_TO_PAUSE:brick_pause_for_beats_edit_text; paldefaults BrickValues.PAUSED_BEATS_FLOAT
BRICKF `catroid/src/main/java/org/catrobat/catroid/content/bricks/PauseForBeatsBrick.java:30`
ACT `catroid/src/main/java/org/catrobat/catroid/content/actions/PauseForBeatsAction.kt:34 via createPauseForBeatsAction`
DESC Приостанавливает выполнение на определенное количество долей (битов) музыки. / Pauses execution for a given number of music beats.

### 35. Воспроизвести музыку Phiro\n тон Продолжительность
RULAB Воспроизвести музыку Phiro\n тон Продолжительность
ENLAB Play Phiro music\n tone Duration
KEYS `phiro_play_tone, brick_phiro_select_tone, phiro_tone_duration`
PARAMS formula PHIRO_DURATION_IN_SECONDS:brick_phiro_play_tone_duration_edit_text; paldefaults PhiroPlayToneBrick.Tone.DO, BrickValues.PHIRO_DURATION; spinner `brick_phiro_select_tone_spinner`
BRICKF `catroid/src/main/java/org/catrobat/catroid/content/bricks/PhiroPlayToneBrick.java:38`
ACT `catroid/src/main/java/org/catrobat/catroid/content/actions/PhiroPlayToneAction.java:39 via createPhiroPlayToneActionAction`; `catroid/src/main/java/org/catrobat/catroid/content/actions/WaitAction.java:33 via createDelayAction`
DESC Проигрывает выбранную ноту (DO, RE, MI и т.д.) на роботе Phiro в течение заданного времени. ВАЖНО: требует подключения по Bluetooth. / Plays the selected note (DO, RE, MI, etc.) on the Phiro robot for the given duration. IMPORTANT: requires a Bluetooth connection.
COND `if (SettingsFragment.isPhiroSharedPreferenceEnabled(context)) {`

### 36. воспроизвести тон Заголовок
RULAB воспроизвести тон Заголовок
ENLAB play tone Title
KEYS `brick_play_tone, dialog_title`
PARAMS formula CUSTOM_PARAM_1:brick_play_tone_frequency; formula DURATION:brick_play_tone_duration; paldefaults Formula(440.0), Formula(1.0)
BRICKF `catroid/src/main/java/org/catrobat/catroid/content/bricks/PlayToneBrick.java:30`
ACT `catroid/src/main/java/org/catrobat/catroid/content/actions/PlayToneAction.kt:32 via createPlayToneAction`
DESC Воспроизводит звуковой тон с заданной частотой и длительностью / Plays a sound tone with the specified frequency and duration
COND `if (SettingsFragment.isPhiroSharedPreferenceEnabled(context)) {`

### 37. панорама звука
RULAB панорама звука
ENLAB set pan
KEYS `brick_set_pan`
PARAMS formula CUSTOM_PARAM_1:brick_set_pan_edit_text; paldefaults Formula(0.0)
BRICKF `catroid/src/main/java/org/catrobat/catroid/content/bricks/SetPanBrick.java:30`
ACT `catroid/src/main/java/org/catrobat/catroid/content/actions/SetPanAction.kt:30 via createSetPanAction`
DESC Задает панораму звука (от -1.0 лево до 1.0 право) / Sets the sound panorama (from -1.0 left to 1.0 right)
COND `if (SettingsFragment.isPhiroSharedPreferenceEnabled(context)) {`

### 38. высота тона
RULAB высота тона
ENLAB set pitch
KEYS `brick_set_pitch`
PARAMS formula PITCH:brick_set_pitch_edit_text; paldefaults Formula(1.0)
BRICKF `catroid/src/main/java/org/catrobat/catroid/content/bricks/SetPitchOnlyBrick.java:30`
ACT `catroid/src/main/java/org/catrobat/catroid/content/actions/SetPitchOnlyAction.kt:30 via createSetPitchOnlyAction`
DESC Изменяет высоту тона звука (1.0 = нормально) / Changes the sound pitch (1.0 = normal)
COND `if (SettingsFragment.isPhiroSharedPreferenceEnabled(context)) {`

### 39. плавное увеличение громкости
RULAB плавное увеличение громкости
ENLAB audio fade in
KEYS `brick_audio_fade_in`
PARAMS formula DURATION:brick_audio_fade_in_edit_text; paldefaults Formula(1.0)
BRICKF `catroid/src/main/java/org/catrobat/catroid/content/bricks/AudioFadeInBrick.java:30`
ACT `catroid/src/main/java/org/catrobat/catroid/content/actions/AudioFadeInAction.kt:10 via createAudioFadeInAction`
DESC Плавно увеличивает громкость звука от 0 до текущей / Gradually increases the sound volume from 0 to current
COND `if (SettingsFragment.isPhiroSharedPreferenceEnabled(context)) {`

### 40. плавное уменьшение громкости
RULAB плавное уменьшение громкости
ENLAB audio fade out
KEYS `brick_audio_fade_out`
PARAMS formula DURATION:brick_audio_fade_out_edit_text; paldefaults Formula(1.0)
BRICKF `catroid/src/main/java/org/catrobat/catroid/content/bricks/AudioFadeOutBrick.java:30`
ACT `catroid/src/main/java/org/catrobat/catroid/content/actions/AudioFadeOutAction.kt:10 via createAudioFadeOutAction`
DESC Плавно уменьшает громкость звука от текущей до 0 / Gradually decreases the sound volume from current to 0
COND `if (SettingsFragment.isPhiroSharedPreferenceEnabled(context)) {`

### 41. эквалайзер полоса Заголовок
RULAB эквалайзер полоса Заголовок
ENLAB equalizer band Title
KEYS `brick_equalizer_set_band, dialog_title`
PARAMS formula CUSTOM_PARAM_1:brick_equalizer_band; formula CUSTOM_PARAM_2:brick_equalizer_gain; paldefaults Formula(0), Formula(0)
BRICKF `catroid/src/main/java/org/catrobat/catroid/content/bricks/EqualizerSetBandBrick.java:30`
ACT `catroid/src/main/java/org/catrobat/catroid/content/actions/EqualizerSetBandAction.kt:32 via createEqualizerSetBandAction`
DESC Задает усиление для полосы эквалайзера / Sets the gain for an equalizer band
COND `if (SettingsFragment.isPhiroSharedPreferenceEnabled(context)) {`
