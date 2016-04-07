TPC-H = /home/o/class/cs127b/PA2/tpc-h.sql
UPDATE = /home/o/class/cs127b/PA2/update.sh

create-db: update
	createdb $(PGDATABASE)

load-db: update
	psql -f $(TPC-H)

stop-db: update
	killall psql -s 9 -u $(USER); true
	sleep 5

delete-db: update stop-db
	killall psql -s 9 -u $(USER);true && dropdb $(PGDATABASE)

initialize-db: create-db load-db

reset-db: delete-db initialize-db

bin/%.class: src/%.java
	javac -cp lib/pa2.jar $< -d bin

plot/out.plot: bin/Part1.class
	java -cp bin:lib/jdbc.jar:lib/pa2.jar Part1 > plot/out.plot

gv/out.gv: bin/Part2.class
	java -cp bin:lib/jdbc.jar:lib/pa2.jar Part2 > gv/out.gv

tex/out.tex: bin/Part3.class
	cat txt/orders.txt | java -cp bin:lib/jdbc.jar:lib/pa2.jar Part3 > tex/out.tex

vis/Part1.png: plot/out.plot
	gnuplot plot/out.plot > vis/Part1.png

vis/Part2.png: gv/out.gv
	dot -Tpng gv/out.gv -o vis/Part2.png

vis/Part3.pdf: tex/out.tex
	cat tex/out.tex | pdflatex -output-directory=temp
	mv temp/*.pdf vis/Part3.pdf
	rm temp/*

build-part1: update bin/Part1.class

build-part2: update bin/Part2.class

build-part3: update bin/Part3.class

run-part1: update plot/out.plot

run-part2: update gv/out.gv

run-part3: update tex/out.tex

vis-part1: update vis/Part1.png

vis-part2: update vis/Part2.png

vis-part3: update vis/Part3.pdf

build: build-part1 build-part2 build-part3 

run: run-part1 run-part2 run-part3

vis: vis-part1 vis-part2 vis-part3

clean:
	rm -rf temp/*; true
	rm plot/*; true
	rm gv/*; true
	rm tex/*; true
	rm vis/*; true
	rm -rf bin/*; true

update:
	@$(UPDATE)

submit:
	mkdir temp/$(FIRSTNAME)_$(LASTNAME)_127b_pa2
	cp -r plot gv tex vis src sql pdf temp/$(FIRSTNAME)_$(LASTNAME)_127b_pa2
	tar -C temp -cvzf $(FIRSTNAME)_$(LASTNAME)_127b_pa2.tar.gz $(FIRSTNAME)_$(LASTNAME)_127b_pa2
	rm -r temp/$(FIRSTNAME)_$(LASTNAME)_127b_pa2
