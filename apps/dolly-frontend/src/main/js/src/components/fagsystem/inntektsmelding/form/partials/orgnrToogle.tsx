import React, { useEffect, useState } from 'react'
import { FormikProps } from 'formik'
import { OrganisasjonMedArbeidsforholdSelect } from '~/components/organisasjonSelect'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import { FormikTextInput } from '~/components/ui/form/inputs/textInput/TextInput'
import { SelectOptionsOppslag } from '~/service/SelectOptionsOppslag'
import {
	OrganisasjonToogleGruppe,
	inputValg,
} from '~/components/organisasjonSelect/OrganisasjonToogleGruppe'

interface OrgnrToggleProps {
	path: string
	formikBag: FormikProps<{}>
}

type Virksomhet = {
	enhetstype: string
	orgnavn: string
	orgnummer: string
}

export const OrgnrToggle = ({ path, formikBag }: OrgnrToggleProps) => {
	const [inputType, setInputType] = useState(inputValg.fraFellesListe)
	const [egneOrganisasjoner, setEgneOrganisasjoner] = useState([])

	useEffect(() => {
		const fetchEgneOrg = async () => {
			const resp = await SelectOptionsOppslag.hentVirksomheterFraOrgforvalter()
				.then((response) => {
					if (!response || response.length === 0) return []
					return response.map((virksomhet: Virksomhet) => ({
						value: virksomhet.orgnummer,
						label: `${virksomhet.orgnummer} (${virksomhet.enhetstype}) - ${virksomhet.orgnavn}`,
					}))
				})
				.catch((e: Error) => {
					return []
				})
			setEgneOrganisasjoner(resp)
		}
		fetchEgneOrg()
	}, [])

	const handleToggleChange = (event: React.ChangeEvent<any>) => {
		setInputType(event.target.value)
		formikBag.setFieldValue(path, '')
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
					path={path}
					label="Arbeidsgiver (orgnr)"
					//@ts-ignore
					isClearable={false}
				/>
			)}
			{inputType === inputValg.fraEgenListe && (
				<FormikSelect
					name={path}
					label="Arbeidsgiver (orgnr)"
					size="xlarge"
					options={egneOrganisasjoner}
				/>
			)}
			{inputType === inputValg.skrivSelv && (
				<FormikTextInput type="number" name={path} label="Arbeidsgiver (orgnr)" size="xlarge" />
			)}
		</div>
	)
}
