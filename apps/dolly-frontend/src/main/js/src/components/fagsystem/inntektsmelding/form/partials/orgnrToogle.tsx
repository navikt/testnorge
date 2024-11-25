import React, { useEffect, useState } from 'react'
import { OrganisasjonMedArbeidsforholdSelect } from '@/components/organisasjonSelect'
import { FormTextInput } from '@/components/ui/form/inputs/textInput/TextInput'
import { OrganisasjonToogleGruppe } from '@/components/organisasjonSelect/OrganisasjonToogleGruppe'
import {
	EgneOrganisasjoner,
	getEgneOrganisasjoner,
} from '@/components/fagsystem/brregstub/form/partials/EgneOrganisasjoner'
import { UseFormReturn } from 'react-hook-form/dist/types'
import { useCurrentBruker } from '@/utils/hooks/useBruker'
import { useDollyFasteDataOrganisasjoner, useOrganisasjoner } from '@/utils/hooks/useOrganisasjoner'
import { ArbeidsgiverTyper } from '@/components/fagsystem/aareg/AaregTypes'
import Loading from '@/components/ui/loading/Loading'

interface OrgnrToggleProps {
	path: string
	formMethods: UseFormReturn
	label?: string
}

export const OrgnrToggle = ({
	path,
	formMethods,
	label = 'Arbeidsgiver (orgnr)',
}: OrgnrToggleProps) => {
	const virksomhetPath = `${path}.arbeidsgiver.virksomhetsnummer`

	const { currentBruker } = useCurrentBruker()

	const { organisasjoner: fasteOrganisasjoner, loading: fasteOrganisasjonerLoading } =
		useDollyFasteDataOrganisasjoner()

	const { organisasjoner: brukerOrganisasjoner, loading: brukerOrganisasjonerLoading } =
		useOrganisasjoner(currentBruker?.brukerId)
	const egneOrganisasjoner = getEgneOrganisasjoner(brukerOrganisasjoner)

	const getOrgType = () => {
		const orgnr = formMethods.watch(virksomhetPath)
		//TODO: Denne kan sikkert lages som felles funksjon
		if (
			!orgnr ||
			orgnr === '' ||
			fasteOrganisasjoner
				?.map((organisasjon: any) => organisasjon?.orgnummer)
				?.some((org: string) => org === orgnr)
		) {
			return ArbeidsgiverTyper.felles
		} else if (
			egneOrganisasjoner
				?.map((organisasjon: any) => organisasjon?.orgnr)
				?.some((org: string) => org === orgnr)
		) {
			return ArbeidsgiverTyper.egen
		} else {
			return ArbeidsgiverTyper.fritekst
		}
	}

	const [inputType, setInputType] = useState(getOrgType())

	useEffect(() => {
		setInputType(getOrgType())
	}, [
		fasteOrganisasjoner,
		brukerOrganisasjoner,
		formMethods.watch('inntektsmelding.inntekter')?.length,
	])

	const handleToggleChange = (value: string) => {
		setInputType(value)
		formMethods.setValue(virksomhetPath, '')
	}

	const handleChangeEgne = (value: { orgnr: string }) => {
		formMethods.setValue(virksomhetPath, value.orgnr)
	}

	if (fasteOrganisasjonerLoading || brukerOrganisasjonerLoading) {
		return <Loading label="Laster organisasjoner ..." />
	}

	return (
		<div className="toggle--wrapper">
			<OrganisasjonToogleGruppe
				path={virksomhetPath}
				inputType={inputType}
				handleToggleChange={handleToggleChange}
				style={{ margin: '5px 0 5px' }}
			/>
			{inputType === ArbeidsgiverTyper.felles && (
				<OrganisasjonMedArbeidsforholdSelect
					path={virksomhetPath}
					label={label}
					//@ts-ignore
					isClearable={false}
					placeholder={'Velg organisasjon ...'}
				/>
			)}
			{inputType === ArbeidsgiverTyper.egen && (
				<EgneOrganisasjoner
					path={virksomhetPath}
					label={label}
					formMethods={formMethods}
					filterValidEnhetstyper={true}
					// @ts-ignore
					handleChange={handleChangeEgne}
				/>
			)}
			{inputType === ArbeidsgiverTyper.fritekst && (
				<FormTextInput type="number" name={virksomhetPath} label={label} size="xlarge" />
			)}
		</div>
	)
}
