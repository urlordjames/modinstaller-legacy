import hashlib
import os
import json

url = "https://jamesvps.tk/mods/jarbin/"

list = {}

for name in os.listdir("mods"):
    f = open("mods/" + name, "rb")
    sha = hashlib.sha256()
    sha.update(f.read())
    f.close()
    list.update({sha.hexdigest(): url + name})

f = open("list.json", "w")
f.write(json.dumps(list))
f.close()