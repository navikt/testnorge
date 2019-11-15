import React from 'react'
import Panel from '~/components/ui/panel/Panel'
import { panelError } from '~/components/ui/form/formUtils'
import { FalskIdentitet } from './falskIdentitet/FalskIdentitet'

export const Identifikasjon = ({ formikBag }) => {
	return (
		<Panel heading="Identifikasjon" hasErrors={panelError(formikBag)} startOpen>
			<FalskIdentitet formikBag={formikBag} />
		</Panel>
	)
}
