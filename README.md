# Version lifecycle plugin

[![GitHub actions](https://github.com/anton-johansson/version-lifecycle-maven-plugin/actions/workflows/build.yml/badge.svg)](https://github.com/anton-johansson/version-lifecycle-maven-plugin/actions)

This Maven plugin aims to provide a better experience regarding version management in Maven. It does this by introducing a new lifecycle. This allows to bump major, minor or patch version numbers with a simple command while still being able to run other plugins during the creation of the version. Output generated from these plugins can easily be added and included in the release tag.


## Maturity

This project has reached stable and version `1.0.0` has been released.


## Usage

```xml
    <build>
        <plugins>
            <plugin>
                <groupId>com.anton-johansson</groupId>
                <artifactId>version-lifecycle-maven-plugin</artifactId>
                <version>1.0.0</version>
                <extensions>true</extensions>
            </plugin>
        </plugins>
    </build>
```

### Major versions

```shell
$ mvn version -Dmajor
```

### Minor versions

```shell
$ mvn version -Dminor
```

### Patch versions

```shell
$ mvn version -Dpatch
```


## Inspiration

This plugin has gotten a lot of inspiration from the `npm version` command, which is a great tool for managing your versions. It's also inspired by [Versions Maven Plugin](https://www.mojohaus.org/versions-maven-plugin/) and actually uses some of its goals as the default goals for some of the lifecycle phases.


## Assumptions

This project tries to not asume too much about your work flow, but it does assume a few things. It assumes that...

* ... you are using Git as your version control system.
* ... artifacts will be published either by a CI tool or manually after the release is created. This lifecycle has no relation to the `deploy` lifecycle phase.


## The lifecycle

Here is an explained table of the whole lifecycle:

| Phase                            | Task                                                                                                                    |
|----------------------------------|-------------------------------------------------------------------------------------------------------------------------|
| version-validate                 | A good place to perform pre-flight checks. By default, does not allow version change, if there are uncommitted changes. |
| version-prepare-release          | Prepares the lifecycle by finding out the next version for the specified type.                                          |
| version-prepare-release-version  | Sets the `version` attribute in the POM.                                                                                |
| version-prepare-release-tag      | Sets the `scm.tag` attribute in the POM.                                                                                |
| version-process-release          | Does nothing by default. A good place to make any pre-commit changes to files, such as generating changelog.            |
| version-commit-release           | Commits the changes for the release.                                                                                    |
| version-prepare-snapshot         | Prepares the lifecycle by finding out the next development version.                                                     |
| version-prepare-snapshot-version | Sets the `version` attribute in the POM.                                                                                |
| version-prepare-snapshot-tag     | Sets the `scm.tag` attribute in the POM.                                                                                |
| version-process-snapshot         | Does nothing by default. A good place to make any pre-commit changes to files.                                          |
| version-commit-snapshot          | Commits the changes for the snapshot version.                                                                           |
| version                          | Does nothing. Its sole purpose is to provide a neat command to create new versions.                                     |


## Parameters

| Name                           | Property                               | Default value                                  | Description                                                                                                          |
|--------------------------------|----------------------------------------|------------------------------------------------|----------------------------------------------------------------------------------------------------------------------|
| `major`                        |                                        |                                                | Triggers a major version bump. Mutually exclusive with `minor` and `patch`.                                          |
| `minor`                        |                                        |                                                | Triggers a minor version bump. Mutually exclusive with `major` and `patch`.                                          |
| `patch`                        |                                        |                                                | Triggers a patch version bump. Mutually exclusive with `major` and `minor`.                                          |
| `tagPrefix`                    | `version.tagPrefix`                    | `v`                                            | The prefix for release tags.                                                                                         |
| `tagSuffix`                    | `version.tagSuffix`                    |                                                | The suffix for release tags.                                                                                         |
| `generateBackupPoms`           | `generateBackupPoms`                   | `false`                                        | Indicates whether or not to generate backup POMs when changes are made.                                              |
| `releaseCommitMessagePattern`  | `version.releaseCommitMessagePattern`  | `[version]`                                    | The commit message to use for releases. The placeholder `[version]` will be replaced with the actual version number. |
| `snapshotCommitMessagePattern` | `version.snapshotCommitMessagePattern` | `Preparing for the next development iteration` | The commit message to use for snapshots.                                                                             |
| `releaseFilePatternsToAdd`     | `version.releaseFilePatternsToAdd`     | `.`                                            | File patterns to add to the Git index before committing the release.                                                 |
| `snapshotFilePatternsToAdd`    | `version.snapshotFilePatternsToAdd`    | `.`                                            | File patterns to add to the Git index before committing the snapshot.                                                |
| `checkForUncommittedChanges`   | `checkForUncommittedChanges`           | `true`                                         | Checks for uncommitted changes and aborts, if there are any.                                                         |


## Signed commits

There is an issue with signed commits. JGit (the underlying library for managing Git operations) does not have support for GPG 2.2 file format. The file, `~/.gnupg/pubring.kbx` will be considered empty. A workaround for this is the following command, which will provide an older format:

```shell
$ gpg --export > ~/.gnupg/pubring.gpg
```


## Release

For releasing this project, you can use the project iself to create a new tag:

```shell
$ mvn version -Dminor
```

However, we have no automatic CI pipeline that deploys the artifacts. To do that, use:

```shell
$ git checkout <tag to deploy>
$ mvn deploy -Psonatype-oss-release
```

Use `-Dgpg.keyname=<ID of key>` if you have multiple GPG keys and want to select one.


## License

Apache License © [Anton Johansson](https://anton-johansson.com)
