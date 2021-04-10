package test.com.github.rzabini.changelog

import com.github.rzabini.changelog.model.Changelog
import spock.lang.Specification

class ChangelogSpecification extends Specification {

    def parse() {
        when:
            Changelog changelog = new Changelog(changelogFile('one-item'))
        then:
            changelog.render() == changelogFile('one-item').text
    }

    def add() {
        given:
            Changelog changelog = new Changelog(changelogFile('one-item'))
        when:
            changelog.addUniqueItem('Added', 'second')
        then:
            changelog.render() == changelogFile('two-items').text
    }

    def addFixed() {
        given:
            Changelog changelog = new Changelog(changelogFile('one-item'))
        when:
            changelog.addUniqueItem('Fixed', 'second')
        then:
            changelog.render() == changelogFile('fixed-item-added').text
    }

    def idempotent() {
        given:
            Changelog changelog = new Changelog(changelogFile('one-item'))
        when:
            changelog.addUniqueItem('Fixed', 'second')
            changelog.addUniqueItem('Fixed', 'second')
        then:
            changelog.render() == changelogFile('fixed-item-added').text
    }

    def addWithPreviousVersion() {
        given:
            Changelog changelog = new Changelog(changelogFile('previous-version'))
        when:
            changelog.addUniqueItem('Added', 'second')
        then:
            changelog.render() == changelogFile('previous-version-add-item').text
    }


    def fixWithPreviousVersion() {
        given:
            Changelog changelog = new Changelog(changelogFile('previous-version'))
        when:
            changelog.addUniqueItem('Fixed', 'second')
        then:
            changelog.render() == changelogFile('previous-version-fix-item').text
    }

    def "Added is always first"() {
        given:
            Changelog changelog = new Changelog(changelogFile('one-fixed-item'))
        when:
            changelog.addUniqueItem('Added', 'second')
        then:
            changelog.render() == changelogFile('one-fixed-item-add').text
    }

    def "Security always last"() {
        given:
            Changelog changelog = new Changelog(changelogFile('added-fixed'))
        when:
            changelog.addUniqueItem('Security', 'sec')
        then:
            changelog.render() == changelogFile('added-fixed-security').text
    }

    def "Fixed between Added and Security"() {
        given:
            Changelog changelog = new Changelog(changelogFile('added-security'))
        when:
            changelog.addUniqueItem('Fixed', 'fix')
        then:
            changelog.render() == changelogFile('added-fixed-security').text
    }

    private static File changelogFile(String issue) {
        new File("src/test/resources/CHANGELOG-${issue}.MD")
    }
}