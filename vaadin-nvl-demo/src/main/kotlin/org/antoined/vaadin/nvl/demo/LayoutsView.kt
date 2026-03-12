package org.antoined.vaadin.nvl.demo

import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.html.H2
import com.vaadin.flow.component.html.Paragraph
import com.vaadin.flow.component.notification.Notification
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.component.select.Select
import com.vaadin.flow.router.Route
import org.antoined.vaadin.nvl.NvlGraph
import org.antoined.vaadin.nvl.NvlHierarchicalOptions
import org.antoined.vaadin.nvl.NvlLayout

@Route("layouts", layout = MainLayout::class)
class LayoutsView : VerticalLayout() {
    init {
        setSizeFull()

        add(H2("Layout Algorithms"))
        add(Paragraph("Switch between all available layout algorithms. The graph re-arranges automatically."))

        val graph = NvlGraph().apply {
            setSizeFull()
            setGraph(SampleData.movieNodes(), SampleData.movieRelationships())
        }

        val layoutSelect = Select<NvlLayout>().apply {
            label = "Layout"
            setItems(*NvlLayout.entries.toTypedArray())
            setItemLabelGenerator { it.name.replace('_', ' ').lowercase().replaceFirstChar { c -> c.uppercase() } }
            value = NvlLayout.FORCE_DIRECTED
            addValueChangeListener { event ->
                graph.setLayout(event.value)
                Notification.show("Layout: ${event.value.jsValue}")
            }
        }

        val directionSelect = Select<NvlHierarchicalOptions.Direction>().apply {
            label = "Hierarchical direction"
            setItems(*NvlHierarchicalOptions.Direction.entries.toTypedArray())
            value = NvlHierarchicalOptions.Direction.DOWN
            addValueChangeListener { event ->
                graph.setLayout(NvlLayout.HIERARCHICAL)
                graph.setLayoutOptions(NvlHierarchicalOptions(direction = event.value))
                layoutSelect.value = NvlLayout.HIERARCHICAL
                Notification.show("Hierarchical: ${event.value.jsValue}")
            }
        }

        val restartBtn = Button("Restart Layout") {
            graph.restart(retainPositions = false)
            Notification.show("Layout restarted")
        }

        val toolbar = HorizontalLayout(layoutSelect, directionSelect, restartBtn).apply {
            defaultVerticalComponentAlignment = com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment.END
            isSpacing = true
        }

        add(toolbar)
        addAndExpand(graph)
    }
}
