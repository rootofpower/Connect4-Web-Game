variable "region" {
  type    = string
  default = "eu-central-1"
}
variable "instance_type" {
  type    = string
  default = "m7i-flex.large"
}

variable "ssh_public_key_path" {
  type    = string
  default = "~/.ssh/connect4-lab.pub"
}
