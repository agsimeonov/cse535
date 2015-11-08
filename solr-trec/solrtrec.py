# -*- coding: utf-8 -*-

import json
from sys import argv, exit
from urllib2 import urlopen


# Make sure we have the correct command line arguments
if len(argv) != 4:
  print "Please provide command line arguments as follows:"
  print "python solrtrec.py <Formatted Query File> <Output File> <Model>"
  exit(0)

urls = [] # (id, url) format

with open(argv[1]) as queries:
  for query in queries.read().splitlines():
    split = query.split(' ', 1)
    urls.append((split[0], split[1]))

output = open(argv[2], 'w')

for url in urls:
  data = urlopen(url[1])
  docs = json.load(data)['response']['docs']
  
  rank = 1
  for doc in docs:
    output.write(url[0] + ' ' + 'Q0' + ' ' + str(doc['id']) + ' ' + str(rank) + ' ' + str(doc['score']) + ' ' + argv[3] + '\n')
    rank += 1

output.close()
