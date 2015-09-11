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

# Sort file paths into bins
dictionary = {ENGLISH: [], GERMAN: [], RUSSIAN: []}

for name in os.listdir(indir):
  if name.startswith(ENGLISH):
    dictionary.get(ENGLISH).append(name)
  elif name.startswith(GERMAN):
    dictionary.get(GERMAN).append(name)
  else:
    dictionary.get(RUSSIAN).append(name)

# Get size of each bin
ensize = len(dictionary.get(ENGLISH))
desize = len(dictionary.get(GERMAN))
rusize = len(dictionary.get(RUSSIAN))

# Get size of smallest bin
minimum = min([ensize, desize, rusize])

# Create an equilibrium of tweets for each language
for names in dictionary.values():
  # Make sure to get a random sample
  random.shuffle(names)
  for name in names[:minimum]:
    # Symlinks should be sufficient
    os.symlink(os.path.join(indir, name), os.path.join(outdir, name))
