<template>
  <main class="dashboard" :class="{ 'dashboard--loading': isLoading }">
    <header class="dashboard__header">
      <h1>{{ title }}</h1>
      <BaseButton size="sm" :disabled="isLoading" @click="refresh">
        {{ isLoading ? 'Refreshing…' : 'Refresh Data' }}
      </BaseButton>
    </header>

    <section class="dashboard__filters">
      <label class="dashboard__search">
        <span>Search cards</span>
        <input
          v-model.trim="search"
          placeholder="Type to filter"
          :aria-busy="isLoading.toString()"
        />
      </label>

      <label class="dashboard__toggle">
        <input type="checkbox" v-model="showOnlyPinned" />
        <span>Show only pinned</span>
      </label>
    </section>

    <TransitionGroup
      name="card"
      tag="section"
      class="dashboard__cards"
      @before-leave="onLeavingCard"
    >
      <article
        v-for="card in visibleCards"
        :key="card.id"
        class="dashboard-card"
        :class="{ 'dashboard-card--pinned': card.pinned }"
        @click="selectCard(card)"
      >
        <header>
          <h2>{{ card.title }}</h2>
          <IconStar v-if="card.pinned" aria-label="Pinned" />
        </header>
        <p>{{ card.preview }}</p>
        <footer>
          <Chip v-for="tag in card.tags" :key="tag" :label="tag" />
          <span class="dashboard-card__meta">{{ formatUpdatedAt(card.updatedAt) }}</span>
        </footer>
      </article>
    </TransitionGroup>

    <EmptyState v-if="!visibleCards.length && !isLoading" text="Nothing to display" />
  </main>
</template>

<script setup lang="ts">
import { computed, defineEmits, defineProps, onMounted, ref, watch } from 'vue';
import type { DashboardCard, UserSession } from '@/types';

const props = defineProps<{
  session: UserSession | null;
  cards: DashboardCard[];
  title?: string;
}>();

const emit = defineEmits<{
  (e: 'refresh'): void;
  (e: 'select-card', card: DashboardCard): void;
}>();

const isLoading = ref(false);
const search = ref('');
const showOnlyPinned = ref(false);
const title = computed(() => props.title ?? 'Realtime Overview');

const visibleCards = computed(() => {
  const query = search.value.toLowerCase();
  return props.cards
    .filter(card => !showOnlyPinned.value || card.pinned)
    .filter(card =>
      [card.title, card.preview, card.tags.join(' ')].some(value =>
        value.toLowerCase().includes(query)
      )
    );
});

function refresh() {
  if (isLoading.value) return;
  isLoading.value = true;
  emit('refresh');
  setTimeout(() => {
    isLoading.value = false;
  }, 650);
}

function selectCard(card: DashboardCard) {
  emit('select-card', card);
}

function formatUpdatedAt(date: string) {
  return new Intl.DateTimeFormat('en', {
    dateStyle: 'medium',
    timeStyle: 'short'
  }).format(new Date(date));
}

function onLeavingCard(el: Element) {
  el.classList.add('dashboard-card--leaving');
}

watch(
  () => props.session?.token,
  (token, prev) => {
    if (token && token !== prev) {
      refresh();
    }
  }
);

onMounted(() => {
  if (!props.cards.length) {
    refresh();
  }
});

defineExpose({ refresh, visibleCards });
</script>

<style scoped lang="scss">
:global(body) {
  font-family: 'JetBrains Mono', ui-monospace, SFMono-Regular, monospace;
}

.dashboard {
  display: grid;
  gap: 1.5rem;
  padding: 2rem;
  background: #1a1b26;
  color: #e9e9ed;

  &--loading::after {
    content: ''; /* overlay shimmer */
    position: absolute;
    inset: 0;
    background: linear-gradient(120deg, transparent, #1f2335aa, transparent);
    animation: shimmer 1.2s linear infinite;
  }
}

.dashboard__header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.dashboard__filters {
  display: flex;
  gap: 1rem;
  flex-wrap: wrap;
}

.dashboard__search {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 0.35rem;

  input {
    border: 1px solid #3d4b73;
    background: #1f2335;
    border-radius: 0.5rem;
    padding: 0.65rem 0.85rem;
    color: inherit;
  }
}

.dashboard__cards {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(240px, 1fr));
  gap: 1rem;
}

.dashboard-card {
  border: 1px solid #2b3150;
  border-radius: 1rem;
  padding: 1rem;
  background: #1f2335;
  transition: border-color 120ms ease, transform 120ms ease;

  &--pinned {
    border-color: #7aa2f7;
  }

  &--leaving {
    opacity: 0.3;
  }

  &:hover {
    border-color: #bb9af7;
    transform: translateY(-2px);
  }
}

.dashboard-card__meta {
  color: #5c7287;
  font-size: 0.75rem;
}

.card-enter-active,
.card-leave-active {
  transition: all 180ms ease;
}

.card-enter-from,
.card-leave-to {
  opacity: 0;
  transform: scale(0.95);
}

@keyframes shimmer {
  from {
    transform: translateX(-100%);
  }
  to {
    transform: translateX(100%);
  }
}
</style>
