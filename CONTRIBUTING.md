<!---
SPDX-FileCopyrightText: 2023 JohnnyJayJay
SPDX-License-Identifier: CC-BY-4.0
-->
# Contributor Guidelines

Thank you for taking an interest in codosseum and being willing to contribute.
Before you create an issue or a pull request, make sure to review the corresponding guidelines below.

## Contributor License Agreement (CLA)

The purpose of this agreement is to ensure that we, as the project, can actually use your contribution.
By agreeing to this, you do not surrender your copyright to us.

In the following, both "work" and "contribution" refer to the modifications and additions you make to
the project's files.

When contributing any work to this project, you agree to the following terms:

1. You have the right to make this contribution, i.e., the work is yours (you own the copyright) or the work is
   licensed under terms that allow you to contribute it to the project and to agree to these terms.
2. You grant the project an appropriate license:
    1. If your contribution is a modification of an existing file, you grant the project the same license to your
       contribution that the original file is licensed under.
    2. If your contribution is source code, you grant the project the GNU Affero General Public License version 3.0
       (and any future versions) for your contribution. You may, additionally, choose to provide alternative,
       AGPL-compatible licenses for your contribution (at your discretion).
    3. If your contribution is material not included in source code files such as
       images, external configuration or prose (documentation), you grant the project an open license of your choosing.
       Examples for such licenses: [CC BY 4.0](https://spdx.org/licenses/CC-BY-4.0.html),
       [MIT](https://spdx.org/licenses/MIT.html). This will be reviewed in detail when you submit your contribution.

## Contribution Workflow

This project follows the [Gitflow](https://www.atlassian.com/git/tutorials/comparing-workflows/gitflow-workflow)
workflow. This means you never open a pull request to the `main` branch and that all your changes should branch off
`develop`.

Here are the steps you should perform if you want to make a contribution to this project:

1. [Fork](https://docs.github.com/en/get-started/quickstart/fork-a-repo) the repository to your GitHub account
2. [Clone](https://docs.github.com/en/repositories/creating-and-managing-repositories/cloning-a-repository) your forked
   repository to your computer
3. `git remote add upstream https://github.com/codosseum-org/backend`
4. `git checkout develop`
5. (Optional if you're working on your own fork) Create an appropriately named
   [feature branch](https://www.atlassian.com/git/tutorials/comparing-workflows/gitflow-workflow):
   `git branch feature/add-foo-support` or similar
6. Install [pre-commit](https://pre-commit.com/) if you haven't already
7. Run `pre-commit install`
8. Set up your editor to enforce this project's code style.
   IntelliJ has a [Checkstyle](https://plugins.jetbrains.com/plugin/1065-checkstyle-idea/) plugin that you can install.
   We follow the "Google Checks" (ships with checkstyle). To make sure you're conforming, run `./gradlew checkstyleMain`
9. Commit and push your changes (see below for [guidelines](#commit-guidelines))
10. Regularly rebase onto `upstream/develop`: `git pull -r upstream develop`
11. Implement thorough unit tests for your changes and additions – untested code will likely be rejected.
12. [Open](https://github.com/codosseum-org/backend/compare) a pull request from your branch to upstream's `develop`
    with a detailed description of what you did.

### Commit Guidelines

Your commits should be
- frequent
- atomic (i.e., only one "thing" per commit)
- descriptive
  - commit message summaries (the first line of the commit message) should give an overview but also be concise
    – if you find yourself writing something like "Add A and Fix B and Adjust C" then you should probably make multiple,
    separate commits.
  - if a more detailed explanation is of interest for your commit, feel free to elaborate as much as you want in the
    commit message's body (separated by a blank line from the summary).

Note that commit message summaries should be in English using the present tense and the imperative mode:

- ❌ "Added FooService"
- ❌ "Adds FooService"
- ✅ "Add FooService"
