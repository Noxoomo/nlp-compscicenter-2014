gsed -n -e '/<content encoding="base64">/,/<\/content>/{s/<content encoding="base64">//;s/<\/content>/RU9GCg==/;s/ //;p' -e '}' $1 |
tr -d ' \n' | base64 --decode | iconv -f cp1251 -t utf-8 > $2
