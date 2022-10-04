import React, { useEffect, useState } from 'react'
import { FormikProps } from 'formik'
import { OrganisasjonMedArbeidsforholdSelect } from '~/components/organisasjonSelect'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import { FormikTextInput } from '~/components/ui/form/inputs/textInput/TextInput'
import {
	inputValg,
	OrganisasjonToogleGruppe,
} from '~/components/organisasjonSelect/OrganisasjonToogleGruppe'
import { useCurrentBruker } from '~/utils/hooks/useBruker'
import { DollyApi } from '~/service/Api'
import { Organisasjon } from '~/service/services/organisasjonforvalter/types'
import { addAlleVirksomheter } from '~/ducks/organisasjon'

interface OrgnrToggleProps {
	path: string
	formikBag: FormikProps<{}>
}

export const OrgnrToggle = ({ path, formikBag }: OrgnrToggleProps) => {
	const [inputType, setInputType] = useState(inputValg.fraFellesListe)
	const [egneOrganisasjoner, setEgneOrganisasjoner] = useState([])
	const {
		currentBruker: { brukerId },
	} = useCurrentBruker()

	const validEnhetstyper = ['BEDR', 'AAFY']

	useEffect(() => {
		const fetchEgneOrg = async () => {
			const resp = await DollyApi.getAlleOrganisasjonerPaaBruker(brukerId)
				.then((response: Organisasjon[]) => {
					if (!response || response.length === 0) {
						return []
					}
					let egneOrg: Organisasjon[] = []
					addAlleVirksomheter(egneOrg, response)
					egneOrg = egneOrg.filter((virksomhet) => validEnhetstyper.includes(virksomhet.enhetstype))

					return egneOrg.map((virksomhet: Organisasjon) => ({
						value: virksomhet.organisasjonsnummer,
						label: `${virksomhet.organisasjonsnummer} (${virksomhet.enhetstype}) - ${virksomhet.organisasjonsnavn}`,
					}))
				})
				.catch((_e: Error) => {
					return []
				})
			setEgneOrganisasjoner(resp)
		}
		fetchEgneOrg()
	}, [])

	const handleToggleChange = (value: string) => {
		setInputType(value)
		formikBag.setFieldValue(path, '')
	}

	return (
		<div className="toggle--wrapper">
			<OrganisasjonToogleGruppe
				path={path}
				inputType={inputType}
				handleToggleChange={handleToggleChange}
				style={{ margin: '5px 0 5px' }}
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
