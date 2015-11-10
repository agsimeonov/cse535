# -*- coding: utf-8 -*-

import codecs
import json
from sys import argv, exit
from urllib2 import quote, urlopen

import goslate


# Make sure we have the correct command line arguments
if len(argv) != 5:
  print "Please provide command line arguments as follows:"
  print "python solrtrec.py <Query File> <Output File> <Domain> <Core>"
  exit(0)

port = 8983
prepend = 'http://' + argv[3] + ':' + str(port) + '/solr/' + argv[4] + '/select?q='
append = '&fl=id%2Cscore&wt=json&indent=true&rows='
urls = [] # (id, url) format

data = urlopen(prepend + '*' + append + '0')
rows = json.load(data)['response']['numFound']

gs = goslate.Goslate(service_urls=['http://translate.google.ca',
                                   'http://translate.google.nl',
                                   'http://translate.google.co.uk'])

def escapeSpecialCharacters(text):
  for character in ['\\', '+', '-', '&&', '||', '!', '(', ')', '{', '}', '[', ']', '^', '"', '~', '*', '?', ':', '/', ' ']:
    if character in text:
      text = text.replace(character, '\\' + character)
  return text

with codecs.open(argv[1],encoding='utf-8') as queries:
  for query in queries.read().splitlines():
    split = query.split(' ', 1)
    en = escapeSpecialCharacters(gs.translate(split[1], 'en'))
    de = escapeSpecialCharacters(gs.translate(split[1], 'de'))
    ru = escapeSpecialCharacters(gs.translate(split[1], 'ru'))
    q = en + ' OR ' + de + ' OR ' + ru
    q = quote(q.encode('utf-8'))
    url = prepend + q + append + str(rows)
    urls.append((split[0], url))

with open(argv[2], 'w') as output:
  for url in urls:
    output.write(url[0] + ' ' + url[1] + '\n')
