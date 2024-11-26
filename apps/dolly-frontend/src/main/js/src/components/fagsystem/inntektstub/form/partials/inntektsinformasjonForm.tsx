import React, { useState } from 'react'
import { FormTextInput } from '@/components/ui/form/inputs/textInput/TextInput'
import InntektsinformasjonLister from './inntektsinformasjonLister/inntektsinformasjonLister'
import InntektsendringForm from './inntektsendringForm'
import { Monthpicker } from '@/components/ui/form/inputs/monthpicker/Monthpicker'
import { useFormContext } from 'react-hook-form'
import { VirksomhetToggle } from '@/components/fagsystem/inntektstub/form/partials/virksomhetToggle'
import { FormDatepicker } from '@/components/ui/form/inputs/datepicker/Datepicker'

interface InntektsinformasjonForm {
	path: string
}

export default ({ path }: InntektsinformasjonForm) => {
	const formMethods = useFormContext()
	const [date, setDate] = useState(
		formMethods.watch(`${path}.sisteAarMaaned`) !== ''
			? Date.parse(formMethods.watch(`${path}.sisteAarMaaned`))
			: null,
	)

	const [rapporteringsdate, setRapporteringsdato] = useState(
		formMethods.watch(`${path}.rapporteringsdato`) !== ''
			? Date.parse(formMethods.watch(`${path}.rapporteringsdato`))
			: null,
	)

	const handleDateChange = (selectedDate: Date) => {
		selectedDate?.setHours(6)
		setDate(selectedDate)
		formMethods.setValue(
			`${path}.sisteAarMaaned`,
			selectedDate ? selectedDate.toISOString().substring(0, 7) : '',
		)
		formMethods.trigger(`${path}.sisteAarMaaned`)
	}

	return (
		<React.Fragment key={path}>
			<div className="flexbox--flex-wrap">
				<Monthpicker
					name={`${path}.sisteAarMaaned`}
					label="År/måned"
					date={date}
					handleDateChange={handleDateChange}
				/>
				<FormTextInput
					name={`${path}.antallMaaneder`}
					label="Generer x mnd tilbake i tid"
					type="number"
				/>
				<FormDatepicker
					name={`${path}.rapporteringsdato`}
					label="Rapporteringstidspunkt"
					format={'DD.MM.YYYY HH:mm'}
					date={rapporteringsdate}
				/>
			</div>
			<VirksomhetToggle path={path} />
			<InntektsinformasjonLister formMethods={formMethods} path={path} />
			<InntektsendringForm formMethods={formMethods} path={path} />
		</React.Fragment>
	)
}
