# script.py

from github import Github
from config import *
import os
import json
import logging

# Setup logging
os.makedirs(os.path.dirname(LOG_FILE), exist_ok=True)
logging.basicConfig(filename=LOG_FILE, level=LOG_LEVEL, format='%(asctime)s - %(levelname)s - %(message)s')

# GitHub Auth
g = Github(GITHUB_TOKEN)
repo = g.get_repo(REPO_NAME)

def fetch_issues():
    logging.info("Fetching issues...")
    issues = repo.get_issues(state='all', since=START_DATE)
    result = []
    for issue in issues:
        if issue.created_at.isoformat() > END_DATE:
            continue
        if issue.pull_request:
            continue  # Skip PRs
        result.append(issue.raw_data)
        if len(result) >= PER_PAGE * MAX_PAGES:
            break
    os.makedirs(DATA_DIR, exist_ok=True)
    with open(ISSUE_DATA_FILE, "w") as f:
        json.dump(result, f, indent=2)
    logging.info(f"Saved {len(result)} issues.")

def fetch_pull_requests():
    logging.info("Fetching PRs...")
    pulls = repo.get_pulls(state='all', sort='created', direction='desc')
    result = []
    for pr in pulls:
        if pr.created_at.isoformat() < START_DATE:
            continue
        if pr.created_at.isoformat() > END_DATE:
            continue
        result.append(pr.raw_data)
        if len(resul
