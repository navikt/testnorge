import React, { useEffect, useState } from 'react'
import { OrganisasjonMedArbeidsforholdSelect } from '~/components/organisasjonSelect'
import { MiljoeApi, OrgforvalterApi, OrgserviceApi } from '~/service/Api'
import { useBoolean } from 'react-use'
import { OrganisasjonMedMiljoeSelect } from '~/components/organisasjonSelect/OrganisasjonMedMiljoeSelect'
import {
	OrganisasjonToogleGruppe,
	inputValg,
} from '~/components/organisasjonSelect/OrganisasjonToogleGruppe'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'

// const getJuridiskEnhet = (orgnr, organisasjoner) => {
// 	for (const org in organisasjoner) {
// 		for (const underenhet in org.underenheter) {
// 			if (underenhet.organisasjonsnummer === orgnr) {
// 				return org.organisasjonsnummer
// 			}
// 		}
// 	}
// 	return ''
// }

const validEnhetstyper = ['BEDR', 'AAFY']

export const OrgnummerToggle = ({ formikBag, path, opplysningspliktigPath }) => {
	const [inputType, setInputType] = useState(inputValg.fraFellesListe)
	const [error, setError] = useState(null)
	const [success, setSuccess] = useBoolean(false)
	const [loading, setLoading] = useBoolean(false)
	const [aktiveMiljoer, setAktiveMiljoer] = useState(null)
	const [environment, setEnvironment] = useState(null)
	const [orgnummer, setOrgnummer] = useState(null)
	const [egneOrganisasjoner, setEgneOrganisasjoner] = useState([])

	useEffect(() => {
		const fetchData = async () => {
			setAktiveMiljoer(await MiljoeApi.getAktiveMiljoer())
		}
		// const fetchEgneOrg = async () => {
		// 	const resp = await OrgforvalterApi.getAlleOrganisasjonerPaaBruker()
		// 		.then((response) => {
		// 			if (!response || response.length === 0) return []
		// 			console.log('response: ', response) //TODO - SLETT MEG
		// 			return response
		// 				.filter((virksomhet) => validEnhetstyper.includes(virksomhet.enhetstype))
		// 				.map((virksomhet) => ({
		// 					value: virksomhet.organisasjonsnummer,
		// 					label: `${virksomhet.organisasjonsnummer} (${virksomhet.enhetstype}) - ${virksomhet.organisasjonsnavn}`,
		// 					orgnr: virksomhet.organisasjonsnummer,
		// 					juridiskEnhet: getJuridiskEnhet(virksomhet.organisasjonsnummer, response),
		// 				}))
		// 		})
		// 		.catch((e) => {
		// 			return []
		// 		})
		// 	setEgneOrganisasjoner(resp)
		// }
		fetchData()
		// fetchEgneOrg()
	}, [])

	const handleToggleChange = (event) => {
		setInputType(event.target.value)
		formikBag.setFieldValue(path, '')
	}

	const handleChange = (value) => {
		opplysningspliktigPath &&
			formikBag.setFieldValue(`${opplysningspliktigPath}`, value.juridiskEnhet)
		formikBag.setFieldValue(`${path}`, value.orgnr)
	}

	const handleManualOrgChange = (org, miljo) => {
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
				<FormikSelect
					name={path}
					label="Organisasjonsnummer"
					size="xlarge"
					options={egneOrganisasjoner}
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
