import React, { useEffect, useState } from 'react'
import { OrganisasjonMedMiljoeSelect } from '@/components/organisasjonSelect/OrganisasjonMedMiljoeSelect'
import * as _ from 'lodash-es'
import { useFormContext } from 'react-hook-form'
import { useOrganisasjonForvalter } from '@/utils/hooks/useDollyOrganisasjoner'
import { AdresseOrgForvalter } from '@/service/services/organisasjonforvalter/types'

interface OrganisasjonTextSelectProps {
	path: string
	setEnhetsinfo: (org: any, path: string) => {}
}

const mapAdresse = (adresse: AdresseOrgForvalter) => {
	if (!adresse) {
		return null
	}
	return {
		adresselinje1: adresse.adresselinjer?.[0],
		kommunenr: adresse.kommunenr,
		landkode: adresse.landkode,
		postnr: adresse.postnr,
		poststed: adresse.poststed,
	}
}

export const OrganisasjonTextSelect = ({ path, setEnhetsinfo }: OrganisasjonTextSelectProps) => {
	const formMethods = useFormContext()
	const [org, setOrg] = useState()
	const { organisasjoner, loading, error } = useOrganisasjonForvalter([org])

	useEffect(() => {
		const org = organisasjoner?.[0]?.q1 || organisasjoner?.[0]?.q2
		const forretningsAdresse = org?.adresser?.filter(
			(adresse) => adresse.adressetype === 'FORRETNINGSADRESSE',
		)?.[0]
		const postAdresse = org?.adresser?.filter(
			(adresse) => adresse.adressetype === 'POSTADRESSE',
		)?.[0]
		const orgInfo = {
			value: org?.organisasjonsnummer,
			orgnr: org?.organisasjonsnummer,
			navn: org?.organisasjonsnavn,
			forretningsAdresse: mapAdresse(forretningsAdresse),
			postAdresse: mapAdresse(postAdresse),
		}
		setEnhetsinfo(orgInfo, parentPath)
	}, [organisasjoner])

	useEffect(() => {
		if (error) {
			formMethods.setError(`manual.${path}`, { message: 'Fant ikke organisasjonen' })
		} else {
			formMethods.clearErrors(`manual.${path}`)
		}
	}, [error])

	const parentPath = path.substring(0, path.lastIndexOf('.'))

	const handleChange = (org: string) => {
		if (!org) {
			formMethods.setError(`manual.${path}`, { message: 'Skriv inn org' })
			return
		}
		formMethods.clearErrors(`manual.${path}`)
		formMethods.clearErrors(path)
	}

	return (
		<OrganisasjonMedMiljoeSelect
			path={path}
			parentPath={parentPath}
			success={organisasjoner?.length > 0 && !loading && !error}
			loading={loading}
			onTextBlur={(event) => {
				const value = event?.target?.value
				if (!_.isEmpty(value)) {
					setOrg(value)
					handleChange(value)
				}
			}}
		/>
	)
}
