locals {
  aseName = "${data.terraform_remote_state.core_apps_compute.ase_name[0]}"
  local_env = "${(var.env == "preview" || var.env == "spreview") ? (var.env == "preview" ) ? "aat" : "saat" : var.env}"
  local_ase = "${(var.env == "preview" || var.env == "spreview") ? (var.env == "preview" ) ? "core-compute-aat" : "core-compute-saat" : local.aseName}"

  previewVaultName = "bar-aat"
  nonPreviewVaultName = "bar-${var.env}"
  vaultName = "${(var.env == "preview" || var.env == "spreview") ? local.previewVaultName : local.nonPreviewVaultName}"
  rg_name = "bar-${var.env}"
  asp_name = "bar-asp-${var.env}"
  vault_rg_name = "${(var.env == "preview" || var.env == "spreview") ? "bar-aat" : local.rg_name}"


}

data "azurerm_key_vault" "bar_key_vault" {
  name = "${local.vaultName}"
  resource_group_name = "${local.vault_rg_name}"
}

data "azurerm_key_vault_secret" "s2s_secret" {
  name      = "bar-S2S-SECRET"
  vault_uri = "${data.azurerm_key_vault.bar_key_vault.vault_uri}"
}

module "bar-api" {
  source   = "git@github.com:hmcts/cnp-module-webapp?ref=master"
  product  = "${var.product}-api"
  location = "${var.location}"
  env      = "${var.env}"
  ilbIp = "${var.ilbIp}"
  subscription = "${var.subscription}"
  is_frontend  = false
  common_tags     = "${var.common_tags}"
  asp_name = "${local.asp_name}"
  asp_rg = "${local.rg_name}"

  app_settings = {
    # db
    SPRING_DATASOURCE_USERNAME = "${module.bar-database.user_name}"
    SPRING_DATASOURCE_PASSWORD = "${module.bar-database.postgresql_password}"
    SPRING_DATASOURCE_URL = "jdbc:postgresql://${module.bar-database.host_name}:${module.bar-database.postgresql_listen_port}/${module.bar-database.postgresql_database}?sslmode=require"
    # idam
    IDAM_CLIENT_BASE_URL = "${var.idam_api_url}"
    S2S_SECRET = "${data.azurerm_key_vault_secret.s2s_secret.value}"
    S2S_AUTH_URL = "http://${var.idam_s2s_url_prefix}-${local.local_env}.service.${local.local_ase}.internal"
    # payhub
    PAYMENT_API_URL = "${var.pay_api_url}"
    # enable/disables liquibase run
    SPRING_LIQUIBASE_ENABLED = "${var.liquibase_enabled}"
    SITE_API_URL = "http://bar-api-${local.local_env}.service.core-compute-${local.local_env}.internal"
  }
}

module "bar-database" {
  source = "git@github.com:hmcts/cnp-module-postgres?ref=master"
  product = "${var.product}-postgres-db"
  location = "${var.location}"
  env = "${var.env}"
  postgresql_user = "${var.postgresql_user}"
  database_name = "${var.database_name}"
  sku_name = "GP_Gen5_2"
  sku_tier = "GeneralPurpose"
  common_tags     = "${var.common_tags}"
  subscription = "${var.subscription}"
}

resource "azurerm_key_vault_secret" "POSTGRES-PASS" {
  name      = "${var.component}-POSTGRES-PASS"
  value     = "${module.bar-database.postgresql_password}"
  key_vault_id = "${data.azurerm_key_vault.bar_key_vault.id}"
}

data "azurerm_key_vault_secret" "appinsights_instrumentation_key" {
  name = "AppInsightsInstrumentationKey"
  vault_uri = "${data.azurerm_key_vault.bar_key_vault.vault_uri}"
}
