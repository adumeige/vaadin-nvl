package org.antoined.vaadin.nvl.demo

import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.html.H2
import com.vaadin.flow.component.html.Paragraph
import com.vaadin.flow.component.html.Span
import com.vaadin.flow.component.notification.Notification
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.component.textfield.NumberField
import com.vaadin.flow.router.Route
import org.antoined.vaadin.nvl.NvlGraph
import org.antoined.vaadin.nvl.NvlZoomOptions

@Route("zoom-pan", layout = MainLayout::class)
class ZoomPanView : VerticalLayout() {
    init {
        setSizeFull()

        add(H2("Zoom & Pan"))
        add(Paragraph("Control zoom level and pan position programmatically. Drag to pan, scroll to zoom interactively."))

        val statusLabel = Span("Zoom: -- | Pan: --, --")

        val graph = NvlGraph().apply {
            setSizeFull()
            setGraph(SampleData.movieNodes(), SampleData.movieRelationships())

            addZoomTransitionDoneListener {
                getScale { scale ->
                    getPan { pan ->
                        statusLabel.text = "Zoom: %.2f | Pan: %.0f, %.0f".format(scale, pan.x, pan.y)
                    }
                }
            }
        }

        val zoomField = NumberField("Zoom level").apply {
            value = 1.0; step = 0.1; min = 0.1; max = 10.0; isStepButtonsVisible = true
        }
        val panXField = NumberField("Pan X").apply { value = 0.0; step = 50.0; isStepButtonsVisible = true }
        val panYField = NumberField("Pan Y").apply { value = 0.0; step = 50.0; isStepButtonsVisible = true }

        val setZoomBtn = Button("Set Zoom") { graph.setZoom(zoomField.value) }
        val setPanBtn = Button("Set Pan") { graph.setPan(panXField.value, panYField.value) }
        val setZoomAndPanBtn = Button("Set Both") {
            graph.setZoomAndPan(zoomField.value, panXField.value, panYField.value)
        }
        val resetZoomBtn = Button("Reset Zoom") { graph.resetZoom() }
        val fitAllBtn = Button("Fit All") { graph.fitAll() }
        val fitAnimatedBtn = Button("Fit All (animated)") {
            graph.fitAll(NvlZoomOptions(animated = true))
        }
        val fitMoviesBtn = Button("Fit Movies Only") {
            graph.fit(listOf("matrix", "matrix2", "matrix3"))
        }
        val getScaleBtn = Button("Get Scale") {
            graph.getScale { Notification.show("Current scale: %.3f".format(it)) }
        }
        val getPanBtn = Button("Get Pan") {
            graph.getPan { Notification.show("Current pan: x=%.1f, y=%.1f".format(it.x, it.y)) }
        }
        val getLimitsBtn = Button("Get Zoom Limits") {
            graph.getZoomLimits { Notification.show("Min: ${it.minZoom}, Max: ${it.maxZoom}") }
        }

        val row1 = HorizontalLayout(zoomField, panXField, panYField, setZoomBtn, setPanBtn, setZoomAndPanBtn).apply {
            defaultVerticalComponentAlignment = com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment.END
            isSpacing = true
        }
        val row2 = HorizontalLayout(resetZoomBtn, fitAllBtn, fitAnimatedBtn, fitMoviesBtn, getScaleBtn, getPanBtn, getLimitsBtn).apply {
            isSpacing = true
            style.set("flex-wrap", "wrap")
        }

        add(row1, row2, statusLabel)
        addAndExpand(graph)
    }
}
