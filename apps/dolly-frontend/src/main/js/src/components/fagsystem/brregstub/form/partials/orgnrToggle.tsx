import React, { useEffect, useState } from 'react'
import { OrganisasjonTextSelect } from '@/components/fagsystem/brregstub/form/partials/organisasjonTextSelect'
import { OrganisasjonToggleGruppe } from '@/components/organisasjonSelect/OrganisasjonToggleGruppe'
import { EgneOrganisasjoner, getEgneOrganisasjoner } from '@/utils/EgneOrganisasjoner'
import { OrganisasjonLoader } from '@/components/organisasjonSelect/OrganisasjonLoader'
import { UseFormReturn } from 'react-hook-form/dist/types'
import { useCurrentBruker } from '@/utils/hooks/useBruker'
import {
	useDollyFasteDataOrganisasjoner,
	useDollyOrganisasjoner,
} from '@/utils/hooks/useDollyOrganisasjoner'
import { ArbeidsgiverTyper } from '@/components/fagsystem/aareg/AaregTypes'
import Loading from '@/components/ui/loading/Loading'
import { getOrgType } from '@/utils/OrgUtils'

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
	const { currentBruker } = useCurrentBruker()

	const { organisasjoner: fasteOrganisasjoner, loading: fasteOrganisasjonerLoading } =
		useDollyFasteDataOrganisasjoner()

	const { organisasjoner: brukerOrganisasjoner, loading: brukerOrganisasjonerLoading } =
		useDollyOrganisasjoner(currentBruker?.brukerId)
	const egneOrganisasjoner = getEgneOrganisasjoner(brukerOrganisasjoner)

	const orgnr = formMethods.watch(`${path}.orgNr`)
	const [inputType, setInputType] = useState(
		getOrgType(orgnr, fasteOrganisasjoner, egneOrganisasjoner),
	)

	useEffect(() => {
		setInputType(getOrgType(orgnr, fasteOrganisasjoner, egneOrganisasjoner))
	}, [fasteOrganisasjoner, brukerOrganisasjoner, formMethods.watch('brregstub.enheter')?.length])

	const handleToggleChange = (value: string) => {
		formMethods.clearErrors([`${path}.orgNr`, `manual.${path}.orgNr`])
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
		formMethods.trigger(path)
	}

	const handleChange = (event: React.ChangeEvent<any>) => {
		setEnhetsinfo(event, path)
	}

	if (fasteOrganisasjonerLoading || brukerOrganisasjonerLoading) {
		return <Loading label="Laster organisasjoner ..." />
	}

	return (
		<div className="toggle--wrapper">
			<OrganisasjonToggleGruppe inputType={inputType} handleToggleChange={handleToggleChange} />
			{inputType === ArbeidsgiverTyper.felles && (
				<OrganisasjonLoader
					path={`${path}.orgNr`}
					handleChange={handleChange}
					value={formMethods.watch(`${path}.orgNr`)}
				/>
			)}
			{inputType === ArbeidsgiverTyper.egen && (
				<EgneOrganisasjoner
					path={`${path}.orgNr`}
					formMethods={formMethods}
					handleChange={handleChange}
					warningMessage={warningMessage}
				/>
			)}
			{inputType === ArbeidsgiverTyper.fritekst && (
				<OrganisasjonTextSelect path={`${path}.orgNr`} setEnhetsinfo={setEnhetsinfo} />
			)}
		</div>
	)
}
