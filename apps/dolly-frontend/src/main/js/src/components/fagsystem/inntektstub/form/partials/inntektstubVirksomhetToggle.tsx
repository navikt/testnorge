import React, { useEffect, useState } from 'react'
import { FormTextInput } from '@/components/ui/form/inputs/textInput/TextInput'
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

	const getInputType = () =>
		!formMethods.watch(virksomhetPath) ||
		formMethods.watch(virksomhetPath).length === orgnummerLength
			? ToggleValg.ORGANISASJON
			: ToggleValg.PRIVAT

	const [inputType, setInputType] = useState(getInputType())

	const handleToggleChange = (value: ToggleValg) => {
		setInputType(value)
		formMethods.setValue(virksomhetPath, '')
		formMethods.setValue(opplysningspliktigPath, '')
	}

	// console.log(
	// 	"formMethods.watch('inntektstub.inntektsinformasjon'): ",
	// 	formMethods.watch('inntektstub.inntektsinformasjon'),
	// ) //TODO - SLETT MEG

	useEffect(() => {
		// console.log('Setter input type... ): ', getInputType()) //TODO - SLETT MEG
		// setTimeout(() => setInputType(getInputType()), 1000)
		setInputType(getInputType())
		// formMethods.trigger(path)
	}, [formMethods.watch('inntektstub.inntektsinformasjon')?.length])
	// console.log('inputType: ', inputType) //TODO - SLETT MEG

	// TODO: Ingen toggles funker som de skal når man sletter inntektsinformasjon.
	// TODO: Men alle under-arrayer ser ut til å fungere som de skal.

	return (
		<div className="toggle--wrapper">
			<ToggleGroup
				size={'small'}
				onChange={handleToggleChange}
				// defaultValue={ToggleValg.ORGANISASJON}
				value={inputType}
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
					<FormTextInput name={virksomhetPath} label="Virksomhet (fnr/dnr/npid)" size="medium" />
					<FormTextInput name={opplysningspliktigPath} label="Opplysningspliktig" size="medium" />
				</div>
			)}
		</div>
	)
}
