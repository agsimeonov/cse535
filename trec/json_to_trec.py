# -*- coding: utf-8 -*-
"""
Created on Tue Oct 27 17:47:48 2015

@author: ruhansa
"""
import json
# if you are using python 3, you should 
# import urllib.request 
import urllib2


# change the url according to your own koding username and query
inurl = 'http://ruhan.koding.io:8983/solr/partb/select?q=*%3A*&fl=id%2Cscore&wt=json&indent=true&rows=1000'
outfn = 'path to your file.txt'


# change query id and IRModel name accordingly
qid = ''
IRModel='default'
outf = open(outfn, 'a+')
data = urllib2.urlopen(inurl)
# if you're using python 3, you should use
# data = urllib.request.urlopen(inurl)

docs = json.load(data)['response']['docs']
# the ranking should start from 1 and increase
rank = 1
for doc in docs:
    outf.write(qid + ' ' + 'Q0' + ' ' + str(doc['id']) + ' ' + str(rank) + ' ' + str(doc['score']) + ' ' + IRModel + '\n')
    rank += 1
outf.close()
