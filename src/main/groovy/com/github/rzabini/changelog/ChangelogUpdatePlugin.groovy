package com.github.rzabini.changelog

import com.github.rzabini.changelog.git.MessageParser
import com.github.rzabini.changelog.model.Item
import com.github.rzabini.changelog.model.Changelog
import groovy.transform.CompileDynamic
import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * Gradle plugin which updates changelog with last commit messages.
 */
@CompileDynamic
class ChangelogUpdatePlugin implements Plugin<Project> {

    public static final String CHANGELOG_FILE = 'CHANGELOG.md'

    @Override
    void apply(Project project) {
        project.task('updateChangelog') {
            doLast {
                Optional<File> changelogFile = changelogFile(project)
                if (changelogFile.present) {
                    List<Item> items = MessageParser.findRecentCommitMessages(project.rootDir, changelogFile.get())
                    if (items.size() > 0) {
                        Changelog changelog = new Changelog(changelogFile.get())
                        items.each {
                            Item item -> changelog.addItem(item.type, item.text)
                        }
                        changelogFile.get().text = changelog.render()
                    }
                }
            }
        }
    }

    private static Optional<File> changelogFile(Project project) {
        project.file(CHANGELOG_FILE).exists() ?
                Optional.of( project.file(CHANGELOG_FILE) ) :
                Optional.empty() as Optional<File>
    }

}
