set -e
#set -x

LOG_FILES="*.log"
ls -la $LOG_FILES
VALIDATION_FILE=failures.txt
echo -n > "$VALIDATION_FILE" # Empty the file.
validate() {
	count=$(grep --perl-regexp --count --null $1 $LOG_FILES || true);
	if [ $count -gt 0 ]; then
		echo "Validation for '$2' failed: found /$1/ $count times." >> "$VALIDATION_FILE";
		# Note: not using --null here, because it garbles the output.
		# The count above is valid, the indication of failures might be not.
		grep --perl-regexp --only-matching --with-filename --line-number $1 $LOG_FILES >> "$VALIDATION_FILE";
		echo "" >> "$VALIDATION_FILE";
	else
		echo "Validation for '$2' passed: couldn't find /$1/.";
	fi
}

validate '(?m)^d[u]m+.*$' "This will not be found"
validate dummy "This will be found"
validate dummy1 "This will not be found"
validate dummy "This will be found again"
validate dummy1 "This will not be found"

if [ -s "$VALIDATION_FILE" ]; then
	echo "Some validations failed.";
	cat "$VALIDATION_FILE";
	exit 1;
else
	echo "All validations passed.";
	exit 0;
fi
