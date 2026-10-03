output "public_ip" {
  value = aws_eip.lab.public_ip
}

output "ssh_command" {
  value = "ssh -i ~/.ssh/connect4-lab ubuntu@${aws_eip.lab.public_ip}"
}

output "instance_id" {
  value = aws_instance.lab.id
}
