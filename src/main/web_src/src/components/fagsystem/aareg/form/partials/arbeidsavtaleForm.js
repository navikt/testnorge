import React, { useState } from 'react'
import _get from 'lodash/get'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import { FormikDatepicker } from '~/components/ui/form/inputs/datepicker/Datepicker'
import { FormikTextInput } from '~/components/ui/form/inputs/textInput/TextInput'
import { ToggleGruppe, ToggleKnapp } from '~/components/ui/toggle/Toggle'
import Hjelpetekst from '~/components/hjelpetekst'
import { ArbeidKodeverk } from '~/config/kodeverk'

const avtaleValg = {
	avtaltArbeidstimerPerUke: 'avtaltArbeidstimerPerUke',
	antallKonverterteTimer: 'antallKonverterteTimer'
}

const initialValue = (path, formikBag) => {
	return _get(formikBag.values, `${path}.arbeidsavtale.antallKonverterteTimer`) !== ''
		? avtaleValg.antallKonverterteTimer
		: avtaleValg.avtaltArbeidstimerPerUke
}

export const ArbeidsavtaleForm = ({ formikBag, path }) => {
	const arbeidsavtalePath = `${path}.arbeidsavtale`
	const arbeidsavtale = _get(formikBag.values, arbeidsavtalePath)

	const [visning, setVisning] = useState(initialValue(path, formikBag))

	console.log('arbeidsavtalePath :>> ', arbeidsavtalePath)

	// const handleToggleChange = event => {
	// 	const { value } = event.target
	// 	setVisning(avtaleValg[value])

	// 	arbeidsavtale.avtaltArbeidstimerPerUke == '' && visning === avtaleValg.avtaltArbeidstimerPerUke
	// 		? formikBag.setFieldValue(`${path}.arbeidsavtale.antallKonverterteTimer`, '')
	// 		: formikBag.setFieldValue(`${path}.arbeidsavtale.avtaltArbeidstimerPerUke`, '')

	// 	arbeidsavtale.antallKonverterteTimer == '' && visning === avtaleValg.antallKonverterteTimer
	// 		? formikBag.setFieldValue(`${path}.arbeidsavtale.avtaltArbeidstimerPerUke`, '')
	// 		: formikBag.setFieldValue(`${path}.arbeidsavtale.antallKonverterteTimer`, '')
	// }

	// const toggleValues = [
	// 	{
	// 		value: avtaleValg.avtaltArbeidstimerPerUke,
	// 		label: 'Avtalte arbeidstimer per uke'
	// 	},
	// 	{
	// 		value: avtaleValg.antallKonverterteTimer,
	// 		label: 'Antall konverterte timer'
	// 	}
	// ]

	return (
		<div>
			<h3>Ansettelsesdetaljer</h3>
			<div className="flexbox--flex-wrap">
				<FormikSelect
					name={`${arbeidsavtalePath}.yrke`}
					label="Yrke"
					kodeverk={ArbeidKodeverk.Yrker}
					size="xxlarge"
					isClearable={false}
					optionHeight={50}
				/>
				<FormikSelect
					name={`${arbeidsavtalePath}.ansettelsesform`}
					label="Ansettelsesform"
					kodeverk={ArbeidKodeverk.AnsettelsesformAareg}
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
				<FormikDatepicker
					name={`${arbeidsavtalePath}.endringsdatoLoenn`}
					label="Endringsdato lønn"
				/>
				<FormikSelect
					name={`${arbeidsavtalePath}.arbeidstidsordning`}
					label="Arbeidstidsordning"
					kodeverk={ArbeidKodeverk.Arbeidstidsordninger}
					size="xxlarge"
					isClearable={false}
				/>
				<FormikTextInput
					name={`${arbeidsavtalePath}.avtaltArbeidstimerPerUke`}
					label="Avtalte arbeidstimer per uke"
					type="number"
					isclearable="true"
				/>
				{/* <div className="toggle--wrapper">
					<ToggleGruppe onChange={handleToggleChange} name={arbeidsavtalePath}>
						{toggleValues.map(val => (
							<ToggleKnapp key={val.value} value={val.value} checked={visning === val.value}>
								{val.label}
							</ToggleKnapp>
						))}
					</ToggleGruppe>
					<FormikTextInput
						name={
							visning === avtaleValg.avtaltArbeidstimerPerUke
								? `${arbeidsavtalePath}.avtaltArbeidstimerPerUke`
								: `${arbeidsavtalePath}.antallKonverterteTimer`
						}
						type="number"
						isclearable="true"
					/>
				</div> */}
				{/* <Hjelpetekst hjelpetekstFor="Avtalte arbeidstimer per uke og antall konverterte timer">
					Antall konverterte timer og avtalte timer per uke kan ikke bli satt samtidig. Hvis feltet
					er tomt blir det automatisk generert avhengig av stillingsprosentet.
				</Hjelpetekst> */}
			</div>
		</div>
	)
}
