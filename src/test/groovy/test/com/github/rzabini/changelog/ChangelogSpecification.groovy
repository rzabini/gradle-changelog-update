package test.com.github.rzabini.changelog

import com.github.rzabini.changelog.model.Changelog
import spock.lang.Specification

class ChangelogSpecification extends Specification {

    def 'render as original'(String changelogID) {
        expect:
            renderAsOriginal(changelogID)
        where:
            changelogID << ['initial', 'with-emphasis', 'longer-description']
    }

    def 'add first "Added" item'() {
        given:
            Changelog changelog = new Changelog(changelogFile('one-item'))
        when:
            changelog.addItem('Added','second')
        then:
            changelog.render() == changelogFile('two-items').text
    }

    def 'create new section if not exists'() {
        given:
            Changelog changelog = new Changelog(changelogFile('one-item'))
        when:
            changelog.addItem('Fixed', 'second')
        then:
            changelog.render() == changelogFile('fixed-item-added').text
    }

    def 'preserve changelog of previous versions'() {
        given:
            Changelog changelog = new Changelog(changelogFile('previous-version'))
        when:
            changelog.addItem('Added', 'second')
        then:
            changelog.render() == changelogFile('previous-version-add-item').text
    }

    def 'preserve changelog of previous version when adding a "Fixed" item'() {
        given:
            Changelog changelog = new Changelog(changelogFile('previous-version'))
        when:
            changelog.addItem('Fixed', 'second')
        then:
            changelog.render() == changelogFile('previous-version-fix-item').text
    }


    def 'preserve section ordering when adding an "Added" item'() {
        given:
            Changelog changelog = new Changelog(changelogFile('one-fixed-item'))
        when:
            changelog.addItem('Added', 'second')
        then:
            changelog.render() == changelogFile('one-fixed-item-add').text
    }

    def 'preserve section ordering when adding a "Security" item'() {
        given:
            Changelog changelog = new Changelog(changelogFile('added-fixed'))
        when:
            changelog.addItem('Security', 'sec')
        then:
            changelog.render() == changelogFile('added-fixed-security').text
    }

    def 'preserve section ordering when adding a new section'() {
        given:
            Changelog changelog = new Changelog(changelogFile('added-security'))
        when:
            changelog.addItem('Fixed', 'fix')
        then:
            changelog.render() == changelogFile('added-fixed-security').text
    }

    private static File changelogFile(String changelogID) {
        new File("src/test/resources/CHANGELOG-${changelogID}.MD")
    }

    private boolean renderAsOriginal(String changelogID) {
        Changelog changelog = new Changelog(changelogFile(changelogID))
        assert changelog.render() == changelogFile(changelogID).text
        true
    }
}
