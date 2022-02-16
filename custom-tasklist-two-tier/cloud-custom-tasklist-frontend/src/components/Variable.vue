<script lang="ts" setup>
const variable = defineProps(["variableName", "variableValue", "taskId"]);
defineEmits(['variableValueChange']);

const jsonValue = JSON.stringify(variable.variableValue,undefined,2);
</script>

<template>
  <td>
    <h4>{{ variable.variableName }}</h4>
  </td>
  <td>
    <textarea
      type="text"
      v-model="jsonValue"
      @change="
        $emit('variableValueChange', {
          taskId: variable.taskId,
          variableName: variable.variableName,
          variableValue: JSON.parse(jsonValue),
        })
      "
    />
  </td>
</template>
