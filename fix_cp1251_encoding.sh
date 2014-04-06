#/bin/sh
find . -name "*.txt" -type f -exec sh -c 'iconv -f cp1251 -t utf-8 "$1" > "$1utf8"' x {} \;
