resource "aws_security_group" "web" {
  name        = "${local.name_prefix}-web"
  description = "Permite HTTP solo desde el balanceador"
  vpc_id      = aws_vpc.main.id

  ingress {
    description     = "HTTP desde el ALB"
    from_port       = 80
    to_port         = 80
    protocol        = "tcp"
    security_groups = [aws_security_group.alb.id]
  }

  egress {
    description = "Salida a internet"
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags = merge(local.common_tags, {
    Name = "${local.name_prefix}-web"
  })
}

resource "aws_instance" "web" {
  count = var.instance_count

  ami                    = data.aws_ami.al2023.id
  instance_type          = var.instance_type
  subnet_id              = local.subnet_ids[count.index % length(local.subnet_ids)]
  vpc_security_group_ids = [aws_security_group.web.id]

  user_data_replace_on_change = true
  user_data                   = <<-EOT
    #!/bin/bash
    dnf install -y httpd
    echo "<h1>${var.mensaje}</h1><p>Ambiente: ${var.environment} - Instancia: ${count.index}</p>" > /var/www/html/index.html
    systemctl enable --now httpd
  EOT

  # El user_data instala paquetes: la instancia necesita salida a internet
  # desde el primer segundo. Terraform no puede deducir esa dependencia.
  depends_on = [aws_route_table_association.public]

  lifecycle {
    create_before_destroy = true
  }

  tags = merge(local.common_tags, {
    Name = "${local.name_prefix}-web-${count.index}"
  })
}
