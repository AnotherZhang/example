# Opencraft

This is the repository of the Opencraft server.
Opencraft is a fork of [Glowstone](https://github.com/GlowstoneMC/Glowstone).

![](https://atlarge.ewi.tudelft.nl/gitlab/opencraft/opencraft/badges/development/pipeline.svg)

## Quick start

### Requirements

- Minecraft: Java Edition version `1.12.2`

### Running with Docker for production

1. Build Docker image

    ```sh
    cd path/to/opencraft-dev
    docker build . -t opencraft
    ```

2. Run the image on port 25565

    ```sh
    docker run -p 25565:25565 --name opencraft opencraft
    ```

3. You can now connect to the server on `localhost:25565` in your Minecraft client.

    In case you're running **Docker in WSL2**, first find your VM's IP address by running the following in Powershell:
    ```sh
    wsl -- ip -o -4 -json addr list eth0 `
    | ConvertFrom-Json `
    | %{ $_.addr_info.local } `
    | ?{ $_ }
    ```
    And replace `localhost` with this IP, e.g. `192.168.12.34:25565`.

### Running with Docker for development

We can use the `builder` stage in the multi-stage Dockerfile as our development environment. 

This can be useful in order to ensure a consistent and tested development environment that already includes all the required software (e.g. JDK version, Gradle, dependencies) for running the Opencraft server, without having to install/configure this yourself manually on your local machine.

In order to do this we do the following:

1. Build the Dockerfile up until the `builder` stage via:

    ```sh
    docker build --target builder -t opencraft-dev-env .
    ```

2. Run the image with a bind mount to your local project

    ```sh
    docker run -it --rm \
    --mount type=bind,source="$(pwd)",target=/usr/src/opencraft \
    -p 25565:25565 \
    --name opencraft-dev-env \
    --entrypoint=/bin/bash \
    opencraft-dev-env
    ```

3. Run the server using Gradle's [application](https://docs.gradle.org/current/userguide/application_plugin.html) plugin.

    ```sh
    ./gradlew run
    ```

4. You can now connect to the server on `localhost:25565` in your Minecraft client.

    In case you're running **Docker in WSL2**, first find your VM's IP address by running the following in Powershell:
    ```sh
    wsl -- ip -o -4 -json addr list eth0 `
    | ConvertFrom-Json `
    | %{ $_.addr_info.local } `
    | ?{ $_ }
    ```
    And replace `localhost` with this IP, e.g. `192.168.12.34:25565`.

> See [Troubleshooting IntelliJ IDEA](#troubleshooting-intellij-idea) for troubleshooting some common issues in IntelliJ IDEA.

# Technical Documentation

* [Serverless Terrain Generation](docs/serverless-terrain-generation.md)
* [Pub-sub Messaging System](docs/pubsub.md)

# Changing the Code

We use [git flow](https://www.atlassian.com/git/tutorials/comparing-workflows/gitflow-workflow) to create and maintain a clean commit history.

If you're adding a new feature, please create a feature branch.

```
git checkout dev
git pull origin/dev
git flow feature start
```

Once you have completed your feature, you can make a merge request to merge it into `dev`. If you have not yet pushed your branch to the Opencraft repository, you will have to do that now. You can do that by issuing the following git command:

```bash
git push -u origin <your-local-branch-name-here>
```

Once that is completed a merge request can be created using the GitLab interface.

# Troubleshooting IntelliJ IDEA

The following section describes some issues that can occur when opening the project in IntelliJ IDEA.

### Code navigation (CTRL + click) is not working

Right-click the directory `src/main/java` in the project tree and click on **Mark directory as source folder**.

### Dependencies are not found (CTRL clickable)

1. Click on **Gradle** in the right sidebar.
2. Right-click on opencraft.
3. Click on Reload Gradle Project.
