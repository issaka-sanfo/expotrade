# ─── ALB Security Group ──────────────────────────────────────────────────────

resource "aws_security_group" "alb" {
  name_prefix = "${local.name_prefix}-alb-"
  vpc_id      = aws_vpc.main.id
  description = "ALB - allow HTTP/HTTPS from internet"

  ingress {
    description = "HTTP"
    from_port   = 80
    to_port     = 80
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  ingress {
    description = "HTTPS"
    from_port   = 443
    to_port     = 443
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags = { Name = "${local.name_prefix}-alb-sg" }

  lifecycle {
    create_before_destroy = true
  }
}

# ─── Backend Security Group ─────────────────────────────────────────────────

resource "aws_security_group" "backend" {
  name_prefix = "${local.name_prefix}-backend-"
  vpc_id      = aws_vpc.main.id
  description = "Backend ECS - allow traffic from ALB"

  ingress {
    description     = "From ALB"
    from_port       = 8080
    to_port         = 8080
    protocol        = "tcp"
    security_groups = [aws_security_group.alb.id]
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags = { Name = "${local.name_prefix}-backend-sg" }

  lifecycle {
    create_before_destroy = true
  }
}

# ─── Frontend Security Group ────────────────────────────────────────────────

resource "aws_security_group" "frontend" {
  name_prefix = "${local.name_prefix}-frontend-"
  vpc_id      = aws_vpc.main.id
  description = "Frontend ECS - allow traffic from ALB"

  ingress {
    description     = "From ALB"
    from_port       = 80
    to_port         = 80
    protocol        = "tcp"
    security_groups = [aws_security_group.alb.id]
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags = { Name = "${local.name_prefix}-frontend-sg" }

  lifecycle {
    create_before_destroy = true
  }
}

# ─── RDS Security Group ─────────────────────────────────────────────────────

resource "aws_security_group" "rds" {
  name_prefix = "${local.name_prefix}-rds-"
  vpc_id      = aws_vpc.main.id
  description = "RDS PostgreSQL - allow from backend only"

  ingress {
    description     = "PostgreSQL from backend"
    from_port       = 5432
    to_port         = 5432
    protocol        = "tcp"
    security_groups = [aws_security_group.backend.id]
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags = { Name = "${local.name_prefix}-rds-sg" }

  lifecycle {
    create_before_destroy = true
  }
}

# ─── ElastiCache Security Group ─────────────────────────────────────────────

resource "aws_security_group" "redis" {
  name_prefix = "${local.name_prefix}-redis-"
  vpc_id      = aws_vpc.main.id
  description = "ElastiCache Redis - allow from backend only"

  ingress {
    description     = "Redis from backend"
    from_port       = 6379
    to_port         = 6379
    protocol        = "tcp"
    security_groups = [aws_security_group.backend.id]
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags = { Name = "${local.name_prefix}-redis-sg" }

  lifecycle {
    create_before_destroy = true
  }
}

# ─── Kafka EC2 Security Group ───────────────────────────────────────────────

resource "aws_security_group" "kafka" {
  name_prefix = "${local.name_prefix}-kafka-"
  vpc_id      = aws_vpc.main.id
  description = "Kafka EC2 - allow from backend only"

  ingress {
    description     = "Kafka from backend"
    from_port       = 9092
    to_port         = 9092
    protocol        = "tcp"
    security_groups = [aws_security_group.backend.id]
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags = { Name = "${local.name_prefix}-kafka-sg" }

  lifecycle {
    create_before_destroy = true
  }
}
