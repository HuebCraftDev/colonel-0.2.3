/*
 * Colonel -- a brigadier expansion library
 * Copyright (C) zml and Colonel contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ca.stellardrift.colonel.impl;


import net.minecraft.util.Identifier;

import java.util.Set;

public interface ServerPlayerBridge {
    /**
     * Set of registered optional argument types
     *
     * @return immutable set of type identifiers
     */
    Set<Identifier> colonel$knownArguments();

    /**
     * Set the set of registered optional argument types.
     *
     * @param arguments set of type identifiers
     */
    void colonel$knownArguments(final Set<Identifier> arguments);
}
