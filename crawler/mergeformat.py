import json
import os
import sys
import time


# Make sure we have the correct command line arguments
if len(sys.argv) != 3:
  print "Please provide command line arguments as follows:"
  print "python equilibrium.py <Input Dir> <Output File>"
  sys.exit(0)
  
# Input directory
directory = sys.argv[1]

# Initialize merge list
merge = []

# Format each raw tweet and put into merge list
for filename in os.listdir(directory):
  formattedTweet = {}

  # Get raw tweet
  with open(os.path.join(directory, filename), 'r') as rawFile:
    rawTweet = json.loads(rawFile.read())

  # Create formatted tweet
  formattedTweet['id'] = rawTweet['id']

  if 'retweeted_status' in rawTweet:
    text = rawTweet['retweeted_status']['text']
  else:
    text = rawTweet['text']
  lang = rawTweet['lang']

  if lang == 'de':
    formattedTweet['text_de'] = text
  elif lang == 'en':
    formattedTweet['text_en'] = text
  else:
    formattedTweet['text_ru'] = text

  formattedTweet['lang'] = lang

  createdAt = time.strptime(rawTweet['created_at'], '%a %b %d %H:%M:%S +0000 %Y')
  formattedTweet['created_at'] = time.strftime('%Y-%m-%dT%H:%M:%SZ', createdAt)

  formattedTweet['tweet_hashtags'] = []
  formattedTweet['tweet_urls'] = []

  for hashtagsDict in rawTweet['entities']['hashtags']:
    formattedTweet['tweet_hashtags'].append(hashtagsDict['text'])
  for urlsDict in rawTweet['entities']['urls']:
    formattedTweet['tweet_urls'].append(urlsDict['expanded_url'])
  if 'media' in rawTweet['entities']:
    for mediaDict in rawTweet['entities']['media']:
      formattedTweet['tweet_urls'].append(mediaDict['media_url'])

  formattedTweet['tweet_urls'] = list(set(formattedTweet['tweet_urls']))

  # Add to formatted tweet to merge list
  merge.append(formattedTweet)

# Dump merged file
with open(sys.argv[2], 'w') as outputFile:
  outputFile.write(json.dumps(merge, indent=4, sort_keys=True, ensure_ascii=False).encode('utf8'))
