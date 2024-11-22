import React, { useState } from 'react'
import { OrganisasjonMedMiljoeSelect } from '@/components/organisasjonSelect/OrganisasjonMedMiljoeSelect'
import { useBoolean } from 'react-use'
import { OrgserviceApi } from '@/service/Api'
import { OrgInfoAdresse } from '@/service/services/organisasjonservice/types'
import _ from 'lodash'
import { useFormContext } from 'react-hook-form'

interface OrgnanisasjonTextSelectProps {
	path: string
	aktiveMiljoer: string[]
	setEnhetsinfo: (org: any, path: string) => {}
	clearEnhetsinfo: () => void
}

const mapAdresse = (adresse: OrgInfoAdresse) => {
	if (!adresse) {
		return null
	}
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
}: OrgnanisasjonTextSelectProps) => {
	const formMethods = useFormContext()
	//TODO: Trenger vi ikke error? Sjekk naar master er tatt inn
	const [error, setError] = useState(null)
	const [success, setSuccess] = useBoolean(false)
	const [loading, setLoading] = useBoolean(false)
	const [orgnummer, setOrgnummer] = useState(formMethods.watch(path) || null)

	const parentPath = path.substring(0, path.lastIndexOf('.'))

	const handleChange = (org: string, miljoe: string) => {
		if (!org || !miljoe) {
			return
		}
		setError(null)
		setLoading(true)
		setSuccess(false)
		formMethods.setValue(`${parentPath}.organisasjonMiljoe`, miljoe)
		OrgserviceApi.getOrganisasjonInfo(org, miljoe)
			.then((response) => {
				const orgInfo = {
					value: response.data.orgnummer,
					orgnr: response.data.orgnummer,
					navn: response.data.navn,
					forretningsAdresse: mapAdresse(response.data.forretningsadresser),
					postAdresse: mapAdresse(response.data.postadresse),
				}
				setEnhetsinfo(orgInfo, parentPath)
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
			parentPath={parentPath}
			miljoeOptions={aktiveMiljoer}
			success={success}
			loading={loading}
			onTextBlur={(event) => {
				if (!_.isEmpty(event?.target?.value)) {
					setOrgnummer(event.target.value)
					handleChange(event.target.value, formMethods.watch(`${parentPath}.organisasjonMiljoe`))
				}
			}}
			onMiljoeChange={(event) => {
				handleChange(orgnummer, event.value)
			}}
			formMethods={formMethods}
		/>
	)
}
