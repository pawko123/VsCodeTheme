#include <stdio.h>
#include <stdbool.h>
#include <stdlib.h>
#include <string.h>
#include <pthread.h>

#define ARRAY_LEN 8
#define UNUSED(x) (void)(x)

static const char *LEVELS[] = {
    [0] = "trace",
    [1] = "debug",
    [2] = "info",
    [3] = "warn",
    [4] = "error"
};

typedef struct {
    const char *name;
    unsigned version;
    bool enabled;
} plugin_t;

typedef struct {
    plugin_t plugins[ARRAY_LEN];
    size_t count;
    pthread_mutex_t lock;
} registry_t;

static registry_t *registry_create(void) {
    registry_t *registry = calloc(1, sizeof(registry_t));
    if (!registry) {
        perror("calloc");
        exit(EXIT_FAILURE);
    }
    pthread_mutex_init(&registry->lock, NULL);
    return registry;
}

static void registry_destroy(registry_t *registry) {
    if (!registry) return;
    pthread_mutex_destroy(&registry->lock);
    free(registry);
}

static void registry_add(registry_t *registry, plugin_t plugin) {
    if (!registry || registry->count >= ARRAY_LEN) return;
    pthread_mutex_lock(&registry->lock);
    registry->plugins[registry->count++] = plugin;
    pthread_mutex_unlock(&registry->lock);
}

static plugin_t *registry_find(registry_t *registry, const char *name) {
    if (!registry || !name) return NULL;
    for (size_t i = 0; i < registry->count; ++i) {
        if (strcmp(registry->plugins[i].name, name) == 0) {
            return &registry->plugins[i];
        }
    }
    return NULL;
}

static void registry_dump(const registry_t *registry) {
    if (!registry) return;
    puts("Registry dump:");
    for (size_t i = 0; i < registry->count; ++i) {
        const plugin_t *plugin = &registry->plugins[i];
        printf("  #%zu %-10s v%u [%s]\n",
               i,
               plugin->name,
               plugin->version,
               plugin->enabled ? "enabled" : "disabled");
    }
}

static void *worker(void *arg) {
    registry_t *registry = arg;
    registry_add(registry, (plugin_t){"metrics", 3u, true});
    return NULL;
}

int main(void) {
    registry_t *registry = registry_create();

    registry_add(registry, (plugin_t){"auth", 5u, true});
    registry_add(registry, (plugin_t){"billing", 2u, false});

    pthread_t thread;
    pthread_create(&thread, NULL, worker, registry);
    pthread_join(thread, NULL);

    plugin_t *plugin = registry_find(registry, "auth");
    if (plugin) {
        plugin->enabled = !plugin->enabled;
    }

    registry_dump(registry);

    registry_destroy(registry);
    return EXIT_SUCCESS;
}
