#include <stdio.h>
#include <unistd.h>
#include <string.h>
#include <errno.h>
#include <sys/socket.h>
#include <sys/un.h>
#include "emze_nio_socket_SocketUtil.h"


static int getFamily(JNIEnv *env, jstring familyName);
static int getType(JNIEnv *env, jstring typeName);
static void fillSockaddr(JNIEnv *env, jstring address, struct sockaddr_un * addrUn);

JNIEXPORT jstring JNICALL Java_emze_nio_socket_SocketUtil_nativeError( JNIEnv *env, jobject obj) {
	return (*env)->NewStringUTF( env, strerror(errno) );
}

JNIEXPORT jint JNICALL Java_emze_nio_socket_SocketUtil_nativeGetFamilyId
  (JNIEnv *env, jclass cl, jstring familyName) {

	return getFamily(env, familyName);
}

JNIEXPORT jint JNICALL Java_emze_nio_socket_SocketUtil_nativeGetTypeId
  (JNIEnv *env, jclass cl, jstring typeName) {

	return getType(env, typeName);
}

JNIEXPORT jint JNICALL Java_emze_nio_socket_SocketUtil_nativeSocket
  (JNIEnv *env, jclass cl, jint family, jint type) {

	return socket(family, type, 0);
}

JNIEXPORT jint JNICALL Java_emze_nio_socket_SocketUtil_nativeClose
  (JNIEnv *env, jclass cl, jint fd) {

	return close(fd);
}

JNIEXPORT jint JNICALL Java_emze_nio_socket_SocketUtil_nativeBind
  (JNIEnv *env, jclass cl, jint fd, jint family, jstring address) {

	struct sockaddr_un addressUn;
	memset(&addressUn, 0, sizeof(addressUn));
	addressUn.sun_family = (sa_family_t) family;
	
	fillSockaddr(env, address, &addressUn);

	return bind(fd, (const struct sockaddr *) & addressUn, sizeof(struct sockaddr_un));
}

JNIEXPORT jint JNICALL Java_emze_nio_socket_SocketUtil_nativeListen
  (JNIEnv *env, jclass cl, jint fd, jint numberOfConnections) {

	return listen(fd, numberOfConnections);
}

JNIEXPORT jint JNICALL Java_emze_nio_socket_SocketUtil_nativeAccept
  (JNIEnv *env, jclass cl, jint fd) {

	struct sockaddr_un clientAddr;
	socklen_t clientAddrSize = sizeof(clientAddr);
	
	memset(&clientAddr, 0, sizeof(clientAddr));

	return accept(fd, (struct sockaddr *) & clientAddr, & clientAddrSize);
}

JNIEXPORT jint JNICALL Java_emze_nio_socket_SocketUtil_nativeConnect
  (JNIEnv *env, jclass cl, jint fd, jint family, jstring address) {

	struct sockaddr_un addressUn;
	memset(&addressUn, 0, sizeof(addressUn));
	addressUn.sun_family = (sa_family_t) family;
	
	fillSockaddr(env, address, &addressUn);

	return connect(fd, (const struct sockaddr *) & addressUn, sizeof(struct sockaddr_un));
}


JNIEXPORT jint JNICALL Java_emze_nio_socket_SocketUtil_nativeSend
  (JNIEnv * env, jclass cl, jint fd, jbyteArray bytes) {

	jbyte* bufferPtr = (*env)->GetByteArrayElements(env, bytes, NULL);
	jsize lengthOfArray = (*env)->GetArrayLength(env, bytes);
	
	ssize_t ret = send(fd, bufferPtr, (size_t) lengthOfArray, MSG_EOR);
	(*env)->ReleaseByteArrayElements(env, bytes, bufferPtr, 0);
	return (jint) ret;
}

JNIEXPORT jint JNICALL Java_emze_nio_socket_SocketUtil_nativeRecv
  (JNIEnv * env, jclass cl, jint fd, jbyteArray bytes, jint maxSize) {

	jbyte buffer[maxSize];
	
	ssize_t ret = recv(fd, buffer, sizeof(buffer), 0);
	if (ret > 0) {
		(*env)->SetByteArrayRegion(env, bytes, 0, (jsize) ret, buffer);
	}
	return (jint) ret;
}

static int getFamily(JNIEnv *env, jstring familyName) {
    int ret = -1;
    const char *familyNameStr = (*env)->GetStringUTFChars(env, familyName, NULL);
    if (strcmp("UNIX", familyNameStr) == 0) {
    	ret = AF_UNIX;
    }
    (*env)->ReleaseStringUTFChars(env, familyName, familyNameStr);
    return ret;
}

static int getType(JNIEnv *env, jstring typeName) {
    int ret = -1;
    const char *typeNameStr = (*env)->GetStringUTFChars(env, typeName, NULL);
    if (strcmp("SEQPACKET", typeNameStr) == 0) {
    	ret = SOCK_SEQPACKET;
    }
    (*env)->ReleaseStringUTFChars(env, typeName, typeNameStr);
    return ret;
}

static void fillSockaddr(JNIEnv *env, jstring address, struct sockaddr_un * addrUn) {
    const char *addressStr = (*env)->GetStringUTFChars(env, address, NULL);

	strcpy(addrUn->sun_path, addressStr);
    (*env)->ReleaseStringUTFChars(env, address, addressStr);

	if (addrUn->sun_path[0] == '@') {
		addrUn->sun_path[0] = 0; //abstract path
	}
}

