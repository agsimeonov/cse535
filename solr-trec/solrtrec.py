# -*- coding: utf-8 -*-

import json
from sys import argv, exit
from urllib2 import urlopen

import goslate


# Make sure we have the correct command line arguments
if len(argv) != 5:
  print "Please provide command line arguments as follows:"
  print "python solrtrec.py <Formatted Query File> <Output File> <Domain> <Core>"
  exit(0)

port = 8983
prepend = "http://" + argv[3] + ":" + str(port) + "/solr/" + argv[4] + "/select?q="
append = '&fl=id%2Cscore&wt=json&indent=true&rows=1000'
urls = [] # (id, url) format
model='default'

gs = goslate.Goslate(service_urls=['http://translate.google.ca',
                                   'http://translate.google.nl',
                                   'http://translate.google.co.uk'])

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
    output.write(url[0] + ' ' + 'Q0' + ' ' + str(doc['id']) + ' ' + str(rank) + ' ' + str(doc['score']) + ' ' + model + '\n')
    rank += 1

output.close()
