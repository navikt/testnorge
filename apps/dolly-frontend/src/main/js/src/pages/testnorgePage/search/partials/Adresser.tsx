import React from 'react'
import _get from 'lodash/get'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import { AdresseKodeverk } from '~/config/kodeverk'
import { RadioGroupOptions } from '~/pages/testnorgePage/search/radioGroupOptions/RadioGroupOptions'
import { FormikProps } from 'formik'
import { yesNoOptions } from '~/pages/testnorgePage/utils'

type AdresserProps = {
	formikBag: FormikProps<{}>
}

const bostedPath = 'adresser.bostedsadresse'
const utlandPath = 'adresser.harUtenlandskAdresse'
const kontaktPath = 'adresser.harKontaktadresse'
const oppholdPath = 'adresser.harOppholdsadresse'

export const Adresser = ({ formikBag }: AdresserProps) => {
	const bostedOptions = [
		{ value: 'Y', label: 'Ja' },
		{
			value: 'N',
			label: 'Nei',
			disabled:
				_get(formikBag.values, `${bostedPath}.kommunenummer`) ||
				_get(formikBag.values, `${bostedPath}.postnummer`),
		},
	]
	return (
		<section>
			<RadioGroupOptions
				formikBag={formikBag}
				name={'Har bostedsadresse i Norge'}
				path={`${bostedPath}.borINorge`}
				options={bostedOptions}
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
			<FormikSelect
				name={`${bostedPath}.historiskPostnummer`}
				label="Har tidligere hatt postnummer"
				kodeverk={AdresseKodeverk.Postnummer}
				optionHeight={50}
				size="medium"
				info="Velg postnummer tilknyttet tidligere bostedsadresse."
			/>
			<FormikSelect
				name={`${bostedPath}.historiskKommunenummer`}
				label="Har tidligere bodd i kommune"
				kodeverk={AdresseKodeverk.Kommunenummer}
				optionHeight={50}
				size="medium"
				info="Velg kommunenummer tilknyttet tidligere bostedsadresse."
			/>
			<RadioGroupOptions
				formikBag={formikBag}
				name={'Har utenlandsk adresse'}
				path={utlandPath}
				options={yesNoOptions}
			/>
			<RadioGroupOptions
				formikBag={formikBag}
				name={'Har kontaktadresse'}
				path={kontaktPath}
				options={yesNoOptions}
			/>
			<RadioGroupOptions
				formikBag={formikBag}
				name={'Har oppholdsadresse'}
				path={oppholdPath}
				options={yesNoOptions}
			/>
		</section>
	)
}

export const AdresserPaths = {
	[bostedPath + '.borINorge']: 'string',
	[bostedPath + '.postnummer']: 'string',
	[bostedPath + '.kommunenummer']: 'string',
	[bostedPath + '.historiskKommunenummer']: 'string',
	[bostedPath + '.historiskPostnummer']: 'string',
	[utlandPath]: 'string',
	[kontaktPath]: 'string',
	[oppholdPath]: 'string',
}
