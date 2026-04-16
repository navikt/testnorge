import { describe, expect, it, vi, beforeEach, afterEach } from 'vitest'
import { renderHook, act, waitFor } from '@testing-library/react'
import { useBestillingStream } from '@/utils/hooks/useBestillingStream'
import { Bestilling } from '@/utils/hooks/useBestilling'

type MockEventSource = {
	onopen: ((event: Event) => void) | null
	onerror: ((event: Event) => void) | null
	close: ReturnType<typeof vi.fn>
	readyState: number
	addEventListener: ReturnType<typeof vi.fn>
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

vi.mock('@/utils/hooks/useBestilling', async () => {
	const actual = await vi.importActual('@/utils/hooks/useBestilling')
	return {
		...actual,
		useBestillingById: vi.fn(() => ({
			bestilling: null,
			loading: false,
		})),
	}
})

describe('useBestillingStream', () => {
	let originalEventSource: typeof globalThis.EventSource

	beforeEach(() => {
		originalEventSource = globalThis.EventSource
		lastMockEventSource = null

		const MockEventSourceClass = function (this: any, _url: string, _options?: EventSourceInit) {
			const mock = createMockEventSource()
			this.close = mock.close
			this.addEventListener = mock.addEventListener
			this.listeners = mock.listeners
			this.readyState = mock.readyState
			this.simulateOpen = function () {
				this.readyState = EventSource.OPEN
				this.onopen?.(new Event('open'))
			}
			this.simulateMessage = mock.simulateMessage.bind(mock)
			this.simulateError = function (rs: number) {
				this.readyState = rs
				this.onerror?.(new Event('error'))
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
		vi.restoreAllMocks()
	})

	it('should not connect for organisasjon bestillinger', () => {
		const { result } = renderHook(() => useBestillingStream(1, true, true))

		expect(result.current.isStreaming).toBe(false)
		expect(result.current.bestilling).toBeNull()
		expect(lastMockEventSource).toBeNull()
	})

	it('should not connect when disabled', () => {
		const { result } = renderHook(() => useBestillingStream(1, false, false))

		expect(result.current.isStreaming).toBe(false)
		expect(lastMockEventSource).toBeNull()
	})

	it('should connect to SSE for regular bestilling', () => {
		renderHook(() => useBestillingStream(1, false, true))

		expect(lastMockEventSource).not.toBeNull()
	})

	it('should stream progress events', async () => {
		const progressData: Partial<Bestilling> = {
			id: 1,
			gruppeId: 10,
			ferdig: false,
			stoppet: false,
			antallLevert: 3,
			antallIdenter: 5,
			feil: null,
			sistOppdatert: '2024-01-01T00:00:00',
		}

		const { result } = renderHook(() => useBestillingStream(1, false, true))

		act(() => {
			lastMockEventSource!.simulateOpen()
		})

		act(() => {
			lastMockEventSource!.simulateMessage('progress', progressData)
		})

		await waitFor(() => {
			expect(result.current.isStreaming).toBe(true)
			expect(result.current.bestilling).toEqual(progressData)
		})
	})

	it('should handle completed event', async () => {
		const completedData: Partial<Bestilling> = {
			id: 1,
			gruppeId: 10,
			ferdig: true,
			stoppet: false,
			antallLevert: 5,
			antallIdenter: 5,
			feil: null,
			sistOppdatert: '2024-01-01T00:01:00',
		}

		const { result } = renderHook(() => useBestillingStream(1, false, true))

		act(() => {
			lastMockEventSource!.simulateOpen()
			lastMockEventSource!.simulateMessage('completed', completedData)
		})

		await waitFor(() => {
			expect(result.current.bestilling?.ferdig).toBe(true)
		})
	})

	it('should fall back to polling on SSE error', async () => {
		const { result } = renderHook(() => useBestillingStream(1, false, true))

		act(() => {
			lastMockEventSource!.simulateError(EventSource.CLOSED)
		})

		await waitFor(() => {
			expect(result.current.isStreaming).toBe(false)
		})
	})
})
