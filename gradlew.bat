@ECHO OFF
SET DIRNAME=%~dp0
IF "%DIRNAME%" == "" SET DIRNAME=.
SET CLASSPATH=%DIRNAME%\gradle\wrapper\gradle-wrapper.jar
java -Xmx64m -Xms64m -cp "%CLASSPATH%" org.gradle.wrapper.GradleWrapperMain %*
