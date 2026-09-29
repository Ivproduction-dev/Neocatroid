package org.catrobat.catroid.test.utils;

import org.catrobat.catroid.test.MockUtil;
import org.catrobat.catroid.utils.DeviceIntegrity;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.junit.Assert.assertEquals;

@RunWith(JUnit4.class)
public class DeviceIntegrityTest {

    @Test
    public void checksDoNotCrashWithoutAndroid() {
        assertEquals(0.0, DeviceIntegrity.isRooted(null), 0.0);
        assertEquals(0.0, DeviceIntegrity.isBootloaderUnlocked(), 0.0);
        assertEquals(0.0, DeviceIntegrity.isEmulator(), 0.0);
        assertEquals(0.0, DeviceIntegrity.isGmsInstalled(null), 0.0);
        assertEquals(0.0, DeviceIntegrity.isGmsSystemApp(null), 0.0);
        assertEquals(-1.0, DeviceIntegrity.playServicesStatus(null), 0.0);
    }

    @Test
    public void checksDoNotCrashWithMockContext() {
        DeviceIntegrity.isRooted(MockUtil.mockContextForProject());
        DeviceIntegrity.isGmsInstalled(MockUtil.mockContextForProject());
        DeviceIntegrity.isGmsSystemApp(MockUtil.mockContextForProject());
        DeviceIntegrity.playServicesStatus(MockUtil.mockContextForProject());
    }
}
