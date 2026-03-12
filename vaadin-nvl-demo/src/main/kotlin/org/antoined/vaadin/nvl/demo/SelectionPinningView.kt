package org.antoined.vaadin.nvl.demo

import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.html.H2
import com.vaadin.flow.component.html.Paragraph
import com.vaadin.flow.component.html.Span
import com.vaadin.flow.component.notification.Notification
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.router.Route
import org.antoined.vaadin.nvl.NvlGraph
import org.antoined.vaadin.nvl.NvlNode

@Route("selection-pinning", layout = MainLayout::class)
class SelectionPinningView : VerticalLayout() {
    init {
        setSizeFull()

        add(H2("Selection & Pinning"))
        add(Paragraph("Click a node to select it, then use the buttons below. Pin nodes to fix their position during layout."))

        val statusLabel = Span("Click a node to see its id here")

        val graph = NvlGraph().apply {
            setSizeFull()
            setGraph(SampleData.movieNodes(), SampleData.movieRelationships())

            addClickListener { event ->
                if (event.nodeIds.isNotEmpty()) {
                    statusLabel.text = "Last clicked: ${event.nodeIds.joinToString()}"
                    // Select the clicked nodes
                    updateElements(
                        nodes = event.nodeIds.map { NvlNode(id = it, selected = true) },
                    )
                }
            }
        }

        val nodeIdField = TextField("Node ID").apply { value = "keanu" }

        val selectBtn = Button("Select Node") {
            graph.updateElements(nodes = listOf(NvlNode(id = nodeIdField.value, selected = true)))
            Notification.show("Selected: ${nodeIdField.value}")
        }

        val deselectBtn = Button("Deselect All") {
            graph.deselectAll()
            Notification.show("All deselected")
        }

        val pinBtn = Button("Pin Node") {
            graph.pinNode(nodeIdField.value)
            Notification.show("Pinned: ${nodeIdField.value}")
        }

        val unpinBtn = Button("Unpin Node") {
            graph.unpinNodes(listOf(nodeIdField.value))
            Notification.show("Unpinned: ${nodeIdField.value}")
        }

        val getSelectedBtn = Button("Get Selected Nodes") {
            graph.getSelectedNodes { nodes ->
                if (nodes.isEmpty()) {
                    Notification.show("No nodes selected")
                } else {
                    Notification.show("Selected: ${nodes.map { it.id }.joinToString()}")
                }
            }
        }

        val getSelectedRelsBtn = Button("Get Selected Relationships") {
            graph.getSelectedRelationships { rels ->
                if (rels.isEmpty()) {
                    Notification.show("No relationships selected")
                } else {
                    Notification.show("Selected: ${rels.map { it.id }.joinToString()}")
                }
            }
        }

        val getPositionsBtn = Button("Get Node Positions") {
            graph.getNodePositions { nodes ->
                val summary = nodes.take(3).joinToString { "${it.id}(${it.x?.toInt()},${it.y?.toInt()})" }
                Notification.show("Positions (first 3): $summary")
            }
        }

        val setPositionBtn = Button("Move Node to (0,0)") {
            graph.setNodePositions(
                listOf(NvlNode(id = nodeIdField.value, x = 0.0, y = 0.0)),
                updateLayout = false,
            )
            graph.pinNode(nodeIdField.value)
            Notification.show("Moved ${nodeIdField.value} to center and pinned")
        }

        val row1 = HorizontalLayout(nodeIdField, selectBtn, deselectBtn, pinBtn, unpinBtn).apply {
            defaultVerticalComponentAlignment = com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment.END
            isSpacing = true
        }
        val row2 = HorizontalLayout(getSelectedBtn, getSelectedRelsBtn, getPositionsBtn, setPositionBtn).apply {
            isSpacing = true
            style.set("flex-wrap", "wrap")
        }

        add(row1, row2, statusLabel)
        addAndExpand(graph)
    }
}
