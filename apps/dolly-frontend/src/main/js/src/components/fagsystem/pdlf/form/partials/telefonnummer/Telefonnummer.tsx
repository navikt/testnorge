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
import styled from 'styled-components'

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
	formikBag?: FormikProps<{}>
	path: string
	idx?: number
}

const StyledAvansert = styled.div`
	&&& {
		button {
			position: inherit;
		}
	}
`

const paths = {
	pdlTelefonnummer: 'pdldata.person.telefonnummer',
	tpsMTelefonnummer: 'tpsMessaging.telefonnummer',
}

export const TelefonnummerFormRedigering = ({ path }: TelefonnummerProps) => {
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
			<StyledAvansert>
				<AvansertForm path={path} kanVelgeMaster={false} />
			</StyledAvansert>
		</>
	)
}

export const TelefonnummerForm = ({ path, formikBag, idx }: TelefonnummerProps) => {
	const tlfListe = _get(formikBag.values, path || 'pdldata.person.telefonnummer')

	useEffect(() => {
		if (tlfListe && tlfListe.length === 1) {
			formikBag.setFieldValue(`${paths.pdlTelefonnummer}[0].prioritet`, 1)
			formikBag.setFieldValue(`${paths.tpsMTelefonnummer}[0].telefontype`, 'MOBI')
		}
	}, [tlfListe])

	if (!tlfListe) {
		return null
	}

	const optionsPrioritet = () => {
		if (tlfListe.length === 1) {
			return [{ value: 1, label: '1' }]
		} else {
			return [
				{ value: 1, label: '1' },
				{ value: 2, label: '2' },
			]
		}
	}

	const handleChangeLandkode = (value: string) => {
		formikBag.setFieldValue(`${path}.landskode`, value)
		formikBag.setFieldValue(`${paths.tpsMTelefonnummer}[${idx}].landkode`, value)
	}

	const handleChangeNummer = (target: { value: string }) => {
		formikBag.setFieldValue(`${path}.nummer`, target.value)
		formikBag.setFieldValue(`${paths.tpsMTelefonnummer}[${idx}].telefonnummer`, target.value)
	}

	const handleChangePrioritet = (value: number) => {
		formikBag.setFieldValue(`${path}.prioritet`, value)
		formikBag.setFieldValue(
			`${paths.tpsMTelefonnummer}[${idx}].telefontype`,
			value === 2 ? 'HJET' : 'MOBI'
		)
	}

	return (
		<>
			<FormikSelect
				name={`${path}.landskode`}
				label="Landkode"
				kodeverk={PersoninformasjonKodeverk.Retningsnumre}
				onChange={({ value }: { value: string }) => handleChangeLandkode(value)}
				size="large"
				isClearable={false}
			/>
			<DollyTextInput
				name={`${path}.nummer`}
				label="Telefonnummer"
				onChange={({ target }: { target: { value: string } }) => handleChangeNummer(target)}
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
				onChange={({ value }: { value: number }) => handleChangePrioritet(value)}
				size="xsmall"
				isClearable={false}
			/>
			<AvansertForm path={path} kanVelgeMaster={false} />
		</>
	)
}

export const Telefonnummer = ({ formikBag, path }: TelefonnummerProps) => {
	const tlfListe = _get(formikBag.values, path || paths.pdlTelefonnummer)
	const tlfListeTps = _get(formikBag.values, path || paths.tpsMTelefonnummer)

	if (!tlfListe) {
		return null
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

	return (
		<div className="flexbox--flex-wrap">
			<FormikDollyFieldArray
				name={path || paths.pdlTelefonnummer}
				header="Telefonnummer"
				newEntry={initialTelefonnummer}
				canBeEmpty={false}
				handleNewEntry={handleNewEntry}
				handleRemoveEntry={handleRemoveEntry}
				maxEntries={2}
				maxReachedDescription={'En person kan maksimalt ha to telefonnumre'}
			>
				{(tlfPath: string, idx: number) => (
					<TelefonnummerForm path={tlfPath} formikBag={formikBag} idx={idx} />
				)}
			</FormikDollyFieldArray>
		</div>
	)
}
