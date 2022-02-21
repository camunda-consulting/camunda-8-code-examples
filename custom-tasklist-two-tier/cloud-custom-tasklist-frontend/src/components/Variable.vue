<script lang="ts" setup>
const variable = defineProps(["variable"]);

</script>

<template>
  <td class="variable-name">
    <h4>{{ variable.variableName }}</h4>
  </td>
  <td class="variable-value">
    <textarea
      type="text"
      v-model="variable.value"
    />
  </td>
</template>
<style>
.variable-name {
  max-width: 100px;
}

textarea {
  width: 95%;
  height: 100%;
}
</style>
