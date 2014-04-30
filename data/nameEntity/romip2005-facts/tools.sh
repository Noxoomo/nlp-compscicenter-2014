#!/bin/bash
# User: Kribesk
# Date: 30.04.14

# Useful shortcuts for facts converter

mode=$1

if [[ $mode == "news" ]]; then
  bzip2 -dk news-080404.xml.bz2
  mv news-080404.xml data1.xml
  cp news-vybory.xml data2.xml
  bzip2 -dk news-shevard.xml.bz2
  mv news-shevard.xml data3.xml
  gzip -dk and_good_table.xml.gz
  iconv -f cp1251 -t utf-8 and_good_table.xml > tasks.xml
  rm and_good_table.xml
  exit
fi

if [[ $mode == "clear" ]]; then
  rm -f data.xml data*.xml tasks.xml output.txt
  exit
fi

echo "Service script for facts converter."
