import os
import sys
import zipfile


# Make sure we have the correct command line arguments
if len(sys.argv) != 3:
  print "Please provide command line arguments as follows:"
  print "'python ziptool.py <Directory> <Zip File>' to append to zip file"
  print "'python ziptool.py <Zip File> <Directory>' to extract from zip file"
  sys.exit(0)

if os.path.exists(sys.argv[1]):
  if os.path.isdir(sys.argv[1]):
    directory = sys.argv[1]
    zfilepath = sys.argv[2]
    zipmode = True
  else:
    directory = sys.argv[2]
    zfilepath = sys.argv[1]
    zipmode = False
else:
  if not os.path.isdir(sys.argv[2]):
    directory = sys.argv[1]
    zfilepath = sys.argv[2]
    zipmode = True
  else:
    directory = sys.argv[2]
    zfilepath = sys.argv[1]
    zipmode = False

if zipmode:
  with zipfile.ZipFile(zfilepath, 'a', zipfile.ZIP_DEFLATED) as zfile:
    for filename in os.listdir(directory):
      filepath = os.path.join(directory, filename)
      zfile.write(filepath, filename, zipfile.ZIP_DEFLATED)
      if filename != os.path.basename(zfilepath):
        os.remove(filepath)
  if not os.path.exists(os.path.join(directory, os.path.basename(zfilepath))):
    os.rmdir(directory)
else:
  with zipfile.ZipFile(zfilepath, "r") as zfile:
    zfile.extractall(directory)
