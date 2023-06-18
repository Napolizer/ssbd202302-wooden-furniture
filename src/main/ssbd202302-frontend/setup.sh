#!/bin/bash

echo "Installing frontend dependencies..."
npm install

echo "Killing any process listening on port 4200..."
npx --yes kill-port 4200

echo "Starting the frontend app..."
npm run start >/dev/null 2>&1 &

while ! nc -z localhost 4200; do
  sleep 1
done

exit 0
