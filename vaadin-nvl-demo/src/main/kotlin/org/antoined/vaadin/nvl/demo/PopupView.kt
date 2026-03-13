package org.antoined.vaadin.nvl.demo

import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.html.H2
import com.vaadin.flow.component.html.Paragraph
import com.vaadin.flow.component.html.Span
import com.vaadin.flow.component.notification.Notification
import com.vaadin.flow.component.orderedlayout.FlexComponent
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.router.Route
import org.antoined.vaadin.nvl.NvlGraph
import org.antoined.vaadin.nvl.NvlNode
import org.antoined.vaadin.nvl.NvlOptions
import org.antoined.vaadin.nvl.NvlRenderer

/**
 * Demo view showcasing the popup feature.
 *
 * Click a node to show an editable popup anchored to it. The popup tracks the node's
 * position as you pan and zoom. Double-click a relationship to show a popup at its midpoint.
 */
@Route("popup", layout = MainLayout::class)
class PopupView : VerticalLayout() {
    init {
        setSizeFull()

        add(H2("Popup on Node / Relationship"))
        add(Paragraph("Click a node to show an edit popup. Double-click a relationship to show its details. The popup follows the element during pan & zoom."))

        val graph = NvlGraph(NvlOptions(renderer = NvlRenderer.CANVAS)).apply {
            setSizeFull()
            setNodeDraggingEnabled(true)
            setGraph(SampleData.movieNodes(), SampleData.movieRelationships())
        }

        val statusLabel = Span("Click a node or double-click a relationship")

        graph.addClickListener { event ->
            if (event.nodeIds.isNotEmpty()) {
                val nodeId = event.nodeIds.first()
                statusLabel.text = "Showing popup for node: $nodeId"

                // Fetch current node data, then build the popup form
                graph.getNodes { nodes ->
                    val node = nodes.find { it.id == nodeId } ?: return@getNodes

                    val popupContent = VerticalLayout().apply {
                        isPadding = true
                        isSpacing = true
                        style.set("background", "var(--lumo-base-color)")
                        style.set("border-radius", "var(--lumo-border-radius-m)")
                        style.set("box-shadow", "var(--lumo-box-shadow-m)")
                        style.set("padding", "var(--lumo-space-m)")
                        style.set("min-width", "200px")

                        val captionField = TextField("Caption").apply {
                            value = node.caption ?: ""
                        }
                        val colorField = TextField("Color").apply {
                            value = node.color ?: ""
                            placeholder = "#hex"
                        }

                        val applyBtn = Button("Apply") {
                            graph.updateElements(
                                nodes = listOf(
                                    NvlNode(
                                        id = nodeId,
                                        caption = captionField.value.ifBlank { null },
                                        color = colorField.value.ifBlank { null },
                                    )
                                )
                            )
                            Notification.show("Updated $nodeId")
                        }

                        val closeBtn = Button("Close") {
                            graph.hidePopup()
                            statusLabel.text = "Popup closed"
                        }

                        val buttonRow = HorizontalLayout(applyBtn, closeBtn).apply {
                            isSpacing = true
                        }

                        add(Span("Edit: ${node.caption ?: nodeId}"), captionField, colorField, buttonRow)
                    }

                    graph.showPopupOnNode(nodeId, popupContent)
                }
            } else {
                // Clicked on empty canvas — close popup
                if (graph.isPopupVisible) {
                    graph.hidePopup()
                    statusLabel.text = "Popup closed"
                }
            }
        }

        graph.addDoubleClickListener { event ->
            if (event.relationshipIds.isNotEmpty()) {
                val relId = event.relationshipIds.first()
                statusLabel.text = "Showing popup for relationship: $relId"

                graph.getRelationships { rels ->
                    val rel = rels.find { it.id == relId } ?: return@getRelationships

                    val popupContent = VerticalLayout().apply {
                        isPadding = true
                        isSpacing = true
                        style.set("background", "var(--lumo-base-color)")
                        style.set("border-radius", "var(--lumo-border-radius-m)")
                        style.set("box-shadow", "var(--lumo-box-shadow-m)")
                        style.set("padding", "var(--lumo-space-m)")

                        val closeBtn = Button("Close") {
                            graph.hidePopup()
                            statusLabel.text = "Popup closed"
                        }

                        add(
                            Span("Relationship: ${rel.caption ?: relId}"),
                            Span("${rel.from} → ${rel.to}"),
                            closeBtn,
                        )
                    }

                    graph.showPopupOnRelationship(relId, popupContent)
                }
            }
        }

        val dismissBtn = Button("Close Popup") {
            graph.hidePopup()
            statusLabel.text = "Popup closed"
        }

        val toolbar = HorizontalLayout(dismissBtn, statusLabel).apply {
            defaultVerticalComponentAlignment = FlexComponent.Alignment.CENTER
            isSpacing = true
        }

        add(toolbar)
        addAndExpand(graph)
    }
}
