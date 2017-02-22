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

import org.kohsuke.args4j.Option;

/**
 * Options for add or rotate command.
 */
class AddRotateOptions extends InOptions {
  @Option(name = "--key-type", required = true, usage = "The key type of the new key")
  String keyTypeValue;

  @Option(name = "--key-format", required = true, usage = "The key format of the new key")
  String keyFormatValue;
}
