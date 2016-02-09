package org.chrisle.netbeans.plugins.nbsvgpreview;

import java.io.IOException;
import java.util.Set;

/**
 *
 * @author chrl
 */
public interface IRenderable {
    String renderAsHtml(Set<RenderOption> renderOptions) throws IOException;
}