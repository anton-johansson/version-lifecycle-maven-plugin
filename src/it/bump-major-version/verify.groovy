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

def asserter = com.antonjohansson.versionlifecycleplugin.RepositoryAsserter.repository("target/it/bump-major-version")

assert asserter.commitCount == 4
assert asserter.uncommittedChanges == 0
assert asserter.numberOfTags == 1
assert asserter.getTagNameOf(1) == "refs/tags/v2.0.0"

assert asserter.currentCommitMessage == "Preparing for the next development iteration"
assert asserter.project.version == "2.1.0-SNAPSHOT"
assert asserter.project.scm.tag == "HEAD"

asserter.reset(1)
assert asserter.currentCommitMessage == "2.0.0"
assert asserter.project.version == "2.0.0"
assert asserter.project.scm.tag == "v2.0.0"

asserter.reset(1)
assert asserter.currentCommitMessage == "Initial commit"
assert asserter.project.version == "1.5.0-SNAPSHOT"
assert asserter.project.scm.tag == "HEAD"
