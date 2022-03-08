import React, { useState } from 'react'
import { OrganisasjonMedMiljoeSelect } from '~/components/organisasjonSelect/OrganisasjonMedMiljoeSelect'
import { useBoolean } from 'react-use'
import { OrgserviceApi } from '~/service/Api'
import {
	OrgInfoResponse,
	OrgInfoAdresse,
} from '~/components/fagsystem/brregstub/form/partials/types'

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

const sortMiljoer = (miljoer: string[]) => {
	return miljoer.sort((a, b) =>
		a.localeCompare(b, undefined, {
			numeric: true,
			sensitivity: 'base',
		})
	)
}

export const OrganisasjonTextSelect = ({
	path,
	aktiveMiljoer,
	setEnhetsinfo,
	clearEnhetsinfo,
}: OrgnanisasjonTextSelectProps) => {
	const [error, setError] = useState(null)
	const [success, setSuccess] = useBoolean(false)
	const [environment, setEnvironment] = useState(null)
	const [orgnummer, setOrgnummer] = useState(null)

	const handleChange = (org: string, miljoe: string) => {
		if (!org || !miljoe) return
		clearEnhetsinfo()
		setError(null)
		setSuccess(false)
		OrgserviceApi.getOrganisasjonInfo(org, miljoe)
			.then((response: OrgInfoResponse) => {
				const orgInfo = {
					value: response.data.orgnummer,
					orgnr: response.data.orgnummer,
					navn: response.data.navn,
					forretningsAdresse: mapAdresse(response.data.forretningsadresser),
					postAdresse: mapAdresse(response.data.postadresse),
				}
				setEnhetsinfo(orgInfo, path)
				setSuccess(true)
			})
			.catch(() => setError('Fant ikke organisasjonen i ' + miljoe))
	}

	return (
		<OrganisasjonMedMiljoeSelect
			path={path}
			environment={environment}
			miljoeOptions={
				aktiveMiljoer &&
				sortMiljoer(aktiveMiljoer).map((value) => ({
					value: value,
					label: value.toUpperCase(),
				}))
			}
			error={error}
			success={success}
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
