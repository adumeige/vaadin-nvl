package org.antoined.vaadin.nvl.demo

import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.html.H2
import com.vaadin.flow.component.html.Image
import com.vaadin.flow.component.html.Paragraph
import com.vaadin.flow.component.notification.Notification
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.router.Route
import org.antoined.vaadin.nvl.NvlGraph

/**
 * Demo view for exporting the graph as PNG, SVG, or a Base64 data URL.
 *
 * Showcases [NvlGraph.saveToFile], [NvlGraph.saveToSvg], [NvlGraph.saveFullGraphToLargeFile],
 * and [NvlGraph.getImageDataUrl].
 */
@Route("export", layout = MainLayout::class)
class ExportView : VerticalLayout() {
    init {
        setSizeFull()

        add(H2("Export"))
        add(Paragraph("Export the graph as PNG, SVG, or retrieve the image data URL for server-side use."))

        val preview = Image().apply {
            maxWidth = "400px"
            maxHeight = "300px"
            style.set("border", "1px solid var(--lumo-contrast-20pct)")
            isVisible = false
        }

        val graph = NvlGraph().apply {
            setSizeFull()
            setGraph(SampleData.movieNodes(), SampleData.movieRelationships())
        }

        val filenameField = TextField("Filename").apply { value = "graph" }
        val bgColorField = TextField("Background color").apply { value = "#ffffff" }

        val savePngBtn = Button("Save PNG (client download)") {
            graph.saveToFile(filename = filenameField.value, backgroundColor = bgColorField.value)
        }

        val saveSvgBtn = Button("Save SVG (client download)") {
            graph.saveToSvg(filename = filenameField.value, backgroundColor = bgColorField.value)
        }

        val saveLargeBtn = Button("Save Full Graph PNG") {
            graph.saveFullGraphToLargeFile(filename = filenameField.value, backgroundColor = bgColorField.value)
        }

        val getDataUrlBtn = Button("Get Data URL (show preview)") {
            graph.getImageDataUrl({ dataUrl ->
                preview.src = dataUrl
                preview.isVisible = true
                Notification.show("Received data URL (${dataUrl.length} chars)")
            }, backgroundColor = bgColorField.value)
        }

        val toolbar = HorizontalLayout(filenameField, bgColorField, savePngBtn, saveSvgBtn, saveLargeBtn, getDataUrlBtn).apply {
            defaultVerticalComponentAlignment = com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment.END
            isSpacing = true
            style.set("flex-wrap", "wrap")
        }

        add(toolbar, preview)
        addAndExpand(graph)
    }
}
