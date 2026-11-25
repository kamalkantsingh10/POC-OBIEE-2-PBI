#!/usr/bin/env bash
# The only build path (Architecture AD-15): javac --release 11, jar, and the project's own test runner.
#
#   ./build.sh                      compile, build build/obi2pbi.jar, run every test
#   ./build.sh test [filter]        the same, running only tests whose name contains the filter
#   ./build.sh compare <estate> [table ...] [--kb <folder>] [--source <estate folder>] [--where column=v1,v2]
#                                   [--where-not column=value] [--show-explained n]
#                                   compare kb/<estate> (or --kb) with reference/knowledge-base
#   ./build.sh compare-all [--out <report file>] [--no-extract]
#                                   extract the three sample estates into kb/, compare every table with the
#                                   reference, and write documents/implementation-artifacts/kb-comparison-report.md
#   ./build.sh skills               copy each folder of skills/ unchanged to .agents/skills/ and .claude/skills/
#                                   (the agent and its skills are edited only under skills/; a test compares the copies)
#
# Keep build.cmd in step with this file.
set -euo pipefail
cd "$(dirname "$0")"

MAIN_OUT=build/classes/main
TEST_OUT=build/classes/test
JAR=build/obi2pbi.jar

compile() {
    rm -rf build/classes "$JAR"
    mkdir -p "$MAIN_OUT" "$TEST_OUT"
    find src/main/java -name '*.java' | sort > build/main-sources.txt
    find src/test/java -name '*.java' | sort > build/test-sources.txt
    javac --release 11 -encoding UTF-8 -Xlint:all -Werror -d "$MAIN_OUT" @build/main-sources.txt
    jar --create --file "$JAR" --main-class obi2pbi.cli.Main -C "$MAIN_OUT" .
    javac --release 11 -encoding UTF-8 -Xlint:all -Werror -cp "$MAIN_OUT" -d "$TEST_OUT" @build/test-sources.txt
}

case "${1:-build}" in
    build)
        compile
        java -cp "$MAIN_OUT:$TEST_OUT" obi2pbi.TestRunner
        echo "built $JAR"
        ;;
    test)
        shift
        compile
        java -cp "$MAIN_OUT:$TEST_OUT" obi2pbi.TestRunner "$@"
        ;;
    compare)
        shift
        if [ ! -d "$TEST_OUT" ]; then compile; fi
        java -cp "$MAIN_OUT:$TEST_OUT" obi2pbi.KbCompare "$@"
        ;;
    compare-all)
        shift
        compile
        java -cp "$MAIN_OUT:$TEST_OUT" obi2pbi.KbCompareReport "$@"
        ;;
    skills)
        for folder in skills/*/; do
            name="$(basename "$folder")"
            case "$name" in bmad-*) echo "skills/$name: a skill here may not be named bmad-*" >&2; exit 2 ;; esac
            for client in .agents/skills .claude/skills; do
                rm -rf "${client:?}/$name"
                mkdir -p "$client"
                cp -R "skills/$name" "$client/$name"
            done
            echo "copied skills/$name"
        done
        ;;
    *)
        echo "usage: ./build.sh [build | test [filter] | compare <estate> [table ...] | compare-all | skills]" >&2
        exit 2
        ;;
esac
