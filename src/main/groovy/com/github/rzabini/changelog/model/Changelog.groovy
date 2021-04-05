package com.github.rzabini.changelog.model

import com.github.rzabini.changelog.parse.ChangelogVisitor
import com.github.rzabini.changelog.render.ChangelogRendererFactory
import groovy.transform.CompileStatic
import org.commonmark.node.Heading
import org.commonmark.node.Paragraph
import org.commonmark.parser.Parser
import org.commonmark.renderer.text.TextContentRenderer

/**
 * Models a changelog according to keepachangelog format.
 */
@CompileStatic
class Changelog {
    Heading title
    Paragraph description
    List<Version> versions = []

    static Builder builder(File file) {
        new Builder(file)
    }

    void addVersion(Heading heading) {
        versions.add(new Version(heading: heading))
    }

    void addItemToUnreleasedVersion(Item item) {
        versions.first().addItem(item)
    }

    void addItemToUnreleasedVersion(String type, String text) {
        versions.first().addItem(new Item(type, text))
    }

    String render() {
        TextContentRenderer renderer = TextContentRenderer.builder()
                .nodeRendererFactory(new ChangelogRendererFactory())
                .build()
        StringBuilder stringBuilder = new StringBuilder()

        stringBuilder.append(renderer.render(title))

        if (description != null) {
            stringBuilder.append(renderer.render(description))
        }

        versions.each { version -> stringBuilder.append(version.render(renderer)) }

        //println stringBuilder.toString()
        stringBuilder.toString()
    }

    static class Builder {
        private final String changelogText

        Builder(File changelogFile) {
            this.changelogText = changelogFile.text
        }

        Changelog build() {
            Changelog changelog = new Changelog()
            Parser.builder().build().parse(changelogText)
                    .accept(new ChangelogVisitor(changelog: changelog))
            changelog
        }
    }
}
