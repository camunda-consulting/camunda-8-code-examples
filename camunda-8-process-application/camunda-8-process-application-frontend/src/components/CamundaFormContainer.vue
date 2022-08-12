<script lang="ts" setup>
import { Form } from "@bpmn-io/form-js-viewer";
import { useQuery, useResult } from "@vue/apollo-composable";
import { ref, reactive, computed, watchEffect, watch } from "vue";
import gql from "graphql-tag";
import CamundaFormViewer from "./viewer/CamundaFormViewer.vue";

type Variable = {
  name: string;
  value: string;
};

const props = defineProps(["task"]);
const emits = defineEmits(["errorMessage"]);

const viewer = ref(null as any);

const formQuery = useQuery(
  gql`
    query form($id: String!, $processDefinitionId: String!) {
      form(id: $id, processDefinitionId: $processDefinitionId) {
        id
        processDefinitionId
        schema
      }
    }
  `,
  {
    id: props.task.formKey.split(":")[2],
    processDefinitionId: props.task.processDefinitionId,
  }
);

const schema = useResult(formQuery.result);

const variables = computed(() => {
  const raw = props.task.variables as Array<Variable>;
  return raw.reduce((map, obj) => ((map[obj.name] = JSON.parse(obj.value)), map), {});
});

const emitErrorMessage = (msg: string) => {
  console.log("Error Message: ", msg);
  emits("errorMessage", msg);
};

defineExpose({
  variables: computed(() => {
    const raw = viewer.value.variables as object;
    console.log("Raw data", raw);
    const v = Object.entries(raw).map((e: any) => {
      return {
        name: e[0],
        value: JSON.stringify(e[1]),
      };
    }) as Array<Variable>;
    console.log("Variables to submit", v);
    return v;
  }),
});
</script>

<template>
  <CamundaFormViewer
    v-if="schema"
    :variables="variables"
    :schema="schema.schema"
    ref="viewer"
    @errorMessage="emitErrorMessage"
  ></CamundaFormViewer>
</template>
