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

import static org.eclipse.jgit.api.ResetCommand.ResetType.HARD;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Iterator;

import org.apache.commons.lang3.StringUtils;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.revwalk.RevCommit;

/**
 * Asserts information in Git repositories.
 */
public final class RepositoryAsserter
{
    private final Git git;

    // Prevent instantiation
    private RepositoryAsserter(Git git)
    {
        this.git = git;
    }

    /**
     * ASD
     * @param message asd
     */
    public static void log(String message)
    {
        System.err.println(message);
    }

    /**
     * Creates a new {@link RepositoryAsserter} for the given {@code path}.
     *
     * @param path the path of the repository
     * @return the asserter
     * @throws IOException if the asserter could not be created
     */
    public static RepositoryAsserter repository(String path) throws IOException
    {
        File directory = new File(path);
        Git git = Git.open(directory);
        return new RepositoryAsserter(git);
    }

    /**
     * Resets the repository a set number of commits back into the history.
     */
    public void reset(int numberOfCommitsBack) throws Exception
    {
        Iterator<RevCommit> iterator = git.log().call().iterator();
        RevCommit commit = iterator.next();
        int counter = 0;
        while (counter++ < numberOfCommitsBack)
        {
            commit = iterator.next();
        }

        git.reset()
                .setRef(commit.getId().getName())
                .setMode(HARD)
                .call();
    }

    /**
     * Gets the number of commits.
     */
    public int getCommitCount() throws Exception
    {
        int count = 0;
        for (@SuppressWarnings("unused")
        RevCommit commit : git.log().call())
        {
            count++;
        }
        return count;
    }

    /**
     * Gets the current commit message.
     */
    public String getCurrentCommitMessage() throws Exception
    {
        String message = git.log()
                .call()
                .iterator()
                .next()
                .getFullMessage();
        return StringUtils.trim(message);
    }

    /**
     * Asserts the number of uncommitted changes.
     */
    public int getUncommittedChanges() throws Exception
    {
        int count = git.status().call().getUncommittedChanges().size();
        return count;
    }

    /**
     * Returns the number of tags of the repository.
     */
    public int getNumberOfTags() throws Exception
    {
        return git.tagList().call().size();
    }

    /**
     * Gets the name of a tag at the given index.
     */
    public String getTagNameOf(int index) throws Exception
    {
        return git.tagList().call().get(index - 1).getName();
    }

    /**
     * Gets the project object model.
     */
    public Model getProject()
    {
        File projectDirectory = git.getRepository().getDirectory().getParentFile();
        File pomFile = new File(projectDirectory, "pom.xml");
        try (Reader reader = new FileReader(pomFile))
        {
            return new MavenXpp3Reader().read(reader);
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }
}
