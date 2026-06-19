import React from 'react'
import { FormDatepicker } from '@/components/ui/form/inputs/datepicker/Datepicker'
import { KdiMeldingFieldsProps } from '@/components/fagsystem/kdi/form/partials/types'
import { PubliseringstidspunktField } from '@/components/fagsystem/kdi/form/partials/PubliseringstidspunktField'

export const ForventetLoeslatelseForm = ({
	path,
	erEksisterendeMelding,
	onSort,
	sortVersjon,
}: KdiMeldingFieldsProps) => {
	return (
		<>
			<PubliseringstidspunktField
				path={path}
				erEksisterendeMelding={erEksisterendeMelding}
				onSort={onSort}
				sortVersjon={sortVersjon}
			/>
			<FormDatepicker
				name={`${path}.tidspunkt`}
				label="Forventet løslatt tidspunkt"
				format={'DD.MM.YYYY HH:mm:ss'}
				size="large"
				disabled={erEksisterendeMelding}
			/>
		</>
	)
}
