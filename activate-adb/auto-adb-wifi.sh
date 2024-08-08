#!/bin/bash

# Fetching host IP address using PowerShell
host_ip=$(powershell.exe -Command "(Get-NetIPAddress -AddressFamily IPv4 | Where-Object InterfaceAlias -EQ 'WLAN').IPAddress" | dos2unix)

sleep 3

# Kill adb service
powershell.exe -Command "Stop-Process -Name adb"

# Wait for servers to be killed
sleep 3

# Start ADB server
powershell.exe "adb -a -P 6666 server"

# Wait for server to start
sleep 5

# Set ADB environment variables
export ANDROID_ADB_SERVER_PORT=6666
export ANDROID_ADB_SERVER_ADDRESS=$host_ip

sleep 2

adb start-server

# Show devices connected
adb devices
