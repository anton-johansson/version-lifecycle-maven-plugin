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

import java.io.IOException;
import java.util.List;
import java.util.stream.Stream;

import org.apache.maven.model.Model;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.Mojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.components.interactivity.Prompter;
import org.codehaus.plexus.components.interactivity.PrompterException;
import org.eclipse.jgit.api.AddCommand;
import org.eclipse.jgit.api.CommitCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.errors.UnsupportedCredentialItem;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.transport.CredentialItem;
import org.eclipse.jgit.transport.CredentialItem.CharArrayType;
import org.eclipse.jgit.transport.CredentialItem.InformationalMessage;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.URIish;

/**
 * Abstract skeleton for {@link Mojo} implementations.
 */
abstract class AbstractVersionMojo extends AbstractMojo
{
    private static final String VERSION = "newVersion";
    private static final String TAG = "newTag";
    private static final String NEXT_VERSION = "com.antonjohansson.versionlifecycleplugin.NextVersion";
    private static final String RELEASE_VERSION = "com.antonjohansson.versionlifecycleplugin.ReleaseVersion";

    @Parameter(defaultValue = "${project}", required = true, readonly = true)
    private MavenProject project;

    @Component
    private Prompter prompter;

    protected Model model()
    {
        return project.getModel();
    }

    protected void setProperty(String key, String value)
    {
        project.getProperties().setProperty(key, value);
    }

    protected String getVersion()
    {
        return project.getProperties().getProperty(VERSION);
    }

    protected void setVersion(String version)
    {
        project.getProperties().setProperty(VERSION, version);
    }

    protected String getTag()
    {
        return project.getProperties().getProperty(TAG);
    }

    protected void setTag(String tag)
    {
        project.getProperties().setProperty(TAG, tag);
    }

    protected String getNextVersion()
    {
        return project.getProperties().getProperty(NEXT_VERSION);
    }

    protected void setNextVersion(String nextVersion)
    {
        project.getProperties().setProperty(NEXT_VERSION, nextVersion);
    }

    protected String getReleaseVersion()
    {
        return project.getProperties().getProperty(RELEASE_VERSION);
    }

    protected void setReleaseVersion(String releaseVersion)
    {
        project.getProperties().setProperty(RELEASE_VERSION, releaseVersion);
    }

    protected String getCurrentBranch() throws MojoExecutionException
    {
        try (Git repository = repository())
        {
            return repository.getRepository().getBranch();
        }
        catch (IOException e)
        {
            throw new MojoExecutionException("Could not retrieve current branch", e);
        }
    }

    protected Git repository() throws MojoExecutionException
    {
        try
        {
            return Git.open(project.getFile().getParentFile());
        }
        catch (IOException e)
        {
            throw new MojoExecutionException("Could not open Git repository", e);
        }
    }

    protected void add(Git repository, List<String> filePatterns) throws MojoExecutionException
    {
        AddCommand add = repository.add();
        for (String filePattern : filePatterns)
        {
            add.addFilepattern(filePattern);
        }

        try
        {
            add.call();
        }
        catch (Exception e)
        {
            throw new MojoExecutionException("Could not add files", e);
        }
    }

    protected void commit(Git repository, String message) throws MojoExecutionException
    {
        try
        {
            CommitCommand command = repository.commit();
            command.setCredentialsProvider(new PassphrasePrompter(getLog(), prompter));
            RevCommit commit = command.setMessage(message).call();
            getLog().info("Generated commit SHA " + commit.getId().getName());
        }
        catch (Exception e)
        {
            throw new MojoExecutionException("Could not commit", e);
        }
    }

    protected void tag(Git repository, String tag) throws MojoExecutionException
    {
        try
        {
            repository.tag().setName(tag).call();
        }
        catch (Exception e)
        {
            throw new MojoExecutionException("Could not create tag", e);
        }
    }
    /**
     * Prompts for GPG passphrases.
     */
    private static class PassphrasePrompter extends CredentialsProvider
    {
        private final Log log;
        private final Prompter prompter;
        private static char[] passphrase;

        private PassphrasePrompter(Log log, Prompter prompter)
        {
            this.log = log;
            this.prompter = prompter;
        }

        @Override
        public boolean supports(CredentialItem... items)
        {
            return Stream.of(items)
                    .map(Object::getClass)
                    .anyMatch(CharArrayType.class::isAssignableFrom);
        }

        @Override
        public boolean isInteractive()
        {
            return true;
        }

        @Override
        public boolean get(URIish uri, CredentialItem... items) throws UnsupportedCredentialItem
        {
            CharArrayType charArray = null;
            String promptText = null;
            for (CredentialItem item : items)
            {
                if (item instanceof InformationalMessage)
                {
                    InformationalMessage message = (InformationalMessage) item;
                    promptText = message.getPromptText();
                }
                else if (item instanceof CharArrayType)
                {
                    charArray = (CharArrayType) item;
                }
            }

            if (passphrase != null)
            {
                charArray.setValue(passphrase);
                return true;
            }

            if (promptText == null)
            {
                promptText = charArray.getPromptText();
            }

            try
            {
                passphrase = prompter.promptForPassword(promptText).toCharArray();
                charArray.setValue(passphrase);
                return true;
            }
            catch (PrompterException e)
            {
                log.warn("Could not prompt for passphrase", e);
                return false;
            }
        }
    }
}
