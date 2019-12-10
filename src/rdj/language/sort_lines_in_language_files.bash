#!/bin/bash
# This script is only here for the developer's convenience (that's me), but you may use it (on UNIX)
# This script re-sorts all the key-lines in the translation*.properties files in alphabetical order

for file in *.properties; do mv "${file}" "${file}_"; sort "${file}_" > "${file}"; rm "${file}_"; done
