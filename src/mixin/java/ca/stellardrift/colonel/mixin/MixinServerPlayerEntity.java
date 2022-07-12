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
package ca.stellardrift.colonel.mixin;

import ca.stellardrift.colonel.impl.ServerPlayerBridge;
import com.google.common.collect.ImmutableSet;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Set;

/**
 * Mixin to store our per-player data
 */
@Mixin(ServerPlayerEntity.class)
public class MixinServerPlayerEntity implements ServerPlayerBridge {

    private Set<Identifier> colonel$arguments = ImmutableSet.of();

    @Override
    public Set<Identifier> colonel$knownArguments() {
        return this.colonel$arguments;
    }

    @Override
    public void colonel$knownArguments(Set<Identifier> arguments) {
        this.colonel$arguments = ImmutableSet.copyOf(arguments);
    }

    // Copy player data on respawn
    @Inject(method = "copyFrom", at = @At("RETURN"))
    public void colonel$copyData(final ServerPlayerEntity from, final boolean alive, final CallbackInfo ci) {
        colonel$knownArguments(((ServerPlayerBridge) from).colonel$knownArguments());
    }
}
