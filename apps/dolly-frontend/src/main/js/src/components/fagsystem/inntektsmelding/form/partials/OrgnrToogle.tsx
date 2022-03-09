import React, { useEffect, useState } from 'react'
import { FormikProps } from 'formik'
import { ToggleGruppe, ToggleKnapp } from '~/components/ui/toggle/Toggle'
import { OrganisasjonMedArbeidsforholdSelect } from '~/components/organisasjonSelect'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import { FormikTextInput } from '~/components/ui/form/inputs/textInput/TextInput'
import { Organisasjon } from '~/service/services/organisasjonforvalter/types'
import OrganisasjonForvalterService from '~/service/services/organisasjonforvalter/OrganisasjonForvalterService'

interface OrgnrToggleProps {
	path: string
	formikBag: FormikProps<{}>
}

const inputValg = { fraEgenListe: 'egen', fraFellesListe: 'felles', skrivSelv: 'skriv' }

const formatLabel = (response: Organisasjon) =>
	`${response.organisasjonsnummer} (${response.enhetstype}) - ${response.organisasjonsnavn}`

export const OrgnrToggle = ({ path, formikBag }: OrgnrToggleProps) => {
	const [inputType, setInputType] = useState(inputValg.fraFellesListe)
	const [egneOrganisasjoner, setEgneOrganisasjoner] = useState([])

	useEffect(() => {
		const fetchEgneOrg = async () => {
			const resp = await OrganisasjonForvalterService.getAlleOrganisasjonerPaaBruker()
				.then((response: Organisasjon[]) => {
					if (response.length === 0) return []
					return response.map((org: Organisasjon) => {
						return {
							value: org.organisasjonsnummer,
							label: formatLabel(org),
						}
					})
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
			<ToggleGruppe onChange={handleToggleChange} name={path}>
				<ToggleKnapp
					key={inputValg.fraFellesListe}
					value={inputValg.fraFellesListe}
					checked={inputType === inputValg.fraFellesListe}
				>
					Felles organisasjoner
				</ToggleKnapp>
				<ToggleKnapp
					key={inputValg.fraEgenListe}
					value={inputValg.fraEgenListe}
					checked={inputType === inputValg.fraEgenListe}
				>
					Egen organisasjon
				</ToggleKnapp>
				<ToggleKnapp
					key={inputValg.skrivSelv}
					value={inputValg.skrivSelv}
					checked={inputType === inputValg.skrivSelv}
				>
					Skriv inn org.nr.
				</ToggleKnapp>
			</ToggleGruppe>
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
				<FormikTextInput name={path} label="Arbeidsgiver (orgnr)" size="xlarge" />
			)}
		</div>
	)
}
