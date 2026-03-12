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

@Tag("nvl-graph")
@JsModule("./nvl-graph.ts")
@NpmPackage(value = "@neo4j-nvl/base", version = "1.1.0")
@NpmPackage(value = "@neo4j-nvl/interaction-handlers", version = "1.1.0")
class NvlGraph(options: NvlOptions? = null) : Component(), HasSize {

    init {
        options?.let { element.setPropertyJson("options", it.toJson()) }
    }

    // ===== Graph data =====

    fun setNodes(nodes: List<NvlNode>) {
        element.setPropertyJson("nodes", nodes.map { it.toJson() }.toJsonArray())
    }

    fun setRelationships(relationships: List<NvlRelationship>) {
        element.setPropertyJson("relationships", relationships.map { it.toJson() }.toJsonArray())
    }

    fun setGraph(nodes: List<NvlNode>, relationships: List<NvlRelationship>) {
        setNodes(nodes)
        setRelationships(relationships)
    }

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

    fun removeNodes(nodeIds: List<String>) {
        element.callJsFunction("removeNodes", nodeIds.toJsonStringArray())
    }

    fun removeRelationships(relationshipIds: List<String>) {
        element.callJsFunction("removeRelationships", relationshipIds.toJsonStringArray())
    }

    // ===== Layout =====

    fun setLayout(layout: NvlLayout) {
        element.callJsFunction("setLayout", layout.jsValue)
    }

    fun setLayoutOptions(options: NvlLayoutOptions) {
        element.callJsFunction("setLayoutOptions", options.toJson())
    }

    // ===== Zoom & pan =====

    fun setZoom(zoomValue: Double) {
        element.callJsFunction("setZoom", zoomValue)
    }

    fun setPan(panX: Double, panY: Double) {
        element.callJsFunction("setPan", panX, panY)
    }

    fun setZoomAndPan(zoom: Double, panX: Double, panY: Double) {
        element.callJsFunction("setZoomAndPan", zoom, panX, panY)
    }

    fun resetZoom() {
        element.callJsFunction("resetZoom")
    }

    fun fit(nodeIds: List<String>, zoomOptions: NvlZoomOptions? = null) {
        if (zoomOptions != null) {
            element.callJsFunction("fit", nodeIds.toJsonStringArray(), zoomOptions.toJson())
        } else {
            element.callJsFunction("fit", nodeIds.toJsonStringArray())
        }
    }

    fun fitAll(zoomOptions: NvlZoomOptions? = null) {
        if (zoomOptions != null) {
            element.callJsFunction("fitAll", zoomOptions.toJson())
        } else {
            element.callJsFunction("fitAll")
        }
    }

    // ===== Selection =====

    fun deselectAll() {
        element.callJsFunction("deselectAll")
    }

    // ===== Pinning =====

    fun pinNode(nodeId: String) {
        element.callJsFunction("pinNode", nodeId)
    }

    fun unpinNodes(nodeIds: List<String>) {
        element.callJsFunction("unpinNodes", nodeIds.toJsonStringArray())
    }

    // ===== Node positions =====

    fun setNodePositions(nodes: List<NvlNode>, updateLayout: Boolean = false) {
        element.callJsFunction(
            "setNodePositions",
            nodes.map { it.toJson() }.toJsonArray(),
            updateLayout,
        )
    }

    // ===== Node dragging =====

    fun setNodeDraggingEnabled(enabled: Boolean) {
        element.setProperty("nodeDraggingEnabled", enabled)
    }

    // ===== Renderer =====

    fun setRenderer(renderer: NvlRenderer) {
        element.callJsFunction("setRenderer", renderer.jsValue)
    }

    // ===== Restart =====

    fun restart(options: NvlOptions? = null, retainPositions: Boolean = false) {
        if (options != null) {
            element.callJsFunction("restart", options.toJson(), retainPositions)
        } else {
            element.callJsFunction("restart", Json.createObject(), retainPositions)
        }
    }

    // ===== Export =====

    fun saveToFile(filename: String? = null, backgroundColor: String? = null) {
        element.callJsFunction("saveToFile", optionsJsonOf(filename, backgroundColor))
    }

    fun saveToSvg(filename: String? = null, backgroundColor: String? = null) {
        element.callJsFunction("saveToSvg", optionsJsonOf(filename, backgroundColor))
    }

    fun saveFullGraphToLargeFile(filename: String? = null, backgroundColor: String? = null) {
        element.callJsFunction("saveFullGraphToLargeFile", optionsJsonOf(filename, backgroundColor))
    }

    // ===== Client→Server getters (async) =====

    fun getScale(callback: SerializableCallback<Double>) {
        element.callJsFunction("getScale").then(Double::class.java) { callback.accept(it) }
    }

    fun getPan(callback: SerializableCallback<NvlPoint>) {
        element.callJsFunction("getPan").then(JsonValue::class.java) { json ->
            val obj = json as JsonObject
            callback.accept(NvlPoint(obj.getNumber("x"), obj.getNumber("y")))
        }
    }

    fun getZoomLimits(callback: SerializableCallback<NvlZoomLimits>) {
        element.callJsFunction("getZoomLimits").then(JsonValue::class.java) { json ->
            val obj = json as JsonObject
            callback.accept(NvlZoomLimits(obj.getNumber("minZoom"), obj.getNumber("maxZoom")))
        }
    }

    fun getNodes(callback: SerializableCallback<List<NvlNode>>) {
        element.callJsFunction("getNodes").then(JsonValue::class.java) { json ->
            callback.accept(parseNodeArray(json as JsonArray))
        }
    }

    fun getRelationships(callback: SerializableCallback<List<NvlRelationship>>) {
        element.callJsFunction("getRelationships").then(JsonValue::class.java) { json ->
            callback.accept(parseRelationshipArray(json as JsonArray))
        }
    }

    fun getSelectedNodes(callback: SerializableCallback<List<NvlNode>>) {
        element.callJsFunction("getSelectedNodes").then(JsonValue::class.java) { json ->
            callback.accept(parseNodeArray(json as JsonArray))
        }
    }

    fun getSelectedRelationships(callback: SerializableCallback<List<NvlRelationship>>) {
        element.callJsFunction("getSelectedRelationships").then(JsonValue::class.java) { json ->
            callback.accept(parseRelationshipArray(json as JsonArray))
        }
    }

    fun getNodePositions(callback: SerializableCallback<List<NvlNode>>) {
        element.callJsFunction("getNodePositions").then(JsonValue::class.java) { json ->
            callback.accept(parseNodeArray(json as JsonArray))
        }
    }

    fun isLayoutMoving(callback: SerializableCallback<Boolean>) {
        element.callJsFunction("isLayoutMoving").then(Boolean::class.java) { callback.accept(it) }
    }

    fun getImageDataUrl(callback: SerializableCallback<String>, backgroundColor: String? = null) {
        val opts = Json.createObject().apply {
            backgroundColor?.let { put("backgroundColor", it) }
        }
        element.callJsFunction("getImageDataUrl", opts).then(String::class.java) { callback.accept(it) }
    }

    // ===== Events =====

    fun addClickListener(listener: ComponentEventListener<NvlClickEvent>): Registration =
        addListener(NvlClickEvent::class.java, listener)

    fun addDoubleClickListener(listener: ComponentEventListener<NvlDoubleClickEvent>): Registration =
        addListener(NvlDoubleClickEvent::class.java, listener)

    fun addContextMenuListener(listener: ComponentEventListener<NvlContextMenuEvent>): Registration =
        addListener(NvlContextMenuEvent::class.java, listener)

    fun addLayoutDoneListener(listener: ComponentEventListener<NvlLayoutDoneEvent>): Registration =
        addListener(NvlLayoutDoneEvent::class.java, listener)

    fun addLayoutComputingListener(listener: ComponentEventListener<NvlLayoutComputingEvent>): Registration =
        addListener(NvlLayoutComputingEvent::class.java, listener)

    fun addZoomTransitionDoneListener(listener: ComponentEventListener<NvlZoomTransitionDoneEvent>): Registration =
        addListener(NvlZoomTransitionDoneEvent::class.java, listener)

    fun addInitializationListener(listener: ComponentEventListener<NvlInitializationEvent>): Registration =
        addListener(NvlInitializationEvent::class.java, listener)

    fun addNodeDragEndListener(listener: ComponentEventListener<NvlNodeDragEndEvent>): Registration =
        addListener(NvlNodeDragEndEvent::class.java, listener)

    // ===== Event classes =====

    @DomEvent("nvl-click")
    class NvlClickEvent(
        source: NvlGraph,
        fromClient: Boolean,
        @EventData("event.detail.nodeIds") nodeIds: JsonArray?,
        @EventData("event.detail.relationshipIds") relationshipIds: JsonArray?,
    ) : NvlHitEvent(source, fromClient, nodeIds, relationshipIds)

    @DomEvent("nvl-dblclick")
    class NvlDoubleClickEvent(
        source: NvlGraph,
        fromClient: Boolean,
        @EventData("event.detail.nodeIds") nodeIds: JsonArray?,
        @EventData("event.detail.relationshipIds") relationshipIds: JsonArray?,
    ) : NvlHitEvent(source, fromClient, nodeIds, relationshipIds)

    @DomEvent("nvl-contextmenu")
    class NvlContextMenuEvent(
        source: NvlGraph,
        fromClient: Boolean,
        @EventData("event.detail.nodeIds") nodeIds: JsonArray?,
        @EventData("event.detail.relationshipIds") relationshipIds: JsonArray?,
    ) : NvlHitEvent(source, fromClient, nodeIds, relationshipIds)

    abstract class NvlHitEvent(
        source: NvlGraph,
        fromClient: Boolean,
        nodeIds: JsonArray?,
        relationshipIds: JsonArray?,
    ) : ComponentEvent<NvlGraph>(source, fromClient) {
        val nodeIds: List<String> = nodeIds?.toStringList() ?: emptyList()
        val relationshipIds: List<String> = relationshipIds?.toStringList() ?: emptyList()
    }

    @DomEvent("nvl-layout-done")
    class NvlLayoutDoneEvent(
        source: NvlGraph,
        fromClient: Boolean,
    ) : ComponentEvent<NvlGraph>(source, fromClient)

    @DomEvent("nvl-layout-computing")
    class NvlLayoutComputingEvent(
        source: NvlGraph,
        fromClient: Boolean,
        @EventData("event.detail.isComputing") val isComputing: Boolean,
    ) : ComponentEvent<NvlGraph>(source, fromClient)

    @DomEvent("nvl-zoom-transition-done")
    class NvlZoomTransitionDoneEvent(
        source: NvlGraph,
        fromClient: Boolean,
    ) : ComponentEvent<NvlGraph>(source, fromClient)

    @DomEvent("nvl-initialization")
    class NvlInitializationEvent(
        source: NvlGraph,
        fromClient: Boolean,
    ) : ComponentEvent<NvlGraph>(source, fromClient)

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

fun interface SerializableCallback<T> : Serializable {
    fun accept(value: T)
}

data class NvlPoint(val x: Double, val y: Double)

data class NvlZoomLimits(val minZoom: Double, val maxZoom: Double)
