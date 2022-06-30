import React, { useEffect } from 'react'
// @ts-ignore
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import { DollyTextInput } from '~/components/ui/form/inputs/textInput/TextInput'
// @ts-ignore
import { PersoninformasjonKodeverk } from '~/config/kodeverk'
import { FormikProps } from 'formik'
import { FormikDollyFieldArray } from '~/components/ui/form/fieldArray/DollyFieldArray'
import { AvansertForm } from '~/components/fagsystem/pdlf/form/partials/avansert/AvansertForm'
import _get from 'lodash/get'

const initialTelefonnummer = {
	landskode: '',
	nummer: '',
	prioritet: 2,
	kilde: 'Dolly',
	master: 'PDL',
}

const initialTpsTelefonnummer = {
	landkode: '',
	telefonnummer: '',
	telefontype: 'HJET',
}

export interface TelefonnummerArray {
	person: {
		telefonnummer: Array<TelefonnummerValues>
	}
}

interface TelefonnummerValues {
	landskode?: string
	nummer?: string
}

interface TelefonnummerProps {
	formikBag: FormikProps<{ pdldata: TelefonnummerArray }>
}

export const Telefonnummer = ({ formikBag }: TelefonnummerProps) => {
	const paths = {
		pdlTelefonnummer: 'pdldata.person.telefonnummer',
		tpsMTelefonnummer: 'tpsMessaging.telefonnummer',
	}
	const tlfListe = _get(formikBag.values, paths.pdlTelefonnummer)
	const tlfListeTps = _get(formikBag.values, paths.tpsMTelefonnummer)

	if (!tlfListe) {
		return null
	}

	useEffect(() => {
		if (tlfListe.length === 1) {
			formikBag.setFieldValue(`${paths.pdlTelefonnummer}[0].prioritet`, 1)
			formikBag.setFieldValue(`${paths.tpsMTelefonnummer}[0].telefontype`, 'MOBI')
		}
	}, [tlfListe])

	const optionsPrioritet = () => {
		if (tlfListe.length === 1) {
			return [{ value: 1, label: '1' }]
		} else
			return [
				{ value: 1, label: '1' },
				{ value: 2, label: '2' },
			]
	}

	const handleNewEntry = () => {
		formikBag.setFieldValue(paths.pdlTelefonnummer, [...tlfListe, initialTelefonnummer])
		formikBag.setFieldValue(paths.tpsMTelefonnummer, [...tlfListeTps, initialTpsTelefonnummer])
	}

	const handleRemoveEntry = (idx: number) => {
		tlfListe.splice(idx, 1)
		tlfListeTps.splice(idx, 1)
		formikBag.setFieldValue(paths.pdlTelefonnummer, tlfListe)
		formikBag.setFieldValue(paths.tpsMTelefonnummer, tlfListeTps)
		formikBag.setFieldValue(`${paths.pdlTelefonnummer}[0].prioritet`, 1)
		formikBag.setFieldValue(`${paths.tpsMTelefonnummer}[0].telefontype`, 'MOBI')
	}

	const handleChangeLandkode = (value: string, path: string, idx: number) => {
		formikBag.setFieldValue(`${path}.landskode`, value)
		formikBag.setFieldValue(`${paths.tpsMTelefonnummer}[${idx}].landkode`, value)
	}

	const handleChangeNummer = (target: { value: string }, path: string, idx: number) => {
		formikBag.setFieldValue(`${path}.nummer`, target.value)
		formikBag.setFieldValue(`${paths.tpsMTelefonnummer}[${idx}].telefonnummer`, target.value)
	}

	const handleChangePrioritet = (value: number, path: string, idx: number) => {
		formikBag.setFieldValue(`${path}.prioritet`, value)
		formikBag.setFieldValue(
			`${paths.tpsMTelefonnummer}[${idx}].telefontype`,
			value === 2 ? 'HJET' : 'MOBI'
		)
	}

	return (
		<div className="flexbox--flex-wrap">
			<FormikDollyFieldArray
				name={paths.pdlTelefonnummer}
				header="Telefonnummer"
				newEntry={initialTelefonnummer}
				canBeEmpty={false}
				handleNewEntry={handleNewEntry}
				handleRemoveEntry={handleRemoveEntry}
				maxEntries={2}
				maxReachedDescription={'En person kan maksimalt ha to telefonnumre'}
			>
				{(path: string, idx: number) => (
					<>
						<FormikSelect
							name={`${path}.landskode`}
							label="Landkode"
							kodeverk={PersoninformasjonKodeverk.Retningsnumre}
							onChange={({ value }: { value: string }) => handleChangeLandkode(value, path, idx)}
							size="large"
							isClearable={false}
						/>
						<DollyTextInput
							name={`${path}.nummer`}
							label="Telefonnummer"
							onChange={({ target }: { target: { value: string } }) =>
								handleChangeNummer(target, path, idx)
							}
							value={_get(formikBag.values, `${path}.nummer`)}
							/*@ts-ignore*/
							size="large"
							feil={
								_get(formikBag.errors, `${path}.nummer`)
									? { feilmelding: _get(formikBag.errors, `${path}.nummer`) }
									: null
							}
						/>
						<FormikSelect
							name={`${path}.prioritet`}
							label="Prioritet"
							options={optionsPrioritet()}
							onChange={({ value }: { value: number }) => handleChangePrioritet(value, path, idx)}
							size="xsmall"
							isClearable={false}
						/>
						<AvansertForm path={path} kanVelgeMaster={false} />
					</>
				)}
			</FormikDollyFieldArray>
		</div>
	)
}
