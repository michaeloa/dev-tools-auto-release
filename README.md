# auto-release

<!-- PROJECT SHIELDS -->
<!--
*** Add Project Shields here. Several of Elhubs systems provide shields, so why not use them to give info at a glance.
*** [TeamCity Builds][SonarQube Quality Gate][SonarQube Vulnerabilities][SonarQube bugs][SonarQube smells][SonarQube Coverage]
-->

<!-- TABLE OF CONTENTS -->
## Table of Contents

* [About](#about)
* [Getting Started](#getting-started)
  * [Prerequisites](#prerequisites)
  * [Installation](#installation)
* [Usage](#usage)
* [Testing](#testing)
* [Issues](link-to-issues)
* [Contributing](link-to-contributing-file)
* [License](link-to-license-file)
* [Owners](link-to-codeowners-file)
* [Meta](#meta)


## About

**auto-release** is a small application that automates the semantic-versioning release workflow for software projects based on software commits. It:
* Determines the version number based on the git tags in the repository,
* Parses the commit log from the last version update to determine the next version,
* Builds and tags the new version, if required,
* Publishes a new version of the repository.

**auto-release** is built to work with a trunk-based development workflow.


## Getting Started

Building the application will generate a runnable jar. 

### Prerequisites

* Java 1.8 or later.

### Installation

Build and test the application code using the gradle wrapper:

```sh
./gradlew build
```

The runnable jar produced can then be deployed where-ever you need it (typically on your CI/CD agents).

Elhub employees can download the latest build of the application from the internal artifact provider.


## Usage

To run the project on the existing repository for a gradle project, use:
```sh
java -jar auto-release.jar . -p gradle
```

The positional parameter can be used to specify the working directory to analyze. The "-p" option is used to specify which type of project that is being
analyzed.

The app works by analyzing the new version from Git, and then writes the next version into the appropriate file used by the project type it is working on. The 
file with the updated version does _not_ need to be committed to git, though you can of course do so if you prefer. Note that the actual number present in the
version file before auto-release is run, is not used by the app.

The app currently allows for the following default projects:

### Gradle

The project should contain a gradle.properties file, storing the project version in the form:
```properties
version=X.Y.Z
```

The project must include a gradle wrapper (gradlew) for building and publishing the project. It assumes that the project can be published using a "publish"
task.

### Maven

The project should contain a pom.xml file, storing the project version in the form:
```xml
<version>version=X.Y.Z</version>
```

It is assumed that the environment can run maven using the mvn command, and that the project can be built and published using a 'publish' task.


## Testing

The full suite of tests can be run using:

```sh
./gradlew test
```

## Roadmap




See the [open issues](https://jira.elhub.cloud/link-to-issues) for a list of proposed features (and known issues).


<!-- META -->
## Meta

* [Relevant Link](https://elhub.cloud)
