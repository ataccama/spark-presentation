all: build

clean:
	mv spark-logo.eps spark-logo.eps.keep
	rm -f *.pdf *.eps *.out *.log *.aux *.nav *.snm *.toc *.vrb *~
	mv spark-logo.eps.keep spark-logo.eps

images := $(patsubst %.dia,%.eps,$(wildcard *.dia))

build: $(images)
	pdflatex spark.tex
	pdflatex spark.tex
	pdflatex spark.tex

%.eps: %.dia
	dia --export=$@ --filter=eps $<
