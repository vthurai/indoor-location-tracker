@if "%DEBUG%" == "" @echo off
@rem ##########################################################################
@rem
@rem  myDayGeofencingFlow startup script for Windows
@rem
@rem ##########################################################################

@rem Set local scope for the variables with windows NT shell
if "%OS%"=="Windows_NT" setlocal

set DIRNAME=%~dp0
if "%DIRNAME%" == "" set DIRNAME=.
set APP_BASE_NAME=%~n0
set APP_HOME=%DIRNAME%..

@rem Add default JVM options here. You can also use JAVA_OPTS and MY_DAY_GEOFENCING_FLOW_OPTS to pass JVM options to this script.
set DEFAULT_JVM_OPTS=

@rem Find java.exe
if defined JAVA_HOME goto findJavaFromJavaHome

set JAVA_EXE=java.exe
%JAVA_EXE% -version >NUL 2>&1
if "%ERRORLEVEL%" == "0" goto init

echo.
echo ERROR: JAVA_HOME is not set and no 'java' command could be found in your PATH.
echo.
echo Please set the JAVA_HOME variable in your environment to match the
echo location of your Java installation.

goto fail

:findJavaFromJavaHome
set JAVA_HOME=%JAVA_HOME:"=%
set JAVA_EXE=%JAVA_HOME%/bin/java.exe

if exist "%JAVA_EXE%" goto init

echo.
echo ERROR: JAVA_HOME is set to an invalid directory: %JAVA_HOME%
echo.
echo Please set the JAVA_HOME variable in your environment to match the
echo location of your Java installation.

goto fail

:init
@rem Get command-line arguments, handling Windows variants

if not "%OS%" == "Windows_NT" goto win9xME_args

:win9xME_args
@rem Slurp the command line arguments.
set CMD_LINE_ARGS=
set _SKIP=2

:win9xME_args_slurp
if "x%~1" == "x" goto execute

set CMD_LINE_ARGS=%*

:execute
@rem Setup the command line

set CLASSPATH=%APP_HOME%\lib\myDayGeofencingFlow-0.0.1.jar;%APP_HOME%\lib\beam-runners-google-cloud-dataflow-java-2.16.0.jar;%APP_HOME%\lib\beam-runners-direct-java-2.16.0.jar;%APP_HOME%\lib\beam-sdks-java-io-google-cloud-platform-2.16.0.jar;%APP_HOME%\lib\beam-sdks-java-extensions-google-cloud-platform-core-2.16.0.jar;%APP_HOME%\lib\beam-runners-core-construction-java-2.16.0.jar;%APP_HOME%\lib\beam-sdks-java-extensions-protobuf-2.16.0.jar;%APP_HOME%\lib\beam-sdks-java-core-2.16.0.jar;%APP_HOME%\lib\google-cloud-datastore-1.59.0.jar;%APP_HOME%\lib\google-cloud-bigquery-1.62.0.jar;%APP_HOME%\lib\commons-io-2.6.jar;%APP_HOME%\lib\jackson-databind-2.9.10.jar;%APP_HOME%\lib\slf4j-jdk14-1.7.25.jar;%APP_HOME%\lib\beam-model-pipeline-2.16.0.jar;%APP_HOME%\lib\beam-model-job-management-2.16.0.jar;%APP_HOME%\lib\beam-vendor-bytebuddy-1_9_3-0.1.jar;%APP_HOME%\lib\beam-vendor-guava-26_0-jre-0.1.jar;%APP_HOME%\lib\gcsio-1.9.16.jar;%APP_HOME%\lib\util-1.9.16.jar;%APP_HOME%\lib\google-api-client-jackson2-1.27.0.jar;%APP_HOME%\lib\google-api-services-clouddebugger-v2-rev20180801-1.27.0.jar;%APP_HOME%\lib\google-api-services-dataflow-v1b3-rev20190607-1.27.0.jar;%APP_HOME%\lib\datastore-v1-proto-client-1.6.0.jar;%APP_HOME%\lib\google-api-services-bigquery-v2-rev20181104-1.27.0.jar;%APP_HOME%\lib\google-api-services-cloudresourcemanager-v1-rev20181015-1.27.0.jar;%APP_HOME%\lib\google-api-services-pubsub-v1-rev20181105-1.27.0.jar;%APP_HOME%\lib\google-api-client-java6-1.27.0.jar;%APP_HOME%\lib\bigtable-client-core-1.8.0.jar;%APP_HOME%\lib\google-cloud-core-http-1.62.0.jar;%APP_HOME%\lib\google-api-services-storage-v1-rev20181109-1.27.0.jar;%APP_HOME%\lib\google-api-client-1.27.0.jar;%APP_HOME%\lib\gax-httpjson-0.55.0.jar;%APP_HOME%\lib\google-cloud-bigquerystorage-0.79.0-alpha.jar;%APP_HOME%\lib\google-cloud-spanner-1.6.0.jar;%APP_HOME%\lib\google-cloud-bigtable-0.73.0-alpha.jar;%APP_HOME%\lib\google-cloud-bigtable-admin-0.73.0-alpha.jar;%APP_HOME%\lib\google-cloud-core-grpc-1.61.0.jar;%APP_HOME%\lib\gax-grpc-1.38.0.jar;%APP_HOME%\lib\grpc-alts-1.17.1.jar;%APP_HOME%\lib\google-cloud-core-1.62.0.jar;%APP_HOME%\lib\gax-1.38.0.jar;%APP_HOME%\lib\google-auth-library-oauth2-http-0.12.0.jar;%APP_HOME%\lib\google-http-client-jackson2-1.27.0.jar;%APP_HOME%\lib\jackson-core-2.9.10.jar;%APP_HOME%\lib\jackson-annotations-2.9.10.jar;%APP_HOME%\lib\avro-1.8.2.jar;%APP_HOME%\lib\metrics-core-3.1.2.jar;%APP_HOME%\lib\slf4j-api-1.7.25.jar;%APP_HOME%\lib\snappy-java-1.1.4.jar;%APP_HOME%\lib\joda-time-2.10.3.jar;%APP_HOME%\lib\xz-1.8.jar;%APP_HOME%\lib\grpc-all-1.17.1.jar;%APP_HOME%\lib\grpc-auth-1.17.1.jar;%APP_HOME%\lib\google-auth-library-credentials-0.12.0.jar;%APP_HOME%\lib\google-http-client-protobuf-1.20.0.jar;%APP_HOME%\lib\google-http-client-jackson-1.20.0.jar;%APP_HOME%\lib\google-http-client-appengine-1.27.0.jar;%APP_HOME%\lib\google-oauth-client-java6-1.27.0.jar;%APP_HOME%\lib\google-oauth-client-1.27.0.jar;%APP_HOME%\lib\google-http-client-1.27.0.jar;%APP_HOME%\lib\beam-vendor-grpc-1_21_0-0.1.jar;%APP_HOME%\lib\args4j-2.33.jar;%APP_HOME%\lib\proto-google-cloud-datastore-v1-0.44.0.jar;%APP_HOME%\lib\grpc-netty-1.17.1.jar;%APP_HOME%\lib\grpc-okhttp-1.17.1.jar;%APP_HOME%\lib\grpc-protobuf-nano-1.17.1.jar;%APP_HOME%\lib\grpc-testing-1.17.1.jar;%APP_HOME%\lib\grpc-grpclb-1.17.1.jar;%APP_HOME%\lib\grpc-google-cloud-pubsub-v1-1.43.0.jar;%APP_HOME%\lib\grpc-google-cloud-bigquerystorage-v1beta1-0.44.0.jar;%APP_HOME%\lib\grpc-google-common-protos-1.12.0.jar;%APP_HOME%\lib\grpc-google-cloud-bigtable-v2-0.38.0.jar;%APP_HOME%\lib\grpc-google-cloud-bigtable-admin-v2-0.38.0.jar;%APP_HOME%\lib\grpc-google-cloud-spanner-v1-1.6.0.jar;%APP_HOME%\lib\grpc-google-cloud-spanner-admin-database-v1-1.6.0.jar;%APP_HOME%\lib\grpc-google-cloud-spanner-admin-instance-v1-1.6.0.jar;%APP_HOME%\lib\grpc-protobuf-1.17.1.jar;%APP_HOME%\lib\grpc-protobuf-lite-1.17.1.jar;%APP_HOME%\lib\grpc-stub-1.17.1.jar;%APP_HOME%\lib\grpc-netty-shaded-1.17.1.jar;%APP_HOME%\lib\opencensus-contrib-grpc-util-0.17.0.jar;%APP_HOME%\lib\grpc-core-1.17.1.jar;%APP_HOME%\lib\proto-google-cloud-bigquerystorage-v1beta1-0.44.0.jar;%APP_HOME%\lib\proto-google-cloud-pubsub-v1-1.43.0.jar;%APP_HOME%\lib\proto-google-cloud-spanner-admin-database-v1-1.6.0.jar;%APP_HOME%\lib\proto-google-cloud-bigtable-admin-v2-0.38.0.jar;%APP_HOME%\lib\proto-google-cloud-spanner-v1-1.6.0.jar;%APP_HOME%\lib\proto-google-cloud-spanner-admin-instance-v1-1.6.0.jar;%APP_HOME%\lib\proto-google-cloud-bigtable-v2-0.44.0.jar;%APP_HOME%\lib\javax.annotation-api-1.2.jar;%APP_HOME%\lib\opencensus-contrib-http-util-0.17.0.jar;%APP_HOME%\lib\proto-google-iam-v1-0.12.0.jar;%APP_HOME%\lib\api-common-1.7.0.jar;%APP_HOME%\lib\protobuf-java-util-3.6.1.jar;%APP_HOME%\lib\guava-27.0.1-jre.jar;%APP_HOME%\lib\error_prone_annotations-2.3.2.jar;%APP_HOME%\lib\jackson-mapper-asl-1.9.13.jar;%APP_HOME%\lib\jackson-core-asl-1.9.13.jar;%APP_HOME%\lib\paranamer-2.7.jar;%APP_HOME%\lib\commons-compress-1.8.1.jar;%APP_HOME%\lib\netty-codec-http2-4.1.30.Final.jar;%APP_HOME%\lib\netty-handler-4.1.30.Final.jar;%APP_HOME%\lib\netty-tcnative-boringssl-static-2.0.17.Final.jar;%APP_HOME%\lib\proto-google-common-protos-1.12.0.jar;%APP_HOME%\lib\protobuf-java-3.6.1.jar;%APP_HOME%\lib\auto-value-annotations-1.6.3.jar;%APP_HOME%\lib\google-extensions-0.3.1.jar;%APP_HOME%\lib\flogger-system-backend-0.3.1.jar;%APP_HOME%\lib\flogger-0.3.1.jar;%APP_HOME%\lib\jsr305-3.0.2.jar;%APP_HOME%\lib\httpclient-4.5.5.jar;%APP_HOME%\lib\j2objc-annotations-1.1.jar;%APP_HOME%\lib\threetenbp-1.3.3.jar;%APP_HOME%\lib\commons-logging-1.2.jar;%APP_HOME%\lib\opencensus-contrib-grpc-metrics-0.17.0.jar;%APP_HOME%\lib\opencensus-api-0.17.0.jar;%APP_HOME%\lib\grpc-context-1.17.1.jar;%APP_HOME%\lib\netty-handler-proxy-4.1.30.Final.jar;%APP_HOME%\lib\netty-codec-http-4.1.30.Final.jar;%APP_HOME%\lib\netty-codec-socks-4.1.30.Final.jar;%APP_HOME%\lib\netty-codec-4.1.30.Final.jar;%APP_HOME%\lib\netty-transport-4.1.30.Final.jar;%APP_HOME%\lib\netty-buffer-4.1.30.Final.jar;%APP_HOME%\lib\okhttp-2.5.0.jar;%APP_HOME%\lib\okio-1.13.0.jar;%APP_HOME%\lib\protobuf-javanano-3.0.0-alpha-5.jar;%APP_HOME%\lib\junit-4.12.jar;%APP_HOME%\lib\mockito-core-1.9.5.jar;%APP_HOME%\lib\netty-resolver-4.1.30.Final.jar;%APP_HOME%\lib\netty-common-4.1.30.Final.jar;%APP_HOME%\lib\hamcrest-core-1.3.jar;%APP_HOME%\lib\objenesis-1.0.jar;%APP_HOME%\lib\gson-2.7.jar;%APP_HOME%\lib\animal-sniffer-annotations-1.17.jar;%APP_HOME%\lib\failureaccess-1.0.1.jar;%APP_HOME%\lib\listenablefuture-9999.0-empty-to-avoid-conflict-with-guava.jar;%APP_HOME%\lib\checker-qual-2.5.2.jar;%APP_HOME%\lib\httpcore-4.4.9.jar;%APP_HOME%\lib\commons-codec-1.10.jar;%APP_HOME%\lib\commons-lang3-3.5.jar

@rem Execute myDayGeofencingFlow
"%JAVA_EXE%" %DEFAULT_JVM_OPTS% %JAVA_OPTS% %MY_DAY_GEOFENCING_FLOW_OPTS%  -classpath "%CLASSPATH%" com.main.Application %CMD_LINE_ARGS%

:end
@rem End local scope for the variables with windows NT shell
if "%ERRORLEVEL%"=="0" goto mainEnd

:fail
rem Set variable MY_DAY_GEOFENCING_FLOW_EXIT_CONSOLE if you need the _script_ return code instead of
rem the _cmd.exe /c_ return code!
if  not "" == "%MY_DAY_GEOFENCING_FLOW_EXIT_CONSOLE%" exit 1
exit /b 1

:mainEnd
if "%OS%"=="Windows_NT" endlocal

:omega
