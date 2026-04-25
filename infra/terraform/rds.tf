# ─── RDS Subnet Group ────────────────────────────────────────────────────────

resource "aws_db_subnet_group" "main" {
  name       = "${local.name_prefix}-db-subnet"
  subnet_ids = aws_subnet.private[*].id

  tags = { Name = "${local.name_prefix}-db-subnet" }
}

# ─── RDS PostgreSQL ─────────────────────────────────────────────────────────

resource "aws_db_instance" "postgres" {
  identifier = "${local.name_prefix}-postgres"

  engine         = "postgres"
  engine_version = "16"
  instance_class = var.db_instance_class

  db_name  = var.db_name
  username = var.db_username
  password = aws_secretsmanager_secret_version.db_password.secret_string

  allocated_storage     = 20
  max_allocated_storage = 50
  storage_type          = "gp3"
  storage_encrypted     = true

  db_subnet_group_name   = aws_db_subnet_group.main.name
  vpc_security_group_ids = [aws_security_group.rds.id]

  multi_az            = false
  publicly_accessible = false

  backup_retention_period = 1
  skip_final_snapshot     = var.environment == "staging"
  final_snapshot_identifier = var.environment == "staging" ? null : "${local.name_prefix}-final-snapshot"
  deletion_protection       = var.environment != "staging"

  tags = { Name = "${local.name_prefix}-postgres" }
}
