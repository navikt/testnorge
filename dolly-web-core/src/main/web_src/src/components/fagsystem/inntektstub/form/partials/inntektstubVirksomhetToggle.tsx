import React, { useState } from 'react'
import _get from 'lodash/get'
import { FormikProps } from 'formik'
import { ToggleGruppe, ToggleKnapp } from '~/components/ui/toggle/Toggle'
import { FormikTextInput } from '~/components/ui/form/inputs/textInput/TextInput'
import { InntektstubOrgnummerSelect } from './inntektstubOrgnummerSelect'

type InntektstubVirksomhetToggle = {
	formikBag: FormikProps<{}>
	path: string
}

const inputValg = { organisasjon: 'velg', enkeltmannsforetak: 'skriv' }

export const InntektstubVirksomhetToggle = ({ formikBag, path }: InntektstubVirksomhetToggle) => {
	const virksomhetPath = `${path}.virksomhet`
	const opplysningspliktigPath = `${path}.opplysningspliktig`
	const orgnummerLength = 9

	const [inputType, setInputType] = useState(
		_get(formikBag.values, virksomhetPath)
			? _get(formikBag.values, virksomhetPath).length === orgnummerLength
				? inputValg.organisasjon
				: inputValg.enkeltmannsforetak
			: inputValg.organisasjon
	)

	const handleToggleChange = (event: React.ChangeEvent<HTMLInputElement>) => {
		setInputType(event.target.value)
		formikBag.setFieldValue(virksomhetPath, '')
		formikBag.setFieldValue(opplysningspliktigPath, '')
	}

	const setOpplysningspliktig = () => {
		formikBag.setFieldValue(opplysningspliktigPath, _get(formikBag.values, virksomhetPath))
	}

	return (
		<div className="toggle--wrapper">
			<ToggleGruppe onChange={handleToggleChange} name={path}>
				<ToggleKnapp
					key={inputValg.organisasjon}
					value={inputValg.organisasjon}
					checked={inputType === inputValg.organisasjon}
				>
					Organisasjon
				</ToggleKnapp>
				<ToggleKnapp
					key={inputValg.enkeltmannsforetak}
					value={inputValg.enkeltmannsforetak}
					checked={inputType === inputValg.enkeltmannsforetak}
				>
					Enkeltmannsforetak
				</ToggleKnapp>
			</ToggleGruppe>

			{inputType === inputValg.organisasjon ? (
				<InntektstubOrgnummerSelect path={path} formikBag={formikBag} />
			) : (
				<FormikTextInput
					name={virksomhetPath}
					label="Virksomhet (personident)"
					size="xlarge"
					onBlur={setOpplysningspliktig}
				/>
			)}
		</div>
	)
}
