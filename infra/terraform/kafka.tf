# ─── Kafka EC2 Instance ──────────────────────────────────────────────────────

data "aws_ami" "amazon_linux" {
  most_recent = true
  owners      = ["amazon"]

  filter {
    name   = "name"
    values = ["al2023-ami-*-x86_64"]
  }

  filter {
    name   = "virtualization-type"
    values = ["hvm"]
  }
}

resource "aws_iam_role" "kafka" {
  name = "${local.name_prefix}-kafka"

  assume_role_policy = jsonencode({
    Version = "2012-10-17"
    Statement = [{
      Effect    = "Allow"
      Principal = { Service = "ec2.amazonaws.com" }
      Action    = "sts:AssumeRole"
    }]
  })

  tags = { Name = "${local.name_prefix}-kafka" }
}

resource "aws_iam_role_policy_attachment" "kafka_ssm" {
  role       = aws_iam_role.kafka.name
  policy_arn = "arn:aws:iam::aws:policy/AmazonSSMManagedInstanceCore"
}

resource "aws_iam_instance_profile" "kafka" {
  name = "${local.name_prefix}-kafka"
  role = aws_iam_role.kafka.name
}

resource "aws_instance" "kafka" {
  ami                    = data.aws_ami.amazon_linux.id
  instance_type          = var.kafka_instance_type
  subnet_id              = aws_subnet.private[0].id
  vpc_security_group_ids = [aws_security_group.kafka.id]
  iam_instance_profile   = aws_iam_instance_profile.kafka.name

  user_data = base64encode(file("${path.module}/scripts/kafka-userdata.sh"))

  root_block_device {
    volume_size = 30
    volume_type = "gp3"
    encrypted   = true
  }

  tags = { Name = "${local.name_prefix}-kafka" }
}
