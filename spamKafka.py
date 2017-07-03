from kafka import KafkaProducer
import json

KAFKA_IP = '192.168.5.70'
KAFKA_PORT = '9092'
TOPIC = 'test'
DB = 'backup-data.json'

# produce json messages


producer = KafkaProducer(bootstrap_servers=[KAFKA_IP + ':' + KAFKA_PORT],
                         value_serializer=lambda m: json.dumps(m).encode('ascii'))

with open(DB, 'r') as db:
    for i, line in enumerate(db):
        if i < 1 or i > 100:
            continue
        print(line[:-2])
        data = json.loads(line[:-2])
        print(data)
        producer.send(TOPIC, data)

# block until all async messages are sent
producer.flush()
