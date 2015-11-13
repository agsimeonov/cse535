from os import listdir, makedirs
from os.path import isfile, join, basename, splitext, exists
from subprocess import call
from sys import argv


# Make sure we have the correct command line arguments
if len(argv) != 4:
  print "Please provide command line arguments as follows:"
  print "python runtrec.py <Trec> <Input Directory> <Relevance Judgements>"
  exit(0)

files = [ join(argv[2], f) for f in listdir(argv[2]) if isfile(join(argv[2],f)) ]
resultpath = join(argv[2], 'result')

if not exists(resultpath):
    makedirs(resultpath)

for f in files:
  name = splitext(basename(f))[0]
  outpath = join(resultpath, name)
  with open(outpath+".txt", 'w') as outfile:
    call([argv[1], '-q', '-c', '-M', '3440', argv[3], f], stdout=outfile)
  with open(outpath+"-map.txt", 'w') as outfile:
    call([argv[1], '-q', '-c', '-M', '3440', '-m', 'map', argv[3], f], stdout=outfile)
  with open(outpath+"-ndcg.txt", 'w') as outfile:
    call([argv[1], '-q', '-c', '-M', '3440', '-m', 'ndcg',  argv[3], f], stdout=outfile)
  with open(outpath+"-bpref.txt", 'w') as outfile:
    call([argv[1], '-q', '-c', '-M', '3440', '-m', 'bpref',  argv[3], f], stdout=outfile)
  with open(outpath+"-f05.txt", 'w') as outfile:
    call([argv[1], '-q', '-c', '-M', '3440', '-m', 'set_F.0.5',  argv[3], f], stdout=outfile)
