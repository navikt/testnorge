import React, { useEffect, useState } from 'react'
import { ToggleGroup } from '@navikt/ds-react'
import { FormTextInput } from '@/components/ui/form/inputs/textInput/TextInput'
import { Kategori } from '@/components/ui/form/kategori/Kategori'
import { UseFormReturn } from 'react-hook-form/dist/types'
import { useCurrentBruker } from '@/utils/hooks/useBruker'
import {
	useDollyFasteDataOrganisasjoner,
	useFasteDataOrganisasjon,
	useOrganisasjoner,
} from '@/utils/hooks/useOrganisasjoner'
import {
	EgneOrganisasjoner,
	getEgneOrganisasjoner,
} from '@/components/fagsystem/brregstub/form/partials/EgneOrganisasjoner'
import { ArbeidsgiverTyper } from '@/components/fagsystem/aareg/AaregTypes'
import { useDollyEnvironments } from '@/utils/hooks/useEnvironments'
import { useBoolean } from 'react-use'
import Loading from '@/components/ui/loading/Loading'
import { arbeidsgiverToggleValues, handleManualOrgChange } from '@/components/fagsystem/utils'
import { OrganisasjonMedArbeidsforholdSelect } from '@/components/organisasjonSelect'
import { OrganisasjonMedMiljoeSelect } from '@/components/organisasjonSelect/OrganisasjonMedMiljoeSelect'
import styled from 'styled-components'

const ToggleArbeidsgiver = styled(ToggleGroup)`
	display: grid;
	grid-template-columns: repeat(auto-fit, minmax(180px, 1fr));
	background-color: #ffffff;
`

type ArbeidsgiverToggleProps = {
	formMethods: UseFormReturn
	path: string
}

export const ArbeidsgiverToggle = ({ formMethods, path }: ArbeidsgiverToggleProps) => {
	const { currentBruker } = useCurrentBruker()

	const { organisasjoner: fasteOrganisasjoner, loading: fasteOrganisasjonerLoading } =
		useDollyFasteDataOrganisasjoner(true)

	const { organisasjoner: brukerOrganisasjoner, loading: brukerOrganisasjonerLoading } =
		useOrganisasjoner(currentBruker?.brukerId)
	const egneOrganisasjoner = getEgneOrganisasjoner(brukerOrganisasjoner)

	const organisasjonPath = `${path}.organisasjonsnummer`
	const personPath = `${path}.personidentifikator`

	const getArbeidsgiverType = () => {
		const orgnr = formMethods.watch(organisasjonPath)
		if (formMethods.watch(personPath)) {
			return ArbeidsgiverTyper.privat
		} else if (
			!orgnr ||
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

	const [typeArbeidsgiver, setTypeArbeidsgiver] = useState(getArbeidsgiverType())

	useEffect(() => {
		setTypeArbeidsgiver(getArbeidsgiverType())
	}, [
		fasteOrganisasjoner,
		brukerOrganisasjoner,
		formMethods.watch('skattekort.arbeidsgiverSkatt')?.length,
	])

	const { dollyEnvironments: aktiveMiljoer } = useDollyEnvironments()
	const [success, setSuccess] = useBoolean(false)
	const [loading, setLoading] = useBoolean(false)
	const [environment, setEnvironment] = useState(null)
	const [orgnummer, setOrgnummer] = useState(formMethods.watch(organisasjonPath) || null)
	const { organisasjon } = useFasteDataOrganisasjon(orgnummer)

	const handleToggleChange = (value: ArbeidsgiverTyper) => {
		setTypeArbeidsgiver(value)
		formMethods.clearErrors(path)
		if (value === ArbeidsgiverTyper.privat) {
			formMethods.setValue(personPath, '')
			formMethods.setValue(organisasjonPath, undefined)
		} else {
			formMethods.setValue(organisasjonPath, '')
			formMethods.setValue(personPath, undefined)
		}
	}

	const handleOrgChange = (value: { orgnr: string }) => {
		formMethods.setValue(organisasjonPath, value.orgnr)
		formMethods.trigger(organisasjonPath)
	}

	if (fasteOrganisasjonerLoading || brukerOrganisasjonerLoading) {
		return <Loading label="Laster organisasjoner ..." />
	}

	return (
		<Kategori title="Arbeidsgiver">
			<div className="toggle--wrapper">
				<ToggleArbeidsgiver
					// @ts-ignore
					onChange={(value: ArbeidsgiverTyper) => handleToggleChange(value)}
					value={typeArbeidsgiver}
					size={'small'}
					fill
				>
					{arbeidsgiverToggleValues.map((type) => (
						<ToggleGroup.Item key={type.value} value={type.value}>
							{type.label}
						</ToggleGroup.Item>
					))}
				</ToggleArbeidsgiver>
				<div className="flexbox--full-width">
					{typeArbeidsgiver === ArbeidsgiverTyper.felles && (
						<OrganisasjonMedArbeidsforholdSelect
							path={organisasjonPath}
							label={'Organisasjonsnummer'}
							placeholder={'Velg organisasjon ...'}
						/>
					)}
					{typeArbeidsgiver === ArbeidsgiverTyper.egen && (
						<EgneOrganisasjoner
							path={organisasjonPath}
							handleChange={handleOrgChange}
							filterValidEnhetstyper={true}
						/>
					)}
					{typeArbeidsgiver === ArbeidsgiverTyper.fritekst && (
						<OrganisasjonMedMiljoeSelect
							path={organisasjonPath}
							environment={environment}
							miljoeOptions={aktiveMiljoer}
							loading={loading}
							success={success}
							onTextBlur={(event) => {
								const org = event.target.value
								setOrgnummer(org)
								handleManualOrgChange(
									org,
									environment,
									formMethods,
									organisasjonPath,
									setLoading,
									setSuccess,
									organisasjon,
									null,
								)
							}}
							onMiljoeChange={(event) => {
								setEnvironment(event.value)
								handleManualOrgChange(
									orgnummer,
									event.value,
									formMethods,
									organisasjonPath,
									setLoading,
									setSuccess,
									organisasjon,
									null,
								)
							}}
						/>
					)}
					{typeArbeidsgiver === ArbeidsgiverTyper.privat && (
						<div className="flexbox--flex-wrap">
							<FormTextInput name={personPath} label="Personidentifikator" size="xlarge" />
						</div>
					)}
				</div>
			</div>
		</Kategori>
	)
}
