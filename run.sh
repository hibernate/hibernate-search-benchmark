#!/bin/bash -e

function log() {
	echo 1>&2 "${@}"
}

function usage() {
	log -e "Usage:\t$0 [-b <binaries directory>] [-t <target directory>] -- <JMH args>"
	log -e "Output will appear in the working directory."
}

TMP=$(mktemp -d)
BIN=$TMP
TARGET=$TMP

while getopts 'b:t:' opt
do
        case "$opt" in
                b)
			BIN="$(readlink -f $OPTARG)"
			;;
                t)
			TARGET="$(readlink -f $OPTARG)"
			;;
		\?)
			log "Invalid arguments"
			usage
			exit 1
			;;
	esac
done

shift $(( OPTIND - 1 ))

for DIR in "$BIN" "$TARGET"
do
	if ! [ -e "$DIR" ]
	then
		mkdir "$DIR"
	fi
done

PATTERN="$1"

if [ -z "$(ls -A $BIN)" ]
then
	for VERSION in "5" "6"
	do
		log "Building binaries for Search $VERSION..."
		mvn -q clean package -Dhsearch.version=$VERSION -DskipTests
		for BACKEND in "elasticsearch" "lucene"
		do
			cp jmh-$BACKEND/target/benchmarks.jar $BIN/search$VERSION-$BACKEND.jar
		done
	done
else
	log "$BIN is not empty; assuming binaries are already there..."
fi

log "Stopping all containers..."
CONTAINERS=($(docker ps -q))
if [ ${#CONTAINERS[@]} != 0 ]
then
	docker stop ${CONTAINERS[@]}
fi

jenkins/docker-prune.sh >/dev/null

PROJDIR="$PWD"
pushd "$TMP"
for VERSION in "5" "6"
do
	for BACKEND in "elasticsearch" "lucene"
	do
		log "=========================================="
		log "Version = $VERSION, backend = $BACKEND"
		log "------------------------------------------"
		log "Starting containers..."
		mvn -q docker:start -Dhsearch.version=$VERSION -f "$PROJDIR/jmh-$BACKEND" -Ddocker.showLogs=false
		log "------------------------------------------"
		log "Running benchmarks..."
		java -jar $BIN/search$VERSION-$BACKEND.jar -rff $TARGET/run-search$VERSION-$BACKEND.csv "${@}"
		log "Done."
		log "------------------------------------------"
		log "Stopping containers..."
		mvn -q docker:stop -Dhsearch.version=$VERSION -f "$PROJDIR/jmh-$BACKEND" -Ddocker.showLogs=false
		log "=========================================="
	done
done
popd

log "Done."
log "See files named run-search*-*.csv in '$TARGET'."

