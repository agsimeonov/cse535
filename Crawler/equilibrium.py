import os
import random
import sys


ENGLISH = 'en'
GERMAN  = 'de'
RUSSIAN = 'ru'

# Make sure we have the correct command line arguments
if len(sys.argv) != 3:
  print "Please provide command line arguments as follows:"
  print "python equilibrium.py <Input Dir> <Output Dir>"
  sys.exit(0)

# Input directory
indir = sys.argv[1]

# Create output directory
outdir = sys.argv[2]
if not os.path.exists(outdir): 
  os.makedirs(outdir)

# Split file names into days
days = {}

for name in os.listdir(indir):
  day = int(name[2:10])

  if day not in days:
    days[day] = [name]
  else:
    days[day].append(name)

# Split each day into language specific bins
dayBins = {}

for day in days:
  # Sort file names into bins
  bins = {ENGLISH: [], GERMAN: [], RUSSIAN: []}

  for name in days[day]:
    if name.startswith(ENGLISH):
      bins.get(ENGLISH).append(name)
    elif name.startswith(GERMAN):
      bins.get(GERMAN).append(name)
    else:
      bins.get(RUSSIAN).append(name)

  dayBins[day] = bins
  
# Find the minimum tweets per language per day
minimum = sys.maxint

for day in dayBins:
  # Get size of each bin
  ensize = len(dayBins[day].get(ENGLISH))
  desize = len(dayBins[day].get(GERMAN))
  rusize = len(dayBins[day].get(RUSSIAN))

  # Get size of smallest bin
  smallest = min([ensize, desize, rusize])
  if smallest < minimum:
    minimum = smallest

# Create an equilibrium of tweets per language per day
for day in dayBins:
  #Create an equilibrium of tweets per language
  for names in dayBins[day].values():
    # Make sure to get a random sample
    random.shuffle(names)
    for name in names[:minimum]:
      # Symlinks should be sufficient
      os.symlink(os.path.join(indir, name), os.path.join(outdir, name))
