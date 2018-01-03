#!/bin/bash
uuid=blocktyper-tmp-$(uuidgen)
git clone https://github.com/spaarkimus/ExampleBlockTyperPlugin.git $uuid
cd $uuid
./new.sh $1 $2
cd ..
mv $uuid/target/projects/* .
rm -rf $uuid