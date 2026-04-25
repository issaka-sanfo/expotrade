variable "aws_region" {
  description = "AWS region"
  type        = string
  default     = "eu-west-1"
}

variable "environment" {
  description = "Environment name (staging, production)"
  type        = string
  default     = "staging"
}

variable "vpc_cidr" {
  description = "CIDR block for VPC"
  type        = string
  default     = "10.0.0.0/16"
}

variable "db_instance_class" {
  description = "RDS instance class"
  type        = string
  default     = "db.t3.micro"
}

variable "db_name" {
  description = "Database name"
  type        = string
  default     = "expotrade"
}

variable "db_username" {
  description = "Database master username"
  type        = string
  default     = "expotrade"
}

variable "redis_node_type" {
  description = "ElastiCache Redis node type"
  type        = string
  default     = "cache.t3.micro"
}

variable "backend_cpu" {
  description = "Backend ECS task CPU units"
  type        = number
  default     = 512
}

variable "backend_memory" {
  description = "Backend ECS task memory (MB)"
  type        = number
  default     = 1024
}

variable "frontend_cpu" {
  description = "Frontend ECS task CPU units"
  type        = number
  default     = 256
}

variable "frontend_memory" {
  description = "Frontend ECS task memory (MB)"
  type        = number
  default     = 512
}

variable "kafka_instance_type" {
  description = "EC2 instance type for Kafka broker"
  type        = string
  default     = "t3.micro"
}

variable "jenkins_instance_type" {
  description = "EC2 instance type for Jenkins server"
  type        = string
  default     = "t3.small"
}

variable "github_org" {
  description = "GitHub organization or username"
  type        = string
  default     = "Issaka"
}

variable "github_repo" {
  description = "GitHub repository name"
  type        = string
  default     = "ExpoTrade"
}
