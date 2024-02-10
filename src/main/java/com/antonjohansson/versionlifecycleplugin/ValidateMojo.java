package com.antonjohansson.versionlifecycleplugin;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

/**
 * Validates the project. Binds to the {@code version-validate} phase.
 */
@Mojo(name = "validate")
public class ValidateMojo extends AbstractVersionMojo
{
    @Parameter(defaultValue = "${checkForUncommittedChanges}")
    boolean checkForUncommittedChanges = true;

    @Override
    public void execute() throws MojoExecutionException
    {
        if (checkForUncommittedChanges && hasUncommittedChanges())
        {
            throw new MojoExecutionException("There are uncommitted changes");
        }
    }
}
