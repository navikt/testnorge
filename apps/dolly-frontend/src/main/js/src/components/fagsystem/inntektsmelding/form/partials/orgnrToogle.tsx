import React, { useEffect, useState } from 'react'
import { OrganisasjonMedArbeidsforholdSelect } from '@/components/organisasjonSelect'
import { FormTextInput } from '@/components/ui/form/inputs/textInput/TextInput'
import { OrganisasjonToggleGruppe } from '@/components/organisasjonSelect/OrganisasjonToggleGruppe'
import { EgneOrganisasjoner, getEgneOrganisasjoner } from '@/utils/EgneOrganisasjoner'
import { UseFormReturn } from 'react-hook-form/dist/types'
import { useCurrentBruker } from '@/utils/hooks/useBruker'
import {
	useDollyFasteDataOrganisasjoner,
	useDollyOrganisasjoner,
} from '@/utils/hooks/useDollyOrganisasjoner'
import { ArbeidsgiverTyper } from '@/components/fagsystem/aareg/AaregTypes'
import Loading from '@/components/ui/loading/Loading'
import { getOrgType } from '@/utils/OrgUtils'

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
		useDollyOrganisasjoner(currentBruker?.brukerId)
	const egneOrganisasjoner = getEgneOrganisasjoner(brukerOrganisasjoner)

	const orgnr = formMethods.watch(virksomhetPath)
	const [inputType, setInputType] = useState(
		getOrgType(orgnr, fasteOrganisasjoner, egneOrganisasjoner),
	)

	useEffect(() => {
		setInputType(getOrgType(orgnr, fasteOrganisasjoner, egneOrganisasjoner))
	}, [
		fasteOrganisasjoner,
		brukerOrganisasjoner,
		formMethods.watch('inntektsmelding.inntekter')?.length,
	])

	const handleToggleChange = (value: string) => {
		setInputType(value)
		formMethods.setValue(virksomhetPath, '')
		formMethods.clearErrors(`manual.${virksomhetPath}`)
		formMethods.clearErrors(virksomhetPath)
	}

	const handleChangeEgne = (value: { orgnr: string }) => {
		formMethods.setValue(virksomhetPath, value.orgnr)
		formMethods.clearErrors(`manual.${virksomhetPath}`)
		formMethods.clearErrors(virksomhetPath)
	}

	if (fasteOrganisasjonerLoading || brukerOrganisasjonerLoading) {
		return <Loading label="Laster organisasjoner ..." />
	}

	return (
		<div className="toggle--wrapper">
			<OrganisasjonToggleGruppe
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
