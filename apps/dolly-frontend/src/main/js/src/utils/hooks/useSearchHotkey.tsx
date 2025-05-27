import { RefObject, useEffect } from 'react'

export const useSearchHotkey = (inputRef: RefObject<any>): string => {
	const shortcutKey = window.navigator.userAgentData?.platform?.toUpperCase().includes('MAC')
		? '⌘-K'
		: 'Ctrl-K'

	useEffect(() => {
		const isMac = window.navigator.userAgentData?.platform?.toUpperCase().includes('MAC')

		const handleKeyDown = (e: KeyboardEvent) => {
			// Listening for ⌘+K (Mac) or Ctrl+K (Windows/Linux)
			if (
				(isMac && e.metaKey && e.key.toLowerCase() === 'k') ||
				(!isMac && e.ctrlKey && e.key.toLowerCase() === 'k')
			) {
				e.preventDefault()

				if (inputRef.current) {
					const input = inputRef.current.inputRef || inputRef.current
					input?.focus()
				}
			}
		}

		document.addEventListener('keydown', handleKeyDown)
		return () => document.removeEventListener('keydown', handleKeyDown)
	}, [inputRef])

	return shortcutKey
}
