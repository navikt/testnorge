import React from 'react'
import _get from 'lodash/get'
import _omit from 'lodash/omit'
import { Vis } from '~/components/bestillingsveileder/VisAttributt'
import Panel from '~/components/ui/panel/Panel'
import { erForste, panelError } from '~/components/ui/form/formUtils'
import { Kategori } from '~/components/ui/form/kategori/Kategori'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import { FormikTextInput } from '~/components/ui/form/inputs/textInput/TextInput'
import { FormikDollyFieldArray } from '~/components/ui/form/fieldArray/DollyFieldArray'
import { SelectOptionsManager as Options } from '~/service/SelectOptions'
import { OrganisasjonKodeverk, AdresseKodeverk } from '~/config/kodeverk'
import Button from '~/components/ui/button/Button'

const organisasjonPaths = [
	'organisasjon.enhetstype',
	'organisasjon.naeringskode',
	'organisasjon.formaal'
]

const kontaktPaths = ['organisasjon.telefon', 'organisasjon.epost', 'organisasjon.nettadresse']

const adressePaths = ['organisasjon.forretningsadresse', 'organisasjon.postadresse']

const detaljerPaths = [organisasjonPaths, kontaktPaths, adressePaths].flat()

export const Detaljer = ({ formikBag, path, level, number }) => {
	console.log('path :>> ', path)
	const initialValues = _omit(formikBag.values.organisasjon, 'underenheter')
	const landForretningsadresse = _get(formikBag, `values.${path}.forretningsadresse.landkode`)
	const landPostadresse = _get(formikBag, `values.${path}.postadresse.landkode`)

	const handleLandChange = adressePath => {
		formikBag.setFieldValue(`${adressePath}.postnr`, '')
		formikBag.setFieldValue(`${adressePath}.kommunenr`, '')
		formikBag.setFieldValue(`${adressePath}.poststed`, '')
	}

	return (
		<>
			<Kategori title={!number ? 'Organisasjon' : null} vis={organisasjonPaths}>
				<FormikSelect
					name={`${path}.enhetstype`}
					label="Enhetstype"
					kodeverk={
						number
							? OrganisasjonKodeverk.EnhetstyperVirksomhet
							: OrganisasjonKodeverk.EnhetstyperJuridiskEnhet
					}
					size="xxlarge"
				/>
				<FormikSelect
					name={`${path}.naeringskode`}
					label="Næringskode"
					kodeverk={OrganisasjonKodeverk.Næringskoder}
					size="xlarge"
					optionHeight={50}
					visHvisAvhuket
				/>
				<FormikTextInput name={`${path}.formaal`} label="Formål" size="xlarge" />
			</Kategori>

			<Kategori title="Kontaktdata" vis={kontaktPaths}>
				<FormikTextInput name={`${path}.telefon`} label="Telefon" size="large" type="number" />
				<FormikTextInput name={`${path}.epost`} label="E-postadresse" size="large" />
				<FormikTextInput name={`${path}.nettadresse`} label="Internettadresse" size="large" />
			</Kategori>

			<Kategori title="Forretningsadresse" vis="organisasjon.forretningsadresse">
				<FormikSelect
					name={`${path}.forretningsadresse.landkode`}
					label="Land"
					kodeverk={AdresseKodeverk.PostadresseLand}
					afterChange={() => handleLandChange(`${path}.forretningsadresse`)}
					isClearable={false}
					size="large"
				/>

				{landForretningsadresse === 'NOR' ? (
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
					name={`${path}.forretningsadresse.adresselinje[0]`}
					label="Adresselinje 1"
				/>
				<FormikTextInput
					name={`${path}.forretningsadresse.adresselinje[1]`}
					label="Adresselinje 2"
				/>
				<FormikTextInput
					name={`${path}.forretningsadresse.adresselinje[2]`}
					label="Adresselinje 3"
				/>
			</Kategori>

			<Kategori title="Postadresse" vis="organisasjon.postadresse">
				<FormikSelect
					name={`${path}.postadresse.landkode`}
					label="Land"
					kodeverk={AdresseKodeverk.PostadresseLand}
					afterChange={() => handleLandChange(`${path}.postadresse`)}
					isClearable={false}
					size="large"
				/>
				{landPostadresse === 'NOR' ? (
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
				<FormikTextInput name={`${path}.postadresse.adresselinje[0]`} label="Adresselinje 1" />
				<FormikTextInput name={`${path}.postadresse.adresselinje[1]`} label="Adresselinje 2" />
				<FormikTextInput name={`${path}.postadresse.adresselinje[2]`} label="Adresselinje 3" />
			</Kategori>

			<FormikDollyFieldArray
				name={`${path}.underenheter`}
				header="Underenhet"
				newEntry={initialValues}
				isFull={level > 3}
				title={level > 3 ? 'Du kan maksimalt lage fire nivåer av underenheter' : null}
				tag={number}
			>
				{(path, idx, curr, number) => {
					return (
						<React.Fragment key={idx}>
							<Detaljer
								formikBag={formikBag}
								path={path}
								level={level + 1}
								number={number ? number : (level + 1).toString()}
							/>
						</React.Fragment>
					)
				}}
			</FormikDollyFieldArray>
		</>
	)
}
