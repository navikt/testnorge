import React, { useState } from 'react'
import _get from 'lodash/get'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import { FormikDatepicker } from '~/components/ui/form/inputs/datepicker/Datepicker'
import { FormikTextInput } from '~/components/ui/form/inputs/textInput/TextInput'
import { ToggleGruppe, ToggleKnapp } from '~/components/ui/toggle/Toggle'
import Hjelpetekst from '~/components/hjelpetekst'
import { ArbeidKodeverk } from '~/config/kodeverk'

export const ArbeidsavtaleForm = ({ formikBag, path }) => {
	const arbeidsavtalePath = `${path}.arbeidsavtale`
	const arbeidsavtale = _get(formikBag.values, arbeidsavtalePath)

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
			</div>
		</div>
	)
}
