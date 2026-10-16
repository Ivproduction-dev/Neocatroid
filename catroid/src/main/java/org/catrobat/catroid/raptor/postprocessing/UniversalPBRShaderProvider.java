package org.catrobat.catroid.raptor.postprocessing;

import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import net.mgsx.gltf.scene3d.shaders.PBRShaderConfig;
import net.mgsx.gltf.scene3d.shaders.PBRShaderProvider;

public class UniversalPBRShaderProvider extends PBRShaderProvider {
    public UniversalPBRShaderProvider(PBRShaderConfig config) {
        super(config);
    }

    @Override
    protected Shader createShader(Renderable renderable) {
        if (renderable.material.has(CustomShaderAttribute.Type)) {
            CustomShaderAttribute attr = (CustomShaderAttribute) renderable.material.get(CustomShaderAttribute.Type);
            return new CustomObjectShader(renderable, attr);
        }
        if (renderable.bones != null && config instanceof PBRShaderConfig) {
            PBRShaderConfig pbrConfig = (PBRShaderConfig) config;
            if (renderable.bones.length > pbrConfig.numBones) {
                pbrConfig.numBones = Math.min(256, renderable.bones.length);
            }
        }
        return super.createShader(renderable);
    }
}
