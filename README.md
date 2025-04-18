# Grails Wrapper

#### Build Status
- [![Java CI](https://github.com/apache/grails-wrapper/actions/workflows/gradle.yml/badge.svg?event=push)](https://github.com/apache/grails-wrapper/actions/workflows/gradle.yml)

Update to the latest wrapper release based on Grails version
---

    ./grailsw update-wrapper

Versions
---

| Grails        | Wrapper                                                       |
|---------------|---------------------------------------------------------------|
| 5.0.0 - 5.3.3 | [3.0.0.M1](https://github.com/apache/grails-wrapper/releases) |
| 5.3.4 - 5.3.X | [3.1.x](https://github.com/apache/grails-wrapper/releases)    |
| 6.x.x         | [4.0.x](https://github.com/apache/grails-wrapper/releases)    |
| 7.x.x         | [7.0.x](https://github.com/apache/grails-wrapper/releases)    |

Release Process
---

- Release new version via GitHub Releases
- After GitHub action is completed and only after the new release is visible on https://central.sonatype.com/artifact/org.apache.grails/grails7-wrapper/versions
- Update the branch to trigger gradle.yml to publish the next snapshot which will update the release version on https://repository.apache.org/content/groups/snapshots/org/apache/grails/grails7-wrapper/maven-metadata.xml
- The release version will be installed locally by grails-wrapper.jar, latest is only used when release is not present

```xml
    <latest>7.0.1-SNAPSHOT</latest>
    <release>7.0.0</release>
```