package com.example

import com.example.ai.KnowledgeBase
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {

    @Test
    fun testDeepanitaIdentity() {
        assertEquals("Deepanita", KnowledgeBase.ASSISTANT_NAME)
        assertEquals("দীপানিতা", KnowledgeBase.ASSISTANT_NAME_BN)
        assertEquals("Siddhartha Mojumdar", KnowledgeBase.CREATOR_NAME)
        assertEquals("সিদ্ধার্থ মোজুমদার", KnowledgeBase.CREATOR_NAME_BN)
    }

    @Test
    fun testMedicinesDirectory() {
        assertTrue("Medicines directory should not be empty", KnowledgeBase.MEDICINES.isNotEmpty())
        val paracetamol = KnowledgeBase.MEDICINES.find { it.name.contains("Paracetamol") }
        assertNotNull("Paracetamol should exist", paracetamol)
        assertTrue(paracetamol!!.commonUses.contains("জ্বর"))
    }

    @Test
    fun testStoriesAndSongs() {
        assertTrue("Stories list should contain stories", KnowledgeBase.STORIES.isNotEmpty())
        assertTrue("Songs list should contain songs", KnowledgeBase.SONGS.isNotEmpty())
        val deepanitaStory = KnowledgeBase.STORIES.find { it.title.contains("দীপানিতা") }
        assertNotNull("Deepanita story should exist", deepanitaStory)
    }
}
