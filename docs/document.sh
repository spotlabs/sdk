javadoc \
	-d ~/spotlabs.github.io/docs/ \
	-sourcepath ../src/ \
	-subpackages com.spotlabs \
	-classpath $ANDROID_HOME/platforms/android-18/android.jar \
	-doclet com.google.doclava.Doclava \
	-docletpath doclava-1.0.6.jar \
	-XDignore.symbol.file \
	-templatedir template \
	#-federate Android http://developer.android.com/reference  \
	# -federationxml Android http://doclava.googlecode.com/svn/static/api/android-8.xml \
