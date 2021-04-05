package com.github.rzabini.changelog.model

import groovy.transform.CompileStatic
import org.commonmark.node.Heading
import org.commonmark.node.ListItem
import org.commonmark.node.Text
import org.commonmark.renderer.text.TextContentRenderer

/**
 * Models a section inside a version
 */
@CompileStatic
class Section implements Comparable<Section> {

    @SuppressWarnings('FieldName')
    enum ChangeType {
        Added,
        Changed,
        Deprecated,
        Removed,
        Fixed,
        Security
    }

    Heading heading
    List<ListItem> listItems = []

    @Override
    int compareTo(Section o) {
        return this.type() <=> o.type()
    }

    void addItem(Item item) {
        ListItem listItem = new ListItem()
        listItem.appendChild(new Text(item.text))
        listItems.add(listItem)
    }

    String render(TextContentRenderer textContentRenderer) {
        StringBuilder stringBuilder = new StringBuilder(textContentRenderer.render(heading))
        listItems.each { listItem ->
            stringBuilder.append(textContentRenderer.render(listItem))
        }
        stringBuilder.toString()
    }

    ChangeType type() {
        ChangeType.valueOf(((Text)heading.firstChild).literal)
    }
}
