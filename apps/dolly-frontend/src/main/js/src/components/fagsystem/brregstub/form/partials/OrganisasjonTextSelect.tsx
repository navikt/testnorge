import React, { useState } from 'react'
import { OrganisasjonMedMiljoeSelect } from '~/components/organisasjonSelect/OrganisasjonMedMiljoeSelect'
import { useBoolean } from 'react-use'
import { OrgserviceApi } from '~/service/Api'
import { OrgInfoAdresse } from '~/service/services/organisasjonservice/types'

interface OrgnanisasjonTextSelectProps {
	path: string
	aktiveMiljoer: string[]
	setEnhetsinfo: (org: any, path: string) => {}
	clearEnhetsinfo: () => void
}

const mapAdresse = (adresse: OrgInfoAdresse) => {
	if (!adresse) return null
	return {
		adresselinje1: adresse.adresselinje1,
		kommunenr: adresse.kommunenummer,
		landkode: adresse.landkode,
		postnr: adresse.postnummer,
		poststed: adresse.poststed,
	}
}

export const OrganisasjonTextSelect = ({
	path,
	aktiveMiljoer,
	setEnhetsinfo,
	clearEnhetsinfo,
}: OrgnanisasjonTextSelectProps) => {
	const [error, setError] = useState(null)
	const [success, setSuccess] = useBoolean(false)
	const [loading, setLoading] = useBoolean(false)
	const [environment, setEnvironment] = useState(null)
	const [orgnummer, setOrgnummer] = useState(null)

	const handleChange = (org: string, miljoe: string) => {
		if (!org || !miljoe) return
		clearEnhetsinfo()
		setError(null)
		setLoading(true)
		setSuccess(false)
		OrgserviceApi.getOrganisasjonInfo(org, miljoe)
			.then((response) => {
				const orgInfo = {
					value: response.data.orgnummer,
					orgnr: response.data.orgnummer,
					navn: response.data.navn,
					forretningsAdresse: mapAdresse(response.data.forretningsadresser),
					postAdresse: mapAdresse(response.data.postadresse),
				}
				setEnhetsinfo(orgInfo, path)
				setLoading(false)
				setSuccess(true)
			})
			.catch(() => {
				setLoading(false)
				setError('Fant ikke organisasjonen i ' + miljoe)
			})
	}

	return (
		<OrganisasjonMedMiljoeSelect
			path={path}
			environment={environment}
			miljoeOptions={aktiveMiljoer}
			error={error}
			success={success}
			loading={loading}
			onTextBlur={(event) => {
				setOrgnummer(event.target.value)
				handleChange(event.target.value, environment)
			}}
			onMiljoeChange={(event) => {
				setEnvironment(event.value)
				handleChange(orgnummer, event.value)
			}}
		/>
	)
}
