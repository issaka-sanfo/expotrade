# ─── Jenkins EC2 Instance ────────────────────────────────────────────────────
# Cloud-based Jenkins server in a public subnet
# Uses IAM Instance Profile for AWS auth (no access keys needed)
# Access Jenkins UI at http://<jenkins_public_ip>:8080

# ─── Security Group ─────────────────────────────────────────────────────────

resource "aws_security_group" "jenkins" {
  name_prefix = "${local.name_prefix}-jenkins-"
  vpc_id      = aws_vpc.main.id
  description = "Jenkins - allow SSH and web UI"

  ingress {
    description = "Jenkins UI"
    from_port   = 8080
    to_port     = 8080
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  ingress {
    description = "SSH"
    from_port   = 22
    to_port     = 22
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags = { Name = "${local.name_prefix}-jenkins-sg" }

  lifecycle {
    create_before_destroy = true
  }
}

# ─── IAM Role & Instance Profile ────────────────────────────────────────────

resource "aws_iam_role" "jenkins" {
  name = "${local.name_prefix}-jenkins"

  assume_role_policy = jsonencode({
    Version = "2012-10-17"
    Statement = [{
      Effect = "Allow"
      Principal = {
        Service = "ec2.amazonaws.com"
      }
      Action = "sts:AssumeRole"
    }]
  })

  tags = { Name = "${local.name_prefix}-jenkins" }
}

resource "aws_iam_role_policy" "jenkins" {
  name = "${local.name_prefix}-jenkins-policy"
  role = aws_iam_role.jenkins.id

  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Sid    = "ECRAuth"
        Effect = "Allow"
        Action = [
          "ecr:GetAuthorizationToken"
        ]
        Resource = "*"
      },
      {
        Sid    = "ECRPush"
        Effect = "Allow"
        Action = [
          "ecr:BatchCheckLayerAvailability",
          "ecr:GetDownloadUrlForLayer",
          "ecr:BatchGetImage",
          "ecr:PutImage",
          "ecr:InitiateLayerUpload",
          "ecr:UploadLayerPart",
          "ecr:CompleteLayerUpload"
        ]
        Resource = [
          aws_ecr_repository.backend.arn,
          aws_ecr_repository.frontend.arn
        ]
      },
      {
        Sid    = "ECS"
        Effect = "Allow"
        Action = [
          "ecs:DescribeServices",
          "ecs:UpdateService",
          "ecs:DescribeTaskDefinition",
          "ecs:RegisterTaskDefinition",
          "ecs:ListTasks",
          "ecs:DescribeTasks"
        ]
        Resource = "*"
      },
      {
        Sid    = "PassRole"
        Effect = "Allow"
        Action = "iam:PassRole"
        Resource = [
          aws_iam_role.ecs_task_execution.arn,
          aws_iam_role.ecs_task.arn
        ]
      },
      {
        Sid    = "ELB"
        Effect = "Allow"
        Action = [
          "elasticloadbalancing:DescribeLoadBalancers",
          "elasticloadbalancing:DescribeTargetHealth"
        ]
        Resource = "*"
      }
    ]
  })
}

resource "aws_iam_instance_profile" "jenkins" {
  name = "${local.name_prefix}-jenkins"
  role = aws_iam_role.jenkins.name

  tags = { Name = "${local.name_prefix}-jenkins" }
}

# ─── EC2 Instance ───────────────────────────────────────────────────────────

resource "aws_instance" "jenkins" {
  ami                    = data.aws_ami.amazon_linux.id
  instance_type          = var.jenkins_instance_type
  subnet_id              = aws_subnet.public[0].id
  vpc_security_group_ids = [aws_security_group.jenkins.id]
  iam_instance_profile   = aws_iam_instance_profile.jenkins.name

  associate_public_ip_address = true

  user_data = base64encode(file("${path.module}/scripts/jenkins-userdata.sh"))

  root_block_device {
    volume_size = 30
    volume_type = "gp3"
    encrypted   = true
  }

  tags = { Name = "${local.name_prefix}-jenkins" }
}
