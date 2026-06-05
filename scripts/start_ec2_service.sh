#!/bin/bash
cd /home/ubuntu/cad-validation-service
pkill -f "cad-validation-service" 2>/dev/null
nohup java -jar target/cad-validation-service-1.0.0.jar > service.log 2>&1 &
sleep 5
curl http://localhost:8080/api/health
echo "Service started!"
