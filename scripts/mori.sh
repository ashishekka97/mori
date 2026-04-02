#!/bin/bash
# scripts/mori.sh
# A wrapper script to manage Gemini CLI sessions and prevent context bloat.

# 1. Configuration
# Set the model to flash-lite for cheaper/faster implementation turns
# Set approval mode to plan to ensure we always have a strategy first
GEMINI_OPTS="--approval-mode plan --model flash"

echo "🌳 Welcome to the Mori AI Task Manager"
echo "To keep billing costs low, we enforce a strict 1-Task = 1-Session rule."
echo "-------------------------------------------------------------------"
echo "1) Start a FRESH session for a new Roadmap Task (Recommended)"
echo "2) Start a FRESH session for general exploration"
echo "3) Continue an ongoing session (Interactive REPL)"
echo "-------------------------------------------------------------------"
read -p "Select an option [1/2/3]: " choice

if [ "$choice" == "1" ]; then
    read -p "Enter Task ID from ROADMAP (e.g., 7.1.1): " task_id
    echo ""
    
    # 2. Automated Task Initialization (Non-interactive)
    # This uses the -p flag to perform setup (Sync, Issue & Branch) without human turns.
    echo "🏗️  Automating Setup (Sync, Issue & Branch) for Task $task_id..."
    gemini -p "1. Sync: 'git checkout main && git pull origin main'.
               2. Validate: If Task $task_id is already checked [x] in ROADMAP.md, stop and warn the user.
               3. Issue: Check if a GitHub Issue for Task $task_id already exists using 'gh issue list'. If not, create one from ROADMAP.md.
               4. Branch: Create a fresh feature branch 'feature/$task_id-...' from main.
               Output ONLY the branch name when done."

    
    # 3. Interactive Coding Session
    # We pass the Task ID to start the implementation.
    echo "🚀 Booting interactive AI session for Implementation..."
    gemini $GEMINI_OPTS "I have created the branch. Let's start implementing Task $task_id from ROADMAP.md. Please activate the relevant skills and propose the implementation plan."

elif [ "$choice" == "2" ]; then
    echo ""
    echo "🚀 Booting fresh AI session..."
    gemini $GEMINI_OPTS "Let's start a fresh session. What is the current status of the project?"

elif [ "$choice" == "3" ]; then
    echo ""
    echo "🔄 Resuming interactive session..."
    gemini $GEMINI_OPTS

else
    echo "❌ Invalid choice. Exiting."
    exit 1
fi
