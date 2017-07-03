import requests
import time
import json

BASE_URL = 'https://api.themoviedb.org/3/'
API_KEY = '935ab085c098ef9bc68d3d49d2ae8140'

# def createSession():
#     res = requests.get(BASE_URL + 'authentication/token/new?api_key=' + API_KEY)
#     print(res.json())
#     with open('api.txt', mode='w'):


def getDiscover(year, page='1'):
    try:
        res = requests.get(BASE_URL + 'discover/movie?api_key=' + API_KEY +
                           '&page=' + page + '&primary_release_year=' + year)
        return res
    except:
        print("***************************** SPLEEPING :(")
        time.sleep(1)
        return getReview(year, id)


def getReview(id):
    try:
        res = requests.get(BASE_URL + 'movie/' + id + '/reviews?api_key=' + API_KEY)
        return res
    except:
        print("***************************** SPLEEPING :(")
        time.sleep(1)
        return getReview(id)


with open('data.json', 'w') as f:
    f.write('{movies: [\n')
    for year in range(1950, 2017):
        print('>>>>>>>>>>>>>>>>>> YEAR IS', year)
        try:
            for page in range(1, 1000):
                print('>>>>>> PAGE IS', page, 'IN', year)
                try:
                    res = getDiscover(str(year), str(page)).json()
                    for r in res['results']:
                        try:
                            res_reviews = getReview(str(r['id'])).json()
                            if len(res_reviews['results']) > 0:
                                r['comments'] = res_reviews['results']
                                # print(r)
                                json.dump(r, f)
                                f.write(',\n')
                        except:
                            print("Weird stuff3... continuing")
                            continue
                except:
                    print("Weird stuff2... continuing")
                    continue
        except:
            print("Weird stuff1... continuing")
            continue
    f.write(']}')
