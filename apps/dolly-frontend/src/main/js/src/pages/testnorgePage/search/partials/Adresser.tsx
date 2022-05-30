import React from 'react'
import _get from 'lodash/get'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import { AdresseKodeverk, GtKodeverk } from '~/config/kodeverk'
import { RadioGroupOptions } from '~/pages/testnorgePage/search/radioGroupOptions/RadioGroupOptions'
import { FormikProps } from 'formik'
import { yesNoOptions } from '~/pages/testnorgePage/utils'
import Button from '~/components/ui/button/Button'
import useBoolean from '~/utils/hooks/useBoolean'

type AdresserProps = {
	formikBag: FormikProps<{}>
}

const bostedPath = 'adresser.bostedsadresse'
const utlandPath = 'adresser.harUtenlandskAdresse'
const kontaktPath = 'adresser.harKontaktadresse'
const oppholdPath = 'adresser.harOppholdsadresse'

export const Adresser = ({ formikBag }: AdresserProps) => {
	const [visAvansert, setVisAvansert, setSkjulAvansert] = useBoolean(false)

	const bostedOptions = [
		{ value: 'Y', label: 'Ja' },
		{
			value: 'N',
			label: 'Nei',
			disabled:
				_get(formikBag.values, `${bostedPath}.kommunenummer`) ||
				_get(formikBag.values, `${bostedPath}.postnummer`) ||
				_get(formikBag.values, `${bostedPath}.bydelsnummer`),
		},
	]
	return (
		<section>
			<div className="options-title-bold">Bostedsadresse</div>
			<RadioGroupOptions
				formikBag={formikBag}
				name={'Har bostedsadresse i Norge'}
				path={`${bostedPath}.borINorge`}
				options={bostedOptions}
			/>
			<FormikSelect
				name={`${bostedPath}.postnummer`}
				label="Postnummer"
				kodeverk={AdresseKodeverk.Postnummer}
				disabled={_get(formikBag.values, `${bostedPath}.borINorge`) === 'N'}
				optionHeight={50}
				size="medium"
			/>
			<FormikSelect
				name={`${bostedPath}.bydelsnummer`}
				label="Bydel"
				kodeverk={GtKodeverk.BYDEL}
				disabled={_get(formikBag.values, `${bostedPath}.borINorge`) === 'N'}
				optionHeight={50}
				size="medium"
			/>
			<FormikSelect
				name={`${bostedPath}.kommunenummer`}
				label="Kommunenummer"
				kodeverk={AdresseKodeverk.Kommunenummer}
				disabled={_get(formikBag.values, `${bostedPath}.borINorge`) === 'N'}
				optionHeight={50}
				size="medium"
			/>
			{visAvansert ? (
				<Button onClick={setSkjulAvansert} kind={'collapse'}>
					SKJUL AVANSERTE VALG
				</Button>
			) : (
				<Button onClick={setVisAvansert} kind={'expand'}>
					VIS AVANSERTE VALG
				</Button>
			)}
			{visAvansert && (
				<>
					<FormikSelect
						name={`${bostedPath}.historiskPostnummer`}
						label="Har tidligere hatt postnummer"
						kodeverk={AdresseKodeverk.Postnummer}
						optionHeight={50}
						size="medium"
						info="Velg postnummer tilknyttet tidligere bostedsadresse."
					/>
					<FormikSelect
						name={`${bostedPath}.historiskBydelsnummer`}
						label="Har tidligere bodd i bydel"
						kodeverk={GtKodeverk.BYDEL}
						optionHeight={50}
						size="medium"
						info="Velg bydel tilknyttet tidligere bostedsadresse."
					/>
					<FormikSelect
						name={`${bostedPath}.historiskKommunenummer`}
						label="Har tidligere bodd i kommune"
						kodeverk={AdresseKodeverk.Kommunenummer}
						optionHeight={50}
						size="medium"
						info="Velg kommunenummer tilknyttet tidligere bostedsadresse."
					/>
				</>
			)}
			<div className="options-title-bold">Annet</div>
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
	[bostedPath + '.bydelsnummer']: 'string',
	[bostedPath + '.kommunenummer']: 'string',
	[bostedPath + '.historiskPostnummer']: 'string',
	[bostedPath + '.historiskBydelsnummer']: 'string',
	[bostedPath + '.historiskKommunenummer']: 'string',
	[utlandPath]: 'string',
	[kontaktPath]: 'string',
	[oppholdPath]: 'string',
}
