import React, { useEffect } from 'react'
import { FormikSelect } from '@/components/ui/form/inputs/select/Select'
import { DollyTextInput, FormikTextInput } from '@/components/ui/form/inputs/textInput/TextInput'
import { PersoninformasjonKodeverk } from '@/config/kodeverk'
import { FormikDollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import { AvansertForm } from '@/components/fagsystem/pdlf/form/partials/avansert/AvansertForm'
import * as _ from 'lodash'
import {
	initialTelefonnummer,
	initialTpsTelefonnummer,
} from '@/components/fagsystem/pdlf/form/initialValues'
import styled from 'styled-components'
import { UseFormReturn } from 'react-hook-form/dist/types'

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
	formMethods: UseFormReturn
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

export const TelefonnummerForm = ({ path, formMethods, idx }: TelefonnummerProps) => {
	const tlfListe = _.get(formMethods.getValues(), path || 'pdldata.person.telefonnummer')

	useEffect(() => {
		if (tlfListe && tlfListe.length === 1) {
			formMethods.setValue(`${paths.pdlTelefonnummer}[0].prioritet`, 1)
			formMethods.setValue(`${paths.tpsMTelefonnummer}[0].telefontype`, 'MOBI')
			formMethods.trigger()
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
		formMethods.setValue(`${path}.landskode`, value)
		formMethods.setValue(`${paths.tpsMTelefonnummer}[${idx}].landkode`, value)
		formMethods.trigger()
	}

	const handleChangeNummer = (target: { value: string }) => {
		formMethods.setValue(`${path}.nummer`, target.value)
		formMethods.setValue(`${paths.tpsMTelefonnummer}[${idx}].telefonnummer`, target.value)
		formMethods.trigger()
	}

	const handleChangePrioritet = (value: number) => {
		formMethods.setValue(`${path}.prioritet`, value)
		formMethods.setValue(
			`${paths.tpsMTelefonnummer}[${idx}].telefontype`,
			value === 2 ? 'HJET' : 'MOBI',
		)
		formMethods.trigger()
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
				value={_.get(formMethods.getValues(), `${path}.nummer`)}
				/*@ts-ignore*/
				size="large"
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

export const Telefonnummer = ({ formMethods, path }: TelefonnummerProps) => {
	const tlfListe = _.get(formMethods.getValues(), path || paths.pdlTelefonnummer)
	const tlfListeTps = _.get(formMethods.getValues(), path || paths.tpsMTelefonnummer)

	if (!tlfListe) {
		return null
	}

	const handleNewEntry = () => {
		formMethods.setValue(paths.pdlTelefonnummer, [...tlfListe, initialTelefonnummer])
		formMethods.setValue(paths.tpsMTelefonnummer, [...tlfListeTps, initialTpsTelefonnummer])
		formMethods.trigger()
	}

	const handleRemoveEntry = (idx: number) => {
		tlfListe.splice(idx, 1)
		tlfListeTps.splice(idx, 1)
		formMethods.setValue(paths.pdlTelefonnummer, tlfListe)
		formMethods.setValue(paths.tpsMTelefonnummer, tlfListeTps)
		formMethods.setValue(`${paths.pdlTelefonnummer}[0].prioritet`, 1)
		formMethods.setValue(`${paths.tpsMTelefonnummer}[0].telefontype`, 'MOBI')
		formMethods.trigger()
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
					<TelefonnummerForm path={tlfPath} formMethods={formMethods} idx={idx} />
				)}
			</FormikDollyFieldArray>
		</div>
	)
}
