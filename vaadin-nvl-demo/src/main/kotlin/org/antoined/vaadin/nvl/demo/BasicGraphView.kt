package org.antoined.vaadin.nvl.demo

import com.vaadin.flow.component.html.H2
import com.vaadin.flow.component.html.Paragraph
import com.vaadin.flow.component.notification.Notification
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.router.Route
import org.antoined.vaadin.nvl.NvlGraph
import org.antoined.vaadin.nvl.NvlOptions
import org.antoined.vaadin.nvl.NvlRenderer

@Route("", layout = MainLayout::class)
class BasicGraphView : VerticalLayout() {
    init {
        setSizeFull()

        add(H2("Basic Graph"))
        add(Paragraph("A movie graph with click and double-click events. Try clicking on nodes and relationships."))

        val graph = NvlGraph(NvlOptions(renderer = NvlRenderer.CANVAS)).apply {
            setSizeFull()
            setGraph(SampleData.movieNodes(), SampleData.movieRelationships())

            addClickListener { event ->
                when {
                    event.nodeIds.isNotEmpty() ->
                        Notification.show("Clicked nodes: ${event.nodeIds.joinToString()}")
                    event.relationshipIds.isNotEmpty() ->
                        Notification.show("Clicked relationships: ${event.relationshipIds.joinToString()}")
                    else ->
                        Notification.show("Clicked on empty canvas")
                }
            }

            addDoubleClickListener { event ->
                if (event.nodeIds.isNotEmpty()) {
                    Notification.show("Double-clicked: ${event.nodeIds.joinToString()}")
                }
            }

            addContextMenuListener { event ->
                if (event.nodeIds.isNotEmpty()) {
                    Notification.show("Right-clicked: ${event.nodeIds.joinToString()}")
                }
            }
        }

        addAndExpand(graph)
    }
}
