#!/bin/bash

font_file=$1
font_name=$2
filename=$3
packagename_suffix=$4

if [ -d ./FlipFont ]
then
  rm -rf ./FlipFont
fi

unzip -q FlipFont.zip

cp "$font_file" "./FlipFont/app/src/main/assets/fonts/FONT_FILENAME.ttf"
sed -i "s|FONT_NAME|$font_name|g" "./FlipFont/app/src/main/assets/xml/FONT_FILENAME.xml" "./FlipFont/app/src/main/res/values/strings.xml"
sed -i "s|FONT_FILENAME|$filename|g" "./FlipFont/app/src/main/assets/xml/FONT_FILENAME.xml"
mv "./FlipFont/app/src/main/assets/fonts/FONT_FILENAME.ttf" "./FlipFont/app/src/main/assets/fonts/${filename}.ttf"
mv "./FlipFont/app/src/main/assets/xml/FONT_FILENAME.xml" "./FlipFont/app/src/main/assets/xml/${filename}.xml"

if [ -n "$packagename_suffix" ]
then
  sed -i "s|tinkerbell|$packagename_suffix|" "./FlipFont/app/build.gradle"
fi

cd FlipFont
chmod 755 ./gradlew
./gradlew clean assembleRelease
