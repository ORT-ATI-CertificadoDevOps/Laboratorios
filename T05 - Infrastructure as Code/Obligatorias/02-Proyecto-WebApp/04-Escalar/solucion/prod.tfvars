environment    = "prod"
instance_type  = "t3.small"
instance_count = 3
mensaje        = "Hola desde prod"

public_subnets = {
  "us-east-1a" = "10.0.1.0/24"
  "us-east-1b" = "10.0.2.0/24"
  "us-east-1c" = "10.0.3.0/24"
}

extra_tags = {
  CostCenter = "ventas"
}
