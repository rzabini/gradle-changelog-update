package com.github.rzabini.changelog.parse

import com.github.rzabini.changelog.model.Changelog
import groovy.transform.CompileStatic
import org.commonmark.node.AbstractVisitor
import org.commonmark.node.Heading
import org.commonmark.node.Paragraph

/**
 * Parses a changelog in keepachangelog format
 */
@CompileStatic
class ChangelogVisitor extends AbstractVisitor {
    Changelog changelog

    @Override
    void visit(Heading heading) {
        if (heading.level == 1) {
            changelog.title = heading
        }
        else if (heading.level == 2) {
            changelog.addVersion(heading)
        }
    }

    @Override
    void visit(Paragraph paragraph) {
        changelog.description = paragraph
    }
}
