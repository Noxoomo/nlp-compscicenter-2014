#!/bin/bash

platform=$(uname)

if [[ $1 == '--help' ]]; then
	echo 'Facts extractor. Usage:'
	echo '     ./facts2names.sh input.xml output.txt'
	echo ''
	exit
fi

if [[ $platform == 'Linux' ]]; then
	sed -n -e '/<content encoding="base64">/,/<\/content>/{s/<content encoding="base64">//;s/<\/content>/RU9GCg==/;s/ //;p' -e '}' $1 |
	tr -d ' \n' | base64 --decode | iconv -f cp1251 -t utf-8 > $2
elif [[ $platform == 'Darwin' ]]; then
	gsed -n -e '/<content encoding="base64">/,/<\/content>/{s/<content encoding="base64">//;s/<\/content>/RU9GCg==/;s/ //;p' -e '}' $1 |
	tr -d ' \n' | base64 --decode | iconv -f cp1251 -t utf-8 > $2
fi