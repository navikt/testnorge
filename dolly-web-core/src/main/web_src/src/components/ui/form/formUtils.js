import React from 'react'
import Button from '~/components/ui/button/Button'

export const fieldError = meta => {
	return !!meta.touched && !!meta.error ? { feilmelding: meta.error } : null
}

export const panelError = formikBag => {
	// Sjekk feil for panelet. Krever at det sendes inn hvilke attributter
	// som ligger under hvilket panel.
	return false
}

export const SyntEvent = (name, value) => ({ target: { name, value } })
