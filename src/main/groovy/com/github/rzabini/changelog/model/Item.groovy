package com.github.rzabini.changelog.model

import groovy.transform.CompileStatic

/**
 * Models a commit message
 */
@CompileStatic
class Item {

    final String type
    final String text

    Item(String type, String text) {
        this.type = type
        this.text = text
    }
}
