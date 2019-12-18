import React from 'react'
import _get from 'lodash/get'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import { FormikDatepicker } from '~/components/ui/form/inputs/datepicker/Datepicker'
import { FormikTextInput } from '~/components/ui/form/inputs/textInput/TextInput'

export const ArbeidsavtaleForm = ({ formikBag, path }) => {
	const arbeidsavtalePath = `${path}.arbeidsavtale`
	const arbeidsavtale = _get(formikBag.values, arbeidsavtalePath)

	const harArbTimerPerUke = arbeidsavtale.avtaltArbeidstimerPerUke.toString()
	const harKonverterteTimer = arbeidsavtale.antallKonverterteTimer.toString()

	// Kun to av feltene (stillingsprosent, antall konverterte timer, avtalte timer per uke) kan settes pr arbeidsforhold
	const infotekst = 'Antall konverterte timer og avtalte timer per uke kan ikke være satt samtidig.'

	return (
		<div>
			<h4>Arbeidsavtale</h4>
			<div className="flexbox--flex-wrap">
				<FormikSelect
					name={`${arbeidsavtalePath}.yrke`}
					label="Yrke"
					kodeverk="Yrker"
					size="xxlarge"
					isClearable={false}
				/>
				<FormikTextInput
					name={`${arbeidsavtalePath}.stillingsprosent`}
					label="Stillingsprosent"
					type="number"
				/>
				<FormikDatepicker
					name={`${arbeidsavtalePath}.endringsdatoStillingsprosent`}
					label="Endringsdato stillingsprosent"
				/>
				<FormikSelect
					name={`${arbeidsavtalePath}.arbeidstidsordning`}
					label="Arbeidstidsordning"
					kodeverk="Arbeidstidsordninger"
					size="xxlarge"
					isClearable={false}
				/>
				<FormikTextInput
					name={`${arbeidsavtalePath}.antallKonverterteTimer`}
					label="Antall konverterte timer"
					type="number"
					disabled={harArbTimerPerUke}
					title={harArbTimerPerUke ? infotekst : undefined}
				/>
				<FormikTextInput
					name={`${arbeidsavtalePath}.avtaltArbeidstimerPerUke`}
					label="Avtalte timer per uke"
					type="number"
					disabled={harKonverterteTimer}
					title={harKonverterteTimer ? infotekst : undefined}
				/>
			</div>
		</div>
	)
}
