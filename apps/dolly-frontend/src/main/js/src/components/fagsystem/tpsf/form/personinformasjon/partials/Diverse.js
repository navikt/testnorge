import React, { useContext } from 'react'
import { isAfter } from 'date-fns'
import { PersoninformasjonKodeverk } from '~/config/kodeverk'
import { BestillingsveilederContext } from '~/components/bestillingsveileder/Bestillingsveileder'
import { Vis } from '~/components/bestillingsveileder/VisAttributt'
import { FormikDatepicker } from '~/components/ui/form/inputs/datepicker/Datepicker'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import { FormikCheckbox } from '~/components/ui/form/inputs/checbox/Checkbox'
import { SelectOptionsManager as Options } from '~/service/SelectOptions'
import { Diskresjonskoder } from './diskresjonskoder/Diskresjonskoder'
import { Telefonnummer } from './telefonnummer/Telefonnummer'

export const Diverse = ({ formikBag }) => {
	const handleChangeKontonr = selected => {
		if (!selected) {
			formikBag.setFieldValue(`tpsf.bankkontonrRegdato`, null)
		}
	}
	const opts = useContext(BestillingsveilederContext)
	const { personFoerLeggTil } = opts
	const harSkjerming = personFoerLeggTil
		? personFoerLeggTil.tpsf.hasOwnProperty('egenAnsattDatoFom')
			? personFoerLeggTil.tpsf.hasOwnProperty('egenAnsattDatoTom')
				? isAfter(new Date(personFoerLeggTil.tpsf.egenAnsattDatoTom), new Date())
				: true
			: false
		: false

	return (
		<React.Fragment>
			<FormikSelect
				name="tpsf.identtype"
				label="Identtype"
				options={Options('identtype')}
				visHvisAvhuket
				isClearable={false}
			/>

			<FormikSelect
				name="tpsf.kjonn"
				label="Kjønn"
				kodeverk={PersoninformasjonKodeverk.Kjoennstyper}
				visHvisAvhuket
			/>

			<FormikSelect
				name="tpsf.harMellomnavn"
				label="Har mellomnavn"
				options={Options('boolean')}
				visHvisAvhuket
			/>

			<FormikSelect
				name="tpsf.sprakKode"
				label="Språk"
				kodeverk={PersoninformasjonKodeverk.Spraak}
				size="large"
				visHvisAvhuket
			/>

			<FormikDatepicker
				name="tpsf.egenAnsattDatoFom"
				label="Skjerming fra"
				disabled={harSkjerming}
				visHvisAvhuket
			/>
			{harSkjerming && (
				<FormikDatepicker name="tpsf.egenAnsattDatoTom" label="Skjerming til" visHvisAvhuket />
			)}

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
			<Telefonnummer formikBag={formikBag} />
			<Diskresjonskoder basePath="tpsf" formikBag={formikBag} />
		</React.Fragment>
	)
}
