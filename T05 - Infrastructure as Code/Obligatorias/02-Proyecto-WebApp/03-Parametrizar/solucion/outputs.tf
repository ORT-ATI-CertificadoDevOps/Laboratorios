output "url" {
  description = "URL de la aplicación"
  value       = "http://${aws_instance.web.public_ip}"
}

output "instance_id" {
  description = "ID de la instancia EC2"
  value       = aws_instance.web.id
}

output "ami_id" {
  description = "AMI que resolvió el data source"
  value       = data.aws_ami.al2023.id
}
