#!/bin/bash
# scripts/verify_engine.sh
# Enforces the Zero-Allocation mandate in the :engine module.

echo "🔍 Auditing :engine module for Zero-Allocation violations..."

# Look for forbidden object allocations in the engine's main source
# - "\.copy(" (Kotlin data class copies)
# - "Rect(" (Targeting android.graphics.Rect specifically, avoiding interface methods like drawRect)
# - "Paint(" or "Path(" (Canvas object allocations inside loops)
# - "new " (Java-style, unlikely in Kotlin)
# Note: We exclude tests and focus on src/main.

# We use [^wd] to ensure we don't catch "drawRect" or "drawPath"
VIOLATIONS=$(grep -rnE "\.copy\(|[^wd]Rect\(|[^wd]Paint\(|[^wd]Path\(" engine/src/main/java/)

if [ -n "$VIOLATIONS" ]; then
    echo "❌ ERROR: Zero-Allocation Violations Found in :engine!"
    echo "The following lines appear to allocate objects on the hot path:"
    echo "$VIOLATIONS"
    echo ""
    echo "Please refactor to use pre-allocated objects or primitives."
    exit 1
else
    echo "✅ SUCCESS: No obvious allocation violations found in :engine."
    exit 0
fi
