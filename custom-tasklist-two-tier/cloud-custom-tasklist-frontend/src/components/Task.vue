<script lang="ts" setup>
import Variable from "./Variable.vue";
import { useQuery, useMutation, useResult } from "@vue/apollo-composable";
import gql from "graphql-tag";
import { ref, reactive, computed } from "vue";

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
  reactive({ id: computed(() => props.taskId) })
);

const task = useResult(taskQuery.result);

const variables = ref([] as Array<any>);
// needs to be a function
const faulty = ref(false);

const addFaulty = (varName: any) => {
  const index = variables.value.indexOf(varName);
  if (index == -1) {
    variables.value.push(varName);
    faulty.value = true;
  }
};
const removeFaulty = (varName: any) => {
  const index = variables.value.indexOf(varName);
  if (index != -1) {
    variables.value.splice(index, 1);
    console.log(variables.value.length);
    if (variables.value.length == 0) {
      console.log("Setting faulty to false");
      faulty.value = false;
      console.log(faulty.value);
    }
  }
};
</script>

<template>
  <div v-if="task">
    <h3>{{ task.name }}</h3>
    <table>
    <colgroup><col class="variable-column"><col class="value-column"></colgroup>
      <tr v-for="variable in task.variables">
        <Variable
          :variable="variable"
          @faulty-variable="addFaulty"
          @correct-variable="removeFaulty"
        ></Variable>
      </tr>
    </table>
    <button
      class="complete"
      :disabled="faulty"
      @click="$emit('completeTask', { taskId: task.id, variables: task.variables })"
    >
      Complete
    </button>
    <div class="error-message" v-if="variables.length > 0">
      Please check and correct these variables: {{ variables.join(", ") }}
    </div>
  </div>
</template>

<style>
h3 {
  padding: 10px 0px;
  width: auto;
}
table {
  width: 100%;
}
tr,td {
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

textarea {
  background-color: gray;
  color: white;
}

.variable-column{
  width: 15%;
}

.value-column {
  width: 85%;
}
</style>
