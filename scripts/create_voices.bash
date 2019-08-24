#!/bin/bash
echo "Select Key Directory" | pico2wave -w /tmp/rontmp.wav; play /tmp/rontmp.wav

export IFS=",";
export reverb=20; 		# %
export hfdamping=96; 	# %
export roomscale=97; 	# %
export stereodepth=98; # %
export predelay=99; 	# ms
export wetgain=0; 		# 0dB

cat << EOF | while read file speech; do echo "File: ${file} - Speech: ${speech}"; echo "${speech}" | pico2wave -w "${file}.wav" -l en-US; sox "${file}.wav" "${file}.aiff" reverb "${reverb}" "${hfdamping}" "${roomscale}" "${stereodepth}" "${predelay}" "${wetgain}"; rm "${file}.wav"; play "${file}.aiff"; done
clone_key_device,Clone Key Device
confirm_password_with_enter,Confirm Password With Enter
create_key,Create Key
create_key_device,Create Key Device
decrypt_files,Dee crypt Files
decrypting_files,Dee crypting Files
encrypt_files,Encrypt Files
encrypting_files,Encrypting Files
encrypt_or_decrypt_files,Encrypt or Deecrypt Files
scanning_files,Scanning Files
select_files,Select Files
select_key,Select Key
select_key_directory,Select Key Directory
voice_disabled,Voice Disabled
voice_enabled,Voice Enabled
wrong key or password,Wrong Key or Password
EOF

