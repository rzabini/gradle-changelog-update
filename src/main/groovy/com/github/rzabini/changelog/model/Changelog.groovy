package com.github.rzabini.changelog.model

import com.github.rzabini.changelog.render.ChangelogRendererFactory
import groovy.transform.CompileStatic
import org.commonmark.node.Node
import org.commonmark.node.BulletList
import org.commonmark.node.Document
import org.commonmark.node.Heading
import org.commonmark.node.ListItem
import org.commonmark.node.Paragraph
import org.commonmark.node.Text
import org.commonmark.parser.Parser
import org.commonmark.renderer.text.TextContentRenderer
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 *  This class models a changelog in markdown format
 */
@CompileStatic
class Changelog {

    public static final String ADDED = 'Added'
    private static final List<String> TYPES = [ADDED, 'Changed', 'Deprecated', 'Removed', 'Fixed', 'Security' ]
    public static final int SECTION_LEVEL = 3
    private final Document document
    private final Logger logger

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

    Heading unreleasedSection(String type) {
        unreelasedSections().find { Heading heading -> (heading.firstChild as Text).literal == type }
    }

    void addItem(String type, String item) {
        BulletList items = ensureUnreleasedSection(type).next as BulletList
        ListItem listItem = items.firstChild as ListItem
        while (listItem != null) {
            if ((listItem.firstChild.firstChild as Text).literal == item) {
                return
            }
            listItem = listItem.next as ListItem
        }
        logger.info('adding item: {} to section {}', item, type)
        items.appendChild(newListItem(item))
    }

    private static Heading newSection(String type) {
        Heading heading = new Heading(level: SECTION_LEVEL)
        heading.appendChild(new Text(type))
        heading
    }

    private static ListItem newListItem(String item) {
        Paragraph paragraph = new Paragraph()
        paragraph.appendChild(new Text(item))
        ListItem listItem = new ListItem()
        listItem.appendChild(paragraph)
        listItem
    }

    private Heading ensureUnreleasedSection(String type) {
        Heading section = unreleasedSection(type)
        if (section == null) {
            section = addSection(type)
        }
        section
    }

    private Heading addSection(String type) {
        Heading section = newSection(type)
        previousElement(type).insertAfter(section)
        BulletList items = new BulletList()
        section.insertAfter(items)
        section
    }

    private Node previousElement(String type) {
        if (type == ADDED) {
            unreleased()
        }
        else {
            (sectionItemList(TYPES.get(TYPES.findIndexOf { it == type } - 1)))
        }
    }

    private BulletList sectionItemList(String type) {
        (unreleasedSection(type) ? unreleasedSection(type).next : previousElement(type)) as BulletList
    }

    private List<Heading> unreelasedSections() {
        List list = []
        Heading section = unreleased().next as Heading
        while (section != null && section.level == SECTION_LEVEL) {
            list << section
            section = section.next.next as Heading
        }
        list
    }

}
