import { memo, useCallback, useDeferredValue, useEffect, useId, useMemo, useReducer, useRef } from 'react';
import PropTypes from 'prop-types';

const initialState = {
  items: [],
  unread: new Set(),
  filter: 'all'
};

function reducer(state, action) {
  switch (action.type) {
    case 'hydrate':
      return {
        ...state,
        items: action.payload,
        unread: new Set(action.payload.filter(item => !item.read).map(item => item.id))
      };
    case 'mark-read': {
      const unread = new Set(state.unread);
      unread.delete(action.id);
      return { ...state, unread };
    }
    case 'filter':
      return { ...state, filter: action.value };
    default:
      return state;
  }
}

function useNotifications(source$) {
  const [state, dispatch] = useReducer(reducer, initialState);

  useEffect(() => {
    const subscription = source$.subscribe(payload => {
      dispatch({ type: 'hydrate', payload });
    });
    return () => subscription.unsubscribe();
  }, [source$]);

  return [state, dispatch];
}

export function NotificationsPanel({ stream$, onSelect }) {
  const [state, dispatch] = useNotifications(stream$);
  const panelId = useId();
  const deferredFilter = useDeferredValue(state.filter);
  const containerRef = useRef(null);

  const visibleItems = useMemo(() => {
    switch (deferredFilter) {
      case 'unread':
        return state.items.filter(item => state.unread.has(item.id));
      case 'mentions':
        return state.items.filter(item => item.kind === 'mention');
      default:
        return state.items;
    }
  }, [state.items, state.unread, deferredFilter]);

  const selectItem = useCallback(
    item => {
      if (!item.read) {
        dispatch({ type: 'mark-read', id: item.id });
      }
      onSelect?.(item);
    },
    [dispatch, onSelect]
  );

  const changeFilter = event => {
    dispatch({ type: 'filter', value: event.target.value });
  };

  useEffect(() => {
    if (!containerRef.current || !state.unread.size) return;
    containerRef.current.dataset.highlight = 'true';
    const timeout = window.setTimeout(() => {
      delete containerRef.current?.dataset.highlight;
    }, 800);
    return () => window.clearTimeout(timeout);
  }, [state.unread]);

  return (
    <section ref={containerRef} className="notifications" aria-labelledby={`${panelId}-title`}>
      <header className="notifications__header">
        <h2 id={`${panelId}-title`}>Inbox</h2>
        <select value={state.filter} onChange={changeFilter}>
          <option value="all">All</option>
          <option value="unread">Unread</option>
          <option value="mentions">Mentions</option>
        </select>
      </header>

      <ul className="notifications__list">
        {visibleItems.map(item => (
          <li
            key={item.id}
            className={`notifications__item ${state.unread.has(item.id) ? 'is-unread' : ''}`}
          >
            <button type="button" onClick={() => selectItem(item)}>
              <strong>{item.title}</strong>
              <span>{item.preview}</span>
              <time dateTime={item.createdAt}>{new Date(item.createdAt).toLocaleTimeString()}</time>
            </button>
          </li>
        ))}
      </ul>
    </section>
  );
}

NotificationsPanel.propTypes = {
  stream$: PropTypes.shape({ subscribe: PropTypes.func.isRequired }).isRequired,
  onSelect: PropTypes.func
};

NotificationsPanel.defaultProps = {
  onSelect: undefined
};

export const NotificationCount = memo(function NotificationCount({ count }) {
  return <span aria-live="polite">{count ? `${count} unread` : 'Inbox zero'}</span>;
});

NotificationCount.propTypes = {
  count: PropTypes.number
};

NotificationCount.defaultProps = {
  count: 0
};
