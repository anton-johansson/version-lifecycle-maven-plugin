# Version lifecycle plugin

This Maven plugin aims to provide a better experience regarding version management in Maven. It does this by introducing a new lifecycle. This allows to bump major, minor or patch version numbers with a simple command while still being able to run other plugins during the creation of the version. Output generated from these plugins can easily be added and included in the release tag.


## Usage

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

This plugin has gotten a lot of inspiration from the `npm version` command, which is a great tool for managing your versions.


## Assumptions

This project tries to not asume too much about your work flow, but there are a few things that it does assume:

* You are using Git as your version control


## The lifecycle

Here is an explained table of the whole lifecycle:

| Phase                            | Task                                                                                                          |
| -------------------------------- | ------------------------------------------------------------------------------------------------------------- |
| version-validate                 | Does nothing by default. A good place to perform pre-flight checks.                                           |
| version-prepare-release          | Prepares the lifecycle by finding out the next version for the specified type.                                |
| version-prepare-release-version  | Sets the `version` attribute in the POM.                                                                      |
| version-prepare-release-tag      | Sets the `scm.tag` attribute in the POM.                                                                      |
| version-process-release          | Does nothing by default. A good place to make any pre-commit changes to files, such as generating changelog.  |
| version-commit-release           | Commits the changes for the release.                                                                          |
| version-prepare-snapshot         | Prepares the lifecycle by finding out the next development version.                                           |
| version-prepare-snapshot-version | Sets the `version` attribute in the POM.                                                                      |
| version-prepare-snapshot-tag     | Sets the `scm.tag` attribute in the POM.                                                                      |
| version-process-snapshot         | Does nothing by default. A good place to make any pre-commit changes to files.                                |
| version-commit-snapshot          | Commits the changes for the snapshot version.                                                                 |
| version                          | Does nothing. Its sole purpose is to provide a neat command to create new versions.                           |


## Signed commits

There is an issue with signed commits. JGit (the underlying library for managing Git operations) does not have support for GPG 2.2 file format. The file, `~/.gnupg/pubring.kbx` will be considered empty. A workaround for this is the following command, which will provide an older format:

```shell
$ gpg --export > ~/.gnupg/pubring.gpg
```


## Release

For releasing this project, use:

```shell
$ git checkout <tag to deploy>
$ mvn deploy -Psonatype-oss-release
```

Use `-Dgpg.keyname=<ID of key>` if you have multiple GPG keys and want to select one.


## License

Apache License © [Anton Johansson](https://anton-johansson.com)
