LOCAL_PATH:= $(call my-dir)
include $(CLEAR_VARS)

LOCAL_MODULE_TAGS := optional

LOCAL_STATIC_JAVA_LIBRARIES := dom4j-1.6.1\
ant\

LOCAL_SRC_FILES := $(call all-java-files-under, src)

LOCAL_PACKAGE_NAME := EditorImage

LOCAL_SDK_VERSION := current

include $(BUILD_PACKAGE)

LOCAL_PREBUILT_STATIC_JAVA_LIBRARIES := dom4j-1.6.1:libs/dom4j-1.6.1.jar \
ant:libs/ant.jar

# Use the following include to make our test apk.
include $(call all-makefiles-under,$(LOCAL_PATH))
