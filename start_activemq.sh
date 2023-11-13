#!/bin/bash

# Define the ActiveMQ installation directory
ACTIVEMQ_DIR="/opt/activemq/bin/activemq"

# Start ActiveMQ
$ACTIVEMQ_DIR/bin/activemq start

# Wait for ActiveMQ to fully start (adjust the sleep time as needed)
sleep 10

# Open the ActiveMQ web console in the default web browser
WEB_CONSOLE_URL="http://localhost:8161/admin/"
xdg-open $WEB_CONSOLE_URL  # Use 'open' on macOS

# Display a message to indicate the process
echo "ActiveMQ is now running. Web console opened in your browser."

# Keep the script running to keep ActiveMQ running (Ctrl+C to stop)
while true; do
  sleep 1
done
