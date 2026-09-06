package org.catrobat.catroid.apkbuildV3

import com.reandroid.arsc.chunk.xml.AndroidManifestBlock
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class V3PermissionsTest {

    private val NAME_ATTR = 0x01010003

    private fun createEmptyManifest(): AndroidManifestBlock {
        val manifest = AndroidManifestBlock.empty()
        manifest.packageName = "org.catrobat.catroid"
        manifest.getOrCreateApplicationElement()
        return manifest
    }

    private fun permissionNames(manifest: AndroidManifestBlock): List<String> {
        return manifest.manifestElement.listElements("uses-permission")
            .map { it.searchAttributeByResourceId(NAME_ATTR)?.valueAsString ?: "" }
    }

    @Test
    fun syncPermissions_replacesTemplatePermissionsWithSelected() {
        val manifest = createEmptyManifest()
        manifest.addUsesPermission("android.permission.CAMERA")
        manifest.addUsesPermission("android.permission.RECORD_AUDIO")
        manifest.addUsesPermission("android.permission.NFC")

        V3ApkAssembler.syncPermissions(manifest, listOf("android.permission.INTERNET"))

        assertEquals(listOf("android.permission.INTERNET"), permissionNames(manifest))
    }

    @Test
    fun syncPermissions_dedupesSelectedPermissions() {
        val manifest = createEmptyManifest()

        V3ApkAssembler.syncPermissions(
            manifest,
            listOf(
                "android.permission.INTERNET",
                "android.permission.INTERNET",
                "android.permission.VIBRATE",
                "android.permission.VIBRATE"
            )
        )

        assertEquals(
            listOf("android.permission.INTERNET", "android.permission.VIBRATE"),
            permissionNames(manifest)
        )
    }

    @Test
    fun syncPermissions_emptySelectionClearsAll() {
        val manifest = createEmptyManifest()
        manifest.addUsesPermission("android.permission.CAMERA")

        V3ApkAssembler.syncPermissions(manifest, emptyList())

        assertTrue(permissionNames(manifest).isEmpty())
    }

    @Test
    fun syncPermissions_retainsDynamicReceiverPermissionIfDeclared() {
        val manifest = AndroidManifestBlock.empty()
        manifest.setPackageName("org.neocatroid.runtime.v5")
        val root = manifest.manifestElement
        val perm = root.createChildElement("permission")
        perm.getOrCreateAndroidAttribute("name", NAME_ATTR)
            .setValueAsString("org.neocatroid.runtime.v5.DYNAMIC_RECEIVER_NOT_EXPORTED_PERMISSION")

        V3ApkAssembler.syncPermissions(manifest, listOf("android.permission.INTERNET"))

        val expected = listOf("android.permission.INTERNET", "org.neocatroid.runtime.v5.DYNAMIC_RECEIVER_NOT_EXPORTED_PERMISSION")
        assertEquals(expected, permissionNames(manifest))
    }
}
