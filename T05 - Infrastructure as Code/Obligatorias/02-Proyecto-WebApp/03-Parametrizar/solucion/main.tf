resource "aws_security_group" "web" {
  name        = "${local.name_prefix}-web"
  description = "Permite HTTP desde internet"

  ingress {
    description = "HTTP"
    from_port   = 80
    to_port     = 80
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
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
  ami                    = data.aws_ami.al2023.id
  instance_type          = var.instance_type
  vpc_security_group_ids = [aws_security_group.web.id]

  user_data_replace_on_change = true
  user_data                   = <<-EOT
    #!/bin/bash
    dnf install -y httpd
    echo "<h1>${var.mensaje}</h1><p>Ambiente: ${var.environment}</p>" > /var/www/html/index.html
    systemctl enable --now httpd
  EOT

  tags = merge(local.common_tags, {
    Name = "${local.name_prefix}-web"
  })
}
