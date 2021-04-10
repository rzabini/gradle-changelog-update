package com.github.rzabini.changelog

import com.github.rzabini.changelog.git.MessageParser
import com.github.rzabini.changelog.model.Changelog
import com.github.rzabini.changelog.model.Item
import groovy.transform.CompileDynamic
import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * Gradle plugin which updates changelog with last commit message.
 */
@CompileDynamic
class ChangelogUpdatePlugin implements Plugin<Project> {
    @Override
    void apply(Project project) {
        project.task('updateChangelog') {
            doLast {
                Optional<File> changelogFile = changelogFile(project)
                if (changelogFile.isPresent()) {
                    List<Item> items = MessageParser.findRecentCommitMessages(project.rootDir, changelogFile.get())
                    if (items.size() > 0) {
                        Changelog changelog = new Changelog(changelogFile.get(), project.logger)
                        items.each {
                            Item item -> changelog.addUniqueItem(item.type, item.text)
                        }
                        changelogFile.get().text = changelog.render()
                    }
                }
            }
        }
    }

    private static Optional<File> changelogFile(Project project) {
        project.file('CHANGELOG.md').exists() ? Optional.of(project.file('CHANGELOG.md')) : Optional.empty() as Optional<File>
    }
}
