# bash scripts usable when not using IntelliJ
# source this from your .bashrc or .zshrc

WEBAPPS="/var/lib/tomcat9/webapps"
VERSION="2.0.10" # match this with pom.xml
DIR=`dirname "$0"` # folder of this script

# copy frontend without building, even works for jsp files
function aimfront() {
  (
    cd "${DIR}/src/main/resources/static"
    gulp sass
  )
  sudo cp -R "${DIR}/src/main/webapp/WEB-INF" "${WEBAPPS}/aim/"
  sudo cp -R "${DIR}/src/main/resources/static" "${WEBAPPS}/aim/WEB-INF/classes/"
  sudo touch "${WEBAPPS}/aim/WEB-INF/web.xml"
}

# build backend
function aimback() {
  # clean
  sudo rm -rf "${WEBAPPS}/aim/"
  sudo rm "${WEBAPPS}/aim.war"
  (
    cd "${DIR}"
    mvn clean install -DskipTests=true
    sudo cp "target/aim-${VERSION}.war" "${WEBAPPS}/aim.war"
    sudo systemctl restart tomcat9
  )
}
