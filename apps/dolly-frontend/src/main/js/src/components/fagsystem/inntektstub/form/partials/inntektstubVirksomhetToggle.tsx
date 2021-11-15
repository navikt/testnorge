import React, { useState } from 'react'
import _get from 'lodash/get'
import { FormikProps } from 'formik'
import { ToggleGruppe, ToggleKnapp } from '~/components/ui/toggle/Toggle'
import { FormikTextInput } from '~/components/ui/form/inputs/textInput/TextInput'
import { InntektstubOrgnummerSelect } from './inntektstubOrgnummerSelect'

type InntektstubVirksomhetToggleProps = {
	formikBag: FormikProps<{}>
	path: string
}

enum ToggleValg {
	ORGANISASJON = 'ORGANISASJON',
	PRIVAT = 'PRIVAT',
}

export const InntektstubVirksomhetToggle = ({
	formikBag,
	path,
}: InntektstubVirksomhetToggleProps) => {
	const virksomhetPath = `${path}.virksomhet`
	const opplysningspliktigPath = `${path}.opplysningspliktig`
	const orgnummerLength = 9

	const [inputType, setInputType] = useState(
		_get(formikBag.values, virksomhetPath)
			? _get(formikBag.values, virksomhetPath).length === orgnummerLength
				? ToggleValg.ORGANISASJON
				: ToggleValg.PRIVAT
			: ToggleValg.ORGANISASJON
	)

	const handleToggleChange = (event: React.ChangeEvent<any>) => {
		setInputType(event.target.value)
		formikBag.setFieldValue(virksomhetPath, '')
		formikBag.setFieldValue(opplysningspliktigPath, '')
	}

	return (
		<div className="toggle--wrapper">
			<ToggleGruppe onChange={handleToggleChange} name={path}>
				<ToggleKnapp
					key={ToggleValg.ORGANISASJON}
					value={ToggleValg.ORGANISASJON}
					checked={inputType === ToggleValg.ORGANISASJON}
				>
					Organisasjon
				</ToggleKnapp>
				<ToggleKnapp
					key={ToggleValg.PRIVAT}
					value={ToggleValg.PRIVAT}
					checked={inputType === ToggleValg.PRIVAT}
				>
					Privat
				</ToggleKnapp>
			</ToggleGruppe>

			{inputType === ToggleValg.ORGANISASJON ? (
				<InntektstubOrgnummerSelect path={path} formikBag={formikBag} />
			) : (
				<div className="flexbox--flex-wrap">
					<FormikTextInput name={virksomhetPath} label="Virksomhet (fnr/dnr/bost)" size="medium" />
					<FormikTextInput name={opplysningspliktigPath} label="Opplysningspliktig" size="medium" />
				</div>
			)}
		</div>
	)
}
