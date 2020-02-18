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

import com.github.zafarkhaja.semver.Version;

/**
 * Validates that a type of version is specified and that the current state of the repository is OK. Binds to the {@code version-prepare-release} phase.
 */
@Mojo(name = "prepare-release")
public class PrepareReleaseMojo extends AbstractVersionMojo
{
    @Parameter(property = "major", name = "major")
    private boolean major;

    @Parameter(property = "minor", name = "minor")
    private boolean minor;

    @Parameter(property = "patch", name = "patch")
    private boolean patch;

    @Parameter(property = "tagPrefix", defaultValue = "v")
    private String tagPrefix;

    @Parameter(property = "tagSuffix")
    private String tagSuffix;

    @Parameter(property = "generateBackupPoms", defaultValue = "false")
    private boolean generateBackupPoms;

    @Override
    public void execute() throws MojoExecutionException
    {
        if (!major && !minor && !patch)
        {
            throw new MojoExecutionException("Specify either -Dmajor, -Dminor or -Dpatch");
        }

        if (major & minor || major & patch || minor & patch)
        {
            throw new MojoExecutionException("-Dmajor, -Dminor and -Dpatch are mutually exclusive");
        }

        String type = major ? "major" : minor ? "minor" : "patch";
        getLog().info("Preparing " + type + " version");

        Version version = getVersion(type);
        String tag = tagPrefix() + version + tagSuffix();
        getLog().info("Version will be " + version + " (" + tag + ")");

        Version nextVersion = version;
        if ("patch".equals(type))
        {
            nextVersion = nextVersion.incrementPatchVersion();
        }
        else
        {
            nextVersion = nextVersion.incrementMinorVersion();
        }
        nextVersion = nextVersion.setPreReleaseVersion("SNAPSHOT");
        getLog().info("Next version will be " + nextVersion.toString());

        setProperty("generateBackupPoms", String.valueOf(generateBackupPoms));
        setVersion(version.toString());
        setTag(tag);
        setNextVersion(nextVersion.toString());
    }

    private Version getVersion(String type) throws MojoExecutionException
    {
        Version current = extractVersion();
        int major = current.getMajorVersion();
        int minor = current.getMinorVersion();
        int patch = current.getPatchVersion();
        boolean snapshot = "SNAPSHOT".equals(current.getPreReleaseVersion());

        if ("major".equals(type))
        {
            checkPatch(type, patch);

            major++;
            minor = 0;
        }
        else if ("minor".equals(type))
        {
            checkPatch(type, patch);

            // For SNAPSHOTs, we assume that we're on the right minor already
            if (!snapshot)
            {
                minor++;
            }
        }
        else if ("patch".equals(type))
        {
            // For SNAPSHOTs, we assume that we're on the right patch already
            if (!snapshot)
            {
                patch++;
            }
        }

        return Version.forIntegers(major, minor, patch);
    }

    private Version extractVersion() throws MojoExecutionException
    {
        String version = model().getVersion();
        try
        {
            return Version.valueOf(version);
        }
        catch (Exception e)
        {
            throw new MojoExecutionException("Could not parse semver version number: " + version, e);
        }
    }

    private void checkPatch(String type, int patch) throws MojoExecutionException
    {
        if (patch > 0)
        {
            throw new MojoExecutionException(type + " versions cannot be built from existing patch versions");
        }
    }

    private String tagPrefix()
    {
        return tagPrefix == null ? "" : tagPrefix;
    }

    private String tagSuffix()
    {
        return tagSuffix == null ? "" : tagSuffix;
    }
}
