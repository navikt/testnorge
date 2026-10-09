import React from 'react'
import InntektsinformasjonLister from './inntektsinformasjonLister/inntektsinformasjonLister'
import { useFormContext } from 'react-hook-form'
import { VirksomhetToggle } from '@/components/fagsystem/inntektstub/form/partials/virksomhetToggle'
import { FormDatepicker } from '@/components/ui/form/inputs/datepicker/Datepicker'
import { MonthCombobox } from '@/components/ui/form/inputs/monthsCombobox/MonthCombobox'

interface InntektsinformasjonForm {
	path: string
}

export default ({ path }: InntektsinformasjonForm) => {
	const formMethods = useFormContext()

	const rapporteringsdate =
		formMethods.watch(`${path}.rapporteringsdato`) !== ''
			? Date.parse(formMethods.watch(`${path}.rapporteringsdato`))
			: null

	return (
		<React.Fragment key={path}>
			<MonthCombobox name={`${path}.perioder`} label="Perioder" />
			<FormDatepicker
				name={`${path}.rapporteringsdato`}
				label="Rapporteringstidspunkt"
				format={'DD.MM.YYYY HH:mm'}
				date={rapporteringsdate}
			/>
			<VirksomhetToggle path={path} />
			<InntektsinformasjonLister formMethods={formMethods} path={path} />
		</React.Fragment>
	)
}
