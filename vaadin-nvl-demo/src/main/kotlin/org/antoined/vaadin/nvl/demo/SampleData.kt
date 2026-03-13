package org.antoined.vaadin.nvl.demo

import org.antoined.vaadin.nvl.NvlNode
import org.antoined.vaadin.nvl.NvlRelationship

/**
 * Provides reusable sample graph data (The Matrix movie cast) used across demo views.
 */
object SampleData {
    /** Returns a list of nodes representing actors, directors, producers, and movies. */
    fun movieNodes() = listOf(
        NvlNode(id = "keanu", caption = "Keanu Reeves", color = "#4C8BF5", size = 30),
        NvlNode(id = "carrie", caption = "Carrie-Anne Moss", color = "#4C8BF5", size = 25),
        NvlNode(id = "laurence", caption = "Laurence Fishburne", color = "#4C8BF5", size = 25),
        NvlNode(id = "hugo", caption = "Hugo Weaving", color = "#4C8BF5", size = 25),
        NvlNode(id = "lilly", caption = "Lilly Wachowski", color = "#34A853", size = 20),
        NvlNode(id = "lana", caption = "Lana Wachowski", color = "#34A853", size = 20),
        NvlNode(id = "joel", caption = "Joel Silver", color = "#FBBC05", size = 20),
        NvlNode(id = "matrix", caption = "The Matrix", color = "#EA4335", size = 40),
        NvlNode(id = "matrix2", caption = "The Matrix Reloaded", color = "#EA4335", size = 35),
        NvlNode(id = "matrix3", caption = "The Matrix Revolutions", color = "#EA4335", size = 35),
    )

    /** Returns a list of relationships (ACTED_IN, DIRECTED, PRODUCED) between the movie nodes. */
    fun movieRelationships() = listOf(
        NvlRelationship(id = "r1", from = "keanu", to = "matrix", caption = "ACTED_IN"),
        NvlRelationship(id = "r2", from = "carrie", to = "matrix", caption = "ACTED_IN"),
        NvlRelationship(id = "r3", from = "laurence", to = "matrix", caption = "ACTED_IN"),
        NvlRelationship(id = "r4", from = "hugo", to = "matrix", caption = "ACTED_IN"),
        NvlRelationship(id = "r5", from = "lilly", to = "matrix", caption = "DIRECTED"),
        NvlRelationship(id = "r6", from = "lana", to = "matrix", caption = "DIRECTED"),
        NvlRelationship(id = "r7", from = "joel", to = "matrix", caption = "PRODUCED"),
        NvlRelationship(id = "r8", from = "keanu", to = "matrix2", caption = "ACTED_IN"),
        NvlRelationship(id = "r9", from = "carrie", to = "matrix2", caption = "ACTED_IN"),
        NvlRelationship(id = "r10", from = "laurence", to = "matrix2", caption = "ACTED_IN"),
        NvlRelationship(id = "r11", from = "hugo", to = "matrix2", caption = "ACTED_IN"),
        NvlRelationship(id = "r12", from = "lilly", to = "matrix2", caption = "DIRECTED"),
        NvlRelationship(id = "r13", from = "lana", to = "matrix2", caption = "DIRECTED"),
        NvlRelationship(id = "r14", from = "keanu", to = "matrix3", caption = "ACTED_IN"),
        NvlRelationship(id = "r15", from = "carrie", to = "matrix3", caption = "ACTED_IN"),
        NvlRelationship(id = "r16", from = "laurence", to = "matrix3", caption = "ACTED_IN"),
        NvlRelationship(id = "r17", from = "lilly", to = "matrix3", caption = "DIRECTED"),
        NvlRelationship(id = "r18", from = "lana", to = "matrix3", caption = "DIRECTED"),
    )
}
