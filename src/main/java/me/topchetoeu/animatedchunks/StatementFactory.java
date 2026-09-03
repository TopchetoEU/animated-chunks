package me.topchetoeu.animatedchunks;

import java.util.Map;

public interface StatementFactory {
    /**
     * Returns a GLSL statement, with access to at least the following variables:
     * <ul>
     * <li>playerPos - vec3, the position of the player</li>
     * <li>chunkPos - vec3, the position of the current chunk</li>
     * <li>pos - vec3, the position of the current vertex</li>
     * <li>x - the raw stage of the animation, from 0 to 1</li>
     * <li>t - the eased stage of the animation, from 0 to 1</li>
     * <li>tmp0 - tmp7 - temporary float variables</li>
     * <li>tmp8 - tmp11 - temporary vec3 variables</li>
     * <li>tmp12 - tmp15 - temporary vec4 variables</li>
     * </ul>
     */
    String statement();
    /**
     * Returns all uniforms that are used by the statement
     */
    public default Map<String, Float> uniforms() {
        return Map.of();
    }
}
