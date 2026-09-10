locals {
  name_prefix = "webapp-${var.environment}"

  common_tags = merge(var.extra_tags, {
    Project     = "webapp"
    Environment = var.environment
    ManagedBy   = "terraform"
  })
}
