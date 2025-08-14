import React from 'react'

export interface ShowErrorContextType {
	showError: boolean
	setShowError: (show: boolean) => void
}

export const ShowErrorContext = React.createContext({
	showError: false,
	setShowError: () => {},
} as ShowErrorContextType)
