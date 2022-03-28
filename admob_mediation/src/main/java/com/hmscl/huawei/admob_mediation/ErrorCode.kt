/*
 *   Copyright 2022. Huawei Technologies Co., Ltd. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.hmscl.huawei.admob_mediation

enum class ErrorCode(val Id: Int) {
    UNKNOWN(0), BAD_REQUEST(1), NETWORK_ERROR(2), NO_INVENTORY(3);

    companion object {
        @JvmStatic //to be accessible from java
        fun fromInt(givenInt: Int): ErrorCode {
            return when (givenInt) {
                UNKNOWN.Id -> UNKNOWN
                BAD_REQUEST.Id -> BAD_REQUEST
                NETWORK_ERROR.Id -> NETWORK_ERROR
                NO_INVENTORY.Id -> NO_INVENTORY
                else -> throw Exception("Invalid id `$givenInt`, available ids are ${values().map { it.Id }}") // or a null or something
            }
        }
    }
}