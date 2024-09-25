import React, { ReactElement, useState } from 'react'
import styled from 'styled-components'
import { Alert, ToggleGroup } from '@navikt/ds-react'
import { AmeldingForm } from './ameldingForm'
import { ArbeidsforholdForm } from './arbeidsforholdForm'
import { FormDollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import {
	initialAaregOrg,
	initialAaregPers,
	initialArbeidsforholdOrg,
	initialArbeidsforholdPers,
	initialValues,
} from '../initialValues'
import { ArbeidsgiverTyper } from '@/components/fagsystem/aareg/AaregTypes'
import { useDollyFasteDataOrganisasjoner } from '@/utils/hooks/useOrganisasjoner'
import { useFormContext } from 'react-hook-form'

const ToggleArbeidsgiver = styled(ToggleGroup)`
	display: grid;
	grid-template-columns: repeat(auto-fit, minmax(180px, 1fr));
`

const StyledAlert = styled(Alert)`
	margin-top: 10px;
`
export const ArbeidsforholdToggle = (): ReactElement => {
	const formMethods = useFormContext()
	const { organisasjoner } = useDollyFasteDataOrganisasjoner(true)

	const getArbeidsgiverType = () => {
		const orgnummer = formMethods.watch('aareg[0].arbeidsgiver.orgnummer')
		if (formMethods.watch('aareg[0].amelding[0]') || formMethods.watch('aareg[0].arbeidsforhold')) {
			return ArbeidsgiverTyper.egen
		} else if (formMethods.watch('aareg[0].arbeidsgiver.aktoertype') === 'PERS') {
			return ArbeidsgiverTyper.privat
		} else if (
			!orgnummer ||
			organisasjoner.map((organisasjon) => organisasjon.orgnummer).some((org) => org === orgnummer)
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

	const handleToggleChange = (value: ArbeidsgiverTyper) => {
		setTypeArbeidsgiver(value)
		if (value === ArbeidsgiverTyper.privat) {
			formMethods.resetField('aareg', { defaultValue: [initialAaregPers] })
		} else if (value === ArbeidsgiverTyper.felles || value === ArbeidsgiverTyper.fritekst) {
			formMethods.resetField('aareg', { defaultValue: [initialAaregOrg] })
		} else if (value === ArbeidsgiverTyper.egen) {
			formMethods.resetField('aareg', { defaultValue: [initialValues] })
		}
		formMethods.clearErrors('aareg')
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
			{typeArbeidsgiver === ArbeidsgiverTyper.egen ? (
				<>
					{
						// @ts-ignore
						<AmeldingForm warningMessage={warningMessage} />
					}
				</>
			) : (
				<>
					<FormDollyFieldArray
						name="aareg"
						header="Arbeidsforhold"
						newEntry={
							typeArbeidsgiver === ArbeidsgiverTyper.privat
								? { ...initialArbeidsforholdPers, arbeidsforholdstype: 'ordinaertArbeidsforhold' }
								: { ...initialArbeidsforholdOrg, arbeidsforholdstype: 'ordinaertArbeidsforhold' }
						}
						canBeEmpty={false}
					>
						{(path: string, idx: number) => (
							<ArbeidsforholdForm
								path={path}
								key={idx}
								arbeidsforholdIndex={idx}
								erLenket={null}
								arbeidsgiverType={typeArbeidsgiver}
								ameldingIndex={undefined}
								warningMessage={undefined}
							/>
						)}
					</FormDollyFieldArray>
				</>
			)}
		</div>
	)
}
