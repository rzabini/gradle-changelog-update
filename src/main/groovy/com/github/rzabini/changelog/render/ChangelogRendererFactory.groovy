package com.github.rzabini.changelog.render

import groovy.transform.CompileStatic
import org.commonmark.renderer.NodeRenderer
import org.commonmark.renderer.text.TextContentNodeRendererContext
import org.commonmark.renderer.text.TextContentNodeRendererFactory

/**
 * Creates custom renderer.
 */
@CompileStatic
class ChangelogRendererFactory implements TextContentNodeRendererFactory {
    @Override
    NodeRenderer create(TextContentNodeRendererContext context) {
        return new ChangelogRenderer(context)
    }
}
