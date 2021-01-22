import React, { useState } from 'react'
import _get from 'lodash/get'
import _has from 'lodash/has'
import _omit from 'lodash/omit'
import { organisasjonPaths, kontaktPaths } from './paths'
import { Kategori } from '~/components/ui/form/kategori/Kategori'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import { FormikTextInput } from '~/components/ui/form/inputs/textInput/TextInput'
import { FormikDollyFieldArray } from '~/components/ui/form/fieldArray/DollyFieldArray'
import { OrganisasjonKodeverk, AdresseKodeverk } from '~/config/kodeverk'
import { ToggleGruppe, ToggleKnapp } from '~/components/ui/toggle/Toggle'
import { FormikProps } from 'formik'
import { EnhetBestilling } from '../types'

type Detaljer = {
	formikBag: FormikProps<{ organisasjon: EnhetBestilling }>
	path: string
	level: number
	number?: string
}

enum TypeUnderenhet {
	JURIDISKENHET = 'JURIDISKENHET',
	VIRKSOMHET = 'VIRKSOMHET'
}

export const Detaljer = ({ formikBag, path, level, number }: Detaljer) => {
	const initialValues = _omit(formikBag.values.organisasjon, 'underenheter')
	initialValues.enhetstype = ''

	if (level === 0 && !_get(formikBag, `values.${path}.underenheter`)) {
		formikBag.setFieldValue(`${path}.underenheter`, [initialValues])
	}

	const landForretningsadresse = _get(formikBag, `values.${path}.forretningsadresse.landkode`)
	const landPostadresse = _get(formikBag, `values.${path}.postadresse.landkode`)

	const handleLandChange = (adressePath: string) => {
		formikBag.setFieldValue(`${adressePath}.postnr`, '')
		formikBag.setFieldValue(`${adressePath}.kommunenr`, '')
		formikBag.setFieldValue(`${adressePath}.poststed`, '')
	}

	const [typeUnderenhet, setTypeUnderenhet] = useState(
		level === 0 ||
			(_has(formikBag.values, `${path}.underenheter`) &&
				_get(formikBag.values, `${path}.underenheter`))
			? TypeUnderenhet.JURIDISKENHET
			: TypeUnderenhet.VIRKSOMHET
	)

	const handleToggleChange = (event: React.ChangeEvent<any>) => {
		setTypeUnderenhet(event.target.value)
		formikBag.setFieldValue(`${path}.enhetstype`, '')
		if (event.target.value === TypeUnderenhet.VIRKSOMHET) {
			formikBag.setFieldValue(`${path}.underenheter`, [])
		} else if (event.target.value === TypeUnderenhet.JURIDISKENHET && level < 4) {
			formikBag.setFieldValue(`${path}.underenheter`, [initialValues])
		}
	}

	return (
		<>
			<Kategori title={!number ? 'Organisasjon' : null} vis={organisasjonPaths}>
				<div className="toggle--wrapper">
					{level > 0 && (
						<ToggleGruppe onChange={handleToggleChange} name={path}>
							<ToggleKnapp
								key={TypeUnderenhet.JURIDISKENHET}
								value={TypeUnderenhet.JURIDISKENHET}
								checked={typeUnderenhet === TypeUnderenhet.JURIDISKENHET}
							>
								Juridisk enhet
							</ToggleKnapp>
							<ToggleKnapp
								key={TypeUnderenhet.VIRKSOMHET}
								value={TypeUnderenhet.VIRKSOMHET}
								checked={typeUnderenhet === TypeUnderenhet.VIRKSOMHET}
							>
								Virksomhet
							</ToggleKnapp>
						</ToggleGruppe>
					)}
					<FormikSelect
						name={`${path}.enhetstype`}
						label="Enhetstype"
						kodeverk={
							typeUnderenhet === TypeUnderenhet.JURIDISKENHET
								? OrganisasjonKodeverk.EnhetstyperJuridiskEnhet
								: OrganisasjonKodeverk.EnhetstyperVirksomhet
						}
						size="xxlarge"
						isClearable={false}
					/>
				</div>
				<FormikSelect
					name={`${path}.naeringskode`}
					label="Næringskode"
					kodeverk={OrganisasjonKodeverk.Næringskoder}
					size="xlarge"
					optionHeight={50}
					isClearable={false}
					visHvisAvhuket
				/>
				<FormikTextInput name={`${path}.formaal`} label="Formål" size="xlarge" />
			</Kategori>

			<Kategori title="Kontaktdata" vis={kontaktPaths}>
				<FormikTextInput name={`${path}.telefon`} label="Telefon" size="large" type="number" />
				<FormikTextInput name={`${path}.epost`} label="E-postadresse" size="large" />
				<FormikTextInput name={`${path}.nettside`} label="Internettadresse" size="large" />
			</Kategori>

			<Kategori title="Forretningsadresse" vis="organisasjon.forretningsadresse">
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

			<Kategori title="Postadresse" vis="organisasjon.postadresse">
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

			<FormikDollyFieldArray
				name={`${path}.underenheter`}
				header="Underenhet"
				newEntry={initialValues}
				disabled={level > 3 || typeUnderenhet === TypeUnderenhet.VIRKSOMHET}
				title={
					level > 3
						? 'Du kan maksimalt lage fire nivåer av underenheter'
						: typeUnderenhet === TypeUnderenhet.VIRKSOMHET
						? 'Du kan ikke legge til underenheter på enhet av type virksomhet'
						: null
				}
				tag={number}
				isOrganisasjon={true}
			>
				{(path: string, idx: number, curr: any, number: string) => {
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
