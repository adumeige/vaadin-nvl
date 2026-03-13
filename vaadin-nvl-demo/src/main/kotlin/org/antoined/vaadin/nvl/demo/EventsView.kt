package org.antoined.vaadin.nvl.demo

import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.html.H2
import com.vaadin.flow.component.html.Paragraph
import com.vaadin.flow.component.html.Span
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.router.Route
import org.antoined.vaadin.nvl.NvlGraph
import org.antoined.vaadin.nvl.NvlLayout
import java.time.LocalTime
import java.time.format.DateTimeFormatter

/**
 * Demo view that logs all NVL callback events in real-time.
 *
 * Demonstrates initialization, layout, zoom, click, double-click, and context menu events
 * with a scrollable log panel and trigger buttons.
 */
@Route("events", layout = MainLayout::class)
class EventsView : VerticalLayout() {
    private val logArea = VerticalLayout().apply {
        isPadding = false
        isSpacing = false
        style.set("font-family", "monospace")
        style.set("font-size", "var(--lumo-font-size-s)")
        style.set("max-height", "200px")
        style.set("overflow-y", "auto")
        style.set("border", "1px solid var(--lumo-contrast-20pct)")
        style.set("padding", "var(--lumo-space-xs)")
    }

    private fun log(message: String) {
        val time = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss.SSS"))
        val entry = Span("[$time] $message")
        logArea.addComponentAsFirst(entry)
        // Keep last 50 entries
        while (logArea.componentCount > 50) {
            logArea.remove(logArea.getComponentAt(logArea.componentCount - 1))
        }
    }

    init {
        setSizeFull()

        add(H2("Events"))
        add(Paragraph("All NVL callback events logged in real-time. Interact with the graph to see events fire."))

        val graph = NvlGraph().apply {
            setSizeFull()
            setGraph(SampleData.movieNodes(), SampleData.movieRelationships())

            addInitializationListener {
                log("onInitialization - NVL instance ready")
            }

            addLayoutDoneListener {
                log("onLayoutDone - layout finished")
            }

            addLayoutComputingListener { event ->
                log("onLayoutComputing - isComputing: ${event.isComputing}")
            }

            addZoomTransitionDoneListener {
                log("onZoomTransitionDone - zoom/fit animation complete")
            }

            addClickListener { event ->
                log("onClick - nodes: ${event.nodeIds}, rels: ${event.relationshipIds}")
            }

            addDoubleClickListener { event ->
                log("onDoubleClick - nodes: ${event.nodeIds}, rels: ${event.relationshipIds}")
            }

            addContextMenuListener { event ->
                log("onContextMenu - nodes: ${event.nodeIds}, rels: ${event.relationshipIds}")
            }
        }

        val triggerLayoutBtn = Button("Change Layout (triggers events)") {
            graph.setLayout(NvlLayout.entries.random())
        }

        val triggerFitBtn = Button("Fit All (triggers zoom event)") {
            graph.fitAll()
        }

        val isMovingBtn = Button("Is Layout Moving?") {
            graph.isLayoutMoving { log("isLayoutMoving: $it") }
        }

        val clearLogBtn = Button("Clear Log") {
            logArea.removeAll()
        }

        val toolbar = HorizontalLayout(triggerLayoutBtn, triggerFitBtn, isMovingBtn, clearLogBtn).apply {
            isSpacing = true
        }

        add(toolbar)
        logArea.height = "200px"
        add(logArea)
        addAndExpand(graph)
    }
}
