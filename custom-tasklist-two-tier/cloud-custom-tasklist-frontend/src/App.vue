<script lang="ts" setup>
import Tasklist from "./components/Tasklist.vue";
import Task from "./components/Task.vue";
import { ref } from "vue";
import { useQuery,  useResult, useMutation } from '@vue/apollo-composable'
import gql from 'graphql-tag'

const selectedTask = ref();

const tasksQuery = useQuery(gql`
  query ($query: TaskQuery!){
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
  query:{
    state: "CREATED"
  }
},
{
  pollInterval: 10000
});
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
  tasksQuery.refetch()});

const completeTask = completeTaskMutation.mutate;
</script>

<template>
  <div class="header">
    <h1>Tasklist</h1>
  </div>
  <div class="tasklist">
    <div class="list"><Tasklist @selectedTask="(task) => selectedTask = task" :tasklist="tasklist"></Tasklist></div>
    <div class="content">
      <Task v-if="selectedTask" :taskId="selectedTask!.id" @completeTask="completeTask"></Task>
    </div>
  </div>
</template>

<style>
html,
body {
  font-family: Arial, Helvetica, sans-serif;
  height: 100%;
}

#app {
  height: 100%;
}
div {
  border-color: black;
  border-width: 0.1em;
  border-style: solid;
}
.tasklist {
  margin-top: 10px;
  display: grid;
  grid-template-columns: 1fr 5fr;
  border-style: solid;
  height: 100%;
}
.list {
  min-width: 300px;
}

.content {
  width: 100%;
}
</style>
