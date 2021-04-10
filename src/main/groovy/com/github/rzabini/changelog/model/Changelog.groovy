package com.github.rzabini.changelog.model

import com.github.rzabini.changelog.render.ChangelogRendererFactory
import org.commonmark.node.*
import org.commonmark.parser.Parser
import org.commonmark.renderer.text.TextContentRenderer
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class Changelog {

    private final Document document
    private final Logger logger
    private static final List<String> TYPES = ['Added', 'Changed', 'Deprecated', 'Removed', 'Fixed', 'Security']

    Changelog(File file) {
        this(file, LoggerFactory.getLogger(Changelog))
    }

    Changelog(File file, Logger logger) {
        document = Parser.builder().build().parse(file.text) as Document
        this.logger = logger
    }

    String render() {
        TextContentRenderer renderer = TextContentRenderer.builder()
                .nodeRendererFactory(new ChangelogRendererFactory())
                .build()
        renderer.render(document)
    }

    Heading unreleased() {
        document.firstChild.next.next as Heading
    }

    private List<Heading> unreelasedSections() {
        List list = []
        Heading section = unreleased().next as Heading
        while (section != null && section.level == 3) {
            list << section
            section = section.next.next as Heading
        }
        list
    }

    Heading unreleasedSection(String type) {
        unreelasedSections().find { Heading heading -> (heading.firstChild as Text).literal == type }
    }

    private static ListItem newListItem(String item) {
        Paragraph paragraph = new Paragraph()
        paragraph.appendChild(new Text(item))
        ListItem listItem = new ListItem()
        listItem.appendChild(paragraph)
        listItem
    }

    void addUniqueItem(String type, String item) {
        BulletList items = ensureUnreleasedSection(type).next as BulletList
        ListItem listItem = items.firstChild as ListItem
        while (listItem != null) {
            if ((listItem.firstChild.firstChild as Text).literal == item)
                return
            else listItem = listItem.next as ListItem
        }
        logger.info("adding item: {} to section {}", item, type)
        items.appendChild(newListItem(item))
    }

    private ensureUnreleasedSection(String type) {
        Heading section = unreleasedSection(type)
        if (section == null) {
            section = addSection(type)
        }
        section
    }

    private addSection(String type) {
        Heading section = newSection(type)
        previousElement(type).insertAfter(section)
        section.insertAfter(new BulletList())
        section
    }

    private previousElement(String type) {
        if (type == 'Added')
            unreleased()
        else
            sectionItemList(TYPES.get(TYPES.findIndexOf { it == type } - 1))
    }

    private sectionItemList(String type) {
        unreleasedSection(type) ? unreleasedSection(type).next : previousElement(type)
    }

    private static newSection(String type) {
        Heading heading = new Heading(level: 3)
        heading.appendChild(new Text(type))
        heading
    }
}
