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
        File postCommitHook = new File('.git/hooks', 'post-commit')
        postCommitHook.withOutputStream { os ->
            os << '#!/bin/sh\n'
            os << './gradlew postCommit'
        }
        postCommitHook.setExecutable(true, true)

        project.task('postCommit') {
            doLast {
                Optional<Item> item = MessageParser.findItemInLastCommitMessage(project.rootDir)

                item.ifPresent {  ->
                    Changelog changelog = Changelog.builder(project.file(changelogFileName())).build()
                    changelog.addItemToUnreleasedVersion(it)
                    project.file(changelogFileName()).text = changelog.render()
                }
            }
        }
    }

    private static String changelogFileName() {
        'CHANGELOG.md'
    }
}
