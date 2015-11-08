# -*- coding: utf-8 -*-

import codecs
from sys import argv, exit
from urllib2 import quote


# Make sure we have the correct command line arguments
if len(argv) != 5:
  print "Please provide command line arguments as follows:"
  print "python solrtrec.py <Query File> <Output File> <Domain> <Core>"
  exit(0)

port = 8983
prepend = "http://" + argv[3] + ":" + str(port) + "/solr/" + argv[4] + "/select?"
append = '&fl=id%2Cscore&wt=json&indent=true&rows=1000'
urls = [] # (id, url) format

# print quote(u"РФ в Сирии вынудили 250 тунисских боевиков бежать".encode('UTF-8'))

with codecs.open(argv[1],encoding='utf-8') as queries:
  for query in queries.read().splitlines():
    split = query.split(' ', 1)
    url = prepend + quote(split[1].encode('utf-8')) + append
    urls.append((split[0], url))
