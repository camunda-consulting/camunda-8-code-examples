<script lang="ts" setup>
import Tasklist from "./components/Tasklist.vue";
import Task from "./components/Task.vue";
import { ref } from "vue";
import { useQuery, useResult, useMutation } from "@vue/apollo-composable";
import gql from "graphql-tag";

const selectedTask = ref();

const tasksQuery = useQuery(
  gql`
    query($query: TaskQuery!) {
      tasks(query: $query) {
        id
        name
        taskDefinitionId
        processName
        creationTime
        completionTime
        assignee
        taskState
        sortValues
        isFirst
        formKey
        processDefinitionId
        candidateGroups
        __typename
      }
    }
  `,
  {
    query: {
      state: "CREATED",
    },
  },
  {
    pollInterval: 60000,
  }
);
const tasklist = useResult(tasksQuery.result);

const completeTaskMutation = useMutation(gql`
  mutation($taskId: String!, $variables: [VariableInput!]!) {
    completeTask(taskId: $taskId, variables: $variables) {
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
        __typename
      }
      taskState
      sortValues
      isFirst
      formKey
      processDefinitionId
      candidateGroups
      __typename
    }
  }
`);

completeTaskMutation.onDone(() => {
  selectedTask.value = undefined;
  tasksQuery.refetch();
});

const completeTask = completeTaskMutation.mutate;
</script>

<template>
  <div class="header">
    <h1>Tasklist</h1>
  </div>
  <div class="tasklist">
    <div class="list">
      <Tasklist
        @selectedTask="(task) => (selectedTask = task)"
        :tasklist="tasklist"
      ></Tasklist>
    </div>
    <Task
      v-if="selectedTask"
      :taskId="selectedTask!.id"
      @completeTask="completeTask"
      class="content"
    ></Task>
  </div>
</template>

<style>
* {
  font-family: var(--e-global-typography-primary-font-family);
}

html,
body,
h1 {
  height: 100%;
  margin: 0px;
  border: 0px;
  padding: 0px;
  color: white;
  background-color: black;
  border-color: var(--e-global-color-af24b6d);
  border-radius: 30px;
}

h1,
h2,
h3,
h4,
h5,
h6 {
  padding: 20px;
  font-weight: var(--e-global-typography-primary-font-weight);
}

#app {
  height: 100%;
}

.header {
  text-align: center;
  height: 10%;
}
.tasklist {
  display: grid;
  grid-template-columns: 1fr 4fr;
  height: 90%;
}
.list {
  min-width: 300px;
  overflow-y: auto;
}

.content {
  width: auto;
  padding: 10px;
  margin-left: 10px;
  overflow-y: auto;
}

/* width */
::-webkit-scrollbar {
  width: 10px;
}

/* Track */
::-webkit-scrollbar-track {
  background: transparent;
  height: 80%;
}

/* Handle */
::-webkit-scrollbar-thumb {
  background: var(--e-global-color-af24b6d);
  border-radius: 5px;
}

/* Handle on hover */
::-webkit-scrollbar-thumb:hover {
  background: var(--primary);
}

::-webkit-scrollbar-track-piece:end {
  background: transparent;
  margin-bottom: 50px;
}

::-webkit-scrollbar-track-piece:start {
  background: transparent;
  margin-top: 50px;
}

.list,
.content {
  border-radius: 30px;
  border-color: var(--primary);
  border-style: dotted;
  border-width: 3px;
}

button {
  cursor: pointer;
  padding: 10px;
  border: 3px solid var(--e-global-color-af24b6d);
  background-color: black;
  color: white;
  border-radius: 30px;
  font-weight: var(--e-global-typography-primary-font-weight);
}

button:hover {
  background-color: var(--e-global-color-af24b6d);
}
</style>
