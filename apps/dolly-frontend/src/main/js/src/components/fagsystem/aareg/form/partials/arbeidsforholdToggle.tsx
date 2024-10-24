import React, { useEffect, useState } from 'react'
import styled from 'styled-components'
import { Alert, ToggleGroup } from '@navikt/ds-react'
import { ArbeidsforholdForm } from './arbeidsforholdForm'
import { initialArbeidsgiverOrg, initialArbeidsgiverPers } from '../initialValues'
import { ArbeidsgiverTyper } from '@/components/fagsystem/aareg/AaregTypes'
import { useDollyFasteDataOrganisasjoner, useOrganisasjoner } from '@/utils/hooks/useOrganisasjoner'
import { useFormContext } from 'react-hook-form'
import { hentStoersteAaregdata } from '@/components/fagsystem/aareg/form/partials/arbeidsforholdForm'
import { useCurrentBruker } from '@/utils/hooks/useBruker'
import { getEgneOrganisasjoner } from '@/components/fagsystem/brregstub/form/partials/EgneOrganisasjoner'
import Loading from '@/components/ui/loading/Loading'

const ToggleArbeidsgiver = styled(ToggleGroup)`
	display: grid;
	grid-template-columns: repeat(auto-fit, minmax(180px, 1fr));
`

const StyledAlert = styled(Alert)`
	margin-top: 10px;
`

export const ArbeidsforholdToggle = ({ path, idx }) => {
	const formMethods = useFormContext()
	const { currentBruker } = useCurrentBruker()

	const aaregData = formMethods.getValues(path)
	console.log('idx: ', idx) //TODO - SLETT MEG
	console.log('aaregData: ', aaregData) //TODO - SLETT MEG

	//TODO: Hent organisasjoner ett nivaa opp kanskje?
	const { organisasjoner: fasteOrganisasjoner, loading: fasteOrganisasjonerLoading } =
		useDollyFasteDataOrganisasjoner(true)

	const { organisasjoner: brukerOrganisasjoner, loading: brukerOrganisasjonerLoading } =
		useOrganisasjoner(currentBruker?.brukerId)
	const egneOrganisasjoner = getEgneOrganisasjoner(brukerOrganisasjoner)

	//TODO: Maa skrives kraftig om
	const getArbeidsgiverType = () => {
		const orgnr = aaregData?.arbeidsgiver?.orgnummer
		// console.log('orgnr: ', orgnr) //TODO - SLETT MEG
		if (aaregData?.arbeidsgiver?.aktoertype === 'PERS') {
			return ArbeidsgiverTyper.privat
		} else if (
			!orgnr ||
			fasteOrganisasjoner
				?.map((organisasjon) => organisasjon?.orgnummer)
				?.some((org) => org === orgnr)
		) {
			return ArbeidsgiverTyper.felles
		} else if (
			egneOrganisasjoner?.map((organisasjon) => organisasjon?.orgnr)?.some((org) => org === orgnr)
		) {
			return ArbeidsgiverTyper.egen
			//TODO Fortsett med egen. Test med 05528718630 (Denne viser 3 ameldinger, men har bare 2?)
		} else {
			return ArbeidsgiverTyper.fritekst
		}
	}

	const [typeArbeidsgiver, setTypeArbeidsgiver] = useState(getArbeidsgiverType())

	useEffect(() => {
		setTypeArbeidsgiver(getArbeidsgiverType())
	}, [fasteOrganisasjoner, brukerOrganisasjoner])

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

	//TODO: Ikke toggle naar legg til paa person??? Mulig aa disable den?
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

	if (fasteOrganisasjonerLoading || brukerOrganisasjonerLoading) {
		return <Loading label="Laster organisasjoner ..." />
	}

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
			{/*TODO: Splitte opp arbeidsgiver og form. Dvs sette inn arbeidsgiverfelter her, og sette resten av form utenfor, kanskje i Form.tsx?*/}
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
