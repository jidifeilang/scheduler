#!/bin/bash
LOCAL=`mktemp -d -t btrplace.XXX`
REMOTE=$1
VERSION=$2
git -C ${LOCAL} init
git -C ${LOCAL} remote add origin git@github.com:btrplace/${REMOTE} || exit 1
git -C ${LOCAL} pull origin gh-pages ||exit 1
git -C ${LOCAL} checkout gh-pages || exit 1
cd ${LOCAL}
rm -rf *
cd -
git -C ${LOCAL} commit -m "clean" -a || exit 1

#Generate and copy
echo "Generate the new javadoc"
mvn -q compile -DskipTests javadoc:aggregate
cp -r target/site/apidocs/* ${LOCAL}/

#Publish
echo "Publishing"
cd ${LOCAL}
git add *|| exit 1
cd -
git -C ${LOCAL} commit -m "apidoc for version ${VERSION}" -a || exit 1
git -C ${LOCAL} push origin gh-pages|| exit 1
rm -rf ${LOCAL}
