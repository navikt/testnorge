import React from 'react'
import _get from 'lodash/get'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import { FormikDatepicker } from '~/components/ui/form/inputs/datepicker/Datepicker'
import { FormikTextInput } from '~/components/ui/form/inputs/textInput/TextInput'

export const ArbeidsavtaleForm = ({ formikBag, idx }) => {
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
				{/* Skal være number +- ting */}
				<FormikTextInput
					name={`aareg[${idx}].arbeidsavtale.stillingsprosent`}
					label="Stillingsprosent"
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
				{/* Skal være number +- ting */}
				<FormikTextInput
					name={`aareg[${idx}].arbeidsavtale.antallKonverterteTimer`}
					label="Antall konverterte timer"
				/>
				<FormikTextInput
					name={`aareg[${idx}].arbeidsavtale.avtaltArbeidstimerPerUke`}
					label="Avtalte timer per uke"
				/>
			</div>
		</div>
	)
}
