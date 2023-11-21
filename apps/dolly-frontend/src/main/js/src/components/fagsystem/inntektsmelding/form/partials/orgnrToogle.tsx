import React, { useState } from 'react'
import { OrganisasjonMedArbeidsforholdSelect } from '@/components/organisasjonSelect'
import { FormikTextInput } from '@/components/ui/form/inputs/textInput/TextInput'
import {
	inputValg,
	OrganisasjonToogleGruppe,
} from '@/components/organisasjonSelect/OrganisasjonToogleGruppe'
import { EgneOrganisasjoner } from '@/components/fagsystem/brregstub/form/partials/EgneOrganisasjoner'
import { UseFormReturn } from 'react-hook-form/dist/types'

interface OrgnrToggleProps {
	path: string
	formMethods: UseFormReturn
}

export const OrgnrToggle = ({ path, formMethods }: OrgnrToggleProps) => {
	const [inputType, setInputType] = useState(inputValg.fraFellesListe)

	const handleToggleChange = (value: string) => {
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
					label="Arbeidsgiver (orgnr)"
					//@ts-ignore
					isClearable={false}
				/>
			)}
			{inputType === inputValg.fraEgenListe && (
				<EgneOrganisasjoner
					path={path}
					label="Arbeidsgiver (orgnr)"
					formMethods={formMethods}
					filterValidEnhetstyper={true}
					// @ts-ignore
					handleChange={handleChangeEgne}
				/>
			)}
			{inputType === inputValg.skrivSelv && (
				<FormikTextInput type="number" name={path} label="Arbeidsgiver (orgnr)" size="xlarge" />
			)}
		</div>
	)
}
