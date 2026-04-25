# ─── Outputs ────────────────────────────────────────────────────────────────

output "alb_dns_name" {
  description = "ALB DNS name - access the application here"
  value       = aws_lb.main.dns_name
}

output "ecr_backend_url" {
  description = "ECR repository URL for backend"
  value       = aws_ecr_repository.backend.repository_url
}

output "ecr_frontend_url" {
  description = "ECR repository URL for frontend"
  value       = aws_ecr_repository.frontend.repository_url
}

output "rds_endpoint" {
  description = "RDS PostgreSQL endpoint"
  value       = aws_db_instance.postgres.endpoint
}

output "redis_endpoint" {
  description = "ElastiCache Redis endpoint"
  value       = aws_elasticache_cluster.redis.cache_nodes[0].address
}

output "kafka_endpoint" {
  description = "Kafka broker endpoint"
  value       = "${aws_instance.kafka.private_ip}:9092"
}

output "ecs_cluster_name" {
  description = "ECS cluster name"
  value       = aws_ecs_cluster.main.name
}

output "github_actions_role_arn" {
  description = "IAM role ARN for GitHub Actions OIDC - set as AWS_DEPLOY_ROLE_ARN in GitHub Environment"
  value       = aws_iam_role.github_actions.arn
}

output "jenkins_public_ip" {
  description = "Jenkins server public IP - access UI at http://<ip>:8080"
  value       = aws_instance.jenkins.public_ip
}

output "jenkins_initial_password_cmd" {
  description = "SSH command to get Jenkins initial admin password"
  value       = "ssh ec2-user@${aws_instance.jenkins.public_ip} 'sudo cat /var/lib/jenkins/secrets/initialAdminPassword'"
}
