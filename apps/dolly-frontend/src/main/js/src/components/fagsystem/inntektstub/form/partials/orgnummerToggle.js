import React, { useEffect, useState } from 'react'
import { ToggleGruppe, ToggleKnapp } from '~/components/ui/toggle/Toggle'
import { OrganisasjonMedArbeidsforholdSelect } from '~/components/organisasjonSelect'
import { MiljoeApi, OrgserviceApi } from '~/service/Api'
import { useBoolean } from 'react-use'
import { OrganisasjonMedMiljoeSelect } from '~/components/organisasjonSelect/OrganisasjonMedMiljoeSelect'

const inputValg = { fraListe: 'velg', skrivSelv: 'skriv' }

export const OrgnummerToggle = ({ formikBag, path, opplysningspliktigPath }) => {
	const [inputType, setInputType] = useState(inputValg.fraListe)
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
		setError(null)
		setLoading(true)
		setSuccess(false)
		OrgserviceApi.getOrganisasjonInfo(org, miljo)
			.then((response) => {
				setLoading(false)
				if (
					!response.data.enhetType.includes('BEDR') &&
					!response.data.enhetType.includes('AAFY')
				) {
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
			<ToggleGruppe onChange={handleToggleChange} name={path}>
				<ToggleKnapp
					key={inputValg.fraListe}
					value={inputValg.fraListe}
					checked={inputType === inputValg.fraListe}
				>
					Velg organisasjonsnummer
				</ToggleKnapp>
				<ToggleKnapp
					key={inputValg.skrivSelv}
					value={inputValg.skrivSelv}
					checked={inputType === inputValg.skrivSelv}
				>
					Skriv inn organisasjonsnummer
				</ToggleKnapp>
			</ToggleGruppe>

			{inputType === inputValg.fraListe ? (
				<OrganisasjonMedArbeidsforholdSelect
					afterChange={handleChange}
					path={path}
					label={'Organisasjonsnummer'}
				/>
			) : (
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
