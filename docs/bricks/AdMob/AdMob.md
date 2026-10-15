# AdMob / Реклама

Ключ категории: `category_admob` (`res/values/strings.xml`: "AdMob"; `res/values-ru/strings.xml`: "Реклама").
Состав и порядок — строго по `CategoryBricksFactory.kt` → `setupAdmobCategoryList` (заголовки `SubCategoryHeaderBrick` не являются блоками и пропущены).

Блоков: 17.

## 1. `AdmobEnableTestModeBrick`

| Поле | Значение |
|---|---|
| Название RU | Вкл. тестовый режим |
| Название EN | Enable test mode |
| Строки (`strings.xml` → `values-ru`) | `brick_admob_enable_test_mode`: EN="Enable test mode" / RU="Вкл. тестовый режим" |
| Параметры | — |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/AdmobEnableTestModeBrick.java:7` |
| Класс экшена (`content/actions`) | `AdmobEnableTestModeAction` (`catroid/src/main/java/org/catrobat/catroid/content/actions/AdmobEnableTestModeAction.kt:7`; via `createAdmobEnableTestModeAction`, `ActionFactory.java:6894`) |
| Описание (строго `BrickInfo.java` + код) | RU: Включает тестовый режим AdMob (показываются тестовые объявления).<br>EN: Enables AdMob test mode (test ads are shown). |
| Вызов в фабрике | `AdmobEnableTestModeBrick()` |
| Layout | `catroid/src/main/res/layout/brick_admob_enable_test_mode.xml` |

## 2. `AdmobSetAppIdBrick`

| Поле | Значение |
|---|---|
| Название RU | Указать App ID |
| Название EN | Set App ID |
| Строки (`strings.xml` → `values-ru`) | `brick_admob_set_app_id`: EN="Set App ID" / RU="Указать App ID" |
| Параметры | `AD_APP_ID` (`brick_admob_app_id_edit`) |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/AdmobSetAppIdBrick.java:8` |
| Класс экшена (`content/actions`) | `AdmobSetAppIdAction` (`catroid/src/main/java/org/catrobat/catroid/content/actions/AdmobSetAppIdAction.kt:9`; via `createAdmobSetAppIdAction`, `ActionFactory.java:6922`) |
| Описание (строго `BrickInfo.java` + код) | RU: Задает идентификатор приложения (App ID) для AdMob. ВАЖНО: должен быть указан до загрузки рекламы.<br>EN: Sets the AdMob application ID (App ID). IMPORTANT: must be set before loading ads. |
| Вызов в фабрике | `AdmobSetAppIdBrick()` |
| Layout | `catroid/src/main/res/layout/brick_admob_set_app_id.xml` |

## 3. `AdmobInitializeBrick`

| Поле | Значение |
|---|---|
| Название RU | Инициализ. AdMob |
| Название EN | Init AdMob |
| Строки (`strings.xml` → `values-ru`) | `brick_admob_initialize`: EN="Init AdMob" / RU="Инициализ. AdMob" |
| Параметры | — |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/AdmobInitializeBrick.java:7` |
| Класс экшена (`content/actions`) | `AdmobInitializeAction` (`catroid/src/main/java/org/catrobat/catroid/content/actions/AdmobInitializeAction.kt:8`; via `createAdmobInitializeAction`, `ActionFactory.java:6880`) |
| Описание (строго `BrickInfo.java` + код) | RU: Инициализирует AdMob (мобильную рекламу). ВАЖНО: вызывайте один раз при запуске проекта.<br>EN: Initializes AdMob (mobile ads). IMPORTANT: call it once when the project starts. |
| Вызов в фабрике | `AdmobInitializeBrick()` |
| Layout | `catroid/src/main/res/layout/brick_admob_initialize.xml` |

## 4. `AdmobSetBannerUnitIdBrick`

| Поле | Значение |
|---|---|
| Название RU | ID баннера |
| Название EN | Banner Unit ID |
| Строки (`strings.xml` → `values-ru`) | `brick_admob_set_banner_unit_id`: EN="Banner Unit ID" / RU="ID баннера" |
| Параметры | `AD_UNIT_ID` (`brick_admob_unit_id_edit`) |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/AdmobSetBannerUnitIdBrick.java:8` |
| Класс экшена (`content/actions`) | `AdmobSetBannerUnitIdAction` (`catroid/src/main/java/org/catrobat/catroid/content/actions/AdmobSetBannerUnitIdAction.kt:8`; via `createAdmobSetBannerUnitIdAction`, `ActionFactory.java:6945`) |
| Описание (строго `BrickInfo.java` + код) | RU: Задает идентификатор рекламного блока (Unit ID) для баннерной рекламы AdMob.<br>EN: Sets the ad unit ID for the AdMob banner ad. |
| Вызов в фабрике | `AdmobSetBannerUnitIdBrick()` |
| Layout | `catroid/src/main/res/layout/brick_admob_set_banner_unit_id.xml` |

## 5. `AdmobLoadBannerBrick`

| Поле | Значение |
|---|---|
| Название RU | Загрузить баннер |
| Название EN | Load banner |
| Строки (`strings.xml` → `values-ru`) | `brick_admob_load_banner`: EN="Load banner" / RU="Загрузить баннер" |
| Параметры | — |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/AdmobLoadBannerBrick.java:7` |
| Класс экшена (`content/actions`) | `AdmobLoadBannerAction` (`catroid/src/main/java/org/catrobat/catroid/content/actions/AdmobLoadBannerAction.kt:8`; via `createAdmobLoadBannerAction`, `ActionFactory.java:6901`) |
| Описание (строго `BrickInfo.java` + код) | RU: Загружает баннерную рекламу AdMob. ВАЖНО: сначала задайте App ID и Banner Unit ID.<br>EN: Loads an AdMob banner ad. IMPORTANT: set the App ID and Banner Unit ID first. |
| Вызов в фабрике | `AdmobLoadBannerBrick()` |
| Layout | `catroid/src/main/res/layout/brick_admob_load_banner.xml` |

## 6. `AdmobShowBannerBrick`

| Поле | Значение |
|---|---|
| Название RU | Показать баннер |
| Название EN | Show banner |
| Строки (`strings.xml` → `values-ru`) | `brick_admob_show_banner`: EN="Show banner" / RU="Показать баннер" |
| Параметры | `AD_POSITION` (`brick_admob_position_edit`) |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/AdmobShowBannerBrick.java:8` |
| Класс экшена (`content/actions`) | `AdmobShowBannerAction` (`catroid/src/main/java/org/catrobat/catroid/content/actions/AdmobShowBannerAction.kt:9`; via `createAdmobShowBannerAction`, `ActionFactory.java:6990`) |
| Описание (строго `BrickInfo.java` + код) | RU: Показывает баннерную рекламу AdMob в указанной позиции. ВАЖНО: баннер нужно сначала загрузить.<br>EN: Shows an AdMob banner ad at the specified position. IMPORTANT: the banner must be loaded first. |
| Вызов в фабрике | `AdmobShowBannerBrick()` |
| Layout | `catroid/src/main/res/layout/brick_admob_show_banner.xml` |

## 7. `AdmobHideBannerBrick`

| Поле | Значение |
|---|---|
| Название RU | Скрыть баннер |
| Название EN | Hide banner |
| Строки (`strings.xml` → `values-ru`) | `brick_admob_hide_banner`: EN="Hide banner" / RU="Скрыть баннер" |
| Параметры | — |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/AdmobHideBannerBrick.java:7` |
| Класс экшена (`content/actions`) | `AdmobHideBannerAction` (`catroid/src/main/java/org/catrobat/catroid/content/actions/AdmobHideBannerAction.kt:7`; via `createAdmobHideBannerAction`, `ActionFactory.java:6887`) |
| Описание (строго `BrickInfo.java` + код) | RU: Скрывает баннер с экрана (без уничтожения).<br>EN: Hides the banner from the screen (without destroying it). |
| Вызов в фабрике | `AdmobHideBannerBrick()` |
| Layout | `catroid/src/main/res/layout/brick_admob_hide_banner.xml` |

## 8. `AdmobDestroyBannerBrick`

| Поле | Значение |
|---|---|
| Название RU | Удалить баннер |
| Название EN | Destroy banner |
| Строки (`strings.xml` → `values-ru`) | `brick_admob_destroy_banner`: EN="Destroy banner" / RU="Удалить баннер" |
| Параметры | — |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/AdmobDestroyBannerBrick.java:7` |
| Класс экшена (`content/actions`) | `AdmobDestroyBannerAction` (`catroid/src/main/java/org/catrobat/catroid/content/actions/AdmobDestroyBannerAction.kt:7`; via `createAdmobDestroyBannerAction`, `ActionFactory.java:6873`) |
| Описание (строго `BrickInfo.java` + код) | RU: Уничтожает (удаляет) баннер с экрана, освобождая ресурсы.<br>EN: Destroys (removes) the banner from the screen, freeing its resources. |
| Вызов в фабрике | `AdmobDestroyBannerBrick()` |
| Layout | `catroid/src/main/res/layout/brick_admob_destroy_banner.xml` |

## 9. `AdmobSetInterstitialUnitIdBrick`

| Поле | Значение |
|---|---|
| Название RU | ID межстр. рекламы |
| Название EN | Interstitial Unit ID |
| Строки (`strings.xml` → `values-ru`) | `brick_admob_set_interstitial_unit_id`: EN="Interstitial Unit ID" / RU="ID межстр. рекламы" |
| Параметры | `AD_UNIT_ID` (`brick_admob_unit_id_edit`) |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/AdmobSetInterstitialUnitIdBrick.java:8` |
| Класс экшена (`content/actions`) | `AdmobSetInterstitialUnitIdAction` (`catroid/src/main/java/org/catrobat/catroid/content/actions/AdmobSetInterstitialUnitIdAction.kt:8`; via `createAdmobSetInterstitialUnitIdAction`, `ActionFactory.java:6961`) |
| Описание (строго `BrickInfo.java` + код) | RU: Задает идентификатор рекламного блока (Unit ID) для полноэкранной (interstitial) рекламы AdMob.<br>EN: Sets the ad unit ID for the AdMob interstitial ad. |
| Вызов в фабрике | `AdmobSetInterstitialUnitIdBrick()` |
| Layout | `catroid/src/main/res/layout/brick_admob_set_interstitial_unit_id.xml` |

## 10. `AdmobLoadInterstitialBrick`

| Поле | Значение |
|---|---|
| Название RU | Загрузить межстр. |
| Название EN | Load interstitial |
| Строки (`strings.xml` → `values-ru`) | `brick_admob_load_interstitial`: EN="Load interstitial" / RU="Загрузить межстр." |
| Параметры | — |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/AdmobLoadInterstitialBrick.java:7` |
| Класс экшена (`content/actions`) | `AdmobLoadInterstitialAction` (`catroid/src/main/java/org/catrobat/catroid/content/actions/AdmobLoadInterstitialAction.kt:8`; via `createAdmobLoadInterstitialAction`, `ActionFactory.java:6908`) |
| Описание (строго `BrickInfo.java` + код) | RU: Загружает полноэкранную (interstitial) рекламу AdMob. ВАЖНО: сначала задайте App ID и Interstitial Unit ID.<br>EN: Loads an AdMob interstitial ad. IMPORTANT: set the App ID and Interstitial Unit ID first. |
| Вызов в фабрике | `AdmobLoadInterstitialBrick()` |
| Layout | `catroid/src/main/res/layout/brick_admob_load_interstitial.xml` |

## 11. `AdmobShowInterstitialBrick`

| Поле | Значение |
|---|---|
| Название RU | Показать межстр. |
| Название EN | Show interstitial |
| Строки (`strings.xml` → `values-ru`) | `brick_admob_show_interstitial`: EN="Show interstitial" / RU="Показать межстр." |
| Параметры | — |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/AdmobShowInterstitialBrick.java:7` |
| Класс экшена (`content/actions`) | `AdmobShowInterstitialAction` (`catroid/src/main/java/org/catrobat/catroid/content/actions/AdmobShowInterstitialAction.kt:8`; via `createAdmobShowInterstitialAction`, `ActionFactory.java:6969`) |
| Описание (строго `BrickInfo.java` + код) | RU: Показывает полноэкранную межстраничную рекламу AdMob (Interstitial). ВАЖНО: её нужно сначала загрузить.<br>EN: Shows an AdMob interstitial full-screen ad. IMPORTANT: the interstitial must be loaded first. |
| Вызов в фабрике | `AdmobShowInterstitialBrick()` |
| Layout | `catroid/src/main/res/layout/brick_admob_show_interstitial.xml` |

## 12. `AdmobSetRewardedUnitIdBrick`

| Поле | Значение |
|---|---|
| Название RU | ID награды |
| Название EN | Rewarded Unit ID |
| Строки (`strings.xml` → `values-ru`) | `brick_admob_set_rewarded_unit_id`: EN="Rewarded Unit ID" / RU="ID награды" |
| Параметры | `AD_UNIT_ID` (`brick_admob_unit_id_edit`) |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/AdmobSetRewardedUnitIdBrick.java:8` |
| Класс экшена (`content/actions`) | `AdmobSetRewardedUnitIdAction` (`catroid/src/main/java/org/catrobat/catroid/content/actions/AdmobSetRewardedUnitIdAction.kt:8`; via `createAdmobSetRewardedUnitIdAction`, `ActionFactory.java:6953`) |
| Описание (строго `BrickInfo.java` + код) | RU: Задает идентификатор рекламного блока (Rewarded Unit ID) для наградной рекламы AdMob.<br>EN: Sets the ad unit ID (Rewarded Unit ID) used for AdMob rewarded ads. |
| Вызов в фабрике | `AdmobSetRewardedUnitIdBrick()` |
| Layout | `catroid/src/main/res/layout/brick_admob_set_rewarded_unit_id.xml` |

## 13. `AdmobLoadRewardedBrick`

| Поле | Значение |
|---|---|
| Название RU | Загрузить награду |
| Название EN | Load rewarded |
| Строки (`strings.xml` → `values-ru`) | `brick_admob_load_rewarded`: EN="Load rewarded" / RU="Загрузить награду" |
| Параметры | — |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/AdmobLoadRewardedBrick.java:7` |
| Класс экшена (`content/actions`) | `AdmobLoadRewardedAction` (`catroid/src/main/java/org/catrobat/catroid/content/actions/AdmobLoadRewardedAction.kt:8`; via `createAdmobLoadRewardedAction`, `ActionFactory.java:6930`) |
| Описание (строго `BrickInfo.java` + код) | RU: Загружает вознаграждаемую (rewarded) рекламу AdMob. ВАЖНО: сначала задайте App ID и Rewarded Unit ID.<br>EN: Loads an AdMob rewarded ad. IMPORTANT: set the App ID and Rewarded Unit ID first. |
| Вызов в фабрике | `AdmobLoadRewardedBrick()` |
| Layout | `catroid/src/main/res/layout/brick_admob_load_rewarded.xml` |

## 14. `AdmobShowRewardedBrick`

| Поле | Значение |
|---|---|
| Название RU | Показать награду |
| Название EN | Show rewarded |
| Строки (`strings.xml` → `values-ru`) | `brick_admob_show_rewarded`: EN="Show rewarded" / RU="Показать награду" |
| Параметры | — |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/AdmobShowRewardedBrick.java:7` |
| Класс экшена (`content/actions`) | `AdmobShowRewardedAction` (`catroid/src/main/java/org/catrobat/catroid/content/actions/AdmobShowRewardedAction.kt:8`; via `createAdmobShowRewardedAction`, `ActionFactory.java:6976`) |
| Описание (строго `BrickInfo.java` + код) | RU: Показывает наградную рекламу AdMob (Rewarded), за просмотр которой пользователь получает вознаграждение. ВАЖНО: рекламу нужно сначала загрузить.<br>EN: Shows an AdMob rewarded ad that gives the user a reward for watching. IMPORTANT: the ad must be loaded first. |
| Вызов в фабрике | `AdmobShowRewardedBrick()` |
| Layout | `catroid/src/main/res/layout/brick_admob_show_rewarded.xml` |

## 15. `AdmobSetAppOpenUnitIdBrick`

| Поле | Значение |
|---|---|
| Название RU | ID App Open |
| Название EN | App Open Unit ID |
| Строки (`strings.xml` → `values-ru`) | `brick_admob_set_app_open_unit_id`: EN="App Open Unit ID" / RU="ID App Open" |
| Параметры | `AD_UNIT_ID` (`brick_admob_unit_id_edit`) |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/AdmobSetAppOpenUnitIdBrick.java:8` |
| Класс экшена (`content/actions`) | `AdmobSetAppOpenUnitIdAction` (`catroid/src/main/java/org/catrobat/catroid/content/actions/AdmobSetAppOpenUnitIdAction.kt:8`; via `createAdmobSetAppOpenUnitIdAction`, `ActionFactory.java:6937`) |
| Описание (строго `BrickInfo.java` + код) | RU: Задает идентификатор рекламного блока (Unit ID) для рекламы типа App Open.<br>EN: Sets the ad unit ID for the AdMob App Open ad. |
| Вызов в фабрике | `AdmobSetAppOpenUnitIdBrick()` |
| Layout | `catroid/src/main/res/layout/brick_admob_set_app_open_unit_id.xml` |

## 16. `AdmobLoadAppOpenBrick`

| Поле | Значение |
|---|---|
| Название RU | Загрузить App Open |
| Название EN | Load App Open |
| Строки (`strings.xml` → `values-ru`) | `brick_admob_load_app_open`: EN="Load App Open" / RU="Загрузить App Open" |
| Параметры | — |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/AdmobLoadAppOpenBrick.java:7` |
| Класс экшена (`content/actions`) | `AdmobLoadAppOpenAction` (`catroid/src/main/java/org/catrobat/catroid/content/actions/AdmobLoadAppOpenAction.kt:8`; via `createAdmobLoadAppOpenAction`, `ActionFactory.java:6915`) |
| Описание (строго `BrickInfo.java` + код) | RU: Загружает рекламу типа App Open (открытие приложения) AdMob. ВАЖНО: сначала задайте App ID и App Open Unit ID.<br>EN: Loads an AdMob App Open ad. IMPORTANT: set the App ID and App Open Unit ID first. |
| Вызов в фабрике | `AdmobLoadAppOpenBrick()` |
| Layout | `catroid/src/main/res/layout/brick_admob_load_app_open.xml` |

## 17. `AdmobShowAppOpenBrick`

| Поле | Значение |
|---|---|
| Название RU | Показать App Open |
| Название EN | Show App Open |
| Строки (`strings.xml` → `values-ru`) | `brick_admob_show_app_open`: EN="Show App Open" / RU="Показать App Open" |
| Параметры | — |
| Класс брика | `catroid/src/main/java/org/catrobat/catroid/content/bricks/AdmobShowAppOpenBrick.java:7` |
| Класс экшена (`content/actions`) | `AdmobShowAppOpenAction` (`catroid/src/main/java/org/catrobat/catroid/content/actions/AdmobShowAppOpenAction.kt:8`; via `createAdmobShowAppOpenAction`, `ActionFactory.java:6983`) |
| Описание (строго `BrickInfo.java` + код) | RU: Показывает полноэкранную рекламу App Open (при открытии приложения) AdMob.<br>EN: Shows an AdMob App Open full-screen ad (displayed when the app opens). |
| Вызов в фабрике | `AdmobShowAppOpenBrick()` |
| Layout | `catroid/src/main/res/layout/brick_admob_show_app_open.xml` |
