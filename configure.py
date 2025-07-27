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
        result.append(issue.raw_data)
        if len(result) >= PER_PAGE * MAX_PAGES:
            break
print(hello)

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
