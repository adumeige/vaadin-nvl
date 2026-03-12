package org.antoined.vaadin.nvl.demo

import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.html.H2
import com.vaadin.flow.component.html.Paragraph
import com.vaadin.flow.component.notification.Notification
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.router.Route
import org.antoined.vaadin.nvl.NvlGraph
import org.antoined.vaadin.nvl.NvlNode
import org.antoined.vaadin.nvl.NvlRelationship

@Route("dynamic", layout = MainLayout::class)
class DynamicGraphView : VerticalLayout() {
    private var nodeCounter = 0
    private var relCounter = 0
    private val colors = listOf("#4C8BF5", "#EA4335", "#34A853", "#FBBC05", "#9C27B0", "#FF5722")

    init {
        setSizeFull()

        add(H2("Dynamic Graph Manipulation"))
        add(Paragraph("Add, update, and remove nodes and relationships at runtime."))

        val graph = NvlGraph().apply {
            setSizeFull()
            // Start with a small seed graph
            setGraph(
                listOf(
                    NvlNode(id = "n0", caption = "Start", color = "#4C8BF5", size = 30),
                ),
                emptyList(),
            )
        }

        val nodeNameField = TextField("Node label").apply { value = "New Node" }

        val addNodeBtn = Button("Add Node") {
            nodeCounter++
            val newId = "n$nodeCounter"
            graph.addAndUpdateElements(
                nodes = listOf(
                    NvlNode(
                        id = newId,
                        caption = nodeNameField.value.ifBlank { "Node $nodeCounter" },
                        color = colors[nodeCounter % colors.size],
                        size = (20..40).random(),
                    )
                ),
            )
            // Connect to a random existing node
            if (nodeCounter > 0) {
                relCounter++
                val targetIdx = (0 until nodeCounter).random()
                graph.addAndUpdateElements(
                    relationships = listOf(
                        NvlRelationship(
                            id = "r$relCounter",
                            from = newId,
                            to = "n$targetIdx",
                            caption = "LINK",
                        )
                    ),
                )
            }
            Notification.show("Added node $newId")
        }

        val removeLastBtn = Button("Remove Last Node") {
            if (nodeCounter > 0) {
                graph.removeNodes(listOf("n$nodeCounter"))
                Notification.show("Removed node n$nodeCounter")
                nodeCounter--
            }
        }

        val updateColorBtn = Button("Randomize Colors") {
            val updates = (0..nodeCounter).map { i ->
                NvlNode(id = "n$i", color = colors.random())
            }
            graph.updateElements(nodes = updates)
            Notification.show("Updated ${updates.size} node colors")
        }

        val replaceAllBtn = Button("Replace with Movie Graph") {
            graph.setGraph(SampleData.movieNodes(), SampleData.movieRelationships())
            Notification.show("Replaced graph with movie data")
        }

        val getNodesBtn = Button("Get Nodes (server round-trip)") {
            graph.getNodes { nodes ->
                Notification.show("Server received ${nodes.size} nodes: ${nodes.map { it.id }.joinToString()}")
            }
        }

        val toolbar = HorizontalLayout(nodeNameField, addNodeBtn, removeLastBtn, updateColorBtn, replaceAllBtn, getNodesBtn).apply {
            defaultVerticalComponentAlignment = com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment.END
            isSpacing = true
            style.set("flex-wrap", "wrap")
        }

        add(toolbar)
        addAndExpand(graph)
    }
}
