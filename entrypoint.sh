#!/bin/bash

# Set the danger github api token as env variable
export DANGER_GITHUB_API_TOKEN="$1"

# download detekt jar
curl -sSLO https://github.com/detekt/detekt/releases/download/v1.8.0/detekt && chmod a+x detekt

# Run detekt with the detekt-hint plugin, requiring a config file to be set up in the repository. Redirect output to null to not bloat the logs with warning from the type-resolution.
java -jar detekt --input . -cp . --report xml:detekt-hint-report.xml --includes '**/*.kt' 

ls

danger ci --dangerfile /Dangerfile.ts --verbose
