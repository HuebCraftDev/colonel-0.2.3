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

import com.mojang.brigadier.tree.CommandNode;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = CommandNode.class, remap = false)
public class MixinCommandNode<S> {

    // https://github.com/Mojang/brigadier/pull/68
    // while this fix was originally provided as a performance boost,
    // our primary goal here is to make redirects not dependent on alphabetical order.
    // this should have no effect on 1.17+, where this fix has been applied directly
    /*
    @Inject(method = "addChild", at = @At(value = "INVOKE", target = "Ljava/util/Map;entrySet()Ljava/util/Set;"), cancellable = true, require = 0)
    private void colonel$dontSortChildren(final CommandNode<S> child, final CallbackInfo ci) {
        ci.cancel();
    }*/
}
