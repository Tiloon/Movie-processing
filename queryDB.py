import requests
import time
import json
import configparser
from kafka import KafkaProducer




config = configparser.ConfigParser()
config.read('query.conf')

FROM_INTERNET = config['GENERAL']['FROM_INTERNET'] == 'True'
SAVE_TO_FILE = config['GENERAL']['SAVE_TO_FILE'] == 'True'
BASE_URL = config['API']['BASE_URL']
API_KEY = config['API']['KEY']


WRITE_FILE = config['FILE']['WRITE']
READ_FILE = config['FILE']['READ']

KAFKA_IP = config['KAFKA']['IP']
KAFKA_PORT = config['KAFKA']['PORT']
TOPIC = config['KAFKA']['TOPIC']


def getDiscover(year, page='1'):
    try:
        res = requests.get(BASE_URL + 'discover/movie?api_key=' + API_KEY +
                           '&page=' + page + '&primary_release_year=' + year)
        return res
    except:
        time.sleep(1)
        return getReview(year, id)


def getReview(id):
    try:
        res = requests.get(BASE_URL + 'movie/' + id + '/reviews?api_key=' + API_KEY)
        return res
    except:
        time.sleep(1)
        return getReview(id)


def fetch_db_internet(producer, f=None):
    for page in range(1, 1000):
        print('>>>>>>>>>>>>>>>>>> PAGE IS', page)
        try:
            for year in range(1950, 2017):
                print('>>>>>> PAGE IS', page, 'IN', year)
                try:
                    res = getDiscover(str(year), str(page)).json()
                    for r in res['results']:
                        try:
                            res_reviews = getReview(str(r['id'])).json()
                            if len(res_reviews['results']) > 0:
                                r['comments'] = res_reviews['results']
                                if SAVE_TO_FILE and not f is None:
                                    json.dump(r, f)
                                    f.write('\n')
                                producer.send(TOPIC, r)
                                # print('Send:', r)
                        except:
                            print("Weird stuff3... continuing")
                            continue
                except:
                    print("Weird stuff2... continuing")
                    continue
        except:
            print("Weird stuff1... continuing")
            continue


def fetch_db_file(producer, f):
    for i, line in enumerate(f):
        if len(line) > 1:
            data = json.loads(line)
            print('Send:', data)
            producer.send(TOPIC, data)


def fetch():
    producer = KafkaProducer(bootstrap_servers=[KAFKA_IP + ':' + KAFKA_PORT],
                             value_serializer=lambda m: json.dumps(m).encode('ascii'))
    if FROM_INTERNET:
        f = None
        if SAVE_TO_FILE:
            f = open(WRITE_FILE, 'w')
        fetch_db_internet(producer, f)
    else: # From file
        with open(READ_FILE, 'r') as f:
            fetch_db_file(producer, f)
    print('Start fulshing to kafka...')
    producer.flush()


fetch()
