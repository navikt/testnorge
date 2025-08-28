import React, { useContext, useEffect } from 'react'
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
	showMiljoeInfo?: boolean
	onToggle?: (value: any) => any
	disablePrivat?: boolean
	fasteOrganisasjoner?: any
	egneOrganisasjoner?: any
	loadingOrganisasjoner?: boolean
}

export const ArbeidsforholdToggle = ({
	path,
	idx,
	onToggle,
	afterChange,
	showMiljoeInfo = true,
	disablePrivat = false,
	fasteOrganisasjoner,
	egneOrganisasjoner,
	loadingOrganisasjoner,
}: ArbeidsforholdToggleProps) => {
	const formMethods = useFormContext()
	const aaregData = formMethods.getValues(path)

	const { personFoerLeggTil } = useContext(
		BestillingsveilederContext,
	) as BestillingsveilederContextType
	const tidligereAaregdata = hentAaregEksisterendeData(personFoerLeggTil)

	const erLaastArbeidsforhold = typeof idx === 'number' && idx < (tidligereAaregdata?.length ?? 0)

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

	const arbeidsgiverType = formMethods.watch(`${path}.arbeidsgiverType`) as
		| ArbeidsgiverTyper
		| undefined

	useEffect(() => {
		if (!arbeidsgiverType) {
			formMethods.setValue(`${path}.arbeidsgiverType`, getArbeidsgiverType(), {
				shouldDirty: false,
			})
		}
	}, [path])

	const handleToggleChange = (value: ArbeidsgiverTyper) => {
		onToggle && onToggle(value)
		formMethods.setValue(`${path}.arbeidsgiverType`, value, {
			shouldDirty: true,
			shouldTouch: true,
		})
		if (value === ArbeidsgiverTyper.privat) {
			formMethods.resetField(`${path}.arbeidsgiver`, {
				defaultValue: initialArbeidsgiverPers,
			})
		} else {
			formMethods.resetField(`${path}.arbeidsgiver`, {
				defaultValue: initialArbeidsgiverOrg,
			})
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

	const currentValue = arbeidsgiverType ?? ArbeidsgiverTyper.felles

	return (
		<div className="toggle--wrapper" key={idx ?? `${path}-toggle`}>
			{erLaastArbeidsforhold ? (
				<DisabledToggleArbeidsgiver
					onChange={() => null}
					value={currentValue}
					size={'small'}
					fill
					key={idx ?? `${path}-disabled`}
					title={'Kan ikke endre arbeidsgivertype på eksisterende arbeidsforhold'}
				>
					{renderArbeidsgiverToggleItems(disablePrivat)}
				</DisabledToggleArbeidsgiver>
			) : (
				<ToggleArbeidsgiver
					onChange={(value: ArbeidsgiverTyper) => handleToggleChange(value)}
					value={currentValue}
					size={'small'}
					fill
					key={idx ?? `${path}-enabled`}
				>
					{renderArbeidsgiverToggleItems(disablePrivat)}
				</ToggleArbeidsgiver>
			)}
			<div style={{ marginTop: '10px' }} className="flexbox--full-width">
				{currentValue === ArbeidsgiverTyper.felles && (
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
				{currentValue === ArbeidsgiverTyper.egen && (
					<div className="flex-box" title={title}>
						<EgneOrganisasjoner
							path={`${path}.arbeidsgiver.orgnummer`}
							afterChange={afterChange}
							showMiljoeInfo={showMiljoeInfo}
							handleChange={(selected: any) => {
								formMethods.setValue(`${path}.arbeidsgiver.orgnummer`, selected?.value, {
									shouldDirty: true,
									shouldTouch: true,
								})
								formMethods.clearErrors(`manual.${path}.arbeidsgiver`)
								formMethods.clearErrors(`${path}.arbeidsgiver`)
							}}
							filterValidEnhetstyper={true}
							isDisabled={erLaastArbeidsforhold}
						/>
					</div>
				)}
				{currentValue === ArbeidsgiverTyper.fritekst && (
					<FormTextInput
						name={`${path}.arbeidsgiver.orgnummer`}
						label={'Organisasjonsnummer'}
						size="xlarge"
						onBlur={afterChange}
						isDisabled={erLaastArbeidsforhold}
						title={title}
					/>
				)}
				{currentValue === ArbeidsgiverTyper.privat && (
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
