#!/bin/bash
set -e

# Install Docker
dnf update -y
dnf install -y docker
systemctl enable docker
systemctl start docker

# Install Docker Compose plugin
mkdir -p /usr/local/lib/docker/cli-plugins
curl -SL "https://github.com/docker/compose/releases/latest/download/docker-compose-linux-x86_64" \
  -o /usr/local/lib/docker/cli-plugins/docker-compose
chmod +x /usr/local/lib/docker/cli-plugins/docker-compose

# Get instance private IP from EC2 metadata
TOKEN=$(curl -s -X PUT "http://169.254.169.254/latest/api/token" -H "X-aws-ec2-metadata-token-ttl-seconds: 60")
PRIVATE_IP=$(curl -s -H "X-aws-ec2-metadata-token: $TOKEN" http://169.254.169.254/latest/meta-data/local-ipv4)

# Create Kafka docker-compose
mkdir -p /opt/kafka
cat > /opt/kafka/docker-compose.yml <<COMPOSE
services:
  zookeeper:
    image: confluentinc/cp-zookeeper:7.6.0
    restart: always
    environment:
      ZOOKEEPER_CLIENT_PORT: 2181
    volumes:
      - zk_data:/var/lib/zookeeper/data

  kafka:
    image: confluentinc/cp-kafka:7.6.0
    restart: always
    ports:
      - "9092:9092"
    environment:
      KAFKA_BROKER_ID: 1
      KAFKA_ZOOKEEPER_CONNECT: zookeeper:2181
      KAFKA_ADVERTISED_LISTENERS: PLAINTEXT://$PRIVATE_IP:9092
      KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR: 1
      KAFKA_LOG_RETENTION_HOURS: 168
      KAFKA_LOG_RETENTION_BYTES: 1073741824
    volumes:
      - kafka_data:/var/lib/kafka/data
    depends_on:
      - zookeeper

volumes:
  zk_data:
  kafka_data:
COMPOSE

# Start Kafka
cd /opt/kafka
docker compose up -d
