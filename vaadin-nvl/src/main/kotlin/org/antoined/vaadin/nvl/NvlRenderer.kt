package org.antoined.vaadin.nvl

/**
 * Rendering backend for the graph visualization.
 *
 * [CANVAS] supports node/relationship captions; [WEBGL] offers better performance for large graphs
 * but does not render captions.
 */
enum class NvlRenderer(val jsValue: String) {
    CANVAS("canvas"),
    WEBGL("webgl"),
}
