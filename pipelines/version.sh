#!/bin/bash

while getopts "v:" opt; do
  case $opt in
    v) version=$OPTARG ;;
    *) echo "Invalid argument" ;;
  esac
done

# Get the latest tag matching semantic versioning format
PREVIOUS_VERSION_NUMBER=$(git describe --tags --match '[[:digit:]]*.[[:digit:]]*.[[:digit:]]*' --abbrev=0)

# If no tag is found, set the version to 0.0.0
if [ -z "$PREVIOUS_VERSION_NUMBER" ]; then
  PREVIOUS_VERSION_NUMBER="0.0.0"
fi

# Split the version string into major, minor, and patch using IFS
IFS='.' read -r major minor patch <<< "$PREVIOUS_VERSION_NUMBER"

echo "Previous Version: $major.$minor.$patch"

# Case statement to handle different version increments
case $version in
    major)
        ((major++))
        minor=0
        patch=0
        ;;
    minor)
        ((minor++))
        patch=0
        ;;
    patch)
        ((patch++))
        ;;
    *)
        echo "Invalid option selected"
        exit 1
        ;;
esac

RELEASE_VERSION_NUMBER="$major.$minor.$patch"
#export RELEASE_VERSION_NUMBER=$(RELEASE_VERSION_NUMBER)
echo "New Release Version: $RELEASE_VERSION_NUMBER"
#echo "##vso[task.setvariable variable=RELEASE_VERSION_NUMBER]$RELEASE_VERSION_NUMBER"
#echo $NEXUS_REPO && echo ${SERVICE_NAME}
#export NEXUS_REPO=${NEXUS_REPO}



#git add .
#git commit -m \"Bumping development version to $RELEASE_VERSION_NUMBER\"
#git tag $RELEASE_VERSION_NUMBER
#git push origin HEAD:refs/heads/master --tags
