<script lang="ts" setup>
import Variable from "./Variable.vue";
import { useQuery, useMutation, useResult } from "@vue/apollo-composable";
import gql from "graphql-tag";

const props = defineProps(["taskId"]);
defineEmits(['completeTask'])

const taskQuery = useQuery(gql`query ($id: String!){
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
}`,{
  id: props.taskId
})

const task = useResult(taskQuery.result)

</script>

<template>
<div v-if="task">
  <h3>{{ task.name }}</h3>
  <table>
    <tr v-for="variable in task.variables">
      <Variable
        :variable="variable"
      ></Variable>
    </tr>
  </table>
  <button @click="$emit('completeTask',{taskId: task.id, variables: task.variables})">Complete</button>
</div>
</template>

<style>
table {
  border: 1px solid;
  width: 100%;
}
tr {
  height: 100px;
}
</style>
