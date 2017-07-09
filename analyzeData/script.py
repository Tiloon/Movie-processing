#!/usr/bin/python3
import sys
import json
from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn.svm import LinearSVC
import pickle


class Payload:
    def __init__(self, svm, vect):
        self.svm = svm
        self.vect = vect

    def use(self, comm):
        return self.svm.predict(self.vect.transform(comm))

def handleLine(line, payload):
    if line:
        ans = json.loads(line)
        comments = ans['comments']
        for comment in comments:
            comment['sentiment'] = 1 if payload.use([comment['content'].encode()])[0] == 'positive' else 0
        print(json.dumps(ans))


with open(sys.argv[0].rsplit('/', 1)[0] + '/payload.pickle', 'rb') as f:
    payload = pickle.load(f)

    for line in sys.stdin:
        handleLine(line.strip(" \n\r"), payload)
