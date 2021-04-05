package com.github.rzabini.changelog.model

import groovy.transform.CompileStatic
import org.commonmark.node.BulletList
import org.commonmark.node.Heading
import org.commonmark.node.Text
import org.commonmark.renderer.text.TextContentRenderer

/**
 * Models a version according to keepachangelog format.
 */
@CompileStatic
class Version {

    Heading heading
    SortedSet<Section> sections = [] as SortedSet<Section>

    void addItem(Item item) {
        sectionForType(item.type).addItem(item)
    }

    String render(TextContentRenderer textContentRenderer) {
        StringBuilder stringBuilder = new StringBuilder()
        stringBuilder.with {
            append(textContentRenderer.render(heading))
            sections.each { Section section ->
                append(section.render(textContentRenderer))
            }
        }
        stringBuilder
    }

    private static Section newSection(String type) {
        Heading heading = new Heading(level: 3)
        heading.appendChild(new Text(type))
        heading.appendChild(new BulletList())
        new Section(heading: heading)
    }

    private Section sectionForType(String type) {
        Section typeSection = sections.find {
            Section section -> Section.ChangeType.valueOf(type) == section.type()
        }
        if (typeSection == null) {
            typeSection = newSection(type)
            sections.add(typeSection)
        }
        typeSection
    }
}
