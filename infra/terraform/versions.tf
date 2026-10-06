terraform {
  required_version = ">= 1.6"
  required_providers {
    aws   = { source = "hashicorp/aws", version = "~> 6.67" }
    http  = { source = "hashicorp/http", version = "~> 3.6" }
    local = { source = "hashicorp/local", version = "~> 2.9" }
  }
}
provider "aws" {
  region = var.region
  default_tags {
    tags = { Project = "connect4-lab", ManagedBy = "terraform" }
  }
}
