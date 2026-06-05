#!/bin/bash
echo "=== CAD Validation Service Status ==="
echo ""
echo "Process:"
ps aux | grep "cad-validation-service" | grep -v grep
echo ""
echo "Port:"
netstat -tlnp | grep 8080
echo ""
echo "Health:"
curl -s http://localhost:8080/api/health | python3 -m json.tool 2>/dev/null || echo "Service not responding"
