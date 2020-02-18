# Integration tests

All integration tests in this folder has a small funky workaround. Since this plugin manipulates the Git repositroy by adding files, committing and tagging, we need to pretend that they are their own repositories. We cannot commit a repository within a repositroy, so we've renamed the `.git` directory to just `git`, and then we have a pre-step in the build that names it back to the original directory name.


## Making changes

Prepare by changing the files, so we turn it into a real repository:

```shell
$ mv git .git && mv gitignore .gitignore
```

Then make necessary changes. When committing the changes in this local repository, use `--amend` to make sure they stick to the same commit. Then clean and pack the repository by running:

```shell
$ git gc --aggressive --prune=now
$ git reflog expire --expire=now --all
```

Then move the files back to their original position:

```shell
$ mv .git git && mv .gitignore gitignore
```

You're now free to commit the changes on the root repository.
