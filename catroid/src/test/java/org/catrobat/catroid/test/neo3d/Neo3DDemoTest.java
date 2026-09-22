package org.catrobat.catroid.test.neo3d;

import org.catrobat.catroid.neo3d.Neo3DEngine;
import org.catrobat.catroid.neo3d.Neo3DScene;
import org.catrobat.catroid.neo3d.demo.Neo3DBenchmark;
import org.catrobat.catroid.neo3d.demo.Neo3DDemoScene;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(JUnit4.class)
public class Neo3DDemoTest {

    @Test
    public void demoSpecValidatesWithZeroErrors() {
        Neo3DScene scene = Neo3DDemoScene.buildDemoSpec();
        assertEquals(6, scene.getObjectCount());
        Neo3DDemoScene.ValidationResult result = Neo3DDemoScene.validate(scene);
        assertTrue("errors: " + result.errors, result.isOk());
        assertTrue("expected missing-file warnings, got: " + result.warnings,
                result.warnings.size() >= 1);
        System.out.println("V2 demo validation: " + result);
    }

    @Test
    public void headlessBenchmarkRuns() {
        Neo3DEngine engine = Neo3DEngine.create(null, Neo3DEngine.BackendType.NULL);
        Neo3DBenchmark.Report report = Neo3DBenchmark.runHeadless(engine, 5, 60);
        assertEquals(0, report.validationErrors);
        assertEquals(6, report.objectCount);
        assertEquals(5 + 60, report.backendFrames);
        assertTrue(report.avgUpdateMs >= 0f);
        assertTrue(report.approxSpecFps > 0f);
        System.out.println(report);
        engine.dispose();
    }
}
