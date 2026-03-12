package org.antoined.vaadin.nvl.demo

import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.checkbox.Checkbox
import com.vaadin.flow.component.html.H2
import com.vaadin.flow.component.html.Paragraph
import com.vaadin.flow.component.html.Span
import com.vaadin.flow.component.notification.Notification
import com.vaadin.flow.component.orderedlayout.FlexComponent
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.component.select.Select
import com.vaadin.flow.component.textfield.NumberField
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.router.Route
import org.antoined.vaadin.nvl.*

@Route("styling", layout = MainLayout::class)
class StylingView : VerticalLayout() {

    private var selectedNodeId: String? = null
    private var selectedRelId: String? = null

    init {
        setSizeFull()

        add(H2("Styling & Renderer"))
        add(Paragraph("Click a node or relationship to select it, then edit its properties below."))

        val graph = NvlGraph(
            NvlOptions(
                renderer = NvlRenderer.CANVAS,
                styling = NvlStyling(
                    defaultNodeColor = "#6366F1",
                    defaultRelationshipColor = "#94A3B8",
                    selectedBorderColor = "#F59E0B",
                ),
            )
        ).apply {
            setSizeFull()
            setNodeDraggingEnabled(true)
            setGraph(
                listOf(
                    NvlNode(id = "1", caption = "Alice", color = "#4C8BF5", size = 30),
                    NvlNode(id = "2", caption = "Bob", color = "#EF4444", size = 30),
                    NvlNode(id = "3", caption = "Charlie", color = "#10B981", size = 30),
                    NvlNode(id = "4", caption = "Diana", color = "#8B5CF6", size = 30),
                    NvlNode(id = "5", caption = "Eve", color = "#F59E0B", size = 30),
                    NvlNode(id = "6", caption = "Frank", color = "#EC4899", size = 30),
                ),
                listOf(
                    NvlRelationship(id = "r1", from = "1", to = "2", caption = "KNOWS"),
                    NvlRelationship(id = "r2", from = "2", to = "3", caption = "KNOWS"),
                    NvlRelationship(id = "r3", from = "3", to = "4", caption = "KNOWS"),
                    NvlRelationship(id = "r4", from = "4", to = "5", caption = "KNOWS"),
                    NvlRelationship(id = "r5", from = "5", to = "6", caption = "KNOWS"),
                    NvlRelationship(id = "r6", from = "6", to = "1", caption = "KNOWS"),
                    NvlRelationship(id = "r7", from = "1", to = "4", caption = "WORKS_WITH"),
                    NvlRelationship(id = "r8", from = "2", to = "5", caption = "WORKS_WITH"),
                ),
            )
        }

        var populatingEditor = false

        // --- Node editor panel ---

        val nodeSelectionLabel = Span("Click a node to select it")

        val nodeCaptionField = TextField("Caption").apply { isEnabled = false }
        val nodeColorField = TextField("Color").apply { isEnabled = false; placeholder = "#hex" }
        val nodeSizeField = NumberField("Size").apply {
            isEnabled = false; min = 5.0; max = 100.0; step = 5.0; isStepButtonsVisible = true
        }
        val nodeCaptionSizeField = NumberField("Caption size (1-3)").apply {
            isEnabled = false; value = 1.0; min = 1.0; max = 3.0; step = 1.0; isStepButtonsVisible = true
        }
        val nodeCaptionAlignSelect = Select<String>().apply {
            label = "Caption align"
            setItems("top", "center", "bottom")
            isEnabled = false
        }
        val nodeDisabledCheck = Checkbox("Disabled").apply { isEnabled = false }
        val nodeActivatedCheck = Checkbox("Activated").apply { isEnabled = false }
        val nodePinnedCheck = Checkbox("Pinned").apply { isEnabled = false }

        val nodeEditorFields = listOf(nodeCaptionField, nodeColorField, nodeSizeField, nodeCaptionSizeField, nodeCaptionAlignSelect, nodeDisabledCheck, nodeActivatedCheck, nodePinnedCheck)

        fun enableNodeEditor(enable: Boolean) {
            nodeEditorFields.forEach { it.isEnabled = enable }
        }

        fun applyNodeUpdate() {
            val id = selectedNodeId ?: return
            graph.updateElements(
                nodes = listOf(
                    NvlNode(
                        id = id,
                        caption = nodeCaptionField.value.ifBlank { null },
                        color = nodeColorField.value.ifBlank { null },
                        size = nodeSizeField.value?.toInt(),
                        captionSize = nodeCaptionSizeField.value,
                        captionAlign = when (nodeCaptionAlignSelect.value) {
                            "top" -> NvlCaptionAlign.TOP
                            "bottom" -> NvlCaptionAlign.BOTTOM
                            "center" -> NvlCaptionAlign.CENTER
                            else -> null
                        },
                        disabled = nodeDisabledCheck.value,
                        activated = nodeActivatedCheck.value,
                        pinned = nodePinnedCheck.value,
                    )
                ),
            )
        }

        nodeCaptionField.addValueChangeListener { if (!populatingEditor && selectedNodeId != null) applyNodeUpdate() }
        nodeColorField.addValueChangeListener { if (!populatingEditor && selectedNodeId != null) applyNodeUpdate() }
        nodeSizeField.addValueChangeListener { if (!populatingEditor && selectedNodeId != null) applyNodeUpdate() }
        nodeCaptionSizeField.addValueChangeListener { if (!populatingEditor && selectedNodeId != null) applyNodeUpdate() }
        nodeCaptionAlignSelect.addValueChangeListener { if (!populatingEditor && selectedNodeId != null) applyNodeUpdate() }
        nodeDisabledCheck.addValueChangeListener { if (!populatingEditor && selectedNodeId != null) applyNodeUpdate() }
        nodeActivatedCheck.addValueChangeListener { if (!populatingEditor && selectedNodeId != null) applyNodeUpdate() }
        nodePinnedCheck.addValueChangeListener { if (!populatingEditor && selectedNodeId != null) applyNodeUpdate() }

        // --- Relationship editor panel ---

        val relSelectionLabel = Span("Click a relationship to select it")

        val relCaptionField = TextField("Caption").apply { isEnabled = false }
        val relColorField = TextField("Color").apply { isEnabled = false; placeholder = "#hex" }
        val relWidthField = NumberField("Width").apply {
            isEnabled = false; min = 0.5; max = 20.0; step = 0.5; isStepButtonsVisible = true
        }
        val relCaptionSizeField = NumberField("Caption size (1-3)").apply {
            isEnabled = false; value = 1.0; min = 1.0; max = 3.0; step = 1.0; isStepButtonsVisible = true
        }
        val relCaptionAlignSelect = Select<String>().apply {
            label = "Caption align"
            setItems("top", "center", "bottom")
            isEnabled = false
        }
        val relDisabledCheck = Checkbox("Disabled").apply { isEnabled = false }

        val relEditorFields = listOf(relCaptionField, relColorField, relWidthField, relCaptionSizeField, relCaptionAlignSelect, relDisabledCheck)

        // Store from/to so we can reconstruct for updateElements
        var relFrom = ""
        var relTo = ""

        fun enableRelEditor(enable: Boolean) {
            relEditorFields.forEach { it.isEnabled = enable }
        }

        fun applyRelUpdate() {
            val id = selectedRelId ?: return
            graph.updateElements(
                relationships = listOf(
                    NvlRelationship(
                        id = id,
                        from = relFrom,
                        to = relTo,
                        caption = relCaptionField.value.ifBlank { null },
                        color = relColorField.value.ifBlank { null },
                        width = relWidthField.value,
                        captionSize = relCaptionSizeField.value,
                        captionAlign = when (relCaptionAlignSelect.value) {
                            "top" -> NvlCaptionAlign.TOP
                            "bottom" -> NvlCaptionAlign.BOTTOM
                            "center" -> NvlCaptionAlign.CENTER
                            else -> null
                        },
                        disabled = relDisabledCheck.value,
                    )
                ),
            )
        }

        relCaptionField.addValueChangeListener { if (!populatingEditor && selectedRelId != null) applyRelUpdate() }
        relColorField.addValueChangeListener { if (!populatingEditor && selectedRelId != null) applyRelUpdate() }
        relWidthField.addValueChangeListener { if (!populatingEditor && selectedRelId != null) applyRelUpdate() }
        relCaptionSizeField.addValueChangeListener { if (!populatingEditor && selectedRelId != null) applyRelUpdate() }
        relCaptionAlignSelect.addValueChangeListener { if (!populatingEditor && selectedRelId != null) applyRelUpdate() }
        relDisabledCheck.addValueChangeListener { if (!populatingEditor && selectedRelId != null) applyRelUpdate() }

        // --- Click handler for both nodes and relationships ---

        graph.addClickListener { event ->
            if (event.nodeIds.isNotEmpty()) {
                val nodeId = event.nodeIds.first()
                selectedNodeId = nodeId
                selectedRelId = null

                graph.getNodes { nodes ->
                    val node = nodes.find { it.id == nodeId } ?: return@getNodes
                    populatingEditor = true
                    nodeSelectionLabel.text = "Editing node: ${node.id}"
                    nodeCaptionField.value = node.caption ?: ""
                    nodeColorField.value = node.color ?: ""
                    nodeSizeField.value = node.size?.toDouble() ?: 25.0
                    nodeCaptionSizeField.value = node.captionSize ?: 1.0
                    nodeCaptionAlignSelect.value = node.captionAlign?.jsValue
                    nodeDisabledCheck.value = node.disabled ?: false
                    nodeActivatedCheck.value = node.activated ?: false
                    nodePinnedCheck.value = node.pinned ?: false
                    populatingEditor = false
                    enableNodeEditor(true)
                }

                // Clear relationship selection
                relSelectionLabel.text = "Click a relationship to select it"
                enableRelEditor(false)

                graph.deselectAll()
                graph.updateElements(nodes = listOf(NvlNode(id = nodeId, selected = true)))

            } else if (event.relationshipIds.isNotEmpty()) {
                val relId = event.relationshipIds.first()
                selectedRelId = relId
                selectedNodeId = null

                graph.getRelationships { rels ->
                    val rel = rels.find { it.id == relId } ?: return@getRelationships
                    populatingEditor = true
                    relFrom = rel.from
                    relTo = rel.to
                    relSelectionLabel.text = "Editing relationship: ${rel.id} (${rel.from} → ${rel.to})"
                    relCaptionField.value = rel.caption ?: ""
                    relColorField.value = rel.color ?: ""
                    relWidthField.value = rel.width
                    relCaptionSizeField.value = rel.captionSize ?: 1.0
                    relCaptionAlignSelect.value = rel.captionAlign?.jsValue
                    relDisabledCheck.value = rel.disabled ?: false
                    populatingEditor = false
                    enableRelEditor(true)

                    // Select after we have from/to (required fields in NvlRelationship)
                    graph.deselectAll()
                    graph.updateElements(relationships = listOf(
                        NvlRelationship(id = relId, from = rel.from, to = rel.to, selected = true)
                    ))
                }

                // Clear node selection
                nodeSelectionLabel.text = "Click a node to select it"
                enableNodeEditor(false)

            } else {
                selectedNodeId = null
                selectedRelId = null
                nodeSelectionLabel.text = "Click a node to select it"
                relSelectionLabel.text = "Click a relationship to select it"
                enableNodeEditor(false)
                enableRelEditor(false)
                graph.deselectAll()
            }
        }

        // --- Global controls ---

        val rendererSelect = Select<NvlRenderer>().apply {
            label = "Renderer"
            setItems(*NvlRenderer.entries.toTypedArray())
            value = NvlRenderer.CANVAS
            addValueChangeListener { event ->
                graph.setRenderer(event.value)
                Notification.show("Renderer: ${event.value.jsValue} (captions only in canvas)")
            }
        }

        val themeSelect = Select<String>().apply {
            label = "Color theme"
            setItems("Vivid", "Warm", "Cool", "Mono")
            addValueChangeListener { event ->
                val palettes = mapOf(
                    "Vivid" to listOf("#4C8BF5", "#EF4444", "#10B981", "#8B5CF6", "#F59E0B", "#EC4899"),
                    "Warm" to listOf("#DC2626", "#EA580C", "#D97706", "#CA8A04", "#B91C1C", "#C2410C"),
                    "Cool" to listOf("#2563EB", "#7C3AED", "#0891B2", "#059669", "#1D4ED8", "#6D28D9"),
                    "Mono" to listOf("#374151", "#4B5563", "#6B7280", "#9CA3AF", "#1F2937", "#52525B"),
                )
                val colors = palettes[event.value] ?: return@addValueChangeListener
                graph.updateElements(
                    nodes = (1..6).mapIndexed { i, id -> NvlNode(id = "$id", color = colors[i]) }
                )
                Notification.show("Applied ${event.value} theme")
            }
        }

        val restartDarkBtn = Button("Dark styling") {
            graph.restart(
                NvlOptions(
                    styling = NvlStyling(
                        defaultNodeColor = "#1E293B",
                        defaultRelationshipColor = "#475569",
                        selectedBorderColor = "#FACC15",
                        dropShadowColor = "#FACC15",
                    ),
                ),
                retainPositions = true,
            )
        }

        val restartLightBtn = Button("Light styling") {
            graph.restart(
                NvlOptions(
                    styling = NvlStyling(
                        defaultNodeColor = "#6366F1",
                        defaultRelationshipColor = "#94A3B8",
                        selectedBorderColor = "#F59E0B",
                    ),
                ),
                retainPositions = true,
            )
        }

        // --- Layout ---

        val globalRow = HorizontalLayout(rendererSelect, themeSelect, restartDarkBtn, restartLightBtn).apply {
            defaultVerticalComponentAlignment = FlexComponent.Alignment.END
            isSpacing = true
            style.set("flex-wrap", "wrap")
        }

        val nodeEditorRow1 = HorizontalLayout(nodeCaptionField, nodeColorField, nodeSizeField, nodeCaptionSizeField, nodeCaptionAlignSelect).apply {
            defaultVerticalComponentAlignment = FlexComponent.Alignment.END
            isSpacing = true
            style.set("flex-wrap", "wrap")
        }
        val nodeEditorRow2 = HorizontalLayout(nodeDisabledCheck, nodeActivatedCheck, nodePinnedCheck).apply {
            defaultVerticalComponentAlignment = FlexComponent.Alignment.CENTER
            isSpacing = true
        }

        val relEditorRow1 = HorizontalLayout(relCaptionField, relColorField, relWidthField, relCaptionSizeField, relCaptionAlignSelect).apply {
            defaultVerticalComponentAlignment = FlexComponent.Alignment.END
            isSpacing = true
            style.set("flex-wrap", "wrap")
        }
        val relEditorRow2 = HorizontalLayout(relDisabledCheck).apply {
            defaultVerticalComponentAlignment = FlexComponent.Alignment.CENTER
            isSpacing = true
        }

        add(globalRow)
        add(nodeSelectionLabel, nodeEditorRow1, nodeEditorRow2)
        add(relSelectionLabel, relEditorRow1, relEditorRow2)
        addAndExpand(graph)
    }
}
