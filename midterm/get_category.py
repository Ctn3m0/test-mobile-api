import requests
from bs4 import BeautifulSoup

url = "https://openlibrary.org/subjects"

payload={}
headers = {
  'authority': 'openlibrary.org',
  'pragma': 'no-cache',
  'cache-control': 'no-cache',
  'sec-ch-ua-mobile': '?0',
  'sec-ch-ua-platform': '"Windows"',
  'upgrade-insecure-requests': '1',
  'accept': 'text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9',
  'sec-fetch-site': 'cross-site',
  'sec-fetch-mode': 'navigate',
  'sec-fetch-user': '?1',
  'sec-fetch-dest': 'document',
  'accept-language': 'vi,en-US;q=0.9,en;q=0.8,vi-VN;q=0.7'
}

response = requests.request("GET", url, headers=headers, data=payload)
soup = BeautifulSoup(response.text,"html.parser")
_subjects = soup.find("div",{"id":"subjectsPage"})

with open('subjects','w') as _file:
    for _ in _subjects.findAll('a'):
        _string = _['href'][1:]
        if "subjects" in _string:
            _file.write(_string+'\n')