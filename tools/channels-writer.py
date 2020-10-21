import os
import json

from typing import Final

# settings
IN_FILE: Final = "../channels.json"
OUT_FILE: Final = "../channels.txt"

with open(os.path.join(*IN_FILE.split("/")), "r+") as input_file:
    # parse
    parsed = json.loads(input_file.read())
    
    # write to file
    with open (os.path.join(*OUT_FILE.split("/")), "w+") as output_file:
        for channel in parsed['channels']:
            output_file.write(channel['url'] + " // " + channel['title'] + "\n")