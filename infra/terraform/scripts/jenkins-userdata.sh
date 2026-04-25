#!/bin/bash
set -e

# ── System updates ──
dnf update -y

# ── Install Docker ──
dnf install -y docker
systemctl enable docker
systemctl start docker

# ── Install Git ──
dnf install -y git

# ── Install Java 21 (for Jenkins + Maven) ──
dnf install -y java-21-amazon-corretto-devel

# ── Install Maven ──
MVN_VERSION=3.9.9
curl -fsSL "https://dlcdn.apache.org/maven/maven-3/${MVN_VERSION}/binaries/apache-maven-${MVN_VERSION}-bin.tar.gz" \
  | tar -xz -C /opt
ln -s /opt/apache-maven-${MVN_VERSION}/bin/mvn /usr/local/bin/mvn

# ── Install Node.js 20 ──
dnf install -y nodejs20 npm

# ── Install AWS CLI v2 ──
curl -fsSL "https://awscli.amazonaws.com/awscli-exe-linux-x86_64.zip" -o /tmp/awscliv2.zip
cd /tmp && unzip -q awscliv2.zip && ./aws/install
rm -rf /tmp/aws /tmp/awscliv2.zip

# ── Install jq (used by deploy pipeline) ──
dnf install -y jq

# ── Install Jenkins ──
wget -O /etc/yum.repos.d/jenkins.repo https://pkg.jenkins.io/redhat-stable/jenkins.repo
rpm --import https://pkg.jenkins.io/redhat-stable/jenkins.io-2023.key
dnf install -y jenkins

# Add jenkins user to docker group
usermod -aG docker jenkins

# Start Jenkins
systemctl enable jenkins
systemctl start jenkins
