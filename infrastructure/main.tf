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
    S2S_SECRET = "${module.bar-s2s.secret}"
    S2S_AUTH_URL = "${local.s2s_auth_url}"
    # payhub
    PAYMENT_API_URL = "${local.payment_api_url}"
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

module "key-vault" {
  source              = "git@github.com:hmcts/moj-module-key-vault?ref=master"
  product             = "${var.product}"
  env                 = "${var.env}"
  tenant_id           = "${var.tenant_id}"
  object_id           = "${var.jenkins_AAD_objectId}"
  resource_group_name = "${module.bar-api.resource_group_name}"
  # group id of dcd_reform_dev_azure
  product_group_object_id = "56679aaa-b343-472a-bb46-58bbbfde9c3d"
}

resource "azurerm_key_vault_secret" "POSTGRES-USER" {
  name      = "bar-POSTGRES-USER"
  value     = "${module.bar-database.user_name}"
  vault_uri = "${module.key-vault.key_vault_uri}"
}

resource "azurerm_key_vault_secret" "POSTGRES-PASS" {
  name      = "bar-POSTGRES-PASS"
  value     = "${module.bar-database.postgresql_password}"
  vault_uri = "${module.key-vault.key_vault_uri}"
}

resource "azurerm_key_vault_secret" "POSTGRES_HOST" {
  name      = "bar-POSTGRES-HOST"
  value     = "${module.bar-database.host_name}"
  vault_uri = "${module.key-vault.key_vault_uri}"
}

resource "azurerm_key_vault_secret" "POSTGRES_PORT" {
  name      = "bar-POSTGRES-PORT"
  value     = "${module.bar-database.postgresql_listen_port}"
  vault_uri = "${module.key-vault.key_vault_uri}"
}

resource "azurerm_key_vault_secret" "POSTGRES_DATABASE" {
  name      = "bar-POSTGRES-DATABASE"
  value     = "${module.bar-database.postgresql_database}"
  vault_uri = "${module.key-vault.key_vault_uri}"
}

resource "azurerm_key_vault_secret" "S2S-SECRET" {
  name      = "bar-S2S-SECRET"
  value     = "${module.bar-s2s.secret}"
  vault_uri = "${module.key-vault.key_vault_uri}"
}
