package com.pedestriamc.strings.api;

/**
 * Represents the default membership of a StringsChannel.
 * If the membership is Membership.DEFAULT, all players receive messages here from default.
 */
public enum Membership {
    DEFAULT, PERMISSION, PROTECTED, PARTY
}
