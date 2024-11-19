import React, { useState } from 'react'
import { OrganisasjonMedArbeidsforholdSelect } from '@/components/organisasjonSelect'
import { OrgserviceApi } from '@/service/Api'
import { useBoolean } from 'react-use'
import { OrganisasjonMedMiljoeSelect } from '@/components/organisasjonSelect/OrganisasjonMedMiljoeSelect'
import {
	inputValg,
	OrganisasjonToogleGruppe,
} from '@/components/organisasjonSelect/OrganisasjonToogleGruppe'
import { EgneOrganisasjoner } from '@/components/fagsystem/brregstub/form/partials/EgneOrganisasjoner'
import { useDollyEnvironments } from '@/utils/hooks/useEnvironments'
import { useFasteDataOrganisasjon } from '@/utils/hooks/useOrganisasjoner'
import { UseFormReturn } from 'react-hook-form/dist/types'

export const ORGANISASJONSTYPE_TOGGLE = 'ORGANISASJONSTYPE_TOGGLE'

type Props = {
	formMethods: UseFormReturn
	path: string
	opplysningspliktigPath: any
}

const validEnhetstyper = ['BEDR', 'AAFY']

export const OrgnummerToggle = ({ formMethods, opplysningspliktigPath, path }: Props) => {
	const { dollyEnvironments: aktiveMiljoer } = useDollyEnvironments()

	const [inputType, setInputType] = useState(
		sessionStorage.getItem(ORGANISASJONSTYPE_TOGGLE) || inputValg.fraFellesListe,
	)
	const [success, setSuccess] = useBoolean(false)
	const [loading, setLoading] = useBoolean(false)
	const [environment, setEnvironment] = useState(null)
	const [orgnummer, setOrgnummer] = useState(formMethods.watch(path) || null)

	const { organisasjon } = useFasteDataOrganisasjon(orgnummer)

	const handleToggleChange = (value: string) => {
		setInputType(value)
		sessionStorage.setItem(ORGANISASJONSTYPE_TOGGLE, value)
		formMethods.setValue(path, '')
		formMethods.clearErrors(path)
	}

	const handleChange = (value: { juridiskEnhet: string; orgnr: string }) => {
		opplysningspliktigPath && formMethods.setValue(`${opplysningspliktigPath}`, value.juridiskEnhet)
		formMethods.trigger(opplysningspliktigPath)
		formMethods.setValue(path, value.orgnr)
	}

	const handleManualOrgChange = (org: string, miljo: string) => {
		if (!org || !miljo) {
			return
		}
		formMethods.clearErrors(path)
		setLoading(true)
		setSuccess(false)
		OrgserviceApi.getOrganisasjonInfo(org, miljo)
			.then((response: { data: { enhetType: string; juridiskEnhet: any; orgnummer: any } }) => {
				setLoading(false)
				if (!validEnhetstyper.includes(response.data.enhetType)) {
					formMethods.setError(path, { message: 'Organisasjonen må være av type BEDR eller AAFY' })
					return
				}
				if (!response.data.juridiskEnhet) {
					if (organisasjon?.overenhet) {
						opplysningspliktigPath &&
							formMethods.setValue(`${opplysningspliktigPath}`, organisasjon.overenhet)
					} else {
						formMethods.setError(path, { message: 'Organisasjonen mangler juridisk enhet' })
						return
					}
				}
				setSuccess(true)
				opplysningspliktigPath &&
					response.data.juridiskEnhet &&
					formMethods.setValue(`${opplysningspliktigPath}`, response.data.juridiskEnhet)
				formMethods.setValue(`${path}`, response.data.orgnummer)
			})
			.catch(() => {
				setLoading(false)
				formMethods.setError(path, { message: 'Fant ikke organisasjonen i ' + miljo })
			})
	}

	return (
		<div className="toggle--wrapper">
			<OrganisasjonToogleGruppe inputType={inputType} handleToggleChange={handleToggleChange} />
			{inputType === inputValg.fraFellesListe && (
				<OrganisasjonMedArbeidsforholdSelect
					afterChange={handleChange}
					path={path}
					label={'Organisasjonsnummer'}
					placeholder={'Velg organisasjon ...'}
				/>
			)}
			{inputType === inputValg.fraEgenListe && (
				<EgneOrganisasjoner
					path={path}
					formMethods={formMethods}
					filterValidEnhetstyper={true}
					// @ts-ignore
					handleChange={handleChange}
				/>
			)}
			{inputType === inputValg.skrivSelv && (
				<OrganisasjonMedMiljoeSelect
					path={path}
					environment={environment}
					miljoeOptions={aktiveMiljoer}
					loading={loading}
					success={success}
					onTextBlur={(event) => {
						const org = event.target.value
						setOrgnummer(org)
						handleManualOrgChange(org, environment)
					}}
					onMiljoeChange={(event) => {
						setEnvironment(event.value)
						handleManualOrgChange(orgnummer, event.value)
					}}
				/>
			)}
		</div>
	)
}
