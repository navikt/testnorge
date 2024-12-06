import React, { useContext, useEffect, useState } from 'react'
import styled from 'styled-components'
import { Alert, ToggleGroup } from '@navikt/ds-react'
import { initialArbeidsgiverOrg, initialArbeidsgiverPers } from '../initialValues'
import { ArbeidsgiverTyper } from '@/components/fagsystem/aareg/AaregTypes'
import { useFormContext } from 'react-hook-form'
import { EgneOrganisasjoner } from '@/utils/EgneOrganisasjoner'
import Loading from '@/components/ui/loading/Loading'
import { OrganisasjonMedArbeidsforholdSelect } from '@/components/organisasjonSelect'
import { FormTextInput } from '@/components/ui/form/inputs/textInput/TextInput'
import { ArbeidsgiverIdent } from '@/components/fagsystem/aareg/form/partials/arbeidsgiverIdent'
import _ from 'lodash'
import { BestillingsveilederContext } from '@/components/bestillingsveileder/BestillingsveilederContext'
import { hentAaregEksisterendeData } from '@/components/fagsystem/aareg/form/utils'
import { arbeidsgiverToggleValues } from '@/utils/OrgUtils'

const ToggleArbeidsgiver = styled(ToggleGroup)`
	display: grid;
	grid-template-columns: repeat(auto-fit, minmax(180px, 1fr));
	background-color: #ffffff;
`

const DisabledToggleArbeidsgiver = styled(ToggleGroup)`
	display: grid;
	grid-template-columns: repeat(auto-fit, minmax(180px, 1fr));

	:hover {
		background-color: white;
		cursor: default;
	}

	&&& {
		button {
			color: #aab0ba;
		}

		.navds-toggle-group__button[aria-checked='true'] {
			background-color: #aab0ba;
			color: white;

			:hover {
				background-color: #aab0ba;
				cursor: default;
			}
		}
	}
`

type ArbeidsforholdToggleProps = {
	path: string
	idx: number
	fasteOrganisasjoner: any
	brukerOrganisasjoner: any
	egneOrganisasjoner: any
	loadingOrganisasjoner: boolean
}

export const ArbeidsforholdToggle = ({
	path,
	idx,
	fasteOrganisasjoner,
	brukerOrganisasjoner,
	egneOrganisasjoner,
	loadingOrganisasjoner,
}: ArbeidsforholdToggleProps) => {
	const formMethods = useFormContext()
	const aaregData = formMethods.getValues(path)

	//@ts-ignore
	const { personFoerLeggTil } = useContext(BestillingsveilederContext)
	const tidligereAaregdata = hentAaregEksisterendeData(personFoerLeggTil)
	const erLaastArbeidsforhold = idx < tidligereAaregdata?.length

	const getArbeidsgiverType = () => {
		const orgnr = aaregData?.arbeidsgiver?.orgnummer
		if (aaregData?.arbeidsgiver?.aktoertype === 'PERS') {
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
	}, [fasteOrganisasjoner, brukerOrganisasjoner, formMethods.watch('aareg')?.length])

	const handleToggleChange = (value: ArbeidsgiverTyper) => {
		setTypeArbeidsgiver(value)
		if (value === ArbeidsgiverTyper.privat) {
			formMethods.resetField(`${path}.arbeidsgiver`, { defaultValue: initialArbeidsgiverPers })
		} else {
			formMethods.resetField(`${path}.arbeidsgiver`, { defaultValue: initialArbeidsgiverOrg })
		}
	}

	const checkAktiveArbeidsforhold = () => {
		const aaregValues = formMethods.getValues('aareg')
		const aktiveArbeidsforhold = aaregValues?.map((arbeidsforhold: any) => {
			const orgnummer = arbeidsforhold?.arbeidsgiver?.orgnummer
			if (!arbeidsforhold?.ansettelsesPeriode?.sluttaarsak) {
				return orgnummer
			}
		})
		const dupliserteAktiveArbeidsforhold = aktiveArbeidsforhold
			.filter(
				(arbeidsforhold: any, index: number) =>
					index !== aktiveArbeidsforhold.indexOf(arbeidsforhold),
			)
			.filter((arbeidsforhold: any) => !_.isEmpty(arbeidsforhold))
		if (!_.isEmpty(dupliserteAktiveArbeidsforhold)) {
			formMethods.setError(`manual.${path}.arbeidsgiver.orgnummer`, {
				message: `Identen har allerede pågående arbeidsforhold i org: ${dupliserteAktiveArbeidsforhold.toString()}`,
			})
		}
	}

	if (loadingOrganisasjoner) {
		return <Loading label="Laster organisasjoner ..." />
	}

	const title = erLaastArbeidsforhold
		? 'Kan ikke endre arbeidsgiver på eksisterende arbeidsforhold'
		: ''

	return (
		<div className="toggle--wrapper" key={idx}>
			{erLaastArbeidsforhold ? (
				<DisabledToggleArbeidsgiver
					onChange={() => null}
					value={typeArbeidsgiver}
					size={'small'}
					fill
					key={idx}
					title={'Kan ikke endre arbeidsgivertype på eksisterende arbeidsforhold'}
				>
					{arbeidsgiverToggleValues.map((type) => (
						<ToggleGroup.Item key={type.value} value={type.value}>
							{type.label}
						</ToggleGroup.Item>
					))}
				</DisabledToggleArbeidsgiver>
			) : (
				<ToggleArbeidsgiver
					// @ts-ignore
					onChange={(value: ArbeidsgiverTyper) => handleToggleChange(value)}
					value={typeArbeidsgiver}
					size={'small'}
					fill
					key={idx}
				>
					{arbeidsgiverToggleValues.map((type) => (
						<ToggleGroup.Item key={type.value} value={type.value}>
							{type.label}
						</ToggleGroup.Item>
					))}
				</ToggleArbeidsgiver>
			)}
			<div className="flexbox--full-width">
				{typeArbeidsgiver === ArbeidsgiverTyper.felles && (
					<div title={title}>
						<OrganisasjonMedArbeidsforholdSelect
							path={`${path}.arbeidsgiver.orgnummer`}
							label={'Organisasjonsnummer'}
							afterChange={() => checkAktiveArbeidsforhold()}
							isDisabled={erLaastArbeidsforhold}
							placeholder={'Velg organisasjon ...'}
						/>
					</div>
				)}
				{typeArbeidsgiver === ArbeidsgiverTyper.egen && (
					<div className="flex-box" title={title}>
						<EgneOrganisasjoner
							path={`${path}.arbeidsgiver.orgnummer`}
							handleChange={(selected: any) =>
								formMethods.setValue(`${path}.arbeidsgiver.orgnummer`, selected?.value)
							}
							filterValidEnhetstyper={true}
							isDisabled={erLaastArbeidsforhold}
						/>
					</div>
				)}
				{typeArbeidsgiver === ArbeidsgiverTyper.fritekst && (
					<FormTextInput
						name={`${path}.arbeidsgiver.orgnummer`}
						label={'Organisasjonsnummer'}
						size="xlarge"
						onBlur={() => checkAktiveArbeidsforhold()}
						defaultValue={formMethods.watch(`${path}.arbeidsgiver.orgnummer`)}
						isDisabled={erLaastArbeidsforhold}
						title={title}
					/>
				)}
				{typeArbeidsgiver === ArbeidsgiverTyper.privat && (
					<ArbeidsgiverIdent
						path={`${path}.arbeidsgiver.ident`}
						isDisabled={erLaastArbeidsforhold}
						title={title}
					/>
				)}
			</div>
		</div>
	)
}
