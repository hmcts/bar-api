locals {
  vaultName = join("-", ["bar", var.env])
  rg_name = "bar-${var.env}-rg"
}

provider "azurerm" {
  features {
    resource_group {
      prevent_deletion_if_contains_resources = false
    }
  }
}

data "azurerm_key_vault" "bar_key_vault" {
  name = "${local.vaultName}"
  resource_group_name = "${local.rg_name}"
}

module "bar-database-v15" {
  providers = {
    azurerm.postgres_network = azurerm.postgres_network
  }
  source = "git@github.com:hmcts/terraform-module-postgresql-flexible?ref=master"
  product = var.product
  component = var.component
  business_area = "cft"
  name = join("-", [var.product, "postgres-db-v15"])
  location = var.location
  env = var.env
  pgsql_admin_username = var.postgresql_user

  # Setup Access Reader db user
  force_user_permissions_trigger = "1"

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

resource "azurerm_key_vault_secret" "POSTGRES-USER" {
  name      = join("-", [var.component, "POSTGRES-USER"])
  value     = module.bar-database-v15.username
  key_vault_id = data.azurerm_key_vault.bar_key_vault.id
}

resource "azurerm_key_vault_secret" "POSTGRES-PASS" {
  name      = join("-", [var.component, "POSTGRES-PASS"])
  value     = module.bar-database-v15.password
  key_vault_id = data.azurerm_key_vault.bar_key_vault.id
}

resource "azurerm_key_vault_secret" "POSTGRES_HOST" {
  name      = join("-", [var.component, "POSTGRES-HOST"])
  value     =  module.bar-database-v15.fqdn
  key_vault_id = data.azurerm_key_vault.bar_key_vault.id
}

resource "azurerm_key_vault_secret" "POSTGRES_PORT" {
  name      = join("-", [var.component, "POSTGRES-PORT"])
  value     =  var.postgresql_flexible_server_port
  key_vault_id = data.azurerm_key_vault.bar_key_vault.id
}

resource "azurerm_key_vault_secret" "POSTGRES_DATABASE" {
  name      = join("-", [var.component, "POSTGRES-DATABASE"])
  value     =  var.database_name
  key_vault_id = data.azurerm_key_vault.bar_key_vault.id
}

