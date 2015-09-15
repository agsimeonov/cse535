import os
import random
import sys


ENGLISH = 'en'
GERMAN  = 'de'
RUSSIAN = 'ru'

total = 0

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

  # Get size of each bin
  ensize = len(bins.get(ENGLISH))
  desize = len(bins.get(GERMAN))
  rusize = len(bins.get(RUSSIAN))

  # Get size of smallest bin
  minimum = min([ensize, desize, rusize])
  
  total += minimum * 3
  print str(day) + " - Daily total: " + str(minimum * 3) + "\tPer language: " + str(minimum)
    
  #Create an equilibrium of tweets for each language
  for names in bins.values():
    # Make sure to get a random sample
    random.shuffle(names)
    for name in names[:minimum]:
      # Symlinks should be sufficient
      os.symlink(os.path.join(indir, name), os.path.join(outdir, name))

print "Total: " + str(total)
