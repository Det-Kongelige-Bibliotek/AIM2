# bash scripts usable when not using IntelliJ
# source this from your .bashrc or .zshrc

WEBAPPS="/var/lib/tomcat9/webapps"
VERSION="2.0.4-SNAPSHOT" # match this with pom.xml
DIR=`dirname "$0"` # folder of this script

# copy frontend without building
function aimfront() {
  (
    cd "${DIR}/src/main/resources/static"
    gulp sass
  )
  sudo cp -R "${DIR}/src/main/resources/static" "${WEBAPPS}/aim/WEB-INF/classes/static/"
  sudo touch "${WEBAPPS}/aim/WEB-INF/web.xml"
}

# build backend
function aimback() {
  # clean
  sudo rm "${WEBAPPS}/aim.war"
  sleep 5
  (
    cd "${DIR}"
    mvn clean install -DskipTests=true
    sudo cp "target/aim-${VERSION}.war" "${WEBAPPS}/aim.war"
  )
}
