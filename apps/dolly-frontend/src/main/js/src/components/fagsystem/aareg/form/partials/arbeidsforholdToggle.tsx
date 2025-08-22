import React, { useContext, useEffect, useState } from 'react'
import styled from 'styled-components'
import { ToggleGroup } from '@navikt/ds-react'
import { initialArbeidsgiverOrg, initialArbeidsgiverPers } from '../initialValues'
import { ArbeidsgiverTyper } from '@/components/fagsystem/aareg/AaregTypes'
import { useFormContext } from 'react-hook-form'
import { EgneOrganisasjoner } from '@/utils/EgneOrganisasjoner'
import Loading from '@/components/ui/loading/Loading'
import { OrganisasjonMedArbeidsforholdSelect } from '@/components/organisasjonSelect'
import { FormTextInput } from '@/components/ui/form/inputs/textInput/TextInput'
import { ArbeidsgiverIdent } from '@/components/fagsystem/aareg/form/partials/arbeidsgiverIdent'
import {
	BestillingsveilederContext,
	BestillingsveilederContextType,
} from '@/components/bestillingsveileder/BestillingsveilederContext'
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
	idx?: number
	afterChange?: (value: any) => any
	onToggle?: (value: any) => any
	disablePrivat?: boolean
	fasteOrganisasjoner?: any
	brukerOrganisasjoner?: any
	egneOrganisasjoner?: any
	loadingOrganisasjoner?: boolean
}

export const ArbeidsforholdToggle = ({
	path,
	idx,
	onToggle,
	afterChange,
	disablePrivat = false,
	fasteOrganisasjoner,
	brukerOrganisasjoner,
	egneOrganisasjoner,
	loadingOrganisasjoner,
}: ArbeidsforholdToggleProps) => {
	const formMethods = useFormContext()
	const aaregData = formMethods.getValues(path)

	//@ts-ignore
	const { personFoerLeggTil } = useContext(
		BestillingsveilederContext,
	) as BestillingsveilederContextType
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
		onToggle && onToggle(value)
		setTypeArbeidsgiver(value)
		if (value === ArbeidsgiverTyper.privat) {
			formMethods.resetField(`${path}.arbeidsgiver`, { defaultValue: initialArbeidsgiverPers })
		} else {
			formMethods.resetField(`${path}.arbeidsgiver`, { defaultValue: initialArbeidsgiverOrg })
		}
		formMethods.clearErrors(`manual.${path}.arbeidsgiver`)
		formMethods.clearErrors(`${path}.arbeidsgiver`)
	}

	if (loadingOrganisasjoner) {
		return <Loading label="Laster organisasjoner ..." />
	}

	const title = erLaastArbeidsforhold
		? 'Kan ikke endre arbeidsgiver på eksisterende arbeidsforhold'
		: ''

	const renderArbeidsgiverToggleItems = (disablePrivat: boolean) =>
		arbeidsgiverToggleValues
			.filter((t) => !(disablePrivat && t.value === ArbeidsgiverTyper.privat))
			.map((t) => (
				<ToggleGroup.Item key={t.value} value={t.value}>
					{t.label}
				</ToggleGroup.Item>
			))

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
					{renderArbeidsgiverToggleItems(disablePrivat)}
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
					{renderArbeidsgiverToggleItems(disablePrivat)}
				</ToggleArbeidsgiver>
			)}
			<div className="flexbox--full-width">
				{typeArbeidsgiver === ArbeidsgiverTyper.felles && (
					<div title={title}>
						<OrganisasjonMedArbeidsforholdSelect
							path={`${path}.arbeidsgiver.orgnummer`}
							label={'Organisasjonsnummer'}
							isDisabled={erLaastArbeidsforhold}
							placeholder={'Velg organisasjon ...'}
							afterChange={afterChange}
						/>
					</div>
				)}
				{typeArbeidsgiver === ArbeidsgiverTyper.egen && (
					<div className="flex-box" title={title}>
						<EgneOrganisasjoner
							path={`${path}.arbeidsgiver.orgnummer`}
							afterChange={afterChange}
							handleChange={(selected: any) => {
								formMethods.setValue(`${path}.arbeidsgiver.orgnummer`, selected?.value)
								formMethods.clearErrors(`manual.${path}.arbeidsgiver`)
								formMethods.clearErrors(`${path}.arbeidsgiver`)
							}}
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
						onBlur={afterChange}
						isDisabled={erLaastArbeidsforhold}
						title={title}
					/>
				)}
				{typeArbeidsgiver === ArbeidsgiverTyper.privat && (
					<ArbeidsgiverIdent
						path={`${path}.arbeidsgiver.ident`}
						isDisabled={erLaastArbeidsforhold || disablePrivat}
						title={title}
					/>
				)}
			</div>
		</div>
	)
}
