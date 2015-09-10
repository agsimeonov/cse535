# -*- coding: utf-8 -*-

import datetime
import json
import os
import sys
import traceback

import tweepy


ENGLISH = 'en'
GERMAN =  'de'
RUSSIAN = 'ru'

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
  print "You have successfully logged on as: " + api.me().screen_name

# Initialize variables
topic =  ['politics', 
          'elections', 
          'government']
topic += ['politik', 
          'politologie', 
          'staatskunst', 
          'politische', 
          'wahlen', 
          'regierung', 
          'regierungsform', 
          'gouvernement']
topic += [u'политика', 
          u'политическая', 
          u'политические', 
          u'выборы', 
          u'правительство']

#Create output directory
directory = sys.argv[5]
if not os.path.exists(directory): 
  os.makedirs(directory)

# Initialize files
# file = open(directory + time.strftime('%Y%m%d-%H%M%S') + extension, 'w')

class StreamListener(tweepy.StreamListener):
  def __init__(self):
    self.decount = 0
    self.encount = 0
    self.rucount = 0
    
  def on_data(self, data):
    decoded = json.loads(data)
    language = ('%s' % (decoded['lang'].encode('utf-8', 'ignore')))
    utc = datetime.datetime.utcnow().strftime('%Y%m%dT%H%M%S%fZ')
    path = os.path.join(directory, language + utc + '.json')

  # Store the data
    with open(path, 'w') as out:
      out.write(data)

  # Show current status
    if language == ENGLISH:
      self.encount += 1
    elif language == GERMAN:
      self.decount += 1
    else:
      self.rucount += 1

    sys.stdout.write('\r\x1b[K' + 'English processed: ' + str(self.encount) + ' ' +
                                  'German processed: '  + str(self.decount) + ' ' +
                                  'Russian processed: ' + str(self.rucount))
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
  stream.filter(track=topic, languages=[ENGLISH, GERMAN, RUSSIAN])
except KeyboardInterrupt:
  pass
except:
  traceback.print_exc()
finally:
  print "\nGoodbye!"
