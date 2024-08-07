import React, { useState } from 'react'
import { ToggleGroup } from '@navikt/ds-react'
import { OrgnummerToggle } from '@/components/fagsystem/inntektstub/form/partials/orgnummerToggle'
import { FormTextInput } from '@/components/ui/form/inputs/textInput/TextInput'
import { Kategori } from '@/components/ui/form/kategori/Kategori'

enum ToggleValg {
	ORGANISASJON = 'ORGANISASJON',
	PRIVAT = 'PRIVAT',
}

export const ArbeidsgiverToggle = ({ formMethods, path }) => {
	const organisasjonPath = `${path}.organisasjonsnummer`
	const personPath = `${path}.personidentifikator`
	const orgnummerLength = 9

	const [inputType, setInputType] = useState(
		!formMethods.watch(organisasjonPath) ||
			formMethods.watch(organisasjonPath).length === orgnummerLength
			? ToggleValg.ORGANISASJON
			: ToggleValg.PRIVAT,
	)

	const handleToggleChange = (value: ToggleValg) => {
		setInputType(value)
		if (value === ToggleValg.ORGANISASJON) {
			formMethods.setValue(organisasjonPath, '')
			formMethods.setValue(personPath, undefined)
		} else if (value === ToggleValg.PRIVAT) {
			formMethods.setValue(personPath, '')
			formMethods.setValue(organisasjonPath, undefined)
		}
	}

	return (
		<Kategori title="Arbeidsgiver">
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
						path={organisasjonPath}
						opplysningspliktigPath={null}
					/>
				) : (
					<div className="flexbox--flex-wrap" style={{ marginTop: '5px' }}>
						<FormTextInput name={personPath} label="Personidentifikator" size="medium" />
					</div>
				)}
			</div>
		</Kategori>
	)
}
