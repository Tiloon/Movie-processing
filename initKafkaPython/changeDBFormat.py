with open("data.json", 'r') as db_old:
    with open("data2.json", 'w') as db_new:
        for i, line in enumerate(db_old):
            db_new.write(line[:-2] + '\n')
            if i < 10:
                print(line[:-2])