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
import com.vaadin.flow.router.Route
import org.antoined.vaadin.nvl.NvlGraph
import org.antoined.vaadin.nvl.NvlNode

@Route("interaction", layout = MainLayout::class)
class InteractionView : VerticalLayout() {
    init {
        setSizeFull()

        add(H2("Interaction"))
        add(Paragraph(
            "Drag the background to pan, scroll to zoom, drag nodes to reposition them. " +
            "Dragged nodes are pinned so the layout does not move them back."
        ))

        val statusLabel = Span("Drag a node or pan the canvas")

        val graph = NvlGraph().apply {
            setSizeFull()
            setNodeDraggingEnabled(true)
            setGraph(SampleData.movieNodes(), SampleData.movieRelationships())

            addNodeDragEndListener { event ->
                statusLabel.text = "Dragged ${event.nodeId} to (%.0f, %.0f)".format(event.x, event.y)
            }

            addClickListener { event ->
                if (event.nodeIds.isNotEmpty()) {
                    statusLabel.text = "Clicked: ${event.nodeIds.joinToString()}"
                }
            }
        }

        val dragToggle = Checkbox("Node dragging enabled").apply {
            value = true
            addValueChangeListener { event ->
                graph.setNodeDraggingEnabled(event.value)
                Notification.show(if (event.value) "Node dragging ON" else "Node dragging OFF")
            }
        }

        val unpinAllBtn = Button("Unpin All Nodes") {
            graph.getNodes { nodes ->
                graph.unpinNodes(nodes.map { it.id })
                Notification.show("Unpinned ${nodes.size} nodes")
            }
        }

        val resetBtn = Button("Reset Graph") {
            graph.setGraph(SampleData.movieNodes(), SampleData.movieRelationships())
            graph.getNodes { nodes -> graph.unpinNodes(nodes.map { it.id }) }
            Notification.show("Graph reset")
        }

        val fitBtn = Button("Fit All") {
            graph.fitAll()
        }

        val arrangeCircleBtn = Button("Arrange in Circle") {
            graph.getNodes { nodes ->
                val n = nodes.size
                val radius = 200.0
                val positioned = nodes.mapIndexed { i, node ->
                    val angle = 2 * Math.PI * i / n
                    NvlNode(
                        id = node.id,
                        x = radius * Math.cos(angle),
                        y = radius * Math.sin(angle),
                    )
                }
                graph.setNodePositions(positioned)
                positioned.forEach { graph.pinNode(it.id) }
                Notification.show("Arranged ${n} nodes in circle")
            }
        }

        val toolbar = HorizontalLayout(dragToggle, unpinAllBtn, resetBtn, fitBtn, arrangeCircleBtn).apply {
            defaultVerticalComponentAlignment = FlexComponent.Alignment.CENTER
            isSpacing = true
            style.set("flex-wrap", "wrap")
        }

        add(toolbar, statusLabel)
        addAndExpand(graph)
    }
}
