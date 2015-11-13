# -*- coding: utf-8 -*-

import json
from sys import argv, exit
from urllib2 import urlopen


# Make sure we have the correct command line arguments
if len(argv) < 4:
  print "Please provide command line arguments as follows:"
  print "python solrtrec.py <Formatted Query File> <Output File> <Model> (Boost)"
  exit(0)

urls = [] # (id, url) format

with open(argv[1]) as queries:
  for query in queries.read().splitlines():
    split = query.split(' ', 1)
    urls.append((split[0], split[1]))

if (len(argv) > 4):
  top = 21
else:
  top = 2
  
for x in range(1, top):
  boost = .1 * x
  booststr = '&qf=text_en%20text_de%20text_ru%20tweet_hashtags^' + str(boost)
  
  if (len(argv) > 4):
    output = open(argv[2] + str(boost) + '.txt', 'w')
  else:
    output = open(argv[2], 'w')

  for url in urls:
    if (len(argv) > 4):
      data = urlopen(url[1] + booststr)
    else:
      data = urlopen(url[1])
    docs = json.load(data)['response']['docs']
  
    rank = 1
    for doc in docs:
      output.write(url[0] + ' ' + 'Q0' + ' ' + str(doc['id']) + ' ' + str(rank) + ' ' + str(doc['score']) + ' ' + argv[3] + '\n')
      rank += 1

  output.close()
