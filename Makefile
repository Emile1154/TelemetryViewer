# -------- CONFIGURATION --------
# JAVA_HOME        ?= /usr/lib/jvm/default-jdk
JAVA_HOME := $(shell echo $$JAVA_HOME)

ifeq ($(JAVA_HOME),)
$(error JAVA_HOME is not set. Please set it to your JDK path, e.g., export JAVA_HOME=/usr/lib/jvm/java-11-openjdk)
endif

PROJECT_ROOT     := $(CURDIR)
JAVA_SRC         := $(PROJECT_ROOT)/src/main/java/com/example/telemetryviewer
CPP_SRC          := $(PROJECT_ROOT)/src/main/cpp
HEADERS          := $(CPP_SRC)/headers
OUT_DIR          := $(PROJECT_ROOT)/build

JNI_INCLUDE      := -I"$(JAVA_HOME)/include"
JNI_PLATFORM     := -I"$(JAVA_HOME)/include/$(shell uname | tr A-Z a-z)"

# -------- TARGETS --------
all: detect-os build-jni

detect-os:
	@echo "Detected OS: $(shell uname)"

build-jni:
ifeq ($(OS),Windows_NT)
	@echo "Building for Windows..."
	javac -h $(HEADERS) -d $(OUT_DIR) $(JAVA_SRC)/service/BinaryReader.java $(JAVA_SRC)/models/*.java
	g++ -m64 -I"$(JAVA_HOME)/include" -I"$(JAVA_HOME)/include/win32" -I$(HEADERS) -shared -o $(OUT_DIR)/filedecoder.dll $(CPP_SRC)/filedecoder.cpp
else
	@echo "Building for Linux..."
	mkdir -p $(OUT_DIR)
	javac -h $(HEADERS) -d $(OUT_DIR) $(JAVA_SRC)/service/BinaryReader.java $(JAVA_SRC)/models/*.java
	g++ -fPIC $(JNI_INCLUDE) $(JNI_PLATFORM) -I$(HEADERS) -c $(CPP_SRC)/filedecoder.cpp -o $(OUT_DIR)/filedecoder.o
	g++ -shared -o $(OUT_DIR)/filedecoder.so $(OUT_DIR)/filedecoder.o
endif

clean:
	rm -rf $(OUT_DIR)/*.o $(OUT_DIR)/*.so $(OUT_DIR)/*.dll
