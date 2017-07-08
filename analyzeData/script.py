#!/usr/bin/python3
import sys
import json

def handleLine(line):
    if line:
        ans = json.loads(line)
        comments = ans['comments']
        nbWord = 0
        for comment in comments:
            nbWord += len(comment['content'].split())
        ans['nbWords'] = nbWord
        print(ans)


for line in sys.stdin:
    handleLine(line.strip(" \n\r"))
