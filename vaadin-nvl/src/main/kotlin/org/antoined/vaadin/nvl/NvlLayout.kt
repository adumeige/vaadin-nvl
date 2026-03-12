package org.antoined.vaadin.nvl

enum class NvlLayout(val jsValue: String) {
    FORCE_DIRECTED("forceDirected"),
    HIERARCHICAL("hierarchical"),
    GRID("grid"),
    FREE("free"),
    D3_FORCE("d3Force"),
    CIRCULAR("circular"),
}
