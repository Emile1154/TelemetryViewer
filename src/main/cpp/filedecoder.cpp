#include "filedecoder.h"
#include <stdint.h>
#include <stdio.h>
#include <cstring>
#include <cstdlib>

/*
        PACKAGE STRUCTURE:
    +-------------------------+
    |__________HEAD___________|
    |  |  telemetry_data_t |  |
    |  |->telemetry_event_t|  |
    +-------------------------+
    |__________BODY___________|
    |    telemetry_data_t     |
    |       depth   [10]      |
    |       tension [10]      |
    |       magnet  [10]      |
    |           ...           |
    +-------------------------+
    |__________FOOT___________|
    |  |  telemetry_data_t |  |
    |  |->telemetry_event_t|  |
    +-------------------------+
*/

typedef struct{
    //calendar
    uint8_t  date;
    uint8_t  mounth;
    uint16_t year;
    //clock
    uint8_t  hour;
    uint8_t  minute;
    uint8_t  second;

    char     event[5];
} telemetry_event_t;

typedef struct{
    telemetry_event_t start_tag;
    telemetry_event_t end_tag;

    uint32_t          address_start;
    uint32_t          address_end;
} telemetry_data_read_t;

typedef struct{
    telemetry_event_t tag;

    float    depth  [10];
    uint16_t tension[10];
    uint8_t  magnet [10];
} telemetry_data_t;

telemetry_data_read_t *datalist;
size_t  size_data_read      = 0;
size_t  size_available_read = 0;

FILE * read_thread;
char * filepath;
//READING ALL HEADER AND TAILS IN BINARY FILE AND SAVE IT IN datalist dynamic array
JNIEXPORT jobjectArray JNICALL Java_com_example_telemetryviewer_service_BinaryReader_readData(JNIEnv *env, jobject obj, jstring path)
{
     filepath = const_cast<char *>(env->GetStringUTFChars(path, NULL));
     read_thread = fopen(filepath, "rb");

     if(read_thread == NULL){
         return NULL;
     }

     datalist =
                     (telemetry_data_read_t *) malloc(
                                             1*sizeof(telemetry_data_read_t)
                                                     );
    size_data_read = 0;
    telemetry_data_t data;
    while (fread(&data, sizeof(telemetry_data_t), 1, read_thread) > 0) {
        if(strcmp(data.tag.event, "DATA") == 0 ){  //read only tags
            continue;
        }
        if (strcmp(data.tag.event, "UP") == 0 || strcmp(data.tag.event, "DOWN") == 0) {
            datalist[size_data_read].start_tag = data.tag;
            datalist[size_data_read].address_start = ftell(read_thread);
        }
        else if(strcmp(data.tag.event, "STOP") == 0){
            //printf("end event detected: %s\n", data.event.event);
            datalist[size_data_read].end_tag = data.tag;
            datalist[size_data_read].address_end =
                                ftell(read_thread) - sizeof(telemetry_data_t);

            size_data_read++;
            //add ram in list
            datalist = (telemetry_data_read_t *) realloc(
                                        datalist,
                                        (size_data_read+1)*sizeof(telemetry_data_read_t)
                                                        );
        }
    }
    fclose(read_thread);

    jclass template_class = env->FindClass("com/example/telemetryviewer/models/TelemetryIndex");
    jobjectArray result = env->NewObjectArray(size_data_read, template_class, NULL);
    if(result == NULL){
        return NULL;
    }
    for(int i = 0; i < size_data_read; i++){
        jobject data = env->AllocObject(template_class);

        jfieldID date    = env->GetFieldID(template_class,   "date",   "S");
        jfieldID month   = env->GetFieldID(template_class,  "month",  "S");
        jfieldID year    = env->GetFieldID(template_class,   "year",   "S");
        jfieldID hour    = env->GetFieldID(template_class,   "hour",   "S");
        jfieldID minute  = env->GetFieldID(template_class, "minute", "S");
        jfieldID second  = env->GetFieldID(template_class, "second", "S");

        jfieldID end_date   = env->GetFieldID(template_class,   "end_date",  "S");
        jfieldID end_month  = env->GetFieldID(template_class, "end_month", "S");
        jfieldID end_year   = env->GetFieldID(template_class,  "end_year",  "S");
        jfieldID end_hour   = env->GetFieldID(template_class,  "end_hour",  "S");
        jfieldID end_minute = env->GetFieldID(template_class, "end_minute", "S");
        jfieldID end_second = env->GetFieldID(template_class, "end_second", "S");

        jfieldID event = env->GetFieldID(template_class,  "event",  "Ljava/lang/String;");
        jfieldID address_start = env->GetFieldID(template_class, "start_address", "J");
        jfieldID address_end = env->GetFieldID(template_class, "end_address",     "J");

        env->SetShortField(data, date,  datalist[i].start_tag.date);
        env->SetShortField(data, month, datalist[i].start_tag.mounth);
        env->SetShortField(data, year,  datalist[i].start_tag.year);
        env->SetShortField(data, hour,  datalist[i].start_tag.hour);
        env->SetShortField(data, minute, datalist[i].start_tag.minute);
        env->SetShortField(data, second, datalist[i].start_tag.second);

        env->SetShortField(data, end_date,  datalist[i].end_tag.date);
        env->SetShortField(data, end_month, datalist[i].end_tag.mounth);
        env->SetShortField(data, end_year,  datalist[i].end_tag.year);
        env->SetShortField(data, end_hour,  datalist[i].end_tag.hour);
        env->SetShortField(data, end_minute, datalist[i].end_tag.minute);
        env->SetShortField(data, end_second, datalist[i].end_tag.second);

        env->SetObjectField(data, event, env->NewStringUTF(datalist[i].start_tag.event));
        env->SetLongField(data, address_start, datalist[i].address_start);
        env->SetLongField(data, address_end, datalist[i].address_end);

        env->SetObjectArrayElement(result, i, data);

        env->DeleteLocalRef(data);
    }
    free(datalist);
    return result;
}


JNIEXPORT jobject JNICALL Java_com_example_telemetryviewer_service_BinaryReader_getData(JNIEnv *env, jobject obj, jint address_start, jint address_end)
{
    if(address_start > address_end){
        return NULL;
    }
    //xSemaphoreTake(xMutexUSB, portMAX_DELAY);
    FILE * read = fopen(filepath, "rb");

    if (! read) {
        //xSemaphoreGive(xMutexUSB);
        return NULL;
    }

    size_t data_size = address_end - address_start;
    size_t cntwrote = data_size/sizeof(telemetry_data_t);
    size_t size = cntwrote*10;

    jfloat depth[size];   // = new jfloat  [size];
    jshort tension[size]; // = new jshort  [size];
    jbyte  magnet[size];  // = new jbyte  [size];

    fseek(read, address_start, SEEK_SET);
    uint32_t cnt = 0;
    telemetry_data_t current;
    for(int i = 0; i < cntwrote; i++){
        if( fread( &current, sizeof(telemetry_data_t), 1, read) == 1){
            for(int j = 0; j < 10; j++){
                depth   [ cnt + j ] = current.depth   [ j ];
                tension [ cnt + j ] = current.tension [ j ];
                magnet  [ cnt + j ] = current.magnet  [ j ];

            }
            cnt += 10;
        }
    }
    fclose(read);

    jclass template_class = env->FindClass("com/example/telemetryviewer/models/TelemetryData");
    jmethodID constructor = env->GetMethodID(template_class, "<init>", "([F[S[B)V");

    if (!template_class) {
        return NULL;
    }

    jfloatArray depthArray    = env->NewFloatArray(size);
    jshortArray tensionArray  = env->NewShortArray(size);
    jbyteArray  magnetArray   = env->NewByteArray(size);

    env->SetFloatArrayRegion(depthArray,   0, size, depth);
    env->SetShortArrayRegion(tensionArray, 0, size, tension);
    env->SetByteArrayRegion(magnetArray,   0, size, magnet);

    jobject result = env->NewObject(template_class, constructor, depthArray, tensionArray, magnetArray);
    env->DeleteLocalRef(depthArray);
    env->DeleteLocalRef(tensionArray);
    env->DeleteLocalRef(magnetArray);
    return result;
}


