package com.patagonia.app.domain.model

import org.junit.Assert.assertEquals
import org.junit.Test

class SpeciesCategoryTest {

    @Test
    fun `puma is classified as MAMMAL`() {
        assertEquals(SpeciesCategory.MAMMAL, SpeciesCategory.fromSpeciesName("Puma"))
    }

    @Test
    fun `huemul is classified as MAMMAL`() {
        assertEquals(SpeciesCategory.MAMMAL, SpeciesCategory.fromSpeciesName("Huemul deer"))
    }

    @Test
    fun `condor is classified as BIRD`() {
        assertEquals(SpeciesCategory.BIRD, SpeciesCategory.fromSpeciesName("Andean Condor"))
    }

    @Test
    fun `penguin is classified as BIRD`() {
        assertEquals(SpeciesCategory.BIRD, SpeciesCategory.fromSpeciesName("Magellanic Penguin"))
    }

    @Test
    fun `araucaria is classified as PLANT`() {
        assertEquals(SpeciesCategory.PLANT, SpeciesCategory.fromSpeciesName("Araucaria"))
    }

    @Test
    fun `copihue is classified as PLANT`() {
        assertEquals(SpeciesCategory.PLANT, SpeciesCategory.fromSpeciesName("Copihue flower"))
    }

    @Test
    fun `mushroom is classified as FUNGI`() {
        assertEquals(SpeciesCategory.FUNGI, SpeciesCategory.fromSpeciesName("Wild Mushroom"))
    }

    @Test
    fun `unknown species returns UNKNOWN`() {
        assertEquals(SpeciesCategory.UNKNOWN, SpeciesCategory.fromSpeciesName("Something Random"))
    }

    @Test
    fun `classification is case-insensitive`() {
        assertEquals(SpeciesCategory.MAMMAL, SpeciesCategory.fromSpeciesName("PUMA"))
        assertEquals(SpeciesCategory.BIRD, SpeciesCategory.fromSpeciesName("CONDOR"))
    }
}
