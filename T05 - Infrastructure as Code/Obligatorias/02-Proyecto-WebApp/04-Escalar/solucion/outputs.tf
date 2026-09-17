output "url" {
  description = "URL de la aplicación (balanceador)"
  value       = "http://${aws_lb.web.dns_name}"
}

output "instance_ids" {
  description = "IDs de las instancias EC2"
  value       = aws_instance.web[*].id
}

output "subnet_ids" {
  description = "Subnet de cada zona de disponibilidad"
  value       = { for az, subnet in aws_subnet.public : az => subnet.id }
}

output "ami_id" {
  description = "AMI que resolvió el data source"
  value       = data.aws_ami.al2023.id
}
