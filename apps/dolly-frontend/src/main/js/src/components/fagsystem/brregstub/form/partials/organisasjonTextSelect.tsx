import React, { useEffect, useState } from 'react'
import { OrganisasjonForvalterSelect } from '@/components/organisasjonSelect/OrganisasjonForvalterSelect'
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
	const [org, setOrg] = useState(formMethods.watch(path))
	const { organisasjoner, loading, error } = useOrganisasjonForvalter([org])
	const forvalterOrg = organisasjoner?.[0]?.q1 || organisasjoner?.[0]?.q2

	useEffect(() => {
		if (!forvalterOrg) {
			if (!loading) {
				formMethods.setError(`manual.${path}`, { message: 'Fant ikke organisasjonen' })
			}
			return
		}
		const forretningsAdresse = forvalterOrg?.adresser?.filter(
			(adresse) => adresse.adressetype === 'FADR',
		)?.[0]
		const postAdresse = forvalterOrg?.adresser?.filter(
			(adresse) => adresse.adressetype === 'PADR',
		)?.[0]
		const orgInfo = {
			value: forvalterOrg?.organisasjonsnummer,
			orgnr: forvalterOrg?.organisasjonsnummer,
			navn: forvalterOrg?.organisasjonsnavn,
			forretningsAdresse: mapAdresse(forretningsAdresse),
			postAdresse: mapAdresse(postAdresse),
		}
		setEnhetsinfo(orgInfo, parentPath)
	}, [forvalterOrg, loading])

	useEffect(() => {
		if (error && !formMethods.getFieldState(`manual.${path}`)?.error?.message) {
			formMethods.setError(`manual.${path}`, { message: 'Fant ikke organisasjonen' })
		} else {
			formMethods.clearErrors(`manual.${path}`)
		}
	}, [error])

	const parentPath = path.substring(0, path.lastIndexOf('.'))

	const handleChange = (org: string) => {
		formMethods.setValue(path, null)
		if (!org) {
			formMethods.setError(`manual.${path}`, { message: 'Skriv inn org' })
			return
		}
		setOrg(org)
		formMethods.clearErrors(`manual.${path}`)
		formMethods.clearErrors(path)
	}

	return (
		<OrganisasjonForvalterSelect
			path={path}
			value={org}
			parentPath={parentPath}
			success={organisasjoner?.length > 0 && !loading && !error}
			loading={loading}
			onTextBlur={(event) => {
				handleChange(event.target.value)
			}}
		/>
	)
}
