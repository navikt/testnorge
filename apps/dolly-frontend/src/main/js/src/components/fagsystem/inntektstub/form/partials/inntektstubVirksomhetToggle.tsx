import React, { useState } from 'react'
import * as _ from 'lodash-es'
import { FormikTextInput } from '@/components/ui/form/inputs/textInput/TextInput'
import { OrgnummerToggle } from '@/components/fagsystem/inntektstub/form/partials/orgnummerToggle'
import { ToggleGroup } from '@navikt/ds-react'
import { UseFormReturn } from 'react-hook-form/dist/types'

type InntektstubVirksomhetToggleProps = {
	formMethods: UseFormReturn
	path: string
}

enum ToggleValg {
	ORGANISASJON = 'ORGANISASJON',
	PRIVAT = 'PRIVAT',
}

export const InntektstubVirksomhetToggle = ({
	formMethods,
	path,
}: InntektstubVirksomhetToggleProps) => {
	const virksomhetPath = `${path}.virksomhet`
	const opplysningspliktigPath = `${path}.opplysningspliktig`
	const orgnummerLength = 9

	const [inputType, setInputType] = useState(
		_.get(formMethods.getValues(), virksomhetPath)
			? _.get(formMethods.getValues(), virksomhetPath).length === orgnummerLength
				? ToggleValg.ORGANISASJON
				: ToggleValg.PRIVAT
			: ToggleValg.ORGANISASJON,
	)

	const handleToggleChange = (value: ToggleValg) => {
		setInputType(value)
		formMethods.setValue(virksomhetPath, '')
		formMethods.setValue(opplysningspliktigPath, '')
	}

	return (
		<div className="toggle--wrapper">
			<ToggleGroup
				size={'small'}
				onChange={handleToggleChange}
				defaultValue={ToggleValg.ORGANISASJON}
				style={{ backgroundColor: '#ffffff' }}
			>
				<ToggleGroup.Item key={ToggleValg.ORGANISASJON} value={ToggleValg.ORGANISASJON}>
					Organisasjon
				</ToggleGroup.Item>
				<ToggleGroup.Item key={ToggleValg.PRIVAT} value={ToggleValg.PRIVAT}>
					Privat
				</ToggleGroup.Item>
			</ToggleGroup>

			{inputType === ToggleValg.ORGANISASJON ? (
				<OrgnummerToggle
					formMethods={formMethods}
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
