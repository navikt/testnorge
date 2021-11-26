import React from 'react'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import { FormikDatepicker } from '~/components/ui/form/inputs/datepicker/Datepicker'
import { FormikTextInput } from '~/components/ui/form/inputs/textInput/TextInput'
import { ArbeidKodeverk } from '~/config/kodeverk'

export const ArbeidsavtaleForm = ({ path, onChangeLenket }) => {
	return (
		<div>
			<h3>Ansettelsesdetaljer</h3>
			<div className="flexbox--flex-wrap">
				<FormikSelect
					name={`${path}.yrke`}
					label="Yrke"
					kodeverk={ArbeidKodeverk.Yrker}
					size="xxxlarge"
					isClearable={false}
					optionHeight={50}
					onChange={onChangeLenket('arbeidsavtale.yrke')}
				/>
				<FormikSelect
					name={`${path}.ansettelsesform`}
					label="Ansettelsesform"
					kodeverk={ArbeidKodeverk.AnsettelsesformAareg}
					onChange={onChangeLenket('arbeidsavtale.ansettelsesform')}
					isClearable={false}
				/>
				<FormikTextInput
					key={`${path}.stillingsprosent`}
					name={`${path}.stillingsprosent`}
					label="Stillingsprosent"
					type="number"
					onBlur={onChangeLenket('arbeidsavtale.stillingsprosent')}
				/>
				<FormikDatepicker
					name={`${path}.endringsdatoStillingsprosent`}
					label="Endringsdato stillingsprosent"
					onChange={onChangeLenket('arbeidsavtale.endringsdatoStillingsprosent')}
					fastfield={false}
				/>
				<FormikDatepicker
					name={`${path}.sisteLoennsendringsdato`}
					label="Endringsdato lønn"
					onChange={onChangeLenket('arbeidsavtale.sisteLoennsendringsdato')}
					fastfield={false}
				/>
				<FormikSelect
					name={`${path}.arbeidstidsordning`}
					label="Arbeidstidsordning"
					kodeverk={ArbeidKodeverk.Arbeidstidsordninger}
					size="xxlarge"
					isClearable={false}
					onChange={onChangeLenket('arbeidsavtale.arbeidstidsordning')}
				/>
				<FormikTextInput
					key={`${path}.avtaltArbeidstimerPerUke`}
					name={`${path}.avtaltArbeidstimerPerUke`}
					label="Avtalte arbeidstimer per uke"
					type="number"
					onBlur={onChangeLenket('arbeidsavtale.avtaltArbeidstimerPerUke')}
				/>
			</div>
		</div>
	)
}
