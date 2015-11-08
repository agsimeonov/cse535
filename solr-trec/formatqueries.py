# -*- coding: utf-8 -*-

import codecs
from sys import argv, exit
from urllib2 import quote

import goslate


# Make sure we have the correct command line arguments
if len(argv) != 5:
  print "Please provide command line arguments as follows:"
  print "python solrtrec.py <Query File> <Output File> <Domain> <Core>"
  exit(0)

port = 8983
prepend = 'http://' + argv[3] + ':' + str(port) + '/solr/' + argv[4] + '/select?q='
append = '&fl=id%2Cscore&wt=json&indent=true&rows=1000'
urls = [] # (id, url) format

gs = goslate.Goslate(service_urls=['http://translate.google.ca',
                                   'http://translate.google.nl',
                                   'http://translate.google.co.uk'])

with codecs.open(argv[1],encoding='utf-8') as queries:
  for query in queries.read().splitlines():
    split = query.split(' ', 1)
    en = gs.translate(split[1], 'en')
    de = gs.translate(split[1], 'de')
    ru = gs.translate(split[1], 'ru')
    q =  'text_en:"' + en + '" OR '
    q += 'text_de:"' + de + '" OR '
    q += 'text_ru:"' + ru + '"'
    q= quote(q.encode('utf-8'))
    url = prepend + q + append
    urls.append((split[0], url))

with open(argv[2], 'w') as output:
  for url in urls:
    output.write(url[0] + ' ' + url[1] + '\n')
