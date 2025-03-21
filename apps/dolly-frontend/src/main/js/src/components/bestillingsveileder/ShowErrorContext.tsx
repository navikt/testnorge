import React from 'react'

export type ShowErrorContextType = { showError: boolean; setShowError: any }
export const ShowErrorContext = React.createContext(false)
