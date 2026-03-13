package org.antoined.vaadin.nvl

import com.vaadin.flow.component.Component
import com.vaadin.flow.component.ComponentEvent
import com.vaadin.flow.component.ComponentEventListener
import com.vaadin.flow.component.DomEvent
import com.vaadin.flow.component.EventData
import com.vaadin.flow.component.HasSize
import com.vaadin.flow.component.Tag
import com.vaadin.flow.component.dependency.JsModule
import com.vaadin.flow.component.dependency.NpmPackage
import com.vaadin.flow.dom.DomListenerRegistration
import com.vaadin.flow.dom.Element
import com.vaadin.flow.shared.Registration
import elemental.json.Json
import elemental.json.JsonArray
import elemental.json.JsonObject
import elemental.json.JsonValue
import java.io.Serializable

/**
 * Vaadin Flow server-side component wrapping the
 * [Neo4j NVL](https://neo4j.com/docs/nvl/current/) JavaScript library for
 * interactive graph visualization.
 *
 * The component communicates with its client-side `nvl-graph.ts` web component
 * via properties, JS function calls, and DOM events.
 *
 * @param options Optional initial [NvlOptions] applied when the component is first attached.
 */
@Tag("nvl-graph")
@JsModule("./nvl-graph.ts")
@NpmPackage(value = "@neo4j-nvl/base", version = "1.1.0")
@NpmPackage(value = "@neo4j-nvl/interaction-handlers", version = "1.1.0")
class NvlGraph(options: NvlOptions? = null) : Component(), HasSize {

    init {
        options?.let { element.setPropertyJson("options", it.toJson()) }
    }

    // ===== Graph data =====

    /** Replaces all nodes in the graph. */
    fun setNodes(nodes: List<NvlNode>) {
        element.setPropertyJson("nodes", nodes.map { it.toJson() }.toJsonArray())
    }

    /** Replaces all relationships in the graph. */
    fun setRelationships(relationships: List<NvlRelationship>) {
        element.setPropertyJson("relationships", relationships.map { it.toJson() }.toJsonArray())
    }

    /** Convenience method to replace both nodes and relationships at once. */
    fun setGraph(nodes: List<NvlNode>, relationships: List<NvlRelationship>) {
        setNodes(nodes)
        setRelationships(relationships)
    }

    /** Adds new elements or updates existing ones (matched by id). */
    fun addAndUpdateElements(
        nodes: List<NvlNode> = emptyList(),
        relationships: List<NvlRelationship> = emptyList(),
    ) {
        element.callJsFunction(
            "addAndUpdateElements",
            nodes.map { it.toJson() }.toJsonArray(),
            relationships.map { it.toJson() }.toJsonArray(),
        )
    }

    /** Updates properties of existing nodes and/or relationships (matched by id). */
    fun updateElements(
        nodes: List<NvlNode> = emptyList(),
        relationships: List<NvlRelationship> = emptyList(),
    ) {
        element.callJsFunction(
            "updateElements",
            nodes.map { it.toJson() }.toJsonArray(),
            relationships.map { it.toJson() }.toJsonArray(),
        )
    }

    /** Adds new nodes and/or relationships to the graph. */
    fun addElements(
        nodes: List<NvlNode> = emptyList(),
        relationships: List<NvlRelationship> = emptyList(),
    ) {
        element.callJsFunction(
            "addElements",
            nodes.map { it.toJson() }.toJsonArray(),
            relationships.map { it.toJson() }.toJsonArray(),
        )
    }

    /** Removes nodes by their IDs; associated relationships are also removed. */
    fun removeNodes(nodeIds: List<String>) {
        element.callJsFunction("removeNodes", nodeIds.toJsonStringArray())
    }

    /** Removes relationships by their IDs. */
    fun removeRelationships(relationshipIds: List<String>) {
        element.callJsFunction("removeRelationships", relationshipIds.toJsonStringArray())
    }

    // ===== Layout =====

    /** Switches the graph to the given [layout] algorithm and restarts the layout. */
    fun setLayout(layout: NvlLayout) {
        element.callJsFunction("setLayout", layout.jsValue)
    }

    /** Sets algorithm-specific layout options (e.g. [NvlHierarchicalOptions]). */
    fun setLayoutOptions(options: NvlLayoutOptions) {
        element.callJsFunction("setLayoutOptions", options.toJson())
    }

    // ===== Zoom & pan =====

    /** Sets the zoom level to the given absolute value. */
    fun setZoom(zoomValue: Double) {
        element.callJsFunction("setZoom", zoomValue)
    }

    /** Sets the pan offset to the given pixel coordinates. */
    fun setPan(panX: Double, panY: Double) {
        element.callJsFunction("setPan", panX, panY)
    }

    /** Sets both zoom level and pan offset in a single call. */
    fun setZoomAndPan(zoom: Double, panX: Double, panY: Double) {
        element.callJsFunction("setZoomAndPan", zoom, panX, panY)
    }

    /** Resets the zoom level to its initial/default value. */
    fun resetZoom() {
        element.callJsFunction("resetZoom")
    }

    /** Adjusts zoom and pan to fit the given nodes into the viewport. */
    fun fit(nodeIds: List<String>, zoomOptions: NvlZoomOptions? = null) {
        if (zoomOptions != null) {
            element.callJsFunction("fit", nodeIds.toJsonStringArray(), zoomOptions.toJson())
        } else {
            element.callJsFunction("fit", nodeIds.toJsonStringArray())
        }
    }

    /** Adjusts zoom and pan to fit the entire graph into the viewport. */
    fun fitAll(zoomOptions: NvlZoomOptions? = null) {
        if (zoomOptions != null) {
            element.callJsFunction("fitAll", zoomOptions.toJson())
        } else {
            element.callJsFunction("fitAll")
        }
    }

    // ===== Selection =====

    /** Clears the selection state of all nodes and relationships. */
    fun deselectAll() {
        element.callJsFunction("deselectAll")
    }

    // ===== Pinning =====

    /** Pins a node so that layout forces no longer affect its position. */
    fun pinNode(nodeId: String) {
        element.callJsFunction("pinNode", nodeId)
    }

    /** Unpins nodes so that layout forces can move them again. */
    fun unpinNodes(nodeIds: List<String>) {
        element.callJsFunction("unpinNodes", nodeIds.toJsonStringArray())
    }

    // ===== Node positions =====

    /**
     * Moves nodes to explicit positions. Only [NvlNode.x] and [NvlNode.y] are used.
     *
     * @param updateLayout When `true`, the layout engine is informed of the new positions.
     */
    fun setNodePositions(nodes: List<NvlNode>, updateLayout: Boolean = false) {
        element.callJsFunction(
            "setNodePositions",
            nodes.map { it.toJson() }.toJsonArray(),
            updateLayout,
        )
    }

    // ===== Node dragging =====

    /** Enables or disables interactive node dragging by the user. */
    fun setNodeDraggingEnabled(enabled: Boolean) {
        element.setProperty("nodeDraggingEnabled", enabled)
    }

    // ===== Renderer =====

    /** Switches the rendering backend at runtime. */
    fun setRenderer(renderer: NvlRenderer) {
        element.callJsFunction("setRenderer", renderer.jsValue)
    }

    // ===== Restart =====

    /**
     * Restarts the NVL instance, optionally applying new [options].
     *
     * @param retainPositions When `true`, existing node positions are preserved across the restart.
     */
    fun restart(options: NvlOptions? = null, retainPositions: Boolean = false) {
        if (options != null) {
            element.callJsFunction("restart", options.toJson(), retainPositions)
        } else {
            element.callJsFunction("restart", Json.createObject(), retainPositions)
        }
    }

    // ===== Export =====

    /** Triggers a client-side PNG download of the visible graph area. */
    fun saveToFile(filename: String? = null, backgroundColor: String? = null) {
        element.callJsFunction("saveToFile", optionsJsonOf(filename, backgroundColor))
    }

    /** Triggers a client-side SVG download of the visible graph area. */
    fun saveToSvg(filename: String? = null, backgroundColor: String? = null) {
        element.callJsFunction("saveToSvg", optionsJsonOf(filename, backgroundColor))
    }

    /** Triggers a client-side PNG download of the full graph (may be larger than the viewport). */
    fun saveFullGraphToLargeFile(filename: String? = null, backgroundColor: String? = null) {
        element.callJsFunction("saveFullGraphToLargeFile", optionsJsonOf(filename, backgroundColor))
    }

    // ===== Client→Server getters (async) =====

    /** Asynchronously retrieves the current zoom scale from the client. */
    fun getScale(callback: SerializableCallback<Double>) {
        element.callJsFunction("getScale").then(Double::class.java) { callback.accept(it) }
    }

    /** Asynchronously retrieves the current pan offset from the client. */
    fun getPan(callback: SerializableCallback<NvlPoint>) {
        element.callJsFunction("getPan").then(JsonValue::class.java) { json ->
            val obj = json as JsonObject
            callback.accept(NvlPoint(obj.getNumber("x"), obj.getNumber("y")))
        }
    }

    /** Asynchronously retrieves the min/max zoom limits from the client. */
    fun getZoomLimits(callback: SerializableCallback<NvlZoomLimits>) {
        element.callJsFunction("getZoomLimits").then(JsonValue::class.java) { json ->
            val obj = json as JsonObject
            callback.accept(NvlZoomLimits(obj.getNumber("minZoom"), obj.getNumber("maxZoom")))
        }
    }

    /** Asynchronously retrieves all nodes currently in the graph from the client. */
    fun getNodes(callback: SerializableCallback<List<NvlNode>>) {
        element.callJsFunction("getNodes").then(JsonValue::class.java) { json ->
            callback.accept(parseNodeArray(json as JsonArray))
        }
    }

    /** Asynchronously retrieves all relationships currently in the graph from the client. */
    fun getRelationships(callback: SerializableCallback<List<NvlRelationship>>) {
        element.callJsFunction("getRelationships").then(JsonValue::class.java) { json ->
            callback.accept(parseRelationshipArray(json as JsonArray))
        }
    }

    /** Asynchronously retrieves the currently selected nodes from the client. */
    fun getSelectedNodes(callback: SerializableCallback<List<NvlNode>>) {
        element.callJsFunction("getSelectedNodes").then(JsonValue::class.java) { json ->
            callback.accept(parseNodeArray(json as JsonArray))
        }
    }

    /** Asynchronously retrieves the currently selected relationships from the client. */
    fun getSelectedRelationships(callback: SerializableCallback<List<NvlRelationship>>) {
        element.callJsFunction("getSelectedRelationships").then(JsonValue::class.java) { json ->
            callback.accept(parseRelationshipArray(json as JsonArray))
        }
    }

    /** Asynchronously retrieves the current x/y positions of all nodes from the client. */
    fun getNodePositions(callback: SerializableCallback<List<NvlNode>>) {
        element.callJsFunction("getNodePositions").then(JsonValue::class.java) { json ->
            callback.accept(parseNodeArray(json as JsonArray))
        }
    }

    /** Asynchronously checks whether the layout is still computing/animating. */
    fun isLayoutMoving(callback: SerializableCallback<Boolean>) {
        element.callJsFunction("isLayoutMoving").then(Boolean::class.java) { callback.accept(it) }
    }

    /** Asynchronously retrieves a Base64 data URL of the current graph rendering. */
    fun getImageDataUrl(callback: SerializableCallback<String>, backgroundColor: String? = null) {
        val opts = Json.createObject().apply {
            backgroundColor?.let { put("backgroundColor", it) }
        }
        element.callJsFunction("getImageDataUrl", opts).then(String::class.java) { callback.accept(it) }
    }

    // ===== Events =====

    /** Registers a listener for single-click events on nodes, relationships, or the canvas. */
    fun addClickListener(listener: ComponentEventListener<NvlClickEvent>): Registration =
        addListener(NvlClickEvent::class.java, listener)

    /** Registers a listener for double-click events on nodes or relationships. */
    fun addDoubleClickListener(listener: ComponentEventListener<NvlDoubleClickEvent>): Registration =
        addListener(NvlDoubleClickEvent::class.java, listener)

    /** Registers a listener for right-click (context menu) events on nodes or relationships. */
    fun addContextMenuListener(listener: ComponentEventListener<NvlContextMenuEvent>): Registration =
        addListener(NvlContextMenuEvent::class.java, listener)

    /** Registers a listener fired when the layout algorithm finishes computing. */
    fun addLayoutDoneListener(listener: ComponentEventListener<NvlLayoutDoneEvent>): Registration =
        addListener(NvlLayoutDoneEvent::class.java, listener)

    /** Registers a listener fired when the layout starts or stops computing. */
    fun addLayoutComputingListener(listener: ComponentEventListener<NvlLayoutComputingEvent>): Registration =
        addListener(NvlLayoutComputingEvent::class.java, listener)

    /** Registers a listener fired when a zoom/pan animation completes. */
    fun addZoomTransitionDoneListener(listener: ComponentEventListener<NvlZoomTransitionDoneEvent>): Registration =
        addListener(NvlZoomTransitionDoneEvent::class.java, listener)

    /** Registers a listener fired once the NVL instance is fully initialized on the client. */
    fun addInitializationListener(listener: ComponentEventListener<NvlInitializationEvent>): Registration =
        addListener(NvlInitializationEvent::class.java, listener)

    /** Registers a listener fired when the user finishes dragging a node. */
    fun addNodeDragEndListener(listener: ComponentEventListener<NvlNodeDragEndEvent>): Registration =
        addListener(NvlNodeDragEndEvent::class.java, listener)

    // ===== Event classes =====

    /** Fired on a single click. Carries the IDs of hit nodes and relationships. */
    @DomEvent("nvl-click")
    class NvlClickEvent(
        source: NvlGraph,
        fromClient: Boolean,
        @EventData("event.detail.nodeIds") nodeIds: JsonArray?,
        @EventData("event.detail.relationshipIds") relationshipIds: JsonArray?,
    ) : NvlHitEvent(source, fromClient, nodeIds, relationshipIds)

    /** Fired on a double click. Carries the IDs of hit nodes and relationships. */
    @DomEvent("nvl-dblclick")
    class NvlDoubleClickEvent(
        source: NvlGraph,
        fromClient: Boolean,
        @EventData("event.detail.nodeIds") nodeIds: JsonArray?,
        @EventData("event.detail.relationshipIds") relationshipIds: JsonArray?,
    ) : NvlHitEvent(source, fromClient, nodeIds, relationshipIds)

    /** Fired on a right-click / context menu. Carries the IDs of hit nodes and relationships. */
    @DomEvent("nvl-contextmenu")
    class NvlContextMenuEvent(
        source: NvlGraph,
        fromClient: Boolean,
        @EventData("event.detail.nodeIds") nodeIds: JsonArray?,
        @EventData("event.detail.relationshipIds") relationshipIds: JsonArray?,
    ) : NvlHitEvent(source, fromClient, nodeIds, relationshipIds)

    /**
     * Base class for pointer events that may hit nodes and/or relationships.
     *
     * @property nodeIds IDs of nodes under the pointer (empty if none).
     * @property relationshipIds IDs of relationships under the pointer (empty if none).
     */
    abstract class NvlHitEvent(
        source: NvlGraph,
        fromClient: Boolean,
        nodeIds: JsonArray?,
        relationshipIds: JsonArray?,
    ) : ComponentEvent<NvlGraph>(source, fromClient) {
        val nodeIds: List<String> = nodeIds?.toStringList() ?: emptyList()
        val relationshipIds: List<String> = relationshipIds?.toStringList() ?: emptyList()
    }

    /** Fired when the layout algorithm completes. */
    @DomEvent("nvl-layout-done")
    class NvlLayoutDoneEvent(
        source: NvlGraph,
        fromClient: Boolean,
    ) : ComponentEvent<NvlGraph>(source, fromClient)

    /** Fired when the layout computing state changes. Check [isComputing] for the current state. */
    @DomEvent("nvl-layout-computing")
    class NvlLayoutComputingEvent(
        source: NvlGraph,
        fromClient: Boolean,
        @EventData("event.detail.isComputing") val isComputing: Boolean,
    ) : ComponentEvent<NvlGraph>(source, fromClient)

    /** Fired when a zoom or fit animation completes. */
    @DomEvent("nvl-zoom-transition-done")
    class NvlZoomTransitionDoneEvent(
        source: NvlGraph,
        fromClient: Boolean,
    ) : ComponentEvent<NvlGraph>(source, fromClient)

    /** Fired once the NVL client-side instance is fully initialized and ready. */
    @DomEvent("nvl-initialization")
    class NvlInitializationEvent(
        source: NvlGraph,
        fromClient: Boolean,
    ) : ComponentEvent<NvlGraph>(source, fromClient)

    /**
     * Fired when the user finishes dragging a node.
     *
     * @property nodeId ID of the dragged node.
     * @property x Final horizontal position.
     * @property y Final vertical position.
     */
    @DomEvent("nvl-node-drag-end")
    class NvlNodeDragEndEvent(
        source: NvlGraph,
        fromClient: Boolean,
        @EventData("event.detail.nodeId") val nodeId: String,
        @EventData("event.detail.x") val x: Double,
        @EventData("event.detail.y") val y: Double,
    ) : ComponentEvent<NvlGraph>(source, fromClient)

    // ===== Helpers =====

    private fun optionsJsonOf(filename: String?, backgroundColor: String?): JsonObject =
        Json.createObject().apply {
            filename?.let { put("filename", it) }
            backgroundColor?.let { put("backgroundColor", it) }
        }

    private fun parseNodeArray(arr: JsonArray): List<NvlNode> =
        (0 until arr.length()).map { NvlNode.fromJson(arr.getObject(it)) }

    private fun parseRelationshipArray(arr: JsonArray): List<NvlRelationship> =
        (0 until arr.length()).map { NvlRelationship.fromJson(arr.getObject(it)) }
}

/**
 * A serializable callback for receiving asynchronous results from client-side JS calls.
 *
 * @param T The type of value returned by the client.
 */
fun interface SerializableCallback<T> : Serializable {
    fun accept(value: T)
}

/** A 2D point representing a pan offset or position in graph coordinates. */
data class NvlPoint(val x: Double, val y: Double)

/** The effective minimum and maximum zoom levels as reported by the client. */
data class NvlZoomLimits(val minZoom: Double, val maxZoom: Double)
