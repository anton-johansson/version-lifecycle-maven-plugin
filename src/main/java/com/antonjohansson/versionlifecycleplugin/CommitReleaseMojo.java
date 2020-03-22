/**
 * Copyright (c) Anton Johansson <hello@anton-johansson.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.antonjohansson.versionlifecycleplugin;

import java.util.List;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.eclipse.jgit.api.Git;

/**
 * Commits the release version and creates the tag. Binds to the {@code version-commit-release} phase.
 */
@Mojo(name = "commit-release")
public class CommitReleaseMojo extends AbstractVersionMojo
{
    @Parameter(name = "releaseCommitMessagePattern", property = "version.releaseCommitMessagePattern", defaultValue = "[version]")
    private String releaseCommitMessagePattern;

    @Parameter(name = "releaseFilePatternsToAdd", property = "version.releaseFilePatternsToAdd", defaultValue = ".")
    private List<String> releaseFilePatternsToAdd;

    @Override
    public void execute() throws MojoExecutionException
    {
        Git repository = repository();

        String version = getVersion();
        String tag = getTag();
        String message = releaseCommitMessagePattern.replace("[version]", version);

        getLog().info("Adding files to commit with patterns: " + releaseFilePatternsToAdd);
        add(repository, releaseFilePatternsToAdd);

        getLog().info("Committing release version with message: " + message);
        commit(repository, message);

        getLog().info("Creating tag " + tag);
        tag(repository, tag);
    }
}
