#!/bin/bash
# ============================================
# KMP Starter - Setup Script
# ============================================
# Customize this template for your new library.
#
# Usage: ./setup.sh <library-name> <package-name>
# Example: ./setup.sh parcel in.sitharaj.parcel
#
# This script will:
# 1. Update gradle.properties
# 2. Rename all package directories
# 3. Update package declarations in Kotlin files
# 4. Update Android manifests and namespaces
# 5. Update iOS project references
# 6. Update documentation

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

print_step() {
    echo -e "${BLUE}▶${NC} $1"
}

print_success() {
    echo -e "${GREEN}✓${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}!${NC} $1"
}

print_error() {
    echo -e "${RED}✗${NC} $1"
}

# Validate arguments
if [ "$#" -ne 2 ]; then
    echo ""
    echo -e "${RED}Error: Invalid arguments${NC}"
    echo ""
    echo "Usage: $0 <library-name> <package-name>"
    echo ""
    echo "Examples:"
    echo "  $0 parcel in.sitharaj.parcel"
    echo "  $0 krate com.example.krate"
    echo "  $0 mylib com.mycompany.mylib"
    echo ""
    exit 1
fi

LIBRARY_NAME=$1
PACKAGE_NAME=$2

# Validate library name (lowercase, no special chars except hyphen)
if [[ ! "$LIBRARY_NAME" =~ ^[a-z][a-z0-9-]*$ ]]; then
    echo -e "${RED}Error: Library name must be lowercase, start with a letter, and contain only letters, numbers, or hyphens${NC}"
    exit 1
fi

# Validate package name
if [[ ! "$PACKAGE_NAME" =~ ^[a-z][a-z0-9]*(\.[a-z][a-z0-9]*)+$ ]]; then
    echo -e "${RED}Error: Package name must be a valid Java package (e.g., com.example.mylib)${NC}"
    exit 1
fi

# Derived values
# Convert library-name to LibraryName (PascalCase) - using perl for macOS compatibility
LIBRARY_NAME_PASCAL=$(echo "$LIBRARY_NAME" | perl -pe 's/(^|-)(\w)/\U$2/g')

PACKAGE_PATH=$(echo "$PACKAGE_NAME" | sed 's/\./\//g')
OLD_PACKAGE="com.example.library"
OLD_PACKAGE_PATH="com/example/library"

echo ""
echo "🚀 KMP Starter Setup"
echo "===================="
echo ""
echo "  Library Name:     $LIBRARY_NAME"
echo "  Library Class:    $LIBRARY_NAME_PASCAL"
echo "  Package:          $PACKAGE_NAME"
echo "  Package Path:     $PACKAGE_PATH"
echo ""

# Confirm before proceeding
read -p "Proceed with setup? (y/N) " -n 1 -r
echo
if [[ ! $REPLY =~ ^[Yy]$ ]]; then
    echo "Setup cancelled."
    exit 0
fi

echo ""

# Function to safely move package directories
move_package_dir() {
    local BASE_DIR=$1
    local PLATFORM=$2
    local OLD_PATH=$3
    local NEW_PATH=$4
    
    local FULL_OLD="$BASE_DIR/src/$PLATFORM/kotlin/$OLD_PATH"
    local FULL_NEW="$BASE_DIR/src/$PLATFORM/kotlin/$NEW_PATH"
    
    if [ -d "$FULL_OLD" ]; then
        # Create new directory structure
        mkdir -p "$FULL_NEW"
        
        # Move files (not rm!)
        if ls "$FULL_OLD"/* >/dev/null 2>&1; then
            mv "$FULL_OLD"/* "$FULL_NEW/"
            print_success "  $BASE_DIR/$PLATFORM → $NEW_PATH"
        fi
        
        # Clean up empty old directories (from deepest to shallowest)
        rmdir "$FULL_OLD" 2>/dev/null || true
        local PARENT=$(dirname "$FULL_OLD")
        while [ "$PARENT" != "$BASE_DIR/src/$PLATFORM/kotlin" ]; do
            rmdir "$PARENT" 2>/dev/null || break
            PARENT=$(dirname "$PARENT")
        done
    fi
}

# ============================================
# 1. Update gradle.properties
# ============================================
print_step "Updating gradle.properties..."

if [ -f "gradle.properties" ]; then
    # Get the group from package (everything except last segment)
    NEW_GROUP=$(echo "$PACKAGE_NAME" | sed 's/\.[^.]*$//')
    
    sed -i '' "s/GROUP=com.example/GROUP=$NEW_GROUP/g" gradle.properties
    sed -i '' "s/POM_ARTIFACT_ID=library/POM_ARTIFACT_ID=$LIBRARY_NAME/g" gradle.properties
    sed -i '' "s/LIBRARY_NAME=MyLibrary/LIBRARY_NAME=$LIBRARY_NAME_PASCAL/g" gradle.properties
    sed -i '' "s/LIBRARY_PACKAGE=com.example.library/LIBRARY_PACKAGE=$PACKAGE_NAME/g" gradle.properties
    print_success "gradle.properties updated"
else
    print_warning "gradle.properties not found"
fi

# ============================================
# 2. Update settings.gradle.kts
# ============================================
print_step "Updating settings.gradle.kts..."

if [ -f "settings.gradle.kts" ]; then
    sed -i '' "s/mylibrary-kmp/$LIBRARY_NAME-kmp/g" settings.gradle.kts
    sed -i '' "s/kmp-starter/$LIBRARY_NAME/g" settings.gradle.kts
    print_success "settings.gradle.kts updated"
fi

# ============================================
# 3. Update build.gradle.kts files
# ============================================
print_step "Updating build.gradle.kts namespaces..."

find . -name "build.gradle.kts" -not -path "./docs-site/*" -not -path "./build/*" -exec sed -i '' "s/com.example.library.sample.android/$PACKAGE_NAME.sample.android/g" {} \;
find . -name "build.gradle.kts" -not -path "./docs-site/*" -not -path "./build/*" -exec sed -i '' "s/com.example.library.sample/$PACKAGE_NAME.sample/g" {} \;
find . -name "build.gradle.kts" -not -path "./docs-site/*" -not -path "./build/*" -exec sed -i '' "s/com.example.library/$PACKAGE_NAME/g" {} \;
print_success "build.gradle.kts files updated"

# ============================================
# 4. Rename library module directories
# ============================================
print_step "Renaming library source directories..."

for PLATFORM in commonMain androidMain iosMain desktopMain jsMain wasmJsMain commonTest; do
    move_package_dir "library" "$PLATFORM" "$OLD_PACKAGE_PATH" "$PACKAGE_PATH"
done

# ============================================
# 5. Rename sample module directories  
# ============================================
print_step "Renaming sample source directories..."

for PLATFORM in commonMain androidMain iosMain desktopMain jsMain wasmJsMain; do
    move_package_dir "sample" "$PLATFORM" "$OLD_PACKAGE_PATH/sample" "$PACKAGE_PATH/sample"
done

# ============================================
# 6. Rename androidApp module directories
# ============================================
print_step "Renaming androidApp source directories..."

move_package_dir "androidApp" "main" "$OLD_PACKAGE_PATH/sample/android" "$PACKAGE_PATH/sample/android"

# ============================================
# 7. Update Kotlin package declarations
# ============================================
print_step "Updating Kotlin package declarations..."

find . -name "*.kt" -not -path "./docs-site/*" -not -path "./build/*" -not -path "./.gradle/*" -exec sed -i '' "s/package com.example.library.sample.android/package $PACKAGE_NAME.sample.android/g" {} \;
find . -name "*.kt" -not -path "./docs-site/*" -not -path "./build/*" -not -path "./.gradle/*" -exec sed -i '' "s/package com.example.library.sample/package $PACKAGE_NAME.sample/g" {} \;
find . -name "*.kt" -not -path "./docs-site/*" -not -path "./build/*" -not -path "./.gradle/*" -exec sed -i '' "s/package com.example.library/package $PACKAGE_NAME/g" {} \;
find . -name "*.kt" -not -path "./docs-site/*" -not -path "./build/*" -not -path "./.gradle/*" -exec sed -i '' "s/import com.example.library.sample/import $PACKAGE_NAME.sample/g" {} \;
find . -name "*.kt" -not -path "./docs-site/*" -not -path "./build/*" -not -path "./.gradle/*" -exec sed -i '' "s/import com.example.library/import $PACKAGE_NAME/g" {} \;
print_success "Package declarations updated"

# ============================================
# 8. Update class names
# ============================================
print_step "Updating class names..."

find . -name "*.kt" -not -path "./docs-site/*" -not -path "./build/*" -not -path "./.gradle/*" -exec sed -i '' "s/MyLibrary/$LIBRARY_NAME_PASCAL/g" {} \;
print_success "Class names updated to $LIBRARY_NAME_PASCAL"

# ============================================
# 9. Update Android Manifests
# ============================================
print_step "Updating Android manifests..."

find . -name "AndroidManifest.xml" -not -path "./build/*" -exec sed -i '' "s/com.example.library.sample.android/$PACKAGE_NAME.sample.android/g" {} \;
find . -name "AndroidManifest.xml" -not -path "./build/*" -exec sed -i '' "s/com.example.library.sample/$PACKAGE_NAME.sample/g" {} \;
find . -name "AndroidManifest.xml" -not -path "./build/*" -exec sed -i '' "s/com.example.library/$PACKAGE_NAME/g" {} \;
print_success "Android manifests updated"

# ============================================
# 10. Update iOS project
# ============================================
print_step "Updating iOS project..."

if [ -f "iosApp/iosApp.xcodeproj/project.pbxproj" ]; then
    sed -i '' "s/com.example.library.sample/$PACKAGE_NAME.sample/g" iosApp/iosApp.xcodeproj/project.pbxproj
    sed -i '' "s/MyLibrary Sample/$LIBRARY_NAME_PASCAL Sample/g" iosApp/iosApp.xcodeproj/project.pbxproj
    print_success "iOS project updated"
fi

# ============================================
# 11. Update iOS Swift files
# ============================================
print_step "Updating iOS Swift files..."

if [ -f "iosApp/iosApp/iOSApp.swift" ]; then
    sed -i '' "s/import sample/import ${LIBRARY_NAME}sample/g" iosApp/iosApp/iOSApp.swift 2>/dev/null || true
fi

# ============================================
# 12. Update README
# ============================================
print_step "Updating README..."

if [ -f "README.md" ]; then
    sed -i '' "s/KMP Starter/$LIBRARY_NAME_PASCAL/g" README.md
    sed -i '' "s/kmp-starter/$LIBRARY_NAME/g" README.md
    sed -i '' "s/com.example.library/$PACKAGE_NAME/g" README.md
    sed -i '' "s/MyLibrary/$LIBRARY_NAME_PASCAL/g" README.md
    print_success "README.md updated"
fi

# ============================================
# Done!
# ============================================
echo ""
echo "==========================================="
echo -e "${GREEN}✅ Setup Complete!${NC}"
echo "==========================================="
echo ""
echo "Your library '$LIBRARY_NAME_PASCAL' is ready!"
echo ""
echo "Next steps:"
echo "  1. Review the changes: git diff"
echo "  2. Build the library:  ./gradlew :library:build"
echo "  3. Run the sample:     ./gradlew :sample:run"
echo "  4. Commit your changes"
echo ""
echo "Documentation:"
echo "  - Update README.md with your library description"
echo "  - Update docs-site/ for your library's website"
echo ""
