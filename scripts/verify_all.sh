#!/bin/bash
# scripts/verify_all.sh
# Runs the full Mori verification suite.

echo "🚀 Starting Mori Verification Suite..."

# 1. Zero Allocation Audit
./scripts/verify_engine.sh
if [ $? -ne 0 ]; then
    echo "❌ Engine Verification Failed."
    exit 1
fi

# 2. KtLint Check
echo "🧹 Running KtLint..."
./gradlew ktlintCheck --quiet
if [ $? -ne 0 ]; then
    echo "❌ KtLint Failed."
    exit 1
fi

# 3. Unit Tests
echo "🧪 Running Unit Tests..."
./gradlew test --quiet
if [ $? -ne 0 ]; then
    echo "❌ Unit Tests Failed."
    exit 1
fi

echo "🎉 All checks passed! Ready for PR."
exit 0
