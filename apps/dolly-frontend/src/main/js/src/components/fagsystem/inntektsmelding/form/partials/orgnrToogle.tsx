import React, { useState } from 'react'
import { OrganisasjonMedArbeidsforholdSelect } from '@/components/organisasjonSelect'
import { FormTextInput } from '@/components/ui/form/inputs/textInput/TextInput'
import {
	inputValg,
	OrganisasjonToogleGruppe,
} from '@/components/organisasjonSelect/OrganisasjonToogleGruppe'
import { EgneOrganisasjoner } from '@/components/fagsystem/brregstub/form/partials/EgneOrganisasjoner'
import { UseFormReturn } from 'react-hook-form/dist/types'
import { ORGANISASJONSTYPE_TOGGLE } from '@/components/fagsystem/utils'

interface OrgnrToggleProps {
	path: string
	formMethods: UseFormReturn
	label?: string
}

export const OrgnrToggle = ({
	path,
	formMethods,
	label = 'Arbeidsgiver (orgnr)',
}: OrgnrToggleProps) => {
	const [inputType, setInputType] = useState(
		sessionStorage.getItem(ORGANISASJONSTYPE_TOGGLE) || inputValg.fraFellesListe,
	)

	const handleToggleChange = (value: string) => {
		sessionStorage.setItem(ORGANISASJONSTYPE_TOGGLE, value)
		setInputType(value)
		formMethods.setValue(path, '')
	}

	const handleChangeEgne = (value: { orgnr: string }) => {
		formMethods.setValue(`${path}`, value.orgnr)
	}

	return (
		<div className="toggle--wrapper">
			<OrganisasjonToogleGruppe
				path={path}
				inputType={inputType}
				handleToggleChange={handleToggleChange}
				style={{ margin: '5px 0 5px' }}
			/>
			{inputType === inputValg.fraFellesListe && (
				<OrganisasjonMedArbeidsforholdSelect
					path={path}
					label={label}
					//@ts-ignore
					isClearable={false}
					placeholder={'Velg organisasjon ...'}
				/>
			)}
			{inputType === inputValg.fraEgenListe && (
				<EgneOrganisasjoner
					path={path}
					label={label}
					formMethods={formMethods}
					filterValidEnhetstyper={true}
					// @ts-ignore
					handleChange={handleChangeEgne}
				/>
			)}
			{inputType === inputValg.skrivSelv && (
				<FormTextInput type="number" name={path} label={label} size="xlarge" />
			)}
		</div>
	)
}
