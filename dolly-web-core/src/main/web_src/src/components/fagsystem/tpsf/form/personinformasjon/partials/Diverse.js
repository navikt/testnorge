import React from 'react'
import Kodeverk from '~/utils/kodeverkMapper'
import { Vis } from '~/components/bestillingsveileder/VisAttributt'
import { FormikDatepicker } from '~/components/ui/form/inputs/datepicker/Datepicker'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import { FormikCheckbox } from '~/components/ui/form/inputs/checbox/Checkbox'
import { SelectOptionsManager as Options } from '~/service/SelectOptions'
import { Diskresjonskoder } from './diskresjonskoder/Diskresjonskoder'

export const Diverse = ({ formikBag }) => {
	const handleChangeKontonr = selected => {
		if (!selected) {
			formikBag.setFieldValue(`tpsf.bankkontonrRegdato`, null)
		}
	}

	return (
		<React.Fragment>
			<FormikSelect name="tpsf.kjonn" label="Kjønn" kodeverk="Kjønnstyper" visHvisAvhuket />

			<FormikSelect
				name="tpsf.harMellomnavn"
				label="Har mellomnavn"
				options={Options('boolean')}
				visHvisAvhuket
			/>

			<FormikSelect
				name="tpsf.sprakKode"
				label="Språk"
				kodeverk={Kodeverk.sprak}
				size="large"
				visHvisAvhuket
			/>

			<FormikDatepicker name="tpsf.egenAnsattDatoFom" label="Egenansatt fra" visHvisAvhuket />

			<Vis attributt="tpsf.erForsvunnet">
				<FormikSelect name="tpsf.erForsvunnet" label="Er forsvunnet" options={Options('boolean')} />

				<FormikDatepicker
					name="tpsf.forsvunnetDato"
					label="Forsvunnet dato"
					disabled={!formikBag.values.tpsf.erForsvunnet}
					fastfield={false}
				/>
			</Vis>
			<Vis attributt="tpsf.harBankkontonr">
				<FormikCheckbox
					name="tpsf.harBankkontonr"
					label="Har bankkontonummer"
					afterChange={handleChangeKontonr}
					checkboxMargin
				/>
				<FormikDatepicker
					name="tpsf.bankkontonrRegdato"
					label="Bankkonto opprettet"
					disabled={!formikBag.values.tpsf.harBankkontonr}
					fastfield={false}
				/>
			</Vis>
			<Diskresjonskoder basePath="tpsf" formikBag={formikBag} />
		</React.Fragment>
	)
}
