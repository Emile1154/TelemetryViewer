	export PWD             = X:\JavaProjets\telemetryViewer
	export PROJECT_PACKAGE = $(PWD)/src/main/java/com/example/telemetryviewer
	export HEADERS         = $(PWD)/src/main/cpp/headers
	export CPP_FOLDER      = $(PWD)/src/main/cpp
	export OUT_DIR         = $(PWD)
	export 64_bit          = C:/TDM-GCC-64/bin/gcc.exe
decoder:

	javac -h $(HEADERS) -d $(OUT_DIR) $(PROJECT_PACKAGE)/service/BinaryReader.java $(PROJECT_PACKAGE)/models/telemetryData.java $(PROJECT_PACKAGE)/models/telemetryIndex.java
	${64_bit} -m64 -c "-IC:/Program Files/Java/jdk-11.0.14/include" "-IC:/Program Files/Java/jdk-11.0.14/include/win32" -I$(HEADERS) $(CPP_FOLDER)/filedecoder.cpp -o $(OUT_DIR)/filedecoder.o
	${64_bit} -shared -o $(OUT_DIR)/filedecoder.dll $(OUT_DIR)/filedecoder.o
