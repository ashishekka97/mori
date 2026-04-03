#!/bin/bash
# scripts/mori.sh
# A wrapper script to manage Mori tasks with separated Planning and Implementation stages.

# 1. Configuration
# We use the latest 3.1 models
PLAN_MODEL="pro"      # Gemini 3.1 Pro for deep reasoning
EXEC_MODEL="flash"    # Gemini 3.1 Flash for surgical execution

echo "🌳 Welcome to the Mori AI Task Manager"
echo "-------------------------------------------------------------------"
echo "1) [PLAN] Start a new task (Sync, Branch, Issue & Research)"
echo "2) [EXEC] Implement a task (Requires an approved plan)"
echo "3) [REPL] Continue a session or general exploration"
echo "-------------------------------------------------------------------"
read -p "Select an option [1/2/3]: " choice

if [ "$choice" == "1" ]; then
    read -p "Enter Task ID from ROADMAP (e.g., 7.1.2): " task_id
    echo ""
    
    # Automated Setup
    echo "🏗️  Automating Setup (Sync, Issue & Branch) for Task $task_id..."
    gemini --approval-mode yolo -p "1. Sync: 'git checkout main && git pull origin main'.
               2. Validate: If Task $task_id is already checked [x] in ROADMAP.md, stop and warn the user.
               3. Issue: Check if a GitHub Issue for Task $task_id already exists using 'gh issue list'. If not, create one from ROADMAP.md.
               4. Branch: Create a fresh feature branch 'feature/$task_id-...' from main.
               Output ONLY the branch name when done."

    # Interactive Planning Session
    echo "🧠 Booting Planning session ($PLAN_MODEL)..."
    gemini --model $PLAN_MODEL --approval-mode default "I have created the branch for Task $task_id. 
               Please research the codebase, activate relevant skills, and propose a detailed implementation plan. 
               Save the plan to '.gemini/plans/$task_id.md' and wait for user approval.
               Do NOT start implementation until I run the [EXEC] command."

elif [ "$choice" == "2" ]; then
    read -p "Enter Task ID to implement (e.g., 7.1.2): " task_id
    
    PLAN_FILE=".gemini/plans/$task_id.md"
    if [ ! -f "$PLAN_FILE" ]; then
        echo "❌ No approved plan found at $PLAN_FILE."
        echo "Please run option [1] first to create a plan."
        exit 1
    fi

    echo "🚀 Booting Implementation session ($EXEC_MODEL)..."
    gemini --model $EXEC_MODEL --approval-mode default "Let's implement Task $task_id. 
               The approved plan is in '$PLAN_FILE'. 
               Follow it strictly, apply surgical changes, and run verification scripts from TOOLS.md when done."

elif [ "$choice" == "3" ]; then
    echo ""
    echo "🔄 Resuming interactive session ($EXEC_MODEL)..."
    gemini --model $EXEC_MODEL --approval-mode default

else
    echo "❌ Invalid choice. Exiting."
    exit 1
fi
