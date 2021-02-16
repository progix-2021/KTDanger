#!/bin/bash

# Set the danger github api token as env variable
export DANGER_GITHUB_API_TOKEN="$1"
source ~/.bashrc

# download detekt jar
curl -sSLO https://github.com/detekt/detekt/releases/download/v1.8.0/detekt && chmod a+x detekt

# download detekt-hint jar
curl -L "https://oss.sonatype.org/service/local/artifact/maven/redirect?r=releases&g=io.github.mkohm&a=detekt-hint&v=0.1.5&e=jar" -o detekt-hint-0.1.5.jar
echo "Should show detekt-hint jar:"
ls

echo "Running detekt with the detekt-hint plugin ..."

# Run detekt with the detekt-hint plugin, requiring a config file to be set up in the repository. Redirect output to null to not bloat the logs with warning from the type-resolution.
java -jar detekt --plugins detekt-hint-0.1.5.jar --config config/detekt-hint-config.yml --input . -cp . --report xml:detekt-hint-report.xml --includes '**/*.kt' &> /dev/null

# Install danger-kotlin
bash <(curl -s https://raw.githubusercontent.com/danger/kotlin/master/scripts/install.sh)
source ~/.bash_profile

npx --package danger danger-kotlin ci --dangerfile /Dangerfile.df.kts --verbose
