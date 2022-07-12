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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Via i509VCB, a trick to get Brig onto the Knot classpath in order to properly mix in.
 *
 * <p>YOU SHOULD ONLY USE THIS CLASS DURING "preLaunch" and ONLY TARGET A CLASS WHICH IS NOT ANY CLASS YOU MIXIN TO.
 *
 * This will likely not work on Gson because FabricLoader has some special logic related to Gson.</p>
 *
 * Original on GitHub at <a href="https://github.com/i509VCB/Fabric-Junkkyard/blob/ce278daa93804697c745a51af06ec812896ec2ad/src/main/java/me/i509/junkkyard/hacks/PreLaunchHacks.java">i509VCB/Fabric-Junkkyard</a>
 *
 * <p>Not needed on loader 0.14.0+</p>
 */
public class PreLaunchHacks {
    private PreLaunchHacks() {}

    private static final ClassLoader KNOT_CLASSLOADER = Thread.currentThread().getContextClassLoader();
    private static final Method ADD_URL_METHOD;

    static {
        Method addUrl = null;
        try {
            addUrl = KNOT_CLASSLOADER.getClass().getMethod("loadClass", String.class);
            addUrl.setAccessible(true);
        } catch (final ReflectiveOperationException e) {
            // Loader 0.14.0+
        }
        ADD_URL_METHOD = addUrl;
    }

    /**
     * Hackily load the package which a mixin may exist within.
     *
     * YOU SHOULD NOT TARGET A CLASS WHICH YOU MIXIN TO.
     *
     * @param pathOfAClass The path of any class within the package.
     * @throws ClassNotFoundException if an unknown class name is used
     * @throws InvocationTargetException if an error occurs while injecting
     * @throws IllegalAccessException if an error occurs while injecting
     */
    public static void hackilyLoadForMixin(final String pathOfAClass) throws ClassNotFoundException, InvocationTargetException, IllegalAccessException {
        if (ADD_URL_METHOD != null) {
            //final URL url = Class.forName(pathOfAClass).getProtectionDomain().getCodeSource().getLocation();
            ADD_URL_METHOD.invoke(KNOT_CLASSLOADER, pathOfAClass);
        }
    }
}
