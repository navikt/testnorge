import { useCallback, useEffect, useRef, useState } from 'react'

interface EventSourceOptions {
	withCredentials?: boolean
	onOpen?: () => void
	onError?: (error: Event) => void
	onComplete?: () => void
	maxRetries?: number
	completeOnEvent?: string
}

interface EventSourceResult<T> {
	data: T | null
	error: Error | null
	isConnected: boolean
	isComplete: boolean
	close: () => void
}

export const useEventSource = <T>(
	url: string | null,
	eventTypes: string[],
	options?: EventSourceOptions,
): EventSourceResult<T> => {
	const [data, setData] = useState<T | null>(null)
	const [error, setError] = useState<Error | null>(null)
	const [isConnected, setIsConnected] = useState(false)
	const [isComplete, setIsComplete] = useState(false)
	const eventSourceRef = useRef<EventSource | null>(null)
	const retryCountRef = useRef(0)
	const completedRef = useRef(false)
	const maxRetries = options?.maxRetries ?? 3

	const close = useCallback(() => {
		if (eventSourceRef.current) {
			eventSourceRef.current.close()
			eventSourceRef.current = null
			setIsConnected(false)
		}
	}, [])

	useEffect(() => {
		if (!url) {
			close()
			return
		}

		retryCountRef.current = 0
		completedRef.current = false
		setData(null)
		setError(null)
		setIsComplete(false)

		const eventSource = new EventSource(url, {
			withCredentials: options?.withCredentials ?? true,
		})
		eventSourceRef.current = eventSource

		eventSource.onopen = () => {
			retryCountRef.current = 0
			setIsConnected(true)
			setError(null)
			options?.onOpen?.()
		}

		eventTypes.forEach((eventType) => {
			eventSource.addEventListener(eventType, (event: MessageEvent) => {
				try {
					const parsed = JSON.parse(event.data) as T
					setData(parsed)

					if (options?.completeOnEvent && eventType === options.completeOnEvent) {
						completedRef.current = true
						setIsComplete(true)
						setIsConnected(false)
						eventSource.close()
						eventSourceRef.current = null
						options?.onComplete?.()
					}
				} catch (parseError) {
					setError(new Error(`Kunne ikke parse SSE-data: ${event.data}`))
				}
			})
		})

		eventSource.onerror = (event) => {
			if (completedRef.current) {
				return
			}

			if (eventSource.readyState === EventSource.CLOSED) {
				setIsConnected(false)
				setError(new Error('SSE-tilkobling lukket'))
				options?.onError?.(event)
			} else if (eventSource.readyState === EventSource.CONNECTING) {
				retryCountRef.current += 1
				setIsConnected(false)
				if (retryCountRef.current >= maxRetries) {
					eventSource.close()
					eventSourceRef.current = null
					setError(new Error('SSE-tilkobling feilet etter flere forsøk'))
					options?.onError?.(event)
				}
			}
		}

		return () => {
			eventSource.close()
			eventSourceRef.current = null
			setIsConnected(false)
		}
	}, [url])

	return { data, error, isConnected, isComplete, close }
}
