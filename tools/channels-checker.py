import os
import requests
import json

from typing import Final

# settings
IN_FILE: Final = "../channels_in.txt"
OUT_FILE: Final = "../channels.json"

# Types
TYPE_ERROR: Final = "❌"
TYPE_UNKNOWN: Final = "👾"
TYPE_CHANNEL: Final = "📺"
TYPE_USER: Final = "👨‍"

"""
This function returns the type of a specified entity.
[Error, Unknown, Channel, User]
"""
def get_type(entity: str) -> [str, str]:
    url: Final = f"https://t.me/{entity}"
    
    # get request
    res = requests.get(url, allow_redirects=False)

    # when redirected to https://telegram.org, the entity is invalid
    if res.status_code == 302:
        return TYPE_UNKNOWN, None
    elif res.status_code != 200:
        return TYPE_ERROR, None

    txt = res.text
    if 'tgme_username_link' in txt and 'you can contact' in txt and 'right away' in txt:
        return TYPE_USER, None

    if 'members' in txt and 'you can view and join' in txt:
        _idx: int = txt.index('"og:title" content="')+20
        title: str = ""
        if _idx > 20:
            title = txt[_idx:txt.index('">', _idx)].strip()
        return TYPE_CHANNEL, title

    # not found
    return TYPE_UNKNOWN, None

if __name__ == "__main__":
    # results
    res: dict = {
        'users': [],
        'channels': []
    }

    checked: list = []

    # read and parse
    with open(os.path.join(*IN_FILE.split("/")), "r+") as input_file:
        # read lines
        for index, line in enumerate(input_file.readlines()):
            orig_line: str = line

            # trim
            line = line.strip()
            
            # ignore comments
            if len(line) <= 0 or line.startswith("#") or line.startswith("//"):
                continue

            # strip any unused prefixes
            if line.startswith("@") or line.startswith("@"):
                line = line[1:]
            elif line.startswith("https://t.me/"):
                line = line[13:]

            # comments in line?
            if '#' in line:
                line = line[:line.index('#')].strip()
            if '//' in line:
                line = line[:line.index('//')].strip()

            # skip already checked
            if line.lower() in checked:
                print()
                print(f" ! Duplicated line {index+1}:")
                print(orig_line)
                print()
                continue

            try:

                # make request to telegram
                line_type, line_title = get_type(line)
                checked.append(line.lower())

                print(line_type, line, line_title)


                if line_type == TYPE_CHANNEL:
                    res['channels'].append({
                        'url': line,
                        'title': line_title
                    })

                else:
                    print()
                    print(f" ! Remove line {index+1}:")
                    print(orig_line)
                    print()

                    if line_type == TYPE_ERROR or line_type == TYPE_UNKNOWN:
                        print(f"    * Skipped {line} ({line_type})")
                        continue
                    elif line_type == TYPE_USER:
                        res['users'].append(line)
                    
            except Exception as e:
                print("An error occured:")
                print(e)

    # write
    with open (os.path.join(*OUT_FILE.split("/")), "w+") as output_file:
        output_file.write(json.dumps(res, indent=2, sort_keys=True))

    print (res)