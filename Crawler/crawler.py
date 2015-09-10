import json
import sys
import traceback

import tweepy

# Make sure we have the correct command line arguments
if len(sys.argv) != 6:
  print "Please provide command line arguments as follows:"
  print "python crawler.py <API Key> <API Secret> <Token> <Token Secret> <Output Dir>"
  sys.exit(0)

# Application settings
apiKey = sys.argv[1]
apiSecret = sys.argv[2]

# Your access token
accesToken = sys.argv[3]
accessTokenSecret = sys.argv[4]

# Build an OAuthHandler
auth = tweepy.OAuthHandler(apiKey, apiSecret)
auth.set_access_token(accesToken, accessTokenSecret)

# Construct the API instance
api = tweepy.API(auth)

# Verify credentials
try:
  api.verify_credentials()
except tweepy.error.TweepError:
  print "Failed to authenticate, please provide correct credentials!"
  sys.exit(0)
else:
  print "You have successfully logged on as: " + api.me().screen_name + "\n"

# Initialize variables
topic = ['politics', 'elections', 'political rallies', 'policy changes', 'government']
topic += ['politik', 'wahlen', 'politische kundgebungen', 'regierung']

# directory = './data/'
# extension = '.txt'
# count = 0
# actualCount = 0
# maxCount = 1000
# flushCount = 100

# Create data directories
# if not os.path.exists(directory): os.makedirs(directory)

# Initialize files
# file = open(directory + time.strftime('%Y%m%d-%H%M%S') + extension, 'w')

class StreamListener(tweepy.StreamListener):
  def __init__(self):
    self.count = 0
    self.actualCount = 0
    
  def on_data(self, data):
    decoded = json.loads(data)
    username = ('%s' % (decoded['user']['screen_name'])).lower()
    followersCount = ('%i' % (decoded['user']['followers_count']))
    tweet = ('%s' % (decoded['text'].encode('utf-8', 'ignore')))
    language = ('%s' % (decoded['lang'].encode('utf-8', 'ignore')))
#         mentions = ''
#         hashtags = ''
#         
#         # Collect coordinates when possible (longitude then latitude)
#         if decoded['coordinates'] is not None:
#             writer.writerow([decoded['coordinates']['coordinates'][0],
#                             decoded['coordinates']['coordinates'][1]])
#         
#         # Remove quotations
#         tweet = tweet.replace('"', '')
#         
#         # Remove links
#         tweet = re.sub(r'\w+:\/{2}[\d\w-]+(\.[\d\w-]+)*(?:(?:\/[^\s/]*))*',
#                        '', tweet)
#         
#         # Cleanup unneeded retweet characters
#         tweet = ' '.join(tweet.split('\n')).lower()
#         if tweet.startswith('rt @'):
#             tweet = tweet.replace(tweet[:3], '')
#         tweet = tweet.replace(':', ' ')
#         
#         # Clean up symbols and numbers
#         tweet = re.sub('[^a-zA-Z@#\n]', ' ', tweet)
#         
#         # Make sure mentions and hashtags are separated by a space
#         tweet = re.sub(r"(\w)(['@|#'])", r"\1 \2", tweet)
#         
#         # Truncate consecutive hashtags to 1
#         tweet = re.sub(r'(#)\1+', r'\1', tweet)
#         
#         # Collect mention usernames and clean them up from the tweet
#         for mention in tweet.split():
#             if mention.startswith('@'):
#                 if len(mention) != 1:
#                     cleanMention = mention.replace('@', ' ', 1)
#                     cleanMention = re.sub('[^a-zA-Z0-9_]', '', cleanMention)
#                     if len(cleanMention) <= 15:
#                         mentions = mentions + ' ' + cleanMention
#                 tweet = tweet.replace(mention, '')
#     
#         # Collect hashtags and clean them up from the tweet
#         for hashtag in tweet.split():
#             if hashtag.startswith('#'):
#                 if len(hashtag) != 1:
#                     cleanHashtag = re.sub('[^a-zA-Z0-9#]', '', hashtag)
#                     hashtags = hashtags + ' ' + cleanHashtag
#                 tweet = tweet.replace(hashtag, '')
#         
#         # Remove words with less than 2 characters
#         tweet = ' '.join(word for word in tweet.split() if len(word) > 2)
# 
#         # Truncate/remove unneeded spaces
#         tweet = ' '.join(tweet.split())
#         mentions = ' '.join(mentions.split())
#         hashtags = ' '.join(hashtags.split())
#         
#         # Split data into different files
#         if count == maxCount:
#             file.close()
#             count = 0
#             file = open(directory +
#                         time.strftime('%Y%m%d-%H%M%S') + extension, 'w')
#         
#         # Write data to file
#         file.write('%s | ' % username)
#         file.write('%s | ' % followersCount)
#         file.write('%s | ' % mentions)
#         file.write('%s | ' % hashtags)
#         file.write('%s |\n' % tweet)
# 
        # Flush and print current status
    self.count = self.count + 1
    self.actualCount = self.actualCount + 1
#         if (count % flushCount) == 0:
#             file.flush()
#             coordinates.flush()
    sys.stdout.write('\r\x1b[K' + 'Tweets processed: ' + str(self.actualCount))
    sys.stdout.flush()

    return True

  def on_error(self, status_code):
    print >> sys.stderr, 'Twitter API Error Code: ' + status_code
    return True
                   
  def on_timeout(self):
    return True

listener = StreamListener()
stream = tweepy.Stream(auth, listener)

try:
  stream.filter(track=topic, languages=['ru', 'en', 'de'])
except KeyboardInterrupt:
  pass
#     file.close()
except:
#     file.close()
  traceback.print_exc()
finally:
  print "\n\nGoodbye!"
