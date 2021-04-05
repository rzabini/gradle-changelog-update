package com.github.rzabini.changelog.render

import groovy.transform.CompileStatic
import org.commonmark.node.Heading
import org.commonmark.node.Link
import org.commonmark.node.ListItem
import org.commonmark.node.Node
import org.commonmark.node.Paragraph
import org.commonmark.renderer.text.CoreTextContentNodeRenderer
import org.commonmark.renderer.text.TextContentNodeRendererContext
import org.commonmark.renderer.text.TextContentWriter

/**
 * Renders changelog according to keepachangelog format
 */
@CompileStatic
class ChangelogRenderer  extends CoreTextContentNodeRenderer {
    private final TextContentWriter textContent

    ChangelogRenderer(TextContentNodeRendererContext context) {
        super(context)
        this.textContent = context.writer
    }

    @Override
    Set<Class<? extends Node>> getNodeTypes() {
        return new HashSet<>(Arrays.asList(
                Heading,
                Paragraph,
                Link,
                ListItem
        ))
    }

    @Override
    void visit(Heading heading) {
        if (heading.level > 1) {
            newline()
        }
        (1..heading.level).each { textContent.write('#') }
        textContent.whitespace()
        visitChildren(heading)
        newline()
    }

    @Override
    void visit(Paragraph paragraph) {
        newline()
        visitChildren(paragraph)
        textContent.line()
    }

    @Override
    void visit(Link link) {
        writeLink(link, link.title, link.destination)
    }

    @Override
    void visit(ListItem listItem) {
        textContent.write('- ')
        visitChildren(listItem)
        textContent.line()
    }

    private void writeLink(Node node, String title, String destination) {
        boolean hasChild = node.firstChild != null
        boolean hasTitle = title != null && !(title == destination)
        boolean hasDestination = destination != null && !(destination == '')

        if (hasChild) {
            textContent.write('[')
            visitChildren(node)
            textContent.write(']')
            if (hasTitle || hasDestination) {
                textContent.write('(')
            }
        }

        if (hasTitle) {
            textContent.write(title)
            if (hasDestination) {
                textContent.colon()
                textContent.whitespace()
            }
        }

        if (hasDestination) {
            textContent.write(destination)
        }

        if (hasChild && (hasTitle || hasDestination)) {
            textContent.write(')')
        }
    }

    private void newline() {
        textContent.write('\n')
    }
}
