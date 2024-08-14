package com.pedestriamc.strings.api;

public final class StringsProvider{

    private static StringsAPI api;

    /**
     * Provides an instance of the StringsAPI.
     * @return A instance of StringsAPI.
     */
    public static StringsAPI get(){
        if(api == null){
            throw new IllegalStateException("StringsProvider not initialized.");
        }
        return StringsProvider.api;
    }

    public static void register(StringsAPI api){
        if(StringsProvider.api != null){
            throw new IllegalStateException("StringsProvider already initialized.");
        }
        StringsProvider.api = api;
    }

    /**
     * Provides a short of the API version.
     * @return A short of the API version.
     */
    public static short getVersion(){
        return 1;
    }
}
