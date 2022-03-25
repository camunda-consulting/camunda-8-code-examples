<script lang="ts" setup>import { ref } from 'vue';

const emits = defineEmits(['faultyVariable','correctVariable']);

const props = defineProps(["variable"]);

const value = ref(props.variable.value)

const changed = ref(false)

const faulty = ref(false)

const validateJSON =async (event:Event) => {
  if(props.variable.value === value.value){
    changed.value = false;
  }else{
    changed.value = true;
  }
  try {
    JSON.parse(value.value);
    faulty.value = false;
    emits('correctVariable',props.variable.name);
  } catch (error) {
    faulty.value = true;
    emits('faultyVariable',props.variable.name);
  }
}

</script>

<template>
  <td class="variable-name">
    <h4>{{ variable.name }}</h4>
  </td>
  <td class="variable-value">
    <textarea type="text" :id ="variable.id"
    v-model="value" 
    @input="validateJSON"
    :class="{faulty: faulty,changed: changed}"/>
  </td>
</template>
<style scoped>
.variable-name {
  max-width: 10%;
  text-align: right;
}

textarea {
  width: 90%;
  height: 90%;
  font-family: var(--font-family-monospace);
  padding: 10px;
  border-radius: 30px;
}

.faulty {
  color: var(--danger);
}

.changed {
  background-color: var(--yellow);
}
</style>
