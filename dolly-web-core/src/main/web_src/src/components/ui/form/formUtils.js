import React from 'react'
import Button from '~/components/ui/button/Button'

export const fieldError = meta => {
	return !!meta.touched && !!meta.error ? { feilmelding: meta.error } : null
}

export const panelError = formikProps => {
	// Sjekk feil for panelet. Krever at det sendes inn hvilke attributter
	// som ligger under hvilket panel.
	return false
}

export const SyntEvent = (name, value) => ({ target: { name, value } })

export const FieldArrayAddButton = ({ title, onClick }) => (
	<Button className="flexbox--align-center field-group-add" kind="add-circle" onClick={onClick}>
		{title}
	</Button>
)

export const FieldArrayRemoveButton = ({ onClick }) => (
	<Button className="field-group-remove" kind="remove-circle" onClick={onClick} title="Fjern" />
)
