#!/usr/bin/env bash

openssl aes-256-cbc -K $encrypted_f6a37a5809a3_key -iv $encrypted_f6a37a5809a3_iv -in .travis/codesigning.asc.enc -out .travis/codesigning.asc -d
gpg --fast-import .travis/codesigning.asc

if [ ! -z "$TRAVIS_TAG" ]
then
    echo "on a tag -> set pom.xml <version> to $TRAVIS_TAG"
    mvn --settings .travis/settings.xml org.codehaus.mojo:versions-maven-plugin:2.1:set -DnewVersion=$TRAVIS_TAG 1>/dev/null 2>/dev/null
    #mvn versions:set -DnewVersion=$TRAVIS_TAG
else
    echo "without tag -> keep snapshot version in pom.xml"
fi

#mvn clean deploy --settings .travis/settings.xml -DskipTests=true -B -U

mvn clean deploy -P release --settings .travis/settings.xml