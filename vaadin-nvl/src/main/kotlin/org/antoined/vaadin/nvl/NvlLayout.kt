package org.antoined.vaadin.nvl

/**
 * Available graph layout algorithms.
 *
 * Each entry maps to its NVL JavaScript string identifier via [jsValue].
 */
enum class NvlLayout(val jsValue: String) {
    FORCE_DIRECTED("forceDirected"),
    HIERARCHICAL("hierarchical"),
    GRID("grid"),
    FREE("free"),
    D3_FORCE("d3Force"),
    CIRCULAR("circular"),
}
