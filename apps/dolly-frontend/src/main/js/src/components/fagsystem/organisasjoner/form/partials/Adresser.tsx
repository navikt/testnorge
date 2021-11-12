import React from 'react'
import _get from 'lodash/get'
import { Kategori } from '~/components/ui/form/kategori/Kategori'
import { FormikTextInput } from '~/components/ui/form/inputs/textInput/TextInput'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import { AdresseKodeverk } from '~/config/kodeverk'
import { FormikProps } from 'formik'
import { EnhetBestilling } from '../../types'

type AdresserProps = {
	formikBag: FormikProps<{ organisasjon: EnhetBestilling }>
	path: string
}

const hjelpetekstAdresser =
	'For å få generert en gyldig norsk adresse kan du velge å fylle ut postnummer, kommunenummer, eller ingen ting. Det vil opprettes en gyldig adresse på grunnlag av det du har fylt ut, og har du ikke fylt ut noe blir adressen en tilfeldig gyldig adresse.'

export const Adresser = ({ formikBag, path }: AdresserProps) => {
	const landForretningsadresse = _get(formikBag, `values.${path}.forretningsadresse.landkode`)
	const landPostadresse = _get(formikBag, `values.${path}.postadresse.landkode`)

	const handleLandChange = (adressePath: string) => {
		formikBag.setFieldValue(`${adressePath}.postnr`, '')
		formikBag.setFieldValue(`${adressePath}.kommunenr`, '')
		formikBag.setFieldValue(`${adressePath}.poststed`, '')
	}

	return (
		<>
			<Kategori
				title="Forretningsadresse"
				vis="organisasjon.forretningsadresse"
				hjelpetekst={hjelpetekstAdresser}
			>
				<FormikSelect
					name={`${path}.forretningsadresse.landkode`}
					label="Land"
					kodeverk={AdresseKodeverk.ArbeidOgInntektLand}
					afterChange={() => handleLandChange(`${path}.forretningsadresse`)}
					isClearable={false}
					size="large"
				/>

				{landForretningsadresse === 'NO' ? (
					<>
						<FormikSelect
							name={`${path}.forretningsadresse.postnr`}
							label={'Postnummer/sted'}
							kodeverk={AdresseKodeverk.Postnummer}
							size="large"
						/>
						<FormikSelect
							name={`${path}.forretningsadresse.kommunenr`}
							label="Kommunenummer"
							kodeverk={AdresseKodeverk.Kommunenummer}
							size="large"
						/>
					</>
				) : (
					<FormikTextInput name={`${path}.forretningsadresse.poststed`} label="Poststed" />
				)}

				<FormikTextInput
					name={`${path}.forretningsadresse.adresselinjer[0]`}
					label="Adresselinje 1"
				/>
				<FormikTextInput
					name={`${path}.forretningsadresse.adresselinjer[1]`}
					label="Adresselinje 2"
				/>
				<FormikTextInput
					name={`${path}.forretningsadresse.adresselinjer[2]`}
					label="Adresselinje 3"
				/>
			</Kategori>

			<Kategori
				title="Postadresse"
				vis="organisasjon.postadresse"
				hjelpetekst={hjelpetekstAdresser}
			>
				<FormikSelect
					name={`${path}.postadresse.landkode`}
					label="Land"
					kodeverk={AdresseKodeverk.ArbeidOgInntektLand}
					afterChange={() => handleLandChange(`${path}.postadresse`)}
					isClearable={false}
					size="large"
				/>
				{landPostadresse === 'NO' ? (
					<>
						<FormikSelect
							name={`${path}.postadresse.postnr`}
							label={'Postnummer/sted'}
							kodeverk={AdresseKodeverk.Postnummer}
							size="large"
						/>
						<FormikSelect
							name={`${path}.postadresse.kommunenr`}
							label="Kommunenummer"
							kodeverk={AdresseKodeverk.Kommunenummer}
							size="large"
						/>
					</>
				) : (
					<FormikTextInput name={`${path}.postadresse.poststed`} label="Poststed" />
				)}
				<FormikTextInput name={`${path}.postadresse.adresselinjer[0]`} label="Adresselinje 1" />
				<FormikTextInput name={`${path}.postadresse.adresselinjer[1]`} label="Adresselinje 2" />
				<FormikTextInput name={`${path}.postadresse.adresselinjer[2]`} label="Adresselinje 3" />
			</Kategori>
		</>
	)
}
