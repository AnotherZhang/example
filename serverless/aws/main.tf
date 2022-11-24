
terraform {
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 4.0"
    }
  }
}

# Configure the AWS Provider
provider "aws" {
  region = "eu-central-1"
}

resource "aws_iam_role" "opencraft_iam_lambda" {
    name = "opencraft_iam_lambda"

    assume_role_policy = <<EOF
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Action": "sts:AssumeRole",
      "Principal": {
        "Service": "lambda.amazonaws.com"
      },
      "Effect": "Allow",
      "Sid": ""
    }
  ]
}
EOF
}

data "aws_iam_policy" "AWSLambdaBasicExecutionRole" {
    arn = "arn:aws:iam::aws:policy/service-role/AWSLambdaBasicExecutionRole"
}

resource "aws_iam_role_policy_attachment" "sto-readonly-role-policy-attach" {
    role       = aws_iam_role.opencraft_iam_lambda.name
    policy_arn = data.aws_iam_policy.AWSLambdaBasicExecutionRole.arn
}

# Create a bucket
resource "aws_s3_bucket" "opencraft_lambdas" {
    bucket = "opencraft-lambdas"
    tags   = {
        Name        = "opencraft-lambdas"
        Environment = "Dev"
    }
}

resource "aws_s3_bucket_acl" "opencraft_bucket_acl" {
    bucket = aws_s3_bucket.opencraft_lambdas.id
    acl    = "private"
}

# Upload an object
resource "aws_s3_object" "terrain_population_lambda" {
    bucket = aws_s3_bucket.opencraft_lambdas.id
    key    = "terrain_population"
    source = "${path.root}/../../../target/opencraft.jar"
    etag   = filemd5("${path.root}/../../../target/opencraft.jar")
}

resource "aws_lambda_function" "opencraft_terrain_population" {
    # If the file is not in the current working directory you will need to include a
    # path.module in the filename.
    #    filename      = "${path.module}/target/opencraft.jar"
    s3_bucket     = aws_s3_bucket.opencraft_lambdas.id
    s3_key        = aws_s3_object.terrain_population_lambda.key
    function_name = "serverless-terrain-population"
    role          = aws_iam_role.opencraft_iam_lambda.arn
    handler       = "science.atlarge.opencraft.opencraft.population.ServerlessPopulationHandler"
    memory_size   = 512
    timeout       = 30

    # The filebase64sha256() function is available in Terraform 0.11.12 and later
    # For Terraform 0.11.11 and earlier, use the base64sha256() function and the file() function:
    # source_code_hash = "${base64sha256(file("lambda_function_payload.zip"))}"
    source_code_hash = filebase64sha256("${path.root}/../../../target/opencraft.jar")

    runtime = "java11"

    environment {
        variables = {
            foo = "bar"
        }
    }
}
