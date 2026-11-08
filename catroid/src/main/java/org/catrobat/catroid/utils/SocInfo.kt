/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2024 The Catrobat Team
 * (<http://developer.catrobat.org/credits>)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * An additional term exception under section 7 of the GNU Affero
 * General Public License, version 3, is available at
 * http://developer.catrobat.org/license_additional_term
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.catrobat.catroid.utils

import android.os.Build
import android.util.Log
import java.io.BufferedReader
import java.io.FileReader
import java.util.Locale

object SocInfo {
    private const val TAG = "SocInfo"
    private const val UNKNOWN = "Unknown"

    private val VENDOR_PREFIX = Regex("^(qcom|qualcomm|mediatek|mtk|samsung|unisoc|spreadtrum|hisilicon)[^a-z0-9]*")
    private val NON_ALNUM = Regex("[^a-z0-9]")

    @JvmStatic
    fun cpuName(): String = cpuNameValue

    @JvmStatic
    fun marketingName(): String = marketingNameValue

    private val cpuNameValue: String by lazy { resolveCpuName() }
    private val marketingNameValue: String by lazy { resolveMarketingName() }

    private fun resolveCpuName(): String {
        val model = socModel()
        if (model.isNotEmpty()) {
            val manufacturer = clean(Build.SOC_MANUFACTURER)
            return when {
                manufacturer.isEmpty() -> model
                model.startsWith(manufacturer, true) -> model
                else -> "$manufacturer $model"
            }
        }
        return listOf(hardware(), board(), cpuInfoHardware)
            .firstOrNull { it.isNotEmpty() }
            ?: UNKNOWN
    }

    private fun resolveMarketingName(): String {
        val candidates = listOf(socModel(), hardware(), board(), cpuInfoHardware)
        candidates.forEach { candidate ->
            if (candidate.isNotEmpty()) {
                lookup(candidate)?.let { return it }
            }
        }
        return candidates.firstOrNull { it.isNotEmpty() } ?: UNKNOWN
    }

    private fun socModel(): String = clean(Build.SOC_MODEL)

    private fun hardware(): String = clean(Build.HARDWARE)

    private fun board(): String = clean(Build.BOARD)

    private fun clean(value: String?): String = value.orEmpty().trim()
        .takeUnless { it.isEmpty() || it.equals(UNKNOWN, true) }
        .orEmpty()

    private val cpuInfoHardware: String by lazy { readCpuInfoHardware() }

    private fun readCpuInfoHardware(): String = try {
        BufferedReader(FileReader("/proc/cpuinfo")).use { reader ->
            reader.lineSequence()
                .firstOrNull { it.contains("hardware", true) }
                ?.substringAfter(':')
                ?.trim()
                .orEmpty()
        }
    } catch (e: Exception) {
        Log.d(TAG, "No hardware line in /proc/cpuinfo", e)
        ""
    }

    private fun lookup(value: String): String? {
        var key = normalize(value)
        while (key.isNotEmpty()) {
            SOC_MARKETING_NAMES[key]?.let { return it }
            if (!key.last().isLetter()) {
                return null
            }
            key = key.dropLast(1)
        }
        return null
    }

    private fun normalize(value: String): String = VENDOR_PREFIX
        .replace(value.lowercase(Locale.ROOT), "")
        .replace(NON_ALNUM, "")

    private val SOC_MARKETING_NAMES = mapOf(
        "mt6572" to "MT6572",
        "mt6580" to "MT6580",
        "mt6582" to "MT6582",
        "mt6592" to "MT6592",
        "mt6735" to "MT6735",
        "mt6737" to "MT6737",
        "mt6739" to "MT6739",
        "mt6750" to "MT6750",
        "mt6753" to "MT6753",
        "mt6755" to "Helio P10",
        "mt6757" to "Helio P20",
        "mt6761" to "Helio A22",
        "mt6762" to "Helio P22",
        "mt6762g" to "Helio G25",
        "mt6763" to "Helio P23",
        "mt6765" to "Helio P35",
        "mt6765g" to "Helio G35",
        "mt6765v" to "Helio G50",
        "mt6765vcb" to "Helio G35",
        "mt6765h" to "Helio G37",
        "mt6765vxaa" to "Helio G36",
        "mt6765vxba" to "Helio G36",
        "mt6768" to "Helio P65",
        "mt6769" to "Helio G70 / G80 / G81 / G85 / G88 / G91 / G92",
        "mt6769vcb" to "Helio G70",
        "mt6769t" to "Helio G80",
        "mt6769vct" to "Helio G80",
        "mt6769vcu" to "Helio G80",
        "mt6769j" to "Helio G81 Ultra",
        "mt6769l" to "Helio G81",
        "mt6769s" to "Helio G81",
        "mt6769z" to "Helio G85",
        "mt6769vcz" to "Helio G85",
        "mt6769h" to "Helio G88",
        "mt6769g" to "Helio G91",
        "mt6769k" to "Helio G91",
        "mt6769i" to "Helio G92",
        "mt6771" to "Helio P60",
        "mt6779" to "Helio P90",
        "mt6781" to "Helio G96",
        "mt6781vcd" to "Helio G96",
        "mt6785" to "Helio G90",
        "mt6785v" to "Helio G90",
        "mt6785t" to "Helio G90T",
        "mt6785vcc" to "Helio G90T",
        "mt6785vcd" to "Helio G95",
        "mt6789" to "Helio G99",
        "mt6789g" to "Helio G99",
        "mt6789u" to "Helio G99",
        "mt6789vcd" to "Helio G99",
        "mt6789h" to "Helio G100",
        "mt6789i" to "Helio G100",
        "mt6789j" to "Helio G100",
        "mt6789t" to "Helio G200",
        "mt8781" to "Helio G99",
        "mt8781vca" to "Helio G99",
        "mt8781vna" to "Helio G99",
        "mt6833" to "Dimensity 700 / 6020",
        "mt6833vza" to "Dimensity 700",
        "mt6833vnza" to "Dimensity 700",
        "mt6833p" to "Dimensity 810 / 6080",
        "mt6833gp" to "Dimensity 810 / 6080",
        "mt6835" to "Dimensity 6020",
        "mt6853" to "Dimensity 720",
        "mt6853vnza" to "Dimensity 720",
        "mt6853vtnza" to "Dimensity 800U",
        "mt6855" to "Dimensity 930",
        "mt6855g" to "Dimensity 930",
        "mt6855vaza" to "Dimensity 930",
        "mt6873" to "Dimensity 800",
        "mt6875" to "Dimensity 820",
        "mt6877" to "Dimensity 900",
        "mt6877vza" to "Dimensity 900",
        "mt6877vtza" to "Dimensity 920",
        "mt6877vttza" to "Dimensity 1080",
        "mt6879" to "Dimensity 1050",
        "mt6883" to "Dimensity 1000C",
        "mt6885" to "Dimensity 1000L",
        "mt6886" to "Dimensity 7200",
        "mt6889" to "Dimensity 1000 / 1000+",
        "mt6891" to "Dimensity 1100",
        "mt6893" to "Dimensity 1200",
        "mt6893z" to "Dimensity 1300",
        "mt6895" to "Dimensity 8000",
        "mt6895t" to "Dimensity 8100",
        "mt6896" to "Dimensity 8200",
        "mt6897" to "Dimensity 8300",
        "mt6983" to "Dimensity 9000",
        "mt6983v" to "Dimensity 9000+",
        "mt6985" to "Dimensity 9200",
        "mt6989" to "Dimensity 9300",
        "mt6989t" to "Dimensity 9300+",
        "mt6991" to "Dimensity 9400",
        "mt8163" to "MT8163",
        "mt8168" to "MT8168",
        "mt8173" to "MT8173",
        "mt8183" to "Helio P60T",
        "mt8788" to "Helio P60",

        "sm4250" to "Snapdragon 460",
        "sm4350" to "Snapdragon 480",
        "sm6115" to "Snapdragon 662",
        "sm6125" to "Snapdragon 665",
        "sm6150" to "Snapdragon 675",
        "sm6225" to "Snapdragon 680",
        "sm6250" to "Snapdragon 690",
        "sm6375" to "Snapdragon 695",
        "sm7125" to "Snapdragon 720G",
        "sm7150" to "Snapdragon 730 / 730G",
        "sm7225" to "Snapdragon 750G",
        "sm7250" to "Snapdragon 765 / 765G",
        "sm7325" to "Snapdragon 778G",
        "sm7435" to "Snapdragon 7s Gen 2",
        "sm7450" to "Snapdragon 7 Gen 1",
        "sm7475" to "Snapdragon 7+ Gen 2",
        "sm7550" to "Snapdragon 7 Gen 3",
        "sm7635" to "Snapdragon 7s Gen 3",
        "sm8150" to "Snapdragon 855",
        "sm8250" to "Snapdragon 865",
        "sm8350" to "Snapdragon 888",
        "sm8450" to "Snapdragon 8 Gen 1",
        "sm8475" to "Snapdragon 8+ Gen 1",
        "sm8550" to "Snapdragon 8 Gen 2",
        "sm8650" to "Snapdragon 8 Gen 3",
        "sm8750" to "Snapdragon 8 Elite",
        "sm8250ac" to "Snapdragon 870",
        "sdm439" to "Snapdragon 439",
        "sdm450" to "Snapdragon 450",
        "sdm632" to "Snapdragon 632",
        "sdm636" to "Snapdragon 636",
        "sdm660" to "Snapdragon 660",
        "sdm665" to "Snapdragon 665",
        "sdm670" to "Snapdragon 670",
        "sdm710" to "Snapdragon 710",
        "sdm845" to "Snapdragon 845",
        "msm8917" to "Snapdragon 425",
        "msm8937" to "Snapdragon 430 / 435",
        "msm8940" to "Snapdragon 435",
        "msm8953" to "Snapdragon 625 / 626 / 630",
        "msm8996" to "Snapdragon 820 / 821",
        "msm8998" to "Snapdragon 835",

        "exynos7870" to "Exynos 7870",
        "exynos7885" to "Exynos 7885",
        "exynos7904" to "Exynos 7904",
        "exynos850" to "Exynos 850",
        "exynos9610" to "Exynos 9610",
        "exynos9611" to "Exynos 9611",
        "exynos9810" to "Exynos 9810",
        "exynos9820" to "Exynos 9820",
        "exynos9825" to "Exynos 9825",
        "exynos9830" to "Exynos 990",
        "exynos2100" to "Exynos 2100",
        "exynos2200" to "Exynos 2200",
        "s5e8535" to "Exynos 1330",
        "s5e8825" to "Exynos 1280",
        "s5e8835" to "Exynos 1380",
        "s5e8845" to "Exynos 1480",
        "s5e8855" to "Exynos 1580",
        "s5e9925" to "Exynos 2200",
        "s5e9945" to "Exynos 2400",
        "s5e9955" to "Exynos 2500",
        "universal7420" to "Exynos 7420",
        "universal7885" to "Exynos 7885",
        "universal9610" to "Exynos 9610 / 9611",
        "universal9810" to "Exynos 9810",
        "universal9820" to "Exynos 9820",
        "universal9825" to "Exynos 9825",
        "universal9830" to "Exynos 990",
        "universal2100" to "Exynos 2100",
        "universal2200" to "Exynos 2200",

        "gs101" to "Google Tensor",
        "gs201" to "Google Tensor G2",
        "zuma" to "Google Tensor G3",
        "zumapro" to "Google Tensor G4",

        "hi3660" to "Kirin 960",
        "hi3670" to "Kirin 970",
        "kirin710" to "Kirin 710",
        "kirin710f" to "Kirin 710F",
        "kirin810" to "Kirin 810",
        "kirin970" to "Kirin 970",
        "kirin980" to "Kirin 980",
        "kirin990" to "Kirin 990",
        "kirin990e" to "Kirin 990E",
        "kirin9000s" to "Kirin 9000s",

        "sc9863a" to "SC9863A",
        "sc9832e" to "SC9832E",
        "t310" to "T310",
        "t603" to "T603",
        "t606" to "T606",
        "t610" to "T610",
        "t612" to "T612",
        "t615" to "T615",
        "t616" to "T616",
        "t618" to "T618",
        "t620" to "T620",
        "t760" to "T760",
        "ud710" to "UD710",

        "tegrax1" to "Tegra X1"
    )
}
