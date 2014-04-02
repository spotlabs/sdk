#!/bin/bash
javadoc \
	-d out \
	-sourcepath ../src/ \
	-subpackages com.spotlabs \
	-classpath $ANDROID_HOME/platforms/android-18/android.jar \
	-doclet com.google.doclava.Doclava \
	-docletpath doclava-1.0.6.jar \
	-XDignore.symbol.file \
	-federate Android http://developer.android.com/reference  \
	-federationxml Android http://doclava.googlecode.com/svn/static/api/android-8.xml \
	-htmldir html/ \
	-hdf project.name "spot developers" \
	-templatedir template \
	-overview overview.html
