from hashlib import sha256
from os import listdir
from json import dumps

url = "https://jamesvps.tk/mods/jarbin/"

modlist = {}

for name in listdir("mods"):
    with open(f"mods/{name}", "rb") as f:
        sha = sha256()
        sha.update(f.read())
    modlist.update({sha.hexdigest(): url + name})

with open("list.json", "w") as f:
    f.write(dumps(modlist))
