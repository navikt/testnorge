import React, { useState } from 'react'
import { OrganisasjonTextSelect } from '@/components/fagsystem/brregstub/form/partials/organisasjonTextSelect'
import {
	inputValg,
	OrganisasjonToogleGruppe,
} from '@/components/organisasjonSelect/OrganisasjonToogleGruppe'
import { EgneOrganisasjoner } from '@/components/fagsystem/brregstub/form/partials/EgneOrganisasjoner'
import { useDollyEnvironments } from '@/utils/hooks/useEnvironments'
import { OrganisasjonLoader } from '@/components/organisasjonSelect/OrganisasjonLoader'
import { UseFormReturn } from 'react-hook-form/dist/types'

interface OrgnrToggleProps {
	path: string
	formMethods: UseFormReturn
	setEnhetsinfo: (org: any, path: string) => Record<string, unknown>
	warningMessage?: any
}

export const OrgnrToggle = ({
	path,
	formMethods,
	setEnhetsinfo,
	warningMessage,
}: OrgnrToggleProps) => {
	const [inputType, setInputType] = useState(inputValg.fraFellesListe)
	const { dollyEnvironments: aktiveMiljoer } = useDollyEnvironments()

	const handleToggleChange = (value: string) => {
		setInputType(value)
		clearEnhetsinfo()
	}

	const clearEnhetsinfo = () => {
		const oldValues = formMethods.watch(path) || {}
		if (oldValues.hasOwnProperty('foretaksNavn')) {
			delete oldValues['foretaksNavn']
		}
		if (oldValues.hasOwnProperty('forretningsAdresse')) {
			delete oldValues['forretningsAdresse']
		}
		if (oldValues.hasOwnProperty('postAdresse')) {
			delete oldValues['postAdresse']
		}
		oldValues['orgNr'] = null
		formMethods.setValue(path, oldValues)
	}

	const handleChange = (event: React.ChangeEvent<any>) => {
		setEnhetsinfo(event, path)
	}

	return (
		<div className="toggle--wrapper">
			<OrganisasjonToogleGruppe inputType={inputType} handleToggleChange={handleToggleChange} />
			{inputType === inputValg.fraFellesListe && (
				<OrganisasjonLoader
					path={`${path}.orgNr`}
					handleChange={handleChange}
					value={formMethods.watch(`${path}.orgNr`)}
				/>
			)}
			{inputType === inputValg.fraEgenListe && (
				<EgneOrganisasjoner
					path={`${path}.orgNr`}
					formMethods={formMethods}
					handleChange={handleChange}
					warningMessage={warningMessage}
				/>
			)}
			{inputType === inputValg.skrivSelv && (
				<OrganisasjonTextSelect
					path={path}
					aktiveMiljoer={aktiveMiljoer}
					setEnhetsinfo={setEnhetsinfo}
					clearEnhetsinfo={clearEnhetsinfo}
				/>
			)}
		</div>
	)
}
