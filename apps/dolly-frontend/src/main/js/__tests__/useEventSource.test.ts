import { describe, expect, it, vi, beforeEach, afterEach } from 'vitest'
import { renderHook, act, waitFor } from '@testing-library/react'
import { useEventSource } from '@/utils/hooks/useEventSource'

type MockEventSource = {
	onopen: ((event: Event) => void) | null
	onerror: ((event: Event) => void) | null
	close: ReturnType<typeof vi.fn>
	readyState: number
	addEventListener: ReturnType<typeof vi.fn>
	removeEventListener: ReturnType<typeof vi.fn>
	listeners: Map<string, ((event: MessageEvent) => void)[]>
	simulateOpen: () => void
	simulateMessage: (type: string, data: unknown) => void
	simulateError: (readyState: number) => void
}

let lastMockEventSource: MockEventSource | null = null

const createMockEventSource = (): MockEventSource => {
	const listeners = new Map<string, ((event: MessageEvent) => void)[]>()
	return {
		onopen: null,
		onerror: null,
		close: vi.fn(),
		readyState: EventSource.CONNECTING,
		addEventListener: vi.fn((type: string, handler: (event: MessageEvent) => void) => {
			const existing = listeners.get(type) || []
			existing.push(handler)
			listeners.set(type, existing)
		}),
		removeEventListener: vi.fn(),
		listeners,
		simulateOpen() {
			this.readyState = EventSource.OPEN
			this.onopen?.(new Event('open'))
		},
		simulateMessage(type: string, data: unknown) {
			const handlers = this.listeners.get(type) || []
			const event = new MessageEvent(type, { data: JSON.stringify(data) })
			handlers.forEach((h) => h(event))
		},
		simulateError(readyState: number) {
			this.readyState = readyState
			this.onerror?.(new Event('error'))
		},
	}
}

describe('useEventSource', () => {
	let originalEventSource: typeof globalThis.EventSource

	beforeEach(() => {
		originalEventSource = globalThis.EventSource
		lastMockEventSource = null

		const MockEventSourceClass = function (this: any, _url: string, _options?: EventSourceInit) {
			const mock = createMockEventSource()
			lastMockEventSource = mock
			this.close = mock.close
			this.addEventListener = mock.addEventListener
			this.removeEventListener = mock.removeEventListener
			this.listeners = mock.listeners
			this.readyState = mock.readyState
			this.simulateOpen = function () {
				this.readyState = EventSource.OPEN
				this.onopen?.(new Event('open'))
				mock.readyState = this.readyState
			}
			this.simulateMessage = mock.simulateMessage.bind(mock)
			this.simulateError = function (rs: number) {
				this.readyState = rs
				this.onerror?.(new Event('error'))
				mock.readyState = rs
			}
			lastMockEventSource = this as unknown as MockEventSource
		} as unknown as { new (url: string, opts?: EventSourceInit): EventSource }
		Object.defineProperty(MockEventSourceClass, 'CONNECTING', { value: 0 })
		Object.defineProperty(MockEventSourceClass, 'OPEN', { value: 1 })
		Object.defineProperty(MockEventSourceClass, 'CLOSED', { value: 2 })

		globalThis.EventSource = MockEventSourceClass as unknown as typeof EventSource
	})

	afterEach(() => {
		globalThis.EventSource = originalEventSource
	})

	it('should not connect when url is null', () => {
		renderHook(() => useEventSource(null, ['progress']))

		expect(lastMockEventSource).toBeNull()
	})

	it('should connect when url is provided', () => {
		renderHook(() => useEventSource('/api/stream', ['progress']))

		expect(lastMockEventSource).not.toBeNull()
	})

	it('should set isConnected on open', async () => {
		const { result } = renderHook(() => useEventSource('/api/stream', ['progress']))

		expect(result.current.isConnected).toBe(false)

		act(() => {
			lastMockEventSource!.simulateOpen()
		})

		await waitFor(() => {
			expect(result.current.isConnected).toBe(true)
		})
	})

	it('should parse incoming message data', async () => {
		const { result } = renderHook(() =>
			useEventSource<{ id: number; ferdig: boolean }>('/api/stream', ['progress']),
		)

		act(() => {
			lastMockEventSource!.simulateOpen()
		})

		act(() => {
			lastMockEventSource!.simulateMessage('progress', { id: 1, ferdig: false })
		})

		await waitFor(() => {
			expect(result.current.data).toEqual({ id: 1, ferdig: false })
		})
	})

	it('should listen to multiple event types', async () => {
		const { result } = renderHook(() =>
			useEventSource<{ status: string }>('/api/stream', ['progress', 'completed']),
		)

		act(() => {
			lastMockEventSource!.simulateOpen()
			lastMockEventSource!.simulateMessage('completed', { status: 'done' })
		})

		await waitFor(() => {
			expect(result.current.data).toEqual({ status: 'done' })
		})
	})

	it('should set error when connection is closed', async () => {
		const { result } = renderHook(() => useEventSource('/api/stream', ['progress']))

		act(() => {
			lastMockEventSource!.simulateOpen()
		})

		act(() => {
			lastMockEventSource!.simulateError(EventSource.CLOSED)
		})

		await waitFor(() => {
			expect(result.current.error).not.toBeNull()
			expect(result.current.isConnected).toBe(false)
		})
	})

	it('should close connection on unmount', () => {
		const { unmount } = renderHook(() => useEventSource('/api/stream', ['progress']))

		const closeFn = lastMockEventSource!.close
		unmount()

		expect(closeFn).toHaveBeenCalled()
	})

	it('should close connection when close is called', async () => {
		const { result } = renderHook(() => useEventSource('/api/stream', ['progress']))

		act(() => {
			lastMockEventSource!.simulateOpen()
		})

		act(() => {
			result.current.close()
		})

		await waitFor(() => {
			expect(result.current.isConnected).toBe(false)
		})
	})

	it('should set error on invalid JSON', async () => {
		const { result } = renderHook(() =>
			useEventSource<{ id: number }>('/api/stream', ['progress']),
		)

		act(() => {
			lastMockEventSource!.simulateOpen()
		})

		const handlers = lastMockEventSource!.listeners.get('progress') || []
		act(() => {
			handlers.forEach((h) =>
				h(new MessageEvent('progress', { data: 'not-valid-json' })),
			)
		})

		await waitFor(() => {
			expect(result.current.error).not.toBeNull()
			expect(result.current.error?.message).toContain('Kunne ikke parse SSE-data')
		})
	})

	it('should call onError callback', () => {
		const onError = vi.fn()

		renderHook(() =>
			useEventSource('/api/stream', ['progress'], { onError }),
		)

		act(() => {
			lastMockEventSource!.simulateError(EventSource.CLOSED)
		})

		expect(onError).toHaveBeenCalled()
	})

	it('should tolerate transient errors and retry', async () => {
		const onError = vi.fn()

		const { result } = renderHook(() =>
			useEventSource('/api/stream', ['progress'], { onError, maxRetries: 3 }),
		)

		act(() => {
			lastMockEventSource!.simulateOpen()
		})

		act(() => {
			lastMockEventSource!.simulateError(EventSource.CONNECTING)
		})

		await waitFor(() => {
			expect(result.current.isConnected).toBe(false)
			expect(result.current.error).toBeNull()
			expect(onError).not.toHaveBeenCalled()
		})
	})

	it('should fail after maxRetries transient errors', async () => {
		const onError = vi.fn()

		const { result } = renderHook(() =>
			useEventSource('/api/stream', ['progress'], { onError, maxRetries: 2 }),
		)

		act(() => {
			lastMockEventSource!.simulateOpen()
		})

		act(() => {
			lastMockEventSource!.simulateError(EventSource.CONNECTING)
		})

		act(() => {
			lastMockEventSource!.simulateError(EventSource.CONNECTING)
		})

		await waitFor(() => {
			expect(result.current.error).not.toBeNull()
			expect(result.current.error?.message).toContain('flere forsøk')
			expect(onError).toHaveBeenCalled()
		})
	})
})
