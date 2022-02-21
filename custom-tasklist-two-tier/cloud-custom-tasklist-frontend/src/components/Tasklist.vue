<script lang="ts" setup>
import { useQuery, useMutation, useResult } from '@vue/apollo-composable'
import gql from 'graphql-tag'

defineProps(['tasklist'])
defineEmits(['selectedTask']);

</script>

<template>
  <div v-for="task in tasklist" class="element">
    <button @click="$emit('selectedTask', task)">{{ task.name }}</button>
  </div>
</template>

<style>
button {
  all: unset;
  cursor: pointer;
  width: 100%;
}
</style>
