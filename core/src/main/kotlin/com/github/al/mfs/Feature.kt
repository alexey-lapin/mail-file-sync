package com.github.al.mfs

interface Feature {
    val name: String
}

interface SenderFeature : Feature

interface ReceiverFeature : Feature
