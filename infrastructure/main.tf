locals {
  aseName = "${data.terraform_remote_state.core_apps_compute.ase_name[0]}"
  local_env = "${(var.env == "preview" || var.env == "spreview") ? (var.env == "preview" ) ? "aat" : "saat" : var.env}"
  local_ase = "${(var.env == "preview" || var.env == "spreview") ? (var.env == "preview" ) ? "core-compute-aat" : "core-compute-saat" : local.aseName}"

  previewVaultName = "bar-aat-vault"
  nonPreviewVaultName = "bar-${var.env}-vault"
  vaultName = "${(var.env == "preview" || var.env == "spreview") ? local.previewVaultName : local.nonPreviewVaultName}"
}

data "azurerm_key_vault" "bar_key_vault" {
  name = "${local.vaultName}"
  resource_group_name = "bar-${var.env}-rg"
}

data "azurerm_key_vault_secret" "s2s_secret" {
  name      = "bar-S2S-SECRET"
  vault_uri = "${data.azurerm_key_vault.bar_key_vault.vault_uri}"
}

module "bar-api" {
  source   = "git@github.com:hmcts/moj-module-webapp?ref=master"
  product  = "${var.product}-api"
  location = "${var.location}"
  env      = "${var.env}"
  ilbIp = "${var.ilbIp}"
  subscription = "${var.subscription}"
  is_frontend  = false
  common_tags     = "${var.common_tags}"

  app_settings = {
    # db
    SPRING_DATASOURCE_USERNAME = "${module.bar-database.user_name}"
    SPRING_DATASOURCE_PASSWORD = "${module.bar-database.postgresql_password}"
    SPRING_DATASOURCE_URL = "jdbc:postgresql://${module.bar-database.host_name}:${module.bar-database.postgresql_listen_port}/${module.bar-database.postgresql_database}?ssl=true"
    # idam
    IDAM_CLIENT_BASE_URL = "${var.idam_api_url}"
    S2S_SECRET = "${data.azurerm_key_vault_secret.s2s_secret.value}"
    S2S_AUTH_URL = "http://${var.idam_s2s_url_prefix}-${local.local_env}.service.${local.local_ase}.internal"
    # payhub
    PAYMENT_API_URL = "http://${var.payment_api_url_prefix}-${local.local_env}.service.${local.local_ase}.internal"
    # enable/disables liquibase run
    SPRING_LIQUIBASE_ENABLED = "${var.liquibase_enabled}"
  }
}

module "bar-database" {
  source = "git@github.com:hmcts/moj-module-postgres?ref=master"
  product = "${var.product}-postgres-db"
  location = "${var.location}"
  env = "${var.env}"
  postgresql_user = "${var.postgresql_user}"
  database_name = "${var.database_name}"
  sku_name = "GP_Gen5_2"
  sku_tier = "GeneralPurpose"
  common_tags     = "${var.common_tags}"
}
