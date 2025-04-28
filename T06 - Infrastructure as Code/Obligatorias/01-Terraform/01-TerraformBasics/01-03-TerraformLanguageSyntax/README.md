# Terraform Configuration Language Syntax

## 01 - Introducción
- Entender Terraform Language Basics
  - Entender Blocks
  - Entender Arguments, Attributes & Meta-Arguments
  - Entender Identifiers
  - Entender Comments
 


## 02 - Terraform Configuration Language Syntax
- Entender Blocks
- Entender Arguments
- Entender Identifiers
- Entender Comments
- [Terraform Configuration](https://www.terraform.io/docs/configuration/index.html)
- [Terraform Configuration Syntax](https://www.terraform.io/docs/configuration/syntax.html)
```t
# Template
<BLOCK TYPE> "<BLOCK LABEL>" "<BLOCK LABEL>"   {
  # Block body
  <IDENTIFIER> = <EXPRESSION> # Argument
}

# AWS Example
resource "aws_instance" "ec2demo" { # BLOCK
  ami           = "ami-04d29b6f966df1537" # Argument
  instance_type = var.instance_type # Argument with value as expression (Variable value replaced from varibales.tf
}
```

## 03 -  Entender sobre Arguments, Attributes and Meta-Arguments.
- Los argumentos puede ser `required` o `optional`
- El formato de los atributos es `resource_type.resource_name.attribute_name`
- Meta-Arguments pueden cambiar comportamiento de los recursos (Ejemplo: count, for_each)
- [Additional Reference](https://learn.hashicorp.com/tutorials/terraform/resource?in=terraform/configuration-language) 
- [Resource: AWS Instance](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/instance)
- [Resource: AWS Instance Argument Reference](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/instance#argument-reference)
- [Resource: AWS Instance Attribute Reference](https://registry.terraform.io/providers/hashicorp/aws/latest/docs/resources/instance#attributes-reference)
- [Resource: Meta-Arguments](https://www.terraform.io/docs/language/meta-arguments/depends_on.html)

## 04 - Entendiendo sobre Terraform Top-Level Blocks
- Informarse sobre Terraform Top-Level blocks
  - Terraform Settings Block
  - Provider Block
  - Resource Block
  - Input Variables Block
  - Output Values Block
  - Local Values Block
  - Data Sources Block
  - Modules Block

