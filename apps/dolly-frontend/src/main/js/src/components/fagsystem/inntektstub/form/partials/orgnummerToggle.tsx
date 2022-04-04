import React, { useEffect, useState } from 'react'
import { OrganisasjonMedArbeidsforholdSelect } from '~/components/organisasjonSelect'
import { MiljoeApi, OrgserviceApi } from '~/service/Api'
import { useBoolean } from 'react-use'
import { OrganisasjonMedMiljoeSelect } from '~/components/organisasjonSelect/OrganisasjonMedMiljoeSelect'
import {
	inputValg,
	OrganisasjonToogleGruppe,
} from '~/components/organisasjonSelect/OrganisasjonToogleGruppe'
import { FormikProps } from 'formik'
import EgneOrganisasjonerConnector from '~/components/fagsystem/brregstub/form/partials/EgneOrganisasjonerConnector'

type Props = {
	formikBag: FormikProps<{}>
	path: string
	opplysningspliktigPath: any
}

const validEnhetstyper = ['BEDR', 'AAFY']

export const OrgnummerToggle = ({ formikBag, opplysningspliktigPath, path }: Props) => {
	const [inputType, setInputType] = useState(inputValg.fraFellesListe)
	const [error, setError] = useState(null)
	const [success, setSuccess] = useBoolean(false)
	const [loading, setLoading] = useBoolean(false)
	const [aktiveMiljoer, setAktiveMiljoer] = useState(null)
	const [environment, setEnvironment] = useState(null)
	const [orgnummer, setOrgnummer] = useState(null)

	useEffect(() => {
		const fetchData = async () => {
			setAktiveMiljoer(await MiljoeApi.getAktiveMiljoer())
		}
		fetchData()
	}, [])

	const handleToggleChange = (event: React.ChangeEvent<any>) => {
		setInputType(event.target.value)
		formikBag.setFieldValue(path, '')
	}

	const handleChange = (value: { juridiskEnhet: string; orgnr: string }) => {
		opplysningspliktigPath &&
			formikBag.setFieldValue(`${opplysningspliktigPath}`, value.juridiskEnhet)
		formikBag.setFieldValue(`${path}`, value.orgnr)
	}

	const handleManualOrgChange = (org: string, miljo: string) => {
		if (!org || !miljo) return
		formikBag.setFieldValue(path, '')
		setError(null)
		setLoading(true)
		setSuccess(false)
		OrgserviceApi.getOrganisasjonInfo(org, miljo)
			.then((response) => {
				setLoading(false)
				if (!validEnhetstyper.includes(response.data.enhetType)) {
					setError('Organisasjonen må være av type BEDR eller AAFY')
					return
				}
				setSuccess(true)
				opplysningspliktigPath &&
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
			<OrganisasjonToogleGruppe
				path={path}
				inputType={inputType}
				handleToggleChange={handleToggleChange}
			/>
			{inputType === inputValg.fraFellesListe && (
				<OrganisasjonMedArbeidsforholdSelect
					afterChange={handleChange}
					path={path}
					label={'Organisasjonsnummer'}
				/>
			)}
			{inputType === inputValg.fraEgenListe && (
				<EgneOrganisasjonerConnector
					path={path}
					formikBag={formikBag}
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
