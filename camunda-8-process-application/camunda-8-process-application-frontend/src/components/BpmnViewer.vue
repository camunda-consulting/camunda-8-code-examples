<script setup>
import gql from "graphql-tag";
import { ref, reactive, computed, watchEffect, watch } from "vue";
import { useQuery, useResult } from "@vue/apollo-composable";
import BpmnViewer from "bpmn-js"

const props = defineProps(["processDefinitionId", "taskDefinitionId"]);
console.log("Props: ", props);
const query = useQuery(
    gql`
    query($processDefinitionId: ID!) {
      bpmnXml(processDefinitionId: $processDefinitionId) {
        id
        data
      }
    }
  `,
    { processDefinitionId: props.processDefinitionId }
);
const bpmnXml = useResult(query.result);

const canvas = ref(null);

watchEffect(async () => {
    if (bpmnXml.value) {
        console.log("Rendering diagram");
        const viewer = new BpmnViewer({ container: canvas.value });
        await viewer.importXML(bpmnXml.value.data);
        console.log("Diagram imported");
        viewer.get("canvas").zoom("fit-viewport");
        const bpmnTask = viewer.get("elementRegistry").get(props.taskDefinitionId);
        console.log(bpmnTask);
        const overlay = document.createElement("div");
        console.log(overlay);
        overlay.className = "active-bpmn-task";
        overlay.setAttribute(
            "style",
            `width: ${bpmnTask.width}px; height: ${bpmnTask.height}px`
        );
        viewer.get("overlays").add(props.taskDefinitionId, {
            position: {
                top: -4,
                left: -4,
            },
            html: overlay,
        });
        window.addEventListener("resize", () =>
            viewer.get("canvas").zoom("fit-viewport")
        );
    }
});
</script>

<template>
    <div ref="canvas" class="diagram-viewer"></div>
</template>

<style>
.diagram-viewer {
    border-radius: 30px;
    width: 100%;
    height: 100%;
    background-color: white;
}

.active-bpmn-task {
    background-color: var(--primary);
    border-radius: 15px;
    border: 4px solid var(--e-global-color-af24b6d);
    opacity: 0.6;
    pointer-events: none;
    padding: 0;
}
</style>
