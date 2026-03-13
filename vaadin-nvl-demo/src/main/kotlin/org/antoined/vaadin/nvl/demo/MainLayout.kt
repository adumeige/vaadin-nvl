package org.antoined.vaadin.nvl.demo

import com.vaadin.flow.component.applayout.AppLayout
import com.vaadin.flow.component.applayout.DrawerToggle
import com.vaadin.flow.component.html.H1
import com.vaadin.flow.component.html.Span
import com.vaadin.flow.component.orderedlayout.FlexComponent
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.sidenav.SideNav
import com.vaadin.flow.component.sidenav.SideNavItem
import com.vaadin.flow.router.Layout

/** Application shell layout with a side navigation drawer listing all demo views. */
@Layout
class MainLayout : AppLayout() {
    init {
        val toggle = DrawerToggle()
        val title = H1("NVL Component Demo").apply {
            style.set("font-size", "var(--lumo-font-size-l)")
            style.set("margin", "0")
        }
        val header = HorizontalLayout(toggle, title).apply {
            defaultVerticalComponentAlignment = FlexComponent.Alignment.CENTER
            setWidthFull()
            addClassNames("py-0", "px-m")
        }
        addToNavbar(header)

        val nav = SideNav().apply {
            addItem(SideNavItem("Basic Graph", BasicGraphView::class.java))
            addItem(SideNavItem("Dynamic Graph", DynamicGraphView::class.java))
            addItem(SideNavItem("Layouts", LayoutsView::class.java))
            addItem(SideNavItem("Zoom & Pan", ZoomPanView::class.java))
            addItem(SideNavItem("Selection & Pinning", SelectionPinningView::class.java))
            addItem(SideNavItem("Styling", StylingView::class.java))
            addItem(SideNavItem("Export", ExportView::class.java))
            addItem(SideNavItem("Interaction", InteractionView::class.java))
            addItem(SideNavItem("Events", EventsView::class.java))
        }
        addToDrawer(nav)
    }
}
