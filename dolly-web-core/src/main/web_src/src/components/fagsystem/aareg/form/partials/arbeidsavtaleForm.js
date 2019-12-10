import React from 'react'
import _get from 'lodash/get'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import { FormikDatepicker } from '~/components/ui/form/inputs/datepicker/Datepicker'
import { FormikTextInput } from '~/components/ui/form/inputs/textInput/TextInput'

export const ArbeidsavtaleForm = ({ formikBag, idx }) => {
	const arbeidsavtale = formikBag.values.aareg[idx].arbeidsavtale

	return (
		<div>
			<h4>Arbeidsavtale</h4>
			<div className="flexbox--flex-wrap">
				<FormikSelect
					name={`aareg[${idx}].arbeidsavtale.yrke`}
					label="Yrke"
					kodeverk="Yrker"
					size="xxlarge"
				/>
				<FormikTextInput
					name={`aareg[${idx}].arbeidsavtale.stillingsprosent`}
					label="Stillingsprosent"
					type="number"
				/>
				<FormikDatepicker
					name={`aareg[${idx}].arbeidsavtale.endringsdatoStillingsprosent`}
					label="Endringsdato stillingsprosent"
				/>
				<FormikSelect
					name={`aareg[${idx}].arbeidsavtale.arbeidstidsordning`}
					label="Arbeidstidsordning"
					kodeverk="Arbeidstidsordninger"
					size="xxlarge"
				/>
				<FormikTextInput
					name={`aareg[${idx}].arbeidsavtale.antallKonverterteTimer`}
					label="Antall konverterte timer"
					type="number"
					// Kun to av feltene (stillingsprosent, antall konverterte timer, avtalte timer per uke) kan settes pr arbeidsforhold
					disabled={
						(arbeidsavtale.stillingsprosent || arbeidsavtale.stillingsprosent === 0) &&
						(arbeidsavtale.avtaltArbeidstimerPerUke || arbeidsavtale.avtaltArbeidstimerPerUke === 0)
					}
					title={
						(arbeidsavtale.stillingsprosent || arbeidsavtale.stillingsprosent === 0) &&
						(arbeidsavtale.avtaltArbeidstimerPerUke || arbeidsavtale.avtaltArbeidstimerPerUke === 0)
							? 'Antall konverterte timer og avtalte timer per uke kan ikke være satt samtidig.'
							: undefined
					}
				/>
				<FormikTextInput
					name={`aareg[${idx}].arbeidsavtale.avtaltArbeidstimerPerUke`}
					label="Avtalte timer per uke"
					type="number"
					// Kun to av feltene (stillingsprosent, antall konverterte timer, avtalte timer per uke) kan settes pr arbeidsforhold
					disabled={
						(arbeidsavtale.stillingsprosent || arbeidsavtale.stillingsprosent === 0) &&
						(arbeidsavtale.antallKonverterteTimer || arbeidsavtale.antallKonverterteTimer === 0)
					}
					title={
						(arbeidsavtale.stillingsprosent || arbeidsavtale.stillingsprosent === 0) &&
						(arbeidsavtale.antallKonverterteTimer || arbeidsavtale.antallKonverterteTimer === 0)
							? 'Antall konverterte timer og avtalte timer per uke kan ikke være satt samtidig.'
							: undefined
					}
				/>
			</div>
		</div>
	)
}
