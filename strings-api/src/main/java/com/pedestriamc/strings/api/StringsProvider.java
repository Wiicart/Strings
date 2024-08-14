package com.pedestriamc.strings.api;

public class StringsProvider {

    private static StringsAPI api;

    /**
     * Provides an instance of the StringsAPI
     * @return A StringsAPI instance.
     */
    public static StringsAPI get(){ return api; }

    /**
     * Used by Strings to initialize API.
     * @param api API.
     */
    public static void setApi(StringsAPI api) {
        StringsProvider.api = api;
    }
}
