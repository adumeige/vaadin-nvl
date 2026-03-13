import { html, LitElement } from "lit";
import { customElement, property } from "lit/decorators.js";
import NVL from "@neo4j-nvl/base";
import type {
  Node,
  Relationship,
  NvlOptions,
  Layout,
  LayoutOptions,
  ZoomOptions,
} from "@neo4j-nvl/base";
import {
  PanInteraction,
  ZoomInteraction,
  DragNodeInteraction,
  ClickInteraction,
} from "@neo4j-nvl/interaction-handlers";

@customElement("nvl-graph")
export class NvlGraph extends LitElement {
  private nvlInstance: NVL | null = null;
  private container: HTMLDivElement | null = null;

  // Interaction handlers
  private panInteraction: PanInteraction | null = null;
  private zoomInteraction: ZoomInteraction | null = null;
  private dragNodeInteraction: DragNodeInteraction | null = null;
  private clickInteraction: ClickInteraction | null = null;

  // Popup state
  private popupOverlay: HTMLDivElement | null = null;
  private popupTargetId: string | null = null;
  private popupTargetIsRelationship = false;
  private popupTrackingRaf: number | null = null;

  @property({ type: Array })
  nodes: Node[] = [];

  @property({ type: Array })
  relationships: Relationship[] = [];

  @property({ type: Object })
  options: NvlOptions = {};

  @property({ type: Boolean })
  nodeDraggingEnabled = false;

  // Disable shadow DOM so NVL gets normal DOM event handling
  createRenderRoot() {
    return this;
  }

  render() {
    return html`
      <style>
        nvl-graph {
          display: block;
          width: 100%;
          height: 100%;
          position: relative;
        }
        .nvl-container {
          width: 100%;
          height: 100%;
          min-height: 400px;
        }
        .nvl-popup-overlay {
          position: absolute;
          z-index: 100;
          pointer-events: auto;
          display: none;
        }
      </style>
      <div class="nvl-container"></div>
    `;
  }

  firstUpdated() {
    this.container = this.querySelector(".nvl-container") as HTMLDivElement;
    // Create popup overlay imperatively (outside Lit's template) so Lit
    // re-renders never wipe its children.
    this.popupOverlay = document.createElement("div");
    this.popupOverlay.classList.add("nvl-popup-overlay");
    this.appendChild(this.popupOverlay);
    this.initNvl();
  }

  updated(changedProperties: Map<string, unknown>) {
    if (
      changedProperties.has("nodes") ||
      changedProperties.has("relationships")
    ) {
      if (this.nvlInstance) {
        this.nvlInstance.addAndUpdateElementsInGraph(
          this.nodes,
          this.relationships
        );
      } else {
        this.initNvl();
      }
    }
    if (changedProperties.has("nodeDraggingEnabled")) {
      this.updateDragInteraction();
    }
  }

  private initNvl() {
    if (!this.container) return;
    this.destroyNvl();

    const opts: NvlOptions = {
      initialZoom: 1,
      renderer: "canvas",
      ...this.options,
    };

    this.nvlInstance = new NVL(
      this.container,
      this.nodes,
      this.relationships,
      opts,
      {
        onLayoutDone: () => this.fireSimpleEvent("nvl-layout-done"),
        onLayoutComputing: (isComputing: boolean) =>
          this.dispatchEvent(
            new CustomEvent("nvl-layout-computing", {
              detail: { isComputing },
              bubbles: true,
              composed: true,
            })
          ),
        onZoomTransitionDone: () =>
          this.fireSimpleEvent("nvl-zoom-transition-done"),
        onInitialization: () => this.fireSimpleEvent("nvl-initialization"),
      }
    );

    // Set up interaction handlers
    this.panInteraction = new PanInteraction(this.nvlInstance);

    this.zoomInteraction = new ZoomInteraction(this.nvlInstance);

    this.clickInteraction = new ClickInteraction(this.nvlInstance);
    this.clickInteraction.updateCallback("onNodeClick", (node, hitElements, event) => {
      this.fireHitEvent("nvl-click", hitElements);
    });
    this.clickInteraction.updateCallback("onRelationshipClick", (rel, hitElements, event) => {
      this.fireHitEvent("nvl-click", hitElements);
    });
    this.clickInteraction.updateCallback("onCanvasClick", (_event) => {
      this.dispatchEvent(
        new CustomEvent("nvl-click", {
          detail: { nodeIds: [], relationshipIds: [] },
          bubbles: true,
          composed: true,
        })
      );
    });
    this.clickInteraction.updateCallback("onNodeDoubleClick", (node, hitElements, event) => {
      this.fireHitEvent("nvl-dblclick", hitElements);
    });
    this.clickInteraction.updateCallback("onRelationshipDoubleClick", (rel, hitElements, event) => {
      this.fireHitEvent("nvl-dblclick", hitElements);
    });
    this.clickInteraction.updateCallback("onCanvasDoubleClick", (_event) => {
      this.dispatchEvent(
        new CustomEvent("nvl-dblclick", {
          detail: { nodeIds: [], relationshipIds: [] },
          bubbles: true,
          composed: true,
        })
      );
    });
    this.clickInteraction.updateCallback("onNodeRightClick", (node, hitElements, event) => {
      this.fireHitEvent("nvl-contextmenu", hitElements);
    });
    this.clickInteraction.updateCallback("onRelationshipRightClick", (rel, hitElements, event) => {
      this.fireHitEvent("nvl-contextmenu", hitElements);
    });
    this.clickInteraction.updateCallback("onCanvasRightClick", (_event) => {
      this.dispatchEvent(
        new CustomEvent("nvl-contextmenu", {
          detail: { nodeIds: [], relationshipIds: [] },
          bubbles: true,
          composed: true,
        })
      );
    });

    this.updateDragInteraction();
  }

  private updateDragInteraction() {
    if (!this.nvlInstance) return;

    if (this.nodeDraggingEnabled && !this.dragNodeInteraction) {
      this.dragNodeInteraction = new DragNodeInteraction(this.nvlInstance);
      this.dragNodeInteraction.updateCallback("onDragEnd", (nodes: Node[], _evt: MouseEvent) => {
        for (const node of nodes) {
          const pos = this.nvlInstance!.getPositionById(node.id);
          this.dispatchEvent(
            new CustomEvent("nvl-node-drag-end", {
              detail: {
                nodeId: node.id,
                x: pos?.x ?? 0,
                y: pos?.y ?? 0,
              },
              bubbles: true,
              composed: true,
            })
          );
        }
      });
      // Don't let pan fight with node drag
      this.panInteraction?.updateTargets(["node", "relationship"], false);
    } else if (!this.nodeDraggingEnabled && this.dragNodeInteraction) {
      this.dragNodeInteraction.destroy();
      this.dragNodeInteraction = null;
      this.panInteraction?.updateTargets([], true);
    }
  }

  // ===== Event helpers =====

  private fireHitEvent(
    eventName: string,
    hitTargets: { nodes: Array<{ data: { id: string } }>; relationships: Array<{ data: { id: string } }> }
  ) {
    this.dispatchEvent(
      new CustomEvent(eventName, {
        detail: {
          nodeIds: hitTargets.nodes.map((n) => n.data.id),
          relationshipIds: hitTargets.relationships.map((r) => r.data.id),
        },
        bubbles: true,
        composed: true,
      })
    );
  }

  private fireSimpleEvent(name: string) {
    this.dispatchEvent(
      new CustomEvent(name, { bubbles: true, composed: true })
    );
  }

  // ===== Graph data methods =====

  addAndUpdateElements(nodes: Node[], relationships: Relationship[]) {
    this.nvlInstance?.addAndUpdateElementsInGraph(nodes, relationships);
  }

  updateElements(nodes: Node[], relationships: Relationship[]) {
    this.nvlInstance?.updateElementsInGraph(nodes, relationships);
  }

  addElements(nodes: Node[], relationships: Relationship[]) {
    this.nvlInstance?.addElementsToGraph(nodes, relationships);
  }

  removeNodes(nodeIds: string[]) {
    this.nvlInstance?.removeNodesWithIds(nodeIds);
  }

  removeRelationships(relationshipIds: string[]) {
    this.nvlInstance?.removeRelationshipsWithIds(relationshipIds);
  }

  // ===== Layout methods =====

  setLayout(layout: string) {
    this.nvlInstance?.setLayout(layout as Layout);
  }

  setLayoutOptions(options: LayoutOptions) {
    this.nvlInstance?.setLayoutOptions(options);
  }

  // ===== Zoom & pan methods =====

  setZoom(zoomValue: number) {
    this.nvlInstance?.setZoom(zoomValue);
  }

  setPan(panX: number, panY: number) {
    this.nvlInstance?.setPan(panX, panY);
  }

  setZoomAndPan(zoom: number, panX: number, panY: number) {
    this.nvlInstance?.setZoomAndPan(zoom, panX, panY);
  }

  resetZoom() {
    this.nvlInstance?.resetZoom();
  }

  fit(nodeIds: string[], zoomOptions?: ZoomOptions) {
    this.nvlInstance?.fit(nodeIds, zoomOptions);
  }

  fitAll(zoomOptions?: ZoomOptions) {
    if (!this.nvlInstance) return;
    const nodes = this.nvlInstance.getNodes();
    this.nvlInstance.fit(
      nodes.map((n) => n.id),
      zoomOptions
    );
  }

  // ===== Selection methods =====

  deselectAll() {
    this.nvlInstance?.deselectAll();
  }

  // ===== Pinning methods =====

  pinNode(nodeId: string) {
    this.nvlInstance?.pinNode(nodeId);
  }

  unpinNodes(nodeIds: string[]) {
    this.nvlInstance?.unPinNode(nodeIds);
  }

  // ===== Node positions =====

  setNodePositions(data: Node[], updateLayout: boolean) {
    this.nvlInstance?.setNodePositions(data, updateLayout);
  }

  // ===== Renderer =====

  setRenderer(renderer: string) {
    this.nvlInstance?.setRenderer(renderer);
  }

  // ===== Restart =====

  restart(options: NvlOptions, retainPositions: boolean) {
    this.nvlInstance?.restart(options, retainPositions);
  }

  // ===== Export methods =====

  saveToFile(options: { filename?: string; backgroundColor?: string }) {
    this.nvlInstance?.saveToFile(options);
  }

  saveToSvg(options: { filename?: string; backgroundColor?: string }) {
    this.nvlInstance?.saveToSvg(options);
  }

  saveFullGraphToLargeFile(options: {
    filename?: string;
    backgroundColor?: string;
  }) {
    this.nvlInstance?.saveFullGraphToLargeFile(options);
  }

  // ===== Getter methods (return values for server round-trip) =====

  getScale(): number {
    return this.nvlInstance?.getScale() ?? 1;
  }

  getPan(): { x: number; y: number } {
    return this.nvlInstance?.getPan() ?? { x: 0, y: 0 };
  }

  getZoomLimits(): { minZoom: number; maxZoom: number } {
    return (
      this.nvlInstance?.getZoomLimits() ?? { minZoom: 0.075, maxZoom: 10 }
    );
  }

  getNodes(): Node[] {
    return this.nvlInstance?.getNodes() ?? [];
  }

  getRelationships(): Relationship[] {
    return this.nvlInstance?.getRelationships() ?? [];
  }

  getSelectedNodes(): Node[] {
    return this.nvlInstance?.getSelectedNodes() ?? [];
  }

  getSelectedRelationships(): Relationship[] {
    return this.nvlInstance?.getSelectedRelationships() ?? [];
  }

  getNodePositions(): Node[] {
    return this.nvlInstance?.getNodePositions() ?? [];
  }

  isLayoutMoving(): boolean {
    return this.nvlInstance?.isLayoutMoving() ?? false;
  }

  getImageDataUrl(options: { backgroundColor?: string }): string {
    return this.nvlInstance?.getImageDataUrl(options) ?? "";
  }

  // ===== Popup methods =====

  showPopup(elementId: string, isRelationship: boolean) {
    this.popupTargetId = elementId;
    this.popupTargetIsRelationship = isRelationship;
    if (this.popupOverlay) {
      // Move all server-appended popup content (direct children that aren't
      // the container, overlay, or style elements) into the overlay div.
      const contentChildren = Array.from(this.children).filter(
        (child) =>
          child !== this.popupOverlay &&
          child !== this.container &&
          child.tagName !== "STYLE"
      );
      for (const child of contentChildren) {
        this.popupOverlay.appendChild(child);
      }
      this.popupOverlay.style.display = "block";
    }
    this.updatePopupPosition();
    this.startPopupTracking();
  }

  hidePopup() {
    this.stopPopupTracking();
    this.popupTargetId = null;
    if (this.popupOverlay) {
      this.popupOverlay.style.display = "none";
      // Move children back out of the overlay so Vaadin can remove them
      while (this.popupOverlay.firstChild) {
        this.appendChild(this.popupOverlay.firstChild);
      }
    }
  }

  private getWorldPosition(
    elementId: string,
    isRelationship: boolean
  ): { x: number; y: number } | null {
    if (!this.nvlInstance) return null;

    if (isRelationship) {
      const rels = this.nvlInstance.getRelationships();
      const rel = rels.find((r) => r.id === elementId);
      if (!rel) return null;
      const fromPos = this.nvlInstance.getPositionById(rel.from);
      const toPos = this.nvlInstance.getPositionById(rel.to);
      if (!fromPos || !toPos) return null;
      return {
        x: ((fromPos.x ?? 0) + (toPos.x ?? 0)) / 2,
        y: ((fromPos.y ?? 0) + (toPos.y ?? 0)) / 2,
      };
    } else {
      const pos = this.nvlInstance.getPositionById(elementId);
      if (!pos) return null;
      return { x: pos.x ?? 0, y: pos.y ?? 0 };
    }
  }

  /**
   * Converts an element's world position to CSS-pixel coordinates relative to
   * the `<nvl-graph>` host element.
   *
   * NVL's `getPan()` returns the camera/viewport offset in world space, so it
   * must be subtracted: screenPos = center + (worldPos - pan) * scale
   */
  private getElementScreenPosition(
    elementId: string,
    isRelationship: boolean
  ): { x: number; y: number } | null {
    if (!this.nvlInstance || !this.container) return null;

    const world = this.getWorldPosition(elementId, isRelationship);
    if (!world) return null;

    const scale = this.nvlInstance.getScale();
    const pan = this.nvlInstance.getPan();
    const thisRect = this.getBoundingClientRect();

    // Canvas renderer
    const canvas = this.container.querySelector("canvas");
    if (canvas) {
      const canvasRect = canvas.getBoundingClientRect();
      return {
        x:
          canvasRect.left -
          thisRect.left +
          canvasRect.width / 2 +
          (world.x - pan.x) * scale,
        y:
          canvasRect.top -
          thisRect.top +
          canvasRect.height / 2 +
          (world.y - pan.y) * scale,
      };
    }

    // SVG renderer
    const svg = this.container.querySelector("svg");
    if (svg) {
      const svgRect = svg.getBoundingClientRect();
      return {
        x:
          svgRect.left -
          thisRect.left +
          svgRect.width / 2 +
          (world.x - pan.x) * scale,
        y:
          svgRect.top -
          thisRect.top +
          svgRect.height / 2 +
          (world.y - pan.y) * scale,
      };
    }

    return null;
  }

  private updatePopupPosition() {
    if (!this.popupOverlay || !this.popupTargetId) return;

    const pos = this.getElementScreenPosition(
      this.popupTargetId,
      this.popupTargetIsRelationship
    );
    if (!pos) return;

    // Position the popup so it's centered horizontally above the element
    this.popupOverlay.style.left = `${pos.x}px`;
    this.popupOverlay.style.top = `${pos.y}px`;
    this.popupOverlay.style.transform = "translate(-50%, -100%)";
  }

  private startPopupTracking() {
    this.stopPopupTracking();
    const track = () => {
      this.updatePopupPosition();
      this.popupTrackingRaf = requestAnimationFrame(track);
    };
    this.popupTrackingRaf = requestAnimationFrame(track);
  }

  private stopPopupTracking() {
    if (this.popupTrackingRaf !== null) {
      cancelAnimationFrame(this.popupTrackingRaf);
      this.popupTrackingRaf = null;
    }
  }

  // ===== Lifecycle =====

  private destroyNvl() {
    this.stopPopupTracking();
    this.clickInteraction?.destroy();
    this.clickInteraction = null;
    this.dragNodeInteraction?.destroy();
    this.dragNodeInteraction = null;
    this.panInteraction?.destroy();
    this.panInteraction = null;
    this.zoomInteraction?.destroy();
    this.zoomInteraction = null;
    if (this.nvlInstance) {
      this.nvlInstance.destroy();
      this.nvlInstance = null;
    }
  }

  disconnectedCallback() {
    super.disconnectedCallback();
    this.destroyNvl();
  }
}
