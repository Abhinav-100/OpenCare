#!/bin/bash

# Script to prepare SSH private key for GitHub Secrets
# This ensures the key is properly formatted

echo "========================================="
echo "SSH Key Preparation for GitHub Secrets"
echo "========================================="
echo ""

# Find the SSH key you use to connect to your server
echo "Which SSH private key do you use to connect to your server?"
echo ""
ls -la ~/.ssh/ | grep -v ".pub" | grep -v "known_hosts" | grep -v "authorized_keys" | grep -v "config"
echo ""
read -p "Enter the key filename (e.g., id_rsa, id_ed25519): " KEY_FILE

KEY_PATH="$HOME/.ssh/$KEY_FILE"

if [ ! -f "$KEY_PATH" ]; then
    echo "Error: Key file not found at $KEY_PATH"
    exit 1
fi

echo ""
echo "========================================="
echo "COPY THE FOLLOWING KEY TO GITHUB SECRETS"
echo "========================================="
echo ""
echo "Steps:"
echo "1. Go to: https://github.com/YOUR_USERNAME/open-care-backend/settings/secrets/actions"
echo "2. Click 'Update' on SSH_PRIVATE_KEY (or 'New repository secret')"
echo "3. Copy EVERYTHING from '-----BEGIN' to '-----END' below"
echo "4. Paste into the secret value"
echo ""
echo "--- START COPYING FROM HERE ---"
cat "$KEY_PATH"
echo ""
echo "--- END COPYING HERE ---"
echo ""
echo "========================================="
echo "VERIFY YOUR PUBLIC KEY IS ON THE SERVER"
echo "========================================="
echo ""
echo "Your public key:"
cat "${KEY_PATH}.pub" 2>/dev/null || echo "Warning: Public key not found"
echo ""
echo "Make sure this public key exists in ~/.ssh/authorized_keys on your server"
echo ""

