import { FormDatepicker } from '@/components/ui/form/inputs/datepicker/Datepicker'
import React, { useEffect } from 'react'
import { useFormContext } from 'react-hook-form'
import { naaPubliseringstidspunkt } from '@/components/fagsystem/kdi/form/Form'

export const PubliseringstidspunktField = ({ path, erEksisterendeMelding }) => {
	const formMethods = useFormContext()
	const publiseringsTidspunktPath = `${path}.publiseringstidspunkt`
	const publiseringstidspunkt = formMethods.watch(publiseringsTidspunktPath)

	useEffect(() => {
		if (!publiseringstidspunkt) {
			formMethods.setValue(publiseringsTidspunktPath, naaPubliseringstidspunkt())
		}
	}, [])

	return (
		<FormDatepicker
			key={publiseringstidspunkt ? 'set' : 'unset'}
			name={`${path}.publiseringstidspunkt`}
			label="Publiseringstidspunkt"
			format={'DD.MM.YYYY HH:mm:ss'}
			disabled={erEksisterendeMelding}
			// date={rapporteringsdate}
		/>
	)
}
