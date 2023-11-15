import React, { useState } from 'react'
import { OrganisasjonMedArbeidsforholdSelect } from '@/components/organisasjonSelect'
import { OrgserviceApi } from '@/service/Api'
import { useBoolean } from 'react-use'
import { OrganisasjonMedMiljoeSelect } from '@/components/organisasjonSelect/OrganisasjonMedMiljoeSelect'
import {
	inputValg,
	OrganisasjonToogleGruppe,
} from '@/components/organisasjonSelect/OrganisasjonToogleGruppe'
import { FormikProps } from 'formik'
import { EgneOrganisasjoner } from '@/components/fagsystem/brregstub/form/partials/EgneOrganisasjoner'
import { useDollyEnvironments } from '@/utils/hooks/useEnvironments'
import { useFasteDataOrganisasjon } from '@/utils/hooks/useOrganisasjoner'

export const ORGANISASJONSTYPE_TOGGLE = 'ORGANISASJONSTYPE_TOGGLE'

type Props = {
	formikBag: FormikProps<{}>
	path: string
	opplysningspliktigPath: any
}

const validEnhetstyper = ['BEDR', 'AAFY']

export const OrgnummerToggle = ({ formMethods, opplysningspliktigPath, path }: Props) => {
	const { dollyEnvironments: aktiveMiljoer } = useDollyEnvironments()

	const [inputType, setInputType] = useState(
		sessionStorage.getItem(ORGANISASJONSTYPE_TOGGLE) || inputValg.fraFellesListe,
	)
	const [error, setError] = useState(null)
	const [success, setSuccess] = useBoolean(false)
	const [loading, setLoading] = useBoolean(false)
	const [environment, setEnvironment] = useState(null)
	const [orgnummer, setOrgnummer] = useState(null)

	const { organisasjon } = useFasteDataOrganisasjon(orgnummer)

	const handleToggleChange = (value: string) => {
		setInputType(value)
		sessionStorage.setItem(ORGANISASJONSTYPE_TOGGLE, value)
		formikBag.setFieldValue(path, '')
	}

	const handleChange = (value: { juridiskEnhet: string; orgnr: string }) => {
		opplysningspliktigPath &&
			formikBag.setFieldValue(`${opplysningspliktigPath}`, value.juridiskEnhet)
		formikBag.setFieldValue(`${path}`, value.orgnr)
	}

	const handleManualOrgChange = (org: string, miljo: string) => {
		if (!org || !miljo) {
			return
		}
		formikBag.setFieldValue(path, '')
		setError(null)
		setLoading(true)
		setSuccess(false)
		OrgserviceApi.getOrganisasjonInfo(org, miljo)
			.then((response: { data: { enhetType: string; juridiskEnhet: any; orgnummer: any } }) => {
				setLoading(false)
				if (!validEnhetstyper.includes(response.data.enhetType)) {
					setError('Organisasjonen må være av type BEDR eller AAFY')
					return
				}
				if (!response.data.juridiskEnhet) {
					if (organisasjon?.overenhet) {
						opplysningspliktigPath &&
							formikBag.setFieldValue(`${opplysningspliktigPath}`, organisasjon.overenhet)
					} else {
						setError('Organisasjonen mangler juridisk enhet')
						return
					}
				}
				setSuccess(true)
				opplysningspliktigPath &&
					response.data.juridiskEnhet &&
					formikBag.setFieldValue(`${opplysningspliktigPath}`, response.data.juridiskEnhet)
				formikBag.setFieldValue(`${path}`, response.data.orgnummer)
			})
			.catch(() => {
				setLoading(false)
				setError('Fant ikke organisasjonen i ' + miljo)
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
					error={error}
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
