provider "azurerm" {
    features {}
}

resource "azurerm_resource_group" "opencraft-rg" {
    name     = "opencraft-rg"
    location = "Germany West Central"
}

resource "azurerm_storage_account" "opencraftsa" {
    name                     = "opencraftsa"
    resource_group_name      = azurerm_resource_group.opencraft-rg.name
    location                 = azurerm_resource_group.opencraft-rg.location
    account_tier             = "Standard"
    account_replication_type = "LRS"
}

resource "azurerm_service_plan" "opencraft-sp" {
    name                = "opencraft-sp"
    location            = azurerm_resource_group.opencraft-rg.location
    resource_group_name = azurerm_resource_group.opencraft-rg.name
    os_type             = "Linux"
    sku_name            = "S1"
}

resource "azurerm_linux_function_app" "opencraft-linux-function" {
    name                = "opencraft-linux-function"
    location            = azurerm_resource_group.opencraft-rg.location
    resource_group_name = azurerm_resource_group.opencraft-rg.name
    service_plan_id     = azurerm_service_plan.opencraft-sp.id

    storage_account_name       = azurerm_storage_account.opencraftsa.name
    storage_account_access_key = azurerm_storage_account.opencraftsa.primary_access_key

    site_config {
        application_stack {
            #            python_version = "3.9"
            java_version = "8"
        }
    }
}

resource "azurerm_function_app_function" "opencraft-terrain-gen-function" {
    name            = "opencraft-terrain-gen-function"
    function_app_id = azurerm_linux_function_app.opencraft-linux-function.id
    language        = "Java"

    file {
        name    = "opencraft.jar"
        # Used sensitive to prevent 'terraform apply' from printing entire file in Base64 to stdout
        content = sensitive(filebase64("${path.root}/../../../target/opencraft.jar"))
    }

    config_json = jsonencode({
        "scriptFile" = "${path.root}/../../../target/opencraft.jar"
        "entryPoint" = "science.atlarge.opencraft.opencraft.population.PopulateAzure.run"
        "bindings"   = [
            {
                "type"      = "httpTrigger"
                "direction" = "in"
                "name"      = "req"
                "methods"   = ["POST"]
                "authLevel" = "ANONYMOUS"
            },
            {
                "type"      = "http"
                "direction" = "out"
                "name"      = "$return"
            },
        ]
    })
}
