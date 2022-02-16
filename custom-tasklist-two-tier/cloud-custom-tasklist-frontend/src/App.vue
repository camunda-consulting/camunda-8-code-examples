<script lang="ts" setup>
import { ref, onMounted, onBeforeUnmount, type Ref } from "vue";
import Variable from "./components/Variable.vue";

const tasklist: Ref<any[]> = ref([]);
let interval: number;

const fetchData = async () => {
  fetch("/api/task").then((response) => {
    response.json().then((body) => {
      tasklist.value = body;
      console.log(tasklist.value);
    });
  });
};

const completeTask = async (taskId: string, variables: any) => {
  fetch(`/api/task/${taskId}/complete`, {
    method: "POST",
    body: JSON.stringify(variables),
    headers: { "Content-Type": "application/json" },
  }).then(() => fetchData());
};

const handleChange = async (changeEvent:any) => {
  console.log(changeEvent);
  const oldValue = tasklist.value.find((task) => task.id == changeEvent.taskId).variables[changeEvent.variableName];
  console.log(`Old value: ${oldValue}`);
  tasklist.value.find((task) => task.id == changeEvent.taskId).variables[changeEvent.variableName] = changeEvent.variableValue;
  const newValue = tasklist.value.find((task) => task.id == changeEvent.taskId).variables[changeEvent.variableName];
  console.log(`New value: ${newValue}`);
}

onMounted(() => {
  fetchData();
  interval = setInterval(fetchData, 10000);
});

onBeforeUnmount(() => clearInterval(interval));
</script>

<template>
  <div class="header">
    <h1>Tasklist</h1>
  </div>
  <div class="content">
    <div v-for="task in tasklist">
      {{ task }}
      <h3>{{ task.taskName }}</h3>
      <table>
        <tr v-for="(variableValue, variableName) in task.variables">
          <Variable
            :variableName="variableName"
            :variableValue="variableValue"
            :taskId="task.id"
            @variable-value-change="handleChange"
          ></Variable>
        </tr>
      </table>
      <button @click="completeTask(task.id, task.variables)">Complete</button>
    </div>
  </div>
</template>

<style>
@import "https://unpkg.com/todomvc-app-css@2.4.1/index.css";
</style>
