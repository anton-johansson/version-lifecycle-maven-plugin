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
 * Commits the snapshot. Binds to the {@code version-commit-snapshot} phase.
 */
@Mojo(name = "commit-snapshot")
public class CommitSnapshotMojo extends AbstractVersionMojo
{
    @Parameter(name = "snapshotCommitMessagePattern", property = "version.snapshotCommitMessagePattern", defaultValue = "Preparing for the next development iteration")
    private String snapshotCommitMessagePattern;

    @Parameter(name = "snapshotFilePatternsToAdd", property = "version.snapshotFilePatternsToAdd", defaultValue = ".")
    private List<String> snapshotFilePatternsToAdd;

    @Override
    public void execute() throws MojoExecutionException
    {
        if (!patch)
        {
            Git repository = repository();

            String version = getVersion();
            String message = snapshotCommitMessagePattern.replace("[version]", version);

            getLog().info("Adding files to commit with patterns: " + snapshotFilePatternsToAdd);
            add(repository, snapshotFilePatternsToAdd);

            getLog().info("Committing snapshot with message: " + message);
            commit(repository, message);
        }
    }
}
