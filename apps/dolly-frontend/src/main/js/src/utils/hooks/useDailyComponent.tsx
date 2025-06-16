import { isSameDay } from 'date-fns'
import { useState, useEffect } from 'react'

export const useDailyComponent = (storageKey = 'daily-component') => {
	const [isVisible, setIsVisible] = useState(false)

	useEffect(() => {
		const lastDismissed = localStorage.getItem(storageKey)

		if (lastDismissed) {
			const lastDate = new Date(lastDismissed)
			const today = new Date()
			setIsVisible(!isSameDay(lastDate, today))
		} else {
			setIsVisible(true)
		}
	}, [storageKey])

	const dismiss = () => {
		localStorage.setItem(storageKey, new Date().toISOString())
		setIsVisible(false)
	}

	return { isVisible, dismiss }
}
