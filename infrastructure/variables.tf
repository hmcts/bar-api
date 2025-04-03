variable "product" {}

variable "location" {
  default = "UK South"
}

variable "component" {}

variable "env" {}

variable "subscription" {}

variable "ilbIp" {
  default = ""
}

variable "tenant_id" {}

variable "jenkins_AAD_objectId" {
  description  = "(Required) The Azure AD object ID of a user, service principal or security group in the Azure Active Directory tenant for the vault. The object ID must be unique for the list of access policies."
}

variable "database_name" {
  default = "bar"
}

variable "postgresql_user" {
  default = "bar"
}

variable "common_tags" {
  type = map(string)
}

variable "postgresql_flexible_sql_version" {
  default = "15"
}

variable "postgresql_flexible_server_port" {
  default = "5432"
}

variable flexible_sku_name {
  default = "GP_Standard_D2s_v3"
}

variable "aks_subscription_id" {}

variable "additional_databases" {
  default = []
}

variable "db_monitor_action_group_name" {
  description = "The name of the Action Group to create."
  type        = string
  default     = "db_monitor_ag"
}

variable "db_alert_email_address_key" {
  description = "Email address key in azure Key Vault."
  type        = string
  default     = "db-alert-monitoring-email-address"
}
