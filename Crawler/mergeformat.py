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
  formattedTweet['text'] = rawTweet['text']
  formattedTweet['lang'] = rawTweet['lang']
  createdAt = time.strptime(rawTweet['created_at'], '%a %b %d %H:%M:%S +0000 %Y')
  formattedTweet['created_at'] = time.strftime('%Y-%m-%dT%H:%M:%SZ', createdAt)
  formattedTweet['twitter_hashtags'] = []
  formattedTweet['twitter_urls'] = []
  for hashtagsDict in rawTweet['entities']['hashtags']:
    formattedTweet['twitter_hashtags'].append(hashtagsDict['text'])
  for urlsDict in rawTweet['entities']['urls']:
    formattedTweet['twitter_urls'].append(urlsDict['expanded_url'])
  if 'media' in rawTweet['entities']:
    for mediaDict in rawTweet['entities']['media']:
      formattedTweet['twitter_urls'].append(urlsDict['expanded_url'])
  formattedTweet['twitter_urls'] = list(set(formattedTweet['twitter_urls']))
  
  # Add to formatted tweet to merge list
  merge.append(formattedTweet)

# Dump merged file
with open(sys.argv[2], 'w') as outputFile:
  outputFile.write(json.dumps(merge, indent=4, sort_keys=True, ensure_ascii=False).encode('utf8'))
