import React, { useState } from 'react'
import { FormikTextInput } from '@/components/ui/form/inputs/textInput/TextInput'
import { InntektstubVirksomhetToggle } from './inntektstubVirksomhetToggle'
import InntektsinformasjonLister from './inntektsinformasjonLister/inntektsinformasjonLister'
import InntektsendringForm from './inntektsendringForm'
import * as _ from 'lodash'
import { Monthpicker } from '@/components/ui/form/inputs/monthpicker/Monthpicker'
import { FormikDateTimepicker } from '@/components/ui/form/inputs/timepicker/Timepicker'
import { useFormContext } from 'react-hook-form'

interface InntektsinformasjonForm {
	path: string
}

export default ({ path }: InntektsinformasjonForm) => {
	const formMethods = useFormContext()
	const [date, setDate] = useState(
		_.get(formMethods.getValues(), `${path}.sisteAarMaaned`) !== ''
			? Date.parse(_.get(formMethods.getValues(), `${path}.sisteAarMaaned`))
			: null,
	)

	const [rapporteringsdate, setRapporteringsdato] = useState(
		_.get(formMethods.getValues(), `${path}.rapporteringsdato`) !== ''
			? Date.parse(_.get(formMethods.getValues(), `${path}.rapporteringsdato`))
			: null,
	)

	const handleDateChange = (selectedDate: Date) => {
		setDate(selectedDate)
		formMethods.setValue(
			`${path}.sisteAarMaaned`,
			selectedDate ? selectedDate.toISOString().substring(0, 7) : '',
		)
		formMethods.trigger(`${path}.sisteAarMaaned`)
	}

	const handleRapporteringDateChange = (selectedDate: Date) => {
		setRapporteringsdato(selectedDate)
		formMethods.setValue(
			`${path}.rapporteringsdato`,
			selectedDate ? selectedDate.toISOString().substring(0, 19) : null,
		)
		formMethods.trigger(`${path}.rapporteringsdato`)
	}

	return (
		<div key={path}>
			<div className="flexbox--flex-wrap">
				<Monthpicker
					name={`${path}.sisteAarMaaned`}
					label="År/måned"
					date={date}
					handleDateChange={handleDateChange}
				/>
				<FormikTextInput
					name={`${path}.antallMaaneder`}
					label="Generer x mnd tilbake i tid"
					type="number"
				/>
				<FormikDateTimepicker
					formMethods={formMethods}
					name={`${path}.rapporteringsdato`}
					label="Rapporteringstidspunkt"
					date={rapporteringsdate}
					onChange={handleRapporteringDateChange}
				/>
			</div>
			<InntektstubVirksomhetToggle formMethods={formMethods} path={path} />
			<InntektsinformasjonLister formMethods={formMethods} path={path} />
			<InntektsendringForm formMethods={formMethods} path={path} />
		</div>
	)
}
