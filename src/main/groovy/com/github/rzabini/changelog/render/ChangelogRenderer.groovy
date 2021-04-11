package com.github.rzabini.changelog.render

import groovy.transform.CompileStatic
import org.commonmark.node.BulletList
import org.commonmark.node.Heading
import org.commonmark.node.Link
import org.commonmark.node.ListItem
import org.commonmark.node.Node
import org.commonmark.node.Paragraph
import org.commonmark.node.StrongEmphasis
import org.commonmark.renderer.text.CoreTextContentNodeRenderer
import org.commonmark.renderer.text.TextContentNodeRendererContext
import org.commonmark.renderer.text.TextContentWriter

/**
 * Renders changelog according to keepachangelog format
 */
@CompileStatic
class ChangelogRenderer  extends CoreTextContentNodeRenderer {
    public static final String STRONG_EMPHASIS_MARKER = '**'
    private final TextContentWriter textContent
    private boolean insideListItem = false

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
                ListItem,
                BulletList,
                StrongEmphasis
        ))
    }

    @SuppressWarnings('Instanceof')
    @Override
    void visit(Heading heading) {
        (1..heading.level).each { textContent.write('#') }
        textContent.whitespace()
        visitChildren(heading)
        aCapo(heading.level == 1 || heading.next instanceof Heading)
    }

    @Override
    void visit(Paragraph paragraph) {
        visitChildren(paragraph)
        if (!insideListItem) {
            aCapo(true)
        }
    }

    @Override
    void visit(Link link) {
        writeLink(link, link.title, link.destination)
    }

    @Override
    void visit(BulletList bulletList) {
        visitChildren(bulletList)
        if (bulletList.next) {
            aCapo(false)
        }
    }

    @Override
    void visit(ListItem listItem) {
        insideListItem = true
        textContent.write('- ')
        visitChildren(listItem)
        aCapo(false)
        insideListItem = false
    }

    @Override
    void visit(StrongEmphasis strongEmphasis) {
        textContent.write(STRONG_EMPHASIS_MARKER)
        visitChildren(strongEmphasis)
        textContent.write(STRONG_EMPHASIS_MARKER)
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

    private void aCapo(boolean blankLineSeparator) {
        newline()
        if (blankLineSeparator) {
            newline()
        }
    }
}
