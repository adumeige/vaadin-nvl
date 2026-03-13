package org.antoined.vaadin.nvl

import elemental.json.Json
import elemental.json.JsonObject

/**
 * Configuration options for the [NvlGraph] component, mapping to the
 * [Neo4j NVL constructor options](https://neo4j.com/docs/nvl/current/).
 *
 * All properties are optional and default to `null`, which lets NVL use its own defaults.
 *
 * @property layout The graph layout algorithm to use (e.g. [NvlLayout.FORCE_DIRECTED], [NvlLayout.HIERARCHICAL]).
 * @property minZoom Minimum allowed zoom level. Must be > 0.
 * @property maxZoom Maximum allowed zoom level.
 * @property allowDynamicMinZoom When `true`, the minimum zoom adjusts dynamically so the entire graph stays visible.
 * @property panX Initial horizontal pan offset in pixels.
 * @property panY Initial vertical pan offset in pixels.
 * @property initialZoom Initial zoom level applied when the graph first renders.
 * @property renderer The rendering backend: [NvlRenderer.CANVAS] (supports captions) or [NvlRenderer.WEBGL].
 * @property styling Global color overrides for nodes, relationships, and selection indicators.
 * @property disableTelemetry When `true`, disables anonymous usage telemetry sent by NVL.
 * @property disableWebWorkers When `true`, runs layout computation on the main thread instead of a Web Worker.
 * @property disableAria When `true`, disables ARIA attributes on the graph canvas for accessibility.
 * @property layoutOptions Algorithm-specific options (e.g. [NvlHierarchicalOptions], [NvlForceDirectedOptions]).
 */
data class NvlOptions(
    val layout: NvlLayout? = null,
    val minZoom: Double? = null,
    val maxZoom: Double? = null,
    val allowDynamicMinZoom: Boolean? = null,
    val panX: Double? = null,
    val panY: Double? = null,
    val initialZoom: Double? = null,
    val renderer: NvlRenderer? = null,
    val styling: NvlStyling? = null,
    val disableTelemetry: Boolean? = null,
    val disableWebWorkers: Boolean? = null,
    val disableAria: Boolean? = null,
    val layoutOptions: NvlLayoutOptions? = null,
) {
    fun toJson(): JsonObject = Json.createObject().apply {
        layout?.let { put("layout", it.jsValue) }
        minZoom?.let { put("minZoom", it) }
        maxZoom?.let { put("maxZoom", it) }
        allowDynamicMinZoom?.let { put("allowDynamicMinZoom", it) }
        panX?.let { put("panX", it) }
        panY?.let { put("panY", it) }
        initialZoom?.let { put("initialZoom", it) }
        renderer?.let { put("renderer", it.jsValue) }
        styling?.let { put("styling", it.toJson()) }
        disableTelemetry?.let { put("disableTelemetry", it) }
        disableWebWorkers?.let { put("disableWebWorkers", it) }
        disableAria?.let { put("disableAria", it) }
        layoutOptions?.let { put("layoutOptions", it.toJson()) }
    }
}

/**
 * Global styling overrides applied to all nodes and relationships in the graph.
 *
 * Color values should be CSS color strings (e.g. `"#4C8BF5"`, `"rgba(0,0,0,0.5)"`).
 *
 * @property defaultNodeColor Default fill color for nodes that don't specify their own color.
 * @property defaultRelationshipColor Default stroke color for relationships.
 * @property nodeDefaultBorderColor Border color for nodes in their default (unselected) state.
 * @property selectedBorderColor Outer border color for selected nodes.
 * @property selectedInnerBorderColor Inner border color for selected nodes.
 * @property dropShadowColor Color of the drop shadow rendered beneath nodes.
 * @property disabledItemColor Fill/stroke color for nodes and relationships marked as disabled.
 * @property disabledItemFontColor Font color for captions on disabled items.
 * @property minimapViewportBoxColor Border color of the viewport indicator in the minimap.
 */
data class NvlStyling(
    val defaultNodeColor: String? = null,
    val defaultRelationshipColor: String? = null,
    val nodeDefaultBorderColor: String? = null,
    val selectedBorderColor: String? = null,
    val selectedInnerBorderColor: String? = null,
    val dropShadowColor: String? = null,
    val disabledItemColor: String? = null,
    val disabledItemFontColor: String? = null,
    val minimapViewportBoxColor: String? = null,
) {
    fun toJson(): JsonObject = Json.createObject().apply {
        defaultNodeColor?.let { put("defaultNodeColor", it) }
        defaultRelationshipColor?.let { put("defaultRelationshipColor", it) }
        nodeDefaultBorderColor?.let { put("nodeDefaultBorderColor", it) }
        selectedBorderColor?.let { put("selectedBorderColor", it) }
        selectedInnerBorderColor?.let { put("selectedInnerBorderColor", it) }
        dropShadowColor?.let { put("dropShadowColor", it) }
        disabledItemColor?.let { put("disabledItemColor", it) }
        disabledItemFontColor?.let { put("disabledItemFontColor", it) }
        minimapViewportBoxColor?.let { put("minimapViewportBoxColor", it) }
    }
}

/**
 * Sealed interface for layout-algorithm-specific options.
 *
 * Implementations: [NvlForceDirectedOptions], [NvlHierarchicalOptions].
 */
sealed interface NvlLayoutOptions {
    fun toJson(): JsonObject
}

/**
 * Options specific to the [NvlLayout.FORCE_DIRECTED] layout algorithm.
 *
 * @property intelWorkaround Enables a workaround for rendering issues on some Intel GPUs.
 * @property enableCytoscape When `true`, uses the Cytoscape.js force layout engine instead of the default.
 */
data class NvlForceDirectedOptions(
    val intelWorkaround: Boolean? = null,
    val enableCytoscape: Boolean? = null,
) : NvlLayoutOptions {
    override fun toJson(): JsonObject = Json.createObject().apply {
        intelWorkaround?.let { put("intelWorkaround", it) }
        enableCytoscape?.let { put("enableCytoscape", it) }
    }
}

/**
 * Options specific to the [NvlLayout.HIERARCHICAL] layout algorithm.
 *
 * @property direction The direction in which the hierarchy flows.
 * @property packing The strategy used to pack disconnected sub-graphs.
 */
data class NvlHierarchicalOptions(
    val direction: Direction? = null,
    val packing: Packing? = null,
) : NvlLayoutOptions {
    /** Direction in which the hierarchical layout arranges nodes. */
    enum class Direction(val jsValue: String) {
        UP("up"), DOWN("down"), LEFT("left"), RIGHT("right")
    }

    /** Packing strategy for disconnected components in a hierarchical layout. */
    enum class Packing(val jsValue: String) {
        BIN("bin"), STACK("stack")
    }

    override fun toJson(): JsonObject = Json.createObject().apply {
        direction?.let { put("direction", it.jsValue) }
        packing?.let { put("packing", it.jsValue) }
    }
}

/**
 * Options passed to zoom-related operations such as `fit` and `fitAll` on [NvlGraph].
 *
 * @property noPan When `true`, only adjusts the zoom level without changing the pan position.
 * @property outOnly When `true`, only zooms out (never zooms in beyond the current level).
 * @property minZoom Minimum zoom level for this operation.
 * @property maxZoom Maximum zoom level for this operation.
 * @property animated When `true`, the zoom transition is animated.
 */
data class NvlZoomOptions(
    val noPan: Boolean? = null,
    val outOnly: Boolean? = null,
    val minZoom: Double? = null,
    val maxZoom: Double? = null,
    val animated: Boolean? = null,
) {
    fun toJson(): JsonObject = Json.createObject().apply {
        noPan?.let { put("noPan", it) }
        outOnly?.let { put("outOnly", it) }
        minZoom?.let { put("minZoom", it) }
        maxZoom?.let { put("maxZoom", it) }
        animated?.let { put("animated", it) }
    }
}
