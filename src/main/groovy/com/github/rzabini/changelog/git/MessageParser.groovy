package com.github.rzabini.changelog.git

import com.github.rzabini.changelog.model.Item
import groovy.transform.CompileStatic
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.lib.Constants
import org.eclipse.jgit.lib.ObjectId
import org.eclipse.jgit.storage.file.FileRepositoryBuilder

import java.util.regex.Matcher

/**
 * Parse git commit messages to find matching patterns.
 */
@CompileStatic
class MessageParser {

    static List<Item> findRecentCommitMessages(File gitDir, File changeLog) {
        Git git = new Git(new FileRepositoryBuilder().findGitDir(gitDir).build())
        ObjectId until = git.repository.resolve(Constants.HEAD)
        ObjectId since = git.log().addPath(gitDir.relativePath(changeLog)).setMaxCount(1).call().first().id

        git.log().addRange(since, until).call()
                .collect { it.shortMessage =~ /^(Added|Fixed|Changed|Deprecated|Removed|Security): (.+)$/ }
                .findAll { it.matches() }
                .collect { Matcher it -> new Item(it.group(1), it.group(2)) }
    }

}
