#!/bin/bash
# Mori: Plan Cleanup Script
# Deletes all temporary plans in .gemini/plans to keep the repo clean.

PLAN_DIR=".gemini/plans"

if [ -d "$PLAN_DIR" ]; then
    echo "Cleaning up $PLAN_DIR..."
    rm -rf "$PLAN_DIR"/*.md
    echo "Done."
else
    echo "Plan directory $PLAN_DIR does not exist. Nothing to clean."
fi
