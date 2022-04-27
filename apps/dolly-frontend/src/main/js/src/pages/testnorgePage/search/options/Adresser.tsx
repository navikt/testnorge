import React from 'react'
import _get from 'lodash/get'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import { AdresseKodeverk } from '~/config/kodeverk'
import { RadioGroupOptions } from '~/pages/testnorgePage/search/options/RadioGroupOptions'
import { FormikProps } from 'formik'

const options = {
	boolean: [
		{ value: 'Y', label: 'Ja' },
		{ value: 'N', label: 'Nei' },
	],
	borINorge: [
		{ value: 'Y', label: 'Har norsk adresse' },
		{ value: 'N', label: 'Har utenlandsk adresse' },
	],
}

type AdresserProps = {
	formikBag: FormikProps<{}>
}

const bostedPath = 'adresser.bostedsadresse'
const kontaktPath = 'adresser.harKontaktadresse'
const oppholdPath = 'adresser.harOppholdsadresse'

export const Adresser = ({ formikBag }: AdresserProps) => {
	return (
		<section>
			<RadioGroupOptions
				formikBag={formikBag}
				name={'Bostedsadresse'}
				path={`${bostedPath}.borINorge`}
				options={options.borINorge}
				disabled={
					_get(formikBag.values, `${bostedPath}.kommunenummer`) ||
					_get(formikBag.values, `${bostedPath}.postnummer`)
				}
			/>
			<FormikSelect
				name={`${bostedPath}.postnummer`}
				label="Bosted - Postnummer"
				kodeverk={AdresseKodeverk.Postnummer}
				disabled={_get(formikBag.values, `${bostedPath}.borINorge`) === 'N'}
				optionHeight={50}
				size="medium"
			/>
			<FormikSelect
				name={`${bostedPath}.kommunenummer`}
				label="Bosted - Kommunenummer"
				kodeverk={AdresseKodeverk.Kommunenummer}
				disabled={_get(formikBag.values, `${bostedPath}.borINorge`) === 'N'}
				optionHeight={50}
				size="medium"
			/>
			<RadioGroupOptions
				formikBag={formikBag}
				name={'Har kontaktadresse'}
				path={kontaktPath}
				options={options.boolean}
			/>
			<RadioGroupOptions
				formikBag={formikBag}
				name={'Har oppholdsadresse'}
				path={oppholdPath}
				options={options.boolean}
			/>
		</section>
	)
}

export const AdresserPaths = {
	[bostedPath + '.borINorge']: 'string',
	[bostedPath + '.postnummer']: 'string',
	[bostedPath + '.kommunenummer']: 'string',
	[kontaktPath]: 'string',
	[oppholdPath]: 'string',
}
