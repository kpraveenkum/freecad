#!/bin/bash

echo "=========================================="
echo "📦 Packaging CAD Validation Files"
echo "=========================================="

cd /home

# Create tar archive
tar -czf CAD_DOC.tar.gz CAD_DOC/

echo "✅ Archive created: /home/CAD_DOC.tar.gz"
echo ""
echo "📥 To download to your PC:"
echo ""
echo "Option 1 - Using SCP from your PC:"
echo "   scp -i your-key.pem ubuntu@34.195.137.185:/home/CAD_DOC.tar.gz ./"
echo ""
echo "Option 2 - Using SCP with different key:"
echo "   scp -i C:\\path\\to\\your-key.pem ubuntu@34.195.137.185:/home/CAD_DOC.tar.gz C:\\Users\\kprav\\Desktop\\"
echo ""
echo "Option 3 - Using WinSCP:"
echo "   Connect to 34.195.137.185 and download /home/CAD_DOC.tar.gz"
echo ""
echo "File size: $(du -h /home/CAD_DOC.tar.gz | cut -f1)"
