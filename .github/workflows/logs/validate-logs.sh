set -e
#set -x

LOG_FILES="*.log"
ls -la $LOG_FILES
VALIDATION_FILE=failures.txt
echo -n > "$VALIDATION_FILE" # Empty the file.
validate() {
	set -e
	count=$(grep --perl-regexp --count --null "$1" $LOG_FILES || true);
	if [ $count -gt 0 ]; then
		echo "Validation for '$2' failed: found /$1/ $count times." >> "$VALIDATION_FILE";
		# Note: not using --null here, because it garbles the output.
		# The count above is valid, the indication of failures might be not.
		grep --perl-regexp --only-matching --with-filename --line-number "$1" $LOG_FILES >> "$VALIDATION_FILE";
		echo "" >> "$VALIDATION_FILE";
	else
		echo "Validation for '$2' passed: couldn't find /$1/.";
	fi
}

# > > Configure project :plugins
# > This version of Gradle expects version '2.1.7' of the `kotlin-dsl` plugin but version '2.3.3' has been applied to project ':plugins'. Let Gradle control the version of `kotlin-dsl` by removing any explicit `kotlin-dsl` version constraints from your build logic.
validate 'Let Gradle control the version of `kotlin-dsl`' "Gradle 7.x Kotlin DSL Plugin self-validation."
# > WARNING: Unsupported Kotlin plugin version.
# > The `embedded-kotlin` and `kotlin-dsl` plugins rely on features of Kotlin `1.5.31` that might work differently than in the requested version `1.6.21`.
validate 'Unsupported Kotlin plugin version' "Gradle 7.x Kotlin Embedded Plugin self-validation."

if [ -s "$VALIDATION_FILE" ]; then
	echo "Some validations failed.";
	cat "$VALIDATION_FILE";
	exit 1;
else
	echo "All validations passed.";
	exit 0;
fi
