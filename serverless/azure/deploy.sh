#!/usr/bin/env bash

set -euo pipefail
IFS=$'\n\t'

gradle build
cd ./serverless/aws
terraform init
terraform apply
cd -
gradle azureFunctionsPackage
gradle azureFunctionsDeploy
