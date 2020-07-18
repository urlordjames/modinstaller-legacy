import hashlib
import os
import json

list = {}

for name in os.listdir("mods"):
    with open("mods/" + name, "rb") as f:
        sha = hashlib.sha256()
        sha.update(f.read())
    list.update({sha.hexdigest(): f"https://jamesvps.tk/mods/jarbin/{name}"})

f = open("list.json", "w")
f.write(json.dumps(list))
f.close()
