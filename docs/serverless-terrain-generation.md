# Serverless Terrain Generation

Opencraft can use Function-as-a-Service (FaaS) on AWS or Azure to generate terrain chunks. Using this feature requires
prior setup, described below.

## AWS

### Setup

1. Clone the Opencraft repository
2. Set the following environment variables:
    - AWS_ACCESS_KEY_ID
    - AWS_SECRET_ACCESS_KEY_ID
    - AWS_REGION
3. Run `./serverless/aws/deploy.sh`

### Runtime

When running the game, you need the following environment variables to be set, and match the ones used during setup:

- LAMBDA_ACCESS_KEY
- LAMBDA_SECRET_KEY
- LAMBDA_REGION

You further need the following settings in `config/opencraft.yml`:

```yml
opencraft:
    chunk-population:
        policy: naive # do NOT use 'default'! default will trigger local chunk generation
        provider: aws
        function: name-of-your-function
```

## Azure

### Setup

1. Install `azure-cli` for deploying your serverless function.
    1. When using MacOS, run `brew update && brew install azure-cli`.
2. Clone the Opencraft repository
3. Run `./serverless/azure/deploy.sh`

### Runtime

You need to set the following configurations in `config/opencraft.yml`:

```yml
opencraft:
    chunk-population:
        policy: naive # do NOT use 'default'! default will trigger local chunk generation
        provider: azure
        endpoint: "https://publicly-available-function-trigger"
```
