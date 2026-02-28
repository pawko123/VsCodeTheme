#include <algorithm>
#include <chrono>
#include <coroutine>
#include <iomanip>
#include <iostream>
#include <map>
#include <optional>
#include <ranges>
#include <span>
#include <string>
#include <string_view>
#include <unordered_map>
#include <vector>

template <typename T>
class generator {
public:
    struct promise_type {
        T value;
        std::suspend_always yield_value(T v) {
            value = std::move(v);
            return {};
        }
        std::suspend_always initial_suspend() { return {}; }
        std::suspend_always final_suspend() noexcept { return {}; }
        generator get_return_object() { return generator{handle_type::from_promise(*this)}; }
        void return_void() {}
        void unhandled_exception() { throw; }
    };

    using handle_type = std::coroutine_handle<promise_type>;

    explicit generator(handle_type handle) : handle_(handle) {}
    generator(generator &&other) noexcept : handle_(other.handle_) { other.handle_ = nullptr; }
    ~generator() {
        if (handle_) handle_.destroy();
    }

    class iterator {
    public:
        void operator++() { handle_.resume(); }
        const T &operator*() const { return handle_.promise().value; }
        bool operator==(std::default_sentinel_t) const { return !handle_ || handle_.done(); }

    private:
        friend generator;
        explicit iterator(handle_type handle) : handle_(handle) {}
        handle_type handle_;
    };

    iterator begin() {
        if (handle_) handle_.resume();
        return iterator{handle_};
    }
    std::default_sentinel_t end() const { return {}; }

private:
    handle_type handle_;
};

generator<int> fibonacci() {
    co_yield 0;
    co_yield 1;

    int a = 0;
    int b = 1;
    while (true) {
        co_yield a + b;
        std::tie(a, b) = std::make_pair(b, a + b);
    }
}

struct Stats {
    std::vector<int> values;
    std::optional<double> average;
};

struct UserViewModel {
    std::string name;
    std::map<std::string, int> abilities;
    std::chrono::system_clock::time_point updated_at;
};

Stats build_stats(std::span<const int> samples) {
    Stats stats{.values = {samples.begin(), samples.end()}, .average = std::nullopt};
    if (!samples.empty()) {
        const auto total = std::accumulate(samples.begin(), samples.end(), 0);
        stats.average = static_cast<double>(total) / samples.size();
    }
    return stats;
}

UserViewModel map_user(std::string_view name, std::span<const int> powers) {
    UserViewModel vm{
        .name = std::string{name},
        .abilities = {}
    };

    int idx = 0;
    for (int p : powers) {
        vm.abilities["skill_" + std::to_string(idx++)] = p;
    }

    vm.updated_at = std::chrono::system_clock::now();
    return vm;
}

int main() {
    std::vector<int> samples{1, 3, 5, 8, 13, 21};
    const auto stats = build_stats(samples);

    const auto vm = map_user("Ada", samples);
    std::cout << "User " << vm.name << " has " << vm.abilities.size() << " abilities\n";

    auto fib = fibonacci();
    for (int value : fib | std::views::take(7)) {
        std::cout << value << ' ';
    }
    std::cout << '\n';

    if (stats.average) {
        std::cout << "Average: " << std::fixed << std::setprecision(2) << *stats.average << '\n';
    }

    return 0;
}
