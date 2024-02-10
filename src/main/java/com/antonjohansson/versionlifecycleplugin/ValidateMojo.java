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
