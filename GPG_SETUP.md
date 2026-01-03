# GPG Key Setup Guide for Maven Central

Complete guide to creating GPG keys for signing your Kotlin Multiplatform library for Maven Central publishing.

## 📋 Table of Contents

- [Prerequisites](#prerequisites)
- [macOS Setup](#macos-setup)
- [Windows Setup](#windows-setup)
- [Linux Setup](#linux-setup)
- [Configure Gradle](#configure-gradle)
- [Troubleshooting](#troubleshooting)

---

## Prerequisites

Before starting, ensure you have:
- A Sonatype account at [central.sonatype.com](https://central.sonatype.com)
- Terminal/Command Prompt access
- About 15 minutes

---

## macOS Setup

### 1. Install GPG

Using Homebrew (recommended):
```bash
brew install gnupg
```

Or download from: https://gpgtools.org/

### 2. Generate Key Pair

```bash
gpg --full-generate-key
```

When prompted:
- **Key type**: `(1) RSA and RSA` (default)
- **Key size**: `4096`
- **Expiration**: `0` (never expires) or your preference
- **Real name**: Your full name
- **Email**: Your email (same as Sonatype account)
- **Comment**: Leave empty or add context
- **Passphrase**: Create a strong passphrase (required for signing)

### 3. List Your Keys

```bash
gpg --list-keys --keyid-format SHORT
```

Output example:
```
pub   rsa4096/ABCD1234 2024-01-03 [SC]
      1234567890ABCDEF1234567890ABCDEF12345678
uid         [ultimate] Your Name <your@email.com>
sub   rsa4096/EFGH5678 2024-01-03 [E]
```

**Your Key ID is**: `ABCD1234` (the 8 characters after `rsa4096/`)

### 4. Export Secret Key

```bash
# Create .gnupg directory in home if not exists
mkdir -p ~/.gnupg

# Export the secret keyring (for Gradle)
gpg --export-secret-keys -o ~/.gnupg/secring.gpg
```

### 5. Publish to Key Server

```bash
gpg --keyserver keyserver.ubuntu.com --send-keys ABCD1234
```

Replace `ABCD1234` with your actual key ID.

Verify it was uploaded:
```bash
gpg --keyserver keyserver.ubuntu.com --recv-keys ABCD1234
```

---

## Windows Setup

### 1. Install Gpg4win

Download and install from: https://www.gpg4win.org/

### 2. Open Kleopatra or Command Prompt

You can use the Kleopatra GUI or the command line.

**Command Line (recommended)**:

Open PowerShell or Command Prompt:

```powershell
gpg --full-generate-key
```

Follow the same prompts as macOS above.

### 3. List Your Keys

```powershell
gpg --list-keys --keyid-format SHORT
```

### 4. Export Secret Key

```powershell
# Find your GPG home directory
gpg --version
# Look for "Home:" line

# Export secret keys
gpg --export-secret-keys -o C:\Users\YourName\.gnupg\secring.gpg
```

### 5. Publish to Key Server

```powershell
gpg --keyserver keyserver.ubuntu.com --send-keys YOUR_KEY_ID
```

---

## Linux Setup

### 1. Install GPG

**Ubuntu/Debian:**
```bash
sudo apt-get update
sudo apt-get install gnupg
```

**Fedora/RHEL:**
```bash
sudo dnf install gnupg2
```

**Arch:**
```bash
sudo pacman -S gnupg
```

### 2. Generate Key Pair

```bash
gpg --full-generate-key
```

Follow the same prompts as macOS above.

### 3. List Your Keys

```bash
gpg --list-keys --keyid-format SHORT
```

### 4. Export Secret Key

```bash
gpg --export-secret-keys -o ~/.gnupg/secring.gpg
chmod 600 ~/.gnupg/secring.gpg
```

### 5. Publish to Key Server

```bash
gpg --keyserver keyserver.ubuntu.com --send-keys YOUR_KEY_ID
```

---

## Configure Gradle

### 1. Create/Edit `~/.gradle/gradle.properties`

```properties
# GPG Signing
signing.keyId=ABCD1234
signing.password=your-passphrase
signing.secretKeyRingFile=/Users/yourname/.gnupg/secring.gpg

# Sonatype Credentials
ossrhUsername=your-sonatype-username
ossrhPassword=your-sonatype-password
```

Replace:
- `ABCD1234` → Your 8-character key ID
- `your-passphrase` → Your GPG key passphrase
- `/Users/yourname/.gnupg/secring.gpg` → Full path to your secret keyring
- `your-sonatype-username` → Your Sonatype Central username
- `your-sonatype-password` → Your Sonatype Central password/token

### Windows Path Example:
```properties
signing.secretKeyRingFile=C:\\Users\\YourName\\.gnupg\\secring.gpg
```

### 2. Verify Signing Works

```bash
./gradlew :library:signMavenPublication
```

If successful, you'll see signed `.asc` files in `library/build/libs/`.

---

## Key Server Options

If one server doesn't work, try others:

```bash
# Ubuntu (most reliable)
gpg --keyserver keyserver.ubuntu.com --send-keys YOUR_KEY_ID

# MIT
gpg --keyserver pgp.mit.edu --send-keys YOUR_KEY_ID

# OpenPGP
gpg --keyserver keys.openpgp.org --send-keys YOUR_KEY_ID
```

---

## Troubleshooting

### "No secret key" Error

Your keyring file is missing or in the wrong location.

```bash
# Check if secret keys exist
gpg --list-secret-keys

# Re-export if needed
gpg --export-secret-keys -o ~/.gnupg/secring.gpg
```

### "Bad passphrase" Error

The passphrase in `gradle.properties` doesn't match your GPG key.

```bash
# Test your passphrase
echo "test" | gpg --clearsign
# Enter your passphrase when prompted
```

### Key Not Found on Server

Wait 5-10 minutes for propagation, then try a different keyserver.

### GPG Agent Issues (macOS)

```bash
# Kill and restart GPG agent
gpgconf --kill gpg-agent
gpg-agent --daemon
```

### Permission Denied (Linux)

```bash
chmod 700 ~/.gnupg
chmod 600 ~/.gnupg/secring.gpg
chmod 600 ~/.gnupg/private-keys-v1.d/*
```

---

## Quick Reference Commands

| Action | Command |
|--------|---------|
| Generate key | `gpg --full-generate-key` |
| List keys | `gpg --list-keys --keyid-format SHORT` |
| List secret keys | `gpg --list-secret-keys` |
| Export public key | `gpg --armor --export YOUR_KEY_ID` |
| Export secret keyring | `gpg --export-secret-keys -o ~/.gnupg/secring.gpg` |
| Send to keyserver | `gpg --keyserver keyserver.ubuntu.com --send-keys YOUR_KEY_ID` |
| Delete key | `gpg --delete-secret-keys YOUR_KEY_ID` then `gpg --delete-keys YOUR_KEY_ID` |

---

## Security Best Practices

1. **Never commit** `~/.gradle/gradle.properties` to git
2. **Use strong passphrases** - at least 20 characters
3. **Backup your keys** - store in a secure location
4. **Set key expiration** - consider 2-year expiration for added security
5. **Use environment variables** in CI/CD instead of files:
   ```bash
   export ORG_GRADLE_PROJECT_signingKey="$(cat ~/.gnupg/secring.gpg | base64)"
   export ORG_GRADLE_PROJECT_signingPassword="your-passphrase"
   ```

---

## Next Steps

After GPG setup, you can:

1. **Create signed bundle**: `./gradlew :library:zipBundle`
2. **Upload to Sonatype**: https://central.sonatype.com
3. **Publish to Maven Central**: After validation, click "Publish"

📖 See [README.md](README.md) for full publishing workflow.
