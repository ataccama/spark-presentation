all: build

clean:
	rm -f *.pdf *.eps *.out *.log *.aux *.nav *.snm *.toc *.vrb *~

images := $(patsubst %.dia,%.eps,$(wildcard *.dia))

build: $(images)
	pdflatex spark.tex

%.eps: %.dia
	dia --export=$@ --filter=eps $<
