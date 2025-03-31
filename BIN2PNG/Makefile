SRCFILES := $(shell find src -name \*.java)
PACKAGES := $(shell find src -name \*.java | sed -E s/^src\\/\|\\/[^\\/]+$$//g | grep -v \\.java$$ | sort | uniq | tr / .)

all: clean bin doc bin2png.jar

eclipseall: bin2png.jar

clean:
	rm -rf bin
	rm -rf doc
	rm -rf *.jar

eclipseclean:
	rm -rf *.jar

bin: src
	mkdir -p bin
ifeq ($(origin JAVACFLAGS), undefined)
	$(eval JAVACFLAGS = $(shell dep/minJavaVerOpts))
endif
	javac $(JAVACFLAGS) -classpath dep/ual.jar -sourcepath src $(SRCFILES) -d bin

doc: src
	mkdir -p doc
	javadoc $(JAVADOCFLAGS) -classpath dep/ual.jar -sourcepath src $(PACKAGES) -d doc

bin2png.jar: bin
	export COPYFILE_DISABLE=true
	find bin -name .DS_Store -delete
	jar cmf dep/MANIFEST.MF bin2png.jar -C bin com/kreative/bin2png
	chmod +x bin2png.jar

.PHONY: all eclipseall clean eclipseclean
