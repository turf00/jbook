# Example script that deals with input params by name

```bash
#!/usr/bin/env bash
set -euo pipefail

# pipefail
# e=if a command fails the shell will exit (i.e. return > 0)
# u=write an error when trying to expand a variable that is not set
# o=set pipefail When used in combination with set -e, pipefail will make a script exit if any command in a pipeline errors.

## Fetching params by name rather than position

INPUT=default-input.txt
OUTPUT=default-output.txt

while [[ ${#} -gt 0 ]]; do
    case "${1}" in
        --input)               INPUT="${2}"; shift;;
        --output)              OUTPUT="${2}"; shift;;
        --)                    break;;
        -*)                    echo "Unrecognized option ${1}"; exit 1;
    esac
    shift
done

# Check for var being null
if [[ ${INPUT} == "" ]]; then
  echo "--input needs to be set"
  exit 1;
fi
```
