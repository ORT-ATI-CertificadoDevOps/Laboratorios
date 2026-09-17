locals {
  name_prefix = "webapp-${var.environment}"

  common_tags = merge(var.extra_tags, {
    Project     = "webapp"
    Environment = var.environment
    ManagedBy   = "terraform"
  })

  # Lista ordenada de IDs de subnet, para repartir las instancias entre zonas
  subnet_ids = [for az in sort(keys(var.public_subnets)) : aws_subnet.public[az].id]
}
