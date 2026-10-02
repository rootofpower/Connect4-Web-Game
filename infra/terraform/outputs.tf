output "public_ip" {
  value = aws_instance.lab.public_ip
}

output "ssh_command" {
  value = "ssh -i ~/.ssh/connect4-lab ubuntu@${aws_instance.lab.public_ip}"
}
