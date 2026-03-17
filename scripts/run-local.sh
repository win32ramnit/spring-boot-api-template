#!/usr/bin/env bash
set -euo pipefail

export SPRING_PROFILES_ACTIVE=dev
export DB_URL=${DB_URL:-jdbc:mysql://localhost:3306/template_dev}
export DB_USERNAME=${DB_USERNAME:-root}
export DB_PASSWORD=${DB_PASSWORD:-root!}

./gradlew bootRun
