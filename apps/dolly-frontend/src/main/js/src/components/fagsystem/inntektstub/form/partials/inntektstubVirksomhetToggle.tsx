import React, { useState } from 'react'
import _get from 'lodash/get'
import { FormikProps } from 'formik'
import { FormikTextInput } from '~/components/ui/form/inputs/textInput/TextInput'
import { OrgnummerToggle } from '~/components/fagsystem/inntektstub/form/partials/orgnummerToggle'
import { ToggleGroup } from '~/components/ui/toggle/Toggle'

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

	const handleToggleChange = (value: ToggleValg) => {
		setInputType(value)
		formikBag.setFieldValue(virksomhetPath, '')
		formikBag.setFieldValue(opplysningspliktigPath, '')
	}

	return (
		<div className="toggle--wrapper">
			<ToggleGroup onChange={handleToggleChange} name={path} defaultValue={ToggleValg.ORGANISASJON}>
				<ToggleGroup.Item key={ToggleValg.ORGANISASJON} value={ToggleValg.ORGANISASJON}>
					Organisasjon
				</ToggleGroup.Item>
				<ToggleGroup.Item key={ToggleValg.PRIVAT} value={ToggleValg.PRIVAT}>
					Privat
				</ToggleGroup.Item>
			</ToggleGroup>

			{inputType === ToggleValg.ORGANISASJON ? (
				<OrgnummerToggle
					formikBag={formikBag}
					path={`${path}.virksomhet`}
					opplysningspliktigPath={`${path}.opplysningspliktig`}
				/>
			) : (
				<div className="flexbox--flex-wrap">
					<FormikTextInput name={virksomhetPath} label="Virksomhet (fnr/dnr/npid)" size="medium" />
					<FormikTextInput name={opplysningspliktigPath} label="Opplysningspliktig" size="medium" />
				</div>
			)}
		</div>
	)
}
