locals {
  vaultName = join("-", ["bar", var.env])
  rg_name = "bar-${var.env}-rg"
}

provider "azurerm" {
  features {}
}

data "azurerm_key_vault" "bar_key_vault" {
  name = "${local.vaultName}"
  resource_group_name = "${local.rg_name}"
}

module "bar-database-v11" {
  source = "git@github.com:hmcts/cnp-module-postgres?ref=master"
  product         = var.product
  component       = var.component
  name            = join("-", [var.product, "postgres-db-v11"])
  location = var.location
  env = var.env
  postgresql_user = var.postgresql_user
  database_name = var.database_name
  sku_name = "GP_Gen5_2"
  sku_tier = "GeneralPurpose"
  common_tags = var.common_tags
  subscription = var.subscription
  postgresql_version = var.postgresql_version
  additional_databases = var.additional_databases
}

module "bar-database-v15" {
  providers = {
    azurerm.postgres_network = azurerm.postgres_network
  }
  source = "git@github.com:hmcts/terraform-module-postgresql-flexible?ref=master"
  product = var.product
  component = var.component
  business_area = "cft"
  name = "${var.product}-${var.component}-postgres-db-v15"
  location = var.location
  env = var.env
  pgsql_admin_username = var.postgresql_user
  pgsql_databases = [
    {
      name : var.database_name
    }
  ]
  pgsql_server_configuration = [
      {
        name  = "azure.extensions"
        value = "plpgsql,pg_stat_statements,pg_buffercache"
      }
    ]
  admin_user_object_id = var.jenkins_AAD_objectId
  common_tags = var.common_tags
  pgsql_version = var.postgresql_flexible_sql_version

}

resource "azurerm_key_vault_secret" "POSTGRES-PASS" {
  name      = join("-", [var.component, "POSTGRES-PASS"])
  value     = module.bar-database-v11.postgresql_password
  key_vault_id = data.azurerm_key_vault.bar_key_vault.id
}

# Populate Vault with Flexible DB info

resource "azurerm_key_vault_secret" "POSTGRES-USER-V15" {
  name      = join("-", [var.component, "POSTGRES-USER-V15"])
  value     = module.bar-database-v15.username
  key_vault_id = data.azurerm_key_vault.bar_key_vault.id
}

resource "azurerm_key_vault_secret" "POSTGRES-PASS-V15" {
  name      = join("-", [var.component, "POSTGRES-PASS-V15"])
  value     = module.bar-database-v15.password
  key_vault_id = data.azurerm_key_vault.bar_key_vault.id
}

resource "azurerm_key_vault_secret" "POSTGRES_HOST-V15" {
  name      = join("-", [var.component, "POSTGRES-HOST-V15"])
  value     =  module.bar-database-v15.fqdn
  key_vault_id = data.azurerm_key_vault.bar_key_vault.id
}

resource "azurerm_key_vault_secret" "POSTGRES_PORT-V15" {
  name      = join("-", [var.component, "POSTGRES-PORT-V15"])
  value     =  var.postgresql_flexible_server_port
  key_vault_id = data.azurerm_key_vault.bar_key_vault.id
}

resource "azurerm_key_vault_secret" "POSTGRES_DATABASE-V15" {
  name      = join("-", [var.component, "POSTGRES-DATABASE-V15"])
  value     =  var.database_name
  key_vault_id = data.azurerm_key_vault.bar_key_vault.id
}

