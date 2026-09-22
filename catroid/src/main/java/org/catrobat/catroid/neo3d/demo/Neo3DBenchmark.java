package org.catrobat.catroid.neo3d.demo;

import org.catrobat.catroid.neo3d.Neo3DEngine;
import org.catrobat.catroid.neo3d.Neo3DScene;
import org.catrobat.catroid.neo3d.backend.INeo3DBackend;

public final class Neo3DBenchmark {

    private Neo3DBenchmark() {
    }

    public static class Report {
        public String backendName;
        public boolean backendInitialized;
        public long sceneBuildMs;
        public long modelUploadMs;
        public long validationErrors;
        public long validationWarnings;
        public int objectCount;
        public int warmupFrames;
        public int measureFrames;
        public float avgUpdateMs;
        public float minUpdateMs = Float.MAX_VALUE;
        public float maxUpdateMs;
        public float approxSpecFps;
        public long usedMemoryBytes;
        public long totalMemoryBytes;
        public long backendFrames;
        public float backendAvgFrameMs;

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("Neo3D V2 headless benchmark\n");
            sb.append("  backend: ").append(backendName)
                    .append(" initialized=").append(backendInitialized).append("\n");
            sb.append("  sceneBuildMs=").append(sceneBuildMs)
                    .append(" modelUploadMs=").append(modelUploadMs).append("\n");
            sb.append("  objects=").append(objectCount)
                    .append(" validationErrors=").append(validationErrors)
                    .append(" warnings=").append(validationWarnings).append("\n");
            sb.append("  ticks: warmup=").append(warmupFrames)
                    .append(" measured=").append(measureFrames).append("\n");
            sb.append("  spec update ms: avg=").append(String.format("%.3f", avgUpdateMs))
                    .append(" min=").append(String.format("%.3f", minUpdateMs))
                    .append(" max=").append(String.format("%.3f", maxUpdateMs)).append("\n");
            sb.append("  approxSpecFps=").append(String.format("%.1f", approxSpecFps)).append("\n");
            sb.append("  memory: used=").append(usedMemoryBytes / 1024).append("KB")
                    .append(" total=").append(totalMemoryBytes / 1024).append("KB\n");
            sb.append("  backendFrames=").append(backendFrames)
                    .append(" backendAvgFrameMs=").append(String.format("%.3f", backendAvgFrameMs));
            return sb.toString();
        }
    }

    public static Report runHeadless(Neo3DEngine engine, int warmupFrames, int measureFrames) {
        Report report = new Report();
        INeo3DBackend backend = engine.getBackend();
        report.backendName = backend.getName();
        report.backendInitialized = backend.isInitialized();

        long t0 = System.nanoTime();
        Neo3DScene scene = Neo3DDemoScene.buildDemoSpec();
        Neo3DDemoScene.ValidationResult validation = Neo3DDemoScene.validate(scene);
        long t1 = System.nanoTime();
        report.sceneBuildMs = (t1 - t0) / 1000000L;
        report.validationErrors = validation.errors.size();
        report.validationWarnings = validation.warnings.size();
        report.objectCount = scene.getObjectCount();

        engine.registerScene(scene);
        byte[] heroGlb = Neo3DEmbeddedCube.buildDefaultGlb();
        byte[] groundGlb = Neo3DEmbeddedCube.buildGlb(2f, new float[]{0.3f, 0.34f, 0.4f, 1f});
        long t2 = System.nanoTime();
        engine.setObjectModelBytes(scene.getId(),
                scene.findByName("V2_Hero").getId(), Neo3DDemoScene.HERO_MODEL_KEY, heroGlb);
        engine.setObjectModelBytes(scene.getId(),
                scene.findByName("V2_Ground").getId(), Neo3DDemoScene.GROUND_MODEL_KEY, groundGlb);
        long t3 = System.nanoTime();
        report.modelUploadMs = (t3 - t2) / 1000000L;

        report.warmupFrames = warmupFrames;
        report.measureFrames = measureFrames;
        float dt = 1f / 60f;
        for (int i = 0; i < warmupFrames; i++) {
            engine.update(scene.getId(), dt, System.nanoTime());
        }
        float sum = 0f;
        for (int i = 0; i < measureFrames; i++) {
            long f0 = System.nanoTime();
            engine.update(scene.getId(), dt, System.nanoTime());
            float ms = (System.nanoTime() - f0) / 1000000f;
            sum += ms;
            report.minUpdateMs = Math.min(report.minUpdateMs, ms);
            report.maxUpdateMs = Math.max(report.maxUpdateMs, ms);
        }
        if (measureFrames > 0) {
            report.avgUpdateMs = sum / measureFrames;
            report.approxSpecFps = report.avgUpdateMs > 0f ? 1000f / report.avgUpdateMs : 0f;
        }

        Runtime rt = Runtime.getRuntime();
        report.totalMemoryBytes = rt.totalMemory();
        report.usedMemoryBytes = rt.totalMemory() - rt.freeMemory();
        INeo3DBackend.BackendStats stats = backend.getStats();
        report.backendFrames = stats.framesRendered;
        report.backendAvgFrameMs = stats.avgFrameMs;
        return report;
    }
}
