import os
import collections
from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn.svm import LinearSVC
import pickle


class Payload:
    def __init__(self, svm, vect):
        self.svm = svm
        self.vect = vect

    def use(self, comm):
        print(type(comm[0]))
        return self.svm.predict(self.vect.transform(comm))


def train(pos_path, neg_path):
    payload = Payload(LinearSVC(C=1000), TfidfVectorizer(min_df=3, max_df=0.95, sublinear_tf=True, use_idf=True))
    train_data, train_labels = [], []
    print('Start load positive')
    extract(pos_path, train_data, train_labels, 'positive')
    print('Start load negative')
    extract(neg_path, train_data, train_labels, 'negative')
    print('Start fitting')
    payload.svm.fit(payload.vect.fit_transform(train_data), train_labels)
    print('rained!')
    return payload


def extract(path, train_data, train_labels, label):
    for rootDir, _, files in os.walk(path):
        for file in files:
            with open(os.path.join(rootDir, file), 'r', encoding='utf-8', errors='ignore') as f:
                train_data.append(f.read())
                train_labels.append(label)


def extract2(path):
    datas = []
    nb = 0
    for rootDir, _, files in os.walk(path):
        for file in files:
            nb += 1
            with open(os.path.join(rootDir, file), 'r', encoding='utf-8', errors='ignore') as f:
                datas.append(f.read())

    return datas, nb

def testPayload(payload, pos_path, neg_path):
    poss, nbpos = extract2(pos_path)
    negs, nbneg = extract2(neg_path)
    posGood = collections.Counter(payload.use(poss))['positive']
    negGood = collections.Counter(payload.use(negs))['negative']

    print('Positive: Pos - ', posGood, '/', nbpos, '=>', posGood/nbpos)
    print('Positive: Neg - ', negGood, '/', nbneg, '=>', negGood / nbneg)

payload = train('/home/hugo/Téléchargements/aclImdb/test/pos', '/home/hugo/Téléchargements/aclImdb/test/neg')

with open('./payload.pickle', 'wb') as f:
    pickle.dump(payload, f)

with open('./payload.pickle', 'rb') as f:
    p = pickle.load(f)
    testPayload(p, '/home/hugo/Téléchargements/aclImdb/train/pos', '/home/hugo/Téléchargements/aclImdb/train/neg')
