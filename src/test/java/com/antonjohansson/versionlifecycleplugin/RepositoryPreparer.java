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

import static org.apache.commons.io.FileUtils.moveDirectory;
import static org.apache.commons.io.FileUtils.moveFile;

import java.io.File;
import java.io.IOException;

/**
 * Prepares repositories.
 */
public class RepositoryPreparer
{
    private final File repository;

    private RepositoryPreparer(File repository)
    {
        this.repository = repository;
    }

    /**
     * Creates a new {@link RepositoryPreparer} for the given {@code path}.
     *
     * @param path the path of the repository
     * @return the preparer
     */
    public static RepositoryPreparer repository(String path)
    {
        File directory = new File(path);
        return new RepositoryPreparer(directory);
    }

    /**
     * Initializes the repository.
     */
    public void init() throws Exception
    {
        move("git");
        move("gitignore");
    }

    private void move(String fileName) throws IOException
    {
        File source = new File(repository, fileName);
        File destination = new File(repository, "." + fileName);
        if (source.isDirectory())
        {
            moveDirectory(source, destination);
        }
        else
        {
            moveFile(source, destination);
        }
    }
}
