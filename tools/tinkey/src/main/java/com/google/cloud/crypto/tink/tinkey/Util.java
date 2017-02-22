// Copyright 2017 Google Inc.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//
////////////////////////////////////////////////////////////////////////////////

package com.google.cloud.crypto.tink.tinkey;

import com.google.cloud.crypto.tink.GoogleCloudKmsProto.GoogleCloudKmsAeadKey;
import com.google.cloud.crypto.tink.KmsEnvelopeProto.KmsEnvelopeAeadKeyFormat;
import com.google.cloud.crypto.tink.KmsEnvelopeProto.KmsEnvelopeAeadParams;
import com.google.cloud.crypto.tink.TinkProto.KeyFormat;

import com.google.common.collect.ImmutableSet;
import com.google.common.reflect.ClassPath;
import com.google.common.reflect.ClassPath.ClassInfo;
import com.google.protobuf.Any;
import com.google.protobuf.Message;
import com.google.protobuf.Message.Builder;
import com.google.protobuf.TextFormat;

import java.io.IOException;
import java.security.GeneralSecurityException;

/**
 * Various helpers.
 */
public class Util {
  /**
   * By convention, the key proto of Xyz would be XyzKey.
   * For example, the key proto of AesGcm is AesGcmKey.
   */
  public static final String KEY_SUFFIX = "Key";

  /**
   * By convention, the key format proto of Xyz would be XyzKeyFormat.
   * For example, the key format proto of AesGcm is AesGcmKeyFormat.
   */
  public static final String KEY_FORMAT_SUFFIX = "KeyFormat";

  /**
   * @returns a {@code KeyFormat} for {@code keyType} and {@code keyFormat}. For example,
   * createKeyFormat("AesGcm", "key_size: 32") would return a {@code KeyFormat} of 256-bit AesGcm.
   *
   * @param the key type. By convention this is the name of the crypto algorithm, e.g., AesGcm.
   * @param a text format of some XyzKeyFormat-proto.
   * @throws IllegalArgumentException if {@code keyType} has not equivalent proto class.
   */
  public static KeyFormat createKeyFormat(String keyType, String keyFormat)
      throws Exception {
    // To parse {@code keyFormat}, we need to find the corresponding proto class.
    String keyFormatName = keyType + KEY_FORMAT_SUFFIX;
    Class<?> keyFormatClass = loadClass(keyFormatName);
    if (keyFormatClass == null) {
      throw new IllegalArgumentException("Cannot find key type " + keyType);
    }
    Builder builder = getBuilder(keyFormatClass);
    TextFormat.merge(keyFormat, builder);

    return createKeyFormat(getTypeUrl(keyType), Any.pack(builder.build()));
  }

  /**
   * @returns the full type url starting with types.googleapis.com of {@code keyType}.
   * @throws IllegalArgumentException if {@code keyType} has not equivalent proto class.
   */
  public static String getTypeUrl(String keyType) throws Exception {
    Class<?> keyClass = loadClass(keyType + KEY_SUFFIX);
    if (keyClass == null) {
      throw new IllegalArgumentException("Cannot find key type " + keyType);
    }
    Builder builder = getBuilder(keyClass);
    return Any.pack(builder.build()).getTypeUrl();
  }

  /**
   * @returns a {@code Builder} of {@code messageClass} which is a protobuf message.
   */
  public static Builder getBuilder(Class<?> messageClass) throws Exception {
    return (Builder) messageClass
        .getDeclaredMethod("newBuilder")
        .invoke(null /* Object, ignored */);
  }

  /**
   * Finds and loads {@code className}.
   */
  public static Class<?> loadClass(String className) throws IOException {
    ImmutableSet<ClassInfo> classInfos =
        ClassPath.from(new Tinkey().getClass().getClassLoader()).getAllClasses();
    for (ClassInfo classInfo : classInfos) {
      if (classInfo.getName().endsWith(className)) {
        return classInfo.load();
      }
    }
    return null;
  }

  /**
   * @returns a {@code KeyFormat} constructed from {@code keyType} and {@code keyFormat}.
   */
  public static KeyFormat createKeyFormat(String keyType, Any keyFormat) {
    return KeyFormat.newBuilder()
        .setKeyType(keyType)
        .setFormat(keyFormat)
        .build();
  }

  /**
   * @returns a {@code GoogleCloudKmsAeadKey}.
   */
  public static GoogleCloudKmsAeadKey createGoogleCloudKmsAeadKey(String kmsKeyUri)
      throws Exception {
    return GoogleCloudKmsAeadKey.newBuilder()
        .setKmsKeyUri(kmsKeyUri)
        .build();
  }

  /**
   * @returns a {@code createKmsEnvelopeAeadKeyFormat}.
   */
  public static KmsEnvelopeAeadKeyFormat createKmsEnvelopeAeadKeyFormat(
      Any kmsKey, KeyFormat dekFormat) throws Exception {
    KmsEnvelopeAeadParams params = KmsEnvelopeAeadParams.newBuilder()
        .setDekFormat(dekFormat)
        .setKmsKey(kmsKey)
        .build();
    return KmsEnvelopeAeadKeyFormat.newBuilder()
        .setParams(params)
        .build();
  }
}
