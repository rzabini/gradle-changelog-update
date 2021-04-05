package test.com.github.rzabini.changelog

import com.github.rzabini.changelog.model.Changelog
import com.github.rzabini.changelog.parse.ChangelogVisitor
import org.commonmark.node.Heading
import org.commonmark.node.Text
import org.commonmark.parser.Parser
import spock.lang.Specification

class ChangelogSpecification extends Specification {

    def "can render Title"() {
        when:
            Heading heading = new Heading(level: 1)
            heading.appendChild(new Text("Changelog project title"))
            Changelog changelog = new Changelog(title: heading)
        then:
            changelog.render() == '# Changelog project title\n'
    }

    def "can render a parsed changelog with title, description and empty unreleased version"() {
        given:
            String changelogText = changelogText('initial')
        when:
            Changelog changelog = new Changelog()
            Parser.builder().build().parse(changelogText).accept(new ChangelogVisitor(changelog: changelog))
        then:
            changelog.render() == changelogText
    }

    def "can render a parsed changelog after adding some items"() {
        given:
            String initialText = changelogText('initial')
            Changelog changelog = new Changelog()
            Parser.builder().build().parse(initialText).accept(new ChangelogVisitor(changelog: changelog))
        when:
            changelog.addItemToUnreleasedVersion("Changed", "A function was changed")
            changelog.addItemToUnreleasedVersion("Added", "New function added")
            changelog.addItemToUnreleasedVersion("Fixed", "Fixing a previous function")
            changelog.addItemToUnreleasedVersion("Added", "Another function added")
        then:
            changelog.render() == changelogText('after-additions')
    }

    private static changelogText(String issue) {
        new File("src/test/resources/CHANGELOG-${issue}.MD").text
    }
}
