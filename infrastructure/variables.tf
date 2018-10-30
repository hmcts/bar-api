variable "product" {
  type    = "string"
  default = "bar"
}

variable "location" {
  type    = "string"
  default = "UK South"
}

variable "env" {
  type = "string"
}
variable "subscription" {
  type = "string"
}

variable "ilbIp"{}

variable "tenant_id" {}

variable "jenkins_AAD_objectId" {
  type                        = "string"
  description                 = "(Required) The Azure AD object ID of a user, service principal or security group in the Azure Active Directory tenant for the vault. The object ID must be unique for the list of access policies."
}

variable "microservice" {
  type = "string"
  default = "bar-app"
}

variable "database_name" {
  type    = "string"
  default = "bar"
}

variable "postgresql_user" {
  type    = "string"
  default = "bar"
}

variable "idam_api_url" {
  default = "http://betaDevAccidamAppLB.reform.hmcts.net"
}

variable "payment_api_url_prefix" {
  default = "payment-api"
}

variable "idam_s2s_url_prefix" {
  default = "rpe-service-auth-provider"
}

variable "capacity" {
  default = "1"
}
variable "common_tags" {
  type = "map"
}
variable "liquibase_enabled" {
  default = "false"
}
