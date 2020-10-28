locals {
  vaultName = join("-", ["bar", var.env])
  rg_name = "bar-${var.env}-rg"
}

provider "azurerm" {
  version = "=2.20.0"
  features {}
}

data "azurerm_key_vault" "bar_key_vault" {
  name = "${local.vaultName}"
  resource_group_name = "${local.rg_name}"
}

module "bar-database" {
  source = "git@github.com:hmcts/cnp-module-postgres?ref=master"
  product = join("-", [var.product, "postgres-db"])
  location = var.location
  env = var.env
  postgresql_user = var.postgresql_user
  database_name = var.database_name
  sku_name = "GP_Gen5_2"
  sku_tier = "GeneralPurpose"
  common_tags = var.common_tags
  subscription = var.subscription
}

resource "azurerm_key_vault_secret" "POSTGRES-PASS" {
  name      = join("-", [var.component, "POSTGRES-PASS"])
  value     = module.bar-database.postgresql_password
  key_vault_id = data.azurerm_key_vault.bar_key_vault.id
}
