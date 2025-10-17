import React, { useEffect, useState } from 'react'
import styled from 'styled-components'
import { ToggleGroup } from '@navikt/ds-react'
import { ArbeidsgiverTyper } from '@/components/fagsystem/aareg/AaregTypes'
import { useFormContext } from 'react-hook-form'
import { EgneOrganisasjoner, getEgneOrganisasjoner } from '@/utils/EgneOrganisasjoner'
import Loading from '@/components/ui/loading/Loading'
import { OrganisasjonMedArbeidsforholdSelect } from '@/components/organisasjonSelect'
import { FormTextInput } from '@/components/ui/form/inputs/textInput/TextInput'
import { useCurrentBruker } from '@/utils/hooks/useBruker'
import {
	useDollyFasteDataOrganisasjoner,
	useDollyOrganisasjoner,
	useOrganisasjonForvalter,
} from '@/utils/hooks/useDollyOrganisasjoner'
import { arbeidsgiverToggleValues, getOrgType, handleManualOrgChange } from '@/utils/OrgUtils'
import { OrganisasjonForvalterSelect } from '@/components/organisasjonSelect/OrganisasjonForvalterSelect'

const ToggleArbeidsgiver = styled(ToggleGroup)`
	display: grid;
	grid-template-columns: repeat(auto-fit, minmax(180px, 1fr));
	background-color: #ffffff;
`

type ArbeidsforholdToggleProps = {
	path: string
}

export const VirksomhetToggle = ({ path }: ArbeidsforholdToggleProps) => {
	const formMethods = useFormContext()
	const { currentBruker } = useCurrentBruker()

	const { organisasjoner: fasteOrganisasjoner, loading: fasteOrganisasjonerLoading } =
		useDollyFasteDataOrganisasjoner(true)

	const { organisasjoner: brukerOrganisasjoner, loading: brukerOrganisasjonerLoading } =
		useDollyOrganisasjoner(currentBruker?.brukerId)
	const egneOrganisasjoner = getEgneOrganisasjoner(brukerOrganisasjoner)

	const virksomhetPath = `${path}.virksomhet`
	const opplysningspliktigPath = `${path}.opplysningspliktig`

	const getArbeidsgiverType = () => {
		const orgnummerLength = 9
		const orgnr = formMethods.watch(virksomhetPath)
		if (!orgnr || orgnr === '' || orgnr?.length === orgnummerLength) {
			return getOrgType(orgnr, fasteOrganisasjoner, egneOrganisasjoner)
		} else {
			return ArbeidsgiverTyper.privat
		}
	}

	const [typeArbeidsgiver, setTypeArbeidsgiver] = useState(getArbeidsgiverType())

	useEffect(() => {
		setTypeArbeidsgiver(getArbeidsgiverType())
	}, [
		fasteOrganisasjoner,
		brukerOrganisasjoner,
		formMethods.watch('inntektstub.inntektsinformasjon')?.length,
	])

	const [orgnummer, setOrgnummer] = useState(formMethods.watch(virksomhetPath) || null)
	const { organisasjoner, loading, error } = useOrganisasjonForvalter([orgnummer])
	const organisasjon = organisasjoner?.[0]?.q1 || organisasjoner?.[0]?.q2

	useEffect(() => {
		if (!organisasjon) {
			if (!loading) {
				formMethods.setError(`manual.${opplysningspliktigPath}`, {
					message: 'Fant ikke organisasjonen',
				})
			}
			return
		}
		formMethods.clearErrors([`manual.${opplysningspliktigPath}`, `manual.${virksomhetPath}`])
		handleManualOrgChange(
			orgnummer,
			formMethods,
			virksomhetPath,
			opplysningspliktigPath,
			organisasjon,
		)
	}, [organisasjon, loading])

	const handleToggleChange = (value: ArbeidsgiverTyper) => {
		setTypeArbeidsgiver(value)
		setOrgnummer(null)
		formMethods.setValue(virksomhetPath, null)
		formMethods.setValue(opplysningspliktigPath, null)
		formMethods.clearErrors(`manual.${path}`)
		formMethods.clearErrors(path)
	}

	const handleOrgChange = (value: { juridiskEnhet: string; orgnr: string }) => {
		opplysningspliktigPath && formMethods.setValue(`${opplysningspliktigPath}`, value.juridiskEnhet)
		formMethods.clearErrors(`manual.${path}`)
		formMethods.trigger(opplysningspliktigPath)
		formMethods.setValue(virksomhetPath, value.orgnr)
	}

	if (fasteOrganisasjonerLoading || brukerOrganisasjonerLoading) {
		return <Loading label="Laster organisasjoner ..." />
	}

	return (
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
						afterChange={handleOrgChange}
						path={virksomhetPath}
						label={'Organisasjonsnummer'}
						placeholder={'Velg organisasjon ...'}
					/>
				)}
				{typeArbeidsgiver === ArbeidsgiverTyper.egen && (
					<EgneOrganisasjoner
						path={virksomhetPath}
						handleChange={handleOrgChange}
						filterValidEnhetstyper={true}
					/>
				)}
				{typeArbeidsgiver === ArbeidsgiverTyper.fritekst && (
					<OrganisasjonForvalterSelect
						value={orgnummer}
						path={virksomhetPath}
						parentPath={opplysningspliktigPath}
						success={formMethods.watch(virksomhetPath) && !error}
						loading={loading}
						onTextBlur={(event) => {
							formMethods.setValue(virksomhetPath, null)
							formMethods.setValue(opplysningspliktigPath, null)
							setOrgnummer(event.target.value)
						}}
					/>
				)}
				{typeArbeidsgiver === ArbeidsgiverTyper.privat && (
					<div className="flexbox--flex-wrap">
						<FormTextInput
							name={virksomhetPath}
							label="Virksomhet (fnr/dnr/npid)"
							size="medium"
							autoFocus
						/>
						<FormTextInput name={opplysningspliktigPath} label="Opplysningspliktig" size="medium" />
					</div>
				)}
			</div>
		</div>
	)
}
