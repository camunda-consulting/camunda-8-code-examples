<script lang="ts" setup>
import GenericForm from "./GenericForm.vue";
import { useQuery, useMutation, useResult } from "@vue/apollo-composable";
import gql from "graphql-tag";
import { ref, reactive, computed, watchEffect } from "vue";
import CheckApplication from "./forms/CheckApplication.vue";
import BpmnViewer from "bpmn-js";

const props = defineProps(["taskId"]);
defineEmits(["completeTask"]);

const taskQuery = useQuery(
  gql`
    query($id: String!) {
      task(id: $id) {
        id
        name
        taskDefinitionId
        processName
        creationTime
        completionTime
        assignee
        variables {
          id
          name
          value
          previewValue
          isValueTruncated
        }
        taskState
        sortValues
        isFirst
        formKey
        processDefinitionId
        candidateGroups
      }
    }
  `,
  reactive({ id: computed(() => props.taskId) }),
  { fetchPolicy: "no-cache" }
);

const form = ref(null);
const canvas = ref(null);

const task = ref(useResult(taskQuery.result));

const bpmnXml = ref();

const formKyMapping = {
  checkApplication: CheckApplication,
};

const faulty = ref(false);
const message = ref("");

const setError = (msg: any) => {
  if (msg) {
    faulty.value = true;
    message.value = msg;
  } else {
    faulty.value = false;
    message.value = "";
  }
};

watchEffect(async () => {
  if (task.value && canvas.value) {
    if (!bpmnXml.value) {
      bpmnXml.value = await fetch(
        `/api/insurance-application/id/${JSON.parse(
          task.value.variables.find((variable: any) => variable.name === "applicationId")
            .value
        )}/xml`
      ).then((response) => response.text());
    } else {
      console.log("Rendering BPMN");
      const viewer = new BpmnViewer({ container: canvas.value });
      await viewer.importXML(bpmnXml.value);
      viewer.get("canvas").zoom("fit-viewport");
      const bpmnTask = viewer.get("elementRegistry").get(task.value.taskDefinitionId);
      const overlay = document.createElement("div");
      overlay.className = "active-bpmn-task";
      overlay.setAttribute("style", `width: ${bpmnTask.width}px; height: ${bpmnTask.height}px`);
      viewer.get("overlays").add(task.value.taskDefinitionId, {
        position: {
          top: -4,
          left: -4,
        },
        html: overlay,
      });
      window.addEventListener("resize", () => viewer.get("canvas").zoom("fit-viewport"));
    }
  }
});

const taskMenu = ["Form", "Diagram", "Raw data"];
const activeTask = ref("Form");
</script>

<template>
  <div v-if="task">
    <h3 class="task-header">{{ task.name }}</h3>
    <div class="task-menu">
      <button v-for="taskMenuPoint in taskMenu" @click="activeTask = taskMenuPoint" :class="{
        active: activeTask === taskMenuPoint,
      }">
        {{ taskMenuPoint }}
      </button>
    </div>
    <div class="task-content">
      <div class="form-container" v-if="activeTask === 'Form'">
        <component :is="formKyMapping[task.formKey] || GenericForm" :task="task" @errorMessage="setError"
          :key="'' + task.id + task.formKey" ref="form"></component>
        <div class="button-wrapper">
          <button class="complete" :disabled="faulty"
            @click="$emit('completeTask', { taskId: task.id, variables: form.variables })">
            Complete
          </button>
        </div>

        <div class="error-message" v-if="faulty">
          {{ message }}
        </div>
      </div>
      <div class="diagram-container" v-if="activeTask === 'Diagram'">
        <div ref="canvas" class="diagram-viewer"></div>
      </div>
      <div class="rawdata-container" v-if="activeTask === 'Raw data'">
        <pre>{{ JSON.stringify(task, undefined, 2) }}</pre>
      </div>
    </div>
  </div>
</template>

<style>
h3 {
  padding: 10px 0px;
  width: auto;
}

.task-header {
  height: 3%;
}

.task-menu {
  display: grid;
  grid-template-columns: 1fr 1fr 1fr;
}

.task-menu * {
  margin: 5px;
}

.task-content {
  height: 80%;
}

table {
  width: 100%;
}

tr,
td {
  height: 50px;
  padding: 10px;
}

.error-message {
  color: red;
}

.complete {
  float: right;
  width: auto;
}

.complete:disabled {
  background-color: var(--e-global-color-9154836);
  border-color: var(--e-global-color-9154836);
  cursor: default;
}

textarea {
  background-color: gray;
  color: white;
}

.variable-column {
  width: 15%;
}

.value-column {
  width: 85%;
}

.button-wrapper {
  height: 50px;
}

.diagram-container {
  height: 100%;
}

.diagram-viewer {
  border-radius: 30px;
  width: 100%;
  height: 100%;
  background-color: white;
}

.active-bpmn-task {
  background-color: var(--primary);
  border-radius: 10px;
  border: 4px solid var(--e-global-color-af24b6d);
  opacity: 0.6;
  pointer-events: none;
  padding: 0;
}
</style>
