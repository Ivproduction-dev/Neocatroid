package org.catrobat.catroid.neo3d;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class Neo3DCustomMaterial {

    private final String name;
    private final Map<String, float[]> vecParams = new LinkedHashMap<>();
    private final Map<String, Float> floatParams = new LinkedHashMap<>();

    public Neo3DCustomMaterial(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Neo3DCustomMaterial setVecParam(String paramName, float x, float y, float z, float w) {
        vecParams.put(paramName, new float[]{x, y, z, w});
        return this;
    }

    public Neo3DCustomMaterial setFloatParam(String paramName, float value) {
        floatParams.put(paramName, value);
        return this;
    }

    public Map<String, float[]> getVecParams() {
        return Collections.unmodifiableMap(vecParams);
    }

    public Map<String, Float> getFloatParams() {
        return Collections.unmodifiableMap(floatParams);
    }

    public static Neo3DCustomMaterial pbrFactors(float[] baseColorRgba, float metallic,
            float roughness, float[] emissiveRgb) {
        Neo3DCustomMaterial mat = new Neo3DCustomMaterial("pbrFactors");
        if (baseColorRgba != null) {
            mat.setVecParam("baseColorFactor", baseColorRgba[0], baseColorRgba[1],
                    baseColorRgba[2], baseColorRgba.length > 3 ? baseColorRgba[3] : 1f);
        }
        mat.setFloatParam("metallicFactor", metallic);
        mat.setFloatParam("roughnessFactor", roughness);
        if (emissiveRgb != null) {
            mat.setVecParam("emissiveFactor", emissiveRgb[0], emissiveRgb[1], emissiveRgb[2], 1f);
        }
        return mat;
    }
}
