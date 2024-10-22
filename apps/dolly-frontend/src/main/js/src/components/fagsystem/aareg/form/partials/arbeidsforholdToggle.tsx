import React, { useState } from 'react'
import styled from 'styled-components'
import { Alert, ToggleGroup } from '@navikt/ds-react'
import { ArbeidsforholdForm } from './arbeidsforholdForm'
import { initialArbeidsgiverOrg, initialArbeidsgiverPers } from '../initialValues'
import { ArbeidsgiverTyper } from '@/components/fagsystem/aareg/AaregTypes'
import { useDollyFasteDataOrganisasjoner } from '@/utils/hooks/useOrganisasjoner'
import { useFormContext } from 'react-hook-form'
import { hentStoersteAaregdata } from '@/components/fagsystem/aareg/form/partials/arbeidsforholdForm'

const ToggleArbeidsgiver = styled(ToggleGroup)`
	display: grid;
	grid-template-columns: repeat(auto-fit, minmax(180px, 1fr));
`

const StyledAlert = styled(Alert)`
	margin-top: 10px;
`
export const ArbeidsforholdToggle = ({ path, idx }) => {
	let aaregdata = hentStoersteAaregdata()

	const formMethods = useFormContext()

	const { organisasjoner } = useDollyFasteDataOrganisasjoner(true)

	//TODO: Maa skrives kraftig om
	const getArbeidsgiverType = () => {
		const orgnr = aaregdata?.[0]?.arbeidsgiver?.orgnummer
		if (aaregdata?.[0]?.amelding?.[0]) {
			return ArbeidsgiverTyper.egen
		} else if (aaregdata?.[0]?.arbeidsgiver?.aktoertype === 'PERS') {
			return ArbeidsgiverTyper.privat
		} else if (
			!orgnr ||
			organisasjoner?.map((organisasjon) => organisasjon?.orgnummer)?.some((org) => org === orgnr)
		) {
			return ArbeidsgiverTyper.felles
		} else {
			return ArbeidsgiverTyper.fritekst
		}
	}

	const [typeArbeidsgiver, setTypeArbeidsgiver] = useState(getArbeidsgiverType())

	const toggleValues = [
		{
			value: ArbeidsgiverTyper.egen,
			label: 'Egen organisasjon',
		},
		{
			value: ArbeidsgiverTyper.felles,
			label: 'Felles organisasjoner',
		},
		{
			value: ArbeidsgiverTyper.fritekst,
			label: 'Skriv inn org.nr.',
		},
		{
			value: ArbeidsgiverTyper.privat,
			label: 'Privat arbeidsgiver',
		},
	]

	//TODO: Ikke toggle naar legg til paa person???
	const handleToggleChange = (value: ArbeidsgiverTyper) => {
		setTypeArbeidsgiver(value)

		if (value === ArbeidsgiverTyper.privat) {
			// formMethods.resetField('aareg', { defaultValue: [initialAaregPers] })
			formMethods.resetField(`${path}.arbeidsgiver`, { defaultValue: initialArbeidsgiverPers })
		} else {
			formMethods.resetField(`${path}.arbeidsgiver`, { defaultValue: initialArbeidsgiverOrg })
		}

		// else if (value === ArbeidsgiverTyper.felles || value === ArbeidsgiverTyper.fritekst) {
		// 	formMethods.resetField('aareg', { defaultValue: [initialAaregOrg] })
		// } else if (value === ArbeidsgiverTyper.egen) {
		// 	formMethods.resetField('aareg', { defaultValue: [initialValues] })
		// }
		// formMethods.clearErrors('aareg')
	}

	const warningMessage = (
		<StyledAlert variant={'warning'}>
			Du har ingen egne organisasjoner, og kan derfor ikke sende inn A-meldinger for person. For å
			lage dine egne organisasjoner trykk {<a href="/organisasjoner">her</a>}. For å opprette person
			med arbeidsforhold i felles organisasjoner eller andre arbeidsgivere, velg en annen kategori
			ovenfor.
		</StyledAlert>
	)

	return (
		<div className="toggle--wrapper">
			<ToggleArbeidsgiver
				onChange={(value: ArbeidsgiverTyper) => handleToggleChange(value)}
				value={typeArbeidsgiver}
				size={'small'}
			>
				{toggleValues.map((type) => (
					<ToggleGroup.Item key={type.value} value={type.value}>
						{type.label}
					</ToggleGroup.Item>
				))}
			</ToggleArbeidsgiver>
			<ArbeidsforholdForm
				path={path}
				key={idx}
				arbeidsforholdIndex={idx}
				erLenket={null}
				arbeidsgiverType={typeArbeidsgiver}
				ameldingIndex={undefined}
				warningMessage={warningMessage}
			/>
		</div>
	)
}
