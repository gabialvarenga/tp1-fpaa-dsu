OUT=out

build-O0:
	if not exist $(OUT) mkdir $(OUT)
	javac -d $(OUT) src/dsu/*.java src/benchmark/*.java src/test/*.java

build-O2:
	if not exist $(OUT) mkdir $(OUT)
	javac -g:none -d $(OUT) src/dsu/*.java src/benchmark/*.java src/test/*.java

test:
	java -cp $(OUT) test.DSUTest

benchmark:
	java -cp $(OUT) benchmark.Benchmark

graficos:
	python scripts/generate_graphs.py

clean:
	if exist $(OUT) rmdir /s /q $(OUT)