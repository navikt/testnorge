import React, { useEffect } from 'react'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import { DollyTextInput, FormikTextInput } from '~/components/ui/form/inputs/textInput/TextInput'
import { PersoninformasjonKodeverk } from '~/config/kodeverk'
import { FormikProps } from 'formik'
import { FormikDollyFieldArray } from '~/components/ui/form/fieldArray/DollyFieldArray'
import { AvansertForm } from '~/components/fagsystem/pdlf/form/partials/avansert/AvansertForm'
import _get from 'lodash/get'
import {
	initialTelefonnummer,
	initialTpsTelefonnummer,
} from '~/components/fagsystem/pdlf/form/initialValues'

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

type TelefonnummerFormTypes = {
	path: string
	formikBag: FormikProps<{}>
	idx?: number
}

export const TelefonnummerFormRedigering = ({ path }: TelefonnummerFormTypes) => {
	return (
		<>
			<FormikSelect
				name={`${path}.landskode`}
				label="Landkode"
				kodeverk={PersoninformasjonKodeverk.Retningsnumre}
				size="large"
				isClearable={false}
			/>
			<FormikTextInput name={`${path}.nummer`} label="Telefonnummer" size="large" />
			<FormikSelect
				name={`${path}.prioritet`}
				label="Prioritet"
				options={[
					{ value: 1, label: '1' },
					{ value: 2, label: '2' },
				]}
				size="xsmall"
				isClearable={false}
			/>
			<AvansertForm path={path} kanVelgeMaster={false} />
		</>
	)
}

export const TelefonnummerForm = ({ path, formikBag, idx }: TelefonnummerFormTypes) => {
	const tlfListe = _get(formikBag.values, 'pdldata.person.telefonnummer')
	if (!tlfListe) return null

	useEffect(() => {
		if (tlfListe.length === 1) {
			formikBag.setFieldValue('pdldata.person.telefonnummer[0].prioritet', 1)
			formikBag.setFieldValue('tpsMessaging.telefonnummer[0].telefontype', 'MOBI')
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

	const handleChangeLandkode = (value: string, path: string, idx: number) => {
		formikBag.setFieldValue(`${path}.landskode`, value)
		formikBag.setFieldValue(`tpsMessaging.telefonnummer[${idx}].landkode`, value)
	}

	const handleChangeNummer = (target: { value: string }, path: string, idx: number) => {
		formikBag.setFieldValue(`${path}.nummer`, target.value)
		formikBag.setFieldValue(`tpsMessaging.telefonnummer[${idx}].telefonnummer`, target.value)
	}

	const handleChangePrioritet = (value: number, path: string, idx: number) => {
		formikBag.setFieldValue(`${path}.prioritet`, value)
		formikBag.setFieldValue(
			`tpsMessaging.telefonnummer[${idx}].telefontype`,
			value === 2 ? 'HJET' : 'MOBI'
		)
	}

	return (
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
	)
}

export const Telefonnummer = ({ formikBag }: TelefonnummerProps) => {
	const tlfListe = _get(formikBag.values, 'pdldata.person.telefonnummer')
	const tlfListeTps = _get(formikBag.values, 'tpsMessaging.telefonnummer')

	if (!tlfListe) return null

	const handleNewEntry = () => {
		formikBag.setFieldValue('pdldata.person.telefonnummer', [...tlfListe, initialTelefonnummer])
		formikBag.setFieldValue('tpsMessaging.telefonnummer', [...tlfListeTps, initialTpsTelefonnummer])
	}

	const handleRemoveEntry = (idx: number) => {
		tlfListe.splice(idx, 1)
		tlfListeTps.splice(idx, 1)
		formikBag.setFieldValue('pdldata.person.telefonnummer', tlfListe)
		formikBag.setFieldValue('tpsMessaging.telefonnummer', tlfListeTps)
		formikBag.setFieldValue('pdldata.person.telefonnummer[0].prioritet', 1)
		formikBag.setFieldValue('tpsMessaging.telefonnummer[0].telefontype', 'MOBI')
	}

	return (
		<div className="flexbox--flex-wrap">
			<FormikDollyFieldArray
				name={'pdldata.person.telefonnummer'}
				header="Telefonnummer"
				newEntry={initialTelefonnummer}
				canBeEmpty={false}
				handleNewEntry={handleNewEntry}
				handleRemoveEntry={handleRemoveEntry}
				maxEntries={2}
				maxReachedDescription={'En person kan maksimalt ha to telefonnumre'}
			>
				{(path: string, idx: number) => (
					<TelefonnummerForm path={path} formikBag={formikBag} idx={idx} />
				)}
			</FormikDollyFieldArray>
		</div>
	)
}
