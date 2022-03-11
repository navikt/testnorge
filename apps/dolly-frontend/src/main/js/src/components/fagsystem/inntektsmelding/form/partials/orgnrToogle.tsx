import React, { useEffect, useState } from 'react'
import { FormikProps } from 'formik'
import { ToggleGruppe, ToggleKnapp } from '~/components/ui/toggle/Toggle'
import { OrganisasjonMedArbeidsforholdSelect } from '~/components/organisasjonSelect'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import { FormikTextInput } from '~/components/ui/form/inputs/textInput/TextInput'
import { SelectOptionsOppslag } from '~/service/SelectOptionsOppslag'

interface OrgnrToggleProps {
	path: string
	formikBag: FormikProps<{}>
}

type Virksomhet = {
	enhetstype: string
	orgnavn: string
	orgnummer: string
}

const inputValg = { fraEgenListe: 'egen', fraFellesListe: 'felles', skrivSelv: 'skriv' }

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
				<FormikTextInput type="number" name={path} label="Arbeidsgiver (orgnr)" size="xlarge" />
			)}
		</div>
	)
}
